package com.ugovori.studentskiugovori

import android.content.SharedPreferences
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ugovori.studentskiugovori.model.Repository
import com.ugovori.studentskiugovori.model.data.calculateEarningsAndGetNumbers
import com.ugovori.studentskiugovori.model.data.parseUgovore
import com.ugovori.studentskiugovori.model.dataclasses.CardData
import com.ugovori.studentskiugovori.model.dataclasses.Ugovor
import com.ugovori.studentskiugovori.model.dataclasses.WorkedHours
import com.ugovori.studentskiugovori.model.dataclasses.WorkedHoursRealm
import com.ugovori.studentskiugovori.model.dataclasses.toStringExport
import com.ugovori.studentskiugovori.model.getWorkedHours
import com.ugovori.studentskiugovori.utils.Result
import com.ugovori.studentskiugovori.utils.Result.ParseResult
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import java.math.BigDecimal
import java.math.MathContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

class MainViewModel(private val repository: Repository) : ViewModel() {


    private val sharedPref: SharedPreferences by KoinJavaComponent.inject(SharedPreferences::class.java)

    val snackbarHostState: SnackbarHostState = SnackbarHostState()
    private val realm: Realm by lazy { Realm.getDefaultInstance() }


    private val _loadedTxt = MutableLiveData<Status>().apply { value = Status.UNSET }
    private val _isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _ugovori = MutableLiveData<List<Ugovor>>().apply { value = emptyList() }
    private val _cardData = MutableLiveData<CardData>().apply { value = CardData() }
    private val _daysWorked: MutableStateFlow<Map<LocalDate, List<WorkedHours>>> =
        MutableStateFlow(mapOf())
    private val _generated = MutableLiveData<String>().apply { value = "" }
    private val _totalPerDay: MutableStateFlow<Map<LocalDate, BigDecimal>> =
        MutableStateFlow(mapOf())
    private val _hourlyPay: MutableLiveData<List<BigDecimal>> = MutableLiveData(emptyList())
    private val _errorText = MutableLiveData<String>().apply { value = "" }

    val loadedTxt: LiveData<Status> = _loadedTxt
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    val generated: LiveData<String> = _generated
    val ugovori: LiveData<List<Ugovor>> = _ugovori
    val cardData: LiveData<CardData> = _cardData
    val daysWorked: StateFlow<Map<LocalDate, List<WorkedHours>>> = _daysWorked
    val totalPerDay: StateFlow<Map<LocalDate, BigDecimal>> = _totalPerDay
    val hourlyPay: LiveData<List<BigDecimal>> = _hourlyPay
    val errorText: LiveData<String> = _errorText

    init {
        realm.getWorkedHours().getWorkedHours().forEach() {
            addDayWorked(
                CalendarDay(LocalDate.ofEpochDay(it.date), DayPosition.MonthDate),
                realmWorkedToRegularWorked(it),
                false
            )
        }
        getData()
    }

    fun getData(refresh: Boolean = false) {
        if (refresh) {
            _isRefreshing.postValue(true)
        }
        _loadedTxt.postValue(Status.FETCHING)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.getData(
                sharedPref.getString("username", "") ?: "",
                sharedPref.getString("password", "") ?: "",
                forceLogin = false,
                timeout = !refresh
            )) {
                is Result.LoginResult.Success -> {
                    when (val parsedData = parseUgovore(result.data as String)) {
                        is ParseResult.Success -> {
                            val rn = SimpleDateFormat(
                                "dd.MM.yyyy HH:mm:ss",
                                Locale.getDefault()
                            ).format(System.currentTimeMillis())
                            _generated.postValue(rn)
                            _ugovori.postValue(parsedData.data as List<Ugovor>?)

                            _hourlyPay.postValue(parsedData.data.filter {
                                it.STATUSNAZIV?.contains("Izdan", ignoreCase = true) == true
                            }.map {
                                it.CIJENAWEB?.toBigDecimal() ?: BigDecimal(0)
                            })

                            _isRefreshing.postValue(false)
                            _loadedTxt.postValue(Status.FETCHED)
                            delay(30)
                            _loadedTxt.postValue(Status.FETCHED_NEW)
                            _cardData.postValue(calculateEarningsAndGetNumbers(parsedData.data))
                        }

                        is ParseResult.Error -> {
                            snackbarHostState.showSnackbar("Greška prilikom parsiranja podataka")
                            println(parsedData.error)
                            _isRefreshing.postValue(false)
                            _loadedTxt.postValue(Status.FETCHING_ERROR)
                            _errorText.postValue(parsedData.error)
                            return@launch
                        }
                    }

                }

                is Result.LoginResult.Refresh -> {
                    _loadedTxt.postValue(Status.FETCHED_NEW)
                    delay(1000)
                    _isRefreshing.postValue(false)
                    return@launch
                }

                is Result.LoginResult.Error -> {
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    println(result.error)
                    _isRefreshing.postValue(false)
                    _loadedTxt.postValue(Status.FETCHING_ERROR)
                    _errorText.postValue(result.error)
                    return@launch
                }
            }
        }
    }

    fun addDayWorked(day: CalendarDay, workedHours: WorkedHours, addToDb: Boolean = true) {
        val map: MutableMap<LocalDate, List<WorkedHours>> = _daysWorked.value.toMutableMap()
        val list = map[workedHours.date]?.toMutableList()
        list?.add(workedHours)
        map[day.date] = list ?: listOf(workedHours)
        _daysWorked.value = map

        if (addToDb) {
            realm.getWorkedHours().addToDb(workedHours)
        }

        val totalMap: MutableMap<LocalDate, BigDecimal> = totalPerDay.value.toMutableMap()
        var total = workedHours.moneyEarned
        if (total > BigDecimal(99)) {
            total = total.setScale(1)
        }
        totalMap[day.date] = totalMap[day.date]?.plus(total) ?: total
        _totalPerDay.value = totalMap
    }

    fun deleteWorkedItem(workedHours: WorkedHours) {
        val map: MutableMap<LocalDate, List<WorkedHours>> = _daysWorked.value.toMutableMap()
        val list = map[workedHours.date]?.toMutableList()
        list?.remove(workedHours)
        if (list.isNullOrEmpty()) {
            map.remove(workedHours.date)
        } else {
            map[workedHours.date] = list
        }
        _daysWorked.value = map

        realm.getWorkedHours().deleteWorkedHoursFromDb(workedHours.id)

        val totalMap: MutableMap<LocalDate, BigDecimal> = totalPerDay.value.toMutableMap()
        if (totalMap[workedHours.date] != null && totalMap[workedHours.date]!! <= workedHours.moneyEarned) {
            totalMap.remove(workedHours.date)
        } else {
            totalMap[workedHours.date] =
                (totalMap[workedHours.date]?.minus(workedHours.moneyEarned)) ?: 0.toBigDecimal()
        }
        _totalPerDay.value = totalMap
    }

    private fun realmWorkedToRegularWorked(workedHoursRealm: WorkedHoursRealm): WorkedHours {

        return WorkedHours(
            workedHoursRealm.id,
            LocalDate.ofEpochDay(workedHoursRealm.date),
            LocalTime.of(
                workedHoursRealm.timeStart.split(":")[0].toInt(),
                workedHoursRealm.timeStart.split(":")[1].toInt()
            ),
            LocalTime.of(
                workedHoursRealm.timeEnd.split(":")[0].toInt(),
                workedHoursRealm.timeEnd.split(":")[1].toInt()
            ),
            BigDecimal(workedHoursRealm.moneyEarned).round(MathContext(3)),
            BigDecimal(workedHoursRealm.hours).round(MathContext(3)),
            BigDecimal(workedHoursRealm.hourlyPay).round(MathContext(3)),
            workedHoursRealm.completed
        )
    }

    fun getTextHours(): String {
        var text = "Zarada po danima:\n\n"
        daysWorked.value.toSortedMap().forEach { (date, workedHours) ->
            workedHours.forEach {
                text += it.toStringExport() + "------------------------\n"
            }
        }
        text += "\n\nUkupna zarada:     "
        var sum = BigDecimal(0)
        totalPerDay.value.forEach {
            sum += it.value
        }
        text += "$sum €"
        return text
    }

    override fun onCleared() {
        realm.close()
        super.onCleared()
    }


}

enum class Status {
    FETCHING,
    FETCHED,
    FETCHED_NEW,
    FETCHING_ERROR,
    UNSET
}
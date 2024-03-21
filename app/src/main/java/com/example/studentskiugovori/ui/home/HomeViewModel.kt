package com.example.studentskiugovori.ui.home

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentskiugovori.model.Repository
import com.example.studentskiugovori.model.data.calculateEarningsAndGetNumbers
import com.example.studentskiugovori.model.dataclasses.CardData
import com.example.studentskiugovori.model.dataclasses.Ugovor
import com.example.studentskiugovori.model.dataclasses.WorkedHours
import com.example.studentskiugovori.model.dataclasses.WorkedHoursRealm
import com.example.studentskiugovori.model.getWorkedHours
import com.example.studentskiugovori.utils.Result
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import io.realm.Realm
import io.realm.RealmResults
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

class HomeViewModel(private val repository: Repository, context: Context) : ViewModel() {


    val sharedPref: SharedPreferences by KoinJavaComponent.inject(SharedPreferences::class.java)


    val snackbarHostState: SnackbarHostState = SnackbarHostState()
    val realm: Realm by lazy { Realm.getDefaultInstance() }




    private val _loadedTxt = MutableLiveData<String>().apply { value = "unset" }
    val loadedTxt: LiveData<String> = _loadedTxt

    private val _isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _generated = MutableLiveData<String>().apply { value = "" }
    val generated: LiveData<String> = _generated

    private val _txt = MutableLiveData<String>().apply { value = "" }
    val txt: LiveData<String> = _txt

    private val _ugovori = MutableLiveData<List<Ugovor>>().apply { value = emptyList() }
    val ugovori: LiveData<List<Ugovor>> = _ugovori

    private val _cardData = MutableLiveData<CardData>().apply { value = CardData() }
    val cardData: LiveData<CardData> = _cardData

    private val _daysWorked: MutableStateFlow<Map<LocalDate, List<WorkedHours>>> = MutableStateFlow(mapOf())
    val daysWorked: StateFlow<Map<LocalDate, List<WorkedHours>>> = _daysWorked

    private val _totalPerDay: MutableStateFlow<Map<LocalDate, BigDecimal>> = MutableStateFlow(mapOf())
    val totalPerDay: StateFlow<Map<LocalDate, BigDecimal>> = _totalPerDay

    val _test = MutableLiveData<String>().apply { value = "test" }
    val test: LiveData<String> = _test
    init{
        realm.getWorkedHours().getWorkedHours().forEach(){
            addDayWorked(CalendarDay(LocalDate.ofEpochDay(it.date), DayPosition.MonthDate), realmWorkedToRegWorked(it), false)
        }
    }
    fun getData(refresh: Boolean = false) {
        if (refresh) {
            _isRefreshing.postValue(true)
        }
        _loadedTxt.postValue("fetching")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.getData(
                sharedPref.getString("username", "") ?: "",
                sharedPref.getString("password", "") ?: "",
                false
            )) {
                is Result.LoginResult.Success -> {
                    val rn = SimpleDateFormat(
                        "dd.MM.yyyy HH:mm:ss",
                        Locale.getDefault()
                    ).format(System.currentTimeMillis())
                    _generated.postValue(rn)
                    _ugovori.postValue(result.data as List<Ugovor>?)
                    _isRefreshing.postValue(false)
                    _loadedTxt.postValue("fetched")
                    delay(30)
                    _loadedTxt.postValue("fetchedNew")
                    _cardData.postValue(calculateEarningsAndGetNumbers(result.data))
                }

                is Result.LoginResult.Error -> {
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    _isRefreshing.postValue(false)
                    _loadedTxt.postValue("fetchingError")
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
        if (total > BigDecimal(99)) { total = total.setScale(1) }
        totalMap[day.date] = totalMap[day.date]?.plus(total) ?: total
        _totalPerDay.value = totalMap
    }

    fun deleteWorkedItem(workedHours: WorkedHours) {
        val map: MutableMap<LocalDate, List<WorkedHours>> = _daysWorked.value.toMutableMap()
        val list = map[workedHours.date]?.toMutableList()
        list?.remove(workedHours)
        map[workedHours.date] = list ?: emptyList()
        _daysWorked.value = map

        realm.getWorkedHours().deleteWorkedHoursFromDb(workedHours.id)

        val totalMap: MutableMap<LocalDate, BigDecimal> = totalPerDay.value.toMutableMap()
        if (totalMap[workedHours.date] != null && totalMap[workedHours.date]!! <= workedHours.moneyEarned) {
            totalMap.remove(workedHours.date)
        } else {
            totalMap[workedHours.date] = (totalMap[workedHours.date]?.minus(workedHours.moneyEarned)) ?: 0.toBigDecimal()
        }
        _totalPerDay.value = totalMap
    }

    fun getWorkedHours(): RealmResults<WorkedHoursRealm> {
        return realm.getWorkedHours().getWorkedHours()
    }

    private fun realmWorkedToRegWorked(workedHoursRealm: WorkedHoursRealm): WorkedHours {

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
            workedHoursRealm.completed
        )
    }

    override fun onCleared() {
        realm.close()
        super.onCleared()
    }

}
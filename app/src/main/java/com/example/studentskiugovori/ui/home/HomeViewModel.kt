package com.example.studentskiugovori.ui.home

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.MasterKeys
import com.example.studentskiugovori.model.Repository
import com.example.studentskiugovori.model.data.calculateEarningsAndGetNumbers
import com.example.studentskiugovori.model.dataclasses.CardData
import com.example.studentskiugovori.model.dataclasses.DayWorked
import com.example.studentskiugovori.model.dataclasses.Ugovor
import com.example.studentskiugovori.utils.Result
import com.kizitonwose.calendar.core.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel(private val repository: Repository, context: Context) : ViewModel() {


    val sharedPref : SharedPreferences by KoinJavaComponent.inject(SharedPreferences::class.java)


    val snackbarHostState: SnackbarHostState = SnackbarHostState()


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

    val _cardData = MutableLiveData<CardData>().apply { value = CardData() }
    val cardData: LiveData<CardData> = _cardData

    val _daysWorked = MutableLiveData<MutableList<Pair<CalendarDay, DayWorked>>>().apply { value = mutableListOf() }
    val daysWorked: MutableLiveData<MutableList<Pair<CalendarDay, DayWorked>>> = _daysWorked

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
    fun addDayWorked(day: CalendarDay, dayWorked: DayWorked) {
        val list = _daysWorked.value
        list?.add(Pair(day, dayWorked))
        _daysWorked.postValue(list)
    }
}
package com.example.studentskiugovori.ui.home

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.studentskiugovori.model.Repository
import com.example.studentskiugovori.model.data.calculateEarningsAndGetNumbers
import com.example.studentskiugovori.model.dataclasses.CardData
import com.example.studentskiugovori.model.dataclasses.Ugovor
import com.example.studentskiugovori.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel(private val repository: Repository, context: Context) : ViewModel() {


    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPref = context.let {
        EncryptedSharedPreferences.create(
            "PreferencesFilename",
            masterKeyAlias,
            it,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

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
                    val rn = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
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
}
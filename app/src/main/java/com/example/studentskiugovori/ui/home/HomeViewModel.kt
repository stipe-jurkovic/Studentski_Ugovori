package com.example.studentskiugovori.ui.home

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.studentskiugovori.model.Repository
import com.example.studentskiugovori.model.dataclasses.Exam
import com.example.studentskiugovori.model.dataclasses.Predmet
import com.example.studentskiugovori.model.dataclasses.Student
import com.example.studentskiugovori.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    private val _student = MutableLiveData<Student>().apply { value = Student() }
    val student: LiveData<Student> = _student

    private val _isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _generated = MutableLiveData<String>().apply { value = "" }
    val generated: LiveData<String> = _generated

    fun getStudomatData(refresh: Boolean = false) {
        _loadedTxt.postValue("fetching")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.loginUser(
                sharedPref.getString("username", "") ?: "",
                sharedPref.getString("password", "") ?: "",
                false
            )) {
                is Result.LoginResult.Success -> {
                    _student.postValue(result.data as Student)
                }

                is Result.LoginResult.Error -> {
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    _loadedTxt.postValue("fetchingError")
                    return@launch
                }
            }
        }
    }
}
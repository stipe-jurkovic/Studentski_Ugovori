package com.example.studentskiugovori.ui.login

import android.content.SharedPreferences
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentskiugovori.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.studentskiugovori.utils.Result
import kotlinx.coroutines.delay

class LoginViewModel(private val repository: Repository) : ViewModel() {

    val snackbarHostState = SnackbarHostState()
    private val _loginSuccess = MutableLiveData<Boolean>().apply { value = false }
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _error = MutableLiveData<Boolean>().apply { value = true }
    val showError: LiveData<Boolean> = _error

    private val _showLoading = MutableLiveData<Boolean>().apply { value = false }
    val showLoading: LiveData<Boolean> = _showLoading

    fun attemptLogin(username: String, password: String, sharedPreferences: SharedPreferences) {
        _showLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            when (repository.getData(username, password, true)) {
                is Result.LoginResult.Success -> {
                    sharedPreferences.edit().putString("username", username).apply()
                    sharedPreferences.edit().putString("password", password).apply()
                    _loginSuccess.postValue(true)
                    delay(100)
                    _loginSuccess.postValue(false)
                    _showLoading.postValue(false)
                }
                is Result.LoginResult.Refresh -> {
                    _loginSuccess.postValue(true)
                    delay(100)
                    _loginSuccess.postValue(false)
                    _showLoading.postValue(false)
                }
                is Result.LoginResult.Error -> {
                    _error.postValue(true)
                    _loginSuccess.postValue(false)
                    _showLoading.postValue(false)
                    snackbarHostState.showSnackbar("Pogrešno korisničko ime ili lozinka")
                }
            }
        }
    }
}
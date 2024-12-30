package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResponse
import kotlinx.coroutines.launch
import com.dicoding.picodiploma.loginwithanimation.utils.Result

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.postValue(Result.Loading)
            try {
                val response = repository.login(email, password)
                repository.saveSession(UserModel(response.loginResult.name, response.loginResult.token))
                _loginResult.postValue(Result.Success(response))
            } catch (e: Exception) {
                _loginResult.postValue(Result.Error(e.message.toString()))
            }
        }
    }
}
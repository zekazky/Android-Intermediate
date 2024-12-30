package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.RegisterResponse
import kotlinx.coroutines.launch
import com.dicoding.picodiploma.loginwithanimation.utils.Result

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResult.postValue(Result.Loading)
            try {
                val response = userRepository.register(name, email, password)
                _registerResult.postValue(Result.Success(response))
            } catch (e: Exception) {
                _registerResult.postValue(Result.Error(e.message.toString()))
            }
        }
    }
}

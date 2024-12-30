package com.dicoding.picodiploma.loginwithanimation.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.utils.Result
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    private val _storiesWithLocation = MutableLiveData<Result<List<ListStoryItem>>>()
    val storiesWithLocation: LiveData<Result<List<ListStoryItem>>> = _storiesWithLocation
    fun getStoriesWithLocation(token: String) {
        viewModelScope.launch {
            try {
                _storiesWithLocation.value = Result.Loading
                val stories = repository.getStoriesWithLocation(token)
                _storiesWithLocation.value = Result.Success(stories)
            } catch (e: Exception) {
                _storiesWithLocation.value = Result.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}

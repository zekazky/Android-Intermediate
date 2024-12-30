package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.dicoding.picodiploma.loginwithanimation.utils.Result

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<String>>()
    val uploadResult: LiveData<Result<String>> = _uploadResult

    fun uploadStory(
        token: String,
        description: String,
        photoFile: File,
        lat: Float? = null,
        lon: Float? = null
    ) {
        viewModelScope.launch {
            _uploadResult.postValue(Result.Loading)
            try {
                val service = ApiConfig.getApiService(token)

                val descriptionBody = description.toRequestBody("text/plain".toMediaType())
                val photoRequestBody = photoFile.asRequestBody("image/jpeg".toMediaType())
                val photoPart = MultipartBody.Part.createFormData("photo", photoFile.name, photoRequestBody)
                val latBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
                val lonBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())

                val response = service.uploadStory(descriptionBody, photoPart, latBody, lonBody)
                _uploadResult.postValue(Result.Success("Story berhasil diunggah!"))
            } catch (e: Exception) {
                _uploadResult.postValue(Result.Error(e.message ?: "Upload gagal"))
            }
        }
    }
}

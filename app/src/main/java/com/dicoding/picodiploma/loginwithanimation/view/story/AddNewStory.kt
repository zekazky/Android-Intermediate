package com.dicoding.picodiploma.loginwithanimation.view.story

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference.Companion.getUserBlocking
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.utils.getImageUri
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import java.io.File
import java.io.FileOutputStream
import com.dicoding.picodiploma.loginwithanimation.utils.Result
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity

class AddNewStory : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null
    private var previousImageUri: Uri? = null  // Added to store previous image URI
    private lateinit var userToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreference = UserPreference.getInstance(dataStore)
        val userSession = userPreference.getUserBlocking()
        userToken = userSession.token

        binding.btnGalery.setOnClickListener { openGallery() }
        binding.btnKamera.setOnClickListener { openCamera() }
        binding.btnUpload.setOnClickListener{ uploadStory() }

        observeViewModel()
    }

    private fun openCamera() {
        previousImageUri = currentImageUri  // Store current image URI before opening camera
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            // Restore previous image URI if camera is cancelled
            currentImageUri = previousImageUri
            showImage()
        }
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            previousImageUri = uri  // Store gallery image URI as previous
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }



    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadStory() {
        val description = binding.etDeskripsi.text.toString()
        if (description.isEmpty()) {
            Toast.makeText(this, "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val photoFile = currentImageUri?.let { uriToFile(it, this) }
        if (photoFile == null) {
            Toast.makeText(this, "Foto tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val finalPhotoFile = if (!isFileSizeValid(photoFile)) {
            compressImage(photoFile)
        } else {
            photoFile
        }

        if (!isFileSizeValid(finalPhotoFile)) {
            Toast.makeText(this, "File masih terlalu besar meskipun sudah dikompresi!", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.uploadStory(userToken, description, finalPhotoFile)
    }

    private fun observeViewModel() {
        viewModel.uploadResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.data, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)

                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()

                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Log.e("AddStoryActivity", "Error: ${result.error}")
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun uriToFile(selectedUri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)

        val inputStream = contentResolver.openInputStream(selectedUri) ?: return tempFile
        val outputStream = FileOutputStream(tempFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }

    private fun isFileSizeValid(file: File, maxSizeInMb: Int = 1): Boolean {
        val maxSizeInBytes = maxSizeInMb * 1000 * 1000
        return file.length() <= maxSizeInBytes
    }

    private fun compressImage(file: File, quality: Int = 80): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val compressedFile = File(cacheDir, "compressed_${file.name}")
        val outputStream = FileOutputStream(compressedFile)

        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.flush()
        outputStream.close()

        return compressedFile
    }

}
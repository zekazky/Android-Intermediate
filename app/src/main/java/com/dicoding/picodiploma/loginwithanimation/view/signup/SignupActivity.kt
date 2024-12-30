package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.utils.Result
import com.dicoding.picodiploma.loginwithanimation.view.customView.MyEditText
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel

    private lateinit var nameEditText: MyEditText
    private lateinit var emailEditText: MyEditText
    private lateinit var passwordEditText: MyEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)




        val factory = ViewModelFactory.getInstance(this)
        signupViewModel = ViewModelProvider(this, factory)[SignupViewModel::class.java]

        setupView()
        setupAction()
        playAnimation()
        observeViewModel()
    }



    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {

                val name = binding.nameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                signupViewModel.register(name, email, password)

        }
    }



    private fun observeViewModel() {
        signupViewModel.registerResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showAlert("Sukses", result.data.message ?: "Registrasi berhasil.")
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()

                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showAlert("Gagal", result.error)
                }
            }
        }
    }

    private fun showAlert(title: String, message: String, onPositive: (() -> Unit)? = null) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { _, _ -> onPositive?.invoke() }
            create()
            show()
        }
    }

    private fun playAnimation() {
        // Membuat animasi skala dan rotasi untuk imageView dengan durasi yang lebih cepat
        ObjectAnimator.ofFloat(binding.imageView, View.SCALE_X, 1f, 1.2f, 1f).apply {
            duration = 3000 // Durasi animasi 3 detik (dipercepat)
            repeatCount = ObjectAnimator.INFINITE // Mengulang animasi secara tak terbatas
            repeatMode = ObjectAnimator.REVERSE // Mengulang dengan mode balik
        }.start()

        ObjectAnimator.ofFloat(binding.imageView, View.SCALE_Y, 1f, 1.2f, 1f).apply {
            duration = 3000 // Durasi animasi 3 detik (dipercepat)
            repeatCount = ObjectAnimator.INFINITE // Mengulang animasi secara tak terbatas
            repeatMode = ObjectAnimator.REVERSE // Mengulang dengan mode balik
        }.start()

        // Membuat animasi alpha untuk berbagai elemen UI
        val titleAnimation = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 0f, 1f).setDuration(300)
        val nameTextViewAnimation = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 0f, 1f).setDuration(300)
        val nameEditTextLayoutAnimation = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 0f, 1f).setDuration(300)
        val emailTextViewAnimation = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 0f, 1f).setDuration(300)
        val emailEditTextLayoutAnimation = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 0f, 1f).setDuration(300)
        val passwordTextViewAnimation = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 0f, 1f).setDuration(300)
        val passwordEditTextLayoutAnimation = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 0f, 1f).setDuration(300)
        val signupButtonAnimation = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 0f, 1f).setDuration(300)

        // Menambahkan animasi zoom in dan zoom out pada tombol
        ObjectAnimator.ofFloat(binding.signupButton, View.SCALE_X, 1f, 1.1f, 1f).apply {
            duration = 600 // Durasi animasi zoom in dan zoom out
            repeatCount = ObjectAnimator.INFINITE // Mengulang animasi secara tak terbatas
            repeatMode = ObjectAnimator.REVERSE // Mengulang dengan mode balik
        }.start()

        ObjectAnimator.ofFloat(binding.signupButton, View.SCALE_Y, 1f, 1.1f, 1f).apply {
            duration = 600 // Durasi animasi zoom in dan zoom out
            repeatCount = ObjectAnimator.INFINITE // Mengulang animasi secara tak terbatas
            repeatMode = ObjectAnimator.REVERSE // Mengulang dengan mode balik
        }.start()

        // Mengatur AnimatorSet untuk menjalankan animasi secara berur utan
        AnimatorSet().apply {
            playSequentially(
                titleAnimation,
                nameTextViewAnimation,
                nameEditTextLayoutAnimation,
                emailTextViewAnimation,
                emailEditTextLayoutAnimation,
                passwordTextViewAnimation,
                passwordEditTextLayoutAnimation,
                signupButtonAnimation
            )
            startDelay = 100 // Menambahkan jeda sebelum animasi dimulai
        }.start()
    }
}
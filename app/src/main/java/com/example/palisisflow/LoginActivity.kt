package com.example.palisisflow

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Snackbar
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.palisisag.pitapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        viewModel = LoginViewModel()

        setContentView(binding.root)

        binding.submitButton.setOnClickListener {
            val userName = binding.userName.text.toString()
            val passWord = binding.password.text.toString()

            viewModel.login(userName, passWord)
        }

        loginObserver()
    }

    private fun loginObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    viewModel.uiState.collect {
                        when(it) {
                            is UiState.Success -> {
                                Snackbar.make(binding.root, "Logged in successfully", Snackbar.LENGTH_LONG).show()

                                binding.apply {
                                    progressBar.isVisible = false
                                    submitButton.isVisible = true
                                }
                            }
                            is UiState.Error -> {
                                Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG)
                                    .show()

                                binding.apply {
                                    progressBar.isVisible = false
                                    submitButton.isVisible = true
                                }
                            }
                            is UiState.Loading -> {
                                binding.apply {
                                    progressBar.isVisible = true
                                    submitButton.isVisible = false
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
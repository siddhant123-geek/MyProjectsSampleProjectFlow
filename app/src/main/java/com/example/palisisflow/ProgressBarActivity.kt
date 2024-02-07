package com.example.palisisflow

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.palisisag.pitapp.databinding.ActivityProgressBarBinding
import com.palisisag.pitapp.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ProgressBarActivity: AppCompatActivity() {

    private lateinit var binding: ActivityProgressBarBinding
    private lateinit var progressText: TextView
    lateinit var progressBarViewModel: ProgressBarActivityViewModel
    private var isApiCallStarted = false
    var isApiCallFinished = false
    var progressStatus = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProgressBarBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        progressBarViewModel = ProgressBarActivityViewModel()

        progressText = binding.progressText
        val job = lifecycleScope.launch {
            isApiCallStarted = true
            progressBarViewModel.uiState.collect {
                when(it) {
                    is UiState.Success -> {
                        Log.d("###", "onCreate: " + this.isActive)
                        binding.progressText.visibility = View.VISIBLE
                        Log.d("###", "onCreate: Coming to success case and the it " + it.data)
                        val percentage = (it.data / 10.0) * 100.0
                        Log.d("###", "onCreate: percentage $percentage")
                        binding.progressText.text = "$percentage %"
                        if(percentage.equals(100.0)) {
                            binding.progressText.text = "All Api calls are done"
                            binding.myProgressBar.visibility = View.GONE
                            this.cancel()
                            Log.d("###", "onCreate: " + this.isActive)
                        }
                    }
                    is UiState.Loading -> {
                        Log.d("###", "onCreate: Coming inside Ui State is loading")
                        binding.progressText.visibility = View.GONE
                    }
                    is UiState.Error -> {
                        TODO("To be handled")
                    }
                }
            }

        }
        Log.d("###", "onCreate: job " + job.isActive)
    }
}
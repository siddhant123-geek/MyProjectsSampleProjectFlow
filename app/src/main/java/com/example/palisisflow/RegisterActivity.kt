package com.example.palisisflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.palisisag.pitapp.databinding.ActivityRegisterBinding

class RegisterActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var registerBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))

        setContentView(binding.root)

        registerBtn = binding.registerPageRegisterBtn

        registerBtn.setOnClickListener {
//            Log.d("###", "onCreate: register user clicked")
            val extraString = "Sid"

            val launchIntent = Intent(this, MainActivity::class.java)
            launchIntent.apply {
                putExtra("name", extraString)
            }

            Log.d("###", "onCreate: callingActivity " + callingActivity?.className)
            if(callingActivity?.className.equals("SettingsActivity")) {
                Log.d("###", "onCreate: coming inside the activity that called it as settings flow")
                setResult(100, launchIntent)
                finish()
            }
            else {
                Log.d("###", "onCreate: coming inside the activity that called it as register flow")
                setResult(101, launchIntent)
                finish()
            }
        }
    }
}
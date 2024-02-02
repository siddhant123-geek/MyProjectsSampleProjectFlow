package com.example.palisisflow

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.palisisag.pitapp.databinding.ActivitySettingsBinding
import kotlin.math.log10

private const val TAG = "###"
class SettingsActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var registerBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this))

        setContentView(binding.root)
        registerBtn = binding.settingsRegisterBtn

        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
//            startActivityForResult(intent, 100)
            resultLauncher.launch(intent)
        }
    }

//    @Deprecated(message= "This method is deprecated")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        Log.d(TAG, "onActivityResult: coming inside onActivityResult")
//        Log.d(TAG, "onActivityResult: requestCode $requestCode")
//        Log.d(TAG, "onActivityResult: resultCode $resultCode")
//
//        if(requestCode == 100) {
//
//            Log.d(TAG, "onActivityResult: Coming inside the activity result of the settings screen")
//
//            val intent = Intent(this, MainActivity::class.java)
//
//            if(data?.getStringExtra("name") != null) {
//                intent.apply {
//                    putExtra("name", data.getStringExtra("name"))
//                }
//                Log.d(TAG, "onCreate: Coming inside currOperator != null")
//                setResult(100, intent)
//                finish()
//            }
//        }
//    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
//        Log.d(TAG, "Coming inside the resultLauncher: Ok")
        if (it.data != null) {
            val intent = Intent(this, MainActivity::class.java)
            if(it.data?.getStringExtra("name") != null) {
                intent.apply {
                    putExtra("name", it.data?.getStringExtra("name"))
                }
//                Log.d(TAG, "onCreate: Coming inside currOperator != null")
                setResult(100, intent)
                finish()
            }
        }
    }
}
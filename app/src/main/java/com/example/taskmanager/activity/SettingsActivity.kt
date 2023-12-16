package com.example.taskmanager.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private val binding: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //top app bar
        binding.topAppBarSettings.setOnClickListener(){
            onBackPressed()
        }
    }
}
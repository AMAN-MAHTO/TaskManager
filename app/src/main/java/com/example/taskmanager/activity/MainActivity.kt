package com.example.taskmanager.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivityMainBinding
import com.example.taskmanager.fragments.HomeFragment

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // navigation
        binding.bottomAppBar.setOnMenuItemClickListener() {
            when (it.itemId) {
                R.id.home -> {
                    // Handle accelerator icon press
                    supportFragmentManager.beginTransaction().replace(com.google.android.material.R.id.fragment_container_view_tag,HomeFragment()).commit()
                    true
                }

                R.id.progress -> {
                    // Handle rotation icon press
                    true
                }



                else -> false
            }

        }
    }
}
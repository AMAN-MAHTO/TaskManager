package com.example.taskmanager.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivityMainBinding
import com.example.taskmanager.fragments.HomeFragment
import com.example.taskmanager.fragments.ProgressFragment
import com.example.taskmanager.mvvm.DatesViewModel
import com.example.taskmanager.mvvm.MainDataViewModel
import com.example.taskmanager.mvvm.HomeViewModelFactory

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    lateinit var mainDataViewModel:MainDataViewModel
    lateinit var datesViewModel:DatesViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // VIEW MODEL
        mainDataViewModel = ViewModelProvider(this, HomeViewModelFactory(this)).get(MainDataViewModel::class.java)

        // date View model
        datesViewModel = ViewModelProvider(this).get(DatesViewModel::class.java)

        replaceFragment(HomeFragment(mainDataViewModel,datesViewModel))


        // navigation
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    // Handle accelerator icon press
                    replaceFragment(HomeFragment(mainDataViewModel,datesViewModel))
                    true
                }

                R.id.progress -> {
                    // Handle rotation icon press
                    replaceFragment(ProgressFragment(mainDataViewModel,datesViewModel))
                    true
                }



                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutMainActivity,fragment).commit()

    }


}
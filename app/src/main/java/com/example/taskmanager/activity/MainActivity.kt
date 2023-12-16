package com.example.taskmanager.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivityMainBinding
import com.example.taskmanager.fragments.HomeFragment
import com.example.taskmanager.fragments.NewOptionsSheet
import com.example.taskmanager.fragments.ProgressFragment
import com.example.taskmanager.mvvm.DatesViewModel
import com.example.taskmanager.mvvm.MainDataViewModel
import com.example.taskmanager.mvvm.HomeViewModelFactory
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(){
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    lateinit var mainDataViewModel:MainDataViewModel
    lateinit var datesViewModel:DatesViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // navigation drawer
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout,R.string.navigation_drawer_start,R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        // top app bar, click open drawer
        binding.topAppBar.setOnClickListener(){
            Log.d("topAppBar", "onCreate: topAppBar clicked")
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
        // navigation drawer, menu item click listener
//        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.navigationView.setNavigationItemSelectedListener{
            when(it.itemId){

                R.id.naviagtionDrawerNewHabit -> startActivity(Intent(this,NewHabit::class.java))

                R.id.naviagtionDrawerNewTask -> startActivity(Intent(this,NewTask::class.java))

                R.id.naviagtionDrawerSettings -> startActivity(Intent(this,SettingsActivity::class.java))

            }

            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            Handler().postDelayed({
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }, 200) }
            true

        }

        // Create new Task button
        binding.floatingButtonCreateNewTask.setOnClickListener(){
            NewOptionsSheet(datesViewModel).show(supportFragmentManager,"NewOptionsSheet")
        }


        // VIEW MODEL
        mainDataViewModel = ViewModelProvider(this, HomeViewModelFactory(this)).get(MainDataViewModel::class.java)

        // date View model
        datesViewModel = ViewModelProvider(this).get(DatesViewModel::class.java)

        replaceFragment(HomeFragment(mainDataViewModel,datesViewModel))


        // bottom navigation
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

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }

    }




}
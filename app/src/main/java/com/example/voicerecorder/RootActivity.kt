package com.example.voicerecorder

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.voicerecorder.databinding.RootActivityBinding

class RootActivity : AppCompatActivity() {
    private lateinit var binding: RootActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RootActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationBar.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_record -> {
                    binding.bottomNavigationBar.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigationBar.visibility = View.VISIBLE
                }
            }
        }
    }

    fun isServiceRunning(): Boolean {
        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)){
            if ("com.example.voicerecorder.recoreder.RecordService" == service.service.className){
                return true
            }
        }
        return false
    }
}
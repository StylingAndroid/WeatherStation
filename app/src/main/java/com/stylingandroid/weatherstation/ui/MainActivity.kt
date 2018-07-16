package com.stylingandroid.weatherstation.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.stylingandroid.weatherstation.R
import kotlinx.android.synthetic.main.activity_main.*

private val REQUIRED_PERMISSIONS: Array<out String> = arrayOf(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION
)

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_controller)

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(this, navController)
        setupWithNavController(toolbar, navController)

        if (REQUIRED_PERMISSIONS.any { checkSelfPermission(it) == PERMISSION_DENIED }) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 0)
        } else {
            navController.navigate(R.id.currentWeather)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (REQUIRED_PERMISSIONS.any { checkSelfPermission(it) == PERMISSION_DENIED }) {
                navController.navigate(R.id.noPermission)
            } else {
                navController.navigate(R.id.currentWeather)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.to_preferences -> item.onNavDestinationSelected(navController)
            else -> super.onOptionsItemSelected(item)
        }
    }
}

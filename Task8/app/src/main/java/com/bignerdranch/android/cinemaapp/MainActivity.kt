package com.bignerdranch.android.cinemaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bignerdranch.android.cinemaapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // переключение м/у экранами навигации
        binding.navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    switchFragment(NavigationHomeFragment())
                    true
                }
                R.id.navigation_collection -> {
                    switchFragment(NavigationCollectionsFragment())
                    true
                }
                R.id.navigation_set -> {
                    switchFragment(NavigationSetFragment())
                    true
                }
                else -> false
            }
        }

        // установка селектора для цвета иконок и текста при нажатии
        binding.navView.itemIconTintList = resources.getColorStateList(R.drawable.bottom_nav_item_color)
        binding.navView.itemTextColor = resources.getColorStateList(R.drawable.bottom_nav_item_color)

        switchFragment(NavigationHomeFragment())
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

}
/**
 * Приложение для организации совместных поездок на байк-фесты "МотоСтрана"
 * 2022 год. Разработчик - Дуюнов Павел
 */
package com.elpablo.motostrana.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.elpablo.motostrana.R
import com.elpablo.motostrana.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Убираем splash_screen
        setTheme(R.style.Theme_MotoStrana)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //настраиваем навигацию приложения
        initNavComponent()
    }

    //NavComponent
    private fun initNavComponent() {
        val navView: BottomNavigationView = binding.navView
        navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment).navController
        navView.setupWithNavController(navController)
    }

    //функция показа меню
    fun showBottomNav() {
        binding.navView.visibility = View.VISIBLE
    }

    //функция отключения показа меню
    fun hideBottomNav() {
        binding.navView.visibility = View.GONE
    }

    //функция скрытия клавиатуры
    fun hideInputKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
}
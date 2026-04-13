package com.habittracker.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val habitsTab = HabitsFragment()
    private val todoTab   = TodoFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, habitsTab, "habits")
                .add(R.id.container, todoTab,   "todos")
                .hide(todoTab)
                .commit()
        }

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> {
                    supportFragmentManager.beginTransaction()
                        .show(habitsTab).hide(todoTab).commit()
                    true
                }
                R.id.nav_todo -> {
                    supportFragmentManager.beginTransaction()
                        .show(todoTab).hide(habitsTab).commit()
                    true
                }
                else -> false
            }
        }
    }
}

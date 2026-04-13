package com.habittracker.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val habitsFragment = HabitsFragment()
    private val todoFragment   = TodoFragment()

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_main)

        if (s == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, habitsFragment, "habits")
                .add(R.id.container, todoFragment,   "todos")
                .hide(todoFragment)
                .commit()
        }

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> {
                    supportFragmentManager.beginTransaction().show(habitsFragment).hide(todoFragment).commit()
                    true
                }
                R.id.nav_todo -> {
                    supportFragmentManager.beginTransaction().show(todoFragment).hide(habitsFragment).commit()
                    true
                }
                else -> false
            }
        }
    }
}

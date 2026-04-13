package com.habittracker.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataManager(context: Context) {
    private val prefs = context.getSharedPreferences("ht_prefs", Context.MODE_PRIVATE)
    private val gson  = Gson()

    fun saveHabits(list: List<Habit>) =
        prefs.edit().putString("habits", gson.toJson(list)).apply()

    fun loadHabits(): MutableList<Habit> {
        val json = prefs.getString("habits", null) ?: return mutableListOf()
        return try {
            gson.fromJson(json, object : TypeToken<MutableList<Habit>>() {}.type)
                ?: mutableListOf()
        } catch (e: Exception) { mutableListOf() }
    }

    fun saveTodos(list: List<TodoItem>) =
        prefs.edit().putString("todos", gson.toJson(list)).apply()

    fun loadTodos(): MutableList<TodoItem> {
        val json = prefs.getString("todos", null) ?: return mutableListOf()
        return try {
            gson.fromJson(json, object : TypeToken<MutableList<TodoItem>>() {}.type)
                ?: mutableListOf()
        } catch (e: Exception) { mutableListOf() }
    }
}

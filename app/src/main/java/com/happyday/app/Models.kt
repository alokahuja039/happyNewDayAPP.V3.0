package com.happyday.app

data class Habit(
    val id: String,
    val name: String,
    val completedDates: MutableSet<String> = mutableSetOf()
)

data class TodoItem(
    val id: String,
    var text: String,
    var isDone: Boolean = false
)

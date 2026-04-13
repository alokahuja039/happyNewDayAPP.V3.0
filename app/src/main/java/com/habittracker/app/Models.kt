package com.habittracker.app

data class Habit(
    val id: String,
    val name: String,
    val goalDirection: String = "do_more",
    val trackingPeriod: String = "daily",
    val goalCount: Int = 1,
    val startDate: String = "",
    val completedDates: MutableMap<String, Int> = mutableMapOf()
)

data class TodoItem(
    val id: String,
    var text: String,
    var isDone: Boolean = false,
    val createdDate: String = ""
)

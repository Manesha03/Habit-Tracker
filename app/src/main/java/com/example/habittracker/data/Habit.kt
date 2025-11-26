package com.example.habittracker.data

import java.util.Date

data class Habit(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "Other",
    val isPeriodic: Boolean = false,
    val frequency: Int = 1, // times per day
    val reminderTime: String = "",
    val color: String = "#FF5722",
    val icon: String = "🏃",
    val isCompleted: Boolean = false,
    val completedDate: Date? = null,
    val scheduledDate: Date? = null,
    val streak: Int = 0
)
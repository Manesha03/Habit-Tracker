package com.example.habittracker.data

import java.util.Date

data class Mood(
    val id: String = "",
    val emoji: String = "😐",
    val moodLevel: Int = 3, // 1-5 scale (1=awful, 5=awesome)
    val note: String = "",
    val tags: List<String> = emptyList(),
    val date: Date = Date(),
    val time: String = ""
)

enum class MoodLevel(val emoji: String, val level: Int, val label: String) {
    AWFUL("😞", 1, "Awful"),
    BAD("😔", 2, "Bad"), 
    NEUTRAL("😐", 3, "Neutral"),
    GOOD("😊", 4, "Good"),
    AWESOME("😄", 5, "Awesome")
}
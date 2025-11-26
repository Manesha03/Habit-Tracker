package com.example.habittracker.data

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar
import java.util.Date

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("habit_tracker_prefs", Context.MODE_PRIVATE)

    // Habit Management - Simple storage without JSON for now
    fun saveHabits(habits: List<Habit>) {
        // For now, we'll store habits as simple key-value pairs
        // This is a simplified approach that doesn't require Gson
        val editor = sharedPreferences.edit()
        editor.putInt("habits_count", habits.size)
        for (i in habits.indices) {
            val habit = habits[i]
            editor.putString("habit_${i}_id", habit.id)
            editor.putString("habit_${i}_name", habit.name)
            editor.putString("habit_${i}_description", habit.description)
            editor.putString("habit_${i}_category", habit.category)
            editor.putBoolean("habit_${i}_isPeriodic", habit.isPeriodic)
            editor.putInt("habit_${i}_frequency", habit.frequency)
            editor.putString("habit_${i}_reminderTime", habit.reminderTime)
            editor.putString("habit_${i}_color", habit.color)
            editor.putString("habit_${i}_icon", habit.icon)
            editor.putBoolean("habit_${i}_isCompleted", habit.isCompleted)
            editor.putLong("habit_${i}_completedDate", habit.completedDate?.time ?: 0L)
            editor.putInt("habit_${i}_streak", habit.streak)
            editor.putLong("habit_${i}_scheduledDate", habit.scheduledDate?.time ?: 0L)
        }
        editor.apply()
    }

    fun getHabits(): List<Habit> {
        val habitsCount = sharedPreferences.getInt("habits_count", 0)
        val habits = mutableListOf<Habit>()
        
        for (i in 0 until habitsCount) {
            val habit = Habit(
                id = sharedPreferences.getString("habit_${i}_id", "") ?: "",
                name = sharedPreferences.getString("habit_${i}_name", "") ?: "",
                description = sharedPreferences.getString("habit_${i}_description", "") ?: "",
                category = sharedPreferences.getString("habit_${i}_category", "Other") ?: "Other",
                isPeriodic = sharedPreferences.getBoolean("habit_${i}_isPeriodic", false),
                frequency = sharedPreferences.getInt("habit_${i}_frequency", 1),
                reminderTime = sharedPreferences.getString("habit_${i}_reminderTime", "") ?: "",
                color = sharedPreferences.getString("habit_${i}_color", "#FF5722") ?: "#FF5722",
                icon = sharedPreferences.getString("habit_${i}_icon", "🏃") ?: "🏃",
                isCompleted = sharedPreferences.getBoolean("habit_${i}_isCompleted", false),
                completedDate = sharedPreferences.getLong("habit_${i}_completedDate", 0L)
                    .let { if (it == 0L) null else Date(it) },
                scheduledDate = sharedPreferences.getLong("habit_${i}_scheduledDate", 0L)
                    .let { if (it == 0L) null else Date(it) },
                streak = sharedPreferences.getInt("habit_${i}_streak", 0)
            )
            habits.add(habit)
        }
        
        return habits
    }

    fun saveHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        val existingIndex = habits.indexOfFirst { it.id == habit.id }
        if (existingIndex >= 0) {
            habits[existingIndex] = habit
        } else {
            habits.add(habit)
        }
        saveHabits(habits)
    }


    // Mood Management - Simple storage without JSON
    fun saveMoods(moods: List<Mood>) {
        val editor = sharedPreferences.edit()
        editor.putInt("moods_count", moods.size)
        for (i in moods.indices) {
            val mood = moods[i]
            editor.putString("mood_${i}_id", mood.id)
            editor.putString("mood_${i}_emoji", mood.emoji)
            editor.putInt("mood_${i}_level", mood.moodLevel)
            editor.putString("mood_${i}_note", mood.note)
            editor.putString("mood_${i}_tags", mood.tags.joinToString(","))
            editor.putLong("mood_${i}_date", mood.date.time)
            editor.putString("mood_${i}_time", mood.time)
        }
        editor.apply()
    }

    fun getMoods(): List<Mood> {
        val moodsCount = sharedPreferences.getInt("moods_count", 0)
        val moods = mutableListOf<Mood>()
        
        for (i in 0 until moodsCount) {
            val mood = Mood(
                id = sharedPreferences.getString("mood_${i}_id", "") ?: "",
                emoji = sharedPreferences.getString("mood_${i}_emoji", "😐") ?: "😐",
                moodLevel = sharedPreferences.getInt("mood_${i}_level", 3),
                note = sharedPreferences.getString("mood_${i}_note", "") ?: "",
                tags = sharedPreferences.getString("mood_${i}_tags", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList(),
                date = Date(sharedPreferences.getLong("mood_${i}_date", System.currentTimeMillis())),
                time = sharedPreferences.getString("mood_${i}_time", "") ?: ""
            )
            moods.add(mood)
        }
        
        return moods
    }

    fun saveMood(mood: Mood) {
        val moods = getMoods().toMutableList()
        val existingIndex = moods.indexOfFirst { it.id == mood.id }
        if (existingIndex >= 0) {
            moods[existingIndex] = mood
        } else {
            moods.add(mood)
        }
        saveMoods(moods)
    }

    fun deleteHabit(habitId: String) {
        val habits = getHabits().toMutableList()
        habits.removeAll { it.id == habitId }
        saveHabits(habits)
    }

    // In PreferencesHelper.kt
    fun resetDailyHabits() {
        val habits = getHabits()
        val today = Calendar.getInstance()
        val updatedHabits = habits.map { habit ->
            if (habit.completedDate != null) {
                val completedCal = Calendar.getInstance()
                completedCal.time = habit.completedDate

                // Reset if not completed today
                if (completedCal.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR) ||
                    completedCal.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {
                    habit.copy(isCompleted = false, completedDate = null)
                } else {
                    habit
                }
            } else {
                habit
            }
        }
        saveHabits(updatedHabits)
    }

    // reminder
    fun saveHydrationReminderEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("hydration_reminder_enabled", enabled).apply()
    }

    fun isHydrationReminderEnabled(): Boolean {
        return sharedPreferences.getBoolean("hydration_reminder_enabled", false)
    }

    fun saveHydrationInterval(intervalMinutes: Int) {
        sharedPreferences.edit().putInt("hydration_interval", intervalMinutes).apply()
    }


    fun getHydrationInterval(): Int {
        return sharedPreferences.getInt("hydration_interval", 60) // default 1 hour
    }

    // Onboarding
    fun setOnboardingSeen(seen: Boolean) {
        sharedPreferences.edit().putBoolean("onboarding_seen", seen).apply()
    }

    fun isOnboardingSeen(): Boolean {
        return sharedPreferences.getBoolean("onboarding_seen", false)
    }

}
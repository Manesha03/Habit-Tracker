package com.example.habittracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.habittracker.data.PreferencesHelper
import com.example.habittracker.ui.fragments.MoodFragment
import com.example.habittracker.ui.fragments.HabitsFragment
import com.example.habittracker.ui.fragments.SettingsFragment
import com.example.habittracker.ui.fragments.StatisticsFragment
import com.example.habittracker.sensors.ShakeDetector
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var shakeDetector: ShakeDetector
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initializeViews()
        setupBottomNavigation()
        setupFloatingActionButton()
        setupShakeDetection()
        
        // Start with Habits fragment
        if (savedInstanceState == null) {
            replaceFragment(HabitsFragment())
        }
        PreferencesHelper(this).resetDailyHabits()
    }
    
    private fun initializeViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        fabAdd = findViewById(R.id.fab_add)
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> {
                    replaceFragment(HabitsFragment())
                    true
                }
                R.id.nav_mood -> {
                    replaceFragment(MoodFragment())
                    true
                }
                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                R.id.nav_statistics -> {
                    replaceFragment(StatisticsFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupFloatingActionButton() {
        fabAdd.setOnClickListener {
            // Show add habit dialog or navigate to add habit screen
            showAddHabitDialog()
        }
    }
    
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    private fun setupShakeDetection() {
        shakeDetector = ShakeDetector(this) {
            // Quick mood entry on shake
            showQuickMoodDialog()
        }
        shakeDetector.start()
    }

    private fun showQuickMoodDialog() {
        val moods = arrayOf("😞 Awful", "😔 Bad", "😐 Neutral", "😊 Good", "😄 Awesome")
        android.app.AlertDialog.Builder(this)
            .setTitle("How are you feeling?")
            .setItems(moods) { _, which ->
                saveQuickMood(which + 1) // mood level 1-5
            }
            .show()
    }

    private fun saveQuickMood(moodLevel: Int) {
        val preferencesHelper = PreferencesHelper(this)
        val moodEmoji = when(moodLevel) {
            1 -> "😞"
            2 -> "😔"
            3 -> "😐"
            4 -> "😊"
            else -> "😄"
        }

        val mood = com.example.habittracker.data.Mood(
            id = java.util.UUID.randomUUID().toString(),
            emoji = moodEmoji,
            moodLevel = moodLevel,
            note = "Quick entry",
            tags = emptyList(),
            date = java.util.Date(),
            time = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                .format(java.util.Date())
        )

        preferencesHelper.saveMood(mood)
        Toast.makeText(this, "Mood saved!", Toast.LENGTH_SHORT).show()
    }
    
    private fun showAddHabitDialog() {
        val dialog = com.example.habittracker.ui.dialogs.AddHabitDialog()
        dialog.show(supportFragmentManager, "AddHabitDialog")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        shakeDetector.stop()
    }
}
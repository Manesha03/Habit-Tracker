package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.data.PreferencesHelper

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)

        findViewById<Button>(R.id.btn_get_started).setOnClickListener {
            PreferencesHelper(this).setOnboardingSeen(true)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}



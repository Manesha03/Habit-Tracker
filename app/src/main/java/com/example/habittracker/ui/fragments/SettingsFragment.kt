package com.example.habittracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.habittracker.R
import com.example.habittracker.data.PreferencesHelper
import com.example.habittracker.services.HydrationReminderService

class SettingsFragment : Fragment() {
    
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var hydrationService: HydrationReminderService
    
    private lateinit var switchHydrationReminder: Switch
    private lateinit var seekBarInterval: SeekBar
    private lateinit var tvIntervalValue: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = PreferencesHelper(requireContext())
        hydrationService = HydrationReminderService(requireContext())
        
        initializeViews(view)
        setupSettings()
    }
    
    private fun initializeViews(view: View) {
        switchHydrationReminder = view.findViewById(R.id.switch_hydration_reminder)
        seekBarInterval = view.findViewById(R.id.seekbar_interval)
        tvIntervalValue = view.findViewById(R.id.tv_interval_value)
        view.findViewById<View>(R.id.btn_save_hydration).setOnClickListener {
            onSaveHydration()
        }
    }
    
    private fun setupSettings() {
        // Load current settings
        switchHydrationReminder.isChecked = preferencesHelper.isHydrationReminderEnabled()
        val currentInterval = preferencesHelper.getHydrationInterval()
        seekBarInterval.progress = currentInterval / 15 // Convert to 15-minute increments
        updateIntervalText(currentInterval)
        
        // Set up listeners
        switchHydrationReminder.setOnCheckedChangeListener { _, isChecked ->
            hydrationService.enableHydrationReminders(isChecked)
            if (isChecked) {
                // Ensure a job is scheduled immediately with current interval
                hydrationService.startHydrationReminders()
            }
        }

        // Open hydration screen via explicit button (no long-press navigation anymore)
        
        seekBarInterval.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val interval = (progress + 1) * 15 // 15-minute increments, minimum 15 minutes
                updateIntervalText(interval)
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val interval = (seekBar?.progress ?: 0 + 1) * 15
                // Wait for explicit Save
                preferencesHelper.saveHydrationInterval(interval)
            }
        })
    }
    
    private fun updateIntervalText(intervalMinutes: Int) {
        val hours = intervalMinutes / 60
        val minutes = intervalMinutes % 60
        
        val text = when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
        
        tvIntervalValue.text = "Every $text"
    }

    private fun onSaveHydration() {
        val enabled = switchHydrationReminder.isChecked
        hydrationService.enableHydrationReminders(enabled)
        if (enabled) {
            hydrationService.startHydrationReminders()
            Toast.makeText(requireContext(), "Hydration reminder saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Hydration reminder disabled", Toast.LENGTH_SHORT).show()
        }
    }
}

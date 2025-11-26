package com.example.habittracker.services

import android.content.Context
import androidx.work.*
import com.example.habittracker.data.PreferencesHelper
import com.example.habittracker.work.HydrationWorker
import java.util.concurrent.TimeUnit

class HydrationReminderService(private val context: Context) {
    
    private val preferencesHelper = PreferencesHelper(context)
    private val workManager = WorkManager.getInstance(context)
    
    fun startHydrationReminders() {
        if (!preferencesHelper.isHydrationReminderEnabled()) {
            return  // Early exit if disabled
        }
        
        val intervalMinutes = preferencesHelper.getHydrationInterval()
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)    // Avoid draining battery
            .build()

        // Assumes HydrationWorker handles notification
        val hydrationRequest = PeriodicWorkRequestBuilder<HydrationWorker>(
            intervalMinutes.toLong(), TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(intervalMinutes.toLong(), TimeUnit.MINUTES)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            "hydration_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,   // Overwrite if already scheduled
            hydrationRequest
        )
    }
    
    fun stopHydrationReminders() {
        workManager.cancelUniqueWork("hydration_reminder")   // Cancel ongoing work
    }
    
    fun updateHydrationInterval(intervalMinutes: Int) {
        preferencesHelper.saveHydrationInterval(intervalMinutes)
        
        if (preferencesHelper.isHydrationReminderEnabled()) {
            stopHydrationReminders()
            startHydrationReminders()   // Reschedule with new interval
        }
    }
    
    fun enableHydrationReminders(enabled: Boolean) {
        preferencesHelper.saveHydrationReminderEnabled(enabled)
        
        if (enabled) {
            startHydrationReminders()
        } else {
            stopHydrationReminders()
        }
    }
}

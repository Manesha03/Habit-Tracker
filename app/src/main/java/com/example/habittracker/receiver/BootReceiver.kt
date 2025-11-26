package com.example.habittracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.habittracker.data.PreferencesHelper
import com.example.habittracker.services.HydrationReminderService

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                // Restart hydration reminders if they were enabled
                val preferencesHelper = PreferencesHelper(context)
                if (preferencesHelper.isHydrationReminderEnabled()) {
                    val hydrationService = HydrationReminderService(context)
                    hydrationService.startHydrationReminders()
                }
            }
        }
    }
}
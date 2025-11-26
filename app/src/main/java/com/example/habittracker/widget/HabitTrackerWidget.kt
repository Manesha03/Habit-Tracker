package com.example.habittracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import com.example.habittracker.data.PreferencesHelper

class HabitTrackerWidget : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onEnabled(context: Context) {
        // Called when the first widget is created
    }
    
    override fun onDisabled(context: Context) {
        // Called when the last widget is deleted
    }
    
    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val preferencesHelper = PreferencesHelper(context)
            val habits = preferencesHelper.getHabits()
            
            val completedHabits = habits.count { it.isCompleted }
            val totalHabits = habits.size
            val progressPercentage = if (totalHabits > 0) {
                (completedHabits * 100) / totalHabits
            } else {
                0
            }
            
            val views = RemoteViews(context.packageName, R.layout.widget_habit_tracker)
            
            // Set progress text
            views.setTextViewText(R.id.tv_widget_progress, "$progressPercentage%")
            views.setTextViewText(R.id.tv_widget_completed, "$completedHabits/$totalHabits")
            
            // Set progress bar
            views.setProgressBar(R.id.progress_bar_widget, 100, progressPercentage, false)
            
            // Set click intent
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent)
            
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

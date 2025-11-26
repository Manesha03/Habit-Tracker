package com.example.habittracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.habittracker.R
import com.example.habittracker.data.Habit
import com.example.habittracker.data.Mood
import com.example.habittracker.data.PreferencesHelper
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {
    
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var layoutStats: LinearLayout
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = PreferencesHelper(requireContext())
        layoutStats = view.findViewById(R.id.layout_stats)
        
        setupStatistics()
    }
    
    private fun setupStatistics() {
        layoutStats.removeAllViews()
        
        val habits = preferencesHelper.getHabits()
        val moods = preferencesHelper.getMoods()
        
        // Overall Progress
        addStatCard("Overall Progress", "${getOverallProgress(habits)}%", "Your completion rate")
        
        // Total Habits
        addStatCard("Total Habits", "${habits.size}", "Habits you're tracking")
        
        // Completed Today
        val completedToday = habits.count { it.isCompleted }
        addStatCard("Completed Today", "$completedToday", "Habits completed today")
        
        // Mood Entries
        addStatCard("Mood Entries", "${moods.size}", "Total mood entries")
        
        // Removed current streak and average mood as requested
    }
    
    private fun addStatCard(title: String, value: String, description: String) {
        val statView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_stat_card, layoutStats, false)
        
        val tvTitle = statView.findViewById<TextView>(R.id.tv_stat_title)
        val tvValue = statView.findViewById<TextView>(R.id.tv_stat_value)
        val tvDescription = statView.findViewById<TextView>(R.id.tv_stat_description)
        
        tvTitle.text = title
        tvValue.text = value
        tvDescription.text = description
        
        layoutStats.addView(statView)
    }
    
    private fun getOverallProgress(habits: List<Habit>): Int {
        if (habits.isEmpty()) return 0
        val completed = habits.count { it.isCompleted }
        return (completed * 100) / habits.size
    }
    
    // Removed current streak helper
}

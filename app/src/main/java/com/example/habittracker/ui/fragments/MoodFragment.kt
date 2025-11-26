package com.example.habittracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.habittracker.R
import com.example.habittracker.data.Mood
import com.example.habittracker.data.MoodLevel
import com.example.habittracker.data.PreferencesHelper
import java.text.SimpleDateFormat
import java.util.*

class MoodFragment : Fragment() {
    
    private lateinit var preferencesHelper: PreferencesHelper
    private var selectedMoodLevel: MoodLevel = MoodLevel.NEUTRAL
    private val selectedTags = mutableSetOf<String>() // deprecated
    
    private lateinit var tvDateTime: TextView
    private lateinit var layoutMoodEmojis: LinearLayout
    private lateinit var layoutMoodList: LinearLayout
    private lateinit var btnSaveMood: Button
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = PreferencesHelper(requireContext())
        
        initializeViews(view)
        setupDateTime()
        setupMoodEmojis()
        setupSaveButton()
        renderMoodList()
    }
    
    private fun initializeViews(view: View) {
        tvDateTime = view.findViewById(R.id.tv_date_time)
        layoutMoodEmojis = view.findViewById(R.id.layout_mood_emojis)
        layoutMoodList = view.findViewById(R.id.layout_mood_list)
        btnSaveMood = view.findViewById(R.id.btn_save_mood)
    }
    
    private fun setupDateTime() {
        val currentTime = Date()
        val dateFormat = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
        tvDateTime.text = dateFormat.format(currentTime)
    }
    
    private fun setupMoodEmojis() {
        layoutMoodEmojis.removeAllViews()
        
        MoodLevel.values().forEach { moodLevel ->
            val moodView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_mood_emoji, layoutMoodEmojis, false)
            
            val tvEmoji = moodView.findViewById<TextView>(R.id.tv_emoji)
            val tvLabel = moodView.findViewById<TextView>(R.id.tv_mood_label)
            
            tvEmoji.text = moodLevel.emoji
            tvLabel.text = moodLevel.label
            
            // Set initial selection
            if (moodLevel == selectedMoodLevel) {
                moodView.setBackgroundResource(R.drawable.selected_mood_background)
            }
            
            moodView.setOnClickListener {
                selectMoodLevel(moodLevel, moodView)
            }
            
            layoutMoodEmojis.addView(moodView)
        }
    }
    
    private fun selectMoodLevel(moodLevel: MoodLevel, selectedView: View) {
        selectedMoodLevel = moodLevel
        
        // Reset all mood views
        for (i in 0 until layoutMoodEmojis.childCount) {
            val child = layoutMoodEmojis.getChildAt(i)
            child.setBackgroundResource(R.drawable.mood_background)
        }
        
        // Set selected mood
        selectedView.setBackgroundResource(R.drawable.selected_mood_background)
    }
    
    private fun renderMoodList() {
        layoutMoodList.removeAllViews()
        val moods = preferencesHelper.getMoods().sortedByDescending { it.date }
        val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        moods.forEach { mood ->
            val row = TextView(requireContext())
            row.text = "${mood.emoji}  ${dateFormat.format(mood.date)}"
            row.textSize = 16f
            row.setTextColor(resources.getColor(android.R.color.black, null))
            row.setPadding(8, 8, 8, 8)
            layoutMoodList.addView(row)
        }
    }
    
    private fun setupSaveButton() {
        btnSaveMood.setOnClickListener {
            saveMood()
        }
    }
    
    private fun saveMood() {
        val currentTime = Date()
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        
        val mood = Mood(
            id = UUID.randomUUID().toString(),
            emoji = selectedMoodLevel.emoji,
            moodLevel = selectedMoodLevel.level,
            note = "",
            tags = emptyList(),
            date = currentTime,
            time = timeFormat.format(currentTime)
        )
        
        preferencesHelper.saveMood(mood)
        
        // Show success message
        Toast.makeText(requireContext(), "Mood saved successfully!", Toast.LENGTH_SHORT).show()
        
        // Clear form
        selectedMoodLevel = MoodLevel.NEUTRAL
        setupMoodEmojis()
        renderMoodList()
    }
}

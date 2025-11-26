package com.example.habittracker.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    private val context: Context,
    private val onShakeDetected: () -> Unit
) : SensorEventListener {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null
    
    private var lastUpdate: Long = 0
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f
    
    private val SHAKE_THRESHOLD = 800f
    private val TIME_THRESHOLD = 200L
    
    fun start() {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    
    fun stop() {
        sensorManager.unregisterListener(this)
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            val currentTime = System.currentTimeMillis()
            
            if (currentTime - lastUpdate > TIME_THRESHOLD) {
                val x = sensorEvent.values[0]
                val y = sensorEvent.values[1]
                val z = sensorEvent.values[2]
                
                val deltaX = x - lastX
                val deltaY = y - lastY
                val deltaZ = z - lastZ
                
                val delta = sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble())
                
                if (delta > SHAKE_THRESHOLD) {
                    onShakeDetected()
                }
                
                lastX = x
                lastY = y
                lastZ = z
                lastUpdate = currentTime
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}

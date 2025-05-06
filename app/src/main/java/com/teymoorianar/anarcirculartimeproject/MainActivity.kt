package com.teymoorianar.anarcirculartimeproject


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.teymoorianar.anar_circular_time.AnarCircularTimeView

class MainActivity : AppCompatActivity() {
    private lateinit var progressView: AnarCircularTimeView
    private val handler = Handler(Looper.getMainLooper())
    private var remainingTime = 240000L // 60 seconds in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressView = findViewById(R.id.progressView)
        progressView.setTotalTime(240000L) // Total time: 60 seconds
        progressView.setRemainingTime(remainingTime)

        // Simulate countdown
        startCountdown()
    }

    private fun startCountdown() {
        handler.post(object : Runnable {
            override fun run() {
                remainingTime -= 1000L
                progressView.setRemainingTime(remainingTime)
                if (remainingTime > 0) {
                    handler.postDelayed(this, 1000L)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
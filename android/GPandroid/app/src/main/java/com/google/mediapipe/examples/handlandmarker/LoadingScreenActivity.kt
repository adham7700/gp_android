package com.google.mediapipe.examples.handlandmarker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class LoadingScreenActivity : AppCompatActivity() {
    private val loadingDelay: Long = 5000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_screen)

        // Delay for loading screen
        Handler().postDelayed({
            // Start main activity
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Finish loading screen activity
        }, loadingDelay)
    }
}
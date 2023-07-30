package com.hfad.ismlarmanosi2023

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // Optionally, add a delay for the splash screen
        gif2()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish() // Optional: Close this activity to prevent going back to it using the back button
            finish()
        }, 2000) // 2000 milliseconds (2 seconds) delay before starting the main activity
    }

    fun gif2() {
        val gifImageView: ImageView = findViewById(R.id.giffy)
        Glide.with(this)
            .asGif()
            .load(R.drawable.giphy) // Assuming "fire.gif" is the name of your animated GIF file
            .into(gifImageView)
    }
}
package com.lexora.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(com.lexora.app.utils.LanguageManager.wrap(newBase))
    }

    private fun applyImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) applyImmersiveMode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyImmersiveMode()

        setContentView(R.layout.activity_splash)

        val videoView = findViewById<VideoView>(R.id.videoView)

        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.splash_video}")
        videoView.setVideoURI(videoUri)

        videoView.setOnCompletionListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        videoView.setOnErrorListener { _, _, _ ->
                        startActivity(Intent(this, MainActivity::class.java))
            finish()
            true
        }

        videoView.start()
    }
}
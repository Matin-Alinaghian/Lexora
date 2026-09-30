package com.lexora.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.content.ContextCompat
import com.lexora.app.ui.LexoraMainContainer
import com.lexora.app.ui.theme.*
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager
import com.lexora.app.utils.LocalMusicManager
import com.lexora.app.utils.BackgroundMusicManager
import com.lexora.app.utils.LanguageManager
import com.lexora.app.utils.LiveStudyTimer
import com.lexora.app.utils.StudyTimeTracker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var soundManager: SoundManager

    @Inject
    lateinit var musicManager: BackgroundMusicManager

    @Inject
    lateinit var studyTimeTracker: StudyTimeTracker

    @Inject
    lateinit var liveStudyTimer: LiveStudyTimer

    
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(studyTimeTracker)
        lifecycle.addObserver(liveStudyTimer)
        enableEdgeToEdge()
        applyImmersiveMode()
        requestNotificationPermission()
        musicManager.startMusic()
        setContent {
            LexoraTheme {
                CompositionLocalProvider(
                    LocalSoundManager provides soundManager,
                    LocalMusicManager provides musicManager
                ) {
                    val currentTheme = ThemeManager.getTheme()
                    val bgColor = if (currentTheme == ThemeType.LIGHT) LightBackground else DarkBackground
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = bgColor,
                    ) {
                        LexoraMainContainer()
                    }
                }
            }
        }
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

    override fun onResume() {
        super.onResume()
        applyImmersiveMode()
        musicManager.startMusic()
    }

    override fun onPause() {
        super.onPause()
        musicManager.pauseMusic()
    }
}

package com.judas.katachi.feature.home

import android.app.WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
import android.app.WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.judas.katachi.databinding.HomeActivityBinding
import com.judas.katachi.feature.about.AboutActivity
import com.judas.katachi.feature.wallpaper.KatachiWallpaperService
import com.judas.katachi.utility.Logger.Level.DEBUG
import com.judas.katachi.utility.log
import dev.chrisbanes.insetter.applyInsetter


class HomeActivity : AppCompatActivity() {
    private lateinit var viewBinding: HomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log(DEBUG, "onCreate")

        viewBinding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.root.applyInsetter {
            type(navigationBars = true, statusBars = true) { padding() }
        }

        // About
        viewBinding.about.setOnClickListener { AboutActivity.start(this@HomeActivity) }

        // Submit => Launch wallpaper update intent
        viewBinding.submit.setOnClickListener {
            startActivity(Intent(ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this@HomeActivity, KatachiWallpaperService::class.java)
                )
            })
        }
    }
}

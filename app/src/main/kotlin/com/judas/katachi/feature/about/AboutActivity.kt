package com.judas.katachi.feature.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.judas.katachi.BuildConfig.VERSION_NAME
import com.judas.katachi.R
import com.judas.katachi.databinding.AboutActivityBinding
import com.judas.katachi.utility.Logger.Level.DEBUG
import com.judas.katachi.utility.log
import dev.chrisbanes.insetter.applyInsetter

class AboutActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, AboutActivity::class.java))
        }
    }

    private lateinit var viewBinding: AboutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log(DEBUG, "onCreate")

        viewBinding = AboutActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.root.applyInsetter {
            type(navigationBars = true, statusBars = true) { padding() }
        }

        // Toolbar
        viewBinding.toolbar.setNavigationOnClickListener { finish() }

        // Version
        viewBinding.version.text = getString(R.string.about_version, VERSION_NAME)
    }
}

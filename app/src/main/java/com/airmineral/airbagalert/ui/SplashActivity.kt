package com.airmineral.airbagalert.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.airmineral.airbagalert.data.PreferenceProvider
import com.airmineral.airbagalert.data.model.Incident
import com.airmineral.airbagalert.ui.auth.AuthActivity
import com.airmineral.airbagalert.ui.incident.AlertActivity
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val preferenceProvider: PreferenceProvider by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        if (intent.extras != null) {
            val incident =
                if (Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra("data", Incident::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra("data")
                } ?: Incident.EMPTY
            Log.d("MainActivity", "onCreate: $incident")

            Toast.makeText(this, incident.toString(), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AlertActivity::class.java).apply {
                putExtra("DATA", incident)
            })
        }

        if (preferenceProvider.getUserId() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }
}
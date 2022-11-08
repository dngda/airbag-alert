package com.airmineral.airbagalert.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.airmineral.airbagalert.R
import com.airmineral.airbagalert.base.BaseActivity
import com.airmineral.airbagalert.data.PreferenceProvider
import com.airmineral.airbagalert.data.model.Incident
import com.airmineral.airbagalert.data.repo.AuthRepository
import com.airmineral.airbagalert.databinding.ActivityMainBinding
import com.airmineral.airbagalert.ui.auth.AuthActivity
import com.airmineral.airbagalert.ui.incident.IncidentFragment
import com.airmineral.airbagalert.utils.scheduleNotification
import com.airmineral.airbagalert.utils.showNotificationWithFullScreenIntent
import com.airmineral.airbagalert.utils.toastLong
import com.airmineral.airbagalert.utils.toastShort
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getLayoutBinding(): (LayoutInflater) -> ActivityMainBinding =
        ActivityMainBinding::inflate

    private val authRepository: AuthRepository by inject()
    private val preferenceProvider: PreferenceProvider by inject()

    override fun setupViews(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            changeFragment(IncidentFragment())
        }
        subscribeFCM()
        checkIfUserRegistered()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_notify_now -> {
                this.showNotificationWithFullScreenIntent(
                    Incident.DUMMY
                )
            }
            R.id.menu_item_notify_later -> {
                this.scheduleNotification(Incident.DUMMY)
            }
            R.id.menu_item_logout -> {
                authRepository.signOut()
                preferenceProvider.clearUserId()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeFragment(fm: Fragment) {
        supportFragmentManager.beginTransaction().replace(binding.fragmentContainer.id, fm)
            .commit()
    }

    private fun subscribeFCM() {
        Firebase.messaging.subscribeToTopic("alert").addOnCompleteListener {
            if (it.isSuccessful) {
                toastShort("Ready to receive notifications")
                Log.d("FCM", "Subscribed to topic 'alert'")
            } else {
                toastLong("Make sure you have internet connection!")
                Log.d("FCM", "Failed to subscribe to topic 'alert'")
            }
        }
    }

    private fun checkIfUserRegistered() {
        val currentUserId = preferenceProvider.getUserId()
        authRepository.getUserData(currentUserId).observe(this) {
            if (it?.agencyName?.isEmpty() == null) {
                toastLong("Please complete your profile!")
                startActivity(Intent(this, AuthActivity::class.java))
                preferenceProvider.saveIsNewUser(true)
                finish()
            }
        }
    }
}
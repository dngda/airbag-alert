package com.airmineral.airbagalert.ui.incident

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import com.airmineral.airbagalert.R
import com.airmineral.airbagalert.base.BaseActivity
import com.airmineral.airbagalert.data.PreferenceProvider
import com.airmineral.airbagalert.data.model.Incident
import com.airmineral.airbagalert.data.repo.AlertRepository
import com.airmineral.airbagalert.databinding.ActivityAlertBinding
import com.airmineral.airbagalert.utils.turnScreenOffAndKeyguardOn
import com.airmineral.airbagalert.utils.turnScreenOnAndKeyguardOff
import org.koin.android.ext.android.inject

class AlertActivity :
    BaseActivity<ActivityAlertBinding>() {
    private val alertRepository: AlertRepository by inject()
    private val preferenceProvider: PreferenceProvider by inject()
    override fun getLayoutBinding(): (LayoutInflater) -> ActivityAlertBinding {
        return ActivityAlertBinding::inflate
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        turnScreenOnAndKeyguardOff()

        val data =
            if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra("DATA", Incident::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("DATA")
            } ?: Incident()

        binding.alertInfoCarId.text = data.car_id
        binding.alertInfoCarModel.text = data.car_model
        binding.alertInfoCarDamaged.text = data.damaged
        binding.alertInfoDate.text =
            getString(R.string.alert_info, data.convertDate(data.date), data.time)

        if (data.handled_by.isEmpty()) {
            binding.alertInfoHandledBy.apply {
                text = context.getString(R.string.unhandled)
                setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            }
        } else {
            binding.alertInfoHandledBy.apply {
                binding.btnAlertGo.isEnabled = false
                text = context.getString(R.string.handled, data.handled_by)
                setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            }
        }

        fun intentToGoogleMap() {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${data.latitude},${data.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(packageManager)?.let {
                startActivity(mapIntent)
            }
        }

        binding.btnAlertGo.setOnClickListener {
            intentToGoogleMap()
            alertRepository.handleIncident(data.id)
            binding.btnAlertGo.isEnabled = false
            binding.alertInfoHandledBy.apply {
                text =
                    context.getString(R.string.handled, preferenceProvider.getUserSavedAgencyName())
                setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            }
        }

        binding.btnAlertGoLocOnly.setOnClickListener {
            intentToGoogleMap()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }

}
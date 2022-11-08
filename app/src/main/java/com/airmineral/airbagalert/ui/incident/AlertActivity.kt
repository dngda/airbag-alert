package com.airmineral.airbagalert.ui.incident

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import com.airmineral.airbagalert.base.BaseActivity
import com.airmineral.airbagalert.data.model.Incident
import com.airmineral.airbagalert.databinding.ActivityAlertBinding
import com.airmineral.airbagalert.utils.turnScreenOffAndKeyguardOn
import com.airmineral.airbagalert.utils.turnScreenOnAndKeyguardOff

class AlertActivity :
    BaseActivity<ActivityAlertBinding>() {

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
            } ?: Incident.EMPTY

        binding.alertInfoCarId.text = data.car_id
        binding.alertInfoCarModel.text = data.car_model
        binding.alertInfoCarDamaged.text = data.damaged

        binding.btnAlertGo.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${data.latitude},${data.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(packageManager)?.let {
                startActivity(mapIntent)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }


}
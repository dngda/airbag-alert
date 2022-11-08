package com.airmineral.airbagalert.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import com.airmineral.airbagalert.base.BaseActivity
import com.airmineral.airbagalert.databinding.ActivityAuthBinding

class AuthActivity : BaseActivity<ActivityAuthBinding>() {
    override fun getLayoutBinding(): (LayoutInflater) -> ActivityAuthBinding =
        ActivityAuthBinding::inflate

    override fun setupViews(savedInstanceState: Bundle?) {

    }
}
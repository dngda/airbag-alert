package com.airmineral.airbagalert.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airmineral.airbagalert.base.BaseFragment
import com.airmineral.airbagalert.data.PreferenceProvider
import com.airmineral.airbagalert.databinding.FragmentRegisterBinding
import com.airmineral.airbagalert.ui.MainActivity
import org.koin.android.ext.android.inject

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    override fun getLayoutBinding(): (LayoutInflater, ViewGroup?, Boolean) -> FragmentRegisterBinding {
        return FragmentRegisterBinding::inflate
    }

    private val viewModel: AuthViewModel by inject()
    private val preferenceProvider: PreferenceProvider by inject()

    override fun setupViews(savedInstanceState: Bundle?) {
        binding.location.setOnClickListener {
            startActivity(Intent(requireContext(), MapsActivity::class.java))
        }
        binding.authViewModel = viewModel
        binding.lifecycleOwner = this

        binding.btnRegister.setOnClickListener {
            viewModel.register(preferenceProvider.getUserId()!!).let {
                if (it) doRegisterView(true)
            }
            viewModel.userData.observe(viewLifecycleOwner) {
                if (it != null) {
                    Log.d("RegisterFragment", "user observer: $it")
                    if (it.agencyName?.isNotEmpty() == true) {
                        preferenceProvider.saveUserAgencyName(it.agencyName)
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                        return@observe
                    }
                    doRegisterView(false)
                }
            }
        }
    }

    private fun doRegisterView(isShow: Boolean) {
        if (isShow) {
            binding.btnRegister.visibility = View.INVISIBLE
            binding.loading.visibility = View.VISIBLE
        } else {
            binding.btnRegister.visibility = View.VISIBLE
            binding.loading.visibility = View.INVISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        preferenceProvider.getUserSavedLocation().let {
            if (it != null) {
                binding.location.setText("${it.latitude}, ${it.longitude}")
            }
            viewModel.latitude.value = it?.latitude.toString()
            viewModel.longitude.value = it?.longitude.toString()
        }
        Log.d(
            "RegisterFragment",
            "onResume: ${viewModel.latitude.value} ${viewModel.longitude.value}"
        )
    }
}

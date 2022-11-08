package com.airmineral.airbagalert.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.airmineral.airbagalert.base.BaseFragment
import com.airmineral.airbagalert.data.PreferenceProvider
import com.airmineral.airbagalert.databinding.FragmentLoginBinding
import com.airmineral.airbagalert.ui.MainActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: AuthViewModel by viewModel()
    private val preferenceProvider: PreferenceProvider by inject()

    override fun getLayoutBinding(): (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding {
        return FragmentLoginBinding::inflate
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        binding.authViewModel = viewModel
        binding.lifecycleOwner = this

        preferenceProvider.isNewUser().let {
            if (it) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }
        }

        binding.btnLogin.setOnClickListener {
            viewModel.login().let {
                if (it) doLoginView(true)
            }
            viewModel.resultSignIn.observe(viewLifecycleOwner) {
                Log.d("LoginFragment", "resultSignInObserver: $it")
                if (it.exception != null) {
                    doLoginView(false)
                    viewModel.resultSignIn.value = null
                    return@observe
                }
                if (it.isNewUser) {
                    preferenceProvider.saveUserId(it.user?.uid!!)
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
                    return@observe
                }
                if (it.user != null) {
                    preferenceProvider.saveUserId(it.user.uid)
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                    return@observe
                }
                doLoginView(false)
            }
        }

        binding.loginForgotPassword.setOnClickListener {
            viewModel.resetPassword(it)
        }
    }

    private fun doLoginView(isShow: Boolean) {
        if (isShow) {
            binding.btnLogin.visibility = android.view.View.INVISIBLE
            binding.loading.visibility = android.view.View.VISIBLE
        } else {
            binding.btnLogin.visibility = android.view.View.VISIBLE
            binding.loading.visibility = android.view.View.GONE
        }
    }
}
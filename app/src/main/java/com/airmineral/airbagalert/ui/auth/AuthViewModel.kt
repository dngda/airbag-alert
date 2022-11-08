package com.airmineral.airbagalert.ui.auth

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airmineral.airbagalert.data.model.User
import com.airmineral.airbagalert.data.repo.AuthRepository
import com.airmineral.airbagalert.utils.toastLong
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val errorInfo = MutableLiveData<String>()
    val errorInfoRegister = MutableLiveData<String>()
    val resultSignIn = MutableLiveData<AuthRepository.SignInResult>()
    val userData = MutableLiveData<User>()

    fun login(): Boolean {
        return if (email.value != null && password.value != null) {
            errorInfo.value = ""
            viewModelScope.launch {
                resultSignIn.value = authRepository.signIn(email.value!!, password.value!!)
                Log.d("AuthViewModel", "login: ${resultSignIn.value}")
                if (resultSignIn.value?.exception != null) errorInfo.value =
                    resultSignIn.value?.exception?.message
            }
            true
        } else {
            errorInfo.value = "Masukkan email dan password yang valid"
            false
        }
    }

    fun resetPassword(view: View) {
        if (email.value != null) {
            errorInfo.value = ""
            authRepository.resetPassword(email.value!!).let {
                if (it) view.context.toastLong("Reset password link sent to your email")
                else errorInfo.value = "Please enter valid email"
            }
        } else {
            errorInfo.value = "Masukkan email yang valid"
        }
    }

    val agencyName = MutableLiveData<String>()
    val latitude = MutableLiveData<String>()
    val longitude = MutableLiveData<String>()
    val locationName = MutableLiveData<String>()

    fun register(uid: String): Boolean {
        return if (agencyName.value != null && latitude.value != null && longitude.value != null) {
            errorInfoRegister.value = ""
            val user = User(
                uid,
                agencyName.value!!,
                latitude.value!!,
                longitude.value!!
            )
            try {
                Log.d(
                    "AuthViewModel",
                    "registering: ${agencyName.value}, ${latitude.value}, ${longitude.value}"
                )
                authRepository.register(user).let {
                    userData.value = user
                }
            } catch (e: Exception) {
                errorInfoRegister.value = e.message
            }
            true
        } else {
            errorInfoRegister.value =
                "Masukkan nama instansi dan pastikan lokasi instansi telah valid"
            false
        }
    }
}

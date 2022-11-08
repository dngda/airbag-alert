package com.airmineral.airbagalert.data

import android.content.Context
import com.google.android.gms.maps.model.LatLng

class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("AirBagAlertPrefs", Context.MODE_PRIVATE)

    fun saveUserId(id: String) {
        prefs.edit().putString("USERID", id).apply()
    }

    fun getUserId(): String? {
        return prefs.getString("USERID", null)
    }

    fun clearUserId() {
        prefs.edit().remove("USERID").apply()
    }

    fun saveIsNewUser(isNewUser: Boolean) {
        prefs.edit().putBoolean("ISNEWUSER", isNewUser).apply()
    }

    fun isNewUser(): Boolean {
        return prefs.getBoolean("ISNEWUSER", false)
    }

    fun saveUserLocation(location: LatLng) {
        prefs.edit().putFloat("USERLAT", location.latitude.toFloat()).apply()
        prefs.edit().putFloat("USERLNG", location.longitude.toFloat()).apply()
    }

    fun getUserSavedLocation(): LatLng? {
        val lat = prefs.getFloat("USERLAT", 0f)
        val lng = prefs.getFloat("USERLNG", 0f)
        return if (lat != 0f && lng != 0f) LatLng(lat.toDouble(), lng.toDouble()) else null
    }
}
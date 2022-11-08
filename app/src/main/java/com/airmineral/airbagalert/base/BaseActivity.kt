package com.airmineral.airbagalert.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    private var _binding: VB? = null
    protected open val binding get() = _binding!!
    abstract fun getLayoutBinding(): (LayoutInflater) -> VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        _binding = getLayoutBinding().invoke(layoutInflater)
        setContentView(binding.root)
        setupViews(savedInstanceState)
    }

    abstract fun setupViews(savedInstanceState: Bundle?)
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
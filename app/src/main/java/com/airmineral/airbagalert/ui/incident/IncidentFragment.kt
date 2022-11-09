package com.airmineral.airbagalert.ui.incident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airmineral.airbagalert.base.BaseFragment
import com.airmineral.airbagalert.databinding.FragmentIncidentBinding
import org.koin.android.ext.android.inject

class IncidentFragment : BaseFragment<FragmentIncidentBinding>() {
    companion object {
        fun get() = IncidentFragment()
    }

    private val viewModel: IncidentViewModel by inject()

    override fun getLayoutBinding(): (LayoutInflater, ViewGroup?, Boolean) -> FragmentIncidentBinding {
        return FragmentIncidentBinding::inflate
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this

        binding.fmIncidentEmpty.visibility = View.VISIBLE
        binding.fragmentIncidentSwipeRefresh.isRefreshing = true

        bindRecyclerView()
        binding.fragmentIncidentSwipeRefresh.setOnRefreshListener {
            if (binding.fragmentIncidentSwipeRefresh.isRefreshing) {
                viewModel.getIncidentList()
            }
        }
    }

    private fun bindRecyclerView() {
        viewModel.getIncidentList()
        val adapter = IncidentAdapter()
        binding.rvIncidents.adapter = adapter
        viewModel.incidentList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) adapter.addItems(it)
            binding.fmIncidentEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.fragmentIncidentSwipeRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        bindRecyclerView()
    }

    fun refreshData() {
        viewModel.getIncidentList()
    }
}
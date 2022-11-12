package com.airmineral.airbagalert.ui.incident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airmineral.airbagalert.base.BaseFragment
import com.airmineral.airbagalert.databinding.FragmentIncidentBinding
import org.koin.android.ext.android.inject

class IncidentFragment : BaseFragment<FragmentIncidentBinding>() {

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
        viewModel.incidentList.observe(viewLifecycleOwner) { incidents ->
            val sortedData = incidents.sortedBy { it.handled_by }
            if (!incidents.isNullOrEmpty()) adapter.updateItemsData(sortedData)
            binding.fmIncidentEmpty.visibility =
                if (incidents.isEmpty()) View.VISIBLE else View.GONE
            binding.fragmentIncidentSwipeRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        bindRecyclerView()
    }
}
package com.airmineral.airbagalert.ui.incident

import android.content.Intent
import com.airmineral.airbagalert.R
import com.airmineral.airbagalert.base.BaseRecyclerViewAdapter
import com.airmineral.airbagalert.data.model.Incident
import com.airmineral.airbagalert.databinding.ItemIncidentBinding

class IncidentAdapter : BaseRecyclerViewAdapter<Incident, ItemIncidentBinding>() {
    override fun getLayout(): Int = R.layout.item_incident

    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ItemIncidentBinding>,
        position: Int
    ) {
        holder.binding.incident = items[position]
        holder.binding.root.setOnClickListener {
            val intent = Intent(it.context, AlertActivity::class.java)
            intent.putExtra("DATA", items[position])
            it.context.startActivity(intent)
        }
    }
}
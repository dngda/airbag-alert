package com.airmineral.airbagalert.ui.incident

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.view.View
import com.airmineral.airbagalert.R
import com.airmineral.airbagalert.base.BaseRecyclerViewAdapter
import com.airmineral.airbagalert.data.model.Incident
import com.airmineral.airbagalert.databinding.ItemIncidentBinding
import com.google.android.material.card.MaterialCardView

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
        holder.cardView = holder.binding.root as MaterialCardView
        holder.cardView.strokeColor = Color.TRANSPARENT

        if (items[position].handled_by?.isEmpty() == true || items[position].handled_by == "") {
            animateCardView(holder)
            holder.binding.itemIncidentIcon.setImageResource(R.drawable.ic_crash_red)
            holder.binding.itemIncidentCarId.setTextColor(Color.RED)
            holder.binding.itemIshandled.visibility = View.GONE
        }
    }

    private fun animateCardView(
        holder: Companion.BaseViewHolder<ItemIncidentBinding>,
    ) {
        ObjectAnimator.ofArgb(holder.cardView, "strokeColor", Color.RED).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
    }
}


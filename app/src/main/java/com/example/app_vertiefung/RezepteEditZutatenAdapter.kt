package com.example.app_vertiefung

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.opencensus.common.ServerStatsFieldEnums
import kotlinx.android.synthetic.main.fragment_rezepte_edit.view.*
import kotlinx.android.synthetic.main.rezepte_edit_schritte.view.*
import kotlinx.android.synthetic.main.rezepte_edit_zutaten.view.*

class RezepteEditZutatenAdapter(
    private var myDataset: List<Pair<String, String>>,
    val adapterOnClick: RezeptEditZutatenAdapterOnClick
) :
    RecyclerView.Adapter<RezepteEditZutatenAdapter.RezepteEditZutatenViewHolder>() {

    class RezepteEditZutatenViewHolder(val view: ConstraintLayout) : RecyclerView.ViewHolder(view)

    interface RezeptEditZutatenAdapterOnClick {
        fun removeRezeptZutat(index: Int)
        fun updateRezeptZutat(index: Int, value: Pair<String, String>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RezepteEditZutatenViewHolder {
        val constraintLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.rezepte_edit_zutaten, parent, false) as ConstraintLayout
        return RezepteEditZutatenViewHolder(constraintLayout)
    }

    override fun onBindViewHolder(holder: RezepteEditZutatenViewHolder, position: Int) {
        if (position.rem(2) == 1) {
            holder.view.setBackgroundColor(Color.parseColor("#F0F0F0"))
        }

        holder.view.editText_rezepte_edit_zutat_name.setText(myDataset[position].first)
        holder.view.editText_rezepte_edit_zutat_name.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                adapterOnClick.updateRezeptZutat(
                    position,
                    Pair(holder.view.editText_rezepte_edit_zutat_name.text.toString(), holder.view.editText_rezepte_edit_zutat_anzahl.text.toString())
                )
            }
        }
        holder.view.editText_rezepte_edit_zutat_anzahl.setText(myDataset[position].second)
        holder.view.editText_rezepte_edit_zutat_anzahl.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                adapterOnClick.updateRezeptZutat(
                    position,
                    Pair(holder.view.editText_rezepte_edit_zutat_name.text.toString(), holder.view.editText_rezepte_edit_zutat_anzahl.text.toString())
                )
            }
        }
        holder.view.button_rezepte_edit_zutaten.setOnClickListener {
            adapterOnClick.removeRezeptZutat(position)
        }
    }

    override fun getItemCount() = myDataset.size
}
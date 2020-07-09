package com.example.app_vertiefung

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rezepte_edit_schritte.view.*

class RezepteEditSchritteAdapter(
    private var myDataset: MutableList<String>,
    val adapterOnClick: RezeptEditSchritteAdapterOnClick
) :
    RecyclerView.Adapter<RezepteEditSchritteAdapter.RezepteEditSchritteViewHolder>() {

    class RezepteEditSchritteViewHolder(val view: ConstraintLayout) : RecyclerView.ViewHolder(view)

    interface RezeptEditSchritteAdapterOnClick {
        fun removeRezeptSchritt(index: Int)
        fun updateRezeptSchritt(index: Int, value: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RezepteEditSchritteViewHolder {
        val constraintLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.rezepte_edit_schritte, parent, false) as ConstraintLayout
        return RezepteEditSchritteViewHolder(constraintLayout)
    }

    override fun onBindViewHolder(holder: RezepteEditSchritteViewHolder, position: Int) {
        if (position.rem(2) == 1)
        {
            holder.view.setBackgroundColor(Color.parseColor("#F0F0F0"))
        }

        holder.view.editText_rezepte_edit_schritte.setText(myDataset[position])
        holder.view.editText_rezepte_edit_schritte.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                adapterOnClick.updateRezeptSchritt(position, holder.view.editText_rezepte_edit_schritte.text.toString())
            }
        }
        holder.view.button_rezepte_edit_schritte.setOnClickListener {
            adapterOnClick.removeRezeptSchritt(position)
        }
    }

    override fun getItemCount() = myDataset.size

}
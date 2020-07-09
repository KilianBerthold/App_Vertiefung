package com.example.app_vertiefung

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.kategorie_list_row.view.*
import models.KategorieModel

class KategorieListAdapter(
    private val data: MutableList<KategorieModel>,
    private val adapterOnClick: KategorieAdapterOnClick
) :
    RecyclerView.Adapter<KategorieListAdapter.KategorieListViewHolder>() {

    class KategorieListViewHolder(val view: ConstraintLayout) : RecyclerView.ViewHolder(view)

    interface KategorieAdapterOnClick {
        fun openKategorieDetailView(kategorie: KategorieModel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KategorieListViewHolder {
        val constraintLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.kategorie_list_row, parent, false) as ConstraintLayout
        return KategorieListViewHolder(constraintLayout)
    }

    override fun onBindViewHolder(
        holder: KategorieListViewHolder,
        position: Int
    ) {
        holder.view.kategorie_list_name.text = data[position].name
        holder.view.setOnClickListener {
            adapterOnClick.openKategorieDetailView(KategorieModel(data[position].id, data[position].name, data[position].beschreibung))
        }
    }

    override fun getItemCount() = data.size

}


package com.example.app_vertiefung

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_rezepte_edit.view.*
import kotlinx.android.synthetic.main.rezepte_edit_kategorie.view.*

class RezepteEditKategorieAdapter(
    private var myDataset: MutableList<String>,
    private var kategorieNamen: List<String>,
    val adapterOnClick: RezeptEditKategorieAdapterOnClick,
    val context: Context
) :
    RecyclerView.Adapter<RezepteEditKategorieAdapter.RezepteEditKategorieViewHolder>() {

    class RezepteEditKategorieViewHolder(val view: ConstraintLayout) : RecyclerView.ViewHolder(view)

    interface RezeptEditKategorieAdapterOnClick {
        fun removeRezeptKategorie(index: Int)
        fun updateRezeptKategorie(index: Int, value: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RezepteEditKategorieViewHolder {
        val constraintLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.rezepte_edit_kategorie, parent, false) as ConstraintLayout
        return RezepteEditKategorieViewHolder(constraintLayout)
    }

    override fun onBindViewHolder(holder: RezepteEditKategorieViewHolder, position: Int) {
        if (position.rem(2) == 1)
        {
            holder.view.setBackgroundColor(Color.parseColor("#F0F0F0"))
        }
        val spinner: Spinner = holder.view.findViewById(R.id.spinner_rezepte_edit_kategorie)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, kategorieNamen)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        holder.view.spinner_rezepte_edit_kategorie.setSelection(kategorieNamen.indexOf(myDataset[position]))
        holder.view.button_rezepte_edit_kategorie.setOnClickListener {
            adapterOnClick.removeRezeptKategorie(position)
        }
        holder.view.spinner_rezepte_edit_kategorie.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                adapterOnClick.updateRezeptKategorie(position, kategorieNamen[p2])
            }
        }
    }

    override fun getItemCount() = myDataset.size

}
package com.example.app_vertiefung

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rezepte_list_row.view.*
import com.example.app_vertiefung.models.SlimRezeptModel
import java.text.DateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class RezepteListAdapter(
    private var myDataset: MutableList<SlimRezeptModel>,
    val adapterOnClick: RezeptAdapterOnClick
) :
    RecyclerView.Adapter<RezepteListAdapter.RezepteListViewHolder>() {

    class RezepteListViewHolder(val view: ConstraintLayout) : RecyclerView.ViewHolder(view)

    interface RezeptAdapterOnClick {
        fun openRezeptDetailView(id: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RezepteListViewHolder {
        val constraintLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.rezepte_list_row, parent, false) as ConstraintLayout
        return RezepteListViewHolder(constraintLayout)
    }

    override fun onBindViewHolder(holder: RezepteListViewHolder, position: Int) {
        holder.view.rezepte_list_name.text = myDataset[position].name
        holder.view.rezepte_list_kategorie.text =
            if (myDataset[position].kategorien.size > 0)
                myDataset[position].kategorien.joinToString(", ")
            else
                "Keine"
        holder.view.rezepte_list_kalorien.text = myDataset[position].kalorien.toString()
        holder.view.rezepte_list_zuletztgekocht.text =
            if (myDataset[position].anzahlGekocht != 0.toLong())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    myDataset[position].zuletztGekochtAm.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).toString()
                } else {
                    val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY)
                    df.format(myDataset[position].zuletztGekochtAm)
                }
            else "Noch nie."
        holder.view.rezepte_list_anzahlgekocht.text = myDataset[position].anzahlGekocht.toString()
        holder.view.setOnClickListener {
            adapterOnClick.openRezeptDetailView(myDataset[position].id)
        }
    }

    override fun getItemCount() = myDataset.size

}
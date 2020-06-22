package com.example.app_vertiefung

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rezepte_list_row.view.*
import models.SlimRezeptModel

class RezepteListAdapter(private val myDataset: MutableList<SlimRezeptModel>) :
    RecyclerView.Adapter<RezepteListAdapter.RezepteListViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class RezepteListViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RezepteListAdapter.RezepteListViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rezepte_list_row, parent, false) as TextView
        // set the view's size, margins, paddings and layout parameters
        //...
        return RezepteListViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RezepteListViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.view.rezepte_list_name.text = myDataset[position].name
        holder.view.rezepte_list_kategorie.text = myDataset[position].kategorien.orEmpty().joinToString(", ")
        holder.view.rezepte_list_kalorien.text = myDataset[position].kalorien.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

}
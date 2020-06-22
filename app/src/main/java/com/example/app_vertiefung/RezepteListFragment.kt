package com.example.app_vertiefung

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_vertiefung.services.RezeptService
import kotlinx.android.synthetic.main.fragment_rezepte_list.*
import models.SlimRezeptModel

class RezepteListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate Fragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        println("onCreateView Fragment")
        return inflater.inflate(R.layout.fragment_rezepte_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_rezepte_list_back).setOnClickListener {
            findNavController().navigate(R.id.action_rezepteListFragment_to_startseiteFragment)
        }
        println("onViewCreated Fragment")

        val rezeptService = RezeptService()
        val rezepteList: MutableList<SlimRezeptModel> = rezeptService.getAllSlimRezepte()

        rezepte_list_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = RezepteListAdapter(rezepteList)

            if (rezepteList.isEmpty())
            {
                view.findViewById<RecyclerView>(R.id.rezepte_list_recycler_view).visibility = View.GONE
                view.findViewById<TextView>(R.id.empty_view).visibility = View.VISIBLE
            } else {
                view.findViewById<RecyclerView>(R.id.rezepte_list_recycler_view).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.empty_view).visibility = View.GONE
            }
        }



    }

}
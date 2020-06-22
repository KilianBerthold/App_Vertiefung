package com.example.app_vertiefung

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

/**
Die Startseite, mit Links zu den anderen Pages
 */
class StartseiteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_startseite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_tutorial).setOnClickListener {
            findNavController().navigate(R.id.action_startseiteFragment_to_FirstFragment)
        }

        view.findViewById<Button>(R.id.button_rezepteList).setOnClickListener {
            findNavController().navigate(R.id.action_startseiteFragment_to_rezepteListFragment)
            //startActivity(Intent(activity, RezepteListActivity::class.java))
        }

    }
}
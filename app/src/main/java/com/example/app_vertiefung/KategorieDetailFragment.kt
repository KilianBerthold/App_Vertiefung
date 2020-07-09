package com.example.app_vertiefung

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.app_vertiefung.services.KategorieService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import models.KategorieModel

class KategorieDetailFragment : Fragment(), CoroutineScope by MainScope() {
    private lateinit var kategorie: KategorieModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_kategorie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kategorie = arguments?.get("kategorie") as KategorieModel
        view.findViewById<TextView>(R.id.textView_kategorie_detail_name).text = kategorie.name
        view.findViewById<TextView>(R.id.textView_kategorie_detail_beschreibung).text = kategorie.beschreibung

        view.findViewById<Button>(R.id.button_kategorie_detail_back).setOnClickListener {
            findNavController().navigate(R.id.action_kategorieDetailFragment_to_kategorieListFragment)
        }

        view.findViewById<Button>(R.id.button_kategorie_detail_edit).setOnClickListener {
            val bundle = bundleOf("kategorie" to kategorie)
            findNavController().navigate(R.id.action_kategorieDetailFragment_to_kategorieEditFragment, bundle)
        }

        view.findViewById<Button>(R.id.button_kategorie_detail_delete).setOnClickListener {
            launch {
                val kategorieService = KategorieService()
                view.findViewById<Button>(R.id.button_kategorie_detail_back).isEnabled = false
                view.findViewById<Button>(R.id.button_kategorie_detail_edit).isEnabled = false
                view.findViewById<Button>(R.id.button_kategorie_detail_delete).isEnabled = false
                kategorieService.deleteKategorie(kategorie.id, kategorie.name)
                kategorieService.cleanUpDeletedRezeptKategorien(kategorie.name)
                findNavController().navigate(R.id.action_kategorieDetailFragment_to_kategorieListFragment)
            }
        }
    }

    companion object
}
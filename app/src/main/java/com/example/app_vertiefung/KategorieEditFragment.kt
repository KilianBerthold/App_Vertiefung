package com.example.app_vertiefung

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.app_vertiefung.services.KategorieService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import models.KategorieModel
import org.w3c.dom.Text

class KategorieEditFragment : Fragment(), CoroutineScope by MainScope() {
    private lateinit var kategorie: KategorieModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_kategorie_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kategorie = arguments?.get("kategorie") as KategorieModel
        view.findViewById<TextView>(R.id.textView_kategorie_edit_name).text = kategorie.name
        view.findViewById<TextView>(R.id.textView_kategorie_edit_beschreibung).text = kategorie.beschreibung

        if (kategorie == KategorieModel()) {
            //Neue Kategorie erstellen
            view.findViewById<TextView>(R.id.textView_kategorie_edit_header).text = getString(R.string.new_kategorie)

            view.findViewById<Button>(R.id.button_kategorie_edit_back).setOnClickListener {
                //Zurück zur Liste
                findNavController().navigate(R.id.action_kategorieEditFragment_to_kategorieListFragment)
            }

            view.findViewById<Button>(R.id.button_kategorie_edit_save).setOnClickListener {
                //Neues KategorieModel aus den veränderten Daten generieren
                launch {
                    val newKategorieModel = getNewKategorieModel(kategorie, view)
                    if (newKategorieModel.name != "") {
                        view.findViewById<Button>(R.id.button_kategorie_edit_back).isEnabled = false
                        view.findViewById<Button>(R.id.button_kategorie_edit_save).isEnabled = false
                        val kategorieService = KategorieService()
                        kategorieService.insertKategorie(newKategorieModel)
                        findNavController().navigate(R.id.action_kategorieEditFragment_to_kategorieListFragment)
                    } else {
                        showNoKategorieNameToast()
                    }
                }
            }
        } else {
            //Bestehende Kategorie verändern
            view.findViewById<Button>(R.id.button_kategorie_edit_back).setOnClickListener {
                //Unverändertes KategorieModel an Details zurückgeben
                val bundle = bundleOf("kategorie" to kategorie)
                findNavController().navigate(R.id.action_kategorieEditFragment_to_kategorieDetailFragment, bundle)
            }

            view.findViewById<Button>(R.id.button_kategorie_edit_save).setOnClickListener {
                //Neues KategorieModel aus den veränderten Daten generieren
                launch {
                    val newKategorieModel = getNewKategorieModel(kategorie, view)
                    if (newKategorieModel.name != "") {
                        view.findViewById<Button>(R.id.button_kategorie_edit_back).isEnabled = false
                        view.findViewById<Button>(R.id.button_kategorie_edit_save).isEnabled = false
                        val kategorieService = KategorieService()
                        kategorieService.updateKategorie(newKategorieModel)
                        kategorieService.updateRezeptKategorien(newKategorieModel.name, kategorie.name)
                        val bundle = bundleOf("kategorie" to newKategorieModel)
                        findNavController().navigate(R.id.action_kategorieEditFragment_to_kategorieDetailFragment, bundle)
                    } else {
                        showNoKategorieNameToast()
                    }
                }
            }
        }

    }

    private fun getNewKategorieModel(kategorie: KategorieModel, view: View): KategorieModel {
        return KategorieModel(
            id = kategorie.id,
            beschreibung = if (kategorie.beschreibung != view.findViewById<TextView>(R.id.textView_kategorie_edit_beschreibung).text)
                view.findViewById<TextView>(R.id.textView_kategorie_edit_beschreibung).text.toString() else kategorie.beschreibung,
            name = if (kategorie.name != view.findViewById<TextView>(R.id.textView_kategorie_edit_name).text)
                view.findViewById<TextView>(R.id.textView_kategorie_edit_name).text.toString() else kategorie.name
        )
    }

    private fun showNoKategorieNameToast() {
        val noKategorieNameToast = Toast.makeText(context, resources.getString(R.string.toast_no_kategorie_name), Toast.LENGTH_SHORT)
        noKategorieNameToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        noKategorieNameToast.show()
    }

    companion object
}
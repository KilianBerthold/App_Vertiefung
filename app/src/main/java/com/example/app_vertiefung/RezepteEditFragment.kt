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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_vertiefung.models.RezeptModel
import com.example.app_vertiefung.services.KategorieService
import com.example.app_vertiefung.services.RezeptService
import kotlinx.android.synthetic.main.fragment_rezepte_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class RezepteEditFragment : Fragment(), CoroutineScope by MainScope(),
    RezepteEditSchritteAdapter.RezeptEditSchritteAdapterOnClick,
    RezepteEditKategorieAdapter.RezeptEditKategorieAdapterOnClick,
    RezepteEditZutatenAdapter.RezeptEditZutatenAdapterOnClick {
    private var modus: String = ""
    private var kategorieNamen: List<String> = emptyList()
    private lateinit var rezept: RezeptModel
    private var tempListKategorien: MutableList<String> = emptyList<String>().toMutableList()
    private var tempListSchritte: MutableList<String> = emptyList<String>().toMutableList()
    private var tempListZutaten: MutableList<Pair<String, String>> = emptyList<Pair<String, String>>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rezepte_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val that = this

        rezept = arguments?.get("rezept") as RezeptModel
        modus = arguments?.getString("modus").toString()
        tempListKategorien = rezept.kategorien
        tempListSchritte = rezept.schritte
        tempListZutaten = mutableMapToMutableListOfPairs(rezept.zutaten)

        view.findViewById<TextView>(R.id.textView_rezepte_edit_name).text = rezept.name
        view.findViewById<TextView>(R.id.textView_rezepte_edit_kalorien).text = rezept.kalorien.toString()

        recyclerView_rezepte_edit_schritte.layoutManager = LinearLayoutManager(activity)
        recyclerView_rezepte_edit_schritte.adapter = RezepteEditSchritteAdapter(tempListSchritte, this)

        recyclerView_rezepte_edit_zutaten.layoutManager = LinearLayoutManager(activity)
        recyclerView_rezepte_edit_zutaten.adapter = RezepteEditZutatenAdapter(tempListZutaten, this)

        launch {
            val kategorieService = KategorieService()
            that.kategorieNamen = kategorieService.getAllKategorieNames()

            recyclerView_rezepte_edit_kategorie.layoutManager = LinearLayoutManager(activity)
            recyclerView_rezepte_edit_kategorie.adapter = RezepteEditKategorieAdapter(tempListKategorien, that.kategorieNamen, that, requireContext())
        }

        if (rezept.id == "") {
            //Neues Rezept erstellen
            view.findViewById<TextView>(R.id.textView_rezepte_edit_header).text = getString(R.string.new_rezept)

            view.findViewById<Button>(R.id.button_rezepte_edit_back).setOnClickListener {
                findNavController().navigate(R.id.action_rezepteEditFragment_to_rezepteListFragment)
            }

            view.findViewById<Button>(R.id.button_rezepte_edit_save).setOnClickListener {
                launch {
                    view.clearFocus()

                    val insertRezeptService = RezeptService()

                    if (!checkIfZutatenHasDuplicates(tempListZutaten)) {
                        view.findViewById<Button>(R.id.button_rezepte_edit_save).isEnabled = false
                        view.findViewById<Button>(R.id.button_rezepte_edit_back).isEnabled = false
                        insertRezeptService.insertRezept(getNewRezeptModel(rezept, view))
                        findNavController().navigate(R.id.action_rezepteEditFragment_to_rezepteListFragment)
                    } else {
                        showDuplicateZutatenToast()
                    }
                }
            }

        } else {
            //Bestehendes Rezept verändern
            view.findViewById<Button>(R.id.button_rezepte_edit_back).setOnClickListener {
                val bundle = bundleOf("rezept" to rezept, "modus" to modus, "id" to rezept.id)
                findNavController().navigate(R.id.action_rezepteEditFragment_to_rezepteDetailFragment, bundle)
            }

            view.findViewById<Button>(R.id.button_rezepte_edit_save).setOnClickListener {
                launch {
                    view.clearFocus()

                    val updateRezeptService = RezeptService()

                    if (!checkIfZutatenHasDuplicates(tempListZutaten)) {
                        view.findViewById<Button>(R.id.button_rezepte_edit_save).isEnabled = false
                        view.findViewById<Button>(R.id.button_rezepte_edit_back).isEnabled = false
                        val updatedRezept = getNewRezeptModel(rezept, view)
                        updateRezeptService.updateRezept(rezept.id, updatedRezept)
                        val bundle = bundleOf("rezept" to updatedRezept, "modus" to modus, "id" to rezept.id)
                        findNavController().navigate(R.id.action_rezepteEditFragment_to_rezepteDetailFragment, bundle)
                    } else {
                        showDuplicateZutatenToast()
                    }
                }
            }
        }

        view.findViewById<Button>(R.id.button_rezepte_edit_schritte_add).setOnClickListener {
            tempListSchritte.add("")
            recyclerView_rezepte_edit_schritte.adapter = RezepteEditSchritteAdapter(tempListSchritte, this)
        }

        view.findViewById<Button>(R.id.button_rezepte_edit_kategorie_add).setOnClickListener {
            tempListKategorien.add("")
            recyclerView_rezepte_edit_kategorie.adapter = RezepteEditKategorieAdapter(tempListKategorien, this.kategorieNamen, this, requireContext())
        }

        view.findViewById<Button>(R.id.button_rezepte_edit_zutaten_add).setOnClickListener {
            tempListZutaten.add(Pair("", ""))
            recyclerView_rezepte_edit_zutaten.adapter = RezepteEditZutatenAdapter(tempListZutaten, this)
        }
    }

    override fun removeRezeptKategorie(index: Int) {
        tempListKategorien.removeAt(index)
        recyclerView_rezepte_edit_kategorie.adapter = RezepteEditKategorieAdapter(tempListKategorien, this.kategorieNamen, this, requireContext())
    }

    override fun updateRezeptKategorie(index: Int, value: String) {
        if (tempListKategorien[index] != value) {
            tempListKategorien[index] = value
            recyclerView_rezepte_edit_kategorie.adapter = RezepteEditKategorieAdapter(tempListKategorien, this.kategorieNamen, this, requireContext())
        }
    }

    override fun removeRezeptSchritt(index: Int) {
        tempListSchritte.removeAt(index)
        recyclerView_rezepte_edit_schritte.adapter = RezepteEditSchritteAdapter(tempListSchritte, this)
    }

    override fun updateRezeptSchritt(index: Int, value: String) {
        tempListSchritte[index] = value
    }

    override fun removeRezeptZutat(index: Int) {
        tempListZutaten.removeAt(index)
        recyclerView_rezepte_edit_zutaten.adapter = RezepteEditZutatenAdapter(tempListZutaten, this)
    }

    override fun updateRezeptZutat(index: Int, value: Pair<String, String>) {
        tempListZutaten[index] = value
    }

    private fun mutableMapToMutableListOfPairs(mutableMap: MutableMap<String, String>): MutableList<Pair<String, String>> {
        val list: MutableList<Pair<String, String>> = emptyList<Pair<String, String>>().toMutableList()
        for (mapEntry in mutableMap) {
            list.add(Pair(mapEntry.key, mapEntry.value))
        }
        return list
    }

    private fun mutableListOfPairsToMutableMap(mutableList: MutableList<Pair<String, String>>): MutableMap<String, String> {
        val map: MutableMap<String, String> = emptyMap<String, String>().toMutableMap()
        for (listEntry in mutableList) {
            map[listEntry.first] = listEntry.second
        }
        return map
    }

    private fun getNewRezeptModel(rezept: RezeptModel, view: View): RezeptModel {
        try {
            return RezeptModel(
                rezeptID = rezept.id,
                rezeptName = if (rezept.name != view.findViewById<TextView>(R.id.textView_rezepte_edit_name).text)
                    view.findViewById<TextView>(R.id.textView_rezepte_edit_name).text.toString() else rezept.name,
                rezeptKategorien = tempListKategorien,
                rezeptKalorien = if (rezept.kalorien != view.findViewById<TextView>(R.id.textView_rezepte_edit_kalorien).text.toString().toLong())
                    view.findViewById<TextView>(R.id.textView_rezepte_edit_kalorien).text.toString().toLong() else rezept.kalorien,
                rezeptAnzahlGekocht = rezept.anzahlGekocht,
                rezeptZutaten = mutableListOfPairsToMutableMap(tempListZutaten),
                rezeptSchritte = tempListSchritte,
                rezeptZuletztGekocht = rezept.zuletztGekochtAm
            )
        } catch (e: Exception) {
            println(e.message)
            println(e.stackTrace)
            return RezeptModel()
        }
    }

    private fun checkIfZutatenHasDuplicates(list: MutableList<Pair<String, String>>): Boolean {
        println(list.groupingBy { it.first }.eachCount())
        return list.groupingBy { it.first }.eachCount().any { it.value > 1 }
    }

    private fun showDuplicateZutatenToast() {
        val duplicateZutatenToast = Toast.makeText(context, resources.getString(R.string.toast_duplicate_zutaten), Toast.LENGTH_SHORT)
        duplicateZutatenToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        duplicateZutatenToast.show()
    }

    companion object
}
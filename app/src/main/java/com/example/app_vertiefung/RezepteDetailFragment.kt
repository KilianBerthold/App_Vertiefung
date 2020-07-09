package com.example.app_vertiefung

import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.app_vertiefung.models.AnzeigeRezeptModel
import com.example.app_vertiefung.models.RezeptModel
import com.example.app_vertiefung.services.KategorieService
import com.example.app_vertiefung.services.RezeptService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class RezepteDetailFragment : Fragment(), CoroutineScope by MainScope() {
    private var id: String = ""
    private var modus: String = ""
    private lateinit var rezept: RezeptModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rezepte_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modus = arguments?.getString("modus").toString()

        if (modus == "List") {
            id = arguments?.getString("id").toString()
            val rezeptService = RezeptService()
            launch {
                rezept = rezeptService.getRezept(id)
                fillTextViews(view, rezept)
            }
            view.findViewById<Button>(R.id.button_rezepte_detail_back).setOnClickListener {
                findNavController().navigate(R.id.action_rezepteDetailFragment_to_rezepteListFragment)
            }
        } else {
            rezept = arguments?.get("rezept") as RezeptModel
            fillTextViews(view, rezept)
            view.findViewById<Button>(R.id.button_rezepte_detail_back).setOnClickListener {
                findNavController().navigate(R.id.action_rezepteDetailFragment_to_randomRezeptFragment)
            }
        }

        view.findViewById<Button>(R.id.button_rezepte_detail_edit).setOnClickListener {
            val bundle = bundleOf("rezept" to rezept, "modus" to modus)
            findNavController().navigate(R.id.action_rezepteDetailFragment_to_rezepteEditFragment, bundle)
        }

        view.findViewById<Button>(R.id.button_rezepte_detail_delete).setOnClickListener {
            launch {
                val rezeptServiceDelete = RezeptService()
                rezeptServiceDelete.deleteRezept(rezept.id)
                findNavController().navigate(R.id.action_rezepteDetailFragment_to_rezepteListFragment)
            }
        }

        view.findViewById<Button>(R.id.button_rezepte_detail_kochen).setOnClickListener {
            launch {
                val rezeptServiceKochen = RezeptService()
                rezept.anzahlGekocht = rezept.anzahlGekocht + 1
                rezept.zuletztGekochtAm = Date()
                val newValues: Map<String, Any> = mapOf(
                    "AnzahlGekocht" to rezept.anzahlGekocht,
                    "ZuletztGekochtAm" to rezept.zuletztGekochtAm
                )
                val zuletztGekochtAm = "Zuletzt gekocht am " + rezept.zuletztGekochtAm.toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).toString()
                val anzahlGekocht = rezept.anzahlGekocht.toString() + " Male gekocht"

                view.findViewById<TextView>(R.id.textView_rezepte_detail_zuletztgekochtam).text = zuletztGekochtAm
                view.findViewById<TextView>(R.id.textView_rezepte_detail_anzahlgekocht).text = anzahlGekocht

                rezeptServiceKochen.jetztKochen(rezept.id, newValues)
            }
        }

    }

    private fun fillTextViews(view: View, rezept: RezeptModel) {
        if (rezept != RezeptModel()) {
            val zutaten = StringBuilder("")
            if (!rezept.zutaten.isNullOrEmpty()) {
                for (zutat in rezept.zutaten) {
                    zutaten.append(zutat.key + ": " + zutat.value + "\n\n")
                }
                zutaten.dropLast(1)
            } else {
                zutaten.append("Keine Zutaten vorhanden")
            }

            val schritte = StringBuilder("")
            if (!rezept.schritte.isNullOrEmpty()) {
                var i = 1
                for (schritt in rezept.schritte) {
                    schritte.append("$i. $schritt\n\n")
                    i++
                }
                schritte.dropLast(1)
            } else {
                schritte.append("Keine Schritte vorhanden.")
            }

            val anzeigeRezept = AnzeigeRezeptModel(
                name = rezept.name,
                kalorien = rezept.kalorien.toString() + " Kalorien",
                zuletztGekochtAm =
                    if (rezept.anzahlGekocht != 0.toLong())
                        "Zuletzt gekocht am " + rezept.zuletztGekochtAm.toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).toString()
                    else "Noch nie gekocht.",
                anzahlGekocht = rezept.anzahlGekocht.toString() + if (rezept.anzahlGekocht < 2) " Mal gekocht" else " Male gekocht",
                zutaten = zutaten.toString(),
                schritte = schritte.toString(),
                kategorien = if (rezept.kategorien.size > 0) rezept.kategorien.joinToString(", ") else "Keine"
            )

            view.findViewById<TextView>(R.id.textView_rezepte_detail_name).text = anzeigeRezept.name
            view.findViewById<TextView>(R.id.textView_rezepte_detail_kategorie).text = anzeigeRezept.kategorien
            view.findViewById<TextView>(R.id.textView_rezepte_detail_kalorien).text = anzeigeRezept.kalorien
            view.findViewById<TextView>(R.id.textView_rezepte_detail_zuletztgekochtam).text = anzeigeRezept.zuletztGekochtAm
            view.findViewById<TextView>(R.id.textView_rezepte_detail_anzahlgekocht).text = anzeigeRezept.anzahlGekocht
            view.findViewById<TextView>(R.id.textView_rezepte_detail_zutaten).text = anzeigeRezept.zutaten
            view.findViewById<TextView>(R.id.textView_rezepte_detail_schritte).text = anzeigeRezept.schritte

            view.findViewById<TextView>(R.id.textView_rezepte_loading).visibility = View.GONE
        }
    }

    companion object
}
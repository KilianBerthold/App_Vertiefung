package com.example.app_vertiefung

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.app_vertiefung.models.SuchKriterienModel
import com.example.app_vertiefung.services.KategorieService
import com.example.app_vertiefung.services.RezeptService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class RandomRezeptFragment : Fragment(), CoroutineScope by MainScope(), AdapterView.OnItemSelectedListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_random_rezept, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_random_rezept_back).setOnClickListener {
            findNavController().navigate(R.id.action_randomRezeptFragment_to_startseiteFragment)
        }

        view.findViewById<Button>(R.id.button_get_random_rezept).visibility = View.GONE
        val spinner: Spinner = view.findViewById(R.id.spinner_random_rezept_kategorie)
        spinner.onItemSelectedListener = this
        val spinner2: Spinner = view.findViewById(R.id.spinner_random_rezept_kalorien)
        spinner2.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.numberRelations,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
        }

        try {
            launch {
                val kategorieService = KategorieService()
                val kategorieNamen = kategorieService.getAllKategorieNames()
                val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kategorieNamen)
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter1
                view.findViewById<Button>(R.id.button_get_random_rezept).visibility = View.VISIBLE
            }
        } catch (err: Exception) {
            println(err.message)
            println(err.stackTrace)
        }

        view.findViewById<Button>(R.id.button_get_random_rezept).setOnClickListener {
            val suchKriterien = SuchKriterienModel(
                kategorie = spinner.selectedItem.toString(),
                kalorien = Pair(
                    spinner2.selectedItem.toString(),
                    if (view.findViewById<EditText>(R.id.editText_random_rezept_kalorien).text.toString() != "")
                        view.findViewById<EditText>(R.id.editText_random_rezept_kalorien).text.toString().toInt()
                    else 0
                ),
                zutat = view.findViewById<TextView>(R.id.editText_random_rezept_zutat).text.toString()
            )

            launch {
                val rezeptService = RezeptService()
                val rezept = rezeptService.getRandomRezept(suchKriterien)
                if (rezept.id == "") {
                    val noRezeptToast = Toast.makeText(context, resources.getString(R.string.toast_random_rezept), Toast.LENGTH_SHORT)
                    noRezeptToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                    noRezeptToast.show()
                } else {
                    val bundle = bundleOf("rezept" to rezept, "modus" to "Random", "id" to "")
                    findNavController().navigate(R.id.action_randomRezeptFragment_to_rezepteDetailFragment, bundle)
                }
            }


        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    companion object
}
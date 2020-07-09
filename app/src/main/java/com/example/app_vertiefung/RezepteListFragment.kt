package com.example.app_vertiefung

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_vertiefung.models.RezeptModel
import com.example.app_vertiefung.services.RezeptService
import kotlinx.android.synthetic.main.fragment_rezepte_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import com.example.app_vertiefung.models.SlimRezeptModel
import models.KategorieModel
import org.w3c.dom.Text

class RezepteListFragment : Fragment(), CoroutineScope by MainScope(),
    RezepteListAdapter.RezeptAdapterOnClick {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rezepte_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val that = this

        rezepte_list_recycler_view.apply {
            adapter = RezepteListAdapter(emptyList<SlimRezeptModel>().toMutableList(), that)
            launch {
                val rezeptService = RezeptService()
                val rezepteList: MutableList<SlimRezeptModel> = rezeptService.getAllSlimRezepte()
                layoutManager = LinearLayoutManager(activity)
                adapter = RezepteListAdapter(rezepteList, that)

                if (rezepteList.isEmpty()) {
                    view.findViewById<RecyclerView>(R.id.rezepte_list_recycler_view).visibility =
                        View.GONE
                    view.findViewById<TextView>(R.id.rezepte_list_empty_view).apply {
                        visibility = View.VISIBLE
                        text = getString(R.string.rezepte_list_no_data_available)
                    }
                } else {
                    view.findViewById<RecyclerView>(R.id.rezepte_list_recycler_view).visibility =
                        View.VISIBLE
                    view.findViewById<TextView>(R.id.rezepte_list_empty_view).visibility = View.GONE
                }
            }
        }

        view.findViewById<Button>(R.id.button_rezepte_list_back).setOnClickListener {
            findNavController().navigate(R.id.action_rezepteListFragment_to_startseiteFragment)
        }

        view.findViewById<Button>(R.id.button_rezepte_list_new).setOnClickListener {

            val bundle = bundleOf("rezept" to RezeptModel())
            findNavController().navigate(R.id.action_rezepteListFragment_to_rezepteEditFragment, bundle)
        }

        view.findViewById<ImageButton>(R.id.button_rezepte_list_sync).setOnClickListener {
            refreshRezepteList()
        }

    }

    private fun refreshRezepteList() {
        val that = this
        launch {
            val rezeptService = RezeptService()

            view?.findViewById<RecyclerView>(R.id.rezepte_list_recycler_view)?.visibility =
                View.GONE
            view?.findViewById<TextView>(R.id.rezepte_list_empty_view)?.apply {
                visibility = View.VISIBLE
                text = getString(R.string.loading)
            }

            val rezepteList: MutableList<SlimRezeptModel> = rezeptService.getAllSlimRezepte()

            if (rezepteList.isEmpty()) {
                view?.findViewById<TextView>(R.id.rezepte_list_empty_view)?.apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.rezepte_list_no_data_available)
                }
            } else {
                view?.findViewById<RecyclerView>(R.id.rezepte_list_recycler_view)?.visibility =
                    View.VISIBLE
                view?.findViewById<TextView>(R.id.rezepte_list_empty_view)?.visibility = View.GONE
            }

            rezepte_list_recycler_view.adapter = RezepteListAdapter(rezepteList, that)
        }
    }

    override fun openRezeptDetailView(id: String) {
        val bundle = bundleOf("id" to id, "modus" to "List", "rezept" to RezeptModel())
        findNavController().navigate(R.id.action_rezepteListFragment_to_rezepteDetailFragment, bundle)
    }

    companion object

}
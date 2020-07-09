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
import com.example.app_vertiefung.services.KategorieService
import kotlinx.android.synthetic.main.fragment_kategorie_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import models.KategorieModel
import java.io.Serializable

class KategorieListFragment : Fragment(), CoroutineScope by MainScope(),
    KategorieListAdapter.KategorieAdapterOnClick {

    private var kategorieList = emptyList<KategorieModel>().toMutableList()

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
        return inflater.inflate(R.layout.fragment_kategorie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val that = this

        kategorie_list_recycler_view.apply {
            if (kategorieList.isEmpty())
            {
                launch {
                    val kategorieService = KategorieService()
                    kategorieList = kategorieService.getAllKategorien()

                    layoutManager = LinearLayoutManager(activity)
                    adapter = KategorieListAdapter(kategorieList, that)

                    if (kategorieList.isEmpty())
                    {
                        view.findViewById<RecyclerView>(R.id.kategorie_list_recycler_view).visibility = View.GONE
                        view.findViewById<TextView>(R.id.kategorie_list_empty_view).apply {
                            visibility = View.VISIBLE
                            text = getString(R.string.kategorie_list_no_data_available)
                        }
                    } else {
                        view.findViewById<RecyclerView>(R.id.kategorie_list_recycler_view).visibility = View.VISIBLE
                        view.findViewById<TextView>(R.id.kategorie_list_empty_view).visibility = View.GONE
                    }
                }
            }
            else {
                layoutManager = LinearLayoutManager(activity)
                adapter = KategorieListAdapter(kategorieList, that)
            }

        }

        view.findViewById<Button>(R.id.button_kategorie_list_back).setOnClickListener {
            findNavController().navigate(R.id.action_kategorieListFragment_to_startseiteFragment)
        }

        view.findViewById<Button>(R.id.button_kategorie_list_new).setOnClickListener {
            val bundle = bundleOf("kategorie" to KategorieModel())
            findNavController().navigate(R.id.action_kategorieListFragment_to_kategorieEditFragment, bundle)
        }

        view.findViewById<ImageButton>(R.id.button_kategorie_list_sync).setOnClickListener {
            refreshKategorieList(savedInstanceState)
        }
    }

    private fun refreshKategorieList(savedInstanceState: Bundle?) {
        launch {
            val kategorieService = KategorieService()

            view?.findViewById<RecyclerView>(R.id.kategorie_list_recycler_view)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.kategorie_list_empty_view)?.apply {
                visibility = View.VISIBLE
                text = getString(R.string.loading)
            }
            val kategorienList: MutableList<KategorieModel> = kategorieService.getAllKategorien()

            if (kategorieList.isEmpty())
            {
                view?.findViewById<TextView>(R.id.kategorie_list_empty_view)?.apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.kategorie_list_no_data_available)
                }
            } else {
                view?.findViewById<RecyclerView>(R.id.kategorie_list_recycler_view)?.visibility = View.VISIBLE
                view?.findViewById<TextView>(R.id.kategorie_list_empty_view)?.visibility = View.GONE
                kategorie_list_recycler_view.adapter = KategorieListAdapter(kategorienList, KategorieListFragment())
                savedInstanceState?.putSerializable("kategorieList", kategorieList as Serializable?)
            }
        }
    }

    override fun openKategorieDetailView(kategorie: KategorieModel) {
        val bundle = bundleOf("kategorie" to kategorie)
        findNavController().navigate(R.id.action_kategorieListFragment_to_kategorieDetailFragment, bundle)
    }

    companion object

}
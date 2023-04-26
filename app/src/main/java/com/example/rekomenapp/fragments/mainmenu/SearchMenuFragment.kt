package com.example.rekomenapp.fragments.mainmenu

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rekomenapp.R
import com.example.rekomenapp.adapters.SearchAdapter
import com.example.rekomenapp.models.ReviewModel
import com.google.firebase.database.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchMenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var dbRef: DatabaseReference
    private lateinit var searchRv: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var dataList: ArrayList<ReviewModel>
    private var eventListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewOfLayout = inflater.inflate(R.layout.fragment_menu_search, container, false)

        dbRef = FirebaseDatabase.getInstance().getReference("Review")

        dataList = ArrayList()
        searchRv = viewOfLayout.findViewById(R.id.searchRv)
        searchRv.layoutManager = LinearLayoutManager(activity)
        searchRv.setHasFixedSize(true)
        adapter = SearchAdapter(activity as Context, dataList)
        searchRv.adapter = adapter

        adapter.searchDataList(emptyList())

        eventListener = dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(ReviewModel::class.java)
                    if (dataClass != null) {
                        dataList.add(dataClass)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        val searchView = viewOfLayout.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }

        })

        return viewOfLayout
    }

    fun searchList(text: String) {
        val searchList = mutableListOf<ReviewModel>()
        if (text.isNotEmpty()) {
            for (dataClass in dataList) {
                if (dataClass.reviewJudul?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true) {
                    searchList.add(dataClass)
                }
                adapter.searchDataList(searchList)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
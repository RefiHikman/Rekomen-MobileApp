package com.example.rekomenapp.fragments.categorymenu

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.rekomenapp.R
import com.example.rekomenapp.activities.DetailActivity
import com.example.rekomenapp.activities.ReadActivity
import com.example.rekomenapp.models.ReviewModel
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryNongkrongFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryNongkrongFragment : Fragment() {
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
    private lateinit var popularCard: CardView
    private lateinit var cardImg1: ImageView
    private lateinit var cardSubtitle1: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewOfLayout = inflater.inflate(R.layout.fragment_category_nongkrong, container, false)

        // FIREBASE REFERENCE
        dbRef = FirebaseDatabase.getInstance().getReference("Review")

        val seeAll4 = viewOfLayout.findViewById<TextView>(R.id.seeAll4)
        val gridItem1 = viewOfLayout.findViewById<RelativeLayout>(R.id.gridItem1)
        val gridItem2 = viewOfLayout.findViewById<RelativeLayout>(R.id.gridItem2)
        val gridItem3 = viewOfLayout.findViewById<RelativeLayout>(R.id.gridItem3)
        val gridItem4 = viewOfLayout.findViewById<RelativeLayout>(R.id.gridItem4)
        val gridItem5 = viewOfLayout.findViewById<RelativeLayout>(R.id.gridItem5)
        val gridItem6 = viewOfLayout.findViewById<RelativeLayout>(R.id.gridItem6)
        val dllCard = viewOfLayout.findViewById<RelativeLayout>(R.id.dllCard)

        seeAll4.setOnClickListener{
            val intent = Intent(activity, ReadActivity::class.java)

            intent.putExtra("category", "Nongkrong")
            intent.putExtra("subcategory", "See All")

            startActivity(intent)
        }
        gridItem1.setOnClickListener {
            val intent = Intent(activity, ReadActivity::class.java)

            intent.putExtra("category", "Nongkrong")
            intent.putExtra("subcategory", "Indoor")

            startActivity(intent)
        }
        gridItem2.setOnClickListener {
            val intent = Intent(activity, ReadActivity::class.java)

            intent.putExtra("category", "Nongkrong")
            intent.putExtra("subcategory", "Outdoor")

            startActivity(intent)
        }
        gridItem3.setOnClickListener {
            val intent = Intent(activity, ReadActivity::class.java)

            intent.putExtra("category", "Nongkrong")
            intent.putExtra("subcategory", "Classic")

            startActivity(intent)
        }
        gridItem4.setOnClickListener {
            val intent = Intent(activity, ReadActivity::class.java)

            intent.putExtra("category", "Nongkrong")
            intent.putExtra("subcategory", "Modern")

            startActivity(intent)
        }
        gridItem5.setOnClickListener {
            val intent = Intent(activity, ReadActivity::class.java)

            intent.putExtra("category", "Nongkrong")
            intent.putExtra("subcategory", "Colorful")

            startActivity(intent)
        }
        gridItem6.setOnClickListener {
            val intent = Intent(activity, ReadActivity::class.java)

            intent.putExtra("category", "Nongkrong")
            intent.putExtra("subcategory", "Alam")

            startActivity(intent)
        }
        dllCard.setOnClickListener {
            val intent = Intent(activity, ReadActivity::class.java)

            intent.putExtra("category", "Nongkrong")
            intent.putExtra("subcategory", "Lainnya")

            startActivity(intent)
        }

        // POPULAR REVIEW VIEWS
        popularCard = viewOfLayout.findViewById(R.id.popularCard)
        cardImg1 = viewOfLayout.findViewById(R.id.cardImg1)
        cardSubtitle1 = viewOfLayout.findViewById(R.id.cardSubtitle1)

        // SWIPE TO REFRESH
        swipeRefreshLayout = viewOfLayout.findViewById(R.id.swipeRefresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.mainOrange)
        swipeRefreshLayout.setOnRefreshListener {
            getRandomReview()
        }

        getRandomReview()
        return viewOfLayout
    }

    private fun getRandomReview() {
        swipeRefreshLayout.isRefreshing = true

        val query = dbRef.orderByChild("reviewCategory").equalTo("Nongkrong")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val count = snapshot.childrenCount.toInt()
                    val randomIndex = (0 until count).random()

                    val randomItemKey = snapshot.children.toList()[randomIndex].key
                    val randomQuery = dbRef.orderByKey().startAt(randomItemKey).limitToFirst(1)

                    randomQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val review = snapshot.children.first().getValue(ReviewModel::class.java)

                            cardSubtitle1.text = review?.reviewDesc

                            // IMAGE VIEW
                            val imgLink = "https://ik.imagekit.io/owdo6w10o/o/images%2Freview%2F${review?.reviewImg1}?alt=media"
                            Glide.with(activity as Context)
                                .load(imgLink)
                                .into(cardImg1)

                            popularCard.setOnClickListener {
                                val intent = Intent(activity as Context, DetailActivity::class.java).apply {
                                    putExtra("selectedReview", review)
                                }
                                startActivity(intent)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.e(ContentValues.TAG, "Error fetching review", error.toException())
                        }
                    })
                }
                swipeRefreshLayout.isRefreshing = false
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching random review", error.toException())
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoryNongkrongFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoryNongkrongFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
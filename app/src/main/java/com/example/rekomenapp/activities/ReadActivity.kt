package com.example.rekomenapp.activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.rekomenapp.R
import com.example.rekomenapp.adapters.ReviewAdapter
import com.example.rekomenapp.models.ReviewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ReadActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var readRv: RecyclerView
    private lateinit var readLoading: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)

        // FIREBASE REFERENCE
        dbRef = FirebaseDatabase.getInstance().getReference("Review")
        firebaseAuth = FirebaseAuth.getInstance()
        currentUserId = firebaseAuth.currentUser!!.uid

        // RECYCLER VIEW
        readRv = findViewById(R.id.readRv)
        readLoading = findViewById(R.id.readLoading)
        readRv.layoutManager = LinearLayoutManager(this)
        readRv.setHasFixedSize(true)

        // SWIPE TO REFRESH
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.mainOrange)
        swipeRefreshLayout.setOnRefreshListener {
            getReviewData()
        }

        // BACK BUTTON
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener{
            finish()
        }

        // TITLE
        val category = intent.getStringExtra("category")
        val subcategory = intent.getStringExtra("subcategory")
        val topTitle = findViewById<TextView>(R.id.topTitleRead)

        topTitle.text = subcategory
        if (subcategory == "See All") {
            topTitle.text = category
        }

        getReviewData()
    }

    private fun getReviewData() {
        swipeRefreshLayout.isRefreshing = true

        readRv.visibility = View.GONE
        readLoading.visibility = View.VISIBLE

        val category = intent.getStringExtra("category")
        val subcategory = intent.getStringExtra("subcategory")

        val query = if (subcategory == "See All") {
            dbRef.orderByChild("reviewCategory").equalTo("$category")
        } else if (subcategory == "Reviewku" && category == "Reviewku") {
            dbRef.orderByChild("userId").equalTo(currentUserId)
        } else {
            dbRef.orderByChild("reviewCategorySort").equalTo("$category-$subcategory")
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val reviewList = mutableListOf<ReviewModel>()
                if (dataSnapshot.exists()) {
                    for (reviewSnapshot in dataSnapshot.children) {
                        val reviewData = reviewSnapshot.getValue(ReviewModel::class.java)
                        reviewList.add(reviewData!!)
                    }
                    val adapter = ReviewAdapter(object : ReviewAdapter.OnItemClickListener {
                        override fun onItemClick(review: ReviewModel) {
                            // Handle the click event here
                            val intent = Intent(this@ReadActivity, DetailActivity::class.java).apply {
                                putExtra("selectedReview", review)
                            }
                            startActivity(intent)
                        }
                    })
                    readRv.adapter = adapter
                    adapter.submitList(reviewList)

                    readRv.visibility = View.VISIBLE
                    readLoading.visibility = View.GONE
                }
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error fetching reviews", error.toException())
            }
        })
    }
}
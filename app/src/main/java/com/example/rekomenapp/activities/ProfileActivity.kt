package com.example.rekomenapp.activities

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.rekomenapp.R
import com.example.rekomenapp.adapters.ProfileAdapter
import com.example.rekomenapp.models.ReviewModel
import com.example.rekomenapp.models.UserModel
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var rV: RecyclerView
    private lateinit var currentUserId: String
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var selectedReview: ReviewModel
    private lateinit var selectedId: String

    private lateinit var reviewku: TextView
    private lateinit var seeAll: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = this.resources.getColor(R.color.mainOrange)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        // FIREBASE REFERENCE
        dbRef = FirebaseDatabase.getInstance().getReference("Review")
        dbRefUser = FirebaseDatabase.getInstance().getReference("Users")

        // FIREBASE AUTH
        firebaseAuth = FirebaseAuth.getInstance()
        currentUserId = firebaseAuth.currentUser!!.uid

        // RECYCLER VIEW
        rV = findViewById(R.id.profileRv)
        rV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rV.setHasFixedSize(true)

        // ADD SNAP TO RECYCLER VIEW
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rV)

        // EDIT BTN
        val editBtn = findViewById<TextView>(R.id.edit)
        editBtn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // BACK BTN
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        // LOGOUT BTN
        val logoutBtn = findViewById<ImageButton>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Yakin ingin logout?")
            builder.setCancelable(false)
            builder.setPositiveButton("Ya") { _, _ ->
                firebaseAuth.signOut()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        // SWIPE TO REFRESH
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.mainOrange)
        swipeRefreshLayout.setOnRefreshListener {
            getReviewData()
            getUserData()
        }

        // SERIALIZABLE
        selectedReview = if (intent.hasExtra("selectedReview")) {
            intent.getSerializableExtra("selectedReview") as ReviewModel
        } else {
            ReviewModel(null)
        }

        // USER ID SERIALIZABLE
        selectedId = if (intent.hasExtra("selectedComment")) {
            intent.getSerializableExtra("selectedComment") as String
        } else if (selectedReview == ReviewModel(null)) {
            currentUserId
        } else {
            selectedReview.userId!!
        }

        // USER CHECK
        if (currentUserId != selectedId) {
            editBtn.alpha = 0f
            editBtn.isClickable = false

            logoutBtn.alpha = 0f
            logoutBtn.isClickable = false
        }

        // SEE ALL BTN
        reviewku = findViewById(R.id.reviewku)
        seeAll = findViewById(R.id.seeAll)
        seeAll.setOnClickListener {
            val intent = Intent(this, ReadActivity::class.java)

            intent.putExtra("category", "Reviewku")
            intent.putExtra("subcategory", "Reviewku")
            intent.putExtra("selectedProfile", selectedId)

            startActivity(intent)
        }

        Log.i(ContentValues.TAG, "currentUserId: $currentUserId")
        Log.i(ContentValues.TAG, "selectedId: $selectedId")

        reviewku.visibility = View.GONE
        seeAll.visibility = View.GONE
        seeAll.isClickable = false

        getReviewData()
        getUserData()
    }

    // CALCULATE AGE FROM DD MMM YYYY
    fun calculateAge(birthdateString: String): Int {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val birthdate = dateFormat.parse(birthdateString)

        val today = Calendar.getInstance().time
        val diff = today.time - birthdate!!.time

        val yearInMillis = 1000L * 60 * 60 * 24 * 365

        return (diff / yearInMillis).toInt()
    }

    // IMAGE ZOOM DIALOG
    private fun imageDialog(link: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.image_dialog)

        val imageDialog = dialog.findViewById<ZoomageView>(R.id.imageDialog)
        Picasso.get().load(link).placeholder(R.color.icon1).into(imageDialog)

        dialog.show()
    }

    // FETCH USER DATA
    private fun getUserData() {
        val nama = findViewById<TextView>(R.id.t2)
        val umur = findViewById<TextView>(R.id.s1)
        val tempat = findViewById<TextView>(R.id.a1)
        val profesi = findViewById<TextView>(R.id.t1)
        val bio = findViewById<TextView>(R.id.b1)
        val image = findViewById<ShapeableImageView>(R.id.g1)

        val query = if (intent.hasExtra("selectedComment")) {
            dbRefUser.child(selectedId)
        } else if (selectedReview == ReviewModel(null)) {
            dbRefUser.child(currentUserId)
        } else {
            dbRefUser.child(selectedId)
        }

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserModel::class.java)
                Log.d("getUserData", "user: $user")

                nama.text = if (user?.userNama != "") {user?.userNama} else {"---"}
                umur.text = if (user?.userTgl != "") {calculateAge("${user?.userTgl}").toString() + " Tahun"} else {"---"}
                tempat.text = if (user?.userLokasi != "") {user?.userLokasi} else {"---"}
                profesi.text = if (user?.userProfesi != "") {user?.userProfesi} else {"---"}
                bio.text = if (user?.userBio != "") {user?.userBio} else {"---"}

                val imgLink = "https://ik.imagekit.io/owdo6w10o/o/images%2Fprofile%2F${user?.userImage}?alt=media"
                Glide.with(this@ProfileActivity).load(imgLink).into(image)

                image.setOnClickListener {
                    imageDialog(imgLink)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching user", databaseError.toException())
            }
        }
        query.addListenerForSingleValueEvent(userListener)
    }

    // FETCH REVIEWS
    private fun getReviewData() {
        swipeRefreshLayout.isRefreshing = true

        val query = if (intent.hasExtra("selectedComment")) {
            dbRef.orderByChild("userId").equalTo(selectedId)
        } else if (selectedReview == ReviewModel(null)) {
            dbRef.orderByChild("userId").equalTo(currentUserId)
        } else {
            dbRef.orderByChild("userId").equalTo(selectedId)
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val reviewList = mutableListOf<ReviewModel>()
                if (dataSnapshot.exists()) {
                    for (reviewSnapshot in dataSnapshot.children) {
                        val reviewData = reviewSnapshot.getValue(ReviewModel::class.java)
                        reviewList.add(reviewData!!)
                    }
                    val adapter = ProfileAdapter(object : ProfileAdapter.OnItemClickListener {
                        override fun onItemClick(review: ReviewModel) {
                            // Handle the click event here
                            val intent = Intent(this@ProfileActivity, DetailActivity::class.java).apply {
                                putExtra("selectedReview", review)
                            }
                            startActivity(intent)
                        }
                    })
                    rV.adapter = adapter
                    adapter.submitList(reviewList)

                    val dotsIndicator = findViewById<ScrollingPagerIndicator>(R.id.dotsIndicator)
                    dotsIndicator.attachToRecyclerView(rV)

                    reviewku.visibility = View.VISIBLE
                    seeAll.visibility = View.VISIBLE
                    seeAll.isClickable = true
                }
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching reviews", error.toException())
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }
}
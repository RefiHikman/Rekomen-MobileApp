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
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowInsetsControllerCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.rekomenapp.R
import com.example.rekomenapp.models.ReviewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRefComment: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storageRef: FirebaseStorage
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var imgLink1: String
    private lateinit var imgLink2: String
    private lateinit var imgLink3: String
    private lateinit var imgLink4: String

    private lateinit var judul: TextView
    private lateinit var detail: TextView
    private lateinit var rating: TextView
    private lateinit var harga: TextView
    private lateinit var gambar: ImageView
    private lateinit var gd1: ShapeableImageView
    private lateinit var gd2: ShapeableImageView
    private lateinit var gd3: ShapeableImageView

    private lateinit var selectedReview: ReviewModel
    private lateinit var selectedReviewNew: ReviewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = this.resources.getColor(R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        // FIREBASE REFERENCE
        dbRef = FirebaseDatabase.getInstance().getReference("Review")
        dbRefComment = FirebaseDatabase.getInstance().getReference("Comment")
        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()

        // SWIPE TO REFRESH
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.mainOrange)
        swipeRefreshLayout.setOnRefreshListener {
            getReviewData()
        }

        // BACK BTN
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener{
            finish()
        }

        // SERIALIZABLE
        selectedReview = intent.getSerializableExtra("selectedReview") as ReviewModel

        // COMMENT BTN
        val komentar = findViewById<Button>(R.id.komentar)
        komentar.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java).apply {
                putExtra("selectedReview", selectedReviewNew)
            }
            startActivity(intent)
        }

        getReviewData()
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

    // DELETE FIREBASE STORAGE ENTIRE FOLDER
    private fun deleteFolder(storage: FirebaseStorage, folderRef: StorageReference) {
        // List all items (files and subfolders) within the folder
        folderRef.listAll().addOnSuccessListener { result ->
            val items = result.items
            val prefixes = result.prefixes

            // Delete all files within the folder
            for (item in items) {
                item.delete().addOnSuccessListener {
                    // File deleted successfully
                    println("Deleted file: ${item.name}")
                }.addOnFailureListener { exception ->
                    // Error occurred while deleting the file
                    println("Failed to delete file: ${item.name}, Error: ${exception.message}")
                }
            }

            // Recursively delete subfolders
            for (prefix in prefixes) {
                deleteFolder(storage, prefix)
            }

            // Delete the folder itself
            folderRef.delete().addOnSuccessListener {
                // Folder deleted successfully
                println("Deleted folder: ${folderRef.name}")
            }.addOnFailureListener { exception ->
                // Error occurred while deleting the folder
                println("Failed to delete folder: ${folderRef.name}, Error: ${exception.message}")
            }
        }.addOnFailureListener { exception ->
            // Error occurred while listing items within the folder
            println("Failed to list items in folder: ${folderRef.name}, Error: ${exception.message}")
        }
    }

    private fun getReviewData() {
        swipeRefreshLayout.isRefreshing = true

        val query = dbRef.child(selectedReview.reviewId!!)

        // FETCH REVIEW DATA
        val reviewListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val review = dataSnapshot.getValue(ReviewModel::class.java)

                selectedReviewNew = review!!
                swipeRefreshLayout.isRefreshing = false

                // MORE BTN
                val moreBtn = findViewById<ImageView>(R.id.moreBtn)
                moreBtn.setOnClickListener {
                    val dialog = BottomSheetDialog(this@DetailActivity)
                    val view = layoutInflater.inflate(R.layout.bottom_sheet_detail, null)

                    // USER PROFILE
                    val userProfile = view.findViewById<LinearLayout>(R.id.userProfile)
                    userProfile.setOnClickListener {
                        val intent = Intent(this@DetailActivity, ProfileActivity::class.java).apply {
                            putExtra("selectedReview", selectedReviewNew)
                        }
                        startActivity(intent)
                    }

                    // EDIT DATA
                    val editData = view.findViewById<LinearLayout>(R.id.editData)
                    editData.setOnClickListener {
                        val intent = Intent(this@DetailActivity, EditReviewActivity::class.java).apply {
                            putExtra("selectedReview", selectedReviewNew)
                        }
                        startActivity(intent)
                    }

                    // DELETE DATA
                    val deleteData = view.findViewById<LinearLayout>(R.id.deleteData)
                    deleteData.setOnClickListener {
                        val builder = AlertDialog.Builder(this@DetailActivity)
                        builder.setMessage("Yakin ingin menghapus review?")
                        builder.setCancelable(false)
                        builder.setPositiveButton("Ya") { _, _ ->
                            // DATABASE DELETE
                            dbRef.child(selectedReviewNew.reviewId!!).removeValue()

                            // STORAGE DELETE
                            val folderRef = storageRef.reference.child("images/review/${selectedReview.reviewId}")
                            if (selectedReviewNew.reviewImg1 != "" || selectedReviewNew.reviewImg2 != "" ||
                                selectedReviewNew.reviewImg3 != "" || selectedReviewNew.reviewImg4 != "") deleteFolder(storageRef, folderRef)

                            // COMMENT DELETE
                            val query = dbRefComment.orderByChild("reviewId").equalTo(selectedReview.reviewId)
                            query.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (item in snapshot.children) {
                                        item.ref.removeValue()
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Log.e(ContentValues.TAG, "Error fetching reviews", error.toException())
                                }
                            })

                            finish()
                        }
                        builder.setNegativeButton("Tidak") { dialogAlert, _ ->
                            dialogAlert.dismiss()
                        }

                        val dialogAlert = builder.create()
                        dialogAlert.show()
                    }

                    if (firebaseAuth.currentUser?.uid != selectedReview.userId
                        && firebaseAuth.currentUser?.uid != "Mw0NG3TEtCfwmqScuE3N8GY6p1p2") {
                        deleteData.visibility = View.GONE
                        deleteData.isClickable = false

                        editData.visibility = View.GONE
                        deleteData.isClickable = false
                    }

                    dialog.setCancelable(true)
                    dialog.setContentView(view)
                    dialog.show()
                }

                // VIEWS
                judul = findViewById(R.id.judul)
                detail = findViewById(R.id.detail)
                rating = findViewById(R.id.rating)
                harga = findViewById(R.id.harga)
                gambar = findViewById(R.id.gambar)
                gd1 = findViewById(R.id.gd1)
                gd2 = findViewById(R.id.gd2)
                gd3 = findViewById(R.id.gd3)

                // IMG LINK
                imgLink1 = "https://ik.imagekit.io/owdo6w10o/o/images%2Freview%2F${selectedReview.reviewId}%2F${selectedReviewNew.reviewImg1}?alt=media"
                imgLink2 = "https://ik.imagekit.io/owdo6w10o/o/images%2Freview%2F${selectedReview.reviewId}%2F${selectedReviewNew.reviewImg2}?alt=media"
                imgLink3 = "https://ik.imagekit.io/owdo6w10o/o/images%2Freview%2F${selectedReview.reviewId}%2F${selectedReviewNew.reviewImg3}?alt=media"
                imgLink4 = "https://ik.imagekit.io/owdo6w10o/o/images%2Freview%2F${selectedReview.reviewId}%2F${selectedReviewNew.reviewImg4}?alt=media"

                judul.text = selectedReviewNew.reviewJudul
                detail.text = selectedReviewNew.reviewDesc
                rating.text = selectedReviewNew.reviewRating.toString()
                harga.text = if (selectedReviewNew.reviewHarga != "") {"Rp. ${selectedReviewNew.reviewHarga}"} else {""}

                if (selectedReviewNew.reviewImg1 != "") Picasso.get().load(imgLink1).placeholder(R.color.icon1).into(gambar)
                if (selectedReviewNew.reviewImg2 != "") Picasso.get().load(imgLink2).placeholder(R.color.icon1).into(gd1)
                if (selectedReviewNew.reviewImg3 != "") Picasso.get().load(imgLink3).placeholder(R.color.icon1).into(gd2)
                if (selectedReviewNew.reviewImg4 != "") Picasso.get().load(imgLink4).placeholder(R.color.icon1).into(gd3)

                if (selectedReviewNew.reviewImg2 == "") gd1.visibility = View.GONE else {gd1.visibility = View.VISIBLE}
                if (selectedReviewNew.reviewImg3 == "") gd2.visibility = View.GONE else {gd2.visibility = View.VISIBLE}
                if (selectedReviewNew.reviewImg4 == "") gd3.visibility = View.GONE else {gd3.visibility = View.VISIBLE}

                if (selectedReviewNew.reviewImg1 != "") {
                    gambar.setOnClickListener {
                        imageDialog(imgLink1)
                    }
                }
                if (selectedReviewNew.reviewImg2 != "") {
                    gd1.setOnClickListener {
                        imageDialog(imgLink2)
                    }
                }
                if (selectedReviewNew.reviewImg3 != "") {
                    gd2.setOnClickListener {
                        imageDialog(imgLink3)
                    }
                }
                if (selectedReviewNew.reviewImg4 != "") {
                    gd3.setOnClickListener {
                        imageDialog(imgLink4)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching user", error.toException())
            }
        }
        query.addListenerForSingleValueEvent(reviewListener)
    }
}

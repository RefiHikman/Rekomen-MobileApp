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
import com.example.rekomenapp.R
import com.example.rekomenapp.models.ReviewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRefComment: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

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

        // BACK BTN
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener{
            finish()
        }

        // VIEWS
        val judul = findViewById<TextView>(R.id.judul)
        val detail = findViewById<TextView>(R.id.detail)
        val rating = findViewById<TextView>(R.id.rating)
        val harga = findViewById<TextView>(R.id.harga)
        val gambar = findViewById<ImageView>(R.id.gambar)
        val gd1 = findViewById<ShapeableImageView>(R.id.gd1)
        val gd2 = findViewById<ShapeableImageView>(R.id.gd2)
        val gd3 = findViewById<ShapeableImageView>(R.id.gd3)

        // SERIALIZABLE
        val selectedReview = intent.getSerializableExtra("selectedReview") as ReviewModel

        // COMMENT BTN
        val komentar = findViewById<Button>(R.id.komentar)
        komentar.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java).apply {
                putExtra("selectedReview", selectedReview)
            }
            startActivity(intent)
        }

        // IMG LINK
        val imgLink1 = "https://firebasestorage.googleapis.com/v0/b/rekomen-926c7.appspot.com/o/images%2Freview%2F${selectedReview.reviewImg1}?alt=media"
        val imgLink2 = "https://firebasestorage.googleapis.com/v0/b/rekomen-926c7.appspot.com/o/images%2Freview%2F${selectedReview.reviewImg2}?alt=media"
        val imgLink3 = "https://firebasestorage.googleapis.com/v0/b/rekomen-926c7.appspot.com/o/images%2Freview%2F${selectedReview.reviewImg3}?alt=media"
        val imgLink4 = "https://firebasestorage.googleapis.com/v0/b/rekomen-926c7.appspot.com/o/images%2Freview%2F${selectedReview.reviewImg4}?alt=media"

        judul.text = selectedReview.reviewJudul
        detail.text = selectedReview.reviewDesc
        rating.text = selectedReview.reviewRating.toString()
        harga.text = if (selectedReview.reviewHarga != "") {"Rp. ${selectedReview.reviewHarga}"} else {""}

        if (selectedReview.reviewImg1 != "") Picasso.get().load(imgLink1).placeholder(R.color.icon1).into(gambar)
        if (selectedReview.reviewImg2 != "") Picasso.get().load(imgLink2).placeholder(R.color.icon1).into(gd1)
        if (selectedReview.reviewImg3 != "") Picasso.get().load(imgLink3).placeholder(R.color.icon1).into(gd2)
        if (selectedReview.reviewImg4 != "") Picasso.get().load(imgLink4).placeholder(R.color.icon1).into(gd3)

        // STORAGE REF
        val storageReference1 = FirebaseStorage.getInstance().getReference("images/review/${selectedReview.reviewImg1}")
        val storageReference2 = FirebaseStorage.getInstance().getReference("images/review/${selectedReview.reviewImg2}")
        val storageReference3 = FirebaseStorage.getInstance().getReference("images/review/${selectedReview.reviewImg3}")
        val storageReference4 = FirebaseStorage.getInstance().getReference("images/review/${selectedReview.reviewImg4}")

        // MORE BTN
        val moreBtn = findViewById<ImageView>(R.id.moreBtn)
        moreBtn.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_detail, null)

            val deleteData = view.findViewById<LinearLayout>(R.id.deleteData)
            deleteData.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Yakin ingin menghapus review?")
                builder.setCancelable(false)
                builder.setPositiveButton("Ya") { _, _ ->
                    // DATABASE DELETE
                    dbRef.child(selectedReview.reviewId!!).removeValue()

                    // STORAGE DELETE
                    if (selectedReview.reviewImg1 != "") storageReference1.delete()
                    if (selectedReview.reviewImg2 != "") storageReference2.delete()
                    if (selectedReview.reviewImg3 != "") storageReference3.delete()
                    if (selectedReview.reviewImg4 != "") storageReference4.delete()

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

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

        // IMAGE ZOOM DIALOG
        fun imageDialog(link: String) {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.image_dialog)

            val imageDialog = dialog.findViewById<ZoomageView>(R.id.imageDialog)
            Picasso.get().load(link).placeholder(R.color.icon1).into(imageDialog)

            dialog.show()
        }

        if (selectedReview.reviewImg1 != "") {
            gambar.setOnClickListener {
                imageDialog(imgLink1)
            }
        }
        if (selectedReview.reviewImg2 != "") {
            gd1.setOnClickListener {
                imageDialog(imgLink2)
            }
        }
        if (selectedReview.reviewImg3 != "") {
            gd2.setOnClickListener {
                imageDialog(imgLink3)
            }
        }
        if (selectedReview.reviewImg4 != "") {
            gd3.setOnClickListener {
                imageDialog(imgLink4)
            }
        }

        // MORE BTN USER
        if (firebaseAuth.currentUser?.uid != selectedReview.userId
            && firebaseAuth.currentUser?.uid != "Mw0NG3TEtCfwmqScuE3N8GY6p1p2") {
            moreBtn.visibility = View.GONE
            moreBtn.isClickable = false
        }
    }
}
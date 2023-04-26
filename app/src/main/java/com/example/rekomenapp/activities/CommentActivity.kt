package com.example.rekomenapp.activities

import android.app.ProgressDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.rekomenapp.R
import com.example.rekomenapp.adapters.CommentAdapter
import com.example.rekomenapp.models.CommentModel
import com.example.rekomenapp.models.ReviewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class CommentActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var commentRv: RecyclerView
    private lateinit var commentLoading: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var editText: EditText
    private lateinit var selectedReview: ReviewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_komentar)

        selectedReview = intent.getSerializableExtra("selectedReview") as ReviewModel

        // FIREBASE REFERENCE
        dbRef = FirebaseDatabase.getInstance().getReference("Comment")
        firebaseAuth = FirebaseAuth.getInstance()
        currentUserId = firebaseAuth.currentUser!!.uid

        editText = findViewById(R.id.editText)

        commentRv = findViewById(R.id.commentRv)
        commentLoading = findViewById(R.id.commentLoading)
        commentRv.layoutManager = LinearLayoutManager(this)
        commentRv.setHasFixedSize(true)

        // SWIPE TO REFRESH
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.mainOrange)
        swipeRefreshLayout.setOnRefreshListener {
            getComment()
        }

        // BACK BUTTON
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener{
            finish()
        }

        val button = findViewById<ImageView>(R.id.button)
        button.setOnClickListener {
            setComment()
        }

        getComment()
    }

    private fun setComment() {
        // GET DATE
        val formatter = SimpleDateFormat("dd-MM-yyyy_HH:mm", Locale.getDefault())
        val now = Date()
        val date = formatter.format(now)

        val commentText = editText.text.toString()

        val commentId = dbRef.push().key!!
        val comment = CommentModel(commentId, commentText, date, selectedReview.reviewId, currentUserId)

        if (commentText.isNotEmpty()) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Sending comment...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            dbRef.child(commentId).setValue(comment)
                .addOnCompleteListener {
                    Toast.makeText(this, "Comment berhasil dimasukkan", Toast.LENGTH_SHORT).show()
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    editText.text.clear()

                    getComment()
                }.addOnFailureListener { err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun getComment() {
        swipeRefreshLayout.isRefreshing = true

        commentRv.visibility = View.GONE
        commentLoading.visibility = View.VISIBLE

        val query = dbRef.orderByChild("reviewId").equalTo(selectedReview.reviewId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val reviewList = mutableListOf<CommentModel>()
                if (dataSnapshot.exists()) {
                    for (reviewSnapshot in dataSnapshot.children) {
                        val reviewData = reviewSnapshot.getValue(CommentModel::class.java)
                        reviewList.add(reviewData!!)
                    }
                    val adapter = CommentAdapter { comment ->
                        if (currentUserId == comment.userId || currentUserId == "Mw0NG3TEtCfwmqScuE3N8GY6p1p2") {
                            val builder = AlertDialog.Builder(this@CommentActivity)
                            builder.setMessage("Yakin ingin menghapus comment?")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ya") { _, _ ->
                                dbRef.child(comment.commentId!!).removeValue()
                                getComment()
                            }
                            builder.setNegativeButton("Tidak") { dialogAlert, _ ->
                                dialogAlert.dismiss()
                            }

                            val dialogAlert = builder.create()
                            dialogAlert.show()
                        }
                    }
                    commentRv.adapter = adapter
                    adapter.submitList(reviewList)

                    commentRv.visibility = View.VISIBLE
                    commentLoading.visibility = View.GONE
                }
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching reviews", error.toException())
            }
        })
    }
}
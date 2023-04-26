package com.example.rekomenapp.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rekomenapp.R
import com.example.rekomenapp.activities.DetailActivity
import com.example.rekomenapp.models.ReviewModel
import com.google.firebase.database.FirebaseDatabase

class ReviewAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    private val reviews = mutableListOf<ReviewModel>()

    interface OnItemClickListener {
        fun onItemClick(review: ReviewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.read_list_item, parent, false)
        return ReviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    override fun getItemCount() = reviews.size

    fun submitList(newReviews: List<ReviewModel>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val judulView: TextView = itemView.findViewById(R.id.textJudul)
        private val descView: TextView = itemView.findViewById(R.id.textDesc)
        private val hargaView: TextView = itemView.findViewById(R.id.textHarga)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val ratingView: TextView = itemView.findViewById(R.id.textRating)
        private val dateView: TextView = itemView.findViewById(R.id.textDate)
        private val userView: TextView = itemView.findViewById(R.id.textUser)

        private val dbUserRef = FirebaseDatabase.getInstance().getReference("Users")

        override fun onClick(v: View?) {
            val position = adapterPosition
            val selectedReview = reviews[position]
            val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                putExtra("selectedReview", selectedReview)
            }
            itemView.context.startActivity(intent)
        }

//        private lateinit var dataList: List<ReviewModel>
//        fun searchDataList(searchList: List<ReviewModel>) {
//            dataList = searchList
//            notifyDataSetChanged()
//        }

        fun bind(review: ReviewModel) {
            judulView.text = review.reviewJudul
            descView.text = review.reviewDesc
            ratingView.text = review.reviewRating.toString()
            hargaView.text = if (review.reviewHarga != "") {"Rp. ${review.reviewHarga}"} else {""}

            // DATE
            if (review.reviewDate != null) {
                val rDate = review.reviewDate?.split("_")
                dateView.text = rDate!![0]
            }

            // REVIEW IMAGE
            val imgLink = "https://firebasestorage.googleapis.com/v0/b/rekomen-926c7.appspot.com/o/images%2Freview%2F${review.reviewImg1}?alt=media"
            Glide.with(itemView)
                .load(imgLink)
                .into(imageView)

             // USER NAME
            dbUserRef.child(review.userId!!).child("userNama").get().addOnSuccessListener {
                val userName = it.value as String
                userView.text = userName
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

            itemView.setOnClickListener(this)
        }
    }

}

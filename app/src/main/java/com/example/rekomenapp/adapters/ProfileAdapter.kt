package com.example.rekomenapp.adapters

import android.content.Intent
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

class ProfileAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
    private val reviews = mutableListOf<ReviewModel>()

    interface OnItemClickListener {
        fun onItemClick(review: ReviewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.profile_list_item, parent, false)
        return ProfileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    override fun getItemCount() = reviews.size

    fun submitList(newReviews: List<ReviewModel>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val judulView: TextView = itemView.findViewById(R.id.textJudul)
        private val imageView: ImageView = itemView.findViewById(R.id.profileRvImg)
        private val dateView: TextView = itemView.findViewById(R.id.textDate)

        override fun onClick(v: View?) {
            val position = adapterPosition
            val selectedReview = reviews[position]
            val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                putExtra("selectedReview", selectedReview)
            }
            itemView.context.startActivity(intent)
        }

        fun bind(review: ReviewModel) {
            judulView.text = review.reviewJudul

            if (review.reviewDate != null) {
                val rDate = review.reviewDate?.split("_")
                dateView.text = rDate!![0]
            }

            // REVIEW IMAGE
            val imgLink = "https://ik.imagekit.io/owdo6w10o/o/images%2Freview%2F${review.reviewImg1}?alt=media"
            Glide.with(itemView)
                .load(imgLink)
                .into(imageView)

            itemView.setOnClickListener(this)
        }
    }

}

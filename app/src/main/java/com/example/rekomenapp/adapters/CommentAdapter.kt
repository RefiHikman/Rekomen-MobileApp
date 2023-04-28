package com.example.rekomenapp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.rekomenapp.R
import com.example.rekomenapp.activities.ProfileActivity
import com.example.rekomenapp.models.CommentModel
import com.example.rekomenapp.models.ReviewModel
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class CommentAdapter(private val context: Context, private val onItemClickListener: (CommentModel) -> Unit) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    private val comments = mutableListOf<CommentModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_list_item, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount() = comments.size

    fun submitList(newComments: List<CommentModel>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val namaView: TextView = itemView.findViewById(R.id.commentNama)
        private val textView: TextView = itemView.findViewById(R.id.commentText)
        private val dateView: TextView = itemView.findViewById(R.id.commentDate)
        private val imageView: ShapeableImageView = itemView.findViewById(R.id.commentImg)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        private val dbUserRef = FirebaseDatabase.getInstance().getReference("Users")

        init {
            cardView.setOnClickListener {
                val comment = comments[adapterPosition]
                onItemClickListener(comment)
            }

            imageView.setOnClickListener {
                val comment = comments[adapterPosition]
                val intent = Intent(context, ProfileActivity::class.java).apply {
                    putExtra("selectedComment", comment.userId.toString())
                }
                context.startActivity(intent)
            }
        }

        fun bind(comment: CommentModel) {
            textView.text = comment.commentText

            // DATE
            if (comment.commentDate != null) {
                val rDate = comment.commentDate?.split("_")
                dateView.text = "${rDate!![0]} ${rDate[1]}"
            }

            // USER NAME
            dbUserRef.child(comment.userId!!).child("userNama").get().addOnSuccessListener {
                val userName = it.value as String
                namaView.text = userName
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

            // USER IMAGE
            dbUserRef.child(comment.userId!!).child("userImage").get().addOnSuccessListener {
                val userImage = it.value as String
                val imgLink = "https://firebasestorage.googleapis.com/v0/b/rekomen-926c7.appspot.com/o/images%2Fprofile%2F${userImage}?alt=media"

                Picasso.get().load(imgLink).placeholder(R.color.icon1).into(imageView)
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
}
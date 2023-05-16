package com.example.rekomenapp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rekomenapp.R
import com.example.rekomenapp.activities.DetailActivity
import com.example.rekomenapp.models.ReviewModel
import com.google.firebase.database.FirebaseDatabase

class SearchAdapter(private val context: Context, private var dataList: List<ReviewModel>) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.read_list_item, parent, false)
        return MyViewHolder(view)
    }

    private val dbUserRef = FirebaseDatabase.getInstance().getReference("Users")

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val selectedReview = dataList[position]

        // IMAGE
        val imgLink = "https://ik.imagekit.io/owdo6w10o/o/images%2Freview%2F${selectedReview.reviewId}%2F${selectedReview.reviewImg1}?alt=media"
        Glide.with(context).load(imgLink)
            .into(holder.imageView)

        holder.judulView.text = dataList[position].reviewJudul
        holder.descView.text = dataList[position].reviewDesc
        holder.hargaView.text = if (dataList[position].reviewHarga != "") {"Rp. ${selectedReview.reviewHarga}"} else {""}
        holder.ratingView.text = dataList[position].reviewRating.toString()

        // DATE
        if (dataList[position].reviewDate != null) {
            val rDate = selectedReview.reviewDate?.split("_")
            holder.dateView.text = rDate!![0]
        }

        // USER NAME
        dbUserRef.child(selectedReview.userId!!).child("userNama").get().addOnSuccessListener {
            val userName = it.value as String
            holder.userView.text = userName
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        // ON CLICK
        holder.itemCard.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("selectedReview", selectedReview)
            }
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return dataList.size
    }
    fun searchDataList(searchList: List<ReviewModel>) {
        dataList = searchList
        notifyDataSetChanged()
    }
}
class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val judulView: TextView
    val descView: TextView
    val hargaView: TextView
    val imageView: ImageView
    val ratingView: TextView
    val dateView: TextView
    val userView: TextView
    val itemCard: CardView

    init {
        judulView = itemView.findViewById(R.id.textJudul)
        descView = itemView.findViewById(R.id.textDesc)
        hargaView = itemView.findViewById(R.id.textHarga)
        imageView = itemView.findViewById(R.id.imageView)
        ratingView = itemView.findViewById(R.id.textRating)
        dateView = itemView.findViewById(R.id.textDate)
        userView = itemView.findViewById(R.id.textUser)
        itemCard = itemView.findViewById(R.id.itemCard)
    }
}
package com.example.rekomenapp.fragments.mainmenu

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rekomenapp.R
import com.example.rekomenapp.models.ReviewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UploadMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadMenuFragment : Fragment() {
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

    private lateinit var reviewJudul: EditText
    private lateinit var reviewDesc: EditText
    private lateinit var reviewRating: RatingBar
    private lateinit var reviewHarga: EditText

    private lateinit var categoryUpload1: RadioButton
    private lateinit var categoryUpload2: RadioButton
    private lateinit var categoryUpload3: RadioButton
    private lateinit var categoryUpload4: RadioButton
    private lateinit var selectedReviewRadios: String

    private lateinit var reviewSpinner: Spinner

    private lateinit var imageUpload1: ImageView
    private lateinit var imageUpload2: ImageView
    private lateinit var imageUpload3: ImageView
    private lateinit var imageUpload4: ImageView

    private var imageUri1: Uri? = null
    private var imageUri2: Uri? = null
    private var imageUri3: Uri? = null
    private var imageUri4: Uri? = null

    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    // GET DATE
    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", Locale.getDefault())
    private val now = Date()
    private val date = formatter.format(now)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewOfLayout = inflater.inflate(R.layout.fragment_menu_upload, container, false)

        // CATEGORY BUTTONS
        categoryUpload1 = viewOfLayout.findViewById(R.id.categoryUpload_1)
        categoryUpload2 = viewOfLayout.findViewById(R.id.categoryUpload_2)
        categoryUpload3 = viewOfLayout.findViewById(R.id.categoryUpload_3)
        categoryUpload4 = viewOfLayout.findViewById(R.id.categoryUpload_4)

        // CATEGORY SPINNER
        reviewSpinner = viewOfLayout.findViewById(R.id.reviewSpinner)

        val categoryList1 = listOf("Martabak", "Es Krim", "Donat", "Mie Ayam", "Burger", "Nasi Goreng", "Lainnya")
        val adapter1 = ArrayAdapter(activity as Context, android.R.layout.simple_spinner_item, categoryList1)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        reviewSpinner.adapter = adapter1
        selectedReviewRadios = categoryUpload1.text.toString()

        categoryUpload1.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reviewSpinner.adapter = adapter1

                selectedReviewRadios = categoryUpload1.text.toString()
            }
        }

        categoryUpload2.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                val categoryList2 = listOf("Bajigur", "Bandrek", "Cendol", "Es Teh", "Thai Tea", "Kopi", "Lainnya")
                val adapter2 = ArrayAdapter(activity as Context, android.R.layout.simple_spinner_item, categoryList2)
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reviewSpinner.adapter = adapter2

                selectedReviewRadios = categoryUpload2.text.toString()
            }
        }

        categoryUpload3.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                val categoryList3 = listOf("Icon Kota", "Museum", "Daerah", "Perkebunan", "Masjid", "Taman", "Lainnya")
                val adapter3 = ArrayAdapter(activity as Context, android.R.layout.simple_spinner_item, categoryList3)
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reviewSpinner.adapter = adapter3

                selectedReviewRadios = categoryUpload3.text.toString()
            }
        }

        categoryUpload4.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                val categoryList4 = listOf("Indoor", "Outdoor", "Classic", "Modern", "Colorful", "Alam", "Lainnya")
                val adapter4 = ArrayAdapter(activity as Context, android.R.layout.simple_spinner_item, categoryList4)
                adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reviewSpinner.adapter = adapter4

                selectedReviewRadios = categoryUpload4.text.toString()
            }
        }

        // REVIEW INPUT
        reviewJudul = viewOfLayout.findViewById(R.id.reviewJudul)
        reviewDesc = viewOfLayout.findViewById(R.id.reviewDesc)
        reviewRating = viewOfLayout.findViewById(R.id.reviewRating)
        reviewHarga = viewOfLayout.findViewById(R.id.reviewHarga)

        // UPlOAD BUTTON
        val reviewSubmit = viewOfLayout.findViewById<Button>(R.id.reviewSubmit)
        reviewSubmit.setOnClickListener{
            saveReviewData()
        }

        // IMAGE UPLOAD BUTTONS
        val tintColor = ContextCompat.getColor(activity as Context, R.color.icon)

        imageUpload1 = viewOfLayout.findViewById(R.id.imageUpload1)
        imageUpload1.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

        imageUpload2 = viewOfLayout.findViewById(R.id.imageUpload2)
        imageUpload2.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

        imageUpload3 = viewOfLayout.findViewById(R.id.imageUpload3)
        imageUpload3.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

        imageUpload4 = viewOfLayout.findViewById(R.id.imageUpload4)
        imageUpload4.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

        imageUpload1.setOnClickListener{
            selectImage1()
        }
        imageUpload2.setOnClickListener{
            selectImage2()
        }
        imageUpload3.setOnClickListener{
            selectImage3()
        }
        imageUpload4.setOnClickListener{
            selectImage4()
        }

        return viewOfLayout
    }

    // IMAGE SELECTS
    private fun selectImage1() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    private fun selectImage2() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 101)
    }

    private fun selectImage3() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 102)
    }

    private fun selectImage4() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 103)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri1 = data?.data
            imageUpload1.colorFilter = null
            imageUpload1.scaleType = ImageView.ScaleType.CENTER_CROP
            imageUpload1.setImageURI(imageUri1)
        }
        if (requestCode == 101 && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri2 = data?.data
            imageUpload2.colorFilter = null
            imageUpload2.scaleType = ImageView.ScaleType.CENTER_CROP
            imageUpload2.setImageURI(imageUri2)
        }
        if (requestCode == 102 && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri3 = data?.data
            imageUpload3.colorFilter = null
            imageUpload3.scaleType = ImageView.ScaleType.CENTER_CROP
            imageUpload3.setImageURI(imageUri3)
        }
        if (requestCode == 103 && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri4 = data?.data
            imageUpload4.colorFilter = null
            imageUpload4.scaleType = ImageView.ScaleType.CENTER_CROP
            imageUpload4.setImageURI(imageUri4)
        }
    }

    // UPLOAD FUNCTION
    private fun saveReviewData() {
        // FIREBASE REF
        firebaseAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Review")

        val category = selectedReviewRadios
        val subcategory = reviewSpinner.selectedItem.toString()
        val judul = reviewJudul.text.toString()
        val deskripsi = reviewDesc.text.toString()
        val rating = reviewRating.rating
        val harga = reviewHarga.text.toString()

        if (judul.isEmpty()) {
            reviewJudul.error = "Mohon masukkan judul review"
        }
        if (deskripsi.isEmpty()) {
            reviewDesc.error = "Mohon masukkan deskripsi review"
        }

        val idReview = dbRef.push().key!!
        val imageId1 = if (imageUri1 != null) {"$idReview-1"} else {""}
        val imageId2 = if (imageUri2 != null) {"$idReview-2"} else {""}
        val imageId3 = if (imageUri3 != null) {"$idReview-3"} else {""}
        val imageId4 = if (imageUri4 != null) {"$idReview-4"} else {""}

        val uId = firebaseAuth.currentUser!!.uid
        val review = ReviewModel(idReview, category, subcategory, judul, deskripsi, rating, harga, imageId1, imageId2, imageId3, imageId4, date, uId)

        // UPLOAD IMAGE FUNCTION
        fun uploadImage(onComplete: () -> Unit) {
            val tintColor = ContextCompat.getColor(activity as Context, R.color.icon)
            var imagesInputed = 0
            var imagesUploaded = 0

            // STORAGE REF
            val storageReference1 = FirebaseStorage.getInstance().getReference("images/review/$imageId1")
            val storageReference2 = FirebaseStorage.getInstance().getReference("images/review/$imageId2")
            val storageReference3 = FirebaseStorage.getInstance().getReference("images/review/$imageId3")
            val storageReference4 = FirebaseStorage.getInstance().getReference("images/review/$imageId4")

            // CALLBACK AFTER >= 1 IMAGES INPUTED
            val onCompleteUpload = {
                imagesUploaded++
                if (imagesUploaded == imagesInputed) {
                    onComplete()
                    Toast.makeText(activity as Context, "Data berhasil dimasukkan dengan $imagesUploaded foto", Toast.LENGTH_LONG).show()
                }
            }

            if (imageUri1 != null) {
                imagesInputed++

                storageReference1.putFile(imageUri1!!)
                    .addOnSuccessListener {
                        imageUpload1.scaleType = ImageView.ScaleType.FIT_CENTER
                        imageUpload1.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                        imageUpload1.setImageResource(R.drawable.ic_upload)

                        imageUri1 = null

                        onCompleteUpload()

                    }.addOnFailureListener{
                        Toast.makeText(activity as Context, it.message.toString(), Toast.LENGTH_SHORT).show()
                        onCompleteUpload()
                    }
            }

            if (imageUri2 != null) {
                imagesInputed++

                storageReference2.putFile(imageUri2!!)
                    .addOnSuccessListener {
                        imageUpload2.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                        imageUpload2.setImageResource(R.drawable.ic_upload)

                        imageUri2 = null

                        onCompleteUpload()

                    }.addOnFailureListener{
                        Toast.makeText(activity as Context, it.message.toString(), Toast.LENGTH_SHORT).show()
                        onCompleteUpload()
                    }
            }

            if (imageUri3 != null) {
                imagesInputed++

                storageReference3.putFile(imageUri3!!)
                    .addOnSuccessListener {
                        imageUpload3.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                        imageUpload3.setImageResource(R.drawable.ic_upload)

                        imageUri3 = null

                        onCompleteUpload()

                    }.addOnFailureListener{
                        Toast.makeText(activity as Context, it.message.toString(), Toast.LENGTH_SHORT).show()
                        onCompleteUpload()
                    }
            }

            if (imageUri4 != null) {
                imagesInputed++

                storageReference4.putFile(imageUri4!!)
                    .addOnSuccessListener {
                        imageUpload4.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                        imageUpload4.setImageResource(R.drawable.ic_upload)

                        imageUri4 = null

                        onCompleteUpload()

                    }.addOnFailureListener{
                        Toast.makeText(activity as Context, it.message.toString(), Toast.LENGTH_SHORT).show()
                        onCompleteUpload()
                    }
            }

            // CALLBACK AFTER NO IMAGE INPUTED
            if (imagesInputed == 0) {
                onComplete()
            }
        }

        // UPLOAD TO DATABASE
        if (judul.isNotEmpty() && deskripsi.isNotEmpty()) {
            val progressDialog = ProgressDialog(activity as Context)
            progressDialog.setMessage("Uploading data...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            dbRef.child(idReview).setValue(review)
                .addOnCompleteListener{
                    uploadImage {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        Toast.makeText(activity as Context, "Data berhasil dimasukkan", Toast.LENGTH_LONG).show()

                        // UI REFRESH
                        reviewJudul.text.clear()
                        reviewDesc.text.clear()
                        reviewHarga.text.clear()
                        reviewRating.rating = 0f
                    }

                }.addOnFailureListener { err ->
                    Toast.makeText(activity as Context, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UploadMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UploadMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
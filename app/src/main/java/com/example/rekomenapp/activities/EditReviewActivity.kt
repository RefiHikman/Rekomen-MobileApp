package com.example.rekomenapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.rekomenapp.R
import com.example.rekomenapp.models.ReviewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class EditReviewActivity : AppCompatActivity() {

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

    private lateinit var selectedReview: ReviewModel

    // GET DATE
    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", Locale.getDefault())
    private val now = Date()
    private val date = formatter.format(now)

    // SPINNER ADAPTER
    private lateinit var adapter1: ArrayAdapter<String>
    private lateinit var adapter2: ArrayAdapter<String>
    private lateinit var adapter3: ArrayAdapter<String>
    private lateinit var adapter4: ArrayAdapter<String>

    // SPINNER LIST
    private lateinit var categoryList1: List<String>
    private lateinit var categoryList2: List<String>
    private lateinit var categoryList3: List<String>
    private lateinit var categoryList4: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review)

        // DATABASE REFERENCE
        dbRef = FirebaseDatabase.getInstance().getReference("Review")

        // SERIALIZABLE
        selectedReview = intent.getSerializableExtra("selectedReview") as ReviewModel

        // BACK BTN
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener{
            finish()
        }

        // CATEGORY BUTTONS
        categoryUpload1 = findViewById(R.id.categoryUpload_1)
        categoryUpload2 = findViewById(R.id.categoryUpload_2)
        categoryUpload3 = findViewById(R.id.categoryUpload_3)
        categoryUpload4 = findViewById(R.id.categoryUpload_4)

        // CATEGORY SPINNER
        reviewSpinner = findViewById(R.id.reviewSpinner)

        categoryList1 = listOf("Martabak", "Es Krim", "Donat", "Mie Ayam", "Burger", "Nasi Goreng", "Lainnya")
        categoryList2 = listOf("Bajigur", "Bandrek", "Cendol", "Es Teh", "Thai Tea", "Kopi", "Lainnya")
        categoryList3 = listOf("Icon Kota", "Museum", "Daerah", "Perkebunan", "Masjid", "Taman", "Lainnya")
        categoryList4 = listOf("Indoor", "Outdoor", "Classic", "Modern", "Colorful", "Alam", "Lainnya")

        adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList1)
        adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList2)
        adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList3)
        adapter4 = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList4)

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
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reviewSpinner.adapter = adapter2

                selectedReviewRadios = categoryUpload2.text.toString()
            }
        }

        categoryUpload3.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reviewSpinner.adapter = adapter3

                selectedReviewRadios = categoryUpload3.text.toString()
            }
        }

        categoryUpload4.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reviewSpinner.adapter = adapter4

                selectedReviewRadios = categoryUpload4.text.toString()
            }
        }

        // REVIEW INPUT
        reviewJudul = findViewById(R.id.reviewJudul)
        reviewDesc = findViewById(R.id.reviewDesc)
        reviewRating = findViewById(R.id.reviewRating)
        reviewHarga = findViewById(R.id.reviewHarga)

        // DESCRIPTION EDITTEXT SCROLL
        reviewDesc.setScroller(Scroller(this))
        reviewDesc.isVerticalScrollBarEnabled = true
        reviewDesc.movementMethod = ScrollingMovementMethod()

        // UPlOAD BUTTON
        val reviewSubmit = findViewById<Button>(R.id.reviewSubmit)
        reviewSubmit.setOnClickListener{
            saveReviewData()
        }

        // IMAGE UPLOAD BUTTONS
        val tintColor = ContextCompat.getColor(this, R.color.icon)

        imageUpload1 = findViewById(R.id.imageUpload1)
        imageUpload1.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

        imageUpload2 = findViewById(R.id.imageUpload2)
        imageUpload2.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

        imageUpload3 = findViewById(R.id.imageUpload3)
        imageUpload3.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)

        imageUpload4 = findViewById(R.id.imageUpload4)
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

        getReviewData()
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

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri1 = data?.data
            imageUpload1.colorFilter = null
            imageUpload1.scaleType = ImageView.ScaleType.CENTER_CROP
            imageUpload1.setImageURI(imageUri1)
        }
        if (requestCode == 101 && resultCode == RESULT_OK) {
            imageUri2 = data?.data
            imageUpload2.colorFilter = null
            imageUpload2.scaleType = ImageView.ScaleType.CENTER_CROP
            imageUpload2.setImageURI(imageUri2)
        }
        if (requestCode == 102 && resultCode == RESULT_OK) {
            imageUri3 = data?.data
            imageUpload3.colorFilter = null
            imageUpload3.scaleType = ImageView.ScaleType.CENTER_CROP
            imageUpload3.setImageURI(imageUri3)
        }
        if (requestCode == 103 && resultCode == RESULT_OK) {
            imageUri4 = data?.data
            imageUpload4.colorFilter = null
            imageUpload4.scaleType = ImageView.ScaleType.CENTER_CROP
            imageUpload4.setImageURI(imageUri4)
        }
    }

    // GET REVIEW DATA
    private fun getReviewData() {

        // CATEGORY RADIO & SPINNER
        when (selectedReview.reviewCategory) {
            // "Martabak", "Es Krim", "Donat", "Mie Ayam", "Burger", "Nasi Goreng", "Lainnya"
            "Makanan" -> {
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reviewSpinner.adapter = adapter1
                selectedReviewRadios = categoryUpload1.text.toString()

                categoryUpload1.isChecked = true

                when (selectedReview.reviewSubCategory) {
                    "Martabak" -> {
                        val selectedSpinner = "Martabak"
                        val position = adapter1.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Es Krim" -> {
                        val selectedSpinner = "Es Krim"
                        val position = adapter1.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Donat" -> {
                        val selectedSpinner = "Donat"
                        val position = adapter1.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Mie Ayam" -> {
                        val selectedSpinner = "Mie Ayam"
                        val position = adapter1.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Burger" -> {
                        val selectedSpinner = "Burger"
                        val position = adapter1.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Nasi Goreng" -> {
                        val selectedSpinner = "Nasi Goreng"
                        val position = adapter1.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Lainnya" -> {
                        val selectedSpinner = "Lainnya"
                        val position = adapter1.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                }
            }

            // "Bajigur", "Bandrek", "Cendol", "Es Teh", "Thai Tea", "Kopi", "Lainnya"
            "Minuman" -> {
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reviewSpinner.adapter = adapter2
                selectedReviewRadios = categoryUpload2.text.toString()

                categoryUpload2.isChecked = true

                when (selectedReview.reviewSubCategory) {
                    "Bajigur" -> {
                        val selectedSpinner = "Bajigur"
                        val position = adapter2.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Bandrek" -> {
                        val selectedSpinner = "Bandrek"
                        val position = adapter2.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Cendol" -> {
                        val selectedSpinner = "Cendol"
                        val position = adapter2.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Es Teh" -> {
                        val selectedSpinner = "Es Teh"
                        val position = adapter2.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Thai Tea" -> {
                        val selectedSpinner = "Thai Tea"
                        val position = adapter2.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Kopi" -> {
                        val selectedSpinner = "Kopi"
                        val position = adapter2.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Lainnya" -> {
                        val selectedSpinner = "Lainnya"
                        val position = adapter2.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                }
            }

            // "Icon Kota", "Museum", "Daerah", "Perkebunan", "Masjid", "Taman", "Lainnya"
            "Refreshing" -> {
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reviewSpinner.adapter = adapter3
                selectedReviewRadios = categoryUpload3.text.toString()

                categoryUpload3.isChecked = true

                when (selectedReview.reviewSubCategory) {
                    "Icon Kota" -> {
                        val selectedSpinner = "Icon Kota"
                        val position = adapter3.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Museum" -> {
                        val selectedSpinner = "Museum"
                        val position = adapter3.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Daerah" -> {
                        val selectedSpinner = "Daerah"
                        val position = adapter3.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Perkebunan" -> {
                        val selectedSpinner = "Perkebunan"
                        val position = adapter3.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Masjid" -> {
                        val selectedSpinner = "Masjid"
                        val position = adapter3.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Taman" -> {
                        val selectedSpinner = "Taman"
                        val position = adapter3.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Lainnya" -> {
                        val selectedSpinner = "Lainnya"
                        val position = adapter3.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                }
            }

            // "Indoor", "Outdoor", "Classic", "Modern", "Colorful", "Alam", "Lainnya"
            "Nongkrong" -> {
                adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                reviewSpinner.adapter = adapter4
                selectedReviewRadios = categoryUpload4.text.toString()

                categoryUpload4.isChecked = true

                when (selectedReview.reviewSubCategory) {
                    "Indoor" -> {
                        val selectedSpinner = "Indoor"
                        val position = adapter4.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Outdoor" -> {
                        val selectedSpinner = "Outdoor"
                        val position = adapter4.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Classic" -> {
                        val selectedSpinner = "Classic"
                        val position = adapter4.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Modern" -> {
                        val selectedSpinner = "Modern"
                        val position = adapter4.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Colorful" -> {
                        val selectedSpinner = "Colorful"
                        val position = adapter4.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Alam" -> {
                        val selectedSpinner = "Alam"
                        val position = adapter4.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                    "Lainnya" -> {
                        val selectedSpinner = "Lainnya"
                        val position = adapter4.getPosition(selectedSpinner)
                        reviewSpinner.setSelection(position)
                    }
                }
            }
        }

        reviewJudul.setText(selectedReview.reviewJudul)
        reviewDesc.setText(selectedReview.reviewDesc)
        reviewRating.rating = selectedReview.reviewRating!!
        reviewHarga.setText(selectedReview.reviewHarga)

        if (selectedReview.reviewImg1 != "") {
            val imgLink1 = "https://ik.imagekit.io/owdo6w10o/o/images%2Freview%2F${selectedReview.reviewId}%2F${selectedReview.reviewImg1}?alt=media"
            Picasso.get().load(imgLink1).into(imageUpload1)

            imageUpload1.colorFilter = null
            imageUpload1.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        if (selectedReview.reviewImg2 != "") {
            val imgLink2 = "https://ik.imagekit.io/owdo6w10o/o/images%2Freview%2F${selectedReview.reviewId}%2F${selectedReview.reviewImg2}?alt=media"
            Picasso.get().load(imgLink2).into(imageUpload2)

            imageUpload2.colorFilter = null
            imageUpload2.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        if (selectedReview.reviewImg3 != "") {
            val imgLink3 = "https://ik.imagekit.io/owdo6w10o/o/images%2Freview%2F${selectedReview.reviewId}%2F${selectedReview.reviewImg3}?alt=media"
            Picasso.get().load(imgLink3).into(imageUpload3)

            imageUpload3.colorFilter = null
            imageUpload3.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        if (selectedReview.reviewImg4 != "") {
            val imgLink4 = "https://ik.imagekit.io/owdo6w10o/o/images%2Freview%2F${selectedReview.reviewId}%2F${selectedReview.reviewImg4}?alt=media"
            Picasso.get().load(imgLink4).into(imageUpload4)

            imageUpload4.colorFilter = null
            imageUpload4.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    // UPLOAD FUNCTION
    private fun saveReviewData() {
        // FIREBASE REF
        firebaseAuth = FirebaseAuth.getInstance()

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

        val imageId1 =
            if (imageUri1 != null && selectedReview.reviewImg1 != "") {             // IMAGE CHANGED
                "${selectedReview.reviewId}-$date-1"
            } else if (imageUri1 != null && selectedReview.reviewImg1 == "") {      // IMAGE ADDED
                "${selectedReview.reviewId}-$date-1"
            } else if (imageUri1 == null && selectedReview.reviewImg1 != null) {    // IMAGE NOT CHANGED / ADDED
                "${selectedReview.reviewImg1}"
            } else {""}                                                             // NO IMAGE

        val imageId2 =
            if (imageUri2 != null && selectedReview.reviewImg2 != "") {             // IMAGE CHANGED
                "${selectedReview.reviewId}-$date-2"
            } else if (imageUri2 != null && selectedReview.reviewImg2 == "") {      // IMAGE ADDED
                "${selectedReview.reviewId}-$date-2"
            } else if (imageUri2 == null && selectedReview.reviewImg2 != null) {    // IMAGE NOT CHANGED / ADDED
                "${selectedReview.reviewImg2}"
            } else {""}                                                             // NO IMAGE

        val imageId3 =
            if (imageUri3 != null && selectedReview.reviewImg3 != "") {             // IMAGE CHANGED
                "${selectedReview.reviewId}-$date-3"
            } else if (imageUri3 != null && selectedReview.reviewImg3 == "") {      // IMAGE ADDED
                "${selectedReview.reviewId}-$date-3"
            } else if (imageUri3 == null && selectedReview.reviewImg3 != null) {    // IMAGE NOT CHANGED / ADDED
                "${selectedReview.reviewImg3}"
            } else {""}                                                             // NO IMAGE

        val imageId4 =
            if (imageUri4 != null && selectedReview.reviewImg4 != "") {             // IMAGE CHANGED
                "${selectedReview.reviewId}-$date-4"
            } else if (imageUri4 != null && selectedReview.reviewImg4 == "") {      // IMAGE ADDED
                "${selectedReview.reviewId}-$date-4"
            } else if (imageUri4 == null && selectedReview.reviewImg4 != null) {    // IMAGE NOT CHANGED / ADDED
                "${selectedReview.reviewImg4}"
            } else {""}                                                             // NO IMAGE

        val uId = firebaseAuth.currentUser!!.uid

        val reviewMod = ReviewModel(
            selectedReview.reviewId,
            category,
            subcategory,
            judul,
            deskripsi,
            rating,
            harga,
            imageId1,
            imageId2,
            imageId3,
            imageId4,
            date,
            uId
        )

        // UPLOAD IMAGE FUNCTION
        fun uploadImage(onComplete: () -> Unit) {
            val tintColor = ContextCompat.getColor(this, R.color.icon)
            var imagesInputed = 0
            var imagesUploaded = 0

            // STORAGE REF
            val storageReference1 = FirebaseStorage.getInstance().getReference("images/review/${selectedReview.reviewId}/$imageId1")
            val storageReference2 = FirebaseStorage.getInstance().getReference("images/review/${selectedReview.reviewId}/$imageId2")
            val storageReference3 = FirebaseStorage.getInstance().getReference("images/review/${selectedReview.reviewId}/$imageId3")
            val storageReference4 = FirebaseStorage.getInstance().getReference("images/review/${selectedReview.reviewId}/$imageId4")

            // CALLBACK AFTER >= 1 IMAGES INPUTED
            val onCompleteUpload = {
                imagesUploaded++
                if (imagesUploaded == imagesInputed) {
                    onComplete()
                    Toast.makeText(this, "Data berhasil dimasukkan dengan $imagesUploaded foto", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
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
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Saving data...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            dbRef.child(selectedReview.reviewId!!).setValue(reviewMod)
                .addOnCompleteListener{
                    uploadImage {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        Toast.makeText(this, "Data berhasil diubah", Toast.LENGTH_LONG).show()

                        // UI REFRESH
                        reviewJudul.text.clear()
                        reviewDesc.text.clear()
                        reviewHarga.text.clear()
                        reviewRating.rating = 0f

                        finish()
                    }

                }.addOnFailureListener { err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
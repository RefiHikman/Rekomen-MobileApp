package com.example.rekomenapp.activities

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.rekomenapp.R
import com.example.rekomenapp.databinding.ActivityEditProfileBinding
import com.example.rekomenapp.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var dbRef: DatabaseReference

    private val calendar = Calendar.getInstance()

    private var imageUri: Uri? = null

    // GET DATE
    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", Locale.getDefault())
    private val now = Date()
    private val date = formatter.format(now)

    private lateinit var currentImageId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = this.resources.getColor(R.color.mainOrange)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        binding.tanggalBtn.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this, this, year, month, day
            )

            datePickerDialog.show()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.imageUpload.setOnClickListener{
            selectImage()
        }

        binding.submitBtn.setOnClickListener{
            saveUserData()
        }

        getUserData()
    }

    private fun getUserData() {
        val nama = binding.editNama
        val kota = binding.editLokasi
        val profesi = binding.editProfesi
        val bio = binding.editBio
        val tanggalLahir = binding.tanggalBtn
        val image = binding.imageUpload

        val uId = FirebaseAuth.getInstance().currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query = dbRef.child(uId)

        // FETCH USER DATA
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserModel::class.java)
                Log.d("getUserData1", "user: $user")

                nama.setText(user?.userNama)
                kota.setText(user?.userLokasi)
                profesi.setText(user?.userProfesi)
                bio.setText(user?.userBio)
                tanggalLahir.text = user?.userTgl

                if (user?.userImage != "") {
                    val imgLink = "https://ik.imagekit.io/owdo6w10o/o/images%2Fprofile%2F${user?.userImage}?alt=media"
                    Picasso.get().load(imgLink).into(image)

                    imageUri = Uri.parse("Assigned")
                    currentImageId = user?.userImage!!
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching user", databaseError.toException())
            }
        }
        query.addListenerForSingleValueEvent(userListener)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val formattedDate = dateFormat.format(calendar.time)

        binding.tanggalBtn.text = formattedDate
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val sourceUri: Uri = data.data!!
            val destinationUri: Uri = Uri.fromFile(File(cacheDir, "cropped"))
            val options = UCrop.Options()
            options.setToolbarColor(ContextCompat.getColor(this, R.color.mainOrange))
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.mainOrange))
            options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.mainOrange))
            UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withOptions(options)
                .start(this)
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imageUri = UCrop.getOutput(data!!)
            binding.imageUpload.setImageURI(imageUri)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Error ${UCrop.getError(data!!)}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserData() {
        val nama = binding.editNama.text.toString()
        val kota = binding.editLokasi.text.toString()
        val profesi = binding.editProfesi.text.toString()
        val bio = binding.editBio.text.toString()
        val tanggalLahir = binding.tanggalBtn.text.toString()

        val uId = FirebaseAuth.getInstance().currentUser!!.uid
        val imageId = if (imageUri != null) {"$uId-$date"} else {""}

        var user = UserModel(uId, nama, tanggalLahir, kota, profesi, bio, imageId)
        if (imageUri == Uri.parse("Assigned")) {
            user = UserModel(uId, nama, tanggalLahir, kota, profesi, bio, currentImageId)
        }

        if (nama.isEmpty()) {
            binding.editNama.error = "Mohon masukkan nama mu"
        }
        if (kota.isEmpty()) {
            binding.editLokasi.error = "Mohon masukkan lokasi mu"
        }
        if (profesi.isEmpty()) {
            binding.editProfesi.error = "Mohon masukkan profesi mu"
        }
        if (tanggalLahir.isEmpty() || tanggalLahir == "Tanggal Lahir") {
            Toast.makeText(this, "Mohon masukkan tanggal lahir mu", Toast.LENGTH_SHORT).show()
        }

        // FUNCTION UPLOAD IMAGE
        fun uploadImage(onComplete: () -> Unit) {
            val storageReference = FirebaseStorage.getInstance().getReference("images/profile/$imageId")

            if (imageUri != null) {
                storageReference.putFile(imageUri!!)
                    .addOnSuccessListener {
                        binding.imageUpload.setImageURI(null)
                        onComplete()

                    }.addOnFailureListener{
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            } else {
                onComplete()
            }
        }

        if (nama.isNotEmpty() && kota.isNotEmpty() && profesi.isNotEmpty() && tanggalLahir != "Tanggal Lahir") {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Saving data...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            dbRef.child(uId).setValue(user)
                .addOnCompleteListener {
                    if (imageUri == Uri.parse("Assigned")) {
                        Toast.makeText(this, "Data user berhasil diubah", Toast.LENGTH_SHORT).show()
                        if (progressDialog.isShowing) progressDialog.dismiss()

                        finish()
                    } else {
                        uploadImage {
                            Toast.makeText(this, "Data user berhasil diubah", Toast.LENGTH_SHORT).show()
                            if (progressDialog.isShowing) progressDialog.dismiss()

                            finish()
                        }
                    }

                }.addOnFailureListener { err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
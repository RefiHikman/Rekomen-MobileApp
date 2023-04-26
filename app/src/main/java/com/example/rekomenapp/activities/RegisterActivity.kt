package com.example.rekomenapp.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat
import com.example.rekomenapp.R
import com.google.firebase.auth.FirebaseAuth
import com.example.rekomenapp.databinding.ActivityRegisterBinding
import com.example.rekomenapp.models.UserModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = this.resources.getColor(R.color.mainOrange)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        val sharedPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        firebaseAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        binding.signInText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.submitBtn.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val pass = binding.editPassword.text.toString()
            val confirmPass = binding.editConfirmPassword.text.toString()

            if (email.isEmpty()) {
                binding.editEmail.error = "Mohon masukkan email mu"
            }
            if (pass.isEmpty()) {
                binding.editPassword.error = "Mohon masukkan password mu"
            }
            if (confirmPass.isEmpty()) {
                binding.editConfirmPassword.error = "Mohon confirm password mu"
            }

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setMessage("Signing Up...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val uId = firebaseAuth.currentUser!!.uid
                            val user = UserModel(uId, "", "", "", "", "", "")

                            dbRef.child(uId).setValue(user)
                                .addOnCompleteListener {
                                    if (progressDialog.isShowing) progressDialog.dismiss()

                                    val intent = Intent(this, CreateProfileActivity::class.java)
                                    startActivity(intent)
                                }.addOnFailureListener { err ->
                                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                                }

                            val editor = sharedPrefs.edit()
                            editor.putBoolean("isFirstTime", false)
                            editor.apply()
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            if (progressDialog.isShowing) progressDialog.dismiss()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
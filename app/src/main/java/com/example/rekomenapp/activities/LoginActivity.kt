package com.example.rekomenapp.activities

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat
import com.example.rekomenapp.R
import com.example.rekomenapp.databinding.ActivityLoginBinding
import com.example.rekomenapp.models.UserModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = this.resources.getColor(R.color.mainOrange)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        firebaseAuth = FirebaseAuth.getInstance()

        binding.submitBtn.setOnClickListener {
            userLogin()
        }

        binding.signUpText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun userLogin() {
        val email = binding.editEmail.text.toString()
        val pass = binding.editPassword.text.toString()

        if (email.isEmpty()) {
            binding.editEmail.error = "Mohon masukkan email mu"
        }
        if (pass.isEmpty()) {
            binding.editPassword.error = "Mohon masukkan password mu"
        }

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Signing In...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener{
                if (it.isSuccessful) {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    val editor = sharedPrefs.edit()
                    editor.putBoolean("isFirstTime", false)
                    editor.apply()
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    if (progressDialog.isShowing) progressDialog.dismiss()
                }
            }
        }
    }
}
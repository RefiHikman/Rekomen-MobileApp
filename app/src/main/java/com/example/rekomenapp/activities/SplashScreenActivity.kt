package com.example.rekomenapp.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.example.rekomenapp.R
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = this.resources.getColor(R.color.mainOrange)
            window.statusBarColor = this.resources.getColor(R.color.mainOrange)
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        }

        firebaseAuth = FirebaseAuth.getInstance()

        supportActionBar?.hide()
        val handler = Handler()

        val sharedPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val isFirstTime = sharedPrefs.getBoolean("isFirstTime", true)
        if (isFirstTime) {
            handler.postDelayed({
                val intent = Intent(this, IntroSliderActivity::class.java)
                startActivity(intent)
            }, 3000)

        } else {
            handler.postDelayed({
                if (firebaseAuth.currentUser != null) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }, 3000)
        }
    }
}
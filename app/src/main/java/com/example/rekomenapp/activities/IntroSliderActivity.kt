package com.example.rekomenapp.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager.widget.ViewPager
import com.example.rekomenapp.R
import com.example.rekomenapp.adapters.IntroSliderAdapter
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class IntroSliderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_slider)

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = this.resources.getColor(R.color.mainOrange)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        val viewpager = findViewById<ViewPager>(R.id.viewpager)
        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dotsIndicator)

        viewpager.adapter = IntroSliderAdapter(supportFragmentManager)
        dotsIndicator.attachTo(viewpager)
    }
}
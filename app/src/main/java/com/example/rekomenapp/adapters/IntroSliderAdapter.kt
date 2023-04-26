package com.example.rekomenapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.rekomenapp.fragments.introslider.IntroSlider1Fragment
import com.example.rekomenapp.fragments.introslider.IntroSlider2Fragment
import com.example.rekomenapp.fragments.introslider.IntroSlider3Fragment
import com.example.rekomenapp.fragments.introslider.IntroSlider4Fragment

class IntroSliderAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val fragment = listOf(
        IntroSlider1Fragment(),
        IntroSlider2Fragment(),
        IntroSlider3Fragment(),
        IntroSlider4Fragment()
    )
    override fun getItem(position: Int): Fragment {
        return fragment[position]
    }
    override fun getCount(): Int {
        return fragment.size
    }

}
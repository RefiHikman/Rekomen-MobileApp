package com.example.rekomenapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.rekomenapp.R
import com.example.rekomenapp.databinding.ActivityMainBinding
import com.example.rekomenapp.fragments.mainmenu.HomeMenuFragment
import com.example.rekomenapp.fragments.mainmenu.SearchMenuFragment
import com.example.rekomenapp.fragments.mainmenu.UploadMenuFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(HomeMenuFragment())

        binding.bottomNavMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeMenuFragment())
                    true
                }
                R.id.search -> {
                    loadFragment(SearchMenuFragment())
                    true
                }
                R.id.upload -> {
                    loadFragment(UploadMenuFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

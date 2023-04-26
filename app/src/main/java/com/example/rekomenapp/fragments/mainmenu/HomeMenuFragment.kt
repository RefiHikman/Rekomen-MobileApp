package com.example.rekomenapp.fragments.mainmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.rekomenapp.R
import com.example.rekomenapp.activities.ProfileActivity
import com.example.rekomenapp.fragments.categorymenu.CategoryMakananFragment
import com.example.rekomenapp.fragments.categorymenu.CategoryMinumanFragment
import com.example.rekomenapp.fragments.categorymenu.CategoryNongkrongFragment
import com.example.rekomenapp.fragments.categorymenu.CategoryRefreshingFragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeMenuFragment : Fragment() {
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

    private lateinit var dbUserRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var profileImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewOfLayout = inflater.inflate(R.layout.fragment_menu_home, container, false)

        val categoryRadio1: RadioButton = viewOfLayout.findViewById(R.id.categoryMenu_1)
        val categoryRadio2: RadioButton = viewOfLayout.findViewById(R.id.categoryMenu_2)
        val categoryRadio3: RadioButton = viewOfLayout.findViewById(R.id.categoryMenu_3)
        val categoryRadio4: RadioButton = viewOfLayout.findViewById(R.id.categoryMenu_4)

        loadFragment(CategoryMakananFragment())

        categoryRadio1.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) loadFragment(CategoryMakananFragment())
        }

        categoryRadio2.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) loadFragment(CategoryMinumanFragment())
        }

        categoryRadio3.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) loadFragment(CategoryRefreshingFragment())
        }

        categoryRadio4.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) loadFragment(CategoryNongkrongFragment())
        }

        profileImage = viewOfLayout.findViewById<ShapeableImageView>(R.id.topProfile)
        profileImage.setOnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }

        getUserImage()
        return viewOfLayout
    }

    private fun getUserImage() {

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = firebaseAuth.currentUser!!.uid

        dbUserRef = FirebaseDatabase.getInstance().getReference("Users")
        dbUserRef.child(currentUserId).child("userImage").get().addOnSuccessListener {
            val userImage = it.value as? String
            if (userImage != null) {
                val imgLink = "https://firebasestorage.googleapis.com/v0/b/rekomen-926c7.appspot.com/o/images%2Fprofile%2F$userImage?alt=media"
                Glide.with(this).load(imgLink).into(profileImage)
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.homeContainer,fragment)
        transaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
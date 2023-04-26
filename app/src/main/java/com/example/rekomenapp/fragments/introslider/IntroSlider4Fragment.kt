package com.example.rekomenapp.fragments.introslider

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.rekomenapp.R
import com.example.rekomenapp.activities.LoginActivity
import com.example.rekomenapp.activities.RegisterActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IntroSlider4Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IntroSlider4Fragment : Fragment() {
    private lateinit var viewOfLayout: View

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewOfLayout =  inflater.inflate(R.layout.fragment_intro_slider4, container, false)

        val btnLogin = viewOfLayout.findViewById<Button>(R.id.btnLogin)
        val btnRegister = viewOfLayout.findViewById<Button>(R.id.btnRegister)

        btnLogin.setOnClickListener{
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        btnRegister.setOnClickListener{
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }

        return viewOfLayout
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IntroSlider4Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IntroSlider4Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
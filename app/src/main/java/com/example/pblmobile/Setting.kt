package com.example.pblmobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class Setting : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_setting, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("user_name", "")
        val email = sharedPreferences.getString("email", "")

        val usernameTextView = view.findViewById<TextView>(R.id.etNama)
        val emailTextView = view.findViewById<TextView>(R.id.etEmail)

        usernameTextView.text = username
        emailTextView.text = email


        view.findViewById<TextView>(R.id.eteditprofile).setOnClickListener {
            startActivity(Intent(requireContext(), Profile::class.java))
        }

        view.findViewById<TextView>(R.id.etpoinsaya).setOnClickListener {
            startActivity(Intent(requireContext(), TukarPoinActivity::class.java))
        }

        view.findViewById<TextView>(R.id.ettentang).setOnClickListener {
            startActivity(Intent(requireContext(), Tentangaplikasi::class.java))
        }

        view.findViewById<TextView>(R.id.etkeluar).setOnClickListener {
            sharedPreferences.edit().clear().apply()
            val intent = Intent(requireContext(), MasukActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}

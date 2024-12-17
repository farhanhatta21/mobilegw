package com.example.pblmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pblmobile.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding

    private var imageResId: Int = 0
    private var title: String = ""
    private var description: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the data to the views
        binding.imageView.setImageResource(imageResId)
        binding.textViewTitle.text = title
        binding.textViewDescription.text = description
    }

    // Method to set data
    fun setData(imageResId: Int, title: String, description: String) {
        this.imageResId = imageResId
        this.title = title
        this.description = description
    }
}

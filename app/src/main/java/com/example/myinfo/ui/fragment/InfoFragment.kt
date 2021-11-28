package com.example.myinfo.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myinfo.R
import com.example.myinfo.databinding.FragmentInfoBinding
import com.example.myinfo.databinding.FragmentSummaryBinding

class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInfoBinding.inflate(inflater, container, false)

        presetAuthorDetails()

        return binding.root
    }

    private fun presetAuthorDetails() {
        binding.infoRandomLayout1.editText!!.setText(getString(R.string.insta))
        binding.infoRandomLayout2.editText!!.setText(getString(R.string.paypal))
        binding.infoRandomLayout3.editText!!.setText(getString(R.string.location))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
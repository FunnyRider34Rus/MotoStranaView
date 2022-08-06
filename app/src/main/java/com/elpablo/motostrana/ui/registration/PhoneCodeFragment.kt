package com.elpablo.motostrana.ui.registration

import android.os.Bundle
import android.view.View
import com.elpablo.motostrana.databinding.FragmentPhoneCodeBinding
import com.elpablo.motostrana.utils.BaseFragment

class PhoneCodeFragment :
    BaseFragment<FragmentPhoneCodeBinding>(FragmentPhoneCodeBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userPhoneNumber = arguments?.get("bundleKey")
        val mText = binding.textDescription.text.toString() + " " + userPhoneNumber
        binding.textDescription.text = mText
    }
}
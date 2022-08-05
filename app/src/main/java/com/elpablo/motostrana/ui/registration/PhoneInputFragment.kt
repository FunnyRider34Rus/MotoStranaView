package com.elpablo.motostrana.ui.registration

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.elpablo.motostrana.R
import com.elpablo.motostrana.databinding.FragmentPhoneRegistrationBinding
import com.elpablo.motostrana.utils.AppTextWatcher
import com.elpablo.motostrana.utils.BaseFragment
import com.elpablo.motostrana.utils.CONST.PHONE_NUMBER_LENGTH
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class PhoneInputFragment :
    BaseFragment<FragmentPhoneRegistrationBinding>(FragmentPhoneRegistrationBinding::inflate) {

    private lateinit var mPhoneNumber: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            buttonBack.setOnClickListener {
                findNavController().navigate(R.id.navigation_welcome)
            }
            enterPhoneInput.addTextChangedListener(PhoneNumberFormattingTextWatcher("RU"))
            enterPhoneInput.addTextChangedListener(
                AppTextWatcher {
                    mPhoneNumber = enterPhoneInput.text.toString()
                })
            enterPhoneButton.setOnClickListener {
                if (mPhoneNumber.length == PHONE_NUMBER_LENGTH) {
                    mPhoneNumber = "+7" + mPhoneNumber.filter { it.isDigit() }
                    val bundle = bundleOf("bundleKey" to mPhoneNumber)
                    findNavController().navigate(R.id.navigation_input_code, bundle)
                }
            }
        }
    }
}
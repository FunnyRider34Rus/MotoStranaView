package com.elpablo.motostrana.ui.registration

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.elpablo.motostrana.R
import com.elpablo.motostrana.databinding.FragmentPhoneInputBinding
import com.elpablo.motostrana.ui.MainActivity
import com.elpablo.motostrana.utils.BaseFragment

class PhoneInputFragment :
    BaseFragment<FragmentPhoneInputBinding>(FragmentPhoneInputBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            buttonBack.setOnClickListener {
                findNavController().navigate(R.id.welcome)
            }

            buttonNext.setOnClickListener {
                goToNextScreen()
            }

            //обработка imeOptions
            editTextPhoneNumber.setOnEditorActionListener { _, actionId, _ -> return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    goToNextScreen()
                }
                else -> false
            } }
        }
    }

    private fun goToNextScreen() : Boolean {
        if (!binding.editTextPhoneNumber.text.isNullOrBlank() && binding.editTextPhoneNumber.text?.length == 10) {
            val userPhoneNumber = "+7" + binding.editTextPhoneNumber.text.toString()
            val bundle = bundleOf("bundleKey" to userPhoneNumber)
            findNavController().navigate(R.id.input_code, bundle)
            return true
        } else {
            return false
        }
    }
}
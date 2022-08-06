package com.elpablo.motostrana.ui.registration

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.elpablo.motostrana.R
import com.elpablo.motostrana.databinding.FragmentWelcomeBinding
import com.elpablo.motostrana.ui.MainActivity
import com.elpablo.motostrana.utils.BaseFragment

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity?)!!.hideBottomNav()
        binding.signInButton.setOnClickListener {
            findNavController().navigate(R.id.input_phone)
        }
    }
}
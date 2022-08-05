package com.elpablo.motostrana.ui.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.elpablo.motostrana.App.Companion.auth
import com.elpablo.motostrana.R
import com.elpablo.motostrana.databinding.FragmentSettingsBinding
import com.elpablo.motostrana.utils.BaseFragment
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            profile.setOnClickListener {
                findNavController().navigate(R.id.navigation_settings_profile)
            }
            buttonLogout.setOnClickListener {
                auth.signOut()
                val intent = requireActivity().intent
                requireActivity().finish()
                startActivity(intent)
            }
        }
    }
}
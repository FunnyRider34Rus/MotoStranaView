package com.elpablo.motostrana.ui.registration

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.elpablo.motostrana.App.Companion.auth
import com.elpablo.motostrana.App.Companion.initUser
import com.elpablo.motostrana.R
import com.elpablo.motostrana.databinding.FragmentWelocmeBinding
import com.elpablo.motostrana.ui.MainActivity
import com.elpablo.motostrana.utils.AppStates
import com.elpablo.motostrana.utils.BaseFragment
import com.elpablo.motostrana.utils.saveUserDataToDB
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class WelcomeFragment : BaseFragment<FragmentWelocmeBinding>(FragmentWelocmeBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Инициализируем пользователя и получаем информацию
        if (auth.currentUser?.uid != null) {
            AppStates.updateState(AppStates.ONLINE)
            initUser()
            findNavController().navigate(R.id.navigation_home)
        } else {
            (activity as MainActivity?)!!.hideBottomNav()
            binding.signInButton.setOnClickListener {
                findNavController().navigate(R.id.navigation_input_phone)
            }
        }
    }
}
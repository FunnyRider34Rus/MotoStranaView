package com.elpablo.motostrana.ui.registration

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.elpablo.motostrana.App.Companion.USER
import com.elpablo.motostrana.App.Companion.auth
import com.elpablo.motostrana.App.Companion.initUser
import com.elpablo.motostrana.App.Companion.remoteDB
import com.elpablo.motostrana.R
import com.elpablo.motostrana.databinding.FragmentInputUserDataBinding
import com.elpablo.motostrana.utils.AppValueEventListener
import com.elpablo.motostrana.utils.BaseFragment
import com.elpablo.motostrana.utils.CONST.NODE_USERS
import com.elpablo.motostrana.utils.saveUserDataToDB

class InputUserDataFragment :
    BaseFragment<FragmentInputUserDataBinding>(FragmentInputUserDataBinding::inflate) {

    private lateinit var name: String
    private lateinit var surname: String
    private lateinit var mUID: String
    private lateinit var phoneNumber: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phoneNumber = arguments?.getString("bundleKey").toString()
        mUID = auth.currentUser?.uid.toString()

        //Проверяем пользователя на повторный вход
        remoteDB.child(NODE_USERS).addListenerForSingleValueEvent(
            AppValueEventListener {
                if (it.hasChild(mUID)) {
                    initUser()
                    findNavController().navigate(R.id.navigation_home)
                }
            }
        )

        //Записываем данные нового пользователя
        with(binding) {
            doneButton.setOnClickListener {
                name = enterUserName.text.toString()
                surname = enterUserSurname.text.toString()
                if (name.isNotEmpty() && surname.isNotEmpty()) {
                    val fullname = "$name $surname"
                    USER.phone = phoneNumber
                    USER.fullname = fullname
                    saveUserDataToDB()
                    findNavController().navigate(R.id.navigation_home)
                } else {
                    Toast.makeText(requireContext(), "Заполнены не все поля", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
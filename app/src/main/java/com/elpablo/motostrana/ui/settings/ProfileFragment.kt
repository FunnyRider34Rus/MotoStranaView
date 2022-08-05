package com.elpablo.motostrana.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import coil.load
import com.elpablo.motostrana.App.Companion.USER
import com.elpablo.motostrana.App.Companion.remoteDB
import com.elpablo.motostrana.App.Companion.remoteStorage
import com.elpablo.motostrana.R
import com.elpablo.motostrana.databinding.FragmentSettingsUserProfileBinding
import com.elpablo.motostrana.utils.*
import com.elpablo.motostrana.utils.CONST.FOLDER_USER_PROFILE_PHOTO
import com.elpablo.motostrana.utils.CONST.NODE_USERS_NICKNAMES
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class ProfileFragment :
    BaseFragment<FragmentSettingsUserProfileBinding>(FragmentSettingsUserProfileBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding) {
            val fullnameList = USER.fullname.split(" ")
            userNickname.setText(USER.nickname)
            userName.setText(fullnameList[0])
            userSurname.setText(fullnameList[1])
            if (USER.photo != "") {
                userLogo.load(USER.photo)
            } else {
                userLogo.load(R.drawable.ic_image_not_supported_96)
            }

            buttonBack.setOnClickListener {
                findNavController().navigate(R.id.navigation_settings)
            }

            buttonEdit.setOnClickListener {
                editData()
            }

            userLogo.setOnClickListener {
                changeUserPhoto()
            }
        }
    }

    private val documentPick =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            val path = remoteStorage.child(FOLDER_USER_PROFILE_PHOTO).child(USER.id)
            uri?.let {
                putImageToStorage (uri, path) {
                    getURLFromStorage(path) {
                        putURLToDatabase (it) {
                            binding.userLogo.load(USER.photo)
                        }
                    }
                }
            }
        }

    private fun changeUserPhoto() {
        documentPick.launch(arrayOf("image/*"))
    }

    private fun editData() {
        with(binding) {
            val name = userName.text.toString()
            val surname = userSurname.text.toString()
            val newNickname = userNickname.text.toString()
            if (name.isNotEmpty() && surname.isNotEmpty() && newNickname.isNotEmpty()) {
                remoteDB.addListenerForSingleValueEvent(
                    AppValueEventListener {
                        if (it.hasChild(newNickname)) {
                            Toast.makeText(
                                requireContext(),
                                "Введённый псевдоним уже существует",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val fullname = "$name $surname"
                            remoteDB.child(NODE_USERS_NICKNAMES).child(USER.nickname).removeValue()
                            remoteDB.child(NODE_USERS_NICKNAMES).child(newNickname).setValue(USER.id)
                            USER.fullname = fullname
                            USER.nickname = newNickname
                            saveUserDataToDB()
                            Toast.makeText(requireContext(), "Данные обновлены", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.navigation_settings)
                        }
                    })
            } else {
                Toast.makeText(
                    requireContext(),
                    "Заполнены не все поля",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
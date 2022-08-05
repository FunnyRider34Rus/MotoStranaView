package com.elpablo.motostrana.utils

import com.elpablo.motostrana.ui.MainActivity

object CONST {
    const val LOCATION_PERMISSION_REQUEST_CODE = 1
    const val CONTACTS_PERMISSION_REQUEST_CODE = 2
    const val TAG = "MyLog"
    const val EVENT = "event"           //константы для отображения на главном экране
    const val NEWS = "news"             //вкладок "новости", либо "события"
    const val KEY_CITY = "city"         //ключ для сохранения состояния города
    const val KEY_STATE = "state"       //ключ для сохранения состояния области
    const val PHONE_NUMBER_LENGTH = 13  //длина номера телефона
    const val NODE_USERS = "users"
    const val NODE_USERS_NICKNAMES = "nicknames"
    const val CHILD_ID = "id"
    const val CHILD_PHONE = "phone"
    const val CHILD_FULLNAME = "fullname"
    const val CHILD_NICKNAME = "nickname"
    const val CHILD_URLPHOTO = "photo"
    const val CHILD_STATE = "state"
    const val FOLDER_USER_PROFILE_PHOTO = "avatars"
    lateinit var ACTIVITY_CONTEXT: MainActivity
}
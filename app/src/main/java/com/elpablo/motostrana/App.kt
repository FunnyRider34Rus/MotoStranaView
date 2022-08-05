package com.elpablo.motostrana

import android.annotation.SuppressLint
import android.app.Application
import com.elpablo.motostrana.model.User
import com.elpablo.motostrana.utils.AppValueEventListener
import com.elpablo.motostrana.utils.CONST
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        auth.setLanguageCode("ru")
        remoteEventDB = Firebase.firestore
        remoteDB = Firebase.database.reference
        remoteStorage = FirebaseStorage.getInstance().reference
        USER = User()
    }

    companion object {
        lateinit var auth: FirebaseAuth
        @SuppressLint("StaticFieldLeak")
        lateinit var remoteEventDB: FirebaseFirestore
        lateinit var remoteDB: DatabaseReference
        lateinit var remoteStorage: StorageReference
        lateinit var USER: User
        var cityName: String? = null
        var regionName: String? = null

        fun initUser() {
            val mUID = auth.currentUser?.uid
            if (mUID != null) {
                remoteDB.child(CONST.NODE_USERS).child(mUID).addListenerForSingleValueEvent(
                    AppValueEventListener { data ->
                        USER = data.getValue(User::class.java) ?: User()
                    })
            }
        }
    }
}
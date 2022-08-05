package com.elpablo.motostrana.utils

import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.elpablo.motostrana.utils.CONST.ACTIVITY_CONTEXT

fun checkPermission(permission: String, requestCode: Int) : Boolean {
    return if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(ACTIVITY_CONTEXT, permission) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(ACTIVITY_CONTEXT, arrayOf(permission), requestCode)
        false
    } else true
}
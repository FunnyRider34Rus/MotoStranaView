package com.elpablo.motostrana.ui.chat

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class OpenDocumentContract : ActivityResultContracts.OpenDocument() {

    override fun createIntent(context: Context, input: Array<String>): Intent {
        val intent = super.createIntent(context, input)
        return intent.addCategory(Intent.CATEGORY_OPENABLE)
    }
}

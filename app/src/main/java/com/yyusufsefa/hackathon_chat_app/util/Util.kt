package com.yyusufsefa.hackathon_chat_app.util

import android.content.Context
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yyusufsefa.hackathon_chat_app.data.model.DeviceToken

fun showInfoDialog(context: Context, message: String, onPositiveClick: (() -> Unit)? = null) {
    MaterialAlertDialogBuilder(context)
        .setTitle("Info")
        .setCancelable(false)
        .setMessage(message)
        .setPositiveButton("Yes") { dialog, _ ->
            if (onPositiveClick != null) {
                onPositiveClick()
            }
        }
        .setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }.show()
}

fun saveCurrentUserDeviceToken(token: String) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser != null) {
        val dbRef = Firebase.database.getReference("device_tokens").child(currentUser.uid)
        val deviceToken = DeviceToken(token)
        dbRef.setValue(deviceToken)
    }
}

// check of textinput
fun TextInputEditText.isNotNullOrEmpty(errorString: String): Boolean {
    val textInputLayout = this.parent.parent as TextInputLayout
    textInputLayout.errorIconDrawable = null
    this.doOnTextChanged { _, _, _, _ ->
        textInputLayout.error = null
    }

    return if (this.text.toString().trim().isEmpty()) {
        textInputLayout.error = errorString
        false
    } else {
        true
    }
}

// çokul viewlerin validasyonunu sağlamak
fun validateAndDo(textInputEditText: List<TextInputEditText>, block: () -> Unit) {

    var boolean = false
    for (text in textInputEditText) {
        if (!text.isNotNullOrEmpty("Required"))
            boolean = true
    }

    if (!boolean)
        block()
}
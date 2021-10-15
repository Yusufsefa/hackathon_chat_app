package com.yyusufsefa.hackathon_chat_app.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showInfoDialog(context: Context, message: String, onPositiveClick: (() -> Unit)? = null){
    MaterialAlertDialogBuilder(context)
        .setTitle("Info")
        .setCancelable(false)
        .setMessage(message)
        .setPositiveButton("Yes") { dialog, _ ->
            if (onPositiveClick != null) {
                onPositiveClick()
            }
        }
        .setNegativeButton("No",) { dialog, _ ->
             dialog.dismiss()
        }.show()
}
package com.yyusufsefa.hackathon_chat_app.data.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.IgnoreExtraProperties
import com.ramo.sweetrecycleradapter.ViewTypeListener
import com.yyusufsefa.hackathon_chat_app.R

@IgnoreExtraProperties
data class ChatMessage(
    var text: String? = "",
    var fromId: String? = "",
    var toId: String? = ""
) : ViewTypeListener {

    var userId: String? = null

    override fun getRecyclerItemLayoutId(): Int {
        return if (fromId == FirebaseAuth.getInstance().currentUser?.uid)
            R.layout.item_chat_right
        else
            R.layout.item_chat_left
    }
}
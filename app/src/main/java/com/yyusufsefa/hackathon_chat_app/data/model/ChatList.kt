package com.yyusufsefa.hackathon_chat_app.data.model

import com.google.firebase.database.IgnoreExtraProperties
import com.ramo.sweetrecycleradapter.ViewTypeListener
import com.yyusufsefa.hackathon_chat_app.R

@IgnoreExtraProperties
data class ChatList(
    var userId: String? = null
)

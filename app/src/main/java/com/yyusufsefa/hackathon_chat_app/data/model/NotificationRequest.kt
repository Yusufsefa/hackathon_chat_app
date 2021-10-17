package com.yyusufsefa.hackathon_chat_app.data.model

import com.google.gson.annotations.SerializedName

data class NotificationRequest(
    @SerializedName("notification") var notification: NotificationModel,
    @SerializedName("data") var data: ChatMessage,
    @SerializedName("to") var token: String = "",
)
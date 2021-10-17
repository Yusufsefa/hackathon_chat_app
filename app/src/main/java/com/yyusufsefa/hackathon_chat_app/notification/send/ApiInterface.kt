package com.yyusufsefa.hackathon_chat_app.notification.send

import com.yyusufsefa.hackathon_chat_app.Constant
import com.yyusufsefa.hackathon_chat_app.data.model.NotificationRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface ApiInterface {
    @Headers(*["Authorization: key=" + Constant.SERVER_KEY, "Content-Type:application/json"])
    @POST("fcm/send")
    fun sendNotification(@Body root: NotificationRequest): Call<ResponseBody>
}
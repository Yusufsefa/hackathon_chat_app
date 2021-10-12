package com.yyusufsefa.hackathon_chat_app.data.model

import com.google.firebase.database.IgnoreExtraProperties
import com.ramo.sweetrecycleradapter.ViewTypeListener
import com.yyusufsefa.hackathon_chat_app.R

@IgnoreExtraProperties
data class User(
    var email: String? = "",
    var name: String? = "",
    var lastName: String? = ""
): ViewTypeListener{
    var userId: String? = null

    override fun getRecyclerItemLayoutId(): Int{
        return R.layout.item_user
    }
}
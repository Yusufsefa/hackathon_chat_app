package com.yyusufsefa.hackathon_chat_app.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.yyusufsefa.hackathon_chat_app.MainActivity
import com.yyusufsefa.hackathon_chat_app.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Looper.getMainLooper()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            val i = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }, 1000)
    }
}
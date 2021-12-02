package com.example.projetoam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
<<<<<<< HEAD
=======
import kotlinx.android.synthetic.main.activity_splash_screen.*
>>>>>>> Rui

class SplashScreen : AppCompatActivity() {

    private val SPLASH_TIME: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({

            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        },SPLASH_TIME)
<<<<<<< HEAD
=======

        imageIpca.alpha = 0f

        imageIpca.animate().setDuration(2000).alpha(1f).withEndAction{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
>>>>>>> Rui
    }
}
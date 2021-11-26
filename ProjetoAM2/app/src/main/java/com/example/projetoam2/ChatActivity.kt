package com.example.projetoam2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //action bar title, name of the user

        var otherUserName = ""
        var otherUserId = ""
        val bundle = intent.extras

        bundle?.let {
            otherUserName = it.getString("name").toString()
            otherUserId = it.getString("uid").toString()
        }
        supportActionBar?.title = otherUserName


    }
}
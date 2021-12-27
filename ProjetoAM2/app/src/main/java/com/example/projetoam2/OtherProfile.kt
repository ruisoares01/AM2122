package com.example.projetoam2


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class OtherProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_profile)

        //hide action bar
        supportActionBar?.hide()

        var otherUserName = ""
        var otherUserId = ""
        var linkfoto = ""
        val bundle = intent.extras

        //collect data
        bundle?.let {
            otherUserName = it.getString("name").toString()
            otherUserId = it.getString("uid").toString()
            linkfoto = it.getString("linkfoto").toString()
        }

        val imgprofile = findViewById<CircleImageView>(R.id.imgProfile)
        Picasso.get().load(linkfoto).into(imgprofile)

        val nameProfile = findViewById<TextView>(R.id.profileName)
        nameProfile.text = otherUserName



    }



}
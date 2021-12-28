package com.example.projetoam2


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
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
        var otherUserEmail = ""
        var otherUserN = ""
        var otherUserCurso = ""
        var otherUserMorada = ""
        val bundle = intent.extras

        //collect data
        bundle?.let {
            otherUserName = it.getString("name").toString()
            otherUserId = it.getString("uid").toString()
            otherUserEmail = it.getString("email").toString()
            otherUserN = it.getString("nAluno").toString()
            otherUserCurso = it.getString("curso").toString()
            otherUserMorada = it.getString("morada").toString()
            linkfoto = it.getString("linkfoto").toString()

        }

        val imgprofile = findViewById<CircleImageView>(R.id.imgProfile)
        Picasso.get().load(linkfoto).into(imgprofile)

        val nameProfile = findViewById<TextView>(R.id.txtProfileName)
        nameProfile.text = otherUserName
        val emailProfile = findViewById<TextView>(R.id.profileEmail)
        emailProfile.text = otherUserEmail
        val nProfile = findViewById<TextView>(R.id.profilenAluno)
        nProfile.text = otherUserN
        val cursoProfile = findViewById<TextView>(R.id.profileCurso)
        cursoProfile.text = otherUserCurso
        val moradaProfile = findViewById<TextView>(R.id.profileMorada)
        moradaProfile.text = otherUserMorada




    }



}
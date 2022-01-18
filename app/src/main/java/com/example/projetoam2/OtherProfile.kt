package com.example.projetoam2


import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class OtherProfile : AppCompatActivity() {

    //firestore
    val db = Firebase.firestore

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
        var otherUserStatus = false
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
            otherUserStatus = it.getBoolean("status")

        }

        val profileOnline = findViewById<ImageButton>(R.id.online_status_perfil)
        if(otherUserStatus == true){
            profileOnline.setVisibility(View.VISIBLE)
            profileOnline.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00b026")))
        }
        else{
            profileOnline.setVisibility(View.VISIBLE)
            profileOnline.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY))
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

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
          //  val intent = Intent(this, ChatActivity::class.java)
            finish()
          //  startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, false)

        db.collection("usuarios").document(uid.toString()).set(user)
            .addOnSuccessListener {
                println("Online")
            }
    }
    override fun onResume() {
        super.onResume()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, true)
        db.collection("usuarios").document(uid.toString()).set(user)
            .addOnSuccessListener {
                println("Offline")
            }
    }
}
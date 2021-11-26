package com.example.projetoam2.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import com.example.projetoam2.Model.User
import com.example.projetoam2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class Profile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var botaofoto : ImageView
    var selectedPhotoUri: Uri? = null
    var linkfoto : String = ""
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //hide action bar
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        var imgprofile = findViewById<CircleImageView>(R.id.imgProfile)
        Picasso.get().load(dados.linkfoto).into(imgprofile)

        val profileName = findViewById<TextView>(R.id.profileName)
        val profileEmail = findViewById<TextView>(R.id.profileEmail)
        val uid = findViewById<TextView>(R.id.profileUid)
        botaofoto = findViewById(R.id.imgPickImage)
        profileName.text = dados.nome
        profileEmail.text = dados.email
        uid.text = dados.uid


        botaofoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        botaofoto = findViewById(R.id.imgPickImage)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            botaofoto.setImageBitmap(bitmap)

            botaofoto.alpha = 0f

            uploadImageToFirebaseStorage()

        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/imagens/$filename")
        val uid = dados.uid

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    linkfoto = it.toString()

                    var imgprofile = findViewById<CircleImageView>(R.id.imgProfile)
                    Picasso.get().load(linkfoto).into(imgprofile)

                    val user = User(dados.uid, dados.nome, dados.email,linkfoto)

                   db.collection("usuarios").document(uid).set(user).addOnSuccessListener {

                   }
                }
            }
            .addOnFailureListener {

            }
    }


}

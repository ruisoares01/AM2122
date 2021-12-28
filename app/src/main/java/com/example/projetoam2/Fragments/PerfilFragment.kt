package com.example.projetoam2.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.projetoam2.Model.User
import com.example.projetoam2.R
import com.example.projetoam2.dados
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class PerfilFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    lateinit var botaofoto : ImageView
    lateinit var selectedPhotoUri: Uri
    lateinit var linkfoto: String
    private val db = Firebase.firestore

    private lateinit var perfilName : TextView
    private lateinit var perfilEmail : TextView
    private lateinit var uid : TextView

    lateinit var circleImageView: CircleImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //hide action bar
        //supportActionBar?.hide()

        val resolver = activity?.contentResolver

        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        auth = FirebaseAuth.getInstance()
        var imgprofile = view.findViewById<CircleImageView>(R.id.imgProfile)
        Picasso.get().load(dados.linkfoto).into(imgprofile)


        perfilName = view.findViewById<TextView>(R.id.profileName)
        perfilEmail = view.findViewById<TextView>(R.id.profileEmail)
        uid = view.findViewById<TextView>(R.id.profileUid)

        botaofoto = view.findViewById(R.id.imgPickImage)
        perfilName.text = dados.nome
        perfilEmail.text = dados.email
        uid.text = dados.uid

        botaofoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)

          val getResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()
                ){
                    if (it.resultCode == Activity.RESULT_OK) {

                        selectedPhotoUri = it.data!!.data!!

                        val bitmap = MediaStore.Images.Media.getBitmap(resolver, selectedPhotoUri)

                        botaofoto.setImageBitmap(bitmap)

                        botaofoto.alpha = 0f

                        uploadImageToFirebaseStorage()
                    }
                }

            getResult.launch(intent)
        }

        return view
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

                    Picasso.get().load(linkfoto).into(circleImageView)

                    val user = User(dados.uid,  dados.nome, dados.email, linkfoto)

                    db.collection("usuarios").document(uid).set(user).addOnSuccessListener {

                    }
                }
            }
            .addOnFailureListener {

            }
    }
}

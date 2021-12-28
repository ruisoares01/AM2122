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
import androidx.appcompat.app.AppCompatActivity





class PerfilFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    lateinit var botaofoto : ImageView
    var selectedPhotoUri: Uri? = null
    var linkfoto : String = ""
    val db = Firebase.firestore

    private lateinit var circleImageView: CircleImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //hide action bar
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        val resolver = activity?.contentResolver

        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        auth = FirebaseAuth.getInstance()
        val imgprofile = view.findViewById<CircleImageView>(R.id.imgProfile)
        Picasso.get().load(dados.linkfoto).into(imgprofile)

        val profileName = view.findViewById<TextView>(R.id.txtProfileName)
        val profileEmail = view.findViewById<TextView>(R.id.profileEmail)
        val profileAluno = view.findViewById<TextView>(R.id.profilenAluno)
        val profileCurso = view.findViewById<TextView>(R.id.profileCurso)
        val profileMorada = view.findViewById<TextView>(R.id.profileMorada)
        botaofoto = view.findViewById(R.id.imgPickImage)
        profileName.text = dados.nome
        profileEmail.text = dados.email
        profileAluno.text = dados.naluno
        profileCurso.text = dados.curso
        profileMorada.text = dados.morada


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

                    val user = User(dados.uid,  dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, linkfoto)

                    db.collection("usuarios").document(uid).set(user).addOnSuccessListener {

                    }
                }
            }
            .addOnFailureListener {

            }
    }
}

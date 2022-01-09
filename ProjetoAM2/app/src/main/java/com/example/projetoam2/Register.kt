package com.example.projetoam2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class Register : AppCompatActivity() {

    // initiating private lateinit var to use it later in other functions
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var editName : EditText
    private lateinit var editEmail : EditText
    private lateinit var editPass : EditText
    private lateinit var editnAluno : EditText
    private lateinit var editCurso : EditText
    private lateinit var editMorada : EditText

    private lateinit var buttonRegister : Button
    lateinit var botaofoto : ImageView
    var selectedPhotoUri: Uri? = null
    var linkfoto : String = ""


    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //hide action bar
        supportActionBar?.hide()

        // Initialize Firebase Auth
        auth = Firebase.auth


        buttonRegister = findViewById(R.id.buttonRegister)


        // logic to make the register button register the user in the app
        buttonRegister.setOnClickListener {
            register()
        }

        botaofoto = findViewById(R.id.imgPickImage)

        botaofoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    // function for register
    private fun register() {

        // find the view created in the xml files
        editName = findViewById(R.id.editTextName)
        editEmail = findViewById(R.id.editTextEmail)
        editPass = findViewById(R.id.editTextPassword)
        editnAluno = findViewById(R.id.editTextNaluno)
        editCurso = findViewById(R.id.editTextCurso)
        editMorada = findViewById(R.id.editTextMorada)

        // declaring the variables for the views
        val nome = editName.text.toString()
        val email = editEmail.text.toString()
        val password = editPass.text.toString()
        val nAluno = editnAluno.text.toString()
        val curso = editCurso.text.toString()
        val morada = editMorada.text.toString()
        val online = false


        // in this validation we are allowing the register method using an email, name and password
        if (editEmail.text.isNotEmpty() && editPass.text.isNotEmpty()) {

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = FirebaseAuth.getInstance().uid

                        addUserToDatabase(nome, email, nAluno, curso, morada, online,auth.currentUser?.uid!!)

                        val intent = Intent(this@Register, LoginActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this@Register, "Ocorreu um erro", Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            // if there are unfilled fields, the user gets a warning to fill it
            Toast.makeText(this@Register,"Preencha os campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUserToDatabase(nome: String, email: String, nAluno: String, curso: String, morada: String, online: Boolean, uid: String){
        var user : User

        user = User(uid,nome,email,nAluno,curso, morada, "https://firebasestorage.googleapis.com/v0/b/projetoam2.appspot.com/o/Avatar_icon_green.png?alt=media&token=8e6d8680-d431-4a98-a6a8-60b8000bb3da", online)
        db.collection("usuarios").document(uid).set(user)

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

                    val user = User(uid, dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, linkfoto, dados.online)

                    db.collection("usuarios").document(uid).set(user).addOnSuccessListener {
                        println("deu")
                    }

                }
            }
            .addOnFailureListener {

            }
    }

}
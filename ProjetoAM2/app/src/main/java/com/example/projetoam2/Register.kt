package com.example.projetoam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.projetoam2.Model.User
import com.example.projetoam2.databinding.ActivityLoginBinding
import com.example.projetoam2.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    // initiating private lateinit var to use it later in other functions
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var editName : EditText
    private lateinit var editEmail : EditText
    private lateinit var editPass : EditText

    private lateinit var buttonRegister : Button
    private lateinit var backButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //hide action bar
        supportActionBar?.hide()

        // Initialize Firebase Auth
        auth = Firebase.auth

        backButton = findViewById(R.id.buttonBack)

        // function to go back to the previous activity
        backButton.setOnClickListener {
            val intent = Intent(this@Register, LoginActivity::class.java)
            startActivity(intent)
        }

        buttonRegister = findViewById(R.id.buttonRegister)


        // logic to make the register button register the user in the app
        buttonRegister.setOnClickListener {
            register()
        }

    }

    // function for register
    private fun register() {

        // find the view created in the xml files
        editName = findViewById(R.id.editTextName)
        editEmail = findViewById(R.id.editTextEmail)
        editPass = findViewById(R.id.editTextPassword)

        // declaring the variables for the views
        val nome = editName.text.toString()
        val email = editEmail.text.toString()
        val password = editPass.text.toString()

        // in this validation we are allowing the register method using an email, name and password
        if (editEmail.text.isNotEmpty() && editPass.text.isNotEmpty()) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().uid
                    database =
                        FirebaseDatabase.getInstance("https://projetoam2-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("/Usuarios/$uid")

                     addUserToDatabase(nome, email, auth.currentUser?.uid!!)

                        val intent = Intent(this@Register, MainActivity::class.java)
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

    private fun addUserToDatabase(nome: String, email: String, uid:String){

        database = FirebaseDatabase.getInstance().getReference()
        database.child("Usuarios").child(uid).setValue(User(nome,email,uid))
    }

}
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

        auth = Firebase.auth

        backButton = findViewById(R.id.buttonBack)

        //voltar para a activity anterior
        backButton.setOnClickListener {
            val intent = Intent(this@Register, LoginActivity::class.java)
            startActivity(intent)
        }

        buttonRegister = findViewById(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            register()
        }

    }

    private fun register() {

        editName = findViewById(R.id.editTextName)
        editEmail = findViewById(R.id.editTextEmail)
        editPass = findViewById(R.id.editTextPassword)

        val nome = editName.text.toString()
        val email = editEmail.text.toString()
        val password = editPass.text.toString()

        if (editName.text.isNotEmpty() && editEmail.text.isNotEmpty() && editPass.text.isNotEmpty()) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().uid
                    database =
                        FirebaseDatabase.getInstance("https://projetoam2-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("/Usuarios/$nome")

                    val utilizadores = User(nome.toString(), email.toString(), uid.toString())

                    database.setValue(utilizadores).addOnSuccessListener {
                        val intent = Intent(this@Register, MainActivity::class.java)
                        startActivity(intent)
                    }


                } else {
                    Toast.makeText(this@Register, "Ocorreu um erro", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this@Register,"Preencha os campos", Toast.LENGTH_SHORT).show()
        }
    }
}
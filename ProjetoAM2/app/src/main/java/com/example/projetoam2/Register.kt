package com.example.projetoam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var buttonRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editEmail.findViewById<EditText>(R.id.editTextEmail)
        editPassword.findViewById<EditText>(R.id.editTextPassword)
        buttonRegister.findViewById<Button>(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            register(email,password)
        }

    }

    private fun register(email: String, password: String){

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // code to jump to home screen
                    val intent = Intent(this@Register, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(this@Register, "Ocorreu um erro", Toast.LENGTH_SHORT).show()

                }
            }

    }


}
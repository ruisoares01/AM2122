package com.example.projetoam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.projetoam2.Model.User
import com.example.projetoam2.databinding.ActivityLoginBinding
import com.example.projetoam2.databinding.ActivityUserRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        var database = FirebaseDatabase.getInstance("https://projetoam2-default-rtdb.europe-west1.firebasedatabase.app").reference

        binding.buttonLogin.setOnClickListener {
            if(binding.editTextEmail.text.isNotEmpty() && binding.editTextTextPassword.text.isNotEmpty()) {
                val email: String = binding.editTextEmail.text.toString()
                val password: String = binding.editTextTextPassword.text.toString()

                database.setValue(User(email, password))

                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(applicationContext,"Insira os dados necessários",Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {

            startActivity(Intent(this@LoginActivity, UserRegister::class.java))
            finish()
        }

            /*auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java ))
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }*/

    }
}
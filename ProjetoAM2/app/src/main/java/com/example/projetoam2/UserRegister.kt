package com.example.projetoam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projetoam2.Model.User
import com.example.projetoam2.databinding.ActivityLoginBinding
import com.google.firebase.database.FirebaseDatabase

class UserRegister : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var database = FirebaseDatabase.getInstance("https://projetoam2-default-rtdb.europe-west1.firebasedatabase.app").reference

        binding.buttonRegister.setOnClickListener {

            val email: String = binding.editTextEmail.text.toString()
            val password: String = binding.editTextTextPassword.text.toString()

            database.setValue(User(email, password))

            startActivity(Intent(this@UserRegister, MainActivity::class.java))
            finish()
        }
    }
}
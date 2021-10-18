package com.example.projetoam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.projetoam2.Model.User
import com.example.projetoam2.databinding.ActivityLoginBinding
import com.example.projetoam2.databinding.ActivityUserRegisterBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserRegister : AppCompatActivity() {

    private lateinit var binding: ActivityUserRegisterBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //var database = FirebaseDatabase.getInstance("https://projetoam2-default-rtdb.europe-west1.firebasedatabase.app").reference

        binding.buttonRegister.setOnClickListener {

            if (binding.editTextNome.text.isNotEmpty() && binding.editTextEmail.text.isNotEmpty() && binding.editTextTextPassword.text.isNotEmpty()) {

                val nome : String = binding.editTextNome.text.toString()
                val email: String = binding.editTextEmail.text.toString()
                val password: String = binding.editTextTextPassword.text.toString()

                database = FirebaseDatabase.getInstance().getReference("Usuários")

                val User = User(nome, email, password)
                database.child(nome).setValue(User).addOnSuccessListener {

                    binding.editTextNome.text.clear()
                    binding.editTextEmail.text.clear()
                    binding.editTextTextPassword.text.clear()

                    Toast.makeText(applicationContext,"Sucesso",Toast.LENGTH_SHORT).show()
                }.addOnCanceledListener {
                    Toast.makeText(applicationContext,"Erro",Toast.LENGTH_SHORT).show()
                }

                startActivity(Intent(this@UserRegister, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(applicationContext,"Insira os dados necessários", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
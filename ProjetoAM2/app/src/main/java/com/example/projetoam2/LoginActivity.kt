package com.example.projetoam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.projetoam2.Model.User
import com.example.projetoam2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegister: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        editEmail.findViewById<EditText>(R.id.editTextEmail)
        editPassword.findViewById<EditText>(R.id.editTextPassword)
        buttonLogin.findViewById<Button>(R.id.buttonRegister)
        buttonRegister.findViewById<Button>(R.id.buttonRegister)


        buttonLogin.setOnClickListener {
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            login(email,password)
        }



        //var database = FirebaseDatabase.getInstance("https://projetoam2-default-rtdb.europe-west1.firebasedatabase.app").reference

    /*    binding.buttonLogin.setOnClickListener {
            if(binding.editTextEmail.text.isNotEmpty() && binding.editTextTextPassword.text.isNotEmpty()) {
                val email: String = binding.editTextEmail.text.toString()
                val password: String = binding.editTextTextPassword.text.toString()


                database = FirebaseDatabase.getInstance().getReference("Usuários")

                val User = User(email,password)

                database.child(email).setValue(User).addOnSuccessListener {

                    binding.editTextEmail.text.clear()
                    binding.editTextTextPassword.text.clear()

                    Toast.makeText(this,"Bem vindo",Toast.LENGTH_SHORT).show()
                } .addOnCanceledListener {
                    Toast.makeText(this,"Erro",Toast.LENGTH_SHORT).show()
                }

                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(applicationContext,"Insira os dados necessários",Toast.LENGTH_SHORT).show()
            }
        }       */

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, Register::class.java))
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


    private fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // code to login user
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(this@LoginActivity, "O utilizador não existe", Toast.LENGTH_SHORT).show()

                }
            }

    }
}
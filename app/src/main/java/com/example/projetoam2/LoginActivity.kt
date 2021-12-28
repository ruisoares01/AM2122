package com.example.projetoam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    var user = arrayListOf<User>()

    private lateinit var editEmail: EditText
    private lateinit var editPass: EditText

    private lateinit var buttonLogin: Button
    private lateinit var buttonUserRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //hide action bar
        supportActionBar?.hide()

        // Initialize Firebase Auth
        auth = Firebase.auth

        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a0-9]{1,256}@alunos.ipca.pt"
        )

        fun isValidString(str: String): Boolean {
            return EMAIL_ADDRESS_PATTERN.matcher(str.toString()).matches()
        }

        // find the view created in the xml files
        editEmail = findViewById(R.id.editTextEmail)
        editPass = findViewById(R.id.editTextTextPassword)

        buttonLogin = findViewById(R.id.buttonLogin)

        // logic to make the login button log the user in the app
        buttonLogin.setOnClickListener {

            register()
        }


        buttonUserRegister = findViewById(R.id.buttonUserRegister)

        buttonUserRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, Register::class.java))
            finish()
        }

    }
        // function for login
        private fun register() {

            // declaring the variables for the views
            val email = editEmail.text.toString()
            val password = editPass.text.toString()

            val emails = arrayOf<String>(editEmail.text.toString())

            // in this validation we are allowing the login method using an email and password
            if (editEmail.text.isNotEmpty() && editPass.text.isNotEmpty()) {

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // code to login user
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "O utilizador não existe",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                // if there are unfilled fields, the user gets a warning to fill it
                Toast.makeText(this@LoginActivity, "Preencha os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
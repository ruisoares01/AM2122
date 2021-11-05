package com.example.projetoam2

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.User
import com.example.projetoam2.Adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListarPerfis : AppCompatActivity() {

    private lateinit var backButton: ImageButton

    var user = arrayListOf<User>()

    private lateinit var auth: FirebaseAuth

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var adapter: UserAdapter

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_perfis)

        //hide action bar
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        user = ArrayList()
        adapter = UserAdapter(this, user)

        userRecyclerView = findViewById(R.id.userRecyclerVieww)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        db.collection("usuarios")
            .addSnapshotListener { documents, e ->

                documents?.let {
                    user.clear()
                    for (document in it) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val userr = User.fromHash(document)

                        //hide your own name within the application
                        if(auth.currentUser?.uid != userr.uid) {
                            user.add(userr)
                        }

                        adapter?.notifyDataSetChanged()
                    }
                }

                backButton = findViewById(R.id.buttonBack)

                //back to the main activity
                backButton.setOnClickListener {
                    val intent = Intent(this@ListarPerfis, MainActivity::class.java)
                    startActivity(intent)
                }
            }
    }
}
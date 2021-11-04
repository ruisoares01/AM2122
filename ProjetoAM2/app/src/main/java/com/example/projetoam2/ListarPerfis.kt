package com.example.projetoam2

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.User
import com.example.projetoam2.Model.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ListarPerfis : AppCompatActivity() {

    private lateinit var backButton: ImageButton

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_perfis)

        //hide action bar
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        adapter = UserAdapter(this,userList)

        userRecyclerView = findViewById(R.id.userRecyclerVieww)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        database.child("Usuarios").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(postSnapshot in snapshot.children){

                    val currentUser = postSnapshot.getValue(User::class.java)

                    //hide your own name within the application
                    if(auth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        backButton = findViewById(R.id.buttonBack)

        //back to the main activity
        backButton.setOnClickListener {
            val intent = Intent(this@ListarPerfis, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
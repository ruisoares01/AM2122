package com.example.projetoam2.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.User
import com.example.projetoam2.Model.UserAdapter
import com.example.projetoam2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ListarPerfis : AppCompatActivity() {

    private lateinit var backButton: ImageButton

    private lateinit var auth: FirebaseAuth

    lateinit var chats : String

    private val adapter = GroupAdapter<GroupieViewHolder>()

    //firestore
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_perfis)

        //hide action bar
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        var userRecyclerView = findViewById<RecyclerView>(R.id.userRecyclerVieww)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        db.collection("usuarios").get().addOnSuccessListener{
                documents ->
            //get all the documents
            for (document in documents) {
                val user = document.toObject(User::class.java)
                if(auth.currentUser?.uid != user.uid) {
                    adapter.add(Users(user))
                }
            }
            adapter.setOnItemClickListener{ item,view ->
                val utilizador = item as Users
                val intent = Intent(view.context,ChatActivity::class.java)

                intent.putExtra("name", utilizador.user.nome)
                intent.putExtra("uid", utilizador.user.uid)

                startActivity(intent)
            }
        }
        /*
        db.collection("salaChat").get().addOnSuccessListener {
            documents ->

            adapter.setOnItemClickListener{ item, view ->
                val chat = item as Sender

                var bundle : Bundle? = intent.extras
                chats = bundle?.getString("chat").toString()
            }
        }*/

        backButton = findViewById(R.id.buttonBack)
        //back to the main activity
        backButton.setOnClickListener {
            val intent = Intent(this@ListarPerfis, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
class Users(val user : User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var nome = viewHolder.itemView.findViewById<TextView>(R.id.text_name)
        nome.text = user.nome
    }

    override fun getLayout() = R.layout.user_layout
}
package com.example.projetoam2.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.projetoam2.ChatActivity
import com.example.projetoam2.Model.User
import com.example.projetoam2.R
import com.example.projetoam2.item.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.*

class HomeFragment : Fragment() {

    //variaveis
    private lateinit var auth: FirebaseAuth

    private val adapter = GroupAdapter<ViewHolder>()

    //firestore
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =inflater.inflate(R.layout.fragment_home, container, false)
        var userRecyclerView = view.findViewById<RecyclerView>(R.id.userRecyclerVieww)

        // Initialize Firebase Auth
        auth = Firebase.auth

        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.adapter = adapter

        //clear the list
        adapter.clear()

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
                val intent = Intent(view.context, ChatActivity::class.java)

                intent.putExtra("name", utilizador.user.nome)
                intent.putExtra("uid", utilizador.user.uid)

                startActivity(intent)
            }
        }

        // Inflate the layout for this fragment
        return view
    }
}
class Users(val user : User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var nome = viewHolder.itemView.findViewById<TextView>(R.id.text_name)
        nome.text = user.nome
    }

    override fun getLayout() = R.layout.user_layout
}
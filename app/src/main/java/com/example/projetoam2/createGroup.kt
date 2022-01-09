package com.example.projetoam2

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.GroupChannel
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.projetoam2.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.db.TEXT
import org.jetbrains.anko.toast


class createGroup : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()

    val db = FirebaseFirestore.getInstance()

    val adapter = GroupAdapter<ViewHolder>()

    val userIds : ArrayList<String> = ArrayList()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        val button :Button = findViewById(R.id.button)

        val text :EditText = findViewById(R.id.editText)

        val rView :RecyclerView = findViewById(R.id.rView)

        rView.adapter = adapter

        adapter.setOnItemClickListener{ item,view ->

            val row = item as Users

            view.isSelected = !view.isSelected

            if(view.isSelected){

                view.setBackgroundColor(R.color.black)
                userIds.add(row.user.uid)

            }
            else {

                //view.setBackgroundColor(R.color.black)
                view.setBackgroundColor(Color.parseColor("#F5FFF5"))

                userIds.forEachIndexed { index, s ->

                    if(row.user.uid == s){
                        userIds.removeAt(index)
                    }

                }

            }

            adapter.notifyDataSetChanged()
        }

        val refUsers = db.collection("usuarios")

        refUsers.get().addOnSuccessListener { result ->
            for (doc in result.documents) {
                val user = doc.toObject(User::class.java)

                if(auth.currentUser?.uid != user!!.uid) {

                    adapter.add(Users(user))
                }
            }
        }

        button.setOnClickListener {

            if (text.text.isEmpty()) {
                Toast.makeText(this, "Não intrudoziu o nome do grupo!!!", Toast.LENGTH_SHORT).show()

            } else {

                val nome = text.text

                val imagemUrl = "https://firebasestorage.googleapis.com/v0/b/projetoam2.appspot.com/o/imagens%2F0fd24a87-038a-4e3c-ab01-8b9087c8959c?alt=media&token=518b667b-c0e3-41cd-bae3-98cef40c9669"

                val group = GroupChannel(nome.toString(), imagemUrl, userIds)

                val refCreateGroup = db.collection("grupos")

                userIds.add(auth.currentUser!!.uid)

                refCreateGroup.add(group).addOnSuccessListener { result ->

                    userIds.forEach {

                        if(it == auth.currentUser!!.uid)
                        {
                            db.collection("usuarios")
                                .document(it)
                                .collection("gruposIds")
                                .document(result.id)
                                .set(mapOf("admin" to true))
                        }
                        else
                        {
                            db.collection("usuarios")
                                .document(it)
                                .collection("gruposIds")
                                .document(result.id)
                                .set(mapOf("admin" to false))
                        }
                    }
                }
            }
        }
    }
}

class Users(val user : User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val nome = viewHolder.itemView.findViewById<TextView>(R.id.text_name)
        nome.text = user.nome

        var imgprofile = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView3)
        Picasso.get().load(user.linkfoto).into(imgprofile)
    }


    override fun getLayout() = R.layout.user_layout
}
package com.example.projetoam2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.GroupChannel
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class AddUserGroupActivity: AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()

    val db = FirebaseFirestore.getInstance()

    val adapter = GroupAdapter<ViewHolder>()

    val userIds : ArrayList<String> = ArrayList()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_group)

        supportActionBar?.hide()

        val recyclerAddUser : RecyclerView = findViewById(R.id.recyclerAddUser)

        recyclerAddUser.adapter = adapter

        val cancelAddUser = findViewById<Button>(R.id.buttonCancelAddGroupUsers)
        val buttonAddUser = findViewById<Button>(R.id.buttonAddGroupUsers)

        var allgroupusers : java.util.ArrayList<String>? = intent.extras?.getStringArrayList("allgroupusers")
        var groupId : String = intent.extras?.get("groupId") as String


        db.collection("usuarios").get().addOnSuccessListener { result ->
            for (doc in result.documents) {
                val user = doc.toObject(User::class.java)
                    if(allgroupusers!!.contains(user!!.uid)){

                    }
                    else{
                        adapter.add(AddUsers(user))
                        adapter.notifyDataSetChanged()
                    }
            }
        }

        adapter.setOnItemClickListener{ item,view ->

            val row = item as AddUsers

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

        }



        cancelAddUser.setOnClickListener { finish() }

        buttonAddUser.setOnClickListener {
            if(userIds.isEmpty()){
                Toast.makeText(this, "Não selecionou nenhum utilizador", Toast.LENGTH_SHORT).show()
            }
            else{
                db.collection("grupos").document(groupId).get().addOnSuccessListener { result ->
                userIds.forEach {
                    db.collection("usuarios")
                        .document(it)
                        .collection("gruposIds")
                        .document(result.id)
                        .set(mapOf("admin" to false))
                    }
                }

                allgroupusers!!.addAll(userIds)
                db.collection("grupos").document(groupId).update(mapOf("userIds" to allgroupusers)).addOnSuccessListener {

                }
                finish()
            }
        }
    }
}

class AddUsers(val user : User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val nome = viewHolder.itemView.findViewById<TextView>(R.id.text_name)
        nome.text = user.nome

        var imgprofile = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageViewUser)
        Picasso.get().load(user.linkfoto).into(imgprofile)
    }


    override fun getLayout() = R.layout.user_layout
}


package com.example.projetoam2

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Fragments.Users
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*

class GroupProfile : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()

    private val adapter = GroupAdapter<ViewHolder>()

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_profile)

        //hide action bar
        supportActionBar?.hide()

        var userRecyclerView = findViewById<RecyclerView>(R.id.groupRecView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        var useringroup: ArrayList<String> = arrayListOf()
        var groupName = ""
        var groupId = ""
        var linkfoto = ""

        val bundle = intent.extras

        //intent.extras.getString("nomeGrupo", groupName)

        //collect data
        bundle?.let {
            groupName = it.getString("name").toString()
            groupId = it.getString("uid").toString()
            linkfoto = it.getString("linkfoto").toString()

        }

        val groupImg = findViewById<CircleImageView>(R.id.groupImg1)
        Picasso.get().load(linkfoto).into(groupImg)

        val groupNameTextView = findViewById<TextView>(R.id.groupNames)
        groupNameTextView.text = groupName

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            //  val intent = Intent(this, ChatActivity::class.java)
            finish()
            //  startActivity(intent)
        }

        db.collection("grupos").document(groupId).get().addOnSuccessListener { chatcontent ->
            useringroup.addAll((chatcontent.get("userIds") as ArrayList<String>).filter { it != auth.currentUser!!.uid })
            println("Users in group : ${useringroup}")

            for (user in useringroup) {
                db.collection("usuarios").document(user).get().addOnSuccessListener {
                    val userInfo = it.toObject(User::class.java)
                    if (auth.currentUser?.uid != userInfo!!.uid) {
                        adapter.add(GroupUsers(userInfo))
                    }
                }
            }
            adapter.setOnItemClickListener { item, view ->
                val utilizadorGrupo = item as GroupUsers
                val intent = Intent(view.context, OtherProfile::class.java)

                intent.putExtra("name", utilizadorGrupo.user.nome)
                intent.putExtra("uid", utilizadorGrupo.user.uid)
                intent.putExtra("email", utilizadorGrupo.user.email)
                intent.putExtra("linkfoto", utilizadorGrupo.user.linkfoto)
                intent.putExtra("nAluno", utilizadorGrupo.user.naluno)
                intent.putExtra("curso", utilizadorGrupo.user.curso)
                intent.putExtra("morada", utilizadorGrupo.user.morada)

                startActivity(intent)
            }

        }

        backButton.setOnClickListener() {
            finish()
        }

    }
}

class GroupUsers(val user : User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var nome = viewHolder.itemView.findViewById<TextView>(R.id.text_name)
        nome.text = user.nome

        var imgprofile = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView3)
        Picasso.get().load(user.linkfoto).into(imgprofile)

        var online_status = viewHolder.itemView.findViewById<ImageButton>(R.id.online_status)
        if(user.online == true){
            online_status.setVisibility(View.VISIBLE)
            online_status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#097320")))}
        else if(user.online == false){
            online_status.setVisibility(View.VISIBLE)
            online_status.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY))}
        else{online_status.setVisibility(View.INVISIBLE)}

    }

    override fun getLayout() = R.layout.user_layout
}
package com.example.projetoam2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.GroupChannel
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList


class createGroup : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()

    val db = FirebaseFirestore.getInstance()

    val adapter = GroupAdapter<ViewHolder>()

    val userIds: ArrayList<String> = ArrayList()

    var linkfoto: String = ""

    private lateinit var circleImageView: CircleImageView

    var selectedPhotoUri: Uri? = null

    var groupId: String = ""

    var imagemUrl =
        "https://firebasestorage.googleapis.com/v0/b/projetoam2.appspot.com/o/Group_icon_green.png?alt=media&token=7afd2cbe-4391-411b-818b-ceea980866ee"


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        supportActionBar?.hide()

        val buttonCreateGroup : Button = findViewById(R.id.buttonCreateGroup)

        val buttonCancelCreateGroup: Button = findViewById(R.id.buttonCancelCreateGroup)

        val text: EditText = findViewById(R.id.editText)

        val rView: RecyclerView = findViewById(R.id.rView)

        val foto: CircleImageView = findViewById(R.id.imgCreateGroup)

        val resolver = applicationContext.contentResolver

        circleImageView = findViewById(R.id.imgCreateGroup)

        Picasso.get().load(imagemUrl).into(circleImageView)

        rView.adapter = adapter

        adapter.setOnItemClickListener { item, view ->

            val row = item as Users

            view.isSelected = !view.isSelected

            if (view.isSelected) {

                view.setBackgroundColor(R.color.black)
                userIds.add(row.user.uid)

            } else {

                //view.setBackgroundColor(R.color.black)
                view.setBackgroundColor(Color.parseColor("#F5FFF5"))

                userIds.forEachIndexed { index, s ->

                    if (row.user.uid == s) {
                        userIds.removeAt(index)
                    }

                }

            }

            adapter.notifyDataSetChanged()
        }

        val getResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {

                    selectedPhotoUri = it.data!!.data!!

                    val bitmap = MediaStore.Images.Media.getBitmap(resolver, selectedPhotoUri)

                    foto.setImageBitmap(bitmap)


                    uploadImageProfileFirebaseStorage()
                }
            }

        foto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)

            getResult.launch(intent)

        }

        val refUsers = db.collection("usuarios")

        refUsers.get().addOnSuccessListener { result ->
            for (doc in result.documents) {
                val user = doc.toObject(User::class.java)

                if (auth.currentUser?.uid != user!!.uid) {

                    adapter.add(Users(user))
                }
            }
        }

        buttonCancelCreateGroup.setOnClickListener { finish() }

        buttonCreateGroup.setOnClickListener {

            if (text.text.isEmpty()) {
                Toast.makeText(this, "Não introduziu o nome do grupo", Toast.LENGTH_SHORT).show()
            }
            else if(userIds.isEmpty()){
                Toast.makeText(this, "Não selecionou nenhum utilizador", Toast.LENGTH_SHORT).show()
            }
            else{

                val nome = text.text

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

                finish()
            }
        }
    }

    private fun uploadImageProfileFirebaseStorage() {


        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/imagens/$filename")
        val uid = dados.uid


        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    imagemUrl = it.toString()

                    Picasso.get().load(imagemUrl).into(circleImageView)
                }
            }
            .addOnFailureListener {

            }
    }
}

    class Users(val user: User) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val nome = viewHolder.itemView.findViewById<TextView>(R.id.text_name)
            nome.text = user.nome

            var imgprofile = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageViewUser)
            Picasso.get().load(user.linkfoto).into(imgprofile)
        }


        override fun getLayout() = R.layout.user_layout
    }
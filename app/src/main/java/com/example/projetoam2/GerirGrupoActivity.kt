package com.example.projetoam2

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GerirGrupoActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()

    private val adapter = GroupAdapter<ViewHolder>()

    val db = Firebase.firestore

    var selectedPhotoUri: Uri? = null

    var deleteUser: ArrayList<String> = arrayListOf()

    lateinit var botaofoto : ImageView

    var linkfoto : String = ""

    private lateinit var circleImageView: CircleImageView

    var groupId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerir_grupo)

        //hide action bar
        supportActionBar?.hide()

        val resolver = applicationContext.contentResolver

        botaofoto = findViewById(R.id.imgPickImage)

        var userRecyclerView = findViewById<RecyclerView>(R.id.listaGerirRecycler)

        circleImageView = findViewById(R.id.imgGroupProfile)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        var useringroup: ArrayList<String> = arrayListOf()
        var groupName = ""
        var linkfoto = ""

        val bundle = intent.extras

        //intent.extras.getString("nomeGrupo", groupName)

        //collect data
        bundle?.let {
            groupName = it.getString("groupName").toString()
            groupId = it.getString("groupID").toString()
            linkfoto = it.getString("linkfoto").toString()

        }

        val groupImg = findViewById<CircleImageView>(R.id.imgGroupProfile)
        Picasso.get().load(linkfoto).into(groupImg)

        val groupNameTextView = findViewById<TextView>(R.id.txtProfileGroup)
        groupNameTextView.text = groupName



        val getResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {

                    selectedPhotoUri = it.data!!.data!!

                    val bitmap = MediaStore.Images.Media.getBitmap(resolver, selectedPhotoUri)

                    botaofoto.setImageBitmap(bitmap)

                    botaofoto.alpha = 0f

                    uploadImageProfileFirebaseStorage()
                }
            }

        botaofoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)

            botaofoto = findViewById(R.id.imgPickImage)

            getResult.launch(intent)

        }

        db.collection("grupos").document(groupId).addSnapshotListener { chatcontent, error ->
            deleteUser.clear()
            adapter.clear()
            if (chatcontent != null) {
                deleteUser.addAll((chatcontent.get("userIds") as ArrayList<String>).filter { it != auth.currentUser!!.uid })
            }
            println("Users in group : ${deleteUser}")

            for (user in deleteUser) {
                db.collection("usuarios").document(user).get().addOnSuccessListener {
                    val userInfo = it.toObject(User::class.java)
                    if (auth.currentUser?.uid != userInfo!!.uid) {
                        adapter.add(GerirUsers(userInfo, groupId))

                    }
                }
            }
        }
    }

    private fun uploadImageProfileFirebaseStorage() {

        var imgGroupLink = hashMapOf(
            "linkfoto" to linkfoto
        )

        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/imagens/$filename")
        val uid = dados.uid


        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    linkfoto = it.toString()

                    imgGroupLink = hashMapOf(
                        "imagemGrupo" to it.toString()
                    )

                    Picasso.get().load(linkfoto).into(circleImageView)

                    db.collection("grupos").document(groupId).update(imgGroupLink as Map<String, Any>).addOnSuccessListener {

                    }
                }
            }
            .addOnFailureListener {

            }
    }

class GerirUsers(val user : User, val groupId : String) : Item<ViewHolder>() {

    val db = Firebase.firestore

    var deletarUser: ArrayList<String> = arrayListOf()


    override fun bind(viewHolder: ViewHolder, position: Int) {
        var nome = viewHolder.itemView.findViewById<TextView>(R.id.text_name)
        nome.text = user.nome

        var imgprofile = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView3)
        Picasso.get().load(user.linkfoto).into(imgprofile)

        var online_status = viewHolder.itemView.findViewById<ImageButton>(R.id.online_status)
        if (user.online == true) {
            online_status.setVisibility(View.VISIBLE)
            online_status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#097320")))
        } else if (user.online == false) {
            online_status.setVisibility(View.VISIBLE)
            online_status.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY))
        } else {
            online_status.setVisibility(View.INVISIBLE)
        }


        viewHolder.itemView.findViewById<Button>(R.id.buttonRetirar).setOnClickListener {
            db.collection("grupos").document(groupId).get().addOnSuccessListener { chatcontent ->
                deletarUser.addAll((chatcontent.get("userIds") as ArrayList<String>).filter { it != user.uid })
                val grupo = hashMapOf(
                    "userIds" to deletarUser
                )

                db.collection("grupos").document(groupId)
                    .update(grupo as Map<String, Any>)

                db.collection("usuarios").document(user.uid)
                    .collection("gruposIds")
                    .document(groupId)
                    .delete()

            }
        }

    }

    override fun getLayout() = R.layout.usergerir
}
}
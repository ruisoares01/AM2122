package com.example.projetoam2

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import eltos.simpledialogfragment.SimpleDialog
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext

lateinit var gerirgrupoupdate : ListenerRegistration

class GerirGrupoActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()

    private val adapter = GroupAdapter<ViewHolder>()

    val db = Firebase.firestore

    var selectedPhotoUri: Uri? = null

    var userRecyclerView: RecyclerView? = null

    var deleteUser: ArrayList<String> = arrayListOf()

    lateinit var botaofoto : ImageView

    var linkfoto : String = ""

    private lateinit var circleImageView: CircleImageView

    var groupId : String = ""


    var removerPessoaGroupId :String = ""
    var removerPessoaArrayList : ArrayList<String> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerir_grupo)

        //hide action bar
        supportActionBar?.hide()


        val resolver = applicationContext.contentResolver

        botaofoto = findViewById(R.id.imgPickImageGerir)

        userRecyclerView = findViewById(R.id.listaGerirRecycler)

        circleImageView = findViewById(R.id.imgGroupProfileGerir)

        var backbuttongerir = findViewById<ImageView>(R.id.backButtonGerir)

        userRecyclerView!!.layoutManager = LinearLayoutManager(this)
        userRecyclerView!!.adapter = adapter

        var groupName = ""
        var linkfoto = ""

        val bundle = intent.extras

        val buttonaddusergroup = findViewById<FloatingActionButton>(R.id.buttonAddUserGroup)


        //intent.extras.getString("nomeGrupo", groupName)

        //collect data
        bundle?.let {
            groupName = it.getString("groupName").toString()
            groupId = it.getString("groupID").toString()
            linkfoto = it.getString("linkfoto").toString()

        }

        val groupImg = findViewById<CircleImageView>(R.id.imgGroupProfileGerir)
        Picasso.get().load(linkfoto).into(groupImg)

        val groupNameTextView = findViewById<TextView>(R.id.txtProfileGroupGerir)
        groupNameTextView.text = groupName


        backbuttongerir.setOnClickListener {
            gerirgrupoupdate.remove()
            finish()
        }

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

            botaofoto = findViewById(R.id.imgPickImageGerir)

            getResult.launch(intent)

        }



        gerirgrupoupdate =
        db.collection("grupos").document(groupId).addSnapshotListener { chatcontent, error ->
            deleteUser.clear()
            adapter.clear()
            if (chatcontent != null) {
                deleteUser.addAll((chatcontent.get("userIds") as ArrayList<String>))
            }

            for (user in deleteUser) {
                db.collection("usuarios")
                    .document(user).collection("gruposIds")
                    .document(groupId).get().addOnSuccessListener { grupoadmin ->
                        var admin = grupoadmin.getBoolean("admin")
                        if(admin==null){
                            admin = false
                        }
                    db.collection("usuarios").document(user).get().addOnSuccessListener {
                        val userInfo = it.toObject(User::class.java)
                        if (auth.currentUser?.uid == userInfo!!.uid) {
                             adapter.add(0,GerirUsers(userInfo, groupId, admin))
                        }
                        else if(auth.currentUser?.uid != userInfo!!.uid) {
                             adapter.add(GerirUsers(userInfo, groupId, admin))
                        }
                    }
                }
            }
        }
        this.onBackPressedDispatcher.addCallback(this) {
            gerirgrupoupdate.remove()
        }

        buttonaddusergroup.setOnClickListener {
            val intent = Intent(this,AddUserGroupActivity::class.java)
            intent.putExtra("allgroupusers",deleteUser)
            intent.putExtra("groupId",groupId)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gerirgrupoupdate.remove()
    }



    private fun uploadImageProfileFirebaseStorage() {

        var imgGroupLink: HashMap<String, String>

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
    

class GerirUsers(var user : User, var groupId : String , var admin : Boolean) : Item<ViewHolder>(){

    val db = Firebase.firestore

    var deletarUser: ArrayList<String> = arrayListOf()

    val auth = FirebaseAuth.getInstance()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        var nome = viewHolder.itemView.findViewById<TextView>(R.id.text_name_gerir)

        val removeadmin = viewHolder.itemView.findViewById<Button>(R.id.buttonRemoverAdmin)
        val addadmin = viewHolder.itemView.findViewById<Button>(R.id.buttonAdicionarAdmin)

        val statusgroup = viewHolder.itemView.findViewById<TextView>(R.id.text_status_gerir)

        var online_status = viewHolder.itemView.findViewById<ImageButton>(R.id.online_status_gerir)
        var imgprofile = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageViewUserGerir)

        val buttonretirar = viewHolder.itemView.findViewById<Button>(R.id.buttonRetirar)

        if(user.nome.length > 10){
            var usercutted = user.nome.substring(0,7) + "..."
            nome.text = usercutted
        }
        else{
            nome.text = user.nome
        }

        if(user.uid != auth.currentUser!!.uid ){
            when(admin) { true ->{
                removeadmin.visibility = View.VISIBLE
                addadmin.visibility = View.INVISIBLE
                statusgroup.text = "Administrador"
            }
                false ->{
                    removeadmin.visibility = View.INVISIBLE
                    addadmin.visibility = View.VISIBLE
                    statusgroup.text = "Utilizador"
                }
            }
        }
        else if(user.uid == auth.currentUser!!.uid){
            removeadmin.visibility = View.INVISIBLE
            addadmin.visibility = View.INVISIBLE
            buttonretirar.visibility = View.INVISIBLE
            statusgroup.setText("Você")
        }

        removeadmin.setOnClickListener {
            db.collection("usuarios")
                .document(user.uid).collection("gruposIds")
                .document(groupId).update("admin",false)
            statusgroup.text = "Utilizador"
            removeadmin.visibility = View.INVISIBLE
            addadmin.visibility = View.VISIBLE
        }

        addadmin.setOnClickListener {
            db.collection("usuarios")
                .document(user.uid).collection("gruposIds")
                .document(groupId).update("admin",true)
            statusgroup.text = "Administrador"
            removeadmin.visibility = View.VISIBLE
            addadmin.visibility = View.INVISIBLE
        }


        Picasso.get().load(user.linkfoto).into(imgprofile)


        if (user.online == true) {
            online_status.setVisibility(View.VISIBLE)
            online_status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#097320")))
        } else if (user.online == false) {
            online_status.setVisibility(View.VISIBLE)
            online_status.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY))
        } else {
            online_status.setVisibility(View.INVISIBLE)
        }


        buttonretirar.setOnClickListener {

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



    /*  fun updatelist(groupID : String){
          db.collection("grupos").document(groupID).get().addOnSuccessListener { chatcontent ->
              deleteUser.clear()
              adapter.clear()
              if (chatcontent != null) {
                  deleteUser.addAll((chatcontent.get("userIds") as ArrayList<String>))
              }

              for (user in deleteUser) {
                  db.collection("usuarios")
                      .document(user).collection("gruposIds")
                      .document(groupID).get().addOnSuccessListener { grupoadmin ->
                          var admin = grupoadmin.getBoolean("admin")
                          if(admin==null){
                              admin = false
                          }
                          db.collection("usuarios").document(user).get().addOnSuccessListener {
                              val userInfo = it.toObject(User::class.java)
                              if (auth.currentUser?.uid == userInfo!!.uid) {
                                  adapter.add(0,GerirUsers(userInfo, groupID, admin))
                              }
                              else if(auth.currentUser?.uid != userInfo!!.uid) {
                                  adapter.add(GerirUsers(userInfo, groupID, admin))
                              }
                          }
                      }

              }

          }
      }*/
}
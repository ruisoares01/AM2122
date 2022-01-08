package com.example.projetoam2.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.ChatActivity
import com.example.projetoam2.Model.TextMessage
import com.example.projetoam2.Model.User
import com.example.projetoam2.R
import com.example.projetoam2.UserListActivity
import com.example.projetoam2.item.TextMessageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.*
import de.hdodenhof.circleimageview.CircleImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.getField
import kotlinx.android.synthetic.main.item_text_message.*
import java.sql.Array
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    //variaveis
    private lateinit var auth: FirebaseAuth

    private val adapter = GroupAdapter<ViewHolder>()

    var chatscomhistorico : ArrayList<String> = arrayListOf()


    //firestore
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var z = 0

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        var userRecyclerView = view.findViewById<RecyclerView>(R.id.userRecyclerVieww)

        var buttonAddChat  = view.findViewById<FloatingActionButton>(R.id.buttonAddChat)

        val buttonHomeGroups = view.findViewById<Button>(R.id.buttonHomeGrupos)
        val buttonHomeHome = view.findViewById<Button>(R.id.buttonHomeChats)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //hide action bar
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.adapter = adapter

        //clear the list
        adapter.clear()

        var otheruserstring : String = ""
        var latest_message : Any?
        var latest_time : Date

        db.collection("usuarios").document(auth.currentUser!!.uid).collection("Salachat").get()
            .addOnSuccessListener { chatsexistentes ->
                while(z<chatsexistentes.documents.size){
                    chatscomhistorico.add(chatsexistentes.documents.get(z).get("channelId") as String)
                    z +=1
                    println("CHAT COM HISTORICO : ${chatscomhistorico}")
                }
                for(chat in chatscomhistorico){
                    println("CHAT : ${chat}")
                    db.collection("Chat").document(chat).get()
                        .addOnSuccessListener { chatcontent ->

                                if(chatcontent.get("userIds")!=null && chatcontent.get("latest_message")!=null){
                                    otheruserstring = ((chatcontent.get("userIds") as ArrayList<String>).filter { it != auth.currentUser!!.uid }
                                        .toString().replace("[","").replace("]",""))
                                    latest_message = (chatcontent.data!!.get("latest_message") as HashMap<String,String>).get("text")
                                    latest_time = ((chatcontent.data!!.get("latest_message") as HashMap<String,String>).get("time") as Timestamp).toDate()

                                    println("Latest Message : ${latest_message} from ${chat} , Other User UID : ${otheruserstring} ")

                                    db.collection("usuarios").document(otheruserstring).get().addOnSuccessListener { documents ->
                                        //get all the documents
                                        val user = documents.toObject(User::class.java)
                                        if (auth.currentUser?.uid != user!!.uid) {
                                            adapter.add(Users(user,latest_message.toString(),latest_time))
                                        }
                                        adapter.setOnItemClickListener { item, view ->
                                            val utilizador = item as Users
                                            val intent = Intent(view.context, ChatActivity::class.java)

                                            intent.putExtra("name", utilizador.user.nome)
                                            intent.putExtra("uid", utilizador.user.uid)
                                            intent.putExtra("email", utilizador.user.email)
                                            intent.putExtra("linkfoto", utilizador.user.linkfoto)
                                            intent.putExtra("nAluno", utilizador.user.naluno)
                                            intent.putExtra("curso", utilizador.user.curso)
                                            intent.putExtra("morada", utilizador.user.morada)

                                            startActivity(intent)
                                        }
                                    }
                                }


                        }
                }

            }


/*        db.collection("usuarios").get().addOnSuccessListener { documents ->
            //get all the documents
            for (document in documents) {
                val user = document.toObject(User::class.java)
                if (auth.currentUser?.uid != user.uid) {
                    adapter.add(Users(user))
                }
            }
            adapter.setOnItemClickListener { item, view ->
                val utilizador = item as Users
                val intent = Intent(view.context, ChatActivity::class.java)

                intent.putExtra("name", utilizador.user.nome)
                intent.putExtra("uid", utilizador.user.uid)
                intent.putExtra("email", utilizador.user.email)
                intent.putExtra("linkfoto", utilizador.user.linkfoto)
                intent.putExtra("nAluno", utilizador.user.naluno)
                intent.putExtra("curso", utilizador.user.curso)
                intent.putExtra("morada", utilizador.user.morada)

                startActivity(intent)
            }
       } */

        buttonHomeGroups.setOnClickListener {
            val fragmenthomegrupos = HomeGruposFragment()
            val fragmentManager = fragmentManager
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragmenthomegrupos)
            fragmentTransaction.commit()
        }

        buttonHomeHome.setOnClickListener{}

        buttonAddChat.setOnClickListener {
            val intent = Intent(view.context, UserListActivity::class.java)
            startActivity(intent)
        }

        // Inflate the layout for this fragment
        return view
    }
}
class Users(val user : User, val textmessage : String, val texttime : Date) : Item<ViewHolder>() {
    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: ViewHolder, position: Int) {

        var secretKeyy = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
        var salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
        var iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

        val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec =  PBEKeySpec(secretKeyy.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
        val tmp = factory.generateSecret(spec);
        val secretKey =  SecretKeySpec(tmp.encoded, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT, Locale.forLanguageTag("PT"))

        var latest = viewHolder.itemView.findViewById<TextView>(R.id.text_latest_message)
        latest.text = String(cipher.doFinal(Base64.decode(textmessage, Base64.DEFAULT))) + " - " + dateFormat.format(texttime)

        var nome = viewHolder.itemView.findViewById<TextView>(R.id.text_name)
        nome.text = user.nome

        var imgprofile = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView3)
        Picasso.get().load(user.linkfoto).into(imgprofile)

    }

    override fun getLayout() = R.layout.user_layout
}
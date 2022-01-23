package com.example.projetoam2.Fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.*
import de.hdodenhof.circleimageview.CircleImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.time.Duration.Companion.milliseconds
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import android.util.Log
import com.example.projetoam2.*
import com.example.projetoam2.Notifications.*
import com.example.projetoam2.Notifications.FirebaseService.Companion.token

import com.example.projetoam2.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class LatestMessageTime(val otheruser : String , val latesttext : String , val latesttime : Date )


lateinit var chatupdate : ListenerRegistration

class HomeFragment : Fragment() {

    private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    private val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

    //variaveis
    private lateinit var auth: FirebaseAuth

    private val adapter = GroupAdapter<ViewHolder>()

    var chatscomhistorico : ArrayList<String> = arrayListOf()

    //firestore
    val db = Firebase.firestore

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "carregar home fragment")
        var z = 0
        var c = 0

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        var userRecyclerView = view.findViewById<RecyclerView>(R.id.userRecyclerVieww)

        val buttonHomeGroups = view.findViewById<Button>(R.id.buttonGrupos)
        val buttonHomeHome = view.findViewById<Button>(R.id.buttonMensagens)

        var buttonAddChat  = view.findViewById<FloatingActionButton>(R.id.buttonAddChat)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //hide action bar
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        userRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        userRecyclerView.adapter = adapter

        //clear the list
        adapter.clear()
        chatscomhistorico.clear()

        var otheruserstring: String
        var latesttexttime : ArrayList<LatestMessageTime> = arrayListOf()
        var latest_message : Any?
        var latest_time : Date

        latesttexttime.clear()

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

                                latesttexttime.add(LatestMessageTime(otheruserstring,latest_message as String,latest_time))

                                println("Latest Message : ${latest_message} from ${chat} , Other User UID : ${otheruserstring} ")
                            }
                            c +=1
                            println("C: ${c}   vs ${chatscomhistorico.size}")
                            if (c == chatscomhistorico.size)
                            {
                                println("latesttexttime size : ${latesttexttime.size} ")
                                latesttexttime.sortByDescending {latesttexttime -> latesttexttime.latesttime }
                                for(latest in latesttexttime)
                                {
                                    println("latest : ${latest} and ${latest.latesttime} ${latest.latesttext} ${latest.otheruser}")
                                    db.collection("usuarios").document(latest.otheruser).get()
                                        .addOnSuccessListener { documents ->
                                            val user = documents.toObject(User::class.java)
                                            if (auth.currentUser?.uid != user!!.uid) {
                                                adapter.add(Users(user,latest.latesttext,latest.latesttime))
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
                                                intent.putExtra("status", utilizador.user.online)

                                                startActivity(intent)
                                            }
                                        }
                                }
                                latesttexttime.sortByDescending {latesttexttime -> latesttexttime.latesttime.time }
                            }
                        }
                }

            }

        buttonHomeGroups.setOnClickListener {
            val fragmenthomegrupos = HomeGruposFragment()
            val fragmentManager = fragmentManager
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragmenthomegrupos)
            fragmentTransaction.commit()
        }

        var x1 = 0.0F
        var x2 = 0.0F
        val MIN_DISTANCE = 150

        buttonHomeHome.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when(event.action) {
                    MotionEvent.ACTION_DOWN -> x1 = event.getX()
                    MotionEvent.ACTION_UP -> {x2 = event.getX()
                        var deltaX = x2 - x1
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            val fragmenthomegrupos = HomeGruposFragment()
                            val fragmentManager = fragmentManager
                            val fragmentTransaction = fragmentManager!!.beginTransaction()
                            fragmentTransaction.replace(R.id.fragment_container, fragmenthomegrupos)
                            fragmentTransaction.commit()
                            return true
                        }
                    }
                }
                return false
            }
        })

        buttonAddChat.setOnClickListener {
            val intent = Intent(view.context, UserListActivity::class.java)
            startActivity(intent)
        }


        chatupdate = db.collection("Chat").addSnapshotListener { chats, error ->
            c=0
           latesttexttime.clear()


            for (documents in chats!!.documentChanges) {
                when (documents.type) {
                    //             DocumentChange.Type.ADDED -> Log.d(TAG, "New city: ${dc.document.data}")
                    DocumentChange.Type.MODIFIED -> {
                        var modifiedchat = (documents.document.data.get("userIds") as ArrayList<String>)

                        if(modifiedchat.contains(auth.currentUser!!.uid)){

                            var modifieduser = ((documents.document.data.get("userIds") as ArrayList<String>).filter { it != auth.currentUser?.uid })
                                .toString().replace("[","").replace("]","")


                            var modifiedtime = ((documents.document.data!!.get("latest_message") as HashMap<String,String>).get("time") as Timestamp).toDate()
                            var modifiedtextencrypted = ((documents.document.data!!.get("latest_message") as HashMap<String,String>).get("text"))

                            var user = ""

                            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
                            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
                            val tmp = factory.generateSecret(spec);
                            val secretKey =  SecretKeySpec(tmp.encoded, "AES")
                            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
                            var modifiedtext = String(cipher.doFinal(Base64.decode(modifiedtextencrypted, Base64.DEFAULT)))
                            var title = ""
                            val message = modifiedtext
                            val token = token!!
                            db.collection("usuarios").document(modifieduser).get().addOnSuccessListener { otheruser ->
                                title = otheruser.getString("nome").toString()
                                user = otheruser.getString("uid").toString()

                                if(user != auth.currentUser!!.uid) {
                                    PushNotification(
                                        NotificationData(title, message),
                                        token
                                    ).also {
                                        sendNotification(it)
                                    }
                                }
                            }



                            adapter.clear()
                            for(chat in chatscomhistorico){
                                println("CHAT : ${chat}")
                                db.collection("Chat").document(chat).get()
                                    .addOnSuccessListener { chatcontent ->
                                        if(chatcontent.get("userIds")!=null && chatcontent.get("latest_message")!=null){
                                            otheruserstring = ((chatcontent.get("userIds") as ArrayList<String>).filter { it != auth.currentUser!!.uid }
                                                .toString().replace("[","").replace("]",""))
                                            latest_message = (chatcontent.data!!.get("latest_message") as HashMap<String,String>).get("text")
                                            latest_time = ((chatcontent.data!!.get("latest_message") as HashMap<String,String>).get("time") as Timestamp).toDate()
                                            latesttexttime.add(LatestMessageTime(otheruserstring,latest_message as String,latest_time))
                                            println("Latest UPDATED Message : ${latest_message} from ${chat} , Other User UID : ${otheruserstring} ")
                                        }
                                        c +=1
                                        println("C: ${c}   vs ${chatscomhistorico.size}")
                                        if (c == chatscomhistorico.size)
                                        {
                                            println("latesttexttime size : ${latesttexttime.size} ")
                                            latesttexttime.sortByDescending {latesttexttime -> latesttexttime.latesttime }
                                            for(latest in latesttexttime)
                                            {
                                                println("latest : ${latest} and ${latest.latesttime} ${latest.latesttext} ${latest.otheruser}")
                                                db.collection("usuarios").document(latest.otheruser).get()
                                                    .addOnSuccessListener { documents ->
                                                        val user = documents.toObject(User::class.java)
                                                        if (auth.currentUser?.uid != user!!.uid) {
                                                            adapter.add(Users(user,latest.latesttext,latest.latesttime))
                                                        }
                                                    }
                                            }
                                        }
                                    }
                            }
                        }




//                        FirebaseMessagingService().sendRecievingChatNotification(modifieduser,"$modifiedtext")
                        }
                    }
                    //            DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed city: ${dc.document.data}")
            }


        }



        // Inflate the layout for this fragment
        return view


    }
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(ContentValues.TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(ContentValues.TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(ContentValues.TAG, e.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatupdate.remove()
    }

}
class Users(val user : User, val textmessage : String, val texttime : Date) : Item<ViewHolder>() {
    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: ViewHolder, position: Int) {

        var secretKeyy = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
        var salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
        var iv = "bVQzNFNhRkQ1Njc4UUFaWA=="
        var miliday = 86400000

        val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec =  PBEKeySpec(secretKeyy.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
        val tmp = factory.generateSecret(spec);
        val secretKey =  SecretKeySpec(tmp.encoded, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        lateinit var dateFormat : DateFormat

        println("Inwholemiliseconds : ${texttime.time.milliseconds.inWholeMilliseconds}")

        if((System.currentTimeMillis()-texttime.time.milliseconds.inWholeMilliseconds)>miliday){
            dateFormat = SimpleDateFormat
                .getDateInstance(SimpleDateFormat.SHORT,Locale.forLanguageTag("PT"))
        }
        else{
            dateFormat = SimpleDateFormat
                .getTimeInstance(SimpleDateFormat.SHORT,Locale.forLanguageTag("PT"))
        }

        var latestmessage = String(cipher.doFinal(Base64.decode(textmessage, Base64.DEFAULT)))

        if(latestmessage.length>18){
            latestmessage = latestmessage.substring(0,15) + "..."
        }

        var latest = viewHolder.itemView.findViewById<TextView>(R.id.text_latest_message)

        latest.text = latestmessage

        var latesthour = viewHolder.itemView.findViewById<TextView>(R.id.text_latest_message_hour)
        latesthour.text = dateFormat.format(texttime)

        var online_status = viewHolder.itemView.findViewById<ImageButton>(R.id.online_status)

        if(user.online == true){
            online_status.setVisibility(View.VISIBLE)
            online_status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#097320")))}
        else if(user.online == false){
            online_status.setVisibility(View.VISIBLE)
            online_status.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY))}
        else{online_status.setVisibility(View.INVISIBLE)}


        var nome = viewHolder.itemView.findViewById<TextView>(R.id.text_name)
        nome.text = user.nome

        var imgprofile = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView3)
        Picasso.get().load(user.linkfoto).into(imgprofile)

    }


    override fun getLayout() = R.layout.user_layout


}
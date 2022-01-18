package com.example.projetoam2

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetoam2.Model.MessageType
import com.example.projetoam2.Model.TextMessage
import com.example.projetoam2.Utils.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.item_text_message.*
import java.util.*
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.projetoam2.Fragments.HomeFragment
import com.example.projetoam2.Model.ImageMessage
import com.example.projetoam2.Model.User
import com.example.projetoam2.Notifications.FirebaseService
import com.example.projetoam2.Notifications.PushNotification
import com.example.projetoam2.Notifications.RetrofitInstance
import com.example.projetoam2.Utils.StorageUtil
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


const val TOPIC = "/topics/myTopic2"
private const val RC_SELECT_IMAGE = 2

class ChatActivity : AppCompatActivity() {

    private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    private val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

    private lateinit var messagesListenerRegistration : ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection : Section

    private lateinit var currentChannelId: String
    private lateinit var currentUser: User
    private lateinit var otherUserId: String

    private val adapter = GroupAdapter<ViewHolder>()

    //firestore
    val db = Firebase.firestore

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //Notificacoes (em desenvolvimento)

        /*FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            etToken.setText(it.token)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)*/

        //action bar
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
          supportActionBar?.hide()

        FirestoreUtil.getCurrentUser {
            currentUser = it
        }



        var otherUserName = ""
        var otherUserId = ""
        var linkfoto = ""
        var otherUserEmail = ""
        var otherUserN = ""
        var otherUserCurso = ""
        var otherUserMorada = ""
        var otherUserStatus = false
        val bundle = intent.extras

        //collect data
        bundle?.let {
            otherUserName = it.getString("name").toString()
            otherUserId = it.getString("uid").toString()
            otherUserEmail = it.getString("email").toString()
            otherUserN = it.getString("nAluno").toString()
            otherUserCurso = it.getString("curso").toString()
            otherUserMorada = it.getString("morada").toString()
            linkfoto = it.getString("linkfoto").toString()
            otherUserStatus = it.getBoolean("status")

        }

        val imgprofile = findViewById<CircleImageView>(R.id.imgProfile)
        Picasso.get().load(linkfoto).into(imgprofile)

        val nameProfile = findViewById<TextView>(R.id.textViewName)
        nameProfile.text = otherUserName

        val userStatus = findViewById<TextView>(R.id.textViewstatus)
        if (otherUserStatus == true){
            userStatus.text = "Online"
        }else{
            userStatus.text = "Offline"
        }

        val profileOnline = findViewById<ImageButton>(R.id.online_status)
        if(otherUserStatus == true){
            profileOnline.setVisibility(View.VISIBLE)
            profileOnline.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00b026")))
        }
        else{
            profileOnline.setVisibility(View.VISIBLE)
            profileOnline.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY))
        }

        nameProfile.setOnClickListener {
            val intent = Intent(this, OtherProfile::class.java)
            intent.putExtra("name", otherUserName)
            intent.putExtra("uid", otherUserId)
            intent.putExtra("email", otherUserEmail)
            intent.putExtra("nAluno", otherUserN)
            intent.putExtra("curso", otherUserCurso)
            intent.putExtra("morada", otherUserMorada)
            intent.putExtra("linkfoto", linkfoto)
            intent.putExtra("status", otherUserStatus)
            startActivity(intent)
        }

        imgprofile.setOnClickListener {
            val intent = Intent(this, OtherProfile::class.java)
            intent.putExtra("name", otherUserName)
            intent.putExtra("uid", otherUserId)
            intent.putExtra("email", otherUserEmail)
            intent.putExtra("nAluno", otherUserN)
            intent.putExtra("curso", otherUserCurso)
            intent.putExtra("morada", otherUserMorada)
            intent.putExtra("linkfoto", linkfoto)
            intent.putExtra("status", otherUserStatus)
            startActivity(intent)
        }

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }

        //get the chat channel
        FirestoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->
            currentChannelId = channelId
            messagesListenerRegistration =
                FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            send_msg_button.setOnClickListener {

                val string1: String
                val string: String
                string = edit_text.getText().toString()


                val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
                val tmp = factory.generateSecret(spec)
                val secretKey =  SecretKeySpec(tmp.encoded, "AES")

                val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
                string1 = Base64.encodeToString(cipher.doFinal(string.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)

                val messageTosend =
                    TextMessage(string1.toString(), Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT)

                edit_text.setText("")

                FirestoreUtil.sendMessage(messageTosend, channelId)
                FirestoreUtil.updateLastestMessage(messageTosend,channelId)





                //Notificacoes (em desenvolvimento)

           /*     val title = "CUCU"
                val message = edit_text.text.toString()
                val recipientToken = etToken.text.toString()
                if(title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                    PushNotification(
                        NotificationData(title, message),
                        recipientToken
                    ).also {
                        sendNotification(it)
                    }
                }*/
            }

            send_image.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
            }
        }



        //action bar title, name of the user
     //   supportActionBar?.title = otherUserName

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null) {
            val selectedImagePath = data.data

            val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()

            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath ->
                val messageToSend =
                    ImageMessage(imagePath, Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid)
                FirestoreUtil.sendMessage(messageToSend, currentChannelId)
                FirestoreUtil.updateLastestMessage(messageToSend,currentChannelId)
            }
        }
    }



    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            recyclerview_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if (shouldInitRecyclerView) {
            init()
        } else {
            updateItems()
        }
    }

    override fun onPause() {
        super.onPause()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, false)

        db.collection("usuarios").document(uid.toString()).set(user)
            .addOnSuccessListener {
                println("Offline")
            }
    }
    override fun onResume() {
        super.onResume()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, true)
        db.collection("usuarios").document(uid.toString()).set(user)
            .addOnSuccessListener {
                println("Offline")
            }
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
}

package com.example.projetoam2


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.projetoam2.Fragments.CalendarioFragment
import com.example.projetoam2.Fragments.HomeFragment
import com.example.projetoam2.Fragments.PerfilFragment
import com.example.projetoam2.Model.Dados
import com.example.projetoam2.Model.Message
import com.example.projetoam2.Model.User
import com.example.projetoam2.Notifications.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TOPIC = "/notification/userid"

class MainActivity : AppCompatActivity() {

    //variaveis
    private lateinit var auth: FirebaseAuth


    //firestore
    val db = Firebase.firestore

    //variaveis fragment
    private val mainFragment = HomeFragment()
    private val calendarioFragment = CalendarioFragment()
    private val perfilFragment = PerfilFragment()


    val TAG = "MainActivity"

/*   var notificationReceiver : NotificationReceiver? = null

    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.extras?.getString(FirebaseMessagingService.NOTIFICATION_MESSAGE)?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth


        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseService.token = it
            println("token : $it") //antes isto era edittexttoken.text
        }
            .addOnFailureListener{
                println("exception -> $it")
            }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)


/*
        val title = "CUCU"
        val message = edit_text.text.toString()
        val recipientToken = etToken.text.toString()
        if(title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
            PushNotification(
                NotificationData(title, message),
                recipientToken
            ).also {
                sendNotification(it)
            }
        }
        */

        //fragment function
        replaceFragment(mainFragment)


        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> replaceFragment(mainFragment)
                R.id.ic_calendario -> replaceFragment(calendarioFragment)
                R.id.ic_perfil -> replaceFragment(perfilFragment)
            }
            true
        }
    }

    private fun replaceFragment (fragment: Fragment){

        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logout) {
            //write the login for logout
            val intent = Intent(this, LoginActivity::class.java)
            auth.signOut()
            startActivity(intent)
            finish()
            return true
        }
        return true
    }


    override fun onPause() {
        super.onPause()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, false)

        db.collection("usuarios").document(uid.toString()).update("online",false)
            .addOnSuccessListener {
                println("Offline")
            }

        /*val title = "Update do Estado"
        val message = "${dados.nome} esta offline"
        PushNotification(
            NotificationData(title, message),
            TOPIC
        ).also {
            sendNotification(it)
        }*/

    //    notificationReceiver?.let {
    //        this.unregisterReceiver(it)
    //    }
    }
    override fun onResume() {
        super.onResume()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, true)
        db.collection("usuarios").document(uid.toString()).update("online",true)
            .addOnSuccessListener {
                println("Offline")

               /*val title = "Update do Estado"
                val message = "${dados.nome} esta offline"
                PushNotification(
                    NotificationData(title, message),
                    TOPIC
                ).also {
                    sendNotification(it)
                }*/
                println("Online")
            }
    }




   /* private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
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
 }*/
}
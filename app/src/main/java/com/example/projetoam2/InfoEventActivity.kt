package com.example.projetoam2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetoam2.Model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class InfoEventActivity : AppCompatActivity() {

    val db = Firebase.firestore
    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_event)

        supportActionBar?.hide()

        val horainicio = intent.extras?.getString("horainicio")
        val titulo = intent.extras?.getString("titulo")
        val descricao = intent.extras?.getString("descricao")
        val tipo = intent.extras?.getString("tipo")
        val infotipo = intent.extras?.getString("infotipo")
        val horafim = intent.extras?.getString("horafim")
        val data = intent.extras?.getString("data")
        val idevent = intent.extras?.getString("idevento")
        val cor = intent.extras?.getInt("cor")
        val sologroup = intent.extras?.getString("sologroup")
        var admin = false


        val horafimcutted =  horafim?.substringAfter("Timestamp(seconds=")
            ?.substringBefore(", nanoseconds=0)")

        val formatterhour = SimpleDateFormat("HH:mm")
        val horafimconverted = formatterhour.format(Date(horafimcutted?.toLong()?.times(1000)!!))


        findViewById<TextView>(R.id.TituloEventoInfo).text = titulo
        findViewById<TextView>(R.id.DescricaoEventoInfo).text = descricao
        findViewById<TextView>(R.id.HoraInicioEventoInfo).text = horainicio
        findViewById<TextView>(R.id.HoraFimEventoInfo).text = horafimconverted
        findViewById<TextView>(R.id.DataEventoInfo).text = data
        val okButton = findViewById<Button>(R.id.buttonOkInfoEvent)
        val okButtonHalf = findViewById<Button>(R.id.buttonOkInfoEventHalf)
        val editButtonHalf = findViewById<Button>(R.id.buttonEditEventHalf)
        okButtonHalf.visibility = View.INVISIBLE
        editButtonHalf.visibility = View.INVISIBLE
        val tipoEventoInfo=  findViewById<TextView>(R.id.tipoEventoInfo)
        val infoTipoEventInfo = findViewById<TextView>(R.id.infoTipoEventInfo)

        if(sologroup=="is_solo"){
        tipoEventoInfo.visibility = View.INVISIBLE
        infoTipoEventInfo.visibility = View.INVISIBLE
        }

        println("tipo : " + tipo)
        if(tipo == "grupo"){
            tipoEventoInfo.text = "Evento criado pelo grupo:"
            if (infotipo != null) {
                println("infotipo :" + infotipo)
                db.collection("grupos").document(infotipo).get()
                    .addOnSuccessListener {
                        infoTipoEventInfo.text = it.getString("nome")
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Nao foi possivel buscar o nome do grupo no firebase",Toast.LENGTH_LONG)
                    }
                db.collection("usuarios").document(auth.currentUser!!.uid).collection("gruposIds").document(infotipo).get()
                    .addOnSuccessListener { admin = it.getBoolean("admin")!!
                        if(admin == true) {
                            okButtonHalf.visibility = View.VISIBLE
                            editButtonHalf.visibility = View.VISIBLE
                            okButton.visibility = View.GONE
                        }
                    }
            }
            else{
                Toast.makeText(this,"Nao foi possivel buscar o nome do grupo",Toast.LENGTH_LONG)
            }
        }
        else if(tipo == "pessoal"){
            tipoEventoInfo.text = "Evento pessoal"
            okButtonHalf.visibility = View.VISIBLE
            editButtonHalf.visibility = View.VISIBLE
            infoTipoEventInfo.visibility = View.INVISIBLE
            okButton.visibility = View.INVISIBLE
        }
        println("Dados DE INFO :  ${titulo} + ${descricao} + ${horainicio} ${horafim} +${data} + ${tipoEventoInfo.text} + ${infoTipoEventInfo.text}")

        editButtonHalf.setOnClickListener {
            val intentt = Intent(this, EditEventActivity::class.java)
            intentt.putExtra("horainicio", horainicio as String)
            intentt.putExtra("titulo", titulo as String)
            intentt.putExtra("descricao", descricao as String)
            intentt.putExtra("tipo", tipo as String)
            intentt.putExtra("infotipo", infotipo as String)
            intentt.putExtra("horafim", horafim)
            intentt.putExtra("data", data as String)
            intentt.putExtra("idevento",idevent)
            intentt.putExtra("admin", admin)
            intentt.putExtra("cor",cor)
            startActivity(intentt)
            finish()
        }

        okButton.setOnClickListener {
            finish()
        }
        okButtonHalf.setOnClickListener {
            finish()
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
}
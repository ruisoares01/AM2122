package com.example.projetoam2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class InfoEventActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_event)


        val buttonOkInfoEvent = findViewById<Button>(R.id.buttonOkInfoEvent)

        val horainicio = intent.extras?.getString("horainicio")
        val titulo = intent.extras?.getString("titulo")
        val descricao = intent.extras?.getString("descricao")
        val tipo = intent.extras?.getString("tipo")
        val infotipo = intent.extras?.getString("infotipo")
        val horafim = intent.extras?.getString("horafim")
        val data = intent.extras?.getString("data")

        val horafimcutted =  horafim?.substringAfter("Timestamp(seconds=")
            ?.substringBefore(", nanoseconds=0)")

        val formatterhour = SimpleDateFormat("HH:mm")
        val horafimconverted = formatterhour.format(Date(horafimcutted?.toLong()?.times(1000)!!))

        findViewById<TextView>(R.id.TituloEventoInfo).text = titulo
        findViewById<TextView>(R.id.DescricaoEventoInfo).text = descricao
        findViewById<TextView>(R.id.HoraInicioEventoInfo).text = horainicio
        findViewById<TextView>(R.id.HoraFimEventoInfo).text = horafimconverted
        findViewById<TextView>(R.id.DataEventoInfo).text = data
        val tipoEventoInfo=  findViewById<TextView>(R.id.tipoEventoInfo)
        val infoTipoEventInfo = findViewById<TextView>(R.id.infoTipoEventInfo)

        if(tipo == "grupo"){
            tipoEventoInfo.text = "Evento criado pelo grupo:"
            if (infotipo != null) {
                db.collection("grupos").document(infotipo).get()
                    .addOnSuccessListener {
                        infoTipoEventInfo.text = it.get("nome").toString()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Nao foi possivel buscar o nome do grupo no firebase",Toast.LENGTH_LONG)
                    }
            }
            else{
                Toast.makeText(this,"Nao foi possivel buscar o nome do grupo",Toast.LENGTH_LONG)
            }
        }
        else if(tipo == "pessoal"){
            tipoEventoInfo.text = "Evento Pessoal criado por si:"
            infoTipoEventInfo.text = dados.nome
        }
        println("Dados DE INFO :  ${titulo} + ${descricao} + ${horainicio} ${horafim} +${data} + ${tipoEventoInfo.text} + ${infoTipoEventInfo.text}")


        buttonOkInfoEvent.setOnClickListener {
            finish()
        }

    }

}
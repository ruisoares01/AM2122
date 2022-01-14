package com.example.projetoam2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.projetoam2.Model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eltos.simpledialogfragment.SimpleDateDialog
import eltos.simpledialogfragment.SimpleDialog
import eltos.simpledialogfragment.SimpleTimeDialog
import eltos.simpledialogfragment.color.SimpleColorDialog
import kotlinx.android.synthetic.main.activity_create_event.*
import java.text.SimpleDateFormat
import java.util.*

class EditEventActivity : AppCompatActivity(), SimpleDialog.OnDialogResultListener {
    var color: Int = -15100386
    var horainicio = ""
    var horafim = ""
    var dateLong = System.currentTimeMillis()
    val db = Firebase.firestore
    var datainicio = ""
    var datafim = ""



    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        supportActionBar?.hide()

        val horainicio = intent.extras?.getString("horainicio")
        val titulo = intent.extras?.getString("titulo")
        val descricao = intent.extras?.getString("descricao")
        val tipo = intent.extras?.getString("tipo")
        val infotipo = intent.extras?.getString("infotipo")
        val horafim = intent.extras?.getString("horafim")?.substringAfter("Timestamp(seconds=")
            ?.substringBefore(", nanoseconds=0)")
        val data = intent.extras?.getString("data")
        val idevent = intent.extras?.getString("idevento")!!
        var admin = intent.extras?.getBoolean("admin")
        color = intent.extras?.getInt("cor")!!

        val colorEventButton = findViewById<ImageButton>(R.id.buttonColorEvent)

        val horaFimTextView = findViewById<TextView>(R.id.textViewHoraFim)
        val horaInicioTextView = findViewById<TextView>(R.id.textViewHoraInicio)
        val dataTextView = findViewById<TextView>(R.id.textViewData)
        val editEventButton = findViewById<Button>(R.id.buttonCreateEvent)
        val cancelEventButton = findViewById<Button>(R.id.buttonCancelEvent)
        val tituloTextView = findViewById<TextView>(R.id.editTextTituloEvento)
        val descTextView = findViewById<TextView>(R.id.editTextDescricaoEvento)
        editEventButton.text = "Ok"

        dateLong = SimpleDateFormat("dd 'de' MMMM 'de' yyyy",Locale.forLanguageTag("PT")).parse(data).time
        dataTextView.setText(data as String)

        val formatterhour = SimpleDateFormat("HH:mm")
        val horafimconverted = formatterhour.format(Date(horafim?.toLong()?.times(1000)!!))


        colorEventButton.setBackgroundTintList(ColorStateList.valueOf(color))


        horaFimTextView.text = horafimconverted
        horaInicioTextView.text = horainicio
        tituloTextView.text = titulo
        descTextView.text = descricao


        colorEventButton.setOnClickListener {
            SimpleColorDialog.build()
                .title("Selecione a Cor do Evento")
                .allowCustom(true)
                .colorPreset(color)
                .show(this,"CorEvento")
            println(color)
        }

        horaInicioTextView.setOnClickListener {
            SimpleTimeDialog.build()
                .hour(12)
                .minute(30)
                .set24HourView(true)
                .show(this,"HoraInicio")
        }

        horaFimTextView.setOnClickListener {
            SimpleTimeDialog.build()
                .hour(12)
                .minute(30)
                .set24HourView(true)
                .show(this,"HoraFim")
        }

        dataTextView.setOnClickListener {
            SimpleDateDialog.build()
                .minDate(System.currentTimeMillis())
                .date(System.currentTimeMillis())
                .show(this, "Data");
        }



        cancelEventButton.setOnClickListener {
            finish()
        }

        editEventButton.setOnClickListener {
            var horafimfinal = formatterhour.parse(horaFimTextView.text.toString()).time
            var horainiciofinal = formatterhour.parse(horaInicioTextView.text.toString()).time
            var dataprocessed = SimpleDateFormat("dd/MM/yyyy").format(Date(dateLong))
            var datafimprocessed = (SimpleDateFormat("dd/MM/yyyy").parse("${dataprocessed}").time + horafimfinal )/ 1000
            var datainicioprocessed = (SimpleDateFormat("dd/MM/yyyy").parse("${dataprocessed}").time + horainiciofinal )/ 1000
            var datafimtimestamp = Timestamp(datafimprocessed,0)
            var datainiciotimestamp = Timestamp(datainicioprocessed,0)


            var evento = hashMapOf(
                "titulo" to tituloTextView.text.toString(),
                "descricao" to descTextView.text.toString(),
                "dataInicio" to datainiciotimestamp.toDate(),
                "dataFim" to datafimtimestamp.toDate(),
                "cor" to color
            )

            println("data fim processed : ${datafimprocessed}")
            println("data inicio processed : ${datainicioprocessed}")
            println(datainicio)
            println(datafim)

            if(datafimprocessed > datainicioprocessed)
            {
                if(tipo == "pessoal"){
                    db.collection("usuarios").document(Firebase.auth.currentUser?.uid.toString()).collection("eventos").document(idevent).set(evento)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener { erro ->
                            Log.w(ContentValues.TAG, "Error when editing event : ", erro)
                        }
                }
                else if(tipo == "grupo"){
                    db.collection("grupos").document(infotipo!!).collection("eventos").document(idevent).set(evento)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener { erro ->
                            Log.w(ContentValues.TAG, "Error when editing event : ", erro)
                        }
                }

                finish()
            }
            else{
                Toast.makeText(this,"Hora de Inicio tem que ser menor que a Hora de Fim do Evento",
                    Toast.LENGTH_SHORT)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResult(dialogTag: String, which: Int, extras: Bundle): Boolean {
        if(dialogTag == "CorEvento" && which == 0 || dialogTag == "Data" && which == 0 ){
            return false
        }
        else if(dialogTag == "CorEvento" && which == -1){
            color = extras.getInt(SimpleColorDialog.COLOR)

            buttonColorEvent.setBackgroundTintList(ColorStateList.valueOf(extras.getInt(SimpleColorDialog.COLOR)))
//        buttonColorEvent.setBackgroundColor(extras.getInt(SimpleColorDialog.COLOR))
        }
        else if(dialogTag == "HoraInicio" && which == -1){
            horainicio = "${extras.getInt(SimpleTimeDialog.HOUR)}:${extras.getInt(SimpleTimeDialog.MINUTE)}"
            textViewHoraInicio.setText("${extras.getInt(SimpleTimeDialog.HOUR)}:${extras.getInt(SimpleTimeDialog.MINUTE)}")
        }
        else if(dialogTag == "HoraFim" && which == -1){
            horafim = "${extras.getInt(SimpleTimeDialog.HOUR)}:${extras.getInt(SimpleTimeDialog.MINUTE)}"
            textViewHoraFim.setText("${extras.getInt(SimpleTimeDialog.HOUR)}:${extras.getInt(SimpleTimeDialog.MINUTE)}")
        }
        else if(dialogTag == "Data" && which == -1){
            dateLong = extras.getLong(SimpleDateDialog.DATE)

            val date = Date(extras.getLong(SimpleDateDialog.DATE))
            val format = SimpleDateFormat("dd 'de' MMMM 'de' yyyy",Locale.forLanguageTag("PT"))
            textViewData.setText(format.format(date).toString())
        }
        return true
    }


}

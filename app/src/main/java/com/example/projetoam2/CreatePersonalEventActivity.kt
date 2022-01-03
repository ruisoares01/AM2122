package com.example.projetoam2

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import eltos.simpledialogfragment.color.SimpleColorDialog
import eltos.simpledialogfragment.SimpleDialog
import kotlinx.android.synthetic.main.activity_create_event.*
import android.os.Build
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eltos.simpledialogfragment.SimpleDateDialog
import eltos.simpledialogfragment.SimpleTimeDialog
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDate.now
import java.util.*
import android.R.string
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Button
import com.google.firebase.Timestamp
import java.time.format.DateTimeFormatter


class CreatePersonalEventActivity : AppCompatActivity(),SimpleDialog.OnDialogResultListener {


    var color: Int = 0
    var horainicio = ""
    var horafim = ""
    var dateLong = 0L
    val db = Firebase.firestore
    var datainicio = ""
    var datafim = ""



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        supportActionBar?.hide()

        val c = intent.extras?.getInt("c")

        val ColorEventButton = findViewById<ImageButton>(R.id.buttonColorEvent)

        val horaFimTextView = findViewById<TextView>(R.id.textViewHoraFim)
        val horaInicioTextView = findViewById<TextView>(R.id.textViewHoraInicio)
        val dataTextView = findViewById<TextView>(R.id.textViewData)
        val createEventButton = findViewById<Button>(R.id.buttonCreateEvent)

        val date = Date(System.currentTimeMillis())
        val format = SimpleDateFormat("dd 'de' MMMM 'de' yyyy",Locale.forLanguageTag("PT"))
        textViewData.setText(format.format(date).toString())


        ColorEventButton.setOnClickListener {
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





        createEventButton.setOnClickListener {
            val dataprocessed = SimpleDateFormat("dd/MM/yyyy").format(Date(dateLong))
            val datafimprocessed = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("${dataprocessed} ${horafim}:00").time / 1000
            val datainicioprocessed = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("${dataprocessed} ${horainicio}:00").time / 1000
            val datafimtimestamp = Timestamp(datafimprocessed,0)
            val datainiciotimestamp = Timestamp(datainicioprocessed,0)


            val evento = hashMapOf(
                "titulo" to findViewById<TextView>(R.id.editTextTituloEvento).text.toString(),
                "descricao" to findViewById<TextView>(R.id.editTextDescricaoEvento).text.toString(),
                "dataInicio" to datainiciotimestamp,
                "dataFim" to datafimtimestamp,
                "cor" to color
            )

            if(datafimtimestamp < datainiciotimestamp)
            {

                println("data fim processed : ${datafimprocessed}")
                println("data inicio processed : ${datainicioprocessed}")


                println(datainicio)
                println(datafim)


                db.collection("usuarios").document(Firebase.auth.currentUser?.uid.toString()).collection("eventos").add(evento)
                  .addOnSuccessListener {
                      Log.d(TAG, "Event created sucessfully with ID: ${it.id}")
                  }
                  .addOnFailureListener { erro ->
                      Log.w(TAG, "Error when adding event : ", erro)
                  }

            }
            else{
                Toast.makeText(this,"Hora de Inicio tem que ser menor que a Hora de Fim do Evento",Toast.LENGTH_SHORT)
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

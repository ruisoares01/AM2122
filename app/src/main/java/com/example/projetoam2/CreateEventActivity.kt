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
import androidx.annotation.RequiresApi
import eltos.simpledialogfragment.SimpleDateDialog
import eltos.simpledialogfragment.SimpleTimeDialog
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDate.now
import java.util.*


class CreateEventActivity : AppCompatActivity(),SimpleDialog.OnDialogResultListener {


    var color: Int = 0
    var horainicio = ""
    var horafim = ""
    var dateLong = 0L

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        supportActionBar?.hide()

        val ColorEventButton = findViewById<ImageButton>(R.id.buttonColorEvent)

        val horaFimTextView = findViewById<TextView>(R.id.textViewHoraFim)
        val horaInicioTextView = findViewById<TextView>(R.id.textViewHoraInicio)
        val dataTextView = findViewById<TextView>(R.id.textViewData)

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

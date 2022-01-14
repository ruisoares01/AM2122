package com.example.projetoam2

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.fragment_calendario.*
import kotlinx.android.synthetic.main.row_calendario.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import eltos.simpledialogfragment.SimpleDialog
import eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener.BUTTON_NEGATIVE
import eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener.BUTTON_POSITIVE
import kotlinx.android.synthetic.main.activity_create_event.*


class EventCalendarGroupActivity : AppCompatActivity(),SimpleDialog.OnDialogResultListener {

    var eventoscalendario: MutableList<Event> = arrayListOf()
    lateinit var adapterlisteventos : CalendarioEventosAdapter
    private val dateFormatMonth = SimpleDateFormat("MMMM 'de' yyyy", Locale.forLanguageTag("PT"))
    val db = Firebase.firestore
    val auth = Firebase.auth
    var arrayChats : MutableList<String> = arrayListOf()
    var eventBeingRemoved = ""
    var grupoID = ""
    var admin = false

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_groups)
        supportActionBar?.hide()

        Locale.setDefault(Locale("pt"))

        // Inflate the layout for this fragment
        var compactCalendar = findViewById<View>(R.id.compactcalendar_view_group) as CompactCalendarView
        compactCalendar.setUseThreeLetterAbbreviation(true)


        //Por o primeiro dia da semana como Domingo
        compactCalendar.setFirstDayOfWeek(2)

        grupoID = intent.extras?.getString("groupID").toString()

        arrayChats.clear()

        val listViewCalendarioEventos = findViewById<ListView>(R.id.listViewCalendarioGrupo)

        adapterlisteventos = CalendarioEventosAdapter()
        if (listViewCalendarioEventos != null) {
            listViewCalendarioEventos.adapter = adapterlisteventos
        }


        //Começar parte do Firebase aqui <---------------------
        var g=0;


       db.collection("usuarios")
            .document(auth.currentUser!!.uid)
            .collection("gruposIds")
            .document(grupoID)
            .get()
            .addOnSuccessListener {
                admin =  it.getBoolean("admin")!!
            }


        db.collection("grupos").document(grupoID).collection("eventos").get()
            .addOnSuccessListener { eventos ->
                while(g<eventos.documents.size) {
                    if (eventos != null) {
                        var infoeventos = "${eventos.documents.get(g).data?.get("titulo").toString()}*" +
                                "${eventos.documents.get(g).data?.get("descricao").toString()}*" +
                                "${eventos.documents.get(g).data?.get("dataFim")}*${eventos.documents.get(g).id}"

                                compactCalendar.addEvent(Event((eventos.documents.get(g).data?.get("cor") as Long).toInt(),
                                    ((eventos.documents.get(g).data?.get("dataInicio") as Timestamp).seconds * 1000),
                                    infoeventos
                                ))

                                }
                                println("g : $g")
                                g += 1
                }
            }

        Thread.sleep(1000)
        adapterlisteventos.notifyDataSetChanged()


        findViewById<ImageView>(R.id.leftArrowImageGrupo)?.setOnClickListener{
            compactCalendar!!.scrollLeft()
        }

        findViewById<ImageView>(R.id.rightArrowImageGrupo)?.setOnClickListener{
            compactCalendar!!.scrollRight()
        }

        //Obtem a data atual no formato ISO
        val firstDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)

        //Converte a data atual para Ano-Mes-Dia
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = formatter.parse(firstDate)

        //Verifica qual é o mês atual em numero e converte o mesmo para extenso
        var mesinicial = ""
        mesinicial = DateTimeFormatter.ofPattern("MMMM",Locale.forLanguageTag("PT")).format(date)

        //Declara diferentes formatações da data
        val anoinicial = DateTimeFormatter.ofPattern("yyyy").format(date)

        //Declara o texto do mesdocalendario mes em extenso e o ano
        findViewById<TextView>(R.id.mesdocalendarioGrupo).text = "$mesinicial de $anoinicial"




        compactCalendar.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                listViewCalendarioEventos!!.setAdapter(null)
                eventoscalendario.clear()
                listViewCalendarioEventos.adapter = adapterlisteventos


                var formatter = SimpleDateFormat("dd/MM/yyyy",Locale.forLanguageTag("PT"));
                var dataAtualClickedString = formatter.format(Date(dateClicked.time))


                for (Event in compactCalendar.getEvents(dateClicked.time)) {
                    eventoscalendario.add(Event(Event.color,Event.timeInMillis,Event.data))
                    println(eventoscalendario)
                    println(eventoscalendario.size)
                }


                adapterlisteventos.notifyDataSetChanged()
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                findViewById<TextView>(R.id.mesdocalendarioGrupo)?.text = dateFormatMonth.format(firstDayOfNewMonth!!)
            }
        })





        findViewById<ImageView>(R.id.buttonCloseCalendarGrupo).setOnClickListener {
            finish()
        }

    }






    inner class CalendarioEventosAdapter : BaseAdapter() {


        override fun getCount(): Int {
            return eventoscalendario.size
        }

        override fun getItem(position: Int): Any {
            return eventoscalendario[position]

        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {


            val rowView = layoutInflater.inflate(R.layout.row_calendario, parent, false)

            val formatterhour = SimpleDateFormat("HH:mm")

            val dateEventlist = rowView.findViewById<TextView>(R.id.dateEventList)
            val titleEventList = rowView.findViewById<TextView>(R.id.titleEventList)
            val descEventList = rowView.findViewById<TextView>(R.id.descEventList)
            val horaFimEventList = rowView.findViewById<TextView>(R.id.horaFimEventList)
            val dataEventList = rowView.findViewById<TextView>(R.id.dataEventList)
            val idEventList = rowView.findViewById<TextView>(R.id.idEventList)
            var removeEventList = rowView.findViewById<ImageView>(R.id.removeEvent)

            //Obtem a data do evento e separa-o em uma lista com 2 strings
            val datamaster = eventoscalendario[position].data.toString()
            val datadescricaotitulo : List<String> = datamaster.split("*")

            val cardView = rowView.findViewById<CardView>(R.id.card_view);
            val buttonShowColor = rowView.findViewById<ImageButton>(R.id.buttonShowColorEvent)

            val formatterdate = SimpleDateFormat("dd 'de' MMMM 'de' yyyy",Locale.forLanguageTag("PT"))

            //0 - Titulo do Evento , 1 - Descricao do Evento , 2- Hora fim , 3- ID do Evento
            dateEventlist.text = formatterhour.format(Date(eventoscalendario[position].timeInMillis))
            titleEventList.text = datadescricaotitulo[0]
            descEventList.text = datadescricaotitulo[1]
            horaFimEventList.text = datadescricaotitulo[2]
            idEventList.text = datadescricaotitulo[3]
            dataEventList.text = formatterdate.format(Date(eventoscalendario[position].timeInMillis))

            buttonShowColor.setBackgroundTintList(ColorStateList.valueOf(eventoscalendario[position].color))



            if(admin == true){
                removeEventList.visibility = View.VISIBLE
            }
            else{
                removeEventList.visibility = View.INVISIBLE
            }

            removeEventList.setOnClickListener {
                eventBeingRemoved = idEventList.text.toString()
                    SimpleDialog.build()
                        .title("Eliminar Evento")
                        .msgHtml("Tem mesmo a certeza que quer eliminar o evento " + "<b>" + titleEventList.text + "</b> ")
                        .pos("Sim")
                        .neg("Nao")
                        .cancelable(false)
                        .show(this@EventCalendarGroupActivity, "EliminarEvento")
            }


            cardView.setOnClickListener {
                val intent = Intent(baseContext, InfoEventActivity::class.java)
                intent.putExtra("horainicio", dateEventlist.text as String)
                intent.putExtra("titulo", titleEventList.text as String)
                intent.putExtra("descricao", descEventList.text as String)
                intent.putExtra("tipo", "grupo")
                intent.putExtra("infotipo", grupoID)
                intent.putExtra("horafim", horaFimEventList.text as String)
                intent.putExtra("data", dataEventList.text as String)
                intent.putExtra("idevento", idEventList.text as String)
                intent.putExtra("cor", eventoscalendario[position].color)
                println("Dados da Intent :  ${dateEventlist.text} + ${titleEventList.text } + ${descEventList.text} + ${typeEventList.text} +${infoTypeEventList.text} + ${horaFimEventList.text} + ${dataEventList.text}")
                startActivity(intent)
            }




            println(datadescricaotitulo)
            Thread.sleep(100)
            adapterlisteventos.notifyDataSetChanged()
            return rowView
        }
    }


    override fun onResult(dialogTag: String, which: Int, extras: Bundle): Boolean {
        if(dialogTag == "EliminarEvento" && which == BUTTON_NEGATIVE){
            return false
        }
        else if(dialogTag == "EliminarEvento" && which == BUTTON_POSITIVE){
            db.collection("grupos").document(grupoID).collection("eventos").document(eventBeingRemoved).delete()
            return true
        }
        return false
    }

}
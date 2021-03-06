package com.example.projetoam2.Fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.projetoam2.Model.Eventos
import com.example.projetoam2.R
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.fragment_calendario.*
import kotlinx.android.synthetic.main.row_calendario.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.backgroundColor
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import android.app.Activity

import android.content.Intent
import com.example.projetoam2.CreatePersonalEventActivity
import com.example.projetoam2.InfoEventActivity
import com.google.firebase.firestore.ListenerRegistration
import eltos.simpledialogfragment.SimpleCheckDialog
import eltos.simpledialogfragment.SimpleDateDialog
import eltos.simpledialogfragment.SimpleDialog
import eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener.BUTTON_NEGATIVE
import eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener.BUTTON_POSITIVE
import eltos.simpledialogfragment.SimpleTimeDialog
import eltos.simpledialogfragment.color.SimpleColorDialog
import kotlinx.android.synthetic.main.activity_create_event.*

var gruposListenerEventEdit : ListenerRegistration? = null
var privateListenerEventEdit : ListenerRegistration? = null
class CalendarioFragment : Fragment(),SimpleDialog.OnDialogResultListener {

    var eventoscalendario: MutableList<Event> = arrayListOf()
    lateinit var adapterlisteventos : CalendarioEventosAdapter
    private val dateFormatMonth = SimpleDateFormat("MMMM 'de' yyyy", Locale.forLanguageTag("PT"))
    var compactCalendar: CompactCalendarView? = null
    val db = Firebase.firestore
    val auth = Firebase.auth
    var arrayChats : MutableList<String> = arrayListOf()
    //no typeofEventRemoval , 0 -> Tipo de Evento , 1-> ID do Evento , 2-> ID do Grupo
    var typeofEventRemoval: Array<String> = arrayOf("","","")
    var adminGrupo = false
    var pos = 0
    var firstSearch = 0
    var calendarlastDateClicked : Date = Date(0)
    lateinit var listViewCalendarioEventos : ListView

    fun Events() {
        // Required empty public constructor!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        Locale.setDefault(Locale("pt"))

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_calendario, container, false)
        compactCalendar = view.findViewById<View>(R.id.compactcalendar_view) as CompactCalendarView
        compactCalendar!!.setUseThreeLetterAbbreviation(true)

        //Por o primeiro dia da semana como Domingo
        compactCalendar!!.setFirstDayOfWeek(2)


        arrayChats.clear()
        eventoscalendario.clear()

        listViewCalendarioEventos = view.findViewById<ListView>(R.id.listViewCalendario)

        adapterlisteventos = CalendarioEventosAdapter()
        if (listViewCalendarioEventos != null) {
            listViewCalendarioEventos.adapter = adapterlisteventos
        }

        //Come??ar parte do Firebase aqui <---------------------

        //db.collection("grupos")
        //    .addSnapshotListener { document,error ->
        //        arrayChats.clear()
        //        eventoscalendario.clear()
        //        listViewCalendarioEventos!!.setAdapter(null)

                if(firstSearch < 2){
                    searchGroupEvent()
                    listViewCalendarioEventos.adapter = adapterlisteventos
                    adapterlisteventos.notifyDataSetChanged()
                }

        //        else if(firstSearch == 2){
        //            compactCalendar!!.removeAllEvents()
        //            searchPersonalEvent()
        //            searchGroupEvent()
        //            obtainEventsfromDay()
        //            listViewCalendarioEventos.adapter = adapterlisteventos
        //           adapterlisteventos.notifyDataSetChanged()
        //        }

        //}

        privateListenerEventEdit =
        db.collection("usuarios").document(Firebase.auth.currentUser?.uid.toString())
            .collection("eventos")
            .addSnapshotListener { document,error ->
                arrayChats.clear()
                eventoscalendario.clear()
                listViewCalendarioEventos!!.setAdapter(null)
                if(firstSearch < 2){
                    searchPersonalEvent()
                    listViewCalendarioEventos.adapter = adapterlisteventos
                    adapterlisteventos.notifyDataSetChanged()
                }
                else if(firstSearch == 2){
                    compactCalendar!!.removeAllEvents()
                    searchGroupEvent()
                    searchPersonalEvent()
                    obtainEventsfromDay()
                    listViewCalendarioEventos.adapter = adapterlisteventos
                    adapterlisteventos.notifyDataSetChanged()
                }
            }


        Thread.sleep(1000)

/*
        for(Event in eventosapagar){
            compactCalendar!!.addEvent(Event(Event.color,Event.timeInMillis,Event.data))
        }
*/


        view.findViewById<ImageView>(R.id.leftArrowImage)?.setOnClickListener{
            compactCalendar!!.scrollLeft()
        }

        view.findViewById<ImageView>(R.id.rightArrowImage)?.setOnClickListener{
            compactCalendar!!.scrollRight()
        }

        //Obtem a data atual no formato ISO
        val firstDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)

        //Converte a data atual para Ano-Mes-Dia
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = formatter.parse(firstDate)

        //Verifica qual ?? o m??s atual em numero e converte o mesmo para extenso
        var mesinicial = ""
        mesinicial = DateTimeFormatter.ofPattern("MMMM",
            Locale.forLanguageTag("PT"))
            .format(date)

        //Declara diferentes formata????es da data
        val anoinicial = DateTimeFormatter.ofPattern("yyyy").format(date)

        //Declara o texto do mesdocalendario mes em extenso e o ano
        view.findViewById<TextView>(R.id.mesdocalendario).text = "$mesinicial de $anoinicial"






        compactCalendar!!.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            //Ap??s ser clicado numa data do calend??rio , limpa a ListView e de seguida lista os diferentes eventos presentes nesse dia
            override fun onDayClick(dateClicked: Date) {
                listViewCalendarioEventos!!.setAdapter(null)
                eventoscalendario.clear()
                listViewCalendarioEventos.adapter = adapterlisteventos


                //Cria uma formata??ao separadamente para hora e data
                //obtem a data selecionada e converte a mesma de milisegundos para uma data e aplica o mesmo num textview
                //var formatter = SimpleDateFormat("dd/MM/yyyy");
                //var dataAtualClickedString = formatter.format(Date(dateClicked.time))

                calendarlastDateClicked = dateClicked

                //V?? se as cor aleatoria nao se repete a anterior
                //e para cada item que esteja no array dos eventos adiciona um evento no calendario

                //Obtem os eventos do dia e por cada evento no dia selecionado , adiciona a hora e a informa????o do Evento na ListView
                for (Event in compactCalendar!!.getEvents(dateClicked.time)) {
                    eventoscalendario.add(Event(Event.color,Event.timeInMillis,Event.data))
                    println(eventoscalendario)
                    println(eventoscalendario.size)
                }
                //Notifica ao Adapter da ListView que Data foi modificada ,para atualizar a ListView
                adapterlisteventos.notifyDataSetChanged()

            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                view.findViewById<TextView>(R.id.mesdocalendario)?.text = dateFormatMonth.format(firstDayOfNewMonth)
            }
        })


        adapterlisteventos.notifyDataSetChanged()


        val buttonaddevent = view.findViewById<FloatingActionButton>(R.id.buttonAddPersonalEvent)

        buttonaddevent.setOnClickListener {

            val itt = Intent(activity, CreatePersonalEventActivity::class.java)
            itt.putExtra("groupOrPersonal","personal")
            startActivity(itt)
            (activity as Activity?)!!.overridePendingTransition(0, 0)

        }




        return view
    }

    fun obtainEventsfromDay(){
        for (Event in compactCalendar!!.getEvents(calendarlastDateClicked.time)) {
            eventoscalendario.add(Event(Event.color,Event.timeInMillis,Event.data))
        }
        adapterlisteventos.notifyDataSetChanged()
    }

    fun searchGroupEvent(){
        var x=0;
        var z=0
        db.collection("usuarios").document(Firebase.auth.currentUser?.uid.toString()).collection("gruposIds").get().addOnSuccessListener {
                document ->
            while(x< document?.documents?.size!!){
                if (document != null) {
                    arrayChats.add(document.documents.get(x).id.replace("[","").replace("]",""))
                    println( "DocumentSnapshot of GrupoIDS associated with user: ${document.documents.get(x).data?.values.toString()}")
                }
                println("x : $x")
                println("On While , List of chats --> " +arrayChats)
                x += 1
            }
            for(arrayChat in arrayChats){
                db.collection("grupos").document(arrayChat).collection("eventos").get()
                    .addOnSuccessListener { eventos ->
                        while(z<eventos.documents.size) {
                            if (eventos != null) {
                                var infoeventos = "${eventos.documents.get(z).data?.get("titulo").toString()}*" +
                                        "${eventos.documents.get(z).data?.get("descricao").toString()}*grupo*" +
                                        "${arrayChat}*${eventos.documents.get(z).data?.get("dataFim")}*${eventos.documents.get(z).id}"

/*                                    Eventos(eventos.documents.get(z).data?.get("titulo").toString(),
                                        eventos.documents.get(z).data?.get("descricao").toString(),
                                        eventos.documents.get(z).data?.get("dataInicio") as Timestamp,
                                        eventos.documents.get(z).data?.get("dataFim") as Timestamp,
                                        eventos.documents.get(z).data?.get("cor") as Long)*/

                                compactCalendar!!.addEvent(Event((eventos.documents.get(z).data?.get("cor") as Long).toInt(),((eventos.documents.get(z).data?.get("dataInicio") as Timestamp).seconds * 1000),"${infoeventos}"))

                                //           compactCalendar!!.addEvent(Event((eventos.documents.get(z).data?.get("cor") as Long).toInt(),((eventos.documents.get(z).data?.get("dataFim") as Timestamp).seconds * 1000),"Fim de ${infoeventos}"))

                                //arrayChats.add(x, eventos.documents.get(x).data?.values.toString().replace("[", "").replace("]", "") )
                                println("DocumentSnapshot data of events on Groups: ${eventos.documents.get(z).data?.toString()}")
                                println("DocumentSnapshot data values of events on Groups : ${eventos.documents.get(z).data?.values.toString()}")
                            }
                            println("z : $z")
                            z += 1
                        }
                    }
                    .addOnFailureListener { exception ->
                        println("Had expection when searching for events on Grupos , error info: $exception")
                    }
            }
        }
        if(firstSearch<2){
            firstSearch += 1
        }

    }

    fun searchPersonalEvent(){
        var c=0
        db.collection("usuarios").document(Firebase.auth.currentUser?.uid.toString()).collection("eventos").get()
            .addOnSuccessListener { eventospessoais ->
                if(eventospessoais.documents.size>0){
                    while(c<eventospessoais.documents.size) {

                        println("DocumentSnapshot data of Personal Events: ${eventospessoais.documents.get(c).data?.toString()}")
                        println("DocumentSnapshot data values of Personal Events : ${eventospessoais.documents.get(c).data?.values.toString()}")

                        var infopersonaleventos = "${eventospessoais.documents.get(c).data?.get("titulo").toString()}*" +
                                "${eventospessoais.documents.get(c).data?.get("descricao").toString()}*" +
                                "pessoal*${Firebase.auth.currentUser?.uid.toString()}" +
                                "*${eventospessoais.documents.get(c).data?.get("dataFim")}*${eventospessoais.documents.get(c).id}"

                        compactCalendar!!.addEvent(Event((eventospessoais.documents.get(c).data?.get("cor") as Long).toInt(),((eventospessoais.documents.get(c).data?.get("dataInicio") as Timestamp).seconds * 1000),"${infopersonaleventos}"))

                        //       compactCalendar!!.addEvent(Event((eventospessoais.documents.get(c).data?.get("cor") as Long).toInt(),((eventospessoais.documents.get(c).data?.get("dataFim") as Timestamp).seconds * 1000),"Fim de ${infopersonaleventos}"))


                        println("c : $c")
                        c += 1
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Had expection when searching for events in usuarios , error info: $exception")
            }
        if(firstSearch<2){
            firstSearch += 1
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(gruposListenerEventEdit != null){
            gruposListenerEventEdit!!.remove()
        }
        else if(privateListenerEventEdit != null){
            privateListenerEventEdit!!.remove()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if(gruposListenerEventEdit != null){
            gruposListenerEventEdit!!.remove()
        }
        else if(privateListenerEventEdit != null){
            privateListenerEventEdit!!.remove()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch (Dispatchers.Main){
            adapterlisteventos.notifyDataSetChanged()
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
            if(gruposListenerEventEdit != null){
                gruposListenerEventEdit!!.remove()
            }


            val rowView = layoutInflater.inflate(R.layout.row_calendario, parent, false)

            val formatterhour = SimpleDateFormat("HH:mm")

            val dateEventlist = rowView.findViewById<TextView>(R.id.dateEventList)
            val titleEventList = rowView.findViewById<TextView>(R.id.titleEventList)
            val descEventList = rowView.findViewById<TextView>(R.id.descEventList)
            val horaFimEventList = rowView.findViewById<TextView>(R.id.horaFimEventList)
            val typeEventList = rowView.findViewById<TextView>(R.id.typeEventList)
            val infoTypeEventList = rowView.findViewById<TextView>(R.id.infoTypeEventList)
            val dataEventList = rowView.findViewById<TextView>(R.id.dataEventList)
            val idEventList = rowView.findViewById<TextView>(R.id.idEventList)
            var removeEventList = rowView.findViewById<ImageView>(R.id.removeEvent)

            //Obtem a data do evento e separa-o em uma lista com 2 strings
            val datamaster = eventoscalendario[position].data.toString()
            val datadescricaotitulo : List<String> = datamaster.split("*")

            val expandableView = rowView.findViewById<ConstraintLayout>(R.id.expandableView);
            val cardView = rowView.findViewById<CardView>(R.id.card_view);
            val buttonShowColor = rowView.findViewById<ImageButton>(R.id.buttonShowColorEvent)

            val formatterdate = SimpleDateFormat("dd 'de' MMMM 'de' yyyy",Locale.forLanguageTag("PT"))

            //0 - Titulo do Evento , 1 - Descricao do Evento , 2 - Tipo de Evento , 3 - User/Grupo que criou, 4- Hora fim , 5- ID do Evento
            dateEventlist.text = formatterhour.format(Date(eventoscalendario[position].timeInMillis))
            titleEventList.text = datadescricaotitulo[0]
            descEventList.text = datadescricaotitulo[1]
            typeEventList.text = datadescricaotitulo[2]
            infoTypeEventList.text = datadescricaotitulo[3]
            horaFimEventList.text = datadescricaotitulo[4]
            idEventList.text = datadescricaotitulo[5]
            dataEventList.text = formatterdate.format(Date(eventoscalendario[position].timeInMillis))

            buttonShowColor.setBackgroundTintList(ColorStateList.valueOf(eventoscalendario[position].color))

            if(typeEventList.text == "grupo"){
                db.collection("usuarios")
                    .document(auth.currentUser!!.uid)
                    .collection("gruposIds")
                    .document(infoTypeEventList.text.toString())
                    .get()
                    .addOnSuccessListener {
                        adminGrupo = it.getBoolean("admin")!!
                        if(adminGrupo == true){
                            removeEventList.visibility = View.VISIBLE
                        }
                        else if(adminGrupo == false){
                            removeEventList.visibility = View.INVISIBLE
                        }
                    }


            }
            else if(typeEventList.text =="pessoal"){
                removeEventList.visibility = View.VISIBLE
            }

            removeEventList.setOnClickListener {
                typeofEventRemoval[0] = typeEventList.text.toString()
                typeofEventRemoval[1] = idEventList.text.toString()
                typeofEventRemoval[2] = infoTypeEventList.text.toString()
                pos = position
                    SimpleDialog.build()
                        .title("Eliminar Evento")
                        .msgHtml("Tem mesmo a certeza que quer eliminar o evento " + "<b>" + titleEventList.text + "</b> ")
                        .pos("Sim")
                        .neg("Nao")
                        .cancelable(false)
                        .show(this@CalendarioFragment, "EliminarEvento")
            }



            cardView.setOnClickListener {
                val intent = Intent(context, InfoEventActivity::class.java)
                intent.putExtra("horainicio", dateEventlist.text as String)
                intent.putExtra("titulo", titleEventList.text as String)
                intent.putExtra("descricao", descEventList.text as String)
                intent.putExtra("tipo", typeEventList.text as String)
                intent.putExtra("infotipo", infoTypeEventList.text as String)
                intent.putExtra("horafim", horaFimEventList.text as String)
                intent.putExtra("data", dataEventList.text as String)
                intent.putExtra("idevento", idEventList.text as String)
                intent.putExtra("cor", eventoscalendario[position].color)
                println("Dados da Intent :  ${dateEventlist.text} + ${titleEventList.text } + ${descEventList.text} + ${typeEventList.text} +${infoTypeEventList.text} + ${horaFimEventList.text} + ${dataEventList.text}")

                gruposListenerEventEdit =
                    db.collection("grupos").document(infoTypeEventList.text as String).collection("eventos")
                        .addSnapshotListener { document, error ->
                                arrayChats.clear()
                                eventoscalendario.clear()
                                listViewCalendarioEventos!!.setAdapter(null)
                                compactCalendar!!.removeAllEvents()
                                listViewCalendarioEventos.adapter = adapterlisteventos
                                searchPersonalEvent()
                                searchGroupEvent()
                                obtainEventsfromDay()
                                adapterlisteventos.notifyDataSetChanged()
                        }
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
            if(typeofEventRemoval[0] == "pessoal"){
                db.collection("usuarios").document(Firebase.auth.currentUser?.uid.toString()).collection("eventos").document(typeofEventRemoval[1]).delete()
                adapterlisteventos.notifyDataSetChanged()
            }
            else if(typeofEventRemoval[0] == "grupo"){
                db.collection("grupos").document(typeofEventRemoval[2]).collection("eventos").document(typeofEventRemoval[1]).delete()
                arrayChats.clear()
                eventoscalendario.clear()
                listViewCalendarioEventos!!.setAdapter(null)
                compactCalendar!!.removeAllEvents()
                listViewCalendarioEventos.adapter = adapterlisteventos
                searchPersonalEvent()
                searchGroupEvent()
                obtainEventsfromDay()
                adapterlisteventos.notifyDataSetChanged()
            }
            else{
                Toast.makeText(context,"Nao foi possivel identificar o tipo deste evento",Toast.LENGTH_LONG)
                return false
            }
            return true
        }
        return false
    }

}
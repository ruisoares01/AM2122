package com.example.projetoam2.Fragments

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
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
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


class CalendarioFragment : Fragment() {

    var eventoscalendario: MutableList<Event> = arrayListOf()
    var eventoslista: MutableList<Eventos> = arrayListOf()
    lateinit var adapterlisteventos : CalendarioEventosAdapter
    private val dateFormatMonth = SimpleDateFormat("MMMM- yyyy", Locale.getDefault())
    var compactCalendar: CompactCalendarView? = null
    val db = Firebase.firestore
    var arrayChats : MutableList<String> = arrayListOf()


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
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_calendario, container, false)
        compactCalendar = view.findViewById<View>(R.id.compactcalendar_view) as CompactCalendarView
        compactCalendar!!.setUseThreeLetterAbbreviation(true)

        //Por o primeiro dia da semana como Domingo
        compactCalendar!!.setFirstDayOfWeek(2)


        arrayChats.clear()

        val text = view.findViewById<TextView>(R.id.mesdocalendario)
        text.text = "AAAAA"

        val listViewCalendarioEventos = view.findViewById<ListView>(R.id.listViewCalendario)

        adapterlisteventos = CalendarioEventosAdapter()
        if (listViewCalendarioEventos != null) {
            listViewCalendarioEventos.adapter = adapterlisteventos
        }




  //      var listevent: MutableList<String> = arrayListOf()

        //Declara uma MutableList de tipo inteiro que contem um array com cores
 /*       val coreslist: MutableList<Int> = arrayListOf(
            Color.parseColor("#ff0000"), Color.parseColor("#000000"),
            Color.parseColor("#FF8F00"), Color.parseColor("#038a34"),
            Color.parseColor("#00ff00"), Color.parseColor("#8591ff"),
            Color.parseColor("#00fff2"), Color.parseColor("#FFFF00"),
            Color.parseColor("#0040ff"), Color.parseColor("#6AA84F"),
            Color.parseColor("#ae00ff"), Color.parseColor("#97D6EC"),
            Color.parseColor("#ff0077"),
            Color.parseColor("#37474F"),
        )*/

        //Começar parte do Firebase aqui <---------------------
        var x=0;
        var z=0
        var stringdocument = ""

        db.collection("usuarios").document(Firebase.auth.currentUser?.uid.toString()).collection("gruposIds").get()
            .addOnSuccessListener { document ->
                while(x<document.documents.size){
                    if (document != null) {
                        arrayChats.add(document.documents.get(x).id.replace("[","").replace("]",""))
                        println( "DocumentSnapshot data: ${document.documents.get(x).data?.values.toString()}")
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
                                    var titulo_descricao = "${eventos.documents.get(z).data?.get("titulo").toString()} * ${eventos.documents.get(z).data?.get("descricao").toString()} "

                                    Eventos(eventos.documents.get(z).data?.get("titulo").toString(),
                                        eventos.documents.get(z).data?.get("descricao").toString(),
                                        eventos.documents.get(z).data?.get("dataInicio") as Timestamp,
                                        eventos.documents.get(z).data?.get("dataFim") as Timestamp,
                                        eventos.documents.get(z).data?.get("cor") as Long)

                                    compactCalendar!!.addEvent(Event((eventos.documents.get(z).data?.get("cor") as Long).toInt(),((eventos.documents.get(z).data?.get("dataFim") as Timestamp).seconds * 1000),"Inicio de ${titulo_descricao}"))

                                    compactCalendar!!.addEvent(Event((eventos.documents.get(z).data?.get("cor") as Long).toInt(),((eventos.documents.get(z).data?.get("dataInicio") as Timestamp).seconds * 1000),"Fim de ${titulo_descricao}"))

                                    //arrayChats.add(x, eventos.documents.get(x).data?.values.toString().replace("[", "").replace("]", "") )
                                    println("DocumentSnapshot data: ${eventos.documents.get(z).data?.toString()}")
                                    println("DocumentSnapshot data values : ${eventos.documents.get(z).data?.values.toString()}")
                                }
                                println("x : $z")
                                println("On While , List of chats --> " + arrayChats)
                                z += 1
                                println("${eventos.metadata} <------ metadata do IT a buscar os eventos")
                                println("${eventos.documents} <------ documents do IT a buscar os eventos")
                                println("On for , each chat --> " + arrayChat)
                            }
                        }
                        .addOnFailureListener { exception ->
                            println("Had expection when searching for events on Chat , error info: $exception")
                        }
                }

            }
            .addOnFailureListener { exception ->
                println("Had expection when searching for salachats in usuarios , error info: $exception")
            }







        //Escolhe um item (neste caso cor) aleatorio do array
 //       var coraleatoria = coreslist.random()



/*        var eventosapagar : MutableList<Event> = arrayListOf(
            Event(coraleatoria, 1607040400000L, "Teachers' Professional Day * Welcome to Teachers Day"),
            Event(coraleatoria, 1624273932000, "Tessdate * Description Test"),
            Event(coraleatoria, 1624274932000, "Teste * MegaTest"),
            Event(coraleatoria, 1623082189198, "Dia 7 * Ja passou"),
            Event(coraleatoria, 1626562800000, "Inicio Sao Joao * Um mes atrasado"),
            Event(coraleatoria, 1626822000000, "Fim Sao Joao * Um mes atrasado"),
            Event(Color.RED,1640544916488,"Após Natal * Apos Natal")
        )




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

        //Verifica qual é o mês atual em numero e converte o mesmo para extenso
        var mesinicial = ""
        when (DateTimeFormatter.ofPattern("MM").format(date)) {
            "01" -> mesinicial = "January"
            "02" -> mesinicial = "February"
            "03" -> mesinicial = "March"
            "04" -> mesinicial = "April"
            "05" -> mesinicial = "May"
            "06" -> mesinicial = "June"
            "07" -> mesinicial = "July"
            "08" -> mesinicial = "August"
            "09" -> mesinicial = "September"
            "10" -> mesinicial = "October"
            "11" -> mesinicial = "November"
            "12" -> mesinicial = "December"
        }
        //Declara diferentes formatações da data
        val anoinicial = DateTimeFormatter.ofPattern("yyyy").format(date)

        //Declara o texto do mesdocalendario mes em extenso e o ano
        view.findViewById<TextView>(R.id.mesdocalendario).text = "$mesinicial- $anoinicial"




        compactCalendar!!.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            //Após ser clicado numa data do calendário , limpa a ListView e de seguida lista os diferentes eventos presentes nesse dia
            override fun onDayClick(dateClicked: Date) {
                listViewCalendarioEventos!!.setAdapter(null)
                eventoscalendario.clear()
                listViewCalendarioEventos.adapter = adapterlisteventos


                //Cria uma formataçao separadamente para hora e data
                //obtem a data selecionada e converte a mesma de milisegundos para uma data e aplica o mesmo num textview
                var formatter = SimpleDateFormat("dd/MM/yyyy");
                var dataAtualClickedString = formatter.format(Date(dateClicked.time))


                //Vê se as cor aleatoria nao se repete a anterior
                //e para cada item que esteja no array dos eventos adiciona um evento no calendario

                //Obtem os eventos do dia e por cada evento no dia selecionado , adiciona a hora e a informação do Evento na ListView
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


        return view
    }


    fun grabevents(){
        var z = 0

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


            val rowView = layoutInflater.inflate(R.layout.row_calendario, parent, false)

            val formatterhour = SimpleDateFormat("hh:mm")

            val dateEventlist = rowView.findViewById<TextView>(R.id.dateEventList)
            val titleEventList = rowView.findViewById<TextView>(R.id.titleEventList)
            val descEventList = rowView.findViewById<TextView>(R.id.descEventList)

            //Obtem a data do evento e separa-o em uma lista com 2 strings
            val datamaster = eventoscalendario[position].data.toString()
            val datadescricaotitulo : List<String> = datamaster.split("*")

            val expandableView = rowView.findViewById<ConstraintLayout>(R.id.expandableView);
            val buttonExpand = rowView.findViewById<Button>(R.id.buttonExpand);
            val cardView = rowView.findViewById<CardView>(R.id.card_view);

            buttonExpand.setOnClickListener(View.OnClickListener {
                if (expandableView.getVisibility() === View.GONE) {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                    expandableView.setVisibility(View.VISIBLE)
                    buttonExpand.setBackgroundResource(R.drawable.ic_baseline_arrow_up_24)
                } else {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                    expandableView.setVisibility(View.GONE)
                    buttonExpand.setBackgroundResource(R.drawable.ic_baseline_arrow_down_24)
                }
            })

            dateEventlist.text = formatterhour.format(Date(eventoscalendario[position].timeInMillis))
            titleEventList.text = datadescricaotitulo[0]
            descEventList.text = datadescricaotitulo[1]
            //cardView.setBackgroundColor(eventoscalendario[position].color)


            return rowView
        }
    }


}
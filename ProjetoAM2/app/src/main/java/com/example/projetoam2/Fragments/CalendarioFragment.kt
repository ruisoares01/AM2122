package com.example.projetoam2.Fragments

import android.graphics.Color
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.projetoam2.Model.Eventos
import com.example.projetoam2.R
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class CalendarioFragment : Fragment() {

    var eventoscalendario: MutableList<Event> = arrayListOf()
    var eventoslista: MutableList<Eventos> = arrayListOf()
    lateinit var adapterlisteventos : CalendarioEventosAdapter
    private val dateFormatMonth = SimpleDateFormat("MMMM- yyyy", Locale.getDefault())
    var compactCalendar: CompactCalendarView? = null

    fun Events() {
        // Required empty public constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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



        val text = view.findViewById<TextView>(R.id.mesdocalendario)
        text.text = "AAAAA"

        val listViewCalendarioEventos = view.findViewById<ListView>(R.id.listViewCalendario)

        adapterlisteventos = CalendarioEventosAdapter()
        if (listViewCalendarioEventos != null) {
            listViewCalendarioEventos.adapter = adapterlisteventos
        }




        var listevent: MutableList<String> = arrayListOf()

        //Declara uma MutableList de tipo inteiro que contem um array com cores
        val coreslist: MutableList<Int> = arrayListOf(
            Color.parseColor("#ff0000"), Color.parseColor("#000000"),
            Color.parseColor("#FF8F00"), Color.parseColor("#038a34"),
            Color.parseColor("#00ff00"), Color.parseColor("#8591ff"),
            Color.parseColor("#00fff2"), Color.parseColor("#FFFF00"),
            Color.parseColor("#0040ff"), Color.parseColor("#6AA84F"),
            Color.parseColor("#ae00ff"), Color.parseColor("#97D6EC"),
            Color.parseColor("#ff0077"),
            Color.parseColor("#37474F"),
        )

        //Escolhe um item (neste caso cor) aleatorio do array
        var coraleatoria = coreslist.random()



        var eventosapagar : MutableList<Event> = arrayListOf(
            Event(coraleatoria, 1607040400000L, "Teachers' Professional Day * Welcome to Teachers Day"),
            Event(coraleatoria, 1624273932000, "Tessdate * Description Test"),
            Event(coraleatoria, 1624274932000, "Teste * MegaTest"),
            Event(coraleatoria, 1623082189198, "Dia 7 * Ja passou"),
            Event(coraleatoria, 1626562800000, "Inicio Sao Joao * Um mes atrasado"),
            Event(coraleatoria, 1626822000000, "Fim Sao Joao * Um mes atrasado")
        )

        for(Event in eventosapagar){
            var coranterior = coraleatoria
            coraleatoria = coreslist.random()
            if(coranterior==coraleatoria){coraleatoria = coreslist.random()}
            compactCalendar!!.addEvent(Event(coraleatoria,Event.timeInMillis,Event.data))
        }





        view.findViewById<ImageView>(R.id.leftArrowImage)?.setOnClickListener{
            compactCalendar!!.scrollLeft()
        }

        view.findViewById<ImageView>(R.id.rightArrowImage)?.setOnClickListener{
            compactCalendar!!.scrollRight()
        }




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


/*    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val view: View = inflater.inflate(R.layout.row_calendario, container, false)

        //Declara o calendario
        val compactCalendar = view.findViewById(R.id.compactcalendar_view) as CompactCalendarView

        //Por o primeiro dia da semana como Domingo
        compactCalendar.setFirstDayOfWeek(2)

        //Fazer com o calendario use somente a abriviação de 3 letras dos dias da semana
        compactCalendar.setUseThreeLetterAbbreviation(true)

        val listViewCalendarioEventos = view.findViewById<ListView>(R.id.listViewCalendario)

        adapterlisteventos = CalendarioEventosAdapter()
        if (listViewCalendarioEventos != null) {
            listViewCalendarioEventos.adapter = adapterlisteventos
        }












        //println(str)


        //Para testes declara uma MutableList de tipo Evento que contem um array com eventos
        /*       var eventosapagar : MutableList<Event> = arrayListOf(
                   Event(coraleatoria, 1607040400000L, "Teachers' Professional Day * Welcome to Teachers Day"),
                   Event(coraleatoria, 1624273932000, "Tessdate * Description Test"),
                   Event(coraleatoria, 1624274932000, "Teste * MegaTest"),
                   Event(coraleatoria, 1623082189198, "Dia 7 * Ja passou"),
                   Event(coraleatoria, 1626562800000, "Inicio Sao Joao * Um mes a frente"),
                   Event(coraleatoria, 1626822000000, "Fim Sao Joao * Um mes a frente")
               )


               //Vê se as cor aleatoria nao se repete a anterior
               //e para cada item que esteja no array dos eventos adiciona um evento no calendario
               for(Event in eventosapagar){
                   var coranterior = coraleatoria
                   coraleatoria = coreslist.random()
                   if(coranterior==coraleatoria){coraleatoria = coreslist.random()}
                   compactCalendar!!.addEvent(Event(coraleatoria,Event.timeInMillis,Event.data))
               }*/




            //Após ser mudado de mês , obtem qual é o primeiro dia do mes e formata o mesmo e insere o mesmo numa textview
            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                view.findViewById<TextView>(R.id.mesdocalendario)?.text = dateFormatMonth.format(firstDayOfNewMonth)
            }
        })



        // Inflate the layout for this fragment
        return view
    }



*/





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
            cardView.setBackgroundColor(eventoscalendario[position].color)


            return rowView
        }
    }


}
package com.example.projetoam2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.example.projetoam2.Model.User

class ListarPerfis : AppCompatActivity() {

    private lateinit var backButton : ImageButton

    var listView : ListView? = null
    lateinit var adapter : PerfisAdapter
    var perfis : MutableList<User> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_perfis)

        backButton = findViewById(R.id.buttonBack)

        //voltar para a activity anterior
        backButton.setOnClickListener {
            val intent = Intent(this@ListarPerfis, LoginActivity::class.java)
            startActivity(intent)
        }


        //encontrar os ids do xml
        listView = findViewById<ListView>(R.id.listViewPerfis)
        adapter = PerfisAdapter()
        listView?.adapter = adapter

    }

    inner class PerfisAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return perfis.size
        }

        override fun getItem(position: Int): Any {
            return perfis[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_perfis, parent, false)

            val textViewNome = rowView.findViewById<TextView>(R.id.textNome)

            textViewNome.text = perfis[position].nome

            return rowView
        }
    }
}
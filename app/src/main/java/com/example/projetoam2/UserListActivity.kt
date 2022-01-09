package com.example.projetoam2

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Fragments.CalendarioFragment
import com.example.projetoam2.Fragments.Users
import com.example.projetoam2.Model.Eventos
import com.example.projetoam2.Model.User
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UserListActivity : AppCompatActivity() {


    var alluserslist: MutableList<User> = arrayListOf()
    var usersLista: MutableList<User> = arrayListOf()
    lateinit var adapterlistusers : AllUsersAdapter

    //variaveis
    private lateinit var auth: FirebaseAuth

    //firestore
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userlist)

        val listView = findViewById<ListView>(R.id.listViewwAll)
        val searchView = findViewById<SearchView>(R.id.searchView)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //hide action bar
        supportActionBar!!.hide()

        adapterlistusers = AllUsersAdapter()
        if (listView != null) {
            listView.adapter = adapterlistusers
        }



        db.collection("usuarios").get().addOnSuccessListener { documents ->
            //get all the documents
            for (document in documents) {
                val user = document.toObject(User::class.java)
                if (auth.currentUser?.uid != user.uid) {
                    usersLista.add(User(user.uid,user.nome,user.email,user.naluno,user.curso,user.morada,user.linkfoto,user.online))
                    adapterlistusers.notifyDataSetChanged()
                }
            }
            alluserslist.addAll(usersLista)
        }

        adapterlistusers.notifyDataSetChanged()



        searchView.queryHint = "Pesquisar por Utilizador"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                //Our Search text
                println("chaos antes de escrever algo , o userlista esta assim ${usersLista}")
                val text = newText!!.toLowerCase(Locale.getDefault())
                //Here We Clear Both ArrayList because We update according to Search query.
                //     image.clear()
                usersLista.clear()
                if (text.length == 0) {
                    /*If Search query is Empty than we add all temp data into our main ArrayList
                    We store Value in temp in Starting of Program.
                    */
                    usersLista.addAll(alluserslist)
                } else {

                    for (i in 0 until alluserslist.size) {

                        println(alluserslist.size)

                        /*
                        If our Search query is not empty than we Check Our search keyword in Temp ArrayList.
                        if our Search Keyword in Temp ArrayList than we add to our Main ArrayList
                        */

                        if (alluserslist[i].nome.toLowerCase(Locale.getDefault()).contains(text)) {

                            //   image.add(tempArrayList.get(i))
                            usersLista.add(alluserslist[i])
                            adapterlistusers.notifyDataSetChanged()

                        }

                    }
                }
                println("text: " + text)
                println("Users Lista : ${usersLista}")
                println("ALL users List : ${alluserslist}")
                //This is to notify that data change in Adapter and Reflect the changes.
                adapterlistusers.notifyDataSetChanged()


                return false
            }
        })

    }




   inner class AllUsersAdapter : BaseAdapter() {
       override fun getCount(): Int {
           return usersLista.size
       }

       override fun getItem(position: Int): Any {
           return usersLista[position]

       }

       override fun getItemId(position: Int): Long {
           return 0
       }

       override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

           val rowView = layoutInflater.inflate(R.layout.user_layout, parent, false)

           rowView.findViewById<TextView>(R.id.text_name).text = usersLista[position].nome

           var photo = rowView.findViewById<CircleImageView>(R.id.imageView3)
           Picasso.get().load(usersLista[position].linkfoto).into(photo)

           val online_status = rowView.findViewById<ImageButton>(R.id.online_status)

           if(usersLista[position].online == true){
               online_status.setVisibility(View.VISIBLE)
               online_status.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN))}
           else if(usersLista[position].online == false){
               online_status.setVisibility(View.VISIBLE)
               online_status.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY))}
           else{online_status.setVisibility(View.INVISIBLE)}

           rowView.setOnClickListener {
               val intent = Intent(baseContext, ChatActivity::class.java)

               intent.putExtra("name", usersLista[position].nome)
               intent.putExtra("uid", usersLista[position].uid)
               intent.putExtra("email", usersLista[position].email)
               intent.putExtra("linkfoto", usersLista[position].linkfoto)
               intent.putExtra("nAluno", usersLista[position].naluno)
               intent.putExtra("curso", usersLista[position].curso)
               intent.putExtra("morada", usersLista[position].morada)
               intent.putExtra("status", usersLista[position].online)

               startActivity(intent)
           }

           return rowView
       }


   }

}
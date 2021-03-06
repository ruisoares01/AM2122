package com.example.projetoam2.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.*
import com.example.projetoam2.Groups.GroupActivity
import com.example.projetoam2.Model.GroupList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.horizontalMargin
import org.jetbrains.anko.padding
import org.jetbrains.anko.verticalMargin
import android.util.DisplayMetrics




var chatgrupoupdate : ListenerRegistration? = null
var displayMetrics : DisplayMetrics? = null

class HomeGruposFragment : Fragment() {

    private val adapter = GroupAdapter<ViewHolder>()
    val db = Firebase.firestore
    var arrayGrupos : MutableList<String> = arrayListOf()


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home_grupos, container, false)
        var userRecyclerViewGrupos = view.findViewById<RecyclerView>(R.id.userRecyclerViewwGrupos)

        val buttonHomeGroupsGrupos = view.findViewById<Button>(R.id.buttonGrupos)
        val buttonHomeHomeGrupos = view.findViewById<Button>(R.id.buttonMensagens)

        var buttonAddGrupos  = view.findViewById<FloatingActionButton>(R.id.buttonAddGrupo)

        userRecyclerViewGrupos.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerViewGrupos.adapter = adapter

        //clear the list

        displayMetrics = requireContext().resources.displayMetrics

        chatgrupoupdate =
        db.collection("usuarios").document(Firebase.auth.currentUser?.uid.toString()).collection("gruposIds").addSnapshotListener { document, error ->
            adapter.clear()
            arrayGrupos.clear()
            var x=0
                while(x< document!!.documents.size ){
                     arrayGrupos.add(document.documents.get(x).id.replace("[","").replace("]",""))
                     x += 1
                }
                for(arrayGrupo in arrayGrupos){
                    db.collection("grupos").document(arrayGrupo).get()
                        .addOnSuccessListener { grupo ->
                            adapter.add(GroupLista(GroupList(grupo.id,grupo.get("nome").toString(),grupo.get("imagemGrupo").toString())))
                        }
                }
            }
        adapter.setOnItemClickListener{ item,view ->
            val grupo = item as GroupLista
            val intent = Intent(view.context, GroupActivity::class.java)

            intent.putExtra("name", grupo.group.nome)
            intent.putExtra("uid", grupo.group.groupid)
            intent.putExtra("linkfoto", grupo.group.imagemGrupo)

            startActivity(intent)
        }

        buttonHomeHomeGrupos.setOnClickListener {
            val fragmenthome = HomeFragment()
            val fragmentManager = fragmentManager
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragmenthome)
            fragmentTransaction.commit()
        }

        buttonHomeGroupsGrupos.setOnClickListener {}

        var x1 = 0.0F
        var x2 = 0.0F
        val MIN_DISTANCE = 150


        buttonHomeGroupsGrupos.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when(event.action) {
                    MotionEvent.ACTION_DOWN -> x1 = event.getX()
                    MotionEvent.ACTION_UP -> {x2 = event.getX()
                        var deltaX = x1 - x2
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            val fragmenthome = HomeFragment()
                            val fragmentManager = fragmentManager
                            val fragmentTransaction = fragmentManager!!.beginTransaction()
                            fragmentTransaction.replace(R.id.fragment_container, fragmenthome)
                            fragmentTransaction.commit()
                            return true
                        }
                    }
                }
                return false
            }
        })

        buttonAddGrupos.setOnClickListener {
            val intent = Intent(view.context, createGroup::class.java)
            startActivity(intent)
        }

        return view
    }


}

class GroupLista(val group : GroupList) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        var relativeuserlayout = viewHolder.itemView.findViewById<RelativeLayout>(R.id.relativeuserlayout)

        var nome = viewHolder.itemView.findViewById<TextView>(R.id.text_name)

        val dpheight = Math.round(10 * (displayMetrics?.xdpi?.div(DisplayMetrics.DENSITY_DEFAULT)!!))

        nome.translationY = dpheight.toFloat()

        if(group.nome?.length!! > 17){ nome.text = group.nome?.substring(0,14) + "..." }
        else{ nome.text = group.nome }

        var imgprofile = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageViewUser)
        Picasso.get().load(group.imagemGrupo).into(imgprofile)

    }

    override fun getLayout() = R.layout.user_layout
}
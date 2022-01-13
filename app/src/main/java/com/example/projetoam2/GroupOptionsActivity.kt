package com.example.projetoam2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eltos.simpledialogfragment.SimpleDialog

class GroupOptionsActivity : AppCompatActivity(), SimpleDialog.OnDialogResultListener {

    val db = Firebase.firestore
    val auth = Firebase.auth
    var useringroup: ArrayList<String> = arrayListOf()
    var groupName = ""
    var groupID = ""
    var groupPhotoLink = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupo_options)
        // Inflate the layout for this fragment

        groupName = intent.extras?.getString("name").toString()
        groupID = intent.extras?.getString("uid").toString()
        groupPhotoLink = intent.extras?.getString("linkfoto").toString()

        val touchListener = findViewById<View>(R.id.touchListener)

        useringroup.clear()
        val grupoOptionsConstraint = findViewById<ConstraintLayout>(R.id.grupoOptionsConstraint)

        grupoOptionsConstraint.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_down))
        grupoOptionsConstraint.setBackgroundColor(Color.parseColor("#00bfbfbf"))

        val verParticipantesOption1 = findViewById<ConstraintLayout>(R.id.opcaoGrupo1)
        val criarEventoOption2 = findViewById<ConstraintLayout>(R.id.opcaoGrupo2)
        val verEventoOpinion3 = findViewById<ConstraintLayout>(R.id.opcaoGrupo3)
        val sairGrupoOption4 = findViewById<ConstraintLayout>(R.id.opcaoGrupo4)
        val eliminarGrupoOption5 = findViewById<ConstraintLayout>(R.id.opcaoGrupo5)

        grupoOptionsConstraint.setBackgroundColor(Color.parseColor("#80000000"))

        var admin = ""
        var y1 = 0.0F
        var y2 = 0.0F
        val MIN_DISTANCE = 150

        if (groupID != null) {
            db.collection("grupos").document(groupID).get()
                .addOnSuccessListener {
                    admin = it.get("administrador").toString()

                    if(admin != auth.currentUser!!.uid){
                        criarEventoOption2.visibility = View.GONE
                        eliminarGrupoOption5.visibility = View.GONE
                    }
                    else if(admin == auth.currentUser!!.uid){
                        sairGrupoOption4.visibility = View.GONE
                    }
                }
        }


        verParticipantesOption1.setOnClickListener {
            val intent = Intent(this, GroupProfile::class.java)
            intent.putExtra("name", groupName)
            intent.putExtra("uid", groupID)
            intent.putExtra("linkfoto", groupPhotoLink)
            startActivity(intent)
        }

        criarEventoOption2.setOnClickListener {
            val itt = Intent(this, CreatePersonalEventActivity::class.java)
            itt.putExtra("groupOrPersonal","group")
            itt.putExtra("groupID" , groupID)
            itt.putExtra("groupName" , groupName)
            startActivity(itt)
        }

        verEventoOpinion3.setOnClickListener {

        }

        findViewById<ImageView>(R.id.closeOptions).setOnClickListener {
            finish()
            findViewById<ConstraintLayout>(R.id.grupoOptionsConstraint).startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_up));
        }

        sairGrupoOption4.setOnClickListener {
            SimpleDialog.build()
                .title("Sair do Grupo")
                .msgHtml("Tem mesmo a certeza que quer sair deste Grupo?")
                .pos("Sim")
                .neg("Nao")
                .cancelable(false)
                .show(this, "sairGrupo")
        }

        eliminarGrupoOption5.setOnClickListener {
            SimpleDialog.build()
                .title("Eliminar Grupo")
                .msgHtml("Tem mesmo a certeza que quer eliminar este Grupo? ("+ "<b>" + "${groupName}" + "</b>" + ")")
                .pos("Sim")
                .neg("Nao")
                .cancelable(false)
                .show(this, "eliminarGrupo")
        }

        touchListener.isClickable = true
        touchListener.setOnTouchListener { view, motionEvent ->

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> y1 = motionEvent.y
                MotionEvent.ACTION_UP -> {
                    y2 = motionEvent.y
                    val deltaY: Float = y1 - y2
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        findViewById<ConstraintLayout>(R.id.grupoOptionsConstraint).setBackgroundColor(Color.parseColor("#000000"))
                        findViewById<ConstraintLayout>(R.id.grupoOptionsConstraint).startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_up));
                        finish()
                    }
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    override fun onResult(dialogTag: String, which: Int, extras: Bundle): Boolean {
        if(dialogTag == "sairGrupo" && which == SimpleDialog.OnDialogResultListener.BUTTON_NEGATIVE ||
            dialogTag == "eliminarGrupo" && which == SimpleDialog.OnDialogResultListener.BUTTON_NEGATIVE ){
            return false
        }
        else if(dialogTag == "sairGrupo" && which == SimpleDialog.OnDialogResultListener.BUTTON_POSITIVE) {
            db.collection("grupos").document(groupID).get().addOnSuccessListener { chatcontent ->
                useringroup.addAll((chatcontent.get("userIds") as ArrayList<String>).filter { it != auth.currentUser!!.uid })
                val grupo = hashMapOf(
                    "imagemGrupo" to groupPhotoLink,
                    "nome" to groupName,
                    "userIds" to useringroup
                )

                db.collection("grupos").document(groupID)
                    .set(grupo)

                db.collection("usuarios").document(auth.currentUser!!.uid)
                    .collection("gruposIds")
                    .document(groupID)
                    .delete()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                findViewById<ConstraintLayout>(R.id.grupoOptionsConstraint).setBackgroundColor(Color.parseColor("#00bfbfbf"))
                findViewById<ConstraintLayout>(R.id.grupoOptionsConstraint).startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_up));
                Thread.sleep(100)
                startActivity(intent)
                finish()
            }
        }
        else if(dialogTag == "eliminarGrupo" && which == SimpleDialog.OnDialogResultListener.BUTTON_POSITIVE){
            db.collection("grupos")
                .document(groupID).get()
                .addOnSuccessListener { chatcontent ->
                useringroup.addAll((chatcontent.get("userIds") as ArrayList<String>))
            }
            for(user in useringroup){
                db.collection("usuarios").document(user).collection("gruposIds").document(groupID).delete()
            }
            db.collection("grupos").document(groupID).delete()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            findViewById<ConstraintLayout>(R.id.grupoOptionsConstraint).setBackgroundColor(Color.parseColor("#00bfbfbf"))
            findViewById<ConstraintLayout>(R.id.grupoOptionsConstraint).startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_up));
            Thread.sleep(100)
            startActivity(intent)
            finish()
        }
        return false
    }


    override fun onPause() {
        super.onPause()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, false)

        db.collection("usuarios").document(uid.toString()).set(user)
            .addOnSuccessListener {
                println("Offline")
            }
    }
    override fun onResume() {
        super.onResume()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, true)
        db.collection("usuarios").document(uid.toString()).set(user)
            .addOnSuccessListener {
                println("Offline")
            }
    }
}
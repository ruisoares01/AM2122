package com.example.projetoam2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eltos.simpledialogfragment.SimpleDialog


class GroupOptionsActivity : AppCompatActivity(), SimpleDialog.OnDialogResultListener {

    val db = Firebase.firestore
    val auth = Firebase.auth
    var useringroupp: ArrayList<String> = arrayListOf()
    var groupName = ""
    var groupID = ""
    var groupPhotoLink = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupo_options)
        // Inflate the layout for this fragment

        groupName = intent.extras?.getString("name").toString()
        groupID = intent.extras?.getString("uid").toString()
        groupPhotoLink = intent.extras?.getString("linkfoto").toString()

        val touchListener = findViewById<View>(R.id.touchListener)

        val grupoOptionsConstraint = findViewById<ConstraintLayout>(R.id.grupoOptionsConstraint)

        grupoOptionsConstraint.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_down))
        grupoOptionsConstraint.setBackgroundColor(Color.parseColor("#00bfbfbf"))

        val viewgroupoptions = findViewById<View>(R.id.view)

        val verParticipantesOption1 = findViewById<ConstraintLayout>(R.id.opcaoGrupo1)
        val criarEventoOption2 = findViewById<ConstraintLayout>(R.id.opcaoGrupo2)
        val verEventoOpinion3 = findViewById<ConstraintLayout>(R.id.opcaoGrupo3)
        val sairGrupoOption4 = findViewById<ConstraintLayout>(R.id.opcaoGrupo4)
        val eliminarGrupoOption5 = findViewById<ConstraintLayout>(R.id.opcaoGrupo5)
        val gerirGrupoOption6 = findViewById<ConstraintLayout>(R.id.opcaoGrupo6)

        grupoOptionsConstraint.setBackgroundColor(Color.parseColor("#80000000"))

        var admin: Boolean
        var y1 = 0.0F
        var y2 = 0.0F
        val MIN_DISTANCE = 150



        if (groupID != null) {
            db.collection("usuarios").document(auth.currentUser!!.uid).collection("gruposIds").document(groupID).get()
                .addOnSuccessListener {
                    admin = it.getBoolean("admin")!!

                    if(admin == false){
                        criarEventoOption2.visibility = View.GONE
                        eliminarGrupoOption5.visibility = View.GONE
                        gerirGrupoOption6.visibility = View.GONE
                        viewgroupoptions.updateLayoutParams<ConstraintLayout.LayoutParams> {verticalBias=1.0f}
                    }
                    else if(admin == true){
                        sairGrupoOption4.visibility = View.GONE
                        viewgroupoptions.updateLayoutParams<ConstraintLayout.LayoutParams> {verticalBias=0.4f}
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
            val intentt = Intent(this, EventCalendarGroupActivity::class.java)
            intentt.putExtra("groupID",groupID)
            intentt.putExtra("groupName",groupName)
            startActivity(intentt)
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

        gerirGrupoOption6.setOnClickListener {
            val intentt = Intent(this, GerirGrupoActivity::class.java)
            intentt.putExtra("groupID",groupID)
            intentt.putExtra("groupName",groupName)
            intentt.putExtra("linkfoto", groupPhotoLink)
            startActivity(intentt)
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
                useringroupp.addAll((chatcontent.get("userIds") as ArrayList<String>).filter { it != auth.currentUser!!.uid })
                val grupo = hashMapOf(
                    "imagemGrupo" to groupPhotoLink,
                    "nome" to groupName,
                    "userIds" to useringroupp
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
            var gd = 0
            db.collection("grupos").document(groupID).get().addOnSuccessListener { groupcontent ->
                useringroupp.addAll((groupcontent.get("userIds") as ArrayList<String>))
                for(user in useringroupp){
                    db.collection("usuarios").document(user).collection("gruposIds").document(groupID).delete()
                    gd +=1
                }
                if(gd == useringroupp.size) {
                    db.collection("grupos").document(groupID).delete()
                }
            }
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
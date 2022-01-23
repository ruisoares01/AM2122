package com.example.projetoam2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.example.projetoam2.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eltos.simpledialogfragment.SimpleDialog
import kotlinx.android.synthetic.main.activity_privado_options.*

class privado_options : AppCompatActivity() {

    val db = Firebase.firestore
    val auth = Firebase.auth

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privado_options)
        // Inflate the layout for this fragment

        val touchListener = findViewById<View>(R.id.touchListener)

        val OptionsConstraint = findViewById<ConstraintLayout>(R.id.OptionsConstraint)

        OptionsConstraint.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_down))
        OptionsConstraint.setBackgroundColor(Color.parseColor("#00bfbfbf"))

        val verPerfil = findViewById<ConstraintLayout>(R.id.opcao1)

        OptionsConstraint.setBackgroundColor(Color.parseColor("#80000000"))

        var y1 = 0.0F
        var y2 = 0.0F
        val MIN_DISTANCE = 150

        var otherUserName = ""
        var otherUserId = ""
        var linkfoto = ""
        var otherUserEmail = ""
        var otherUserN = ""
        var otherUserCurso = ""
        var otherUserMorada = ""
        var otherUserStatus = false
        val bundle = intent.extras

        //collect data
        bundle?.let {
            otherUserName = it.getString("name").toString()
            otherUserId = it.getString("uid").toString()
            otherUserEmail = it.getString("email").toString()
            otherUserN = it.getString("nAluno").toString()
            otherUserCurso = it.getString("curso").toString()
            otherUserMorada = it.getString("morada").toString()
            linkfoto = it.getString("linkfoto").toString()
            otherUserStatus = it.getBoolean("status")

        }


        verPerfil.setOnClickListener {
            val intent = Intent(this, OtherProfile::class.java)
            intent.putExtra("name", otherUserName)
            intent.putExtra("uid", otherUserId)
            intent.putExtra("email", otherUserEmail)
            intent.putExtra("nAluno", otherUserN)
            intent.putExtra("curso", otherUserCurso)
            intent.putExtra("morada", otherUserMorada)
            intent.putExtra("linkfoto", linkfoto)
            intent.putExtra("status", otherUserStatus)
            finish()
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.closeOptions).setOnClickListener {
            finish()
            findViewById<ConstraintLayout>(R.id.OptionsConstraint).startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_up));
        }


        touchListener.isClickable = true
        touchListener.setOnTouchListener { view, motionEvent ->

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> y1 = motionEvent.y
                MotionEvent.ACTION_UP -> {
                    y2 = motionEvent.y
                    val deltaY: Float = y1 - y2
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        findViewById<ConstraintLayout>(R.id.OptionsConstraint).setBackgroundColor(Color.parseColor("#000000"))
                        findViewById<ConstraintLayout>(R.id.OptionsConstraint).startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_up));
                        finish()
                    }
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }


    override fun onPause() {
        super.onPause()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, false)
        db.collection("usuarios").document(uid.toString()).set(user)
    }
    override fun onResume() {
        super.onResume()
        val uid = FirebaseAuth.getInstance().uid
        val user = User(uid.toString(), dados.nome, dados.email, dados.naluno, dados.curso, dados.morada, dados.linkfoto, true)
        db.collection("usuarios").document(uid.toString()).set(user)
    }
}


package com.example.projetoam2.Utils

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.projetoam2.ChatActivity
import com.example.projetoam2.Fragments.LatestMessageTime
import com.example.projetoam2.Fragments.Users
import com.example.projetoam2.Model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object AppUtils {

    /*public fun getUid(): String? {
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid
    }

    fun updateOnlineStatus(status:String){
        val databaseReference: DatabaseReference = FirebaseDatabase
            .getInstance()
            .getReference("usuarios")
            .child(getUid()!!)

        val map = HashMap<String,Any>()
        map["online"] = status
        databaseReference.updateChildren(map)
    }*/
}
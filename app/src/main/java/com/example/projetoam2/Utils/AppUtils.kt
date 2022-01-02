package com.example.projetoam2.Utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

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
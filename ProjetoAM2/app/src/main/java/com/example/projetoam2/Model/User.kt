package com.example.projetoam2.Model

import android.app.DownloadManager
import android.graphics.Bitmap
import com.google.firebase.firestore.QueryDocumentSnapshot

class User{

    var nome : String = ""
    var email : String = ""
    //var photo : String? = ""
    var uid : String = ""

    constructor(nome: String,email: String, uid: String){
        this.nome = nome
        //this.photo = photo
        this.email = email
        this.uid = uid
    }

    fun toHash() : HashMap<String, Any>{
        var hashMap = HashMap<String, Any>()
        hashMap.put("nome", nome)
        hashMap.put("email", email)
        hashMap.put("uid", uid)
        return hashMap
    }

    companion object{
        fun fromHash(hashMap: QueryDocumentSnapshot) : User {
            val user = User(
                hashMap["nome"] as String,
                hashMap["email"] as String,
                hashMap ["uid"] as String
            )
            return  user
        }
    }
}
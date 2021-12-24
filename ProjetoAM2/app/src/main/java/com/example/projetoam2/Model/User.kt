package com.example.projetoam2.Model

import android.app.DownloadManager
import android.graphics.Bitmap
import com.google.firebase.firestore.QueryDocumentSnapshot

class User(val uid: String, val nome: String, val email:String,val linkfoto: String)
{
    constructor() : this("", "", "","")
}
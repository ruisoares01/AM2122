package com.example.projetoam2.Model

import android.app.DownloadManager
import android.graphics.Bitmap
import com.google.firebase.firestore.QueryDocumentSnapshot

class User(val uid: String, val nome: String, val email:String, val naluno: String, val curso: String, val morada: String, val linkfoto: String, var online: Boolean)
{
    constructor() : this("", "", "", "", "", "", "", false)
}
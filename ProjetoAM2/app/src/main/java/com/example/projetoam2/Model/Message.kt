package com.example.projetoam2.Model

import java.util.*

object MessageType{
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Message {
    val time: Date
    val senderId : String
    val type: String
}
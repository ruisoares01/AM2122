package com.example.projetoam2.Model

import java.util.*

//val type check if it's a text message or a image message
data class TextMessage(val text: String,
                       override val time: Date,
                       override val senderId: String,
                       override val type: String = MessageType.TEXT)
    : Message{

        constructor() : this("", Date(0),"")
}
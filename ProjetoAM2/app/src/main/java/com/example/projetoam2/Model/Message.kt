package com.example.projetoam2.Model

<<<<<<< HEAD
class Message(val message: String, val senderUid : String, val receiverUid : String){
    constructor() : this("","","")
}

=======
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
>>>>>>> Rui

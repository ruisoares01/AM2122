package com.example.projetoam2.Model

import java.util.*


data class ImageMessage(val text: String,
                        override val time: Date,
                        override val senderId: String,
                        override val type: String = MessageType.IMAGE)
    : Message {
    constructor() : this("", Date(0), "", "")
}
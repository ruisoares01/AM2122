package com.example.projetoam2.Model

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}
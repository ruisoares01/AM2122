package com.example.projetoam2.Model


data class GroupChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}
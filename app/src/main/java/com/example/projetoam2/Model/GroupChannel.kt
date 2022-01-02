package com.example.projetoam2.Model


data class GroupChannel(val nome : String? = null, val imagemGrupo: String? = null, val userIds: MutableList<String>? = null) {


    constructor() : this(null, null, null)
}
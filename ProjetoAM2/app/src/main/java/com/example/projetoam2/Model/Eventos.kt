package com.example.projetoam2.Model

import com.google.firebase.Timestamp

class Eventos {
    var titulo          : String? = null
    var descricao       : String? = null
    var dataInicio      : Timestamp? = null
    var dataFim         : Timestamp? = null
    var cor             : String? = null

    constructor(
        titulo          : String?,
        descricao       : String?,
        dataInicio      : Timestamp?,
        dataFim         : Timestamp?,
        cor             : String?

    ) {
        this.titulo         = titulo
        this.descricao      = descricao
        this.dataInicio     = dataInicio
        this.dataFim        = dataFim
        this.cor            = cor
    }

    constructor(){

    }

    companion object{
    }
}
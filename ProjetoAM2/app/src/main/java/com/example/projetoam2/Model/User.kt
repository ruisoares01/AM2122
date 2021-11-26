package com.example.projetoam2.Model

class User{

    var uid : String? = null
    var nome : String? = null
    var email : String? = null
    var linkfoto : String? = null


    constructor(uid: String,nome: String,email: String,linkfoto: String){

        this.uid = uid
        this.nome = nome
        this.email = email
        this.linkfoto = linkfoto

    }


}
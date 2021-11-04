package com.example.projetoam2.Model

class User{

    var nome : String? = null
    var email : String? = null
    //var photo : String? = null
    var uid : String? = null

    constructor(){}

    constructor(nome: String?,email: String?, uid: String){
        this.nome = nome
        //this.photo = photo
        this.email = email
        this.uid = uid
    }
}
package com.example.projetoam2.Model

class Dados(val uid: String, val nome: String, val email:String,val naluno: String, val curso: String, val morada: String, val linkfoto: String)
{
    constructor() : this("", "", "", "", "", "", "")
}
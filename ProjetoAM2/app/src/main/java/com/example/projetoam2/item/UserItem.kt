package com.example.projetoam2.item

import android.content.Context
import com.example.projetoam2.Model.User
import com.example.projetoam2.Model.UserId
import com.example.projetoam2.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.user_layout.*


class UserItem(val person: User,
               val userId: UserId,
               private val context: Context)
    : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int){
        viewHolder.text_name.text = person.nome
    }

    override fun getLayout() = R.layout.user_layout
}
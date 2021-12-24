package com.example.projetoam2.Model

import android.content.Context
import com.example.projetoam2.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import java.text.FieldPosition

class TextMessageItem(val message: TextMessage,
                      val context: Context)

    : Item() {

        override fun bind(viewHolder: ViewHolder, position: Int){
            //TODO: placeHolder bind
        }

        override fun getLayout() = R.layout.item_text_message
}

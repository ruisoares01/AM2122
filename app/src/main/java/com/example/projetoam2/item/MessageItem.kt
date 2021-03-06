package com.example.projetoam2.item

import android.view.Gravity
import android.widget.FrameLayout
import com.example.projetoam2.Model.Message
import com.example.projetoam2.R
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import eltos.simpledialogfragment.SimpleDialog
import kotlinx.android.synthetic.main.item_text_message.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.wrapContent
import java.text.SimpleDateFormat


abstract class MessageItem(private val message: Message)
    : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        setTimeText(viewHolder)
        setMessageRootGravity(viewHolder)
    }

    private fun setTimeText(viewHolder: ViewHolder){
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        viewHolder.textView_message_time.text = dateFormat.format(message.time)
    }

    private fun setMessageRootGravity(viewHolder: ViewHolder){
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid){
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rounded_corners
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.END)
                this.layoutParams = lParams
            }
        }
        else{
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rect_round_msg_received
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
                this.layoutParams = lParams
            }
        }
    }
}
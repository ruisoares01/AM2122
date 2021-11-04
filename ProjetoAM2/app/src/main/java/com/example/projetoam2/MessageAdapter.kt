package com.example.projetoam2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Model.UserAdapter
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val item_received = 1
    val item_sent = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1){
            // inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.received, parent,false)
            return ReceiveViewHolder(view)

        }else {
            // inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent,false)
            return SentViewHolder(view)

        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]

        if(holder.javaClass == SentViewHolder::class.java){
            // process for the sent view holder
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message


        }else{
            // process for the receiver view holder
            val viewHolder = holder as ReceiveViewHolder
            holder.receivedMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){

            return item_sent

        }else {

            return item_received

        }

    }

    override fun getItemCount(): Int {

        return messageList.size

    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val sentMessage = itemView.findViewById<TextView>(R.id.text_sent_message)

    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val receivedMessage = itemView.findViewById<TextView>(R.id.text_received_message)

    }
}


package com.example.projetoam2.item

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.example.projetoam2.Model.TextMessage
import com.example.projetoam2.R
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import java.text.SimpleDateFormat
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.wrapContent
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import com.example.projetoam2.dados
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_text_message.view.*
import org.jetbrains.anko.matchParent

val db = Firebase.firestore

private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
private val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

class TextMessageItem(val message: TextMessage,
                      val context: Context)
    : MessageItem(message) {

        override fun bind(viewHolder: ViewHolder, position: Int ){

            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

           val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
           cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            viewHolder.textview_message_text.text = String(cipher.doFinal(Base64.decode(message.text, Base64.DEFAULT)))
            super.bind(viewHolder, position)

            val viewImgProfile = viewHolder.itemView.imgProfile

            val refUser = db.collection("usuarios")
                .document(message.senderId)
            refUser.get().addOnSuccessListener {

                if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid){
                    viewHolder.img_root.apply {
                        Picasso.get().load(it.getString("linkfoto")).into(viewImgProfile)
                        val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.END or Gravity.CENTER_VERTICAL)
                        this.layoutParams = lParams
                    }
                }
                else{
                    viewHolder.img_root.apply {
                        Picasso.get().load(it.getString("linkfoto")).into(viewImgProfile)
                        val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START or Gravity.CENTER_VERTICAL)
                        this.layoutParams = lParams
                    }
                }

            }
        }

    /* private fun deleteMessage(viewHolder: ViewHolder){
        viewHolder.layoutMessage.setOnClickListener {

        }

    }   */


        override fun getLayout() = R.layout.item_text_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean{
        if (other !is TextMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean{
        return isSameAs(other as TextMessageItem)
    }
}

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


private val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
private val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
private val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

class TextMessageItem(val message: TextMessage,
                      val context: Context)
    : Item() {

        override fun bind(viewHolder: ViewHolder, position: Int ){

           // var string1 :String

            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

           val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
           cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
          //  string1 = String(cipher.doFinal(Base64.decode(message.text, Base64.DEFAULT)))

            viewHolder.textview_message_text.text = String(cipher.doFinal(Base64.decode(message.text, Base64.DEFAULT)))
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
                backgroundResource = R.drawable.rect_round_primary_color
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
                this.layoutParams = lParams
            }
        }
    }

        override fun getLayout() = R.layout.item_text_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean{
        if (other !is TextMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean{
        return isSameAs(other as TextMessageItem)
    }
}

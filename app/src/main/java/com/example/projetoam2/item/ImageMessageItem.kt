package com.example.projetoam2.item

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.projetoam2.Model.ImageMessage
import com.example.projetoam2.R
import com.example.projetoam2.Utils.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import eltos.simpledialogfragment.SimpleDialog
import kotlinx.android.synthetic.main.item_image_message.*
import kotlinx.android.synthetic.main.item_image_message.img_root
import kotlinx.android.synthetic.main.item_image_message.view.*
import kotlinx.android.synthetic.main.item_image_message.view.imgProfile
import kotlinx.android.synthetic.main.item_text_message.*
import kotlinx.android.synthetic.main.item_text_message.view.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.wrapContent


class ImageMessageItem(val message: ImageMessage,
                       val context: Context
)
    : ImageItem(message) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        val aa = FirebaseStorage.getInstance().getReference(message.text)

         Glide.with(context.applicationContext)
               .load(aa)
                .placeholder(R.drawable.ic_baseline_image_24).dontAnimate()
                .into(viewHolder.itemView.imageView_message_image)
        println(StorageUtil.pathToReference(message.text))


        val viewImgProfile = viewHolder.itemView.imgProfile
        val viewProfileName = viewHolder.itemView.nameProfile2
        val deleteMessage = viewHolder.itemView.layoutMessage

        val refUser = db.collection("usuarios")
            .document(message.senderId)
        refUser.get().addOnSuccessListener {

            if (message.senderId != FirebaseAuth.getInstance().currentUser?.uid){
                viewHolder.img_root.apply {
                    Picasso.get().load(it.getString("linkfoto")).into(viewImgProfile)
                    val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
                    this.layoutParams = lParams
                }
            }

            if (message.senderId != FirebaseAuth.getInstance().currentUser?.uid){
                viewHolder.name_root_2.apply {
                    viewProfileName.text = it.getString("nome")
                    val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
                    this.layoutParams = lParams
                }
            }
        }
    }

    override fun getLayout() = R.layout.item_image_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is ImageMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? ImageMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }
}
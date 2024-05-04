package com.example.personal_diary1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.learnandroid.loginsqlite.DBHelper

class CustomAdapter(
    context: Context,
    resource: Int,
    private val items: List<String>,
    private val encodedImages: List<String>
) :
    ArrayAdapter<String>(context, resource, items) {


    private val DB = DBHelper(context)
    val userMessages = DB.getUserMessages("sehan")
    private val datelist = DB.getdate()
    private val timelist = DB.gettime()
    private val messagelist = DB.getmessages()





    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            itemView = inflater.inflate(R.layout.list_itm, parent, false)
        }

        val textViewItem = itemView?.findViewById<TextView>(R.id.textViewListItem)
        val imageViewItem = itemView?.findViewById<ImageView>(R.id.imagedb)
        val item = items[position]

        if (position < encodedImages.size) {

            val encodedImage = encodedImages[position]
            val decodedBitmap = decodeBase64ToBitmap(encodedImage)
            val resizedBitmap = resizeBitmap(decodedBitmap, 40, 40)
            val drawable = BitmapDrawable(context.resources, resizedBitmap)
            imageViewItem?.background = drawable
        } else {
           imageViewItem?.background = null
        }
        textViewItem?.text = item
        return itemView!!
    }
    private fun decodeBase64ToBitmap(encodedImage: String): Bitmap? {
        val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
    private fun resizeBitmap(bitmap: Bitmap?, newWidth: Int, newHeight: Int): Bitmap? {
        if (bitmap == null) return null
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
    }
}

package com.example.personal_diary1

import android.content.Context
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomAdapter(context: Context, resource: Int, items: List<String>) :
    ArrayAdapter<String>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            itemView = inflater.inflate(R.layout.list_itm, parent, false)
        }
        val item = getItem(position)
        val spannableString = SpannableString(item)


        val textViewItem = itemView?.findViewById<TextView>(R.id.textViewListItem)
        textViewItem?.text = item

        return itemView!!
    }
}

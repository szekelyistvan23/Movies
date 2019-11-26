package com.stevensekler.android.movies.Fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.stevensekler.android.movies.MainActivity
import com.stevensekler.android.movies.R

/**
 * Created by Szekely Istvan on 6/27/2017.
 *
 */

class NavigationDrawerAdapter(mainActivity: MainActivity, private val result: Array<String>, private val imageId: IntArray) : BaseAdapter() {
    private val inflater: LayoutInflater = mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return result.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class Holder {
        internal var textView: TextView? = null
        internal var imageView: ImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var rowView: View? = convertView

        if (rowView == null) {

            rowView = inflater.inflate(R.layout.navigation_drawer_item, parent, false)
            val viewHolder = Holder()
            viewHolder.textView = rowView!!.findViewById(R.id.navigation_text)
            viewHolder.imageView = rowView.findViewById(R.id.navigation_image)
            rowView.tag = viewHolder
        }

        val holder = rowView.tag as Holder
        holder.textView?.text = result[position]
        holder.imageView?.setImageResource(imageId[position])

        return rowView
    }


}

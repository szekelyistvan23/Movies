package com.stevensekler.android.movies.Fragments

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar

import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.stevensekler.android.movies.DetailActivity
import com.stevensekler.android.movies.R

import java.util.ArrayList

/**
 * Created by Szekely Istvan on 8/27/17.
 *
 */

class BackdropAdapter(private val images: ArrayList<String>, private val context: Context) : RecyclerView.Adapter<BackdropAdapter.BackdropViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackdropAdapter.BackdropViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.backdrop_recycler_view, parent, false)
        return BackdropViewHolder(view)
    }


    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: BackdropAdapter.BackdropViewHolder, position: Int) {

        Picasso.with(context)
                .load(images[position])
                .placeholder(R.drawable.blank_landscape92)
                .into(holder.backdropImage)
    }

    inner class BackdropViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var backdropImage: ImageView
        lateinit var imageLoading: ProgressBar

        init {
            itemView.setOnClickListener(this)
            backdropImage = itemView.findViewById(R.id.backdrop_image)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            val s = images[position].replace("w92", "w500")
            val mainImage = (context as DetailActivity).findViewById<View>(R.id.imageView2) as ImageView
            imageLoading = context.findViewById<View>(R.id.backdrop_image_loading) as ProgressBar
            imageLoading.visibility = View.VISIBLE
            Picasso.with(context)
                    .load(s)
                    .noPlaceholder()
                    .into(mainImage, object : Callback {
                        override fun onSuccess() {
                            imageLoading.visibility = View.GONE
                        }

                        override fun onError() {
                            imageLoading.visibility = View.GONE
                        }
                    })
        }
    }
}

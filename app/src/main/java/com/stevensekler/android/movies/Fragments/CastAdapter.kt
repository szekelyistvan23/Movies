package com.stevensekler.android.movies.Fragments

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso
import com.stevensekler.android.movies.Model.Cast
import com.stevensekler.android.movies.R

import java.util.ArrayList

/**
 * Created by Szekely Istvan on 6/29/2017.
 *
 */

class CastAdapter(private val actors: ArrayList<Cast>, private val context: Context) : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.cast_recycler_view, parent, false)
        return CastViewHolder(view)
    }


    override fun getItemCount(): Int {
        return actors.size
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {

        holder.castName.text = actors[position].name
        holder.castCharacter.text = actors[position].character

        if (actors[position].profileImage == "NO_IMAGE") {
            when (actors[position].gender) {
                1 -> holder.castProfileImage.setImageResource(R.drawable.woman)
                2 -> holder.castProfileImage.setImageResource(R.drawable.man)
                else -> holder.castProfileImage.setImageResource(R.drawable.no_gender)
            }
        } else {
            Picasso.with(context)
                    .load(actors[position].profileImage)
                    .placeholder(R.drawable.blank185)
                    .into(holder.castProfileImage)
        }
    }

    inner class CastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var castName: TextView
        var castCharacter: TextView
        var castProfileImage: ImageView

        init {
            castName = itemView.findViewById(R.id.cast_name)
            castCharacter = itemView.findViewById(R.id.cast_character)
            castProfileImage = itemView.findViewById(R.id.cast_image)
        }
    }
}


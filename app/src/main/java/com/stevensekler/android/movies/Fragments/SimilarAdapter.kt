package com.stevensekler.android.movies.Fragments

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso
import com.stevensekler.android.movies.Model.Movie
import com.stevensekler.android.movies.R

import java.util.ArrayList

/**
 * Created by Szekely Istvan on 6/29/2017.
 *
 */

class SimilarAdapter(private val movies: ArrayList<Movie>, private val context: Context) : RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.similar_recycler_view, parent, false)
        return SimilarViewHolder(view)
    }


    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {

        val pos = holder.adapterPosition

        holder.movieName.text = movies[position].name +
                "\n\n" +
                movies[position].votes +
                "\n\n" +
                movies[position].genre
        if (movies[position].poster == "NO_IMAGE") {
            holder.moviePoster.setImageResource(R.drawable.film354)
        } else {
            Picasso.with(context)
                    .load(movies[position].poster)
                    .error(R.drawable.film354)
                    .into(holder.moviePoster)
        }

        if (movies[pos].isClicked) {
            holder.movieTranslucentView.visibility = VISIBLE
            holder.movieName.visibility = VISIBLE
        } else {
            holder.movieTranslucentView.visibility = INVISIBLE
            holder.movieName.visibility = INVISIBLE
        }
    }


    inner class SimilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener {
        var movieName: TextView
        var moviePoster: ImageView
        var movieTranslucentView: View

        init {
            itemView.setOnClickListener(this)
            movieName = itemView.findViewById(R.id.similar_movie_name)
            moviePoster = itemView.findViewById(R.id.similar_movie_image)
            movieTranslucentView = itemView.findViewById(R.id.similar_movie_view)
        }

        override fun onClick(view: View) {
            movieTranslucentView.visibility = VISIBLE
            movieName.visibility = VISIBLE
            movies[adapterPosition].isClicked = true
        }
    }
}



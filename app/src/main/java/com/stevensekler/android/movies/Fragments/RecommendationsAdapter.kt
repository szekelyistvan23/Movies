package com.stevensekler.android.movies.Fragments

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
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

class RecommendationsAdapter(private val movies: ArrayList<Movie>, private val context: Context) : RecyclerView.Adapter<RecommendationsAdapter.RecommendationsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationsViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.recommendations_recycler_view, parent, false)
        return RecommendationsViewHolder(view)
    }


    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: RecommendationsViewHolder, position: Int) {

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
            holder.movieTranslucentView.visibility = View.VISIBLE
            holder.movieName.visibility = View.VISIBLE
        } else {
            holder.movieTranslucentView.visibility = View.INVISIBLE
            holder.movieName.visibility = View.INVISIBLE
        }
    }


    inner class RecommendationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var movieName: TextView
        var moviePoster: ImageView
        var movieTranslucentView: View

        init {
            itemView.setOnClickListener(this)
            movieName = itemView.findViewById(R.id.recommendations_movie_name)
            moviePoster = itemView.findViewById(R.id.recommendations_movie_image)
            movieTranslucentView = itemView.findViewById(R.id.recommendations_movie_view)
        }

        override fun onClick(view: View) {
            movieTranslucentView.visibility = View.VISIBLE
            movieName.visibility = View.VISIBLE
            movies[adapterPosition].isClicked = true
        }
    }
}



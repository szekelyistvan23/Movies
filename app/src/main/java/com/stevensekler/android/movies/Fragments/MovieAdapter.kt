package com.stevensekler.android.movies.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso
import com.stevensekler.android.movies.DetailActivity
import com.stevensekler.android.movies.MainActivity
import com.stevensekler.android.movies.Model.Movie
import com.stevensekler.android.movies.R
import com.stevensekler.android.movies.Utils.NetworkQuery

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

import com.stevensekler.android.movies.R.drawable.ic_add
import com.stevensekler.android.movies.R.drawable.ic_list_add_check
import com.stevensekler.android.movies.R.drawable.ic_list_item_watched


/**
 * Created by Szekely Istvan on 5/16/2017.
 *
 */

class MovieAdapter(private val movies: ArrayList<Movie>, private val context: Context) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val contextView = parent.context
        val inflater = LayoutInflater.from(contextView)
        val view = inflater.inflate(R.layout.movie_list_item, parent, false)
        return MovieViewHolder(view)
    }


    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        val pos = holder.adapterPosition

        holder.movieName.text = movies[position].name
        holder.movieGenre.text = movies[position].genre
        holder.releaseMonth.text = getReleaseYear(movies[position].release)
        val s = NetworkQuery.doubleVotesToText(movies[position].votes) + "/10"
        holder.movieVotes.text = s

        Picasso.with(context)
                .load(movies[position].poster)
                .placeholder(R.drawable.blank154)
                .error(R.drawable.film154)
                .into(holder.posterImage)

        holder.addImage.setOnClickListener {
            val rotate = AnimationUtils.loadAnimation(context, R.anim.rotate_image_view)

            if ((context as MainActivity).isMovieWatched(movies[pos].id)) {
                context.showToast(context.getString(R.string.already_watched))
            } else {

                if (context.movieWatchedFromArray(movies[pos])) {
                    holder.addImage.setImageResource(ic_list_item_watched)
                    holder.addImage.startAnimation(rotate)
                    context.showToast(context.getString(R.string.marked_watched))
                    context.sortMovies()
                    context.updateList(false)
                } else {
                    context.addToMyMovieArray(movies[pos])
                    holder.addImage.setImageResource(ic_list_add_check)
                    holder.addImage.startAnimation(rotate)
                    context.showToast(context.getString(R.string.added_to_list))
                    context.sortMovies()
                }
            }
        }

        val id = movies[pos].id

        if ((context as MainActivity).checkMovieInArray(id)) {
            holder.addImage.setImageResource(context.buttonCheck(id))
        } else {
            holder.addImage.setImageResource(ic_add)

        }
    }


    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var movieName: TextView
        var movieGenre: TextView
        var releaseMonth: TextView
        var movieVotes: TextView
        var posterImage: ImageView
        var addImage: ImageView


        init {
            itemView.setOnClickListener(this)
            movieName = itemView.findViewById(R.id.movie_name)
            movieGenre = itemView.findViewById(R.id.movie_genre)
            releaseMonth = itemView.findViewById(R.id.release_year)
            movieVotes = itemView.findViewById(R.id.movie_votes)
            posterImage = itemView.findViewById(R.id.movie_image)
            addImage = itemView.findViewById(R.id.list_button)

        }

        override fun onClick(v: View) {

            val args = Bundle()
            val position = adapterPosition
            val id = movies[position].id
            args.putInt(MOVIE_ID, id)
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtras(args)
            context.startActivity(intent)
            (context as MainActivity).overridePendingTransition(R.anim.slide_in, R.anim.no_animation)
        }

    }

    private fun getReleaseYear(s: String?): String {
        val currentDate = Date()

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        df.isLenient = false

        var startDate = Date()
        try {
            startDate = df.parse(s)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val startCalendar = GregorianCalendar()
        startCalendar.time = startDate

        val releaseYear = startCalendar.get(Calendar.YEAR)

        return if (startDate.after(currentDate)) {
            context.getString(R.string.no_release)
        } else {
            "" + releaseYear
        }

    }

    companion object {

        val MOVIE_ID = "movie_id"
    }
}

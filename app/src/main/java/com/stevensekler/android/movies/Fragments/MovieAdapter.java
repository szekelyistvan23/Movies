package com.stevensekler.android.movies.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stevensekler.android.movies.DetailActivity;
import com.stevensekler.android.movies.MainActivity;
import com.stevensekler.android.movies.Model.Movie;
import com.stevensekler.android.movies.R;
import com.stevensekler.android.movies.Utils.NetworkQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.stevensekler.android.movies.R.drawable.ic_add;
import static com.stevensekler.android.movies.R.drawable.ic_list_add_check;
import static com.stevensekler.android.movies.R.drawable.ic_list_item_watched;


/**
 * Created by Szekely Istvan on 5/16/2017.
 *
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public static final String MOVIE_ID = "movie_id";
    private ArrayList<Movie> movies;
    private Context context;

    public MovieAdapter(ArrayList<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context contextView = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(contextView);
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return movies.size();
    }
    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {

        final int pos = holder.getAdapterPosition();

        holder.movieName.setText(movies.get(position).getName());
        holder.movieGenre.setText(movies.get(position).getGenre());
        holder.releaseMonth.setText(getReleaseYear(movies.get(position).getRelease()));
        String s = NetworkQuery.doubleVotesToText(movies.get(position).getVotes()) + "/10";
        holder.movieVotes.setText(s);

        Picasso.with(context)
                .load(movies.get(position).getPoster())
                .placeholder(R.drawable.blank154)
                .error(R.drawable.film154)
                .into(holder.posterImage);

        holder.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation rotate = AnimationUtils.loadAnimation(context, R.anim.rotate_image_view);

                if (((MainActivity) context).isMovieWatched(movies.get(pos).getId())) {
                    ((MainActivity) context).showToast(context.getString(R.string.already_watched));
                } else {

                    if ((((MainActivity) context).movieWatchedFromArray(movies.get(pos)))) {
                        holder.addImage.setImageResource(ic_list_item_watched);
                        holder.addImage.startAnimation(rotate);
                        ((MainActivity) context).showToast(context.getString(R.string.marked_watched));
                        ((MainActivity) context).sortMovies();
                        ((MainActivity) context).updateList(false);
                    } else {
                        ((MainActivity) context).addToMyMovieArray(movies.get(pos));
                        holder.addImage.setImageResource(ic_list_add_check);
                        holder.addImage.startAnimation(rotate);
                        ((MainActivity) context).showToast(context.getString(R.string.added_to_list));
                        ((MainActivity) context).sortMovies();
                    }
                }


            }
        });

        int id = movies.get(pos).getId();

        if (((MainActivity) context).checkMovieInArray(id)) {
            holder.addImage.setImageResource(((MainActivity) context).buttonCheck(id));
        } else {
            holder.addImage.setImageResource(ic_add);

        }
    }


    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView movieName;
        TextView movieGenre;
        TextView releaseMonth;
        TextView movieVotes;
        ImageView posterImage;
        ImageView addImage;


        public MovieViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            movieName = itemView.findViewById(R.id.movie_name);
            movieGenre = itemView.findViewById(R.id.movie_genre);
            releaseMonth = itemView.findViewById(R.id.release_year);
            movieVotes = itemView.findViewById(R.id.movie_votes);
            posterImage = itemView.findViewById(R.id.movie_image);
            addImage = itemView.findViewById(R.id.list_button);

        }

        @Override
        public void onClick(View v) {

            Bundle args = new Bundle();
            int position = getAdapterPosition();
            int id = movies.get(position).getId();
            args.putInt(MOVIE_ID, id);
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtras(args);
            context.startActivity(intent);
            ((MainActivity) context).overridePendingTransition(R.anim.slide_in, R.anim.no_animation);
        }

    }

    private String getReleaseYear(String s) {
        Date currentDate = new Date();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        df.setLenient(false);

        Date startDate = new Date();
        try {
            startDate = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);

        int releaseYear = startCalendar.get(Calendar.YEAR);

        if (startDate.after(currentDate)) {
            return context.getString(R.string.no_release);
        } else {
            return "" + releaseYear;
        }

    }
}

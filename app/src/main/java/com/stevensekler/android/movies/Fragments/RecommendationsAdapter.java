package com.stevensekler.android.movies.Fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stevensekler.android.movies.Model.Movie;
import com.stevensekler.android.movies.R;

import java.util.ArrayList;

/**
 * Created by Szekely Istvan on 6/29/2017.
 *
 */

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.RecommendationsViewHolder> {

    private ArrayList<Movie> movies;
    private Context context;

    public RecommendationsAdapter(ArrayList<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;

    }

    @Override
    public RecommendationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recommendations_recycler_view, parent, false);
        return new RecommendationsAdapter.RecommendationsViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void onBindViewHolder(RecommendationsViewHolder holder, int position) {

        int pos = holder.getAdapterPosition();

        holder.movieName.setText(movies.get(position).getName() +
                "\n\n" +
                movies.get(position).getVotes() +
                "\n\n" +
                movies.get(position).getGenre());

        if ((movies.get(position).getPoster()).equals("NO_IMAGE")) {
            holder.moviePoster.setImageResource(R.drawable.film354);
        } else {
            Picasso.with(context)
                    .load(movies.get(position).getPoster())
                    .error(R.drawable.film354)
                    .into(holder.moviePoster);
        }

        if (movies.get(pos).isClicked()) {
            holder.movieTranslucentView.setVisibility(View.VISIBLE);
            holder.movieName.setVisibility(View.VISIBLE);
        } else {
            holder.movieTranslucentView.setVisibility(View.INVISIBLE);
            holder.movieName.setVisibility(View.INVISIBLE);
        }
    }


    class RecommendationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView movieName;
        ImageView moviePoster;
        View movieTranslucentView;

        public RecommendationsViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            movieName = itemView.findViewById(R.id.recommendations_movie_name);
            moviePoster = itemView.findViewById(R.id.recommendations_movie_image);
            movieTranslucentView = itemView.findViewById(R.id.recommendations_movie_view);
        }

        @Override
        public void onClick(View view) {
            movieTranslucentView.setVisibility(View.VISIBLE);
            movieName.setVisibility(View.VISIBLE);
            movies.get(getAdapterPosition()).setClicked(true);
        }
    }
}



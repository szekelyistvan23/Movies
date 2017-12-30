package com.stevensekler.android.movies.Fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stevensekler.android.movies.DetailActivity;
import com.stevensekler.android.movies.R;

import java.util.ArrayList;

/**
 * Created by Szekely Istvan on 8/27/17.
 *
 */

public class BackdropAdapter extends RecyclerView.Adapter<BackdropAdapter.BackdropViewHolder> {

    private ArrayList<String> images;
    private Context context;


    public BackdropAdapter(ArrayList<String> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public BackdropAdapter.BackdropViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.backdrop_recycler_view, parent, false);
        return new BackdropAdapter.BackdropViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public void onBindViewHolder(BackdropAdapter.BackdropViewHolder holder, int position) {

        Picasso.with(context)
                .load(images.get(position))
                .placeholder(R.drawable.blank_landscape92)
                .into(holder.backdropImage);
    }

    class BackdropViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView backdropImage;
        ProgressBar imageLoading;

        public BackdropViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            backdropImage = itemView.findViewById(R.id.backdrop_image);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String s = images.get(position).replace("w92", "w500");
            ImageView mainImage = (ImageView) ((DetailActivity) context).findViewById(R.id.imageView2);
            imageLoading = (ProgressBar) ((DetailActivity) context).findViewById(R.id.backdrop_image_loading);
            imageLoading.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(s)
                    .noPlaceholder()
                    .into(mainImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            imageLoading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imageLoading.setVisibility(View.GONE);
                        }
                    });
        }
    }
}

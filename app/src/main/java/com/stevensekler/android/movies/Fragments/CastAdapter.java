package com.stevensekler.android.movies.Fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stevensekler.android.movies.Model.Cast;
import com.stevensekler.android.movies.R;

import java.util.ArrayList;

/**
 * Created by Szekely Istvan on 6/29/2017.
 *
 */

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private ArrayList<Cast> actors;
    private Context context;


    public CastAdapter(ArrayList<Cast> actors, Context context) {
        this.actors = actors;
        this.context = context;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cast_recycler_view, parent, false);
        return new CastAdapter.CastViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return actors.size();
    }

    @Override
    public void onBindViewHolder(CastViewHolder holder, int position) {

        holder.castName.setText(actors.get(position).getName());
        holder.castCharacter.setText(actors.get(position).getCharacter());

        if ((actors.get(position).getProfileImage()).equals("NO_IMAGE")) {
            switch (actors.get(position).getGender()) {
                case 1:
                    holder.castProfileImage.setImageResource(R.drawable.woman);
                    break;
                case 2:
                    holder.castProfileImage.setImageResource(R.drawable.man);
                    break;
                default:
                    holder.castProfileImage.setImageResource(R.drawable.no_gender);
                    break;
            }
        } else {
            Picasso.with(context)
                    .load(actors.get(position).getProfileImage())
                    .placeholder(R.drawable.blank185)
                    .into(holder.castProfileImage);
        }
    }

    class CastViewHolder extends RecyclerView.ViewHolder {
        TextView castName;
        TextView castCharacter;
        ImageView castProfileImage;

        public CastViewHolder(final View itemView) {
            super(itemView);
            castName = itemView.findViewById(R.id.cast_name);
            castCharacter = itemView.findViewById(R.id.cast_character);
            castProfileImage = itemView.findViewById(R.id.cast_image);
        }
    }
}


package com.stevensekler.android.movies.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stevensekler.android.movies.MainActivity;
import com.stevensekler.android.movies.R;

/**
 * Created by Szekely Istvan on 6/27/2017.
 *
 */

public class NavigationDrawerAdapter extends BaseAdapter {

    private String[] result;
    private int[] imageId;
    private LayoutInflater inflater;

    public NavigationDrawerAdapter(MainActivity mainActivity, String[] menuItemList, int[] menuItemIcons) {

        result = menuItemList;
        imageId = menuItemIcons;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView textView;
        ImageView imageView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (rowView == null) {

            rowView = inflater.inflate(R.layout.navigation_drawer_item, parent, false);
            Holder viewHolder = new Holder();
            viewHolder.textView = rowView.findViewById(R.id.navigation_text);
            viewHolder.imageView = rowView.findViewById(R.id.navigation_image);
            rowView.setTag(viewHolder);
        }

        Holder holder = (Holder) rowView.getTag();
        holder.textView.setText(result[position]);
        holder.imageView.setImageResource(imageId[position]);

        return rowView;
    }


}

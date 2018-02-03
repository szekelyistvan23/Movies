package com.stevensekler.android.movies.Model;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Szekely Istvan on 5/24/2017.
 *
 */

public class UriArray {
    private ArrayList<Uri> uriArrayList;

    public UriArray() {
        this.uriArrayList = new ArrayList<>();
    }

    public void addToUriArray(Uri uri) {
        uriArrayList.add(uri);
    }

    public int uriArraySize() {
        return uriArrayList.size();
    }

    public Uri getUriArray(int i) {
        return uriArrayList.get(i);
    }
}

package com.stevensekler.android.movies.Model;


import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Szekely Istvan on 5/24/2017.
 *
 */

public class UrlArray implements Parcelable {
    private ArrayList<URL> urlArrayList;

    public UrlArray() {
        this.urlArrayList = new ArrayList<>();
    }

    public void addToUrlArray(URL url) {
        urlArrayList.add(url);
    }

    public URL getUrlArray(int i) {
        return urlArrayList.get(i);
    }

    public int urlArraySize() {
        return urlArrayList.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.urlArrayList);
    }

    protected UrlArray(Parcel in) {
        this.urlArrayList = new ArrayList<>();
        in.readList(this.urlArrayList, URL.class.getClassLoader());
    }

    public static final Parcelable.Creator<UrlArray> CREATOR = new Parcelable.Creator<UrlArray>() {
        @Override
        public UrlArray createFromParcel(Parcel source) {
            return new UrlArray(source);
        }

        @Override
        public UrlArray[] newArray(int size) {
            return new UrlArray[size];
        }
    };
}

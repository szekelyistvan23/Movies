package com.stevensekler.android.movies.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Szekely Istvan on 12.05.2017.
 *
 */

public class Movie implements Parcelable {
    private int id;
    private String name;
    private String genre;
    private String release;
    private double votes;
    private String poster;
    private int watched;
    private boolean clicked;

    public Movie(int id, String name, String genre, String release, double votes, String poster, int watched, boolean clicked) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.release = release;
        this.votes = votes;
        this.poster = poster;
        this.watched = watched;
        this.clicked = clicked;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getRelease() {
        return release;
    }

    public double getVotes() {
        return votes;
    }

    public String getPoster() {
        return poster;
    }

    public int getWatched() {
        return watched;
    }

    public void setWatched(int watched) {
        this.watched = watched;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.genre);
        dest.writeString(this.release);
        dest.writeDouble(this.votes);
        dest.writeString(this.poster);
        dest.writeInt(this.watched);
        dest.writeByte(this.clicked ? (byte) 1 : (byte) 0);
    }

    protected Movie(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.genre = in.readString();
        this.release = in.readString();
        this.votes = in.readDouble();
        this.poster = in.readString();
        this.watched = in.readInt();
        this.clicked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

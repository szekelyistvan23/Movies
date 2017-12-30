package com.stevensekler.android.movies.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.stevensekler.android.movies.Model.Movie;

import java.util.ArrayList;

import static com.stevensekler.android.movies.Utils.MovieContract.MovieEntry.CREATE_TABLE;
import static com.stevensekler.android.movies.Utils.MovieContract.MovieEntry.DB_NAME;
import static com.stevensekler.android.movies.Utils.MovieContract.MovieEntry.DB_VERSION;
import static com.stevensekler.android.movies.Utils.MovieContract.MovieEntry.MOVIE_COLUMN_GENRE;
import static com.stevensekler.android.movies.Utils.MovieContract.MovieEntry.MOVIE_COLUMN_ID;
import static com.stevensekler.android.movies.Utils.MovieContract.MovieEntry.MOVIE_COLUMN_NAME;
import static com.stevensekler.android.movies.Utils.MovieContract.MovieEntry.MOVIE_COLUMN_POSTER;
import static com.stevensekler.android.movies.Utils.MovieContract.MovieEntry.MOVIE_COLUMN_RELEASE;
import static com.stevensekler.android.movies.Utils.MovieContract.MovieEntry.MOVIE_COLUMN_VOTES;
import static com.stevensekler.android.movies.Utils.MovieContract.MovieEntry.MOVIE_COLUMN_WATCHED;
import static com.stevensekler.android.movies.Utils.MovieContract.MovieEntry.TABLE_NAME;

/**
 * Created by Szekely Istvan on 6/22/2017.
 *
 */

public class MovieDatabaseHelper extends SQLiteOpenHelper {


    public MovieDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);

    }

    public boolean insertMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIE_COLUMN_ID, movie.getId());
        contentValues.put(MOVIE_COLUMN_NAME, movie.getName());
        contentValues.put(MOVIE_COLUMN_GENRE, movie.getGenre());
        contentValues.put(MOVIE_COLUMN_RELEASE, movie.getRelease());
        contentValues.put(MOVIE_COLUMN_VOTES, movie.getVotes());
        contentValues.put(MOVIE_COLUMN_POSTER, movie.getPoster());
        contentValues.put(MOVIE_COLUMN_WATCHED, movie.getWatched());
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS movie");
        onCreate(db);
    }


    public int numberOfRows() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return (int) DatabaseUtils.queryNumEntries(db, DB_NAME);
        } catch (SQLiteException e) {
            return -1;
        }

    }


    public void clearMovies(SQLiteDatabase db) {
        db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS movie");
        db.execSQL(CREATE_TABLE);
    }

    public ArrayList<Movie> getAllMovies() {
        ArrayList<Movie> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM movie", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {

            array_list.add(new Movie(res.getInt(res.getColumnIndex(MOVIE_COLUMN_ID)),
                    res.getString(res.getColumnIndex(MOVIE_COLUMN_NAME)),
                    res.getString(res.getColumnIndex(MOVIE_COLUMN_GENRE)),
                    res.getString(res.getColumnIndex(MOVIE_COLUMN_RELEASE)),
                    res.getDouble(res.getColumnIndex(MOVIE_COLUMN_VOTES)),
                    res.getString(res.getColumnIndex(MOVIE_COLUMN_POSTER)),
                    res.getInt(res.getColumnIndex(MOVIE_COLUMN_WATCHED)),
                    false));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}

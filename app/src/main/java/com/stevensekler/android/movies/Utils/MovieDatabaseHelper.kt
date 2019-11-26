package com.stevensekler.android.movies.Utils

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

import com.stevensekler.android.movies.Model.Movie
import com.stevensekler.android.movies.Utils.MovieContract.CREATE_TABLE
import com.stevensekler.android.movies.Utils.MovieContract.DB_NAME
import com.stevensekler.android.movies.Utils.MovieContract.DB_VERSION
import com.stevensekler.android.movies.Utils.MovieContract.MOVIE_COLUMN_GENRE
import com.stevensekler.android.movies.Utils.MovieContract.MOVIE_COLUMN_ID
import com.stevensekler.android.movies.Utils.MovieContract.MOVIE_COLUMN_NAME
import com.stevensekler.android.movies.Utils.MovieContract.MOVIE_COLUMN_POSTER
import com.stevensekler.android.movies.Utils.MovieContract.MOVIE_COLUMN_RELEASE
import com.stevensekler.android.movies.Utils.MovieContract.MOVIE_COLUMN_VOTES
import com.stevensekler.android.movies.Utils.MovieContract.MOVIE_COLUMN_WATCHED
import com.stevensekler.android.movies.Utils.MovieContract.TABLE_NAME

import java.util.ArrayList

/**
 * Created by Szekely Istvan on 6/22/2017.
 *
 */

class MovieDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    val allMovies: ArrayList<Movie>
        get() {
            val array_list = ArrayList<Movie>()

            val db = this.readableDatabase
            val res = db.rawQuery("SELECT * FROM movie", null)
            res.moveToFirst()

            while (!res.isAfterLast) {

                array_list.add(Movie(res.getInt(res.getColumnIndex(MOVIE_COLUMN_ID)),
                        res.getString(res.getColumnIndex(MOVIE_COLUMN_NAME)),
                        res.getString(res.getColumnIndex(MOVIE_COLUMN_GENRE)),
                        res.getString(res.getColumnIndex(MOVIE_COLUMN_RELEASE)),
                        res.getDouble(res.getColumnIndex(MOVIE_COLUMN_VOTES)),
                        res.getString(res.getColumnIndex(MOVIE_COLUMN_POSTER)),
                        res.getInt(res.getColumnIndex(MOVIE_COLUMN_WATCHED)),
                        false))
                res.moveToNext()
            }
            res.close()
            return array_list
        }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(CREATE_TABLE)

    }

    fun insertMovie(movie: Movie): Boolean {
        val db = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(MOVIE_COLUMN_ID, movie.id)
        contentValues.put(MOVIE_COLUMN_NAME, movie.name)
        contentValues.put(MOVIE_COLUMN_GENRE, movie.genre)
        contentValues.put(MOVIE_COLUMN_RELEASE, movie.release)
        contentValues.put(MOVIE_COLUMN_VOTES, movie.votes)
        contentValues.put(MOVIE_COLUMN_POSTER, movie.poster)
        contentValues.put(MOVIE_COLUMN_WATCHED, movie.watched)
        db.insert(TABLE_NAME, null, contentValues)
        return true
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS movie")
        onCreate(db)
    }


    fun numberOfRows(): Int {
        try {
            val db = this.readableDatabase
            return DatabaseUtils.queryNumEntries(db, DB_NAME).toInt()
        } catch (e: SQLiteException) {
            return -1
        }

    }


    fun clearMovies(db: SQLiteDatabase?) {
        var database = db
        database = this.writableDatabase
        database.execSQL("DROP TABLE IF EXISTS movie")
        database.execSQL(CREATE_TABLE)
    }
}

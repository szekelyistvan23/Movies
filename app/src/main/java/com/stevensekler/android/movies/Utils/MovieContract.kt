package com.stevensekler.android.movies.Utils

import android.provider.BaseColumns

/**
 * Created by Szekely Istvan on 6/24/2017.
 *
 */

object MovieContract {

    val DB_NAME = "movies.db"
    val DB_VERSION = 1
    val TABLE_NAME = "movie"
    val MOVIE_COLUMN_ID = "MOVIE_ID"
    val MOVIE_COLUMN_NAME = "NAME"
    val MOVIE_COLUMN_GENRE = "GENRE"
    val MOVIE_COLUMN_RELEASE = "RELEASE"
    val MOVIE_COLUMN_VOTES = "VOTES"
    val MOVIE_COLUMN_POSTER = "POSTER"
    val MOVIE_COLUMN_WATCHED = "WATCHED"
    val CREATE_TABLE = ("CREATE TABLE movie ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "MOVIE_ID INTEGER, "
            + "NAME TEXT, "
            + "GENRE TEXT, "
            + "RELEASE TEXT, "
            + "VOTES NUMERIC, "
            + "POSTER TEXT, "
            + "WATCHED NUMERIC);")
}

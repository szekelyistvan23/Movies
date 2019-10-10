package com.stevensekler.android.movies.Utils

import android.provider.BaseColumns

/**
 * Created by Szekely Istvan on 6/24/2017.
 *
 */

object MovieContract {

    const val DB_NAME = "movies.db"
    const val DB_VERSION = 1
    const val TABLE_NAME = "movie"
    const val MOVIE_COLUMN_ID = "MOVIE_ID"
    const val MOVIE_COLUMN_NAME = "NAME"
    const val MOVIE_COLUMN_GENRE = "GENRE"
    const val MOVIE_COLUMN_RELEASE = "RELEASE"
    const val MOVIE_COLUMN_VOTES = "VOTES"
    const val MOVIE_COLUMN_POSTER = "POSTER"
    const val MOVIE_COLUMN_WATCHED = "WATCHED"
    const val CREATE_TABLE = ("CREATE TABLE movie ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "MOVIE_ID INTEGER, "
            + "NAME TEXT, "
            + "GENRE TEXT, "
            + "RELEASE TEXT, "
            + "VOTES NUMERIC, "
            + "POSTER TEXT, "
            + "WATCHED NUMERIC);")
}

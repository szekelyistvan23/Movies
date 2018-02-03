package com.stevensekler.android.movies.Utils;

import android.provider.BaseColumns;

/**
 * Created by Szekely Istvan on 6/24/2017.
 *
 */

public class MovieContract {

    private MovieContract() {
    }

    public static final class MovieEntry implements BaseColumns {
        public static final String DB_NAME = "movies.db";
        public static final int DB_VERSION = 1;
        public static final String TABLE_NAME = "movie";
        public static final String MOVIE_COLUMN_ID = "MOVIE_ID";
        public static final String MOVIE_COLUMN_NAME = "NAME";
        public static final String MOVIE_COLUMN_GENRE = "GENRE";
        public static final String MOVIE_COLUMN_RELEASE = "RELEASE";
        public static final String MOVIE_COLUMN_VOTES = "VOTES";
        public static final String MOVIE_COLUMN_POSTER = "POSTER";
        public static final String MOVIE_COLUMN_WATCHED = "WATCHED";
        public static final String CREATE_TABLE = "CREATE TABLE movie ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "MOVIE_ID INTEGER, "
                + "NAME TEXT, "
                + "GENRE TEXT, "
                + "RELEASE TEXT, "
                + "VOTES NUMERIC, "
                + "POSTER TEXT, "
                + "WATCHED NUMERIC);";
    }
}

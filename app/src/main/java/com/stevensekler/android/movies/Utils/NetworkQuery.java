package com.stevensekler.android.movies.Utils;

/*
*  Created by Szekely Istvan on 5/18/2017.
*
*/


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.StringDef;

import com.stevensekler.android.movies.BuildConfig;
import com.stevensekler.android.movies.Model.UriArray;
import com.stevensekler.android.movies.Model.UrlArray;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class NetworkQuery {

    public static final String DETAILS = "details";
    public static final String IMAGES = "images";
    public static final String REVIEWS = "reviews";
    public static final String CREDITS = "credits";
    public static final String RECOMMENDATIONS = "recommendations";
    public static final String POPULAR = "popular";
    public static final String NOW_PLAYING = "now_playing";
    public static final String TOP_RATED = "top_rated";
    public static final String SEARCH = "search";
    public static final int NUMBER_OF_PAGES_FOR_PNT = 7;
    public static final int NUMBER_OF_PAGES_FOR_SEARCH_QUERY = 5;
    private static final String MOVIEDB_BASE_URL =
            "https://api.themoviedb.org/3/movie";
    private static final String MOVIEDB_BASE_SEARCH_URL =
            "https://api.themoviedb.org/3/search/movie";
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String detailUrlEn = "https://api.themoviedb.org/3/movie/movie_id?" +
            "api_key=" + API_KEY + "&append_to_response=" +
            "reviews,credits,similar,recommendations,images";
    private static final String detailUrlHu = "https://api.themoviedb.org/3/movie/movie_id?" +
            "api_key=" + API_KEY + "&language=hu-HU" + "&append_to_response=" +
            "reviews,credits,similar,recommendations,images&include_image_language=en,null";


    public static UrlArray buildUrl(@QueryType String queryType, String search) {
        UriArray builtUri;
        switch (queryType) {
            case POPULAR:
                builtUri = buildUri(POPULAR, "", NUMBER_OF_PAGES_FOR_PNT);
                break;
            case NOW_PLAYING:
                builtUri = buildUri(NOW_PLAYING, "", NUMBER_OF_PAGES_FOR_PNT);
                break;
            case TOP_RATED:
                builtUri = buildUri(TOP_RATED, "", NUMBER_OF_PAGES_FOR_PNT);
                break;
            case SEARCH:
                builtUri = buildUri(SEARCH, search, NUMBER_OF_PAGES_FOR_SEARCH_QUERY);
                break;
            default:
                builtUri = new UriArray();
                break;

        }

        UrlArray url = new UrlArray();
        URL urlElement = null;
        try {
            for (int i = 0; i < builtUri.uriArraySize(); i++) {
                urlElement = new URL(builtUri.getUriArray(i).toString());
                url.addToUrlArray(urlElement);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrl(int movieId) {
        String language = Locale.getDefault().getDisplayLanguage().toLowerCase();
        String detailUrl = "";
        if (language.equals("magyar")) {
            detailUrl = detailUrlHu;
        } else {
            detailUrl = detailUrlEn;
        }
        String builtUri = detailUrl.replace("movie_id", String.valueOf(movieId));
        URL url = null;
        try {
            url = new URL(builtUri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static UriArray buildUri(@QueryType String queryType, String search, int numberOfPages) {
        UriArray finalUri = new UriArray();
        if (numberOfPages > 0) {
            for (int i = 1; i < numberOfPages + 1; i++) {
                finalUri.addToUriArray(buildUriPnts(queryType, search, i));
            }
        }
        return finalUri;
    }

    public static Uri buildUriPnts(@QueryType String queryType, String search, int pageNumber) {
        Uri builtUri;
        String language = Locale.getDefault().getDisplayLanguage().toLowerCase();
        String value = "";
        if (language.equals("magyar")) {
            value = "hu-HU";
        } else {
            value = "en-US";
        }
        if (!queryType.equals(SEARCH)) {
            builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendPath(queryType)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("language", value)
                    .appendQueryParameter("page", String.valueOf(pageNumber))
                    .build();
        } else {
            builtUri = Uri.parse(MOVIEDB_BASE_SEARCH_URL).buildUpon()
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("language", value)
                    .appendQueryParameter("query", search)
                    .appendQueryParameter("page", String.valueOf(pageNumber))
                    .appendQueryParameter("include_adult", "false")
                    .build();
        }

        return builtUri;
    }

    public static String convertStreamToString(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static ArrayList<String> convertStreamToString(UrlArray url) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < url.urlArraySize(); i++) {
            HttpURLConnection urlConnection = (HttpURLConnection) url.getUrlArray(i).openConnection();
            try {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    result.add(scanner.next());

                } else {
                    return null;
                }
            } finally {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    public static boolean haveNetworkConnection(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                if (networkInfo.isConnected()) {
                    return true;
                }
            }

            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (networkInfo.isConnected()) {
                    return true;
                }
            }
        }

        return false;
    }

    @StringDef({DETAILS, IMAGES, REVIEWS, CREDITS, RECOMMENDATIONS, POPULAR, NOW_PLAYING, TOP_RATED, SEARCH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface QueryType {
    }

    public static String returnGenreName(int i) {
        switch (i) {
            case 28:
                return "Action";
            case 12:
                return "Adventure";
            case 16:
                return "Animation";
            case 35:
                return "Comedy";
            case 80:
                return "Crime";
            case 99:
                return "Documentary";
            case 18:
                return "Drama";
            case 10751:
                return "Family";
            case 14:
                return "Fantasy";
            case 36:
                return "History";
            case 27:
                return "Horror";
            case 10402:
                return "Music";
            case 9648:
                return "Mystery";
            case 10749:
                return "Romance";
            case 878:
                return "Science Fiction";
            case 10770:
                return "TV Movie";
            case 53:
                return "Thriller";
            case 10752:
                return "War";
            case 37:
                return "Western";
            default:
                return "";
        }
    }

    public static String returnGenreNameHungarian(int i) {
        switch (i) {
            case 28:
                return "Akció";
            case 12:
                return "Kaland";
            case 16:
                return "Animáció";
            case 35:
                return "Vígjáték";
            case 80:
                return "Krimi";
            case 99:
                return "Dokumentum";
            case 18:
                return "Dráma";
            case 10751:
                return "Családi";
            case 14:
                return "Fantasy";
            case 36:
                return "Történelmi";
            case 27:
                return "Horror";
            case 10402:
                return "Zenés";
            case 9648:
                return "Misztikus";
            case 10749:
                return "Romantikus";
            case 878:
                return "Sci-fi";
            case 10770:
                return "TV film";
            case 53:
                return "Thriller";
            case 10752:
                return "Háborús";
            case 37:
                return "Western";
            default:
                return "";
        }
    }

    public static String doubleVotesToText(double d) {
        DecimalFormat dec = new DecimalFormat("#0.0", DecimalFormatSymbols.getInstance());
        return dec.format(d);
    }
}

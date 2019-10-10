package com.stevensekler.android.movies.Utils

/*
*  Created by Szekely Istvan on 5/18/2017.
*
*/


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.support.annotation.StringDef

import com.stevensekler.android.movies.BuildConfig
import com.stevensekler.android.movies.Model.UriArray
import com.stevensekler.android.movies.Model.UrlArray
import com.stevensekler.android.movies.Utils.NetworkQuery.QueryType.*

import java.io.IOException
import java.io.InputStream
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.ArrayList
import java.util.Locale
import java.util.Scanner

object NetworkQuery {

    val DETAILS = "details"
    val IMAGES = "images"
    val REVIEWS = "reviews"
    val CREDITS = "credits"
    val RECOMMENDATIONS = "recommendations"
    val POPULAR = "popular"
    val NOW_PLAYING = "now_playing"
    val TOP_RATED = "top_rated"
    val SEARCH = "search"
    val NUMBER_OF_PAGES_FOR_PNT = 7
    val NUMBER_OF_PAGES_FOR_SEARCH_QUERY = 5
    private val MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie"
    private val MOVIEDB_BASE_SEARCH_URL = "https://api.themoviedb.org/3/search/movie"
    private val API_KEY = BuildConfig.API_KEY
    private val detailUrlEn = "https://api.themoviedb.org/3/movie/movie_id?" +
            "api_key=" + API_KEY + "&append_to_response=" +
            "reviews,credits,similar,recommendations,images"
    private val detailUrlHu = "https://api.themoviedb.org/3/movie/movie_id?" +
            "api_key=" + API_KEY + "&language=hu-HU" + "&append_to_response=" +
            "reviews,credits,similar,recommendations,images&include_image_language=en,null"


    fun buildUrl(queryType: QueryType, search: String?): UrlArray {
        val builtUri: UriArray
        when (queryType) {
            popular -> builtUri = buildUri(popular, "", NUMBER_OF_PAGES_FOR_PNT)
            now_playing -> builtUri = buildUri(now_playing, "", NUMBER_OF_PAGES_FOR_PNT)
            top_rated -> builtUri = buildUri(top_rated, "", NUMBER_OF_PAGES_FOR_PNT)
            QueryType.search -> if (search != null) builtUri = buildUri(QueryType.search, search, NUMBER_OF_PAGES_FOR_SEARCH_QUERY) else builtUri = UriArray()
            else -> builtUri = UriArray()
        }

        val url = UrlArray()
        var urlElement: URL? = null
        try {
            for (i in 0 until builtUri.uriArraySize()) {
                urlElement = URL(builtUri.getUriArray(i).toString())
                url.addToUrlArray(urlElement)
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        return url
    }

    fun buildUrl(movieId: Int): URL? {
        val language = Locale.getDefault().displayLanguage.toLowerCase()
        var detailUrl = ""
        if (language == "magyar") {
            detailUrl = detailUrlHu
        } else {
            detailUrl = detailUrlEn
        }
        val builtUri = detailUrl.replace("movie_id", movieId.toString())
        var url: URL? = null
        try {
            url = URL(builtUri)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        return url
    }

    fun buildUri(queryType: QueryType, search: String, numberOfPages: Int): UriArray {
        val finalUri = UriArray()
        if (numberOfPages > 0) {
            for (i in 1 until numberOfPages + 1) {
                finalUri.addToUriArray(buildUriPnts(queryType, search, i))
            }
        }
        return finalUri
    }

    fun buildUriPnts(queryType: QueryType, search: String, pageNumber: Int): Uri {
        val builtUri: Uri
        val language = Locale.getDefault().displayLanguage.toLowerCase()
        var value = ""
        if (language == "magyar") {
            value = "hu-HU"
        } else {
            value = "en-US"
        }
        if (queryType != QueryType.search) {
            builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendPath(queryType.toString())
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("language", value)
                    .appendQueryParameter("page", pageNumber.toString())
                    .build()
        } else {
            builtUri = Uri.parse(MOVIEDB_BASE_SEARCH_URL).buildUpon()
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("language", value)
                    .appendQueryParameter("query", search)
                    .appendQueryParameter("page", pageNumber.toString())
                    .appendQueryParameter("include_adult", "false")
                    .build()
        }

        return builtUri
    }

    @Throws(IOException::class)
    fun convertStreamToString(url: URL): String? {
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val `in` = urlConnection.inputStream

            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")

            val hasInput = scanner.hasNext()
            return if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }

    @Throws(IOException::class)
    fun convertStreamToString(url: UrlArray): ArrayList<String>? {
        val result = ArrayList<String>()
        for (i in 0 until url.urlArraySize()) {
            val urlConnection = url.getUrlArray(i).openConnection() as HttpURLConnection
            try {
                val `in` = urlConnection.inputStream

                val scanner = Scanner(`in`)
                scanner.useDelimiter("\\A")

                val hasInput = scanner.hasNext()
                if (hasInput) {
                    result.add(scanner.next())

                } else {
                    return null
                }
            } finally {
                urlConnection.disconnect()
            }
        }
        return result
    }

    fun haveNetworkConnection(context: Context): Boolean {

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        if (networkInfo != null) {
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                if (networkInfo.isConnected) {
                    return true
                }
            }

            if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                return networkInfo.isConnected
            }
        }

        return false
    }

    enum class QueryType{details, images, reviews, credits, recommendations, popular, now_playing, top_rated, search}

    fun returnGenreName(i: Int): String {
        when (i) {
            28 -> return "Action"
            12 -> return "Adventure"
            16 -> return "Animation"
            35 -> return "Comedy"
            80 -> return "Crime"
            99 -> return "Documentary"
            18 -> return "Drama"
            10751 -> return "Family"
            14 -> return "Fantasy"
            36 -> return "History"
            27 -> return "Horror"
            10402 -> return "Music"
            9648 -> return "Mystery"
            10749 -> return "Romance"
            878 -> return "Science Fiction"
            10770 -> return "TV Movie"
            53 -> return "Thriller"
            10752 -> return "War"
            37 -> return "Western"
            else -> return ""
        }
    }

    fun returnGenreNameHungarian(i: Int): String {
        when (i) {
            28 -> return "Akció"
            12 -> return "Kaland"
            16 -> return "Animáció"
            35 -> return "Vígjáték"
            80 -> return "Krimi"
            99 -> return "Dokumentum"
            18 -> return "Dráma"
            10751 -> return "Családi"
            14 -> return "Fantasy"
            36 -> return "Történelmi"
            27 -> return "Horror"
            10402 -> return "Zenés"
            9648 -> return "Misztikus"
            10749 -> return "Romantikus"
            878 -> return "Sci-fi"
            10770 -> return "TV film"
            53 -> return "Thriller"
            10752 -> return "Háborús"
            37 -> return "Western"
            else -> return ""
        }
    }

    fun doubleVotesToText(d: Double): String {
        val dec = DecimalFormat("#0.0", DecimalFormatSymbols.getInstance())
        return dec.format(d)
    }
}

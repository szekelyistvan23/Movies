package com.stevensekler.android.movies.Fragments


import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.StringDef
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast

import com.stevensekler.android.movies.MainActivity
import com.stevensekler.android.movies.Model.Movie
import com.stevensekler.android.movies.Model.UrlArray
import com.stevensekler.android.movies.R
import com.stevensekler.android.movies.Utils.NetworkQuery
import com.stevensekler.android.movies.Utils.NetworkQuery.QueryType
import com.stevensekler.android.movies.Utils.NetworkQuery.QueryType.*

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.net.URL
import java.util.ArrayList
import java.util.Locale

import com.stevensekler.android.movies.Utils.NetworkQuery.buildUrl


class ListFragment : Fragment(), LoaderManager.LoaderCallbacks<ArrayList<Movie>>, SearchView.OnQueryTextListener {
    private var urlResultArray: ArrayList<String>? = null
    private var recyclerView: RecyclerView? = null
    private var movieAdapter: MovieAdapter? = null
    private var searchItem: Menu? = null
    private var searchView: SearchView? = null
    private var cachedData: ArrayList<Movie>? = null
    private var configurationChange: Boolean = false
    private var search: String? = null
    private var searchEnabled: Boolean = false
    private var linearLayoutManager: LinearLayoutManager? = null
    private var loadingBarRecyclerView: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        configurationChange = false
        extractSavedInstanceState(savedInstanceState)

        if ((activity as MainActivity).menuPosition == 4) {
            setHasOptionsMenu(true)
        }


        var moviedbSearchUrl = UrlArray()


        if (!configurationChange) {
            val j = arguments.getInt(MainActivity.FRAGMENT_NUMBER)
            search = arguments.getString(MainActivity.SEARCH_STRING)

            searchEnabled = false

            when (j) {
                0 -> {
                }
                1 -> moviedbSearchUrl = buildUrl(popular, "")
                2 -> moviedbSearchUrl = buildUrl(now_playing, "")
                3 -> moviedbSearchUrl = buildUrl(top_rated, "")
                4 -> {moviedbSearchUrl = buildUrl(QueryType.search, search)
                    searchEnabled = true}
                else -> moviedbSearchUrl = buildUrl(popular, "")
            }
        }


        var checkSearch = false
        var change = false

        if (searchEnabled) {
            checkSearch = searchEnabled
        }

        if (configurationChange) {
            change = configurationChange
        }

        if (change && checkSearch && search != null) {
            moviedbSearchUrl = buildUrl(QueryType.search, search)
        }


        val moviedbSearchUrlArray = ArrayList<String>()
        for (i in 0 until moviedbSearchUrl.urlArraySize()) {
            moviedbSearchUrlArray.add(moviedbSearchUrl.getUrlArray(i).toString())
        }

        val queryBundle = Bundle()
        queryBundle.putStringArrayList(DATA_FROM_URL, moviedbSearchUrlArray)

        val loaderManager = activity.supportLoaderManager
        val moviedbSearchLoader = loaderManager.getLoader<String>(MOVIEDB_SEARCH_LOADER)


        if (moviedbSearchLoader == null) {
            loaderManager.initLoader(MOVIEDB_SEARCH_LOADER, queryBundle, this).forceLoad()
        } else {
            loaderManager.restartLoader(MOVIEDB_SEARCH_LOADER, queryBundle, this).forceLoad()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_list, container, false)

        recyclerView = rootView.findViewById(R.id.rv_movies_list)
        recyclerView!!.setHasFixedSize(true)

        linearLayoutManager = LinearLayoutManager(this.activity)
        linearLayoutManager!!.isItemPrefetchEnabled = true
        recyclerView!!.layoutManager = linearLayoutManager

        movieAdapter = MovieAdapter(ArrayList(), activity)
        recyclerView!!.adapter = movieAdapter

        loadingBarRecyclerView = rootView.findViewById(R.id.recyclerViewProgressBar)
        loadingBarRecyclerView!!.visibility = View.VISIBLE

        val config = activity.resources.configuration

        if (config.orientation == 2 && config.screenWidthDp >= 600) {
            val marginLayoutParams = recyclerView!!.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.setMargins((config.screenWidthDp * 0.25).toInt(), 0, (config.screenWidthDp * 0.25).toInt(), 0)
            recyclerView!!.layoutParams = marginLayoutParams
        }

        return rootView
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<ArrayList<Movie>> {
        return object : AsyncTaskLoader<ArrayList<Movie>>(context) {

            override fun onStartLoading() {
                super.onStartLoading()

                if (cachedData != null) {
                    loadingBarRecyclerView!!.visibility = View.GONE
                    deliverResult(cachedData)
                }

                if (cachedData == null && takeContentChanged()) {
                    forceLoad()
                }
            }

            override fun loadInBackground(): ArrayList<Movie>? {
                val searchQueryUrlString = args.getStringArrayList(DATA_FROM_URL) ?: return null
                val queryArray = ArrayList<Movie>()
                var queryArrayOne: ArrayList<Movie>?
                val searchQueryUrl = UrlArray()


                try {
                    for (i in searchQueryUrlString.indices) {
                        val searchUrl = URL(searchQueryUrlString[i])
                        searchQueryUrl.addToUrlArray(searchUrl)
                    }
                    urlResultArray = NetworkQuery.convertStreamToString(searchQueryUrl)
                    for (j in urlResultArray!!.indices) {
                        queryArrayOne = extractMoviesFromJSON(urlResultArray!![j])
                        for (k in queryArrayOne!!.indices) {
                            queryArray.add(queryArrayOne[k])
                        }
                    }
                    return queryArray
                } catch (e: IOException) {
                    e.printStackTrace()
                    return null

                }

            }

            override fun deliverResult(data: ArrayList<Movie>?) {
                cachedData = data
                super.deliverResult(data)
            }
        }

    }

    override fun onLoadFinished(loader: Loader<ArrayList<Movie>>, data: ArrayList<Movie>?) {
        var data = data

        if (data != null && data.size > 0) {
            cachedData = data
        }

        if ((activity as MainActivity).menuPosition == 0) {
            if ((activity as MainActivity).myMovieArraySize() != 0) {
                data = (activity as MainActivity).myMovieArray

            } else {
                Toast.makeText(activity, context.getString(R.string.empty_list), Toast.LENGTH_SHORT).show()

            }
        }

        if (data != null && recyclerView != null) {
            movieAdapter = MovieAdapter(data, activity)
            recyclerView!!.adapter = movieAdapter
        }
        if (loadingBarRecyclerView != null) {
            loadingBarRecyclerView!!.visibility = View.GONE
        }


        if ((activity as MainActivity).recyclerViewIndex > 0) {
            val layoutManager = recyclerView!!.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset((activity as MainActivity).recyclerViewIndex, (activity as MainActivity).recyclerViewTop)
            resetRecyclerViewPosition()

        }

        if (data != null && data.size == 0) {
            if ((activity as MainActivity).menuPosition == 4 && (activity as MainActivity).isSearchDisplay) {
                Toast.makeText(activity, context.getString(R.string.no_movie), Toast.LENGTH_SHORT).show()
                (activity as MainActivity).isSearchDisplay = false
            }

            if ((activity as MainActivity).menuPosition == 4) {
                (activity as MainActivity).isSearchDisplay = true
            }
        }
    }

    override fun onLoaderReset(loader: Loader<ArrayList<Movie>>) {}

    private fun extractSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelableArrayList<Parcelable>(CACHED_DATA) != null && savedInstanceState.getParcelableArrayList<Parcelable>(CACHED_DATA)!!.size > 0) {
                cachedData = savedInstanceState.getParcelableArrayList(CACHED_DATA)
            }

            if (savedInstanceState.getInt(INDEX) > 0) {
                (activity as MainActivity).recyclerViewIndex = savedInstanceState.getInt(INDEX)
            }
            if (savedInstanceState.getInt(TOP) < -1) {
                (activity as MainActivity).recyclerViewTop = savedInstanceState.getInt(TOP)
            }
            if (savedInstanceState.getString(SEARCH_WORD) != null) {
                search = savedInstanceState.getString(SEARCH_WORD)
            }

            if (savedInstanceState.getBoolean(ENABLED)) {
                searchEnabled = savedInstanceState.getBoolean(ENABLED)
            }

            configurationChange = savedInstanceState.getBoolean(CONFIGURATION_CHANGE)

        }
    }

    private fun extractMoviesFromJSON(moviesJSON: String): ArrayList<Movie>? {
        val moviesList = ArrayList<Movie>()
        if (TextUtils.isEmpty(moviesJSON)) {
            return null
        }

        try {

            val jsonResult = JSONObject(moviesJSON)

            val resultsArray = jsonResult.getJSONArray("results")
            val i = resultsArray.length()
            if (i > 0) {
                for (j in 0 until i) {

                    val movieInfo = resultsArray.getJSONObject(j)
                    val genreIds = movieInfo.getJSONArray("genre_ids")

                    val id = movieInfo.getInt("id")
                    val name = movieInfo.getString("title")
                    val genre = jsonArrayToString(genreIds)
                    val release = movieInfo.getString("release_date")
                    val votes = movieInfo.getString("vote_average")
                    var image = movieInfo.getString("poster_path")
                    image = rawPosterPathProcess(image, ImageWidth.w154)

                    moviesList.add(Movie(id, name, genre, release, java.lang.Double.parseDouble(votes), image, 0, false))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return moviesList
    }

    private fun rawPosterPathProcess(posterPath: String?, size: ImageWidth?): String {
        var startPath = posterPath
        var path = ""
        if (startPath != null && size != null) {
            startPath = startPath.substring(1)
            path = "$IMAGE_BASE_URL$size//$startPath"
        }
        return path
    }

    fun jsonArrayToString(array: JSONArray): String {
        var result = ""
        val language = Locale.getDefault().displayLanguage.toLowerCase()
        try {
            for (i in 0 until array.length()) {
                if (language == "magyar") {
                    result += NetworkQuery.returnGenreNameHungarian(array.get(i) as Int) + "  "
                } else {
                    result += NetworkQuery.returnGenreName(array.get(i) as Int) + "  "
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return result
    }


    override fun onQueryTextSubmit(query: String): Boolean {

        search = query

        val fragment = (activity as MainActivity).newListFragment(4, query)

        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.list_layout, fragment)
        fragmentTransaction.commit()

        searchItem!!.findItem(R.id.action_search).collapseActionView()
        searchView!!.setQuery("", false)
        searchView!!.clearFocus()


        (activity as MainActivity).closeNavigationDrawer()

        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_search, menu)
        searchItem = menu

        searchView = menu!!.findItem(R.id.action_search).actionView as SearchView

        searchView!!.setOnQueryTextListener(this)
        searchView!!.queryHint = context.getString(R.string.action_search)
    }

    fun resetRecyclerViewPosition() {
        (activity as MainActivity).recyclerViewIndex = -1
        (activity as MainActivity).recyclerViewTop = -1
    }

    enum class ImageWidth {w92, w154, w185, w342, w500, w780, original}

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        val index = linearLayoutManager!!.findFirstVisibleItemPosition()
        val v = recyclerView!!.getChildAt(0)
        val top = if (v == null) 0 else v.top - recyclerView!!.paddingTop

        if (cachedData != null) {
            outState!!.putParcelableArrayList(CACHED_DATA, cachedData)
        }
        outState!!.putBoolean(CONFIGURATION_CHANGE, true)
        if (searchEnabled) {
            outState.putBoolean(ENABLED, searchEnabled)
        }
        if (search != null) {
            outState.putString(SEARCH_WORD, search)
            (activity as MainActivity).setSearchString(search.toString())
        }

        if (index > 0)
            outState.putInt(INDEX, index)
        if (top < -1)
            outState.putInt(TOP, top)
    }

    companion object {
        private val MOVIEDB_SEARCH_LOADER = 22
        private val IMAGE_BASE_URL = "http://image.tmdb.org/t/p/"
        private val CACHED_DATA = "cached_data"
        private val CONFIGURATION_CHANGE = "configuration_change"
        private val ENABLED = "ENABLED"
        private val INDEX = "INDEX"
        private val SEARCH_WORD = "SEARCH"
        private val TOP = "TOP"
        val DATA_FROM_URL = "dataFromUrl"
    }
}

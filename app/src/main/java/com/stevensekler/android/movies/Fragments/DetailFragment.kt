package com.stevensekler.android.movies.Fragments

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.StringDef
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.stevensekler.android.movies.DetailActivity
import com.stevensekler.android.movies.Fragments.DetailFragment.ImageWidth.*
import com.stevensekler.android.movies.Model.Cast
import com.stevensekler.android.movies.Model.Movie
import com.stevensekler.android.movies.Model.MovieDetails
import com.stevensekler.android.movies.R
import com.stevensekler.android.movies.Utils.NetworkQuery

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.net.URL
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.Locale


class DetailFragment : Fragment(), LoaderManager.LoaderCallbacks<MovieDetails> {
    private var urlResult: String? = null
    private var fragmentContext: Context? = null


    private var movieBackdrop: ImageView? = null
    private var movieLength: TextView? = null
    private var movieGenres: TextView? = null
    private var movieGenresHeader: TextView? = null
    private var movieDescriptionHeader: TextView? = null
    private var movieProductionDetails: TextView? = null
    private var movieVotes: TextView? = null
    private var movieDescription: TextView? = null
    private var movieComments: TextView? = null
    private var movieCommentsHeader: TextView? = null
    private var castHeader: TextView? = null
    private var recommendationsHeader: TextView? = null
    private var similarHeader: TextView? = null
    private var productionDetailsHeader: TextView? = null
    private var genresView: View? = null
    private var descriptionView: View? = null
    private var castView: View? = null
    private var productionDetailsView: View? = null
    private var recommendationsView: View? = null
    private var commentsView: View? = null
    private var animationView: View? = null
    private var backdropView: View? = null
    private var productionCompanies: String? = null
    private var productionCountries: String? = null
    private var backdropRecyclerView: RecyclerView? = null
    private var castRecyclerView: RecyclerView? = null
    private var recommendationsRecyclerView: RecyclerView? = null
    private var similarRecyclerView: RecyclerView? = null
    private var scrollView: ScrollView? = null
    private var backdropLoading: ProgressBar? = null
    private var cachedData: MovieDetails? = null


    enum class ImageWidth {w92, w154, w185, w342, w500, w780, original}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null && savedInstanceState.getParcelable<Parcelable>(CACHED_DATA) != null) {
            cachedData = savedInstanceState.getParcelable(CACHED_DATA)
        }

        productionCompanies = fragmentContext!!.resources.getString(R.string.production_companies)
        productionCountries = fragmentContext!!.resources.getString(R.string.production_countries)

        val movieId = arguments.getInt(MovieAdapter.MOVIE_ID)

        val moviedbSearchUrl = NetworkQuery.buildUrl(movieId)

        val queryBundle = Bundle()
        queryBundle.putString(MULTIPLE_QUERY_FROM_URL, moviedbSearchUrl!!.toString())

        activity.supportLoaderManager.initLoader(MOVIEDB_DETAIL_SEARCH_LOADER, queryBundle, this).forceLoad()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_detail, container, false)


        scrollView = rootView.findViewById(R.id.see_details)
        scrollView!!.visibility = View.INVISIBLE

        animationView = rootView.findViewById(R.id.animation_view)
        movieBackdrop = rootView.findViewById(R.id.imageView2)
        movieVotes = rootView.findViewById(R.id.film_votes)
        movieLength = rootView.findViewById(R.id.film_length)

        backdropRecyclerView = rootView.findViewById(R.id.rv_backdrop)
        backdropRecyclerView!!.setHasFixedSize(true)
        val backdropLinearLayoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        backdropRecyclerView!!.layoutManager = backdropLinearLayoutManager
        val backdropAdapter = BackdropAdapter(ArrayList(), activity)
        backdropRecyclerView!!.adapter = backdropAdapter


        backdropView = rootView.findViewById(R.id.view_backdrop)
        movieGenresHeader = rootView.findViewById(R.id.textView2)
        movieGenres = rootView.findViewById(R.id.film_genre)
        movieDescriptionHeader = rootView.findViewById(R.id.textView3)
        movieDescription = rootView.findViewById(R.id.film_description)
        castHeader = rootView.findViewById(R.id.textView7)

        castRecyclerView = rootView.findViewById(R.id.rv_detail_cast)
        castRecyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        castRecyclerView!!.layoutManager = linearLayoutManager
        val castAdapter = CastAdapter(ArrayList(), activity)
        castRecyclerView!!.adapter = castAdapter


        productionDetailsHeader = rootView.findViewById(R.id.textView8)
        movieProductionDetails = rootView.findViewById(R.id.production_details)
        recommendationsHeader = rootView.findViewById(R.id.textView9)

        recommendationsRecyclerView = rootView.findViewById(R.id.rv_recommendations)
        recommendationsRecyclerView!!.setHasFixedSize(true)
        val recommendationsLinearLayoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        recommendationsRecyclerView!!.layoutManager = recommendationsLinearLayoutManager
        val recommendationsAdapter = RecommendationsAdapter(ArrayList(), activity)
        recommendationsRecyclerView!!.adapter = recommendationsAdapter

        movieCommentsHeader = rootView.findViewById(R.id.textView10)
        movieComments = rootView.findViewById(R.id.film_comments)
        similarHeader = rootView.findViewById(R.id.textView11)

        similarRecyclerView = rootView.findViewById(R.id.rv_similar)
        similarRecyclerView!!.setHasFixedSize(true)
        val similarLinearLayoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        similarRecyclerView!!.layoutManager = similarLinearLayoutManager
        val similarAdapter = SimilarAdapter(ArrayList(), activity)
        similarRecyclerView!!.adapter = similarAdapter


        genresView = rootView.findViewById(R.id.view_genre)
        descriptionView = rootView.findViewById(R.id.view_description)
        castView = rootView.findViewById(R.id.view_cast)
        productionDetailsView = rootView.findViewById(R.id.view_production_details)
        recommendationsView = rootView.findViewById(R.id.view_recommendations)
        commentsView = rootView.findViewById(R.id.view_comments)
        backdropLoading = rootView.findViewById(R.id.backdrop_image_loading)


        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels

        movieBackdrop!!.layoutParams.width = width
        movieBackdrop!!.layoutParams.height = (width * 0.562820512820513).toInt()

        return rootView
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<MovieDetails> {
        return object : AsyncTaskLoader<MovieDetails>(context) {
            override fun onStartLoading() {
                super.onStartLoading()

                if (cachedData != null) {
                    deliverResult(cachedData)
                }

                if (cachedData == null) {
                    forceLoad()
                }
            }

            override fun loadInBackground(): MovieDetails? {
                val searchQueryUrlString = args.getString(MULTIPLE_QUERY_FROM_URL)
                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                    return null
                }


                try {
                    val searchUrl = URL(searchQueryUrlString)
                    urlResult = NetworkQuery.convertStreamToString(searchUrl)
                    return extractMoviesFromJSON(urlResult)
                } catch (e: IOException) {
                    e.printStackTrace()
                    return null
                }

            }

            override fun deliverResult(data: MovieDetails?) {
                cachedData = data
                super.deliverResult(data)
            }
        }
    }

    override fun onLoadFinished(loader: Loader<MovieDetails>, data: MovieDetails?) {

        animationView!!.visibility = View.GONE
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels

        movieBackdrop!!.layoutParams.width = width
        movieBackdrop!!.layoutParams.height = (width * 0.562820512820513).toInt()

        if (data != null) {

            val productionDetails = calculateProductionDetails(data)

            setupBackdropRecyclerView(data)
            setupCastRecyclerView(data)
            setupRecommendationRecyclerView(data)
            setupSimilarRecyclerView(data)

            loadBackdropImage(data, width)
            setupActionBar(data)
            setupGenreDetail(data)


            movieProductionDetails!!.text = productionDetails
            movieVotes!!.text = NetworkQuery.doubleVotesToText(data.votesDetail)
            scrollView!!.visibility = View.VISIBLE

            if (activity is DetailActivity && (activity as DetailActivity).fragmentHeaders != null) {
                extractHeadersFromArray()
                (activity as DetailActivity).fragmentHeaders = null
            }


            setupDescriptionDetail(data)
            setupCommentsFragment(data)


        } else if (!NetworkQuery.haveNetworkConnection(activity)) {
            Toast.makeText(activity, fragmentContext!!.resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, fragmentContext!!.resources.getString(R.string.no_data_available), Toast.LENGTH_SHORT).show()
        }

        setScrollPosition()

    }

    override fun onLoaderReset(loader: Loader<MovieDetails>) {}

    private fun extractMoviesFromJSON(moviesJSON: String?): MovieDetails? {
        var moviesDetail = MovieDetails()
        if (TextUtils.isEmpty(moviesJSON)) {
            return null
        }

        try {

            val movieInfo = JSONObject(moviesJSON)

            val genres = movieInfo.getJSONArray("genres")
            val companies = movieInfo.getJSONArray("production_companies")
            val countries = movieInfo.getJSONArray("production_countries")
            val credits = movieInfo.getJSONObject("credits")
            val cast = credits.getJSONArray("cast")
            val reviews = movieInfo.getJSONObject("reviews")
            val similar = movieInfo.getJSONObject(JSON_SIMILAR)
            val recommendations = movieInfo.getJSONObject(JSON_RECOMMENDATIONS)
            val images = movieInfo.getJSONObject("images")

            val id = movieInfo.getString("id")
            val name = movieInfo.getString("title")
            val genre = jsonArrayToStringGenre(genres)
            val overview = movieInfo.getString("overview")
            val release = formatDate(movieInfo.getString("release_date"))
            val votes = movieInfo.getString("vote_average")
            val backdropImage = rawPosterPathProcess(movieInfo.getString("backdrop_path"), w780)
            val voteCountDetail = movieInfo.getString("vote_count")
            val posterImage = rawPosterPathProcess(movieInfo.getString("poster_path"), w500)

            var runtime: String
            val JSON_RUNTIME = "runtime"
            if (movieInfo.getString(JSON_RUNTIME) != "null" || movieInfo.getString(JSON_RUNTIME) != null ||
                    movieInfo.getString(JSON_RUNTIME) == "0") {
                runtime = runtimeFormatter(movieInfo.getString(JSON_RUNTIME))
            } else {
                runtime = "- " + fragmentContext!!.resources.getString(R.string.minute)
            }

            if (runtime.toLowerCase().contains("null")) {
                runtime = "- " + fragmentContext!!.resources.getString(R.string.minute)
            }

            if (runtime == "0") {
                runtime = "- " + fragmentContext!!.resources.getString(R.string.minute)
            }


            val budget = numberFormatter(movieInfo.getInt("budget")) + " USD"
            val revenue = numberFormatter(movieInfo.getInt("revenue")) + " USD"
            val productionCompanies = jsonArrayToString(companies, JSON_COMPANIES)
            val productionCountries = jsonArrayToString(countries, "countries")
            val castDetail = jsonArrayToCast(cast)
            val reviewDetail = jsonObjectToMap(reviews)
            val similarDetail = jsonObjectToMovieArray(similar, JSON_SIMILAR)
            val recommendationsDetail = jsonObjectToMovieArray(recommendations, JSON_RECOMMENDATIONS)
            val imagesDetail = jsonObjectToImageArray(images)


            moviesDetail = MovieDetails(id, name, genre, overview, release, java.lang.Double.parseDouble(votes), posterImage, backdropImage,
                    voteCountDetail, runtime, budget, revenue, productionCompanies,
                    productionCountries, castDetail, reviewDetail, similarDetail, recommendationsDetail, imagesDetail)


        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return moviesDetail
    }


    private fun rawPosterPathProcess(posterPath: String, size: ImageWidth): String {
        var s: String
        try {
            s = "$IMAGE_BASE_URL$size/$posterPath"
        } catch (e: ClassCastException) {
            e.printStackTrace()
            s = "NO_IMAGE"
        }

        return s
    }

    private fun runtimeFormatter(s: String): String {
        var i = 0
        try {
            i = Integer.parseInt(s)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return "- " + fragmentContext!!.resources.getString(R.string.minute)
        }

        var time = ""
        if (i / 60 >= 1) {
            time = time + i / 60 + fragmentContext!!.resources.getString(R.string.hour) + " "
        }

        if (i % 60 > 0) {
            time = time + i % 60 + fragmentContext!!.resources.getString(R.string.minute)
        }

        if (time == "") {
            time = "- " + fragmentContext!!.resources.getString(R.string.minute)
        }
        return time
    }

    private fun jsonArrayToString(array: JSONArray, s: String): String {
        var result = ""
        var nameId: JSONObject
        var count = 0
        try {
            for (i in 0 until array.length()) {
                nameId = array.getJSONObject(i)
                if (nameId.getString("name") != null) {
                    result += nameId.getString("name") + "   "
                    ++count
                }
            }

            if (s == JSON_COMPANIES && count == 1) {
                productionCompanies = fragmentContext!!.resources.getString(R.string.production_company)
            }

            if (s == JSON_COMPANIES && count > 1) {
                productionCompanies = fragmentContext!!.resources.getString(R.string.production_companies)
            }
            if (array.length() == 1 && s == "countries") {
                productionCountries = fragmentContext!!.resources.getString(R.string.production_country)
            } else {
                productionCountries = fragmentContext!!.resources.getString(R.string.production_countries)
            }

        } catch (e: org.json.JSONException) {
            e.printStackTrace()
        }

        return result
    }


    fun jsonArrayToString(array: JSONArray): String {
        var result = ""
        val language = Locale.getDefault().displayLanguage.toLowerCase()
        try {
            for (i in 0 until array.length()) {

                if (array.get(i) as Int == 878) {
                    result += fragmentContext!!.resources.getString(R.string.sci_fi) + "\n"
                } else {
                    if (language == "magyar") {
                        result += NetworkQuery.returnGenreNameHungarian(array.get(i) as Int) + "\n"
                    } else {
                        result += NetworkQuery.returnGenreName(array.get(i) as Int) + "\n"
                    }
                }
            }
        } catch (e: org.json.JSONException) {
            e.printStackTrace()
        }

        return result
    }


    private fun jsonArrayToStringGenre(array: JSONArray): String {
        var result = ""
        var nameId: JSONObject
        try {
            if (array.length() == 1 && movieGenresHeader != null) {
                movieGenresHeader!!.text = fragmentContext!!.resources.getString(R.string.genre)
            }
            for (i in 0 until array.length()) {
                nameId = array.getJSONObject(i)
                result += nameId.getString("name") + "   "
            }
        } catch (e: org.json.JSONException) {
            e.printStackTrace()
        }

        return result
    }

    private fun numberFormatter(number: Int): String {
        val numFormat = DecimalFormat("###,###,###")
        return numFormat.format(number.toLong())
    }

    private fun jsonArrayToCast(array: JSONArray): ArrayList<Cast> {
        val result = ArrayList<Cast>()
        var castDetail: JSONObject?
        var name = ""
        var character = ""
        var profilePath = ""
        var gender = 0
        var o: Any?

        val j = array.length()
        for (i in 0 until j) {
            try {
                castDetail = array.getJSONObject(i)

                if (castDetail != null) {
                    name = castDetail.getString("name")
                    character = castDetail.getString("character")
                    gender = castDetail.getInt("gender")
                    o = castDetail.get("profile_path")

                    if (o != null) {
                        profilePath = rawPosterPathProcess(castDetail.get("profile_path") as String, w185)
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
                profilePath = "NO_IMAGE"
            } finally {
                result.add(Cast(name, character, gender, profilePath))
            }
        }

        return result
    }


    private fun jsonObjectToMap(jobject: JSONObject): MutableMap<String, String> {
        val result = HashMap<String, String>()
        var partialResult: JSONObject
        var key: String
        var value: String
        try {
            val content = jobject.getJSONArray("results")

            if (content != null) {

                if (content.length() == 1 && movieCommentsHeader != null) {
                    movieCommentsHeader!!.text = fragmentContext!!.resources.getString(R.string.comment)
                }

                for (i in 0 until content.length()) {
                    partialResult = content.getJSONObject(i)
                    key = partialResult.get("author") as String
                    value = partialResult.get("content") as String
                    result[key] = value
                }
            }
        } catch (e: org.json.JSONException) {
            e.printStackTrace()
        }

        return result
    }

    private fun jsonObjectToMovieArray(jobject: JSONObject, s: String): ArrayList<Movie> {
        val result = ArrayList<Movie>()
        var partialResult: JSONObject
        val o: Any?
        var genres: String


        try {
            o = jobject.getJSONArray("results")
            if (o != null) {
                if (o.length() == 1 && s == JSON_RECOMMENDATIONS && recommendationsHeader != null && isAdded) {
                    recommendationsHeader!!.text = fragmentContext!!.resources.getString(R.string.recommendation)
                }
                if (o.length() == 1 && s == JSON_SIMILAR && similarHeader != null) {
                    similarHeader!!.text = fragmentContext!!.resources.getString(R.string.similar_movie)
                }

                for (i in 0 until o.length()) {
                    partialResult = o.getJSONObject(i)
                    genres = jsonArrayToString(partialResult.getJSONArray("genre_ids"))
                    try {
                        result.add(Movie(partialResult.getInt("id"), partialResult.getString("title"),
                                genres, partialResult.getString("release_date"), partialResult.getDouble("vote_average"),
                                rawPosterPathProcess(partialResult.get("poster_path") as String, w342), 0, false))
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }

                }
            }
        } catch (e: org.json.JSONException) {
            e.printStackTrace()
        }

        return result
    }

    private fun jsonObjectToImageArray(jobject: JSONObject): ArrayList<String> {
        val result = ArrayList<String>()
        var partialResult: JSONObject
        val o: Any?

        try {
            o = jobject.getJSONArray("backdrops")
            if (o != null) {
                for (i in 0 until o.length()) {
                    partialResult = o.getJSONObject(i)

                    try {
                        result.add(rawPosterPathProcess(partialResult.get("file_path") as String, w92))
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }

                }
            }
        } catch (e: org.json.JSONException) {
            e.printStackTrace()
        }

        return result
    }

    private fun commentsConcatenate(map: Map<String, String>?): String {
        var s = ""
        val threeNewLine = "\n\n\n"
        if (map != null && map.size > 0) {
            val it = map.entries.iterator()
            var pair = it.next()
            s += fragmentContext!!.resources.getString(R.string.author) + pair.key + threeNewLine + pair.value
            while (it.hasNext()) {
                pair = it.next()
                s += threeNewLine + fragmentContext!!.resources.getString(R.string.author) + pair.key + threeNewLine + pair.value
            }
            return s
        } else {
            return ""
        }
    }

    fun headersToText(): Array<String> {
        val array = arrayOf<String>()
        array[0] = movieProductionDetails!!.text.toString()
        array[1] = movieGenresHeader!!.text.toString()
        array[2] = movieCommentsHeader!!.text.toString()
        array[3] = recommendationsHeader!!.text.toString()
        array[4] = similarHeader!!.text.toString()

        return array
    }

    fun extractHeadersFromArray() {
        val array = (activity as DetailActivity).fragmentHeaders
        movieProductionDetails?.text = array?.get(0)
        movieGenresHeader?.text = array?.get(1)
        movieCommentsHeader?.text = array?.get(2)
        recommendationsHeader?.text = array?.get(3)
        similarHeader?.text = array?.get(4)

    }

    private fun calculateProductionDetails(data: MovieDetails): String {
        var count = 0

        var productionDetails = ""
        if (data.releaseDetail != "") {
            productionDetails += fragmentContext!!.resources.getString(R.string.release_date) + data.releaseDetail + "\n\n"
            count += 1
        }

        if (data.budgetDetail != "0 USD") {
            productionDetails += fragmentContext!!.resources.getString(R.string.budget) + data.budgetDetail + "\n\n"
            count += 1
        }

        if (data.revenueDetail != "0 USD") {
            productionDetails += fragmentContext!!.resources.getString(R.string.revenue) + data.revenueDetail + "\n\n"
            count += 1
        }

        if (data.productionCompaniesDetail != "") {
            productionDetails += productionCompanies + "     " + data.productionCompaniesDetail + "\n\n"
            count += 1
        }

        if (data.productionCountriesDetail != "") {
            productionDetails += productionCountries + "     " + data.productionCountriesDetail
            count += 1
        }

        if (count == 1) {
            productionDetailsHeader!!.text = fragmentContext!!.resources.getString(R.string.production_detail)
        }

        if (count == 0) {
            productionDetailsHeader!!.visibility = View.GONE
            movieProductionDetails!!.visibility = View.GONE
            productionDetailsView!!.visibility = View.GONE
        }

        return productionDetails
    }

    private fun setupBackdropRecyclerView(data: MovieDetails) {
        if (data.backdrops!!.size > 1) {
            val backdropAdapter = BackdropAdapter(data.backdrops!!, activity)
            backdropRecyclerView!!.adapter = backdropAdapter
        } else {
            backdropView!!.visibility = View.GONE
            backdropRecyclerView!!.visibility = View.GONE
        }
    }

    private fun setupCastRecyclerView(data: MovieDetails) {
        if (data.castDetail!!.size != 0) {
            val castAdapter = CastAdapter(data.castDetail!!, activity)
            castRecyclerView!!.adapter = castAdapter
        } else {
            castHeader!!.visibility = View.GONE
            castRecyclerView!!.visibility = View.GONE
            castView!!.visibility = View.GONE
        }
    }

    private fun setupRecommendationRecyclerView(data: MovieDetails) {
        if (data.recommendationsDetail!!.size != 0) {
            val recommendationsAdapter = RecommendationsAdapter(data.recommendationsDetail!!, activity)
            recommendationsRecyclerView!!.adapter = recommendationsAdapter
        } else {
            recommendationsHeader!!.visibility = View.GONE
            recommendationsRecyclerView!!.visibility = View.GONE
            recommendationsView!!.visibility = View.GONE
        }
    }

    private fun setupSimilarRecyclerView(data: MovieDetails) {
        if (data.similarDetail!!.size != 0) {
            val similarAdapter = SimilarAdapter(data.similarDetail!!, activity)
            similarRecyclerView!!.adapter = similarAdapter

        } else {
            similarHeader!!.visibility = View.GONE
            similarRecyclerView!!.visibility = View.GONE
        }
    }

    private fun loadBackdropImage(data: MovieDetails, width: Int) {
        backdropLoading!!.visibility = View.VISIBLE
        Picasso.with(fragmentContext)
                .load(data.backdropDetail)
                .error(R.drawable.film500)
                .resize((width * 0.95).toInt(), (width * 0.95 * 0.562820512820513).toInt())
                .into(movieBackdrop!!, object : Callback {
                    override fun onSuccess() {
                        backdropLoading!!.visibility = View.GONE
                    }

                    override fun onError() {
                        backdropLoading!!.visibility = View.GONE
                    }
                })
        movieLength!!.text = data.runtime
    }

    private fun setupActionBar(data: MovieDetails) {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true)
            actionBar.setCustomView(R.layout.action_bar_title_layout)
        }
        (activity.findViewById<View>(R.id.action_bar_title) as TextView).text = data.nameDetail
    }

    private fun setupDescriptionDetail(data: MovieDetails) {
        if (data.descriptionDetail != "" && data.descriptionDetail != "null") {
            movieDescription!!.text = data.descriptionDetail
        } else {
            movieDescriptionHeader!!.visibility = View.GONE
            movieDescription!!.visibility = View.GONE
            descriptionView!!.visibility = View.GONE
        }
    }

    private fun setupGenreDetail(data: MovieDetails) {
        if (data.genreDetail != "") {
            movieGenres!!.text = data.genreDetail
        } else {
            movieGenresHeader!!.visibility = View.GONE
            movieGenres!!.visibility = View.GONE
            genresView!!.visibility = View.GONE
        }
    }

    private fun setupCommentsFragment(data: MovieDetails) {

        val comments = commentsConcatenate(data.reviewsDetail)

        if (comments != "") {


            movieComments!!.text = comments
            movieComments!!.setOnClickListener {
                val args = Bundle()
                args.putString(COMMENTS_TEXT, comments)
                val newFragment = CommentsFragment()
                newFragment.isCancelable = false
                newFragment.arguments = args
                newFragment.show(activity.supportFragmentManager, "comments")
            }


        } else {
            movieCommentsHeader!!.visibility = View.GONE
            movieComments!!.visibility = View.GONE
            commentsView!!.visibility = View.GONE
        }
    }

    private fun setScrollPosition() {
        if (activity is DetailActivity && (activity as DetailActivity).scrollPosition != 0) {
            scrollView!!.post {
                try {
                    scrollView!!.smoothScrollTo(0, (activity as DetailActivity).scrollPosition)
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)


        if (scrollView!!.scrollY != 0 && activity is DetailActivity) {
            (activity as DetailActivity).scrollPosition = scrollView!!.scrollY
        }

        if (cachedData != null) {
            outState!!.putParcelable(CACHED_DATA, cachedData)
        }

        if (activity is DetailActivity) {
            (activity as DetailActivity).fragmentHeaders = headersToText()
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fragmentContext = context
    }

    companion object {
        private val MOVIEDB_DETAIL_SEARCH_LOADER = 23

        private val IMAGE_BASE_URL = "http://image.tmdb.org/t/p/"
        private val W92 = "w92"
        private val W154 = "w154"
        private val W185 = "w185"
        private val W342 = "w342"
        private val W500 = "w500"
        private val W780 = "w780"
        private val ORIGINAL = "original"
        val COMMENTS_TEXT = "comments_text"
        private val CACHED_DATA = "cached_data"
        private val JSON_SIMILAR = "similar"
        private val JSON_COMPANIES = "companies"
        private val JSON_RECOMMENDATIONS = "recommendations"
        private val MULTIPLE_QUERY_FROM_URL = "multipleQueryFromUrl"

        private fun formatDate(s: String): String {

            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            df.isLenient = false

            var date = Date()
            try {
                date = df.parse(s)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val string = date.toString()
            val array = string.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val language = Locale.getDefault().displayLanguage.toLowerCase()
            return if (language == "magyar") {
                array[5] + " " + array[1] + " " + array[2]
            } else {
                array[1] + " " + array[2] + " " + array[5]
            }
        }
    }
}


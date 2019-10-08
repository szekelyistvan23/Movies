package com.stevensekler.android.movies.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

import java.util.ArrayList

/**
 * Created by Szekely Istvan on 5/24/2017.
 *
 */
@Parcelize
class MovieDetails(
        private var movieIdDetail: String? = null,
        var nameDetail: String? = null,
        var genreDetail: String? = null,
        var descriptionDetail: String? = null,
        var releaseDetail: String? = null,
        var votesDetail: Double = 0.toDouble(),
        var posterDetail: String? = null,
        var backdropDetail: String? = null,
        var voteCountDetail: String? = null,
        var runtime: String? = null,
        var budgetDetail: String? = null,
        var revenueDetail: String? = null,
        var productionCompaniesDetail: String? = null,
        var productionCountriesDetail: String? = null,
        var castDetail: ArrayList<Cast>? = null,
        var reviewsDetail: MutableMap<String, String>? = null,
        var similarDetail: ArrayList<Movie>? = null,
        var recommendationsDetail: ArrayList<Movie>? = null,
        var backdrops: ArrayList<String>? = null) : Parcelable
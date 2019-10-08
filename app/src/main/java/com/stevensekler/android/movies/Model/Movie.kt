package com.stevensekler.android.movies.Model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Szekely Istvan on 12.05.2017.
 *
 */
@Parcelize
class Movie(
        var id: Int = 0,
        var name: String? = null,
        var genre: String? = null,
        var release: String? = null,
        var votes: Double = 0.toDouble(),
        var poster: String? = null,
        var watched: Int = 0,
        var isClicked: Boolean = false) : Parcelable
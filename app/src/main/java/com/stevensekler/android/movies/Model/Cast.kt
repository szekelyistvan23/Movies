package com.stevensekler.android.movies.Model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Szekely Istvan on 5/30/2017.
 *
 */
@Parcelize
class Cast(
    var name: String? = null,
    var character: String? = null,
    var gender: Int = 0,
    var profileImage: String? = null) : Parcelable
//    TODO:      private set ??????


package com.stevensekler.android.movies.Model


import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

import java.net.URL
import java.util.ArrayList

/**
 * Created by Szekely Istvan on 5/24/2017.
 *
 */
@Parcelize
class UrlArray : Parcelable {
    private var urlArrayList: ArrayList<URL>? = ArrayList()

    fun addToUrlArray(url: URL) {
        urlArrayList!!.add(url)
    }

    fun getUrlArray(i: Int): URL {
        return urlArrayList!![i]
    }

    fun urlArraySize(): Int {
        return urlArrayList!!.size
    }
}

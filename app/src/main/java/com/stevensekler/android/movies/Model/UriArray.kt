package com.stevensekler.android.movies.Model

import android.net.Uri

import java.util.ArrayList

/**
 * Created by Szekely Istvan on 5/24/2017.
 *
 */

class UriArray {
    private val uriArrayList: ArrayList<Uri> = ArrayList()

    fun addToUriArray(uri: Uri) {
        uriArrayList.add(uri)
    }

    fun uriArraySize(): Int {
        return uriArrayList.size
    }

    fun getUriArray(i: Int): Uri {
        return uriArrayList[i]
    }
}

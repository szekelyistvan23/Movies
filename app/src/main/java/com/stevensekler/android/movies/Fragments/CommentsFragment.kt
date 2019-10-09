package com.stevensekler.android.movies.Fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

/**
 * Created by Szekely Istvan on 6/28/2017.
 *
 */

class CommentsFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val s = arguments.getString(DetailFragment.COMMENTS_TEXT)
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(s)
                .setPositiveButton("Ok") { dialog, id -> }
        return builder.create()

    }
}

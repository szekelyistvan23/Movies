package com.stevensekler.android.movies.Fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

import com.stevensekler.android.movies.MainActivity
import com.stevensekler.android.movies.R

/**
 * Created by Szekely Istvan on 6/27/2017.
 *
 */

class DeleteYoursListFragment : DialogFragment() {
    private var fragmentContext: Context? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(fragmentContext!!.resources.getString(R.string.delete_question))
                .setPositiveButton(resources.getString(R.string.delete)) { dialog, id -> (activity as MainActivity).updateList() }
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, id -> }
        return builder.create()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fragmentContext = context
    }
}

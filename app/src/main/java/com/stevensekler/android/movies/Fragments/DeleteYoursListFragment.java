package com.stevensekler.android.movies.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.stevensekler.android.movies.MainActivity;
import com.stevensekler.android.movies.R;

/**
 * Created by Szekely Istvan on 6/27/2017.
 *
 */

public class DeleteYoursListFragment extends DialogFragment {
    private Context fragmentContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(fragmentContext.getResources().getString(R.string.delete_question))
                .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity) getActivity()).updateList();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentContext = context;
    }
}

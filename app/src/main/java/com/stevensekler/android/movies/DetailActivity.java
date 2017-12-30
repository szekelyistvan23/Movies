package com.stevensekler.android.movies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.stevensekler.android.movies.Fragments.DetailFragment;

import java.util.Locale;

import static com.stevensekler.android.movies.Fragments.MovieAdapter.MOVIE_ID;

/**
 * Created by Szekely Istvan on 6/11/2017.
 *
 */


public class DetailActivity extends AppCompatActivity {


    private int scrollPosition;
    private String[] fragmentHeaders;
    private String defaultLanguage;
    private static final String SCROLL_POSITION = "scroll_position";
    private static final String FRAGMENT_HEADERS = "fragment_headers";
    private static final String DETAIL_FRAGMENT = "detail_fragment";
    private static final String DEFAULT_LANGUAGE = "default_language";

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getInt(SCROLL_POSITION) != 0) {
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION);
        } else {
            scrollPosition = 0;
        }

        if (savedInstanceState != null) {
            fragmentHeaders = savedInstanceState.getStringArray(FRAGMENT_HEADERS);
        }

        if (savedInstanceState != null &&
                !savedInstanceState.getString(DEFAULT_LANGUAGE).equals((Locale.getDefault().getDisplayLanguage().toLowerCase()))) {
            onBackPressed();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        defaultLanguage = Locale.getDefault().getDisplayLanguage().toLowerCase();
        Fragment fragment;
        if (savedInstanceState == null) {

            fragment = new DetailFragment();

            Bundle extras = getIntent().getExtras();
            int receivingInt = 0;
            if (extras != null) {
                receivingInt = extras.getInt(MOVIE_ID);
            }


            Bundle args = new Bundle();
            args.putInt(MOVIE_ID, receivingInt);

            fragment.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.add(R.id.detail_activity_movie, fragment, DETAIL_FRAGMENT);
            fragmentTransaction.commit();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public int getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
    }

    public String[] getFragmentHeaders() {
        return fragmentHeaders;
    }

    public void setFragmentHeaders(String[] fragmentHeaders) {
        this.fragmentHeaders = fragmentHeaders;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCROLL_POSITION, scrollPosition);
        outState.putStringArray(FRAGMENT_HEADERS, fragmentHeaders);
        outState.putString(DEFAULT_LANGUAGE, defaultLanguage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out);
    }
}

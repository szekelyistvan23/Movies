package com.stevensekler.android.movies

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

import com.stevensekler.android.movies.Fragments.DetailFragment
import com.stevensekler.android.movies.Fragments.MovieAdapter

import java.util.Locale

/**
 * Created by Szekely Istvan on 6/11/2017.
 *
 */


class DetailActivity : AppCompatActivity() {


    var scrollPosition: Int = 0
    var fragmentHeaders: Array<String>? = null
    private var defaultLanguage: String? = null

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        if (savedInstanceState != null && savedInstanceState.getInt(SCROLL_POSITION) != 0) {
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION)
        } else {
            scrollPosition = 0
        }

        if (savedInstanceState != null) {
            fragmentHeaders = savedInstanceState.getStringArray(FRAGMENT_HEADERS)
        }

        if (savedInstanceState != null && savedInstanceState.getString(DEFAULT_LANGUAGE) != Locale.getDefault().displayLanguage.toLowerCase()) {
            onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        defaultLanguage = Locale.getDefault().displayLanguage.toLowerCase()
        val fragment: Fragment
        if (savedInstanceState == null) {

            fragment = DetailFragment()

            val extras = intent.extras
            var receivingInt = 0
            if (extras != null) {
                receivingInt = extras.getInt(MovieAdapter.MOVIE_ID)
            }


            val args = Bundle()
            args.putInt(MovieAdapter.MOVIE_ID, receivingInt)

            fragment.setArguments(args)

            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            fragmentTransaction.add(R.id.detail_activity_movie, fragment, DETAIL_FRAGMENT)
            fragmentTransaction.commit()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_POSITION, scrollPosition)
        outState.putStringArray(FRAGMENT_HEADERS, fragmentHeaders)
        outState.putString(DEFAULT_LANGUAGE, defaultLanguage)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out)
    }

    companion object {
        private val SCROLL_POSITION = "scroll_position"
        private val FRAGMENT_HEADERS = "fragment_headers"
        private val DETAIL_FRAGMENT = "detail_fragment"
        private val DEFAULT_LANGUAGE = "default_language"
    }
}

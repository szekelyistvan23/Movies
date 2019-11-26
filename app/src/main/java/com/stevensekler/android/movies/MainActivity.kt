/*
 * Copyright (C) 2017 Szekely Istvan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stevensekler.android.movies

import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast

import com.stevensekler.android.movies.Fragments.DeleteYoursListFragment
import com.stevensekler.android.movies.Fragments.ListFragment
import com.stevensekler.android.movies.Fragments.NavigationDrawerAdapter
import com.stevensekler.android.movies.Model.Movie
import com.stevensekler.android.movies.Utils.MovieDatabaseHelper
import com.stevensekler.android.movies.Utils.NetworkQuery

import java.util.ArrayList

import com.stevensekler.android.movies.R.drawable.ic_add
import com.stevensekler.android.movies.R.drawable.ic_list_add_check
import com.stevensekler.android.movies.R.drawable.ic_list_item_watched

class MainActivity : AppCompatActivity() {
    private var movieArray: Array<String>? = null
    private var drawerList: ListView? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var drawerLayout: DrawerLayout? = null
    private var searchString: String? = null
    private var toast: Toast? = null
    private var cachedData: ArrayList<Movie>? = null
    var isSearchDisplay: Boolean = false
    var menuPosition: Int = 0
        private set
    private val database: SQLiteDatabase? = null
    private var movieDatabaseHelper: MovieDatabaseHelper? = null
    var myMovieArray: ArrayList<Movie>? = null
        private set
    private var fragment: Fragment? = null
    var recyclerViewIndex: Int = 0
    var recyclerViewTop: Int = 0


    override fun onStop() {
        super.onStop()
        movieDatabaseHelper!!.clearMovies(database)
        if (myMovieArray != null && myMovieArraySize() > 0) {
            for (i in 0 until myMovieArraySize()) {
                movieDatabaseHelper!!.insertMovie(myMovieArray!![i])
            }
        } else {
            movieDatabaseHelper!!.clearMovies(database)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (toast != null) {
            toast!!.cancel()
        }

        if (movieDatabaseHelper!!.numberOfRows() > 0) {
            database!!.close()
        }
        movieDatabaseHelper!!.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.activity_main)

        movieArray = resources.getStringArray(R.array.movie_array)

        var configurationChange: Boolean
        configurationChange = false
        if (savedInstanceState != null) {
            configurationChange = savedInstanceState.getBoolean(CONFIGURATION_CHANGE)
        }

        extractSavedInstanceSate(savedInstanceState, configurationChange)

        try {
            movieDatabaseHelper = MovieDatabaseHelper(this)
            myMovieArray = movieDatabaseHelper!!.allMovies
        } catch (e: Exception) {
            showToast(resources.getString(R.string.wrong))
            e.printStackTrace()
        }

        drawerList = findViewById<View>(R.id.left_drawer) as ListView
        drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout

        addDrawerItems()
        setupDrawer()


        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
        }


        val check = NetworkQuery.haveNetworkConnection(applicationContext)

        if (!check) {
            finish()
            showToast(resources.getString(R.string.no_internet))
        }

        var defaultPage = 2

        if (savedInstanceState != null && configurationChange) {
            defaultPage = savedInstanceState.getInt(MENU_POSITION)
        }

        selectItem(defaultPage)
    }

    private fun addDrawerItems() {
        val header = layoutInflater.inflate(R.layout.list_view_header, null)

        drawerList!!.addHeaderView(header, null, false)
        drawerList!!.adapter = NavigationDrawerAdapter(this, movieArray!!, movieIcons)
        drawerList!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectItem(position - 1)
            if (supportActionBar != null) {
                supportActionBar!!.title = movieArray!![menuPosition]
            }
        }
    }


    private fun setupDrawer() {
        drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(view: View?) {
                super.onDrawerClosed(view)
                if (supportActionBar != null) {
                    supportActionBar!!.title = movieArray!![menuPosition]
                }
                invalidateOptionsMenu()
            }

        }
        drawerToggle!!.isDrawerIndicatorEnabled = true
        drawerLayout!!.addDrawerListener(drawerToggle!!)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_delete_list) {
            if (myMovieArray!!.size > 0) {
                val newFragment = DeleteYoursListFragment()
                newFragment.isCancelable = false
                newFragment.show(supportFragmentManager, "delete")
            } else {
                showToast(getString(R.string.empty_list_already))
            }
            return true
        }

        if (id == R.id.action_exit) {
            finish()
            return true
        }

        return if (drawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)

    }

    private fun selectItem(position: Int) {
        if (menuPosition == 4) {
            isSearchDisplay = false
        }
        menuPosition = position

        fragment = newListFragment(position, searchString)


        if (NetworkQuery.haveNetworkConnection(applicationContext)) {
            showFragment(fragment)
        } else if (!NetworkQuery.haveNetworkConnection(applicationContext) && menuPosition == 0) {
            showFragment(fragment)
        } else {
            showToast(resources.getString(R.string.no_internet))
        }

        drawerLayout!!.closeDrawer(drawerList)

    }

    fun newListFragment(position: Int, searchString: String?): Fragment {
        val fragment = ListFragment()

        val args = Bundle()
        args.putInt(FRAGMENT_NUMBER, position)
        if (searchString != null) {
            args.putString(SEARCH_STRING, searchString)
        }
        fragment.arguments = args
        return fragment
    }

    fun addToMyMovieArray(movie: Movie) {
        if (!checkMovieInArray(movie.id)) {
            myMovieArray!!.add(movie)
        }
    }

    fun myMovieArraySize(): Int {
        return myMovieArray!!.size
    }

    private fun movieWatched(id: Int) {
        myMovieArray!![id].watched = 1
    }

    fun checkMovieInArray(movieId: Int): Boolean {
        val i = myMovieArraySize()
        for (j in 0 until i) {
            val k = myMovieArray!![j].id
            if (k == movieId) return true
        }
        return false
    }


    fun updateList(b: Boolean) {
        if (b) {
            myMovieArray = ArrayList()
        }
        if (menuPosition == 0) {
            drawerList!!.performItemClick(
                    drawerList!!.adapter.getView(1, null, null),
                    1,
                    drawerList!!.adapter.getItemId(1))
        }
    }

    fun updateList() {
        myMovieArray = ArrayList()
        if (menuPosition == 0) {
            drawerList!!.performItemClick(
                    drawerList!!.adapter.getView(1, null, null),
                    1,
                    drawerList!!.adapter.getItemId(1))
        }

        selectItem(menuPosition)
    }

    fun sortMovies() {
        val watched = ArrayList<Movie>()
        val notWatched = ArrayList<Movie>()
        for (movie in myMovieArray!!) {
            if (!isMovieWatched(movie.id)) {
                notWatched.add(movie)
            } else {
                watched.add(movie)
            }
        }
        myMovieArray!!.clear()
        myMovieArray!!.addAll(notWatched)
        myMovieArray!!.addAll(watched)
    }

    fun movieWatchedFromArray(movie: Movie): Boolean {
        for (j in 0 until myMovieArraySize()) {
            val k = myMovieArray!![j].id
            if (k == movie.id) {
                movieWatched(j)
                return true
            }
        }
        return false
    }

    private fun extractSavedInstanceSate(savedInstanceState: Bundle?, configurationChange: Boolean) {
        if (savedInstanceState != null && configurationChange && savedInstanceState.getInt(MENU_POSITION) > 0) {
            menuPosition = savedInstanceState.getInt(MENU_POSITION)
            if (supportActionBar != null) {
                supportActionBar!!.title = movieArray!![menuPosition]
            }
            searchString = savedInstanceState.getString(SEARCH)
            isSearchDisplay = savedInstanceState.getBoolean(SEARCH_DISPLAY)
            cachedData = savedInstanceState.getParcelableArrayList(CACHED_DATA)
        }
    }

    fun isMovieWatched(movieId: Int): Boolean {
        val i = myMovieArraySize()
        for (j in 0 until i) {
            val k = myMovieArray!![j].watched
            val id = myMovieArray!![j].id

            if (k == 1 && id == movieId) return true
        }
        return false
    }

    fun buttonCheck(id: Int): Int {
        if (isMovieWatched(id)) {
            return ic_list_item_watched
        }
        return if (checkMovieInArray(id) && !isMovieWatched(id)) {
            ic_list_add_check
        } else ic_add
    }

    private fun showFragment(fragment: Fragment?) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                        .replace(R.id.list_layout, fragment, LIST_FRAGMENT)
                        .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MENU_POSITION, menuPosition)
        outState.putBoolean(CONFIGURATION_CHANGE, true)
        if (searchString != null) {
            outState.putString(SEARCH, searchString)
        }
        if (isSearchDisplay) {
            outState.putBoolean(SEARCH_DISPLAY, isSearchDisplay)
        }
        if (cachedData != null) {
            outState.putParcelableArrayList(CACHED_DATA, cachedData)
        }
    }

    override fun onBackPressed() {

        if (drawerLayout!!.isDrawerOpen(drawerList!!)) {
            drawerLayout!!.closeDrawer(drawerList)
        } else {
            super.onBackPressed()
        }
    }

    fun closeNavigationDrawer() {
        if (drawerLayout!!.isDrawerOpen(drawerList!!)) {
            drawerLayout!!.closeDrawer(drawerList)
        }

    }

    fun showToast(text: String) {
        if (toast != null) {
            toast!!.cancel()
        }
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast!!.show()
    }

    fun setSearchString(searchString: String) {
        this.searchString = searchString
    }

    companion object {


        val FRAGMENT_NUMBER = "fragment_number"
        val SEARCH_STRING = "search_string"
        val MENU_POSITION = "menu_position"
        val CONFIGURATION_CHANGE = "configuration_change"
        val CACHED_DATA = "cached_data"
        val LIST_FRAGMENT = "list_fragment"
        val SEARCH = "search"
        private val SEARCH_DISPLAY = "search_display"
        private val movieIcons = intArrayOf(R.drawable.ic_list, R.drawable.ic_popular, R.drawable.ic_now_playing, R.drawable.ic_top_rated, R.drawable.ic_navigation_search)
    }

}
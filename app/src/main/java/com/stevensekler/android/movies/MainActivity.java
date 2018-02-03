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

package com.stevensekler.android.movies;

import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.stevensekler.android.movies.Fragments.DeleteYoursListFragment;
import com.stevensekler.android.movies.Fragments.ListFragment;
import com.stevensekler.android.movies.Fragments.NavigationDrawerAdapter;
import com.stevensekler.android.movies.Model.Movie;
import com.stevensekler.android.movies.Utils.MovieDatabaseHelper;
import com.stevensekler.android.movies.Utils.NetworkQuery;

import java.util.ArrayList;

import static com.stevensekler.android.movies.R.drawable.ic_add;
import static com.stevensekler.android.movies.R.drawable.ic_list_add_check;
import static com.stevensekler.android.movies.R.drawable.ic_list_item_watched;

public class MainActivity extends AppCompatActivity {


    public static final String FRAGMENT_NUMBER = "fragment_number";
    public static final String SEARCH_STRING = "search_string";
    public static final String MENU_POSITION = "menu_position";
    public static final String CONFIGURATION_CHANGE = "configuration_change";
    public static final String CACHED_DATA = "cached_data";
    public static final String LIST_FRAGMENT = "list_fragment";
    public static final String SEARCH = "search";
    private static final String SEARCH_DISPLAY = "search_display";
    private String[] movieArray;
    private static final int[] movieIcons = {R.drawable.ic_list, R.drawable.ic_popular, R.drawable.ic_now_playing,
            R.drawable.ic_top_rated, R.drawable.ic_navigation_search};
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private String searchString;
    private Toast toast;
    private ArrayList<Movie> cachedData;
    private boolean searchDisplay;
    private int menuPosition;
    private SQLiteDatabase database;
    private MovieDatabaseHelper movieDatabaseHelper;
    private ArrayList<Movie> myMovieArray;
    private Fragment fragment;
    private int recyclerViewIndex;
    private int recyclerViewTop;


    @Override
    protected void onStop() {
        super.onStop();
        movieDatabaseHelper.clearMovies(database);
        if (myMovieArray != null && myMovieArraySize() > 0) {
            for (int i = 0; i < myMovieArraySize(); i++) {
                movieDatabaseHelper.insertMovie(myMovieArray.get(i));
            }
        } else {
            movieDatabaseHelper.clearMovies(database);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (toast != null){
            toast.cancel();
        }

        if (movieDatabaseHelper.numberOfRows() > 0) {
            database.close();
        }
        movieDatabaseHelper.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        movieArray = getResources().getStringArray(R.array.movie_array);

        boolean configurationChange;
        configurationChange = false;
        if (savedInstanceState != null){
            configurationChange = savedInstanceState.getBoolean(CONFIGURATION_CHANGE);
        }

        extractSavedInstanceSate(savedInstanceState, configurationChange);

        try {
            movieDatabaseHelper = new MovieDatabaseHelper(this);
            myMovieArray = movieDatabaseHelper.getAllMovies();
        } catch (Exception e) {
            showToast(getResources().getString(R.string.wrong));
            e.printStackTrace();
        }

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        addDrawerItems();
        setupDrawer();


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


        boolean check = NetworkQuery.haveNetworkConnection(getApplicationContext());

        if (!check) {
            finish();
            showToast(getResources().getString(R.string.no_internet));
        }

        int defaultPage = 2;

        if (savedInstanceState != null && configurationChange) {
            defaultPage = savedInstanceState.getInt(MENU_POSITION);
        }

            selectItem(defaultPage);
    }

    private void addDrawerItems() {
        View header = getLayoutInflater().inflate(R.layout.list_view_header, null);

        drawerList.addHeaderView(header, null, false);
        drawerList.setAdapter(new NavigationDrawerAdapter(this, movieArray, movieIcons));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectItem(position - 1);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(movieArray[menuPosition]);
                }
            }
        });
    }


    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(movieArray[menuPosition]);
                }
                invalidateOptionsMenu();
            }

        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete_list) {
            if (myMovieArray.size() > 0) {
                DialogFragment newFragment = new DeleteYoursListFragment();
                newFragment.setCancelable(false);
                newFragment.show(getSupportFragmentManager(), "delete");
            } else {
                showToast(getString(R.string.empty_list_already));
            }
            return true;
        }

        if (id == R.id.action_exit) {
            finish();
            return true;
        }

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {
        if (menuPosition == 4) {
            searchDisplay = false;
        }
        menuPosition = position;

        fragment = newListFragment(position, searchString);


        if (NetworkQuery.haveNetworkConnection(getApplicationContext())) {
            showFragment(fragment);
        } else if (!NetworkQuery.haveNetworkConnection(getApplicationContext()) && menuPosition == 0) {
            showFragment(fragment);
        } else {
            showToast( getResources().getString(R.string.no_internet));
        }

        drawerLayout.closeDrawer(drawerList);

    }

    public Fragment newListFragment(int position, String searchString) {
        Fragment fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putInt(FRAGMENT_NUMBER, position);
        if (searchString != null) {
            args.putString(SEARCH_STRING, searchString);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public int getMenuPosition() {
        return menuPosition;
    }

    public ArrayList<Movie> getMyMovieArray() {
        return myMovieArray;
    }

    public void addToMyMovieArray(Movie movie) {
        if (!checkMovieInArray(movie.getId())) {
            myMovieArray.add(movie);
        }
    }

    public int myMovieArraySize() {
        return myMovieArray.size();
    }

    private void movieWatched(int id) {
        myMovieArray.get(id).setWatched(1);
    }

    public boolean checkMovieInArray(int movieId) {
        int i = myMovieArraySize();
        for (int j = 0; j < i; j++) {
            int k = getMyMovieArray().get(j).getId();
            if (k == movieId) return true;
        }
        return false;
    }


    public void updateList(boolean b) {
        if (b) {
            myMovieArray = new ArrayList<>();
        }
        if (menuPosition == 0) {
            drawerList.performItemClick(
                    drawerList.getAdapter().getView(1, null, null),
                    1,
                    drawerList.getAdapter().getItemId(1));
        }
    }

    public void updateList() {
        myMovieArray = new ArrayList<>();
        if (menuPosition == 0) {
            drawerList.performItemClick(
                    drawerList.getAdapter().getView(1, null, null),
                    1,
                    drawerList.getAdapter().getItemId(1));
        }

        selectItem(menuPosition);
    }

    public void sortMovies() {
        ArrayList<Movie> watched = new ArrayList<>();
        ArrayList<Movie> notWatched = new ArrayList<>();
        for (Movie movie : myMovieArray) {
            if (!isMovieWatched(movie.getId())) {
                notWatched.add(movie);
            } else {
                watched.add(movie);
            }
        }
        myMovieArray.clear();
        myMovieArray.addAll(notWatched);
        myMovieArray.addAll(watched);
    }

    public boolean movieWatchedFromArray(Movie movie) {
        for (int j = 0; j < myMovieArraySize(); j++) {
            int k = getMyMovieArray().get(j).getId();
            if (k == movie.getId()) {
                movieWatched(j);
                return true;
            }
        }
        return false;
    }

    private void extractSavedInstanceSate(Bundle savedInstanceState, boolean configurationChange) {
        if (savedInstanceState != null && configurationChange && savedInstanceState.getInt(MENU_POSITION) > 0) {
            menuPosition = savedInstanceState.getInt(MENU_POSITION);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(movieArray[menuPosition]);
            }
            searchString = savedInstanceState.getString(SEARCH);
            searchDisplay = savedInstanceState.getBoolean(SEARCH_DISPLAY);
            cachedData = savedInstanceState.getParcelableArrayList(CACHED_DATA);
        }
    }

    public boolean isMovieWatched(int movieId) {
        int i = myMovieArraySize();
        for (int j = 0; j < i; j++) {
            int k = getMyMovieArray().get(j).getWatched();
            int id = getMyMovieArray().get(j).getId();

            if (k == 1 && id == movieId) return true;
        }
        return false;
    }

    public int buttonCheck(int id) {
        if (isMovieWatched(id)) {
            return ic_list_item_watched;
        }
        if (checkMovieInArray(id) && !isMovieWatched(id)) {
            return ic_list_add_check;
        }
        return ic_add;
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_layout, fragment, LIST_FRAGMENT);
        fragmentTransaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MENU_POSITION, menuPosition);
        outState.putBoolean(CONFIGURATION_CHANGE, true);
        if (searchString != null) {
            outState.putString(SEARCH, searchString);
        }
        if (isSearchDisplay()) {
            outState.putBoolean(SEARCH_DISPLAY, searchDisplay);
        }
        if (cachedData != null) {
            outState.putParcelableArrayList(CACHED_DATA, cachedData);
        }
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(drawerList)) {
            drawerLayout.closeDrawer(drawerList);
        } else {
            super.onBackPressed();
        }
    }

    public void closeNavigationDrawer() {
        if (drawerLayout.isDrawerOpen(drawerList)) {
            drawerLayout.closeDrawer(drawerList);
        }

    }

    public void showToast(String text){
        if (toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public int getRecyclerViewIndex() {
        return recyclerViewIndex;
    }

    public void setRecyclerViewIndex(int recyclerViewIndex) {
        this.recyclerViewIndex = recyclerViewIndex;
    }

    public int getRecyclerViewTop() {
        return recyclerViewTop;
    }

    public void setRecyclerViewTop(int recyclerViewTop) {
        this.recyclerViewTop = recyclerViewTop;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public boolean isSearchDisplay() {
        return searchDisplay;
    }

    public void setSearchDisplay(boolean searchDisplay) {
        this.searchDisplay = searchDisplay;
    }

}
package com.stevensekler.android.movies.Fragments;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.stevensekler.android.movies.MainActivity;
import com.stevensekler.android.movies.Model.Movie;
import com.stevensekler.android.movies.Model.UrlArray;
import com.stevensekler.android.movies.R;
import com.stevensekler.android.movies.Utils.NetworkQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static com.stevensekler.android.movies.Utils.NetworkQuery.buildUrl;

public class ListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<ArrayList<Movie>>, SearchView.OnQueryTextListener {
    private static final int MOVIEDB_SEARCH_LOADER = 22;
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String CACHED_DATA = "cached_data";
    private static final String CONFIGURATION_CHANGE = "configuration_change";
    private static final String W92 = "w92";
    private static final String W154 = "w154";
    private static final String W185 = "w185";
    private static final String W342 = "w342";
    private static final String W500 = "w500";
    private static final String W780 = "w780";
    private static final String ORIGINAL = "original";
    private static final String ENABLED = "ENABLED";
    private static final String INDEX = "INDEX";
    private static final String SEARCH_WORD = "SEARCH";
    private static final String TOP = "TOP";
    public static final String DATA_FROM_URL = "dataFromUrl";
    private ArrayList<String> urlResultArray;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private Menu searchItem;
    private SearchView searchView;
    private ArrayList<Movie> cachedData;
    private boolean configurationChange;
    private String search;
    private boolean searchEnabled;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar loadingBarRecyclerView;


    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        configurationChange = false;
        extractSavedInstanceState(savedInstanceState);

        if (((MainActivity) getActivity()).getMenuPosition() == 4) {
            setHasOptionsMenu(true);
        }


        UrlArray moviedbSearchUrl = new UrlArray();


        if (!configurationChange) {
            int j = getArguments().getInt(MainActivity.FRAGMENT_NUMBER);
            search = getArguments().getString(MainActivity.SEARCH_STRING);

            searchEnabled = false;

            switch (j) {
                case 0:
                    break;
                case 1:
                    moviedbSearchUrl = buildUrl(NetworkQuery.POPULAR, "");
                    break;
                case 2:
                    moviedbSearchUrl = buildUrl(NetworkQuery.NOW_PLAYING, "");
                    break;
                case 3:
                    moviedbSearchUrl = buildUrl(NetworkQuery.TOP_RATED, "");
                    break;
                case 4:
                    if (search != null) {
                        moviedbSearchUrl = buildUrl(NetworkQuery.SEARCH, search);
                        searchEnabled = true;

                    }
                    break;
                default:
                    moviedbSearchUrl = buildUrl(NetworkQuery.POPULAR, "");

            }
        }


        boolean checkSearch = false;
        boolean change = false;

        if (searchEnabled) {
            checkSearch = searchEnabled;
        }

        if (configurationChange) {
            change = configurationChange;
        }

        if (change && checkSearch && search != null) {
            moviedbSearchUrl = buildUrl(NetworkQuery.SEARCH, search);
        }


        ArrayList<String> moviedbSearchUrlArray = new ArrayList<>();
        for (int i = 0; i < moviedbSearchUrl.urlArraySize(); i++) {
            moviedbSearchUrlArray.add(moviedbSearchUrl.getUrlArray(i).toString());
        }

        Bundle queryBundle = new Bundle();
        queryBundle.putStringArrayList(DATA_FROM_URL, moviedbSearchUrlArray);

        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Loader<String> moviedbSearchLoader = loaderManager.getLoader(MOVIEDB_SEARCH_LOADER);


        if (moviedbSearchLoader == null) {
            loaderManager.initLoader(MOVIEDB_SEARCH_LOADER, queryBundle, this).forceLoad();
        } else {
            loaderManager.restartLoader(MOVIEDB_SEARCH_LOADER, queryBundle, this).forceLoad();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = rootView.findViewById(R.id.rv_movies_list);
        recyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setItemPrefetchEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        movieAdapter = new MovieAdapter(new ArrayList<Movie>(), getActivity());
        recyclerView.setAdapter(movieAdapter);

        loadingBarRecyclerView = rootView.findViewById(R.id.recyclerViewProgressBar);
        loadingBarRecyclerView.setVisibility(View.VISIBLE);

        Configuration config = getActivity().getResources().getConfiguration();

        if (config.orientation == 2 && config.screenWidthDp >= 600) {
            ViewGroup.MarginLayoutParams marginLayoutParams =
                    (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
            marginLayoutParams.setMargins((int) (config.screenWidthDp * 0.25), 0, (int) (config.screenWidthDp * 0.25), 0);
            recyclerView.setLayoutParams(marginLayoutParams);
        }

        return rootView;
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(getContext()) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (cachedData != null) {
                    loadingBarRecyclerView.setVisibility(View.GONE);
                    deliverResult(cachedData);
                }

                if (cachedData == null && takeContentChanged()) {
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                ArrayList<String> searchQueryUrlString = args.getStringArrayList(DATA_FROM_URL);
                if (searchQueryUrlString == null) {
                    return null;
                }
                ArrayList<Movie> queryArray = new ArrayList<>();
                ArrayList<Movie> queryArrayOne;
                UrlArray searchQueryUrl = new UrlArray();


                try {
                    for (int i = 0; i < searchQueryUrlString.size(); i++) {
                        URL searchUrl = new URL(searchQueryUrlString.get(i));
                        searchQueryUrl.addToUrlArray(searchUrl);
                    }
                    urlResultArray = NetworkQuery.convertStreamToString(searchQueryUrl);
                    for (int j = 0; j < urlResultArray.size(); j++) {
                        queryArrayOne = extractMoviesFromJSON(urlResultArray.get(j));
                        for (int k = 0; k < queryArrayOne.size(); k++) {
                            queryArray.add(queryArrayOne.get(k));
                        }
                    }
                    return queryArray;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;

                }
            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                cachedData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {

        if (data != null && data.size() > 0) {
            cachedData = data;
        }

        if (((MainActivity) getActivity()).getMenuPosition() == 0) {
            if ((((MainActivity) getActivity()).myMovieArraySize() != 0)) {
                data = ((MainActivity) getActivity()).getMyMovieArray();

            } else {
                Toast.makeText(getActivity(), getContext().getString(R.string.empty_list), Toast.LENGTH_SHORT).show();

            }
        }

        if (data != null && recyclerView != null) {
            movieAdapter = new MovieAdapter(data, getActivity());
            recyclerView.setAdapter(movieAdapter);
        }
        if (loadingBarRecyclerView != null) {
            loadingBarRecyclerView.setVisibility(View.GONE);
        }


        if (((MainActivity) getActivity()).getRecyclerViewIndex() > 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(((MainActivity) getActivity()).getRecyclerViewIndex()
                    , ((MainActivity) getActivity()).getRecyclerViewTop());
            resetRecyclerViewPosition();

        }

        if (data != null && data.size() == 0) {
            if (((MainActivity) getActivity()).getMenuPosition() == 4 && ((MainActivity) getActivity()).isSearchDisplay()) {
                Toast.makeText(getActivity(), getContext().getString(R.string.no_movie), Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).setSearchDisplay(false);
            }

            if (((MainActivity) getActivity()).getMenuPosition() == 4) {
                ((MainActivity) getActivity()).setSearchDisplay(true);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    private void extractSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelableArrayList(CACHED_DATA) != null && savedInstanceState.getParcelableArrayList(CACHED_DATA).size() > 0) {
                cachedData = savedInstanceState.getParcelableArrayList(CACHED_DATA);
            }

            if (savedInstanceState.getInt(INDEX) > 0) {
                ((MainActivity) getActivity()).setRecyclerViewIndex(savedInstanceState.getInt(INDEX));
            }
            if (savedInstanceState.getInt(TOP) < -1) {
                ((MainActivity) getActivity()).setRecyclerViewTop(savedInstanceState.getInt(TOP));
            }
            if (savedInstanceState.getString(SEARCH_WORD) != null) {
                search = savedInstanceState.getString(SEARCH_WORD);
            }

            if (savedInstanceState.getBoolean(ENABLED)) {
                searchEnabled = savedInstanceState.getBoolean(ENABLED);
            }

            configurationChange = savedInstanceState.getBoolean(CONFIGURATION_CHANGE);

        }
    }

    private ArrayList<Movie> extractMoviesFromJSON(String moviesJSON) {
        ArrayList<Movie> moviesList = new ArrayList<>();
        if (TextUtils.isEmpty(moviesJSON)) {
            return null;
        }

        try {

            JSONObject jsonResult = new JSONObject(moviesJSON);

            JSONArray resultsArray = jsonResult.getJSONArray("results");
            int i = resultsArray.length();
            if (i > 0) {
                for (int j = 0; j < i; j++) {

                    JSONObject movieInfo = resultsArray.getJSONObject(j);
                    JSONArray genreIds = movieInfo.getJSONArray("genre_ids");

                    int id = movieInfo.getInt("id");
                    String name = movieInfo.getString("title");
                    String genre = jsonArrayToString(genreIds);
                    String release = movieInfo.getString("release_date");
                    String votes = movieInfo.getString("vote_average");
                    String image = movieInfo.getString("poster_path");
                    image = rawPosterPathProcess(image, W154);

                    moviesList.add(new Movie(id, name, genre, release, Double.parseDouble(votes), image, 0, false));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return moviesList;
    }

    private String rawPosterPathProcess(String posterPath, @ImageWidth String size) {
        String path = "";
        if (posterPath != null && size != null) {
            posterPath = posterPath.substring(1);
            path = IMAGE_BASE_URL + size + "//" + posterPath;
        }
        return path;
    }

    public String jsonArrayToString(JSONArray array) {
        String result = "";
        String language = Locale.getDefault().getDisplayLanguage().toLowerCase();
        try {
            for (int i = 0; i < array.length(); i++) {
                if (language.equals("magyar")) {
                    result += NetworkQuery.returnGenreNameHungarian((int) array.get(i)) + "  ";
                } else {
                    result += NetworkQuery.returnGenreName((int) array.get(i)) + "  ";
                }
            }
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        search = query;

        Fragment fragment = ((MainActivity) getActivity()).newListFragment(4, query);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_layout, fragment);
        fragmentTransaction.commit();

        searchItem.findItem(R.id.action_search).collapseActionView();
        searchView.setQuery("", false);
        searchView.clearFocus();


        ((MainActivity) getActivity()).closeNavigationDrawer();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        searchItem = menu;

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getContext().getString(R.string.action_search));
    }

    public void resetRecyclerViewPosition() {
        ((MainActivity) getActivity()).setRecyclerViewIndex(-1);
        ((MainActivity) getActivity()).setRecyclerViewTop(-1);
    }

    @StringDef({W92, W154, W185, W342, W500, W780, ORIGINAL})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ImageWidth {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int index = linearLayoutManager.findFirstVisibleItemPosition();
        View v = recyclerView.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - recyclerView.getPaddingTop());

        if (cachedData != null) {
            outState.putParcelableArrayList(CACHED_DATA, cachedData);
        }
        outState.putBoolean(CONFIGURATION_CHANGE, true);
        if (searchEnabled) {
            outState.putBoolean(ENABLED, searchEnabled);
        }
        if (search != null) {
            outState.putString(SEARCH_WORD, search);
            ((MainActivity) getActivity()).setSearchString(search);
        }

        if (index > 0)
            outState.putInt(INDEX, index);
        if (top < -1)
            outState.putInt(TOP, top);
    }
}

package com.stevensekler.android.movies.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stevensekler.android.movies.DetailActivity;
import com.stevensekler.android.movies.Model.Cast;
import com.stevensekler.android.movies.Model.Movie;
import com.stevensekler.android.movies.Model.MovieDetails;
import com.stevensekler.android.movies.R;
import com.stevensekler.android.movies.Utils.NetworkQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class DetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<MovieDetails> {
    private static final int MOVIEDB_DETAIL_SEARCH_LOADER = 23;
    private String urlResult;
    private Context fragmentContext;


    private ImageView movieBackdrop;
    private TextView movieLength;
    private TextView movieGenres;
    private TextView movieGenresHeader;
    private TextView movieDescriptionHeader;
    private TextView movieProductionDetails;
    private TextView movieVotes;
    private TextView movieDescription;
    private TextView movieComments;
    private TextView movieCommentsHeader;
    private TextView castHeader;
    private TextView recommendationsHeader;
    private TextView similarHeader;
    private TextView productionDetailsHeader;
    private View genresView;
    private View descriptionView;
    private View castView;
    private View productionDetailsView;
    private View recommendationsView;
    private View commentsView;
    private View animationView;
    private View backdropView;
    private String productionCompanies;
    private String productionCountries;
    private RecyclerView backdropRecyclerView;
    private RecyclerView castRecyclerView;
    private RecyclerView recommendationsRecyclerView;
    private RecyclerView similarRecyclerView;
    private ScrollView scrollView;
    private ProgressBar backdropLoading;
    private MovieDetails cachedData;

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String W92 = "w92";
    private static final String W154 = "w154";
    private static final String W185 = "w185";
    private static final String W342 = "w342";
    private static final String W500 = "w500";
    private static final String W780 = "w780";
    private static final String ORIGINAL = "original";
    public static final String COMMENTS_TEXT = "comments_text";
    private static final String CACHED_DATA = "cached_data";
    private static final String JSON_SIMILAR = "similar";
    private static final String JSON_COMPANIES = "companies";
    private static final String JSON_RECOMMENDATIONS = "recommendations";
    private static final String MULTIPLE_QUERY_FROM_URL = "multipleQueryFromUrl";


    @StringDef({W92, W154, W185, W342, W500, W780, ORIGINAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ImageWidth {
    }


    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getParcelable(CACHED_DATA) != null) {
            cachedData = savedInstanceState.getParcelable(CACHED_DATA);
        }

        productionCompanies = fragmentContext.getResources().getString(R.string.production_companies);
        productionCountries = fragmentContext.getResources().getString(R.string.production_countries);

        int movieId = getArguments().getInt(MovieAdapter.MOVIE_ID);

        URL moviedbSearchUrl = NetworkQuery.buildUrl(movieId);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(MULTIPLE_QUERY_FROM_URL, moviedbSearchUrl.toString());

        getActivity().getSupportLoaderManager().initLoader(MOVIEDB_DETAIL_SEARCH_LOADER, queryBundle, this).forceLoad();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        scrollView = rootView.findViewById(R.id.see_details);
        scrollView.setVisibility(View.INVISIBLE);

        animationView = rootView.findViewById(R.id.animation_view);
        movieBackdrop = rootView.findViewById(R.id.imageView2);
        movieVotes = rootView.findViewById(R.id.film_votes);
        movieLength = rootView.findViewById(R.id.film_length);

        backdropRecyclerView = rootView.findViewById(R.id.rv_backdrop);
        backdropRecyclerView.setHasFixedSize(true);
        LinearLayoutManager backdropLinearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        backdropRecyclerView.setLayoutManager(backdropLinearLayoutManager);
        BackdropAdapter backdropAdapter = new BackdropAdapter(new ArrayList<String>(), getActivity());
        backdropRecyclerView.setAdapter(backdropAdapter);


        backdropView = rootView.findViewById(R.id.view_backdrop);
        movieGenresHeader = rootView.findViewById(R.id.textView2);
        movieGenres = rootView.findViewById(R.id.film_genre);
        movieDescriptionHeader = rootView.findViewById(R.id.textView3);
        movieDescription = rootView.findViewById(R.id.film_description);
        castHeader = rootView.findViewById(R.id.textView7);

        castRecyclerView = rootView.findViewById(R.id.rv_detail_cast);
        castRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        castRecyclerView.setLayoutManager(linearLayoutManager);
        CastAdapter castAdapter = new CastAdapter(new ArrayList<Cast>(), getActivity());
        castRecyclerView.setAdapter(castAdapter);


        productionDetailsHeader = rootView.findViewById(R.id.textView8);
        movieProductionDetails = rootView.findViewById(R.id.production_details);
        recommendationsHeader = rootView.findViewById(R.id.textView9);

        recommendationsRecyclerView = rootView.findViewById(R.id.rv_recommendations);
        recommendationsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager recommendationsLinearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        recommendationsRecyclerView.setLayoutManager(recommendationsLinearLayoutManager);
        RecommendationsAdapter recommendationsAdapter = new RecommendationsAdapter(new ArrayList<Movie>(), getActivity());
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);

        movieCommentsHeader = rootView.findViewById(R.id.textView10);
        movieComments = rootView.findViewById(R.id.film_comments);
        similarHeader = rootView.findViewById(R.id.textView11);

        similarRecyclerView = rootView.findViewById(R.id.rv_similar);
        similarRecyclerView.setHasFixedSize(true);
        LinearLayoutManager similarLinearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        similarRecyclerView.setLayoutManager(similarLinearLayoutManager);
        SimilarAdapter similarAdapter = new SimilarAdapter(new ArrayList<Movie>(), getActivity());
        similarRecyclerView.setAdapter(similarAdapter);


        genresView = rootView.findViewById(R.id.view_genre);
        descriptionView = rootView.findViewById(R.id.view_description);
        castView = rootView.findViewById(R.id.view_cast);
        productionDetailsView = rootView.findViewById(R.id.view_production_details);
        recommendationsView = rootView.findViewById(R.id.view_recommendations);
        commentsView = rootView.findViewById(R.id.view_comments);
        backdropLoading = rootView.findViewById(R.id.backdrop_image_loading);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final int width = displayMetrics.widthPixels;

        movieBackdrop.getLayoutParams().width = width;
        movieBackdrop.getLayoutParams().height = (int) (width * 0.562820512820513);

        return rootView;
    }

    @Override
    public Loader<MovieDetails> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieDetails>(getContext()) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (cachedData != null) {
                    deliverResult(cachedData);
                }

                if (cachedData == null) {
                    forceLoad();
                }
            }

            @Override
            public MovieDetails loadInBackground() {
                String searchQueryUrlString = args.getString(MULTIPLE_QUERY_FROM_URL);
                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }


                try {
                    URL searchUrl = new URL(searchQueryUrlString);
                    urlResult = NetworkQuery.convertStreamToString(searchUrl);
                    return extractMoviesFromJSON(urlResult);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(MovieDetails data) {
                cachedData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieDetails> loader, MovieDetails data) {

        animationView.setVisibility(View.GONE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final int width = displayMetrics.widthPixels;

        movieBackdrop.getLayoutParams().width = width;
        movieBackdrop.getLayoutParams().height = (int) (width * 0.562820512820513);

        if (data != null) {

            String productionDetails = calculateProductionDetails(data);

            setupBackdropRecyclerView(data);
            setupCastRecyclerView(data);
            setupRecommendationRecyclerView(data);
            setupSimilarRecyclerView(data);

            loadBackdropImage(data, width);
            setupActionBar(data);
            setupGenreDetail(data);


            movieProductionDetails.setText(productionDetails);
            movieVotes.setText(NetworkQuery.doubleVotesToText(data.getVotesDetail()));
            scrollView.setVisibility(View.VISIBLE);

            if (getActivity() instanceof DetailActivity && ((DetailActivity) getActivity()).getFragmentHeaders() != null) {
                extractHeadersFromArray();
                ((DetailActivity) getActivity()).setFragmentHeaders(null);
            }


            setupDescriptionDetail(data);
            setupCommentsFragment(data);


        } else if (!NetworkQuery.haveNetworkConnection(getActivity())) {
            Toast.makeText(getActivity(), fragmentContext.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), fragmentContext.getResources().getString(R.string.no_data_available), Toast.LENGTH_SHORT).show();
        }

        setScrollPosition();

    }

    @Override
    public void onLoaderReset(Loader<MovieDetails> loader) {
    }

    private MovieDetails extractMoviesFromJSON(String moviesJSON) {
        MovieDetails moviesDetail = new MovieDetails();
        if (TextUtils.isEmpty(moviesJSON)) {
            return null;
        }

        try {

            JSONObject movieInfo = new JSONObject(moviesJSON);

            JSONArray genres = movieInfo.getJSONArray("genres");
            JSONArray companies = movieInfo.getJSONArray("production_companies");
            JSONArray countries = movieInfo.getJSONArray("production_countries");
            JSONObject credits = movieInfo.getJSONObject("credits");
            JSONArray cast = credits.getJSONArray("cast");
            JSONObject reviews = movieInfo.getJSONObject("reviews");
            JSONObject similar = movieInfo.getJSONObject(JSON_SIMILAR);
            JSONObject recommendations = movieInfo.getJSONObject(JSON_RECOMMENDATIONS);
            JSONObject images = movieInfo.getJSONObject("images");

            String id = movieInfo.getString("id");
            String name = movieInfo.getString("title");
            String genre = jsonArrayToStringGenre(genres);
            String overview = movieInfo.getString("overview");
            String release = formatDate(movieInfo.getString("release_date"));
            String votes = movieInfo.getString("vote_average");
            String backdropImage = rawPosterPathProcess(movieInfo.getString("backdrop_path"), W780);
            String voteCountDetail = movieInfo.getString("vote_count");
            String posterImage = rawPosterPathProcess(movieInfo.getString("poster_path"), W500);

            String runtime;
            final String JSON_RUNTIME = "runtime";
            if (!movieInfo.getString(JSON_RUNTIME).equals("null") || movieInfo.getString(JSON_RUNTIME) != null ||
                    movieInfo.getString(JSON_RUNTIME).equals("0")) {
                runtime = runtimeFormatter(movieInfo.getString(JSON_RUNTIME));
            } else {
                runtime = "- " + fragmentContext.getResources().getString(R.string.minute);
            }

            if (runtime.toLowerCase().contains("null")) {
                runtime = "- " + fragmentContext.getResources().getString(R.string.minute);
            }

            if (runtime.equals("0")) {
                runtime = "- " + fragmentContext.getResources().getString(R.string.minute);
            }


            String budget = numberFormatter(movieInfo.getInt("budget")) + " USD";
            String revenue = numberFormatter(movieInfo.getInt("revenue")) + " USD";
            String productionCompanies = jsonArrayToString(companies, JSON_COMPANIES);
            String productionCountries = jsonArrayToString(countries, "countries");
            ArrayList<Cast> castDetail = jsonArrayToCast(cast);
            Map<String, String> reviewDetail = jsonObjectToMap(reviews);
            ArrayList<Movie> similarDetail = jsonObjectToMovieArray(similar, JSON_SIMILAR);
            ArrayList<Movie> recommendationsDetail = jsonObjectToMovieArray(recommendations, JSON_RECOMMENDATIONS);
            ArrayList<String> imagesDetail = jsonObjectToImageArray(images);


            moviesDetail = new MovieDetails(id, name, genre, overview, release, Double.parseDouble(votes), posterImage, backdropImage,
                    voteCountDetail, runtime, budget, revenue, productionCompanies,
                    productionCountries, castDetail, reviewDetail, similarDetail, recommendationsDetail, imagesDetail);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return moviesDetail;
    }


    private String rawPosterPathProcess(String posterPath, @ImageWidth String size) {
        String s;
        try {
            s = IMAGE_BASE_URL + size + "/" + posterPath;
        } catch (ClassCastException e) {
            e.printStackTrace();
            s = "NO_IMAGE";
        }
        return s;
    }

    private String runtimeFormatter(String s) {
        int i = 0;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "- " + fragmentContext.getResources().getString(R.string.minute);
        }
        String time = "";
        if (i / 60 >= 1) {
            time = time + i / 60 + fragmentContext.getResources().getString(R.string.hour) + " ";
        }

        if (i % 60 > 0) {
            time = time + i % 60 + fragmentContext.getResources().getString(R.string.minute);
        }

        if (time.equals("")) {
            time = "- " + fragmentContext.getResources().getString(R.string.minute);
        }
        return time;
    }

    private String jsonArrayToString(JSONArray array, String s) {
        String result = "";
        JSONObject nameId;
        int count = 0;
        try {
            for (int i = 0; i < array.length(); i++) {
                nameId = array.getJSONObject(i);
                if (nameId.getString("name") != null) {
                    result += nameId.getString("name") + "   ";
                    ++count;
                }
            }

            if (s.equals(JSON_COMPANIES) && count == 1) {
                productionCompanies = fragmentContext.getResources().getString(R.string.production_company);
            }

            if (s.equals(JSON_COMPANIES) && count > 1) {
                productionCompanies = fragmentContext.getResources().getString(R.string.production_companies);
            }
            if (array.length() == 1 && s.equals("countries")) {
                productionCountries = fragmentContext.getResources().getString(R.string.production_country);
            } else {
                productionCountries = fragmentContext.getResources().getString(R.string.production_countries);
            }

        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    public String jsonArrayToString(JSONArray array) {
        String result = "";
        String language = Locale.getDefault().getDisplayLanguage().toLowerCase();
        try {
            for (int i = 0; i < array.length(); i++) {

                if ((int) array.get(i) == 878) {
                    result += fragmentContext.getResources().getString(R.string.sci_fi) + "\n";
                } else {
                    if (language.equals("magyar")) {
                        result += NetworkQuery.returnGenreNameHungarian((int) array.get(i)) + "\n";
                    } else {
                        result += NetworkQuery.returnGenreName((int) array.get(i)) + "\n";
                    }
                }
            }
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    private String jsonArrayToStringGenre(JSONArray array) {
        String result = "";
        JSONObject nameId;
        try {
            if (array.length() == 1 && movieGenresHeader != null) {
                movieGenresHeader.setText(fragmentContext.getResources().getString(R.string.genre));
            }
            for (int i = 0; i < array.length(); i++) {
                nameId = array.getJSONObject(i);
                result += nameId.getString("name") + "   ";
            }
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private String numberFormatter(int number) {
        DecimalFormat numFormat = new DecimalFormat("###,###,###");
        return numFormat.format(number);
    }

    private ArrayList<Cast> jsonArrayToCast(JSONArray array) {
        ArrayList<Cast> result = new ArrayList<>();
        JSONObject castDetail;
        String name = "";
        String character = "";
        String profilePath = "";
        int gender = 0;
        Object o;

        int j = array.length();
        for (int i = 0; i < j; i++) {
            try {
                castDetail = array.getJSONObject(i);

                if (castDetail != null) {
                    name = castDetail.getString("name");
                    character = castDetail.getString("character");
                    gender = castDetail.getInt("gender");
                    o = castDetail.get("profile_path");

                    if (o != null) {
                        profilePath = rawPosterPathProcess((String) castDetail.get("profile_path"), W185);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                profilePath = "NO_IMAGE";
            } finally {
                result.add(new Cast(name, character, gender, profilePath));
            }
        }

        return result;
    }


    private Map<String, String> jsonObjectToMap(JSONObject jobject) {
        Map<String, String> result = new HashMap<>();
        JSONObject partialResult;
        String key;
        String value;
        try {
            JSONArray content = jobject.getJSONArray("results");

            if (content != null) {

                if (content.length() == 1 && movieCommentsHeader != null) {
                    movieCommentsHeader.setText(fragmentContext.getResources().getString(R.string.comment));
                }

                for (int i = 0; i < content.length(); i++) {
                    partialResult = content.getJSONObject(i);
                    key = (String) partialResult.get("author");
                    value = (String) partialResult.get("content");
                    result.put(key, value);
                }
            }
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private ArrayList<Movie> jsonObjectToMovieArray(JSONObject jobject, String s) {
        ArrayList<Movie> result = new ArrayList<>();
        JSONObject partialResult;
        Object o;
        String genres;


        try {
            o = jobject.getJSONArray("results");
            if (o != null) {
                if (((JSONArray) o).length() == 1 && s.equals(JSON_RECOMMENDATIONS) && recommendationsHeader != null && isAdded()) {
                    recommendationsHeader.setText(fragmentContext.getResources().getString(R.string.recommendation));
                }
                if (((JSONArray) o).length() == 1 && s.equals(JSON_SIMILAR) && similarHeader != null) {
                    similarHeader.setText(fragmentContext.getResources().getString(R.string.similar_movie));
                }

                for (int i = 0; i < ((JSONArray) o).length(); i++) {
                    partialResult = ((JSONArray) o).getJSONObject(i);
                    genres = jsonArrayToString(partialResult.getJSONArray("genre_ids"));
                    try {
                        result.add(new Movie(partialResult.getInt("id"), partialResult.getString("title"),
                                genres, partialResult.getString("release_date"), partialResult.getDouble("vote_average"),
                                rawPosterPathProcess((String) partialResult.get("poster_path"), W342), 0, false));
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private ArrayList<String> jsonObjectToImageArray(JSONObject jobject) {
        ArrayList<String> result = new ArrayList<>();
        JSONObject partialResult;
        Object o;

        try {
            o = jobject.getJSONArray("backdrops");
            if (o != null) {
                for (int i = 0; i < ((JSONArray) o).length(); i++) {
                    partialResult = ((JSONArray) o).getJSONObject(i);

                    try {
                        result.add(rawPosterPathProcess((String) partialResult.get("file_path"), W92));
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String commentsConcatenate(Map<String, String> map) {
        String s = "";
        String threeNewLine = "\n\n\n";
        if (map != null && map.size() > 0) {
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            Map.Entry<String, String> pair = it.next();
            s += fragmentContext.getResources().getString(R.string.author) + pair.getKey() + threeNewLine + pair.getValue();
            while (it.hasNext()) {
                pair = it.next();
                s += threeNewLine + fragmentContext.getResources().getString(R.string.author) + pair.getKey() + threeNewLine + pair.getValue();
            }
            return s;
        } else {
            return "";
        }
    }

    private static String formatDate(String s) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        df.setLenient(false);

        Date date = new Date();
        try {
            date = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String string = date.toString();
        String[] array = string.split(" ");

        String language = Locale.getDefault().getDisplayLanguage().toLowerCase();
        if (language.equals("magyar")) {
            return array[5] + " " + array[1] + " " + array[2];
        } else {
            return array[1] + " " + array[2] + " " + array[5];
        }
    }

    public String[] headersToText() {
        String[] array = new String[5];
        array[0] = movieProductionDetails.getText().toString();
        array[1] = movieGenresHeader.getText().toString();
        array[2] = movieCommentsHeader.getText().toString();
        array[3] = recommendationsHeader.getText().toString();
        array[4] = similarHeader.getText().toString();

        return array;
    }

    public void extractHeadersFromArray() {
        String[] array = ((DetailActivity) getActivity()).getFragmentHeaders();
        movieProductionDetails.setText(array[0]);
        movieGenresHeader.setText(array[1]);
        movieCommentsHeader.setText(array[2]);
        recommendationsHeader.setText(array[3]);
        similarHeader.setText(array[4]);

    }

    private String calculateProductionDetails(MovieDetails data) {
        int count = 0;

        String productionDetails = "";
        if (!data.getReleaseDetail().equals("")) {
            productionDetails += fragmentContext.getResources().getString(R.string.release_date) + data.getReleaseDetail() + "\n\n";
            count += 1;
        }

        if (!data.getBudgetDetail().equals("0 USD")) {
            productionDetails += fragmentContext.getResources().getString(R.string.budget) + data.getBudgetDetail() + "\n\n";
            count += 1;
        }

        if (!data.getRevenueDetail().equals("0 USD")) {
            productionDetails += fragmentContext.getResources().getString(R.string.revenue) + data.getRevenueDetail() + "\n\n";
            count += 1;
        }

        if (!data.getProductionCompaniesDetail().equals("")) {
            productionDetails += productionCompanies + "     " + data.getProductionCompaniesDetail() + "\n\n";
            count += 1;
        }

        if (!data.getProductionCountriesDetail().equals("")) {
            productionDetails += productionCountries + "     " + data.getProductionCountriesDetail();
            count += 1;
        }

        if (count == 1) {
            productionDetailsHeader.setText(fragmentContext.getResources().getString(R.string.production_detail));
        }

        if (count == 0) {
            productionDetailsHeader.setVisibility(View.GONE);
            movieProductionDetails.setVisibility(View.GONE);
            productionDetailsView.setVisibility(View.GONE);
        }

        return productionDetails;
    }

    private void setupBackdropRecyclerView(MovieDetails data) {
        if (data.getBackdrops().size() > 1) {
            BackdropAdapter backdropAdapter = new BackdropAdapter(data.getBackdrops(), getActivity());
            backdropRecyclerView.setAdapter(backdropAdapter);
        } else {
            backdropView.setVisibility(View.GONE);
            backdropRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setupCastRecyclerView(MovieDetails data) {
        if (data.getCastDetail().size() != 0) {
            CastAdapter castAdapter = new CastAdapter(data.getCastDetail(), getActivity());
            castRecyclerView.setAdapter(castAdapter);
        } else {
            castHeader.setVisibility(View.GONE);
            castRecyclerView.setVisibility(View.GONE);
            castView.setVisibility(View.GONE);
        }
    }

    private void setupRecommendationRecyclerView(MovieDetails data) {
        if (data.getRecommendationsDetail().size() != 0) {
            RecommendationsAdapter recommendationsAdapter = new RecommendationsAdapter(data.getRecommendationsDetail(), getActivity());
            recommendationsRecyclerView.setAdapter(recommendationsAdapter);
        } else {
            recommendationsHeader.setVisibility(View.GONE);
            recommendationsRecyclerView.setVisibility(View.GONE);
            recommendationsView.setVisibility(View.GONE);
        }
    }

    private void setupSimilarRecyclerView(MovieDetails data) {
        if (data.getSimilarDetail().size() != 0) {
            SimilarAdapter similarAdapter = new SimilarAdapter(data.getSimilarDetail(), getActivity());
            similarRecyclerView.setAdapter(similarAdapter);

        } else {
            similarHeader.setVisibility(View.GONE);
            similarRecyclerView.setVisibility(View.GONE);
        }
    }

    private void loadBackdropImage(MovieDetails data, final int width) {
        backdropLoading.setVisibility(View.VISIBLE);
        Picasso.with(fragmentContext)
                .load(data.getBackdropDetail())
                .error(R.drawable.film500)
                .resize((int) (width * 0.95), (int) ((width * 0.95) * 0.562820512820513))
                .into(movieBackdrop, new Callback() {
                    @Override
                    public void onSuccess() {
                        backdropLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        backdropLoading.setVisibility(View.GONE);
                    }
                });
        movieLength.setText(data.getRuntime());
    }

    private void setupActionBar(MovieDetails data) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.action_bar_title_layout);
        }
        ((TextView) getActivity().findViewById(R.id.action_bar_title)).setText(data.getNameDetail());
    }

    private void setupDescriptionDetail(MovieDetails data) {
        if (!(data.getDescriptionDetail()).equals("") && !(data.getDescriptionDetail().equals("null"))) {
            movieDescription.setText(data.getDescriptionDetail());
        } else {
            movieDescriptionHeader.setVisibility(View.GONE);
            movieDescription.setVisibility(View.GONE);
            descriptionView.setVisibility(View.GONE);
        }
    }

    private void setupGenreDetail(MovieDetails data) {
        if (!(data.getGenreDetail()).equals("")) {
            movieGenres.setText(data.getGenreDetail());
        } else {
            movieGenresHeader.setVisibility(View.GONE);
            movieGenres.setVisibility(View.GONE);
            genresView.setVisibility(View.GONE);
        }
    }

    private void setupCommentsFragment(MovieDetails data) {

        final String comments = commentsConcatenate(data.getReviewsDetail());

        if (!comments.equals("")) {


            movieComments.setText(comments);
            movieComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putString(COMMENTS_TEXT, comments);
                    DialogFragment newFragment = new CommentsFragment();
                    newFragment.setCancelable(false);
                    newFragment.setArguments(args);
                    newFragment.show(getActivity().getSupportFragmentManager(), "comments");
                }
            });


        } else {
            movieCommentsHeader.setVisibility(View.GONE);
            movieComments.setVisibility(View.GONE);
            commentsView.setVisibility(View.GONE);
        }
    }

    private void setScrollPosition() {
        if (getActivity() instanceof DetailActivity && ((DetailActivity) getActivity()).getScrollPosition() != 0) {
            scrollView.post(new Runnable() {
                public void run() {
                    try {
                        scrollView.smoothScrollTo(0, ((DetailActivity) getActivity()).getScrollPosition());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        if (scrollView.getScrollY() != 0 && getActivity() instanceof DetailActivity) {
            ((DetailActivity) getActivity()).setScrollPosition(scrollView.getScrollY());
        }

        if (cachedData != null) {
            outState.putParcelable(CACHED_DATA, cachedData);
        }

        if (getActivity() instanceof DetailActivity) {
            ((DetailActivity) getActivity()).setFragmentHeaders(headersToText());
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentContext = context;
    }
}


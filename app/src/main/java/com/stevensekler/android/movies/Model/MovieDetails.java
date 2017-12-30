package com.stevensekler.android.movies.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Szekely Istvan on 5/24/2017.
 *
 */

public class MovieDetails implements Parcelable {
    private String movieIdDetail;
    private String nameDetail;
    private String genreDetail;
    private String descriptionDetail;
    private String releaseDetail;
    private double votesDetail;
    private String posterDetail;
    private String backdropDetail;
    private String voteCountDetail;
    private String runtime;
    private String budgetDetail;
    private String revenueDetail;
    private String productionCompaniesDetail;
    private String productionCountriesDetail;
    private ArrayList<Cast> castDetail;
    private Map<String, String> reviewsDetail;
    private ArrayList<Movie> similarDetail;
    private ArrayList<Movie> recommendationsDetail;
    private ArrayList<String> backdrops;

    public MovieDetails(String movieIdDetail, String nameDetail, String genreDetail, String descriptionDetail, String releaseDetail, double votesDetail, String posterDetail, String backdropDetail, String voteCountDetail, String runtime, String budgetDetail, String revenueDetail, String productionCompaniesDetail, String productionCountriesDetail, ArrayList<Cast> castDetail, Map<String, String> reviewsDetail, ArrayList<Movie> similarDetail, ArrayList<Movie> recommendationsDetail, ArrayList<String> backdrops) {
        this.movieIdDetail = movieIdDetail;
        this.nameDetail = nameDetail;
        this.genreDetail = genreDetail;
        this.descriptionDetail = descriptionDetail;
        this.releaseDetail = releaseDetail;
        this.votesDetail = votesDetail;
        this.posterDetail = posterDetail;
        this.backdropDetail = backdropDetail;
        this.voteCountDetail = voteCountDetail;
        this.runtime = runtime;
        this.budgetDetail = budgetDetail;
        this.revenueDetail = revenueDetail;
        this.productionCompaniesDetail = productionCompaniesDetail;
        this.productionCountriesDetail = productionCountriesDetail;
        this.castDetail = castDetail;
        this.reviewsDetail = reviewsDetail;
        this.similarDetail = similarDetail;
        this.recommendationsDetail = recommendationsDetail;
        this.backdrops = backdrops;
    }

    public MovieDetails() {
    }

    public String getNameDetail() {
        return nameDetail;
    }

    public String getGenreDetail() {
        return genreDetail;
    }

    public String getDescriptionDetail() {
        return descriptionDetail;
    }

    public String getReleaseDetail() {
        return releaseDetail;
    }

    public double getVotesDetail() {
        return votesDetail;
    }

    public String getBackdropDetail() {
        return backdropDetail;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getBudgetDetail() {
        return budgetDetail;
    }

    public String getRevenueDetail() {
        return revenueDetail;
    }

    public String getProductionCompaniesDetail() {
        return productionCompaniesDetail;
    }

    public String getProductionCountriesDetail() {
        return productionCountriesDetail;
    }

    public ArrayList<Cast> getCastDetail() {
        return castDetail;
    }

    public Map<String, String> getReviewsDetail() {
        return reviewsDetail;
    }

    public ArrayList<Movie> getSimilarDetail() {
        return similarDetail;
    }

    public ArrayList<Movie> getRecommendationsDetail() {
        return recommendationsDetail;
    }

    public ArrayList<String> getBackdrops() {
        return backdrops;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.movieIdDetail);
        dest.writeString(this.nameDetail);
        dest.writeString(this.genreDetail);
        dest.writeString(this.descriptionDetail);
        dest.writeString(this.releaseDetail);
        dest.writeDouble(this.votesDetail);
        dest.writeString(this.posterDetail);
        dest.writeString(this.backdropDetail);
        dest.writeString(this.voteCountDetail);
        dest.writeString(this.runtime);
        dest.writeString(this.budgetDetail);
        dest.writeString(this.revenueDetail);
        dest.writeString(this.productionCompaniesDetail);
        dest.writeString(this.productionCountriesDetail);
        dest.writeTypedList(this.castDetail);
        dest.writeInt(this.reviewsDetail.size());
        for (Map.Entry<String, String> entry : this.reviewsDetail.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeTypedList(this.similarDetail);
        dest.writeTypedList(this.recommendationsDetail);
        dest.writeStringList(this.backdrops);
    }

    protected MovieDetails(Parcel in) {
        this.movieIdDetail = in.readString();
        this.nameDetail = in.readString();
        this.genreDetail = in.readString();
        this.descriptionDetail = in.readString();
        this.releaseDetail = in.readString();
        this.votesDetail = in.readDouble();
        this.posterDetail = in.readString();
        this.backdropDetail = in.readString();
        this.voteCountDetail = in.readString();
        this.runtime = in.readString();
        this.budgetDetail = in.readString();
        this.revenueDetail = in.readString();
        this.productionCompaniesDetail = in.readString();
        this.productionCountriesDetail = in.readString();
        this.castDetail = in.createTypedArrayList(Cast.CREATOR);
        int reviewsDetailSize = in.readInt();
        this.reviewsDetail = new HashMap<>(reviewsDetailSize);
        for (int i = 0; i < reviewsDetailSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.reviewsDetail.put(key, value);
        }
        this.similarDetail = in.createTypedArrayList(Movie.CREATOR);
        this.recommendationsDetail = in.createTypedArrayList(Movie.CREATOR);
        this.backdrops = in.createStringArrayList();
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR = new Parcelable.Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel source) {
            return new MovieDetails(source);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };
}
package com.stevensekler.android.movies.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Szekely Istvan on 5/30/2017.
 *
 */

public class Cast implements Parcelable {
    private String name;
    private String character;
    private int gender;
    private String profileImage;

    public Cast(String name, String character, int gender, String profileImage) {
        this.name = name;
        this.character = character;
        this.gender = gender;
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public String getCharacter() {
        return character;
    }

    public int getGender() {
        return gender;
    }

    public String getProfileImage() {
        return profileImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.character);
        dest.writeInt(this.gender);
        dest.writeString(this.profileImage);
    }

    protected Cast(Parcel in) {
        this.name = in.readString();
        this.character = in.readString();
        this.gender = in.readInt();
        this.profileImage = in.readString();
    }

    public static final Parcelable.Creator<Cast> CREATOR = new Parcelable.Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel source) {
            return new Cast(source);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };
}

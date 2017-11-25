package com.infinitemessage.adewan.infinitemessage.model;

import com.google.gson.annotations.SerializedName;

/**
 * Class represents the model of the Author json object
 * that we get from the API call.
 *
 * Also defines getter and setter methods for the name
 * and photoURL.
 *
 * Created by a.dewan on 5/11/17.
 */

public class Author {
    @SerializedName("name")
    private String name;

    @SerializedName("photoUrl")
    private String photoUrl;

    public Author(String name, String photoUrl) {
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

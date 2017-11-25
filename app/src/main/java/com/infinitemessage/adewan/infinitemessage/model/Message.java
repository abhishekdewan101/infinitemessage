package com.infinitemessage.adewan.infinitemessage.model;

import com.google.gson.annotations.SerializedName;

/**
 * Class represents the model for the Message json object
 * that we get from the API. It defines the content, updated
 * id and author values for the message object.
 *
 * Also defines getter and setter methods for all.
 * Created by a.dewan on 5/11/17.
 */

public class Message {

    @SerializedName("content")
    String content;

    @SerializedName("updated")
    String updated;

    @SerializedName("id")
    int id;

    @SerializedName("author")
    Author author;

    public Message(String content, String updated, int id, Author author) {
        this.content = content;
        this.updated = updated;
        this.id = id;
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}

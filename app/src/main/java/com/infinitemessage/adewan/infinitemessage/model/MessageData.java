package com.infinitemessage.adewan.infinitemessage.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Class represents the model for the entire json object we
 * get from the api call. This encompasses the count of messages
 * , the pageToken for the next series of messages and all the data
 * for the messages we got back from the API call.
 * Created by a.dewan on 5/11/17.
 */

public class MessageData {

    @SerializedName("count")
    int count;

    @SerializedName("pageToken")
    String pageToken;

    @SerializedName("messages")
    ArrayList<Message> messages;

    public MessageData(int count, String pageToken, ArrayList<Message> messages) {
        this.count = count;
        this.pageToken = pageToken;
        this.messages = messages;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPageToken() {
        return pageToken;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

}

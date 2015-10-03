package com.codepath.apps.simpletwitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nikhil on 9/27/15.
 */
public class Tweet {
    private String body;
    private long uid;
    private User user;
    private String createdAt;

    public static Tweet fromJson(JSONObject tweetJson) {
        Tweet tweet = new Tweet();

        try {
            tweet.body = tweetJson.getString("text");
            tweet.uid = tweetJson.getLong("id");
            tweet.createdAt = tweetJson.getString("created_at");
            tweet.user = User.fromJson(tweetJson.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return tweet;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }


}

package com.codepath.apps.simpletwitterclient.models;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nikhil on 9/27/15.
 */

@Table(name = "tweets")
public class Tweet extends Model {

    @Column(name = "body")
    private String body;

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;

    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;

    @Column(name = "created_at")
    private String createdAt;

    public Tweet() {
        super();
    }

    public static Tweet fromJson(JSONObject tweetJson) {
        Tweet tweet = null;

        try {
            long uid = tweetJson.getLong("id");
            tweet = new Select()
                    .from(Tweet.class)
                    .where("uid = ?", uid)
                    .executeSingle();
            if (tweet != null) {
                return tweet;
            }

            tweet = new Tweet();

            tweet.body = tweetJson.getString("text");
            tweet.uid = tweetJson.getLong("id");
            tweet.createdAt = tweetJson.getString("created_at");
            tweet.user = User.fromJson(tweetJson.getJSONObject("user"));

            tweet.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJson(JSONArray tweetArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < tweetArray.length(); i++) {
                try {
                    JSONObject tweetObject = tweetArray.getJSONObject(i);
                    tweets.add(Tweet.fromJson(tweetObject));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

        return tweets;
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

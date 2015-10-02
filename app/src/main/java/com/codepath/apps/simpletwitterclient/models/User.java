package com.codepath.apps.simpletwitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nikhil on 10/1/15.
 */
public class User {

    private String name;
    private long uid;
    private String screenName;
    private String profileImageUrl;

    public static User fromJson(JSONObject jsonUser) {
        User u = new User();

        try {
            u.name = jsonUser.getString("name");
            u.uid = jsonUser.getLong("id");
            u.screenName = jsonUser.getString("screen_name");
            u.profileImageUrl = jsonUser.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}

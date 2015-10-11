package com.codepath.apps.simpletwitterclient.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.adapters.TweetsPagerAdapter;
import com.codepath.apps.simpletwitterclient.models.TwitterApplication;
import com.codepath.apps.simpletwitterclient.models.TwitterClient;
import com.codepath.apps.simpletwitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import fragments.ComposeTweetDialog;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialog.ComposeTweetDialogListener {

    private TwitterClient client;
    private User currentUser;
    private ViewPager vpPager;
    private PagerSlidingTabStrip tabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();

        vpPager = (ViewPager) findViewById(R.id.vpPager);
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabStrip);
        tabStrip.setViewPager(vpPager);

        if (isNetworkConnected()) {
            client.getCurrentUser(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    currentUser = User.fromJson(response);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TimelineActivity.this);
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putLong("user_id", currentUser.getUid());
                    edit.commit();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            long userId = preferences.getLong("user_id", -1);
            currentUser = new Select()
                    .from(User.class)
                    .where("uid = ?", userId)
                    .executeSingle();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline_menu, menu);
        MenuItem composeItem = menu.findItem(R.id.miComposeTweet);
        composeItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                composeTweet();
                return true;
            }
        });

        MenuItem profileItem = menu.findItem(R.id.miProfile);
        profileItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showProfile();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void composeTweet() {
        if (!isNetworkConnected()) {
            return;
        }

        FragmentManager manager = getSupportFragmentManager();
        ComposeTweetDialog dialog = ComposeTweetDialog.newInstance(getString(R.string.tweet_fragment_title), currentUser);
        dialog.show(manager, "fragment_compose_tweet");
    }

    private void showProfile() {
        if (!isNetworkConnected()) {
            return;
        }

        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("user", currentUser);

        startActivity(i);
    }

    @Override
    public void onFinishComposeDialog(String inputText) {
        // Don't post a tweet if the tweet is empty
        if (inputText.isEmpty()) {
            return;
        }

        if (!isNetworkConnected()) {
            return;
        }
        client.postTweet(inputText, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                fragmentTweets.fetchNewerTweets();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean networkConnection = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        if (!networkConnection) {
            showNoNetworkError();
        }
        return networkConnection;
    }

    private void showNoNetworkError() {
        Toast.makeText(this, "You don't have an internet connection", Toast.LENGTH_SHORT).show();
    }
}

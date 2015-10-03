package com.codepath.apps.simpletwitterclient;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialog.ComposeTweetDialogListener {

    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;

    private SwipeRefreshLayout swipeContainer;

    private User currentUser;

    private JsonHttpResponseHandler moreTweetsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        lvTweets = (ListView) findViewById(R.id.lvTweets);

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNewerTweets();
            }
        });

        client = TwitterApplication.getRestClient();
        setupMoreTweetsHandler();

        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                currentUser = User.fromJson(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemCount) {
                fetchOlderTweets();

                return true; // only if more loaded, else return false
            }
        });

        fetchOlderTweets();
    }

    private void setupMoreTweetsHandler() {
        moreTweetsHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> newTweets = Tweet.fromJson(response);
                tweets.addAll(newTweets);
                Collections.sort(tweets, new Comparator<Tweet>() {
                    @Override
                    public int compare(Tweet tweet, Tweet t1) {
                        long longComparator = t1.getUid() - tweet.getUid();
                        if (longComparator == 0) {
                            return 0;
                        } else if (longComparator > 0) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
                aTweets.notifyDataSetChanged();

                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                swipeContainer.setRefreshing(false);
            }
        };
    }

    private void fetchOlderTweets() {
        long oldestTweetUid = Long.MAX_VALUE;
        if (!tweets.isEmpty()) {
            oldestTweetUid = tweets.get(tweets.size() - 1).getUid();
        }

        client.getHomeTimelineBefore(oldestTweetUid, moreTweetsHandler);
    }

    private void fetchNewerTweets() {
        long newestTweetUid = 0;
        if (!tweets.isEmpty()) {
            newestTweetUid = tweets.get(0).getUid();
        }

        client.getHomeTimelineSince(newestTweetUid, moreTweetsHandler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline_menu, menu);
        MenuItem composeItem = menu.findItem(R.id.tweet_compose);
        composeItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                composeTweet();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void composeTweet() {
        FragmentManager manager = getSupportFragmentManager();
        ComposeTweetDialog dialog = ComposeTweetDialog.newInstance(getString(R.string.tweet_fragment_title), currentUser);
        dialog.show(manager, "fragment_compose_tweet");
    }

    @Override
    public void onFinishComposeDialog(String inputText) {
        Toast.makeText(this, inputText, Toast.LENGTH_SHORT).show();
        client.postTweet(inputText, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                fetchNewerTweets();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}

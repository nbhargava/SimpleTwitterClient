package fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.simpletwitterclient.EndlessScrollListener;
import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.TweetList;
import com.codepath.apps.simpletwitterclient.models.TwitterApplication;
import com.codepath.apps.simpletwitterclient.models.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by nikhil on 10/4/15.
 */
public abstract class TweetsListFragment extends Fragment {

    private JsonHttpResponseHandler moreTweetsHandler;
    private SwipeRefreshLayout swipeContainer;
    private ListView lvTweets;

    private TweetsArrayAdapter aTweets;
    private TweetList tweets;

    protected TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new TweetList();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);

        client = TwitterApplication.getRestClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemCount) {
                return fetchOlderTweets();
            }
        });

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNewerTweets();
            }
        });

        setupMoreTweetsHandler();

        boolean isFetching = fetchOlderTweets();
        if (!isFetching) {
            fetchTweetsFromDB();
        }

        return v;
    }

    public void addAll(List<Tweet> newTweets) {
        aTweets.addAll(newTweets);
    }

    public void clearTweets() {
        aTweets.clear();
    }

    protected boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean networkConnection = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        if (!networkConnection) {
            showNoNetworkError();
        }
        return networkConnection;
    }

    private void showNoNetworkError() {
        Toast.makeText(getActivity(), "You don't have an internet connection", Toast.LENGTH_SHORT).show();
    }

    private void fetchTweetsFromDB() {
        clearTweets();
        List<Tweet> storedTweets = new Select().from(Tweet.class)
                .orderBy("uid DESC")
                .execute();
        addAll(storedTweets);
    }

    private boolean fetchOlderTweets() {
        long oldestTweetUid = Long.MAX_VALUE;
        if (!tweets.isEmpty()) {
            oldestTweetUid = tweets.get(tweets.size() - 1).getUid();
        }

        if (isNetworkConnected()) {
            getTweetsBefore(oldestTweetUid, moreTweetsHandler);
            return true;
        } else {
            return false;
        }
    }

    public boolean fetchNewerTweets() {
        long newestTweetUid = 0;
        if (!tweets.isEmpty()) {
            newestTweetUid = tweets.get(0).getUid();
        }

        if (isNetworkConnected()) {
            getTweetsSince(newestTweetUid, moreTweetsHandler);
            return true;
        } else {
            swipeContainer.setRefreshing(false);
            return false;
        }
    }

    private void setupMoreTweetsHandler() {
        moreTweetsHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addAll(Tweet.fromJson(response));
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                swipeContainer.setRefreshing(false);
            }
        };
    }

    protected abstract void getTweetsBefore(long oldestUid, JsonHttpResponseHandler handler);
    protected abstract void getTweetsSince(long newestUid, JsonHttpResponseHandler handler);
}

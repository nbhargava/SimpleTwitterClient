package fragments;

import android.os.Bundle;

import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by nikhil on 10/9/15.
 */
public class UserTimelineFragment extends TweetsListFragment {

    private String screenName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenName = getArguments().getString("screen_name");
    }

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void getTweetsBefore(long oldestUid, JsonHttpResponseHandler handler) {
        client.getUserTimelineBefore(screenName, oldestUid, handler);
    }

    @Override
    protected void getTweetsSince(long newestUid, JsonHttpResponseHandler handler) {
        client.getUserTimelineSince(screenName, newestUid, handler);
    }
}

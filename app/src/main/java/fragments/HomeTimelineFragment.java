package fragments;

import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by nikhil on 10/4/15.
 */
public class HomeTimelineFragment extends TweetsListFragment {

    @Override
    protected void getTweetsBefore(long oldestUid, JsonHttpResponseHandler handler) {
        client.getHomeTimelineBefore(oldestUid, handler);
    }

    @Override
    protected void getTweetsSince(long newestUid, JsonHttpResponseHandler handler) {
        client.getHomeTimelineSince(newestUid, handler);
    }
}

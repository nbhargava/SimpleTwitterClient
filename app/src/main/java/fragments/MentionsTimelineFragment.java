package fragments;

import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by nikhil on 10/9/15.
 */
public class MentionsTimelineFragment extends TweetsListFragment {

    @Override
    protected void getTweetsBefore(long oldestUid, JsonHttpResponseHandler handler) {
        client.getMentionsTimelineBefore(oldestUid, handler);
    }

    @Override
    protected void getTweetsSince(long newestUid, JsonHttpResponseHandler handler) {
        client.getMentionsTimelineSince(newestUid, handler);
    }
}

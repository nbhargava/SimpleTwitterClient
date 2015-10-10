package com.codepath.apps.simpletwitterclient.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by nikhil on 10/4/15.
 *
 * Sorted ArrayList of Tweets organized by id, descending
 */
public class TweetList extends ArrayList<Tweet> {
    @Override
    public boolean addAll(Collection<? extends Tweet> collection) {
        boolean result = super.addAll(collection);
        Collections.sort(this, new Comparator<Tweet>() {
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

        return result;
    }

    public long getOldestTweetUid() {
        long oldestTweetUid = Long.MAX_VALUE;
        if (!isEmpty()) {
            oldestTweetUid = get(size() - 1).getUid();
        }

        return oldestTweetUid;
    }

    public long getNewestTweetUid() {
        long newestTweetUid = 0;
        if (!isEmpty()) {
            newestTweetUid = get(0).getUid();
        }

        return newestTweetUid;
    }
}

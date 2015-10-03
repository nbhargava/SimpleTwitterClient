package com.codepath.apps.simpletwitterclient;

import android.widget.AbsListView;

/**
 * Created by nikhil on 9/25/15.
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private int startingPageIndex = 0;

    public EndlessScrollListener() {

    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        if (loading && totalItemCount > this.previousTotalItemCount) {
            loading = false;
            this.previousTotalItemCount = totalItemCount;
            this.currentPage++;
        }

        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + this.visibleThreshold) {
            loading = onLoadMore(currentPage + 1, totalItemCount);
        }
    }

    public abstract boolean onLoadMore(int page, int totalItemCount);

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        // do nothing
    }
}

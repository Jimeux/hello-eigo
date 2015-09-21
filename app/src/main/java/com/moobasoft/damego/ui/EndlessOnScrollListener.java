package com.moobasoft.damego.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Adapted from this gist: https://gist.github.com/ssinss/e06f12ef66c51252563e
 */
public abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {

    // The minimum amount of items to have below your current scroll position before loading more.
    private static final int VISIBLE_THRESHOLD = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // The total number of items in the dataset after the last load
    private int previousTotal = 0;
    // The current page
    private int currentPage = 1;

    private final LinearLayoutManager layoutManager;
    private final SwipeRefreshLayout refreshLayout;

    public EndlessOnScrollListener(LinearLayoutManager layoutManager,
                                   SwipeRefreshLayout refreshLayout) {
        this.layoutManager = layoutManager;
        this.refreshLayout = refreshLayout;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (refreshLayout.isRefreshing()) return;

        super.onScrolled(recyclerView, dx, dy);

        refreshLayout.setEnabled(
                layoutManager.findFirstCompletelyVisibleItemPosition() == 0);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount   = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (loading && totalItemCount > previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
        }

        boolean endReached = (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + VISIBLE_THRESHOLD);

        if (!loading && endReached) {
            currentPage++;
            loading = true;
            onLoadMore(currentPage);
        }
    }

    public void reset() {
        previousTotal = 0;
        currentPage = 1;
    }

    public void restorePage(int currentPage, int previousTotal) {
        this.currentPage   = currentPage;
        this.previousTotal = previousTotal;
    }

    public int getCurrentPage()   { return currentPage; }
    public int getPreviousTotal() { return previousTotal; }

    public abstract void onLoadMore(int current_page);
}
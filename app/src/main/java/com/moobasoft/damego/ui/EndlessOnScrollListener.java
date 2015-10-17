package com.moobasoft.damego.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.parceler.Parcel;

/**
 * Adapted from this gist: https://gist.github.com/ssinss/e06f12ef66c51252563e
 */
public abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {

    // The minimum amount of items to have below your current scroll position before loading more.
    static final int VISIBLE_THRESHOLD = 1;
    // True if we are still waiting for the last set of data to load.
    boolean loading = true;
    // The total number of items in the dataset after the last load
    int previousTotal = 0;
    // The current page
    int currentPage = 1;
    // True if setFinished() has been called
    boolean isFinished = false;

    private final LinearLayoutManager layoutManager;

    public EndlessOnScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (isRefreshing() || isFinished) return;

        super.onScrolled(recyclerView, dx, dy);

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

    /**
     * Reset to first page. Use in onRefresh()
     */
    public void reset() {
        previousTotal = 0;
        currentPage = 1;
        isFinished = false;
        loading = false;
    }

    public void setFinished() {
        isFinished = true;
    }

    /**
     * Restore state from a previous instance of EndlessOnScrollListener.
     * Use in onRestoreInstanceState()
     */
    public void restorePage(ScrollOutState state) {
        this.currentPage   = state.currentPage;
        this.previousTotal = state.previousTotal;
        this.isFinished    = state.isFinished;
        this.loading       = false;
    }

    public ScrollOutState getOutState() {
        return new ScrollOutState(currentPage, previousTotal, isFinished);
    }

    public int getCurrentPage()   { return currentPage; }

    /**
     * Allow client to define behaviour when next page is loaded.
     */
    public abstract void onLoadMore(int currentPage);

    public abstract boolean isRefreshing();

    public boolean isFinished() {
        return isFinished;
    }

    @Parcel
    public static class ScrollOutState {
        public int currentPage;
        public int previousTotal;
        public boolean isFinished;
        public boolean loading;

        public ScrollOutState() {}

        public ScrollOutState(int currentPage, int previousTotal, boolean isFinished) {
            this.currentPage = currentPage;
            this.previousTotal = previousTotal;
            this.isFinished = isFinished;
        }
    }
}
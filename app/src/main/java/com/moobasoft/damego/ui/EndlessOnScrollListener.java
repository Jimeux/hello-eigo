package com.moobasoft.damego.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.parceler.Parcel;

/**
 * Adapted from this gist: https://gist.github.com/ssinss/e06f12ef66c51252563e
 */
public abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {

    // The minimum amount of items to have below your current scroll position before loading more.
    private static final int VISIBLE_THRESHOLD = 1;
    // The total number of items in the dataset after the last load
    private int previousTotal = 0;
    // The current page
    private int currentPage = 1;
    // True if setFinished() has been called
    private boolean isFinished = false;

    private LinearLayoutManager layoutManager;

    /** Getters */
    public int getCurrentPage() { return currentPage; }
    public boolean isFinished() { return isFinished; }

    /**
     * Needs to be set manually because of fragment lifecycle issues
     */
    public void setLayoutManager(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (layoutManager == null || isRefreshing() || isFinished) return;

        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount   = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (firstVisibleItem < 0) return;

        if (totalItemCount > previousTotal)
            previousTotal = totalItemCount;

        boolean endReached = (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + VISIBLE_THRESHOLD);

        if (endReached) {
            currentPage++;
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
    }

    /**
     * Set flag to show that no more data should be loaded
     */
    public void setFinished() {
        isFinished = true;
    }

    /**
     * Restore state from a previous instance.
     * Use in onRestoreInstanceState()
     */
    public void restoreState(ScrollOutState state) {
        this.currentPage   = state.currentPage;
        this.previousTotal = state.previousTotal;
        this.isFinished    = state.isFinished;
    }

    /**
     * The necessary state to restore this instance.
     * Use in onSaveInstanceState()
     */
    public ScrollOutState getOutState() {
        return new ScrollOutState(currentPage, previousTotal, isFinished);
    }

    /**
     * Allow client to define behaviour when next page is loaded.
     */
    public abstract void onLoadMore(int currentPage);

    /**
     * Is previous request still loading?
     */
    public abstract boolean isRefreshing();

    @Parcel
    public static class ScrollOutState {
        public int currentPage;
        public int previousTotal;
        public boolean isFinished;

        public ScrollOutState() {}

        public ScrollOutState(int currentPage, int previousTotal, boolean isFinished) {
            this.currentPage = currentPage;
            this.previousTotal = previousTotal;
            this.isFinished = isFinished;
        }
    }
}
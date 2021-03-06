package com.moobasoft.helloeigo.ui;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.helloeigo.R;
import com.moobasoft.helloeigo.rest.models.Post;
import com.moobasoft.helloeigo.ui.views.PostSummaryView;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.ViewHolder;

public class PostsAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final int ID_PREFIX     = 12345678;
    public static final int TYPE_FEATURED = 0;
    public static final int TYPE_NORMAL   = 1;
    public static final int TYPE_FOOTER   = 2;
    private final String tagName;
    private boolean hideFooter = true;

    private OnPostClickListener summaryClickListener;
    private ArrayList<Post> postList;
    private final int columns;

    public PostsAdapter(OnPostClickListener summaryClickListener, int columns, String tagName) {
        this.summaryClickListener = summaryClickListener;
        this.postList = new ArrayList<>();
        this.columns = columns;
        this.tagName = tagName;
    }

    public ArrayList<Post> getPostList() {
        return postList;
    }

    public void loadPosts(List<Post> posts) {
        postList.addAll(posts);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.element_list_footer, parent, false);
            return new FooterHolder(view);
        } else {
            int layout = (viewType == TYPE_FEATURED) ?
                    R.layout.view_post_featured :
                    R.layout.view_post_normal;

            PostSummaryView view = (PostSummaryView) LayoutInflater
                    .from(parent.getContext())
                    .inflate(layout, parent, false);
            return new SummaryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof SummaryViewHolder )
            ((SummaryViewHolder)holder)
                    .bindTo(postList.get(position), getItemViewType(position), summaryClickListener, tagName);
        else if (holder instanceof FooterHolder) {
            ((StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams())
                    .setFullSpan(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == postList.size())
            return TYPE_FOOTER;
        else if (columns == 3 && (position == 0 || position == 2 || position == 3 ||
                (position >= 7  && (position - 7)  % 6 == 0) ||
                (position >= 9  && (position - 9)  % 6 == 0) ||
                (position >= 10 && (position - 10) % 6 == 0)))
            return TYPE_FEATURED;
        else if (columns == 2 && (position % 10 == 0 || (position >= 6 && (position + 4) % 10 == 0)))
            return TYPE_FEATURED;
        else if (columns == 1 && (position % 10 == 0))
            return TYPE_FEATURED;
        else
            return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        int size = postList.size();
        return hideFooter ? size : size + 1;
    }

    public void clear() {
        postList.clear();
        notifyDataSetChanged();
    }

    public void setFinished() {
        hideFooter = false;
        notifyDataSetChanged();
    }

    public boolean isEmpty() { return postList.isEmpty(); }

    public interface OnPostClickListener {
        void onSummaryClicked(Post post, boolean openComments, String tagName);
        void onTagClicked(String tag);
    }

    static class SummaryViewHolder extends ViewHolder {

        private final PostSummaryView itemView;

        public SummaryViewHolder(PostSummaryView itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void bindTo(Post post, int itemViewType, OnPostClickListener listener, String tagName) {
            itemView.setId(ID_PREFIX + post.getId());
            itemView.bindTo(post, itemViewType, listener, tagName);
        }
    }

    static class FooterHolder extends ViewHolder {
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }
}
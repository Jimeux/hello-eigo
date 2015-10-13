package com.moobasoft.damego.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.views.PostSummaryView;

import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

public class PostsAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final int ID_PREFIX = 12345678;
    public static final int TYPE_FEATURED = 0;
    public static final int TYPE_NORMAL   = 1;
    public static final int TYPE_FOOTER   = 2;
    private final boolean showFeatures;

    private PostClickListener summaryClickListener;
    private List<Post> postList;
    private final int columns;

    public PostsAdapter(PostClickListener summaryClickListener, List<Post> posts, int columns, boolean showFeatures) {
        this.summaryClickListener = summaryClickListener;
        this.postList = posts;
        this.columns = columns;
        this.showFeatures = showFeatures;
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
                    .bindTo(postList.get(position), getItemViewType(position), summaryClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == postList.size())
            return TYPE_FOOTER;
        else if (showFeatures && (position % 10 == 0 || (columns == 2 && (position-1) % 10 == 0)))
            return TYPE_FEATURED;
        else
            return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return postList.size() + 1; // Add 1 for the footer
    }

    public void clear() {
        postList.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() { return postList.isEmpty(); }

    public interface PostClickListener {
        void onSummaryClicked(Post post);
        void onTagClicked(String tag);
    }

    class SummaryViewHolder extends ViewHolder {

        private final PostSummaryView itemView;

        public SummaryViewHolder(PostSummaryView itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void bindTo(Post post, int itemViewType, PostClickListener postClickListener) {
            itemView.setId(ID_PREFIX + post.getId());
            itemView.bindTo(post, itemViewType, postClickListener);
        }
    }

    static class FooterHolder extends ViewHolder {
        //private final View view;

        public FooterHolder(View itemView) {
            super(itemView);
            //this.view = itemView;
        }
    }
}
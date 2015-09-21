package com.moobasoft.damego.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.views.PostSummaryView;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.SummaryViewHolder> {

    public static final int ID_PREFIX = 12345678;
    public static final int TYPE_FEATURED = 0;
    public static final int TYPE_NORMAL   = 1;

    private OnSummaryClickListener summaryClickListener;
    private List<Post> postList;

    public PostsAdapter(OnSummaryClickListener summaryClickListener, List<Post> posts) {
        this.summaryClickListener = summaryClickListener;
        this.postList = posts;
    }

    public void loadPosts(List<Post> posts) {
        postList.addAll(posts);
        notifyDataSetChanged();
    }

    @Override
    public SummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.view_post_normal;

        if (viewType == TYPE_FEATURED) layout = R.layout.view_post_featured;
        PostSummaryView view = (PostSummaryView) LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new SummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SummaryViewHolder holder, int position) {
        holder.bindTo(postList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 6 == 0) ? TYPE_FEATURED : TYPE_NORMAL;
    }

    @Override
    public int getItemCount() { return postList.size(); }

    public void clear() {
        postList.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() { return postList.isEmpty(); }

    class SummaryViewHolder extends RecyclerView.ViewHolder {

        private final PostSummaryView itemView;

        public SummaryViewHolder(PostSummaryView itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void bindTo(Post post) {
            itemView.setId(ID_PREFIX + post.getId());
            itemView.bindTo(post);
            itemView.setOnClickListener(
                    v -> summaryClickListener.onSummaryClicked(post));
        }
    }

    public interface OnSummaryClickListener {
        void onSummaryClicked(Post post);
    }
}
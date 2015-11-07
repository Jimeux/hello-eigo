package com.moobasoft.helloeigo.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.moobasoft.helloeigo.R;
import com.moobasoft.helloeigo.rest.models.Comment;
import com.moobasoft.helloeigo.ui.views.CommentView;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    public static final int ID_PREFIX = 12345678;
    private List<Comment> commentList;

    public CommentsAdapter() {
        this.commentList = new ArrayList<>();
    }

    public void loadComments(List<Comment> comments) {
        commentList.addAll(comments);
        notifyDataSetChanged();
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CommentView view = (CommentView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        holder.bindTo(commentList.get(position));
    }

    @Override
    public int getItemCount() { return commentList.size(); }

    public void clear() {
        commentList.clear();
        notifyDataSetChanged();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        private final CommentView itemView;

        public CommentViewHolder(CommentView itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void bindTo(Comment comment) {
            itemView.setId(ID_PREFIX + comment.getId());
            itemView.bindTo(comment);
        }
    }

}
package com.moobasoft.helloeigo.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moobasoft.helloeigo.R;
import com.moobasoft.helloeigo.rest.models.Comment;
import com.moobasoft.helloeigo.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class CommentView extends LinearLayout {
    @Bind(R.id.username)       TextView username;
    @Bind(R.id.body)           TextView body;
    @Bind(R.id.created_at)     TextView createdAt;

    public CommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindTo(Comment comment) {
        username.setText(comment.getUser().getUsername());
        body.setText(comment.getBody());
        createdAt.setText(Util.formatShortDate(comment.getCreatedAt()));
    }

}
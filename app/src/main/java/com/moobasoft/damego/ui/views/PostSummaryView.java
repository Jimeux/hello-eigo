package com.moobasoft.damego.ui.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.moobasoft.damego.ui.PostsAdapter.OnPostClickListener;
import static com.moobasoft.damego.ui.PostsAdapter.TYPE_FEATURED;

public final class PostSummaryView extends LinearLayout {
    @Bind(R.id.image)          ImageView image;
    @Bind(R.id.title)          TextView title;
    @Bind(R.id.body)           TextView body;
    @Bind(R.id.comments_count) TextView commentsCount;
    @Bind(R.id.comment_strip)  ViewGroup commentStrip;
    @Bind(R.id.tags)           ViewGroup tags;

    public PostSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindTo(Post post, int itemViewType, OnPostClickListener onPostClickListener, String tagName) {
        title.setText(post.getTitle());
        body.setText(Html.fromHtml(post.getBody()));
        commentsCount.setText(String.valueOf(post.getCommentsCount()));
        commentStrip.setOnClickListener(v -> onPostClickListener.onSummaryClicked(post, true, tagName));
        setOnClickListener(v -> onPostClickListener.onSummaryClicked(post, false, tagName));

        loadImage(post, itemViewType);
        loadTags(post, onPostClickListener);
    }

    private void loadImage(Post post, int itemViewType) {
        String imageUrl = (itemViewType == TYPE_FEATURED) ?
                post.getImageUrl() : post.getThumbnailUrl();
        Glide.with(getContext())
                .load(imageUrl)
                .into(image);
    }

    private void loadTags(Post post, OnPostClickListener onPostClickListener) {
        LayoutInflater inflater = (LayoutInflater) tags.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Util.insertTags(post, inflater, tags, false);

        for (int i = 0; i < tags.getChildCount(); i++) {
            TextView tag = (TextView) tags.getChildAt(i);
            tag.setOnClickListener(v -> onPostClickListener.onTagClicked(tag.getText().toString()));
        }
    }

}
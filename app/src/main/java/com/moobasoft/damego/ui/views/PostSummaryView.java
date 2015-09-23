package com.moobasoft.damego.ui.views;

import android.content.Context;
import android.content.Intent;
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
import com.moobasoft.damego.ui.activities.CommentsActivity;
import com.moobasoft.damego.ui.activities.ShowActivity;
import com.moobasoft.damego.util.PostUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.moobasoft.damego.ui.PostsAdapter.PostClickListener;
import static com.moobasoft.damego.ui.PostsAdapter.TYPE_FEATURED;

public final class PostSummaryView extends LinearLayout {
    @Bind(R.id.image)          ImageView image;
    @Bind(R.id.title)          TextView title;
    @Bind(R.id.body)           TextView body;
    @Bind(R.id.comments_count) TextView commentsCount;
    @Bind(R.id.comment_strip)  ViewGroup commentStrip;
    @Bind(R.id.tags)           ViewGroup tags;

    private int postId = -1;

    public PostSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindTo(Post post, int itemViewType, PostClickListener postClickListener) {
        postId = post.getId();
        title.setText(post.getTitle());
        body.setText(Html.fromHtml(post.getBody()));
        commentsCount.setText(String.valueOf(post.getCommentsCount()));

        loadImage(post, itemViewType);
        LayoutInflater inflater = (LayoutInflater) tags.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PostUtil.insertTags(post, inflater, tags, false);

        setOnClickListener(v -> postClickListener.onSummaryClicked(post));
        for (int i = 0; i < tags.getChildCount(); i++) {
            TextView tag = (TextView) tags.getChildAt(i);
            tag.setOnClickListener(v -> postClickListener.onTagClicked(tag.getText().toString()));
        }
    }

    private void loadImage(Post post, int itemViewType) {
        String imageUrl = (itemViewType == TYPE_FEATURED) ?
                post.getImageUrl() : post.getThumbnailUrl();
        Glide.with(getContext())
                .load(imageUrl)
                .into(image);
    }

    @OnClick(R.id.comment_strip)
    public void clickCommentStrip() {
        Intent i = new Intent(getContext(), CommentsActivity.class);
        i.putExtra(ShowActivity.POST_ID, postId);
        getContext().startActivity(i);
    }

}
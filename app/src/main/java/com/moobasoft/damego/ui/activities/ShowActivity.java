package com.moobasoft.damego.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moobasoft.damego.App;
import com.moobasoft.damego.R;
import com.moobasoft.damego.di.components.DaggerMainComponent;
import com.moobasoft.damego.di.modules.MainModule;
import com.moobasoft.damego.rest.models.Comment;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.presenters.ShowPresenter;
import com.moobasoft.damego.ui.views.CommentView;
import com.moobasoft.damego.util.PostUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowActivity extends BaseActivity implements ShowPresenter.ShowView {

    public static final String POST_ID = "post_id";
    private int postId;

    @Inject ShowPresenter presenter;

    @Bind(R.id.title)             TextView title;
    @Bind(R.id.body)              TextView body;
    @Bind(R.id.tags)              ViewGroup tags;
    @Bind(R.id.backdrop)          ImageView backdrop;
    @Bind(R.id.comment_title)     TextView commentTitle;
    @Bind(R.id.comments_preview)  ViewGroup commentContainer;
    @Bind(R.id.view_comments_btn) ViewGroup viewCommentsBtn;
    @Bind({R.id.loading_view, R.id.error_view, R.id.empty_view, R.id.content})
    List<ViewGroup> stateViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialiseInjector();
        presenter.bindView(this);

        activateView(R.id.loading_view);

        postId = getIntent().getIntExtra(POST_ID, -1);
        if (postId == -1) onPostError();
        else presenter.getPost(postId);
    }

    @Override
    protected void onDestroy() {
        presenter.releaseView();
        super.onDestroy();
    }

    private void activateView(int id) {
        for (ViewGroup vg : stateViews) vg.setVisibility(View.GONE);
        findViewById(id).setVisibility(View.VISIBLE);
    }

    private void initialiseInjector() {
        DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App)getApplication()).getAppComponent())
                .build().inject(this);
    }

    @Override
    public void onPostRetrieved(Post post) {
        activateView(R.id.content);
        title.setText(post.getTitle());
        commentTitle.setText(getString(
                R.string.comments_title, post.getCommentsCount()));
        Glide.with(this)
                .load(post.getImageUrl())
                .into(backdrop);

        body.setText(Html.fromHtml(post.getBody().trim().replaceAll("[\n\r]", "")));
        PostUtil.insertTags(post, getLayoutInflater(), tags, true);
        insertComments(post);
    }

    private void insertComments(Post post) {
        final int end = (post.getComments().size() >= 3) ? 3 : post.getComments().size();
        final List<Comment> comments = post.getComments().subList(0, end);
        for (Comment comment : comments) {
            CommentView view = (CommentView)
                    getLayoutInflater().inflate(R.layout.view_comment, null);
            view.bindTo(comment);
            commentContainer.addView(view);
        }

        viewCommentsBtn.setVisibility(
                comments.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPostError() {
        activateView(R.id.error_view);
        Snackbar.make(title, "Error getting post.", Snackbar.LENGTH_LONG).show();
    }

    @OnClick({R.id.comment_title, R.id.view_comments_btn})
    public void clickViewComments() {
        Intent i = new Intent(ShowActivity.this, CommentsActivity.class);
        i.putExtra(POST_ID, postId);
        startActivity(i);
    }

    @OnClick(R.id.fab)
    public void clickFab() {
        Intent intent = new Intent(ShowActivity.this, CreateCommentActivity.class);
        intent.putExtra(ShowActivity.POST_ID, postId);
        doIfLoggedIn(intent);
    }

}
package com.moobasoft.damego.ui.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Comment;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.activities.BaseActivity;
import com.moobasoft.damego.ui.activities.CommentsActivity;
import com.moobasoft.damego.ui.activities.CreateCommentActivity;
import com.moobasoft.damego.ui.presenters.ShowPresenter;
import com.moobasoft.damego.ui.views.CommentView;
import com.moobasoft.damego.util.PostUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.VISIBLE;

public class ShowFragment extends BaseFragment implements ShowPresenter.ShowView {

    public static final String POST_ID_KEY = "post_id";
    private int postId;

    @Inject ShowPresenter presenter;

    @Nullable @Bind(R.id.app_bar) AppBarLayout appBarLayout;
    @Bind(R.id.title)             TextView title;
    @Bind(R.id.body)              TextView body;
    @Bind(R.id.tags)              ViewGroup tags;
    @Bind(R.id.backdrop)          ImageView backdrop;
    @Bind(R.id.comment_title)     TextView commentTitle;
    @Bind(R.id.comments_preview)  ViewGroup commentContainer;
    @Bind(R.id.view_comments_btn) ViewGroup viewCommentsBtn;
    private Post post;

    public ShowFragment() {}

    public static ShowFragment newInstance(int postId) {
        ShowFragment fragment = new ShowFragment();
        Bundle args = new Bundle();
        args.putInt(POST_ID_KEY, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        presenter.bindView(this);
        postId = getArguments().getInt(POST_ID_KEY, 0);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        ButterKnife.bind(this, view);
        if (appBarLayout != null && toolbar != null) {
            appBarLayout.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null && postId == 0) //TODO: Restore instance state
            postId = savedInstanceState.getInt(POST_ID_KEY);
        if (post == null) onRefresh();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(POST_ID_KEY, postId);
    }

    @Override
    public void onDestroyView() {
        presenter.releaseView();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        setAppBarExpanded(false);
        activateLoadingView();
        if (postId == -1) onError("No post ID given!");
        else presenter.getPost(postId);
    }

    @Override
    public void onPostRetrieved(Post post) {
        this.post = post;
        loadPost(post);
    }

    private void loadPost(Post post) {
        postId = post.getId();
        title.setText(post.getTitle());
        commentTitle.setText(getString(
                R.string.comments_title, post.getCommentsCount()));
        Glide.with(this)
                .load(post.getImageUrl())
                .into(backdrop);

        body.setText(Html.fromHtml(post.getBody().trim().replaceAll("[\n\r]", "")));
        PostUtil.insertTags(post, getActivity().getLayoutInflater(), tags, true);
        insertComments(post);

        activateContentView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (toolbar != null) toggleVisibility(toolbar);
            toggleVisibility(title, body, tags);
            TransitionManager.beginDelayedTransition((ViewGroup) getView().getRootView(), new Slide());
            toggleVisibility(title, body, tags);
            if (toolbar != null) toggleVisibility(toolbar);
        }
        setAppBarExpanded(true);
    }

    @Override
    public void setToolbar() {
        if (toolbar != null && appBarLayout != null) {
            toolbar.setTitle("");
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setAppBarExpanded(boolean expanded) {
        if (appBarLayout != null && toolbar != null) {
            appBarLayout.setExpanded(expanded, false);
            appBarLayout.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            //if (expanded  && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                //getActivity().getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private void insertComments(Post post) {
        final int end = (post.getComments().size() >= 3) ? 3 : post.getComments().size();
        final List<Comment> comments = post.getComments().subList(0, end);
        for (Comment comment : comments) {
            CommentView view = (CommentView)
                    getActivity().getLayoutInflater().inflate(R.layout.view_comment, null);
            view.bindTo(comment);
            commentContainer.addView(view);
        }
        viewCommentsBtn.setVisibility(
                comments.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void promptForLogin() {
        ((BaseActivity)getActivity()).promptForLogin(); // TODO: Use callback interface?
    }

    @Override
    public void onError(String message) {
        if(loadingView.getVisibility() == VISIBLE || errorView.getVisibility() == VISIBLE) {
            activateErrorView(message);
            setAppBarExpanded(false);
        } else
            Snackbar.make(title, message, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick({R.id.comment_title, R.id.view_comments_btn})
    public void clickViewComments() {
        Intent i = new Intent(getActivity(), CommentsActivity.class);
        i.putExtra(POST_ID_KEY, postId);
        startActivity(i);
    }

    @OnClick(R.id.fab)
    public void clickFab() { // TODO: The Intent code should go in the activity, used through callback
        Intent intent = new Intent(getActivity(), CreateCommentActivity.class);
        intent.putExtra(ShowFragment.POST_ID_KEY, postId);
        ((BaseActivity)getActivity()).doIfLoggedIn(intent);
    }

}
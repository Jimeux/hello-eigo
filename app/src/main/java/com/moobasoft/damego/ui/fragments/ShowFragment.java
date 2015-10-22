package com.moobasoft.damego.ui.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.moobasoft.damego.ui.activities.IndexActivity;
import com.moobasoft.damego.ui.presenters.ShowPresenter;
import com.moobasoft.damego.ui.views.CommentView;
import com.moobasoft.damego.util.PostUtil;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.VISIBLE;

public class ShowFragment extends BaseFragment implements ShowPresenter.ShowView {

    public static final String POST_KEY    = "post_key";
    public static final String POST_ID_KEY = "post_id";
    public static final String TAG_NAME_KEY = "tag_name";
    private int postIdArg;
    private String tagNameArg;
    private Post post;

    @Inject ShowPresenter presenter;

    @Bind(R.id.title)             TextView title;
    @Bind(R.id.body)              TextView body;
    @Bind(R.id.tags)              ViewGroup tags;
    @Bind(R.id.backdrop)          ImageView backdrop;
    @Bind(R.id.comment_title)     TextView commentTitle;
    @Bind(R.id.comments_preview)  ViewGroup commentContainer;
    @Bind(R.id.view_comments_btn) ViewGroup viewCommentsBtn;

    public String getName() {
        if (post != null) return post.getTitle();
        else return "No post!";
    }

    public ShowFragment() {}

    public static ShowFragment newInstance(int postId, String tagName) {
        ShowFragment fragment = new ShowFragment();
        Bundle args = new Bundle();
        args.putInt(POST_ID_KEY, postId);
        args.putString(TAG_NAME_KEY, tagName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        presenter.bindView(this);
        postIdArg = getArguments().getInt(POST_ID_KEY, 0);
        tagNameArg = getArguments().getString(TAG_NAME_KEY);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       if (savedInstanceState != null)
            post = Parcels.unwrap(savedInstanceState.getParcelable(POST_KEY));

        if (post != null)
            loadPost(post);
        else
            onRefresh();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(POST_KEY, Parcels.wrap(post));
    }

    @Override
    public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        activateLoadingView();
        presenter.getPost(postIdArg);
    }

    @Override
    public void onPostRetrieved(Post post) {
        this.post = post;
        loadPost(post);
    }

    private void loadPost(Post post) {
        title.setText(post.getTitle());
        commentTitle.setText(getString(
                R.string.comments_title, post.getCommentsCount()));
        body.setText(Html.fromHtml(post.getBody().trim().replaceAll("[\n\r]", "")));
        insertComments(post);
        Glide.with(this)
                .load(post.getImageUrl())
                .into(backdrop);

        PostUtil.insertTags(post, getActivity().getLayoutInflater(), tags, true);
        for (int i = 0; i < tags.getChildCount(); i++) {
            TextView tag = (TextView) tags.getChildAt(i);
            tag.setOnClickListener(v ->
                    ((IndexActivity) getActivity()).onTagClicked(tag.getText().toString()));
        }

        activateContentView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getView() != null) {
            toggleVisibility(title, body, tags);
            TransitionManager.beginDelayedTransition((ViewGroup) getView().getRootView(), new Slide());
            toggleVisibility(title, body, tags);
        }
    }

    @Override
    public void setToolbar() {
        if (toolbar != null) {
            toolbar.setTitle(tagNameArg);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void insertComments(Post post) {
        int end = (post.getComments().size() >= 3) ? 3 : post.getComments().size();
        List<Comment> comments = post.getComments().subList(0, end);
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
    public void onError(String message) {
        if (loadingView.getVisibility() == VISIBLE || errorView.getVisibility() == VISIBLE)
            activateErrorView(message);
        else
            Snackbar.make(title, message, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick({R.id.comment_title, R.id.view_comments_btn})
    public void clickViewComments() {
        Intent i = new Intent(getActivity(), CommentsActivity.class);
        i.putExtra(POST_ID_KEY, postIdArg);
        startActivity(i);
    }

    @OnClick(R.id.fab)
    public void clickFab() {
        Intent intent = new Intent(getActivity(), CreateCommentActivity.class);
        intent.putExtra(ShowFragment.POST_ID_KEY, postIdArg);
        ((BaseActivity)getActivity()).doIfLoggedIn(intent);
    }

}
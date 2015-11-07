package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.CommentsAdapter;
import com.moobasoft.damego.ui.fragments.base.RxFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

public class CommentsFragment extends RxFragment implements OnRefreshListener {

    @Bind(R.id.comment_list)  RecyclerView commentList;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout refreshLayout;

    private static final String POST_KEY = "post_key";
    private CommentsAdapter commentsAdapter;

    public CommentsFragment() {}

    public static CommentsFragment newInstance(Post post) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(POST_KEY, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        ButterKnife.bind(this, view);
        initialiseRecyclerView();
        Post post = getArguments().getParcelable(POST_KEY);

        if (post != null && post.getComments() != null) {
            if (post.getComments().isEmpty())
                activateEmptyView(getString(R.string.comments_empty));
            else
                commentsAdapter.loadComments(post.getComments());
        } else
            activateErrorView(getString(R.string.error_default));

        return view;
    }

    private void initialiseRecyclerView() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        commentsAdapter  = new CommentsAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        commentList.setLayoutManager(layoutManager);
        commentList.setAdapter(commentsAdapter);
    }

    @Override
    public void onRefresh() {
        ((RxFragment)getParentFragment()).onRefresh();
    }

}
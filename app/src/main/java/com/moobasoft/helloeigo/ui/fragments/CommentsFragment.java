package com.moobasoft.helloeigo.ui.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.helloeigo.R;
import com.moobasoft.helloeigo.events.EventBus;
import com.moobasoft.helloeigo.events.comment.CommentCreatedEvent;
import com.moobasoft.helloeigo.rest.models.Post;
import com.moobasoft.helloeigo.ui.CommentsAdapter;
import com.moobasoft.helloeigo.ui.fragments.base.RxFragment;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

public class CommentsFragment extends RxFragment implements OnRefreshListener {

    @Bind(R.id.comment_list)  RecyclerView commentList;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout refreshLayout;

    private static final String POST_KEY = "post_key";
    private CommentsAdapter commentsAdapter;
    private CompositeSubscription eventSubscriptions;

    @Inject EventBus eventBus;

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
        getComponent().inject(this);

        if (post != null && post.getComments() != null) {
            if (post.getComments().isEmpty())
                activateEmptyView(getString(R.string.comments_empty));
            else
                commentsAdapter.loadComments(post.getComments());
        } else
            activateErrorView(getString(R.string.error_default));

        return view;
    }

    @Override public void onStart() {
        super.onStart();
        subscribeToEvents();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (eventSubscriptions != null)
            eventSubscriptions.clear();
    }

    private void subscribeToEvents() {
        Subscription createdEvent = eventBus
                .listenFor(CommentCreatedEvent.class)
                //.map(CommentCreatedEvent::getComment)
                //.subscribe(commentsAdapter::loadComment);
                .subscribe(event -> onRefresh());
        eventSubscriptions = new CompositeSubscription(createdEvent);
    }

    private void initialiseRecyclerView() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        commentsAdapter  = new CommentsAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        commentList.setItemAnimator(new DefaultItemAnimator());
        commentList.setLayoutManager(layoutManager);
        commentList.setAdapter(commentsAdapter);
    }

    @Override
    public void onRefresh() {
        ((RxFragment)getParentFragment()).onRefresh();
    }

}
package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.CommentsAdapter;
import com.moobasoft.damego.ui.EndlessOnScrollListener;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

public class CommentsFragment extends Fragment implements OnRefreshListener {

    @Bind(R.id.comment_list)  RecyclerView commentList;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout refreshLayout;

    private static final String LAYOUT_KEY = "layout";
    public static final String POST_KEY = "post_key";
    private CommentsAdapter commentsAdapter;
    private Post post;

    public CommentsFragment() {}

    public static CommentsFragment newInstance(Post post) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(POST_KEY, Parcels.wrap(post));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        ButterKnife.bind(this, view);

        post = Parcels.unwrap(getArguments().getParcelable(POST_KEY)); // TODO: Check and throw error
        initialiseRecyclerView();
        commentsAdapter.loadComments(post.getComments());
        refreshLayout.setRefreshing(false);
        return view;
    }

    private void initialiseRecyclerView() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent);
        commentsAdapter  = new CommentsAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        commentList.setLayoutManager(layoutManager);
        commentList.setAdapter(commentsAdapter);
        commentList.addOnScrollListener(new EndlessOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                //refreshLayout.setRefreshing(true);
                //presenter.getPost(currentPage); //TODO: Paginate comments
            }

            @Override  // TODO: Fix this duplicated code
            public boolean isRefreshing() {
                /*if (toolbar != null && commentList != null) {
                    boolean cannotScrollUp =
                            toolbar.getVisibility() == VISIBLE &&
                                    !ViewCompat.canScrollVertically(commentList, -1);
                    refreshLayout.setEnabled(cannotScrollUp);
                }*/
                return refreshLayout.isRefreshing();
            }
        });
    }

    /*@Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(LAYOUT_KEY, commentList.getLayoutManager().onSaveInstanceState());
    }*/

    @Override
    public void onViewStateRestored(Bundle state) {
        super.onViewStateRestored(state);
        if (state != null) {
            Parcelable savedRecyclerLayoutState = state.getParcelable(LAYOUT_KEY);
            commentList.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onRefresh() {
        commentsAdapter.clear();
    }

}
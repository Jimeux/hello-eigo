package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;

import java.util.List;

public class IndexPresenter extends RxPresenter<IndexPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onPostsRetrieved(List<Post> posts);
    }

    private final PostService postService;

    public IndexPresenter(PostService postService, RxSubscriber subscriptions) {
        super(subscriptions);
        this.postService = postService;
    }

    public void postsIndex(int page) {
        subscriptions.add(postService.index(page),
                          view::onPostsRetrieved,
                          this::handleError);
    }

    public void filterByTag(String tag, int page) {
        subscriptions.add(postService.filterByTag(tag, page),
                          view::onPostsRetrieved,
                          this::handleError);
    }

}
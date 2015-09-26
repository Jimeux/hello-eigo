package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;

import java.util.List;

import retrofit.Response;
import retrofit.Result;

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
                          this::handleOnNext,
                          this::handleError);
    }

    public void filterByTag(String tag, int page) {
        subscriptions.add(postService.filterByTag(tag, page),
                          this::handleOnNext,
                          this::handleError);
    }

    public void handleOnNext(Result<List<Post>> result) {
        Response<List<Post>> response = result.response();

        if (result.isError())
            handleError(result.error());
        else if (response.code() == SUCCESS)
            view.onPostsRetrieved(response.body());
        else
            defaultResponses(response.code());
    }

}
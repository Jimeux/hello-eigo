package com.moobasoft.helloeigo.ui.presenters;

import com.moobasoft.helloeigo.rest.services.PostService;
import com.moobasoft.helloeigo.ui.RxSubscriber;
import com.moobasoft.helloeigo.ui.presenters.base.RxPresenter;

import java.util.List;

import retrofit.Response;
import retrofit.Result;

public class IndexPresenter extends RxPresenter<IndexPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onTagsRetrieved(List<String> tags);
    }

    private final PostService postService;

    public IndexPresenter(PostService postService, RxSubscriber subscriptions) {
        super(subscriptions);
        this.postService = postService;
    }

    public void getTags() {
        subscriptions.add(postService.getTags(),
                this::handleOnNext,
                this::handleError);
    }

    public void handleOnNext(Result<List<String>> result) {
        Response<List<String>> response = result.response();

        if (result.isError())
            handleError(result.error());
        else if (response.code() == SUCCESS)
            view.onTagsRetrieved(response.body());
        else
            defaultResponses(response.code());
    }

}
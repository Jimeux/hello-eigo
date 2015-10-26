package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;

import java.util.List;

import retrofit.Response;
import retrofit.Result;

public class MainPresenter extends RxPresenter<MainPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onTagsRetrieved(List<String> tags);
    }

    private final PostService postService;

    public MainPresenter(PostService postService, RxSubscriber subscriptions) {
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
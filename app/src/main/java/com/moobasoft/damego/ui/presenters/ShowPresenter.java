package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;

import retrofit.Response;
import retrofit.Result;

public class ShowPresenter extends RxPresenter<ShowPresenter.ShowView> {

    public interface ShowView extends RxPresenter.RxView {
        void onPostRetrieved(Post post);
    }

    private PostService postService;

    public ShowPresenter(PostService postService, RxSubscriber rxSubscriber) {
        super(rxSubscriber);
        this.postService = postService;
    }

    public void getPost(int id) {
        subscriptions.add(postService.show(id),
                          this::handleOnNext,
                          this::handleError);
    }

    public void handleOnNext(Result<Post> result) {
        Response<Post> response = result.response();

        if (result.isError())
            handleError(result.error());
        else if (response.code() == SUCCESS)
            view.onPostRetrieved(response.body());
        else
            defaultResponses(response.code());
    }

}
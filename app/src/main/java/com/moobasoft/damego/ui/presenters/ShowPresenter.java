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
        void onBookmarked(boolean added);
    }

    private PostService postService;

    public ShowPresenter(PostService postService, RxSubscriber rxSubscriber) {
        super(rxSubscriber);
        this.postService = postService;
    }

    public void createBookmark(int postId) {
        subscriptions.add(postService.createBookmark(postId),
                          this::onBookmarked,
                          this::handleError);
    }

    public void deleteBookmark(int postId) {
        subscriptions.add(postService.deleteBookmark(postId),
                          this::onUnbookmarked,
                          this::handleError);
    }

    public void getPost(int id) {
        subscriptions.add(postService.show(id),
                this::onPostReturned,
                this::handleError);
    }

    public void onBookmarked(Result<Void> result) {
        Response response = result.response();
        if (response.code() == SUCCESS)
            view.onBookmarked(true);
        else
            handleResponse(result, response);
    }

    public void onUnbookmarked(Result<Void> result) {
        Response response = result.response();
        if (response.code() == SUCCESS)
            view.onBookmarked(false);
        else
            handleResponse(result, response);
    }

    public void onPostReturned(Result<Post> result) {
        Response<Post> response = result.response();
        if (response.code() == SUCCESS)
            view.onPostRetrieved(response.body());
        else
            handleResponse(result, response);
    }

    private void handleResponse(Result result, Response response) {
        if (result.isError())
            handleError(result.error());
        else
            defaultResponses(response.code());
    }

}
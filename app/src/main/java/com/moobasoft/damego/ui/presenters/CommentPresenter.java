package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.models.Comment;
import com.moobasoft.damego.rest.models.CommentError;
import com.moobasoft.damego.rest.requests.CommentRequest;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.HttpException;

public class CommentPresenter extends RxPresenter<CommentPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onCommentSubmitted(Comment comment);
    }

    private PostService postService;

    public CommentPresenter(PostService postService, RxSubscriber rxSubscriber) {
        super(rxSubscriber);
        this.postService = postService;
    }

    public void createComment(int postId, String body) {
        subscriptions.add(
                postService.createComment(postId, new CommentRequest(body)),
                view::onCommentSubmitted,
                this::handleError);
    }

    public void handleError(Throwable throwable) {
        int errorType = getErrorType(throwable);

        if (errorType == INPUT_ERROR)
            view.onError(getInputError(throwable));
        else
            super.handleError(throwable);
    }

    private String getInputError(Throwable httpException) {
        try {
            ResponseBody responseBody = ((HttpException) httpException).response().errorBody();
            CommentError commentError = CommentError.CONVERTER.fromBody(responseBody);
            return commentError.getBody().get(0);
        } catch (ClassCastException | IOException e) {
            return "An unexpected error occurred";
        }
    }
}
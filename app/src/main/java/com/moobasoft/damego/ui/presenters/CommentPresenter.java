package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.models.Comment;
import com.moobasoft.damego.rest.errors.CommentError;
import com.moobasoft.damego.rest.requests.CommentRequest;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.Response;
import retrofit.Result;

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
                this::handleOnNext,
                this::handleError);
    }

    public void handleOnNext(Result<Comment> result) {
        Response<Comment> response = result.response();

        if (result.isError())
            handleError(result.error());
        else if (response.isSuccess())
            view.onCommentSubmitted(response.body());
        else if (response.code() == UNPROCESSABLE_ENTITY)
            view.onError(getInputError(response.errorBody()));
        else
            defaultResponses(response.code());
    }

    private String getInputError(ResponseBody errorBody) {
        try {
            CommentError commentError = CommentError.CONVERTER.convert(errorBody);
            return commentError.getBody().get(0);
        } catch (IOException e) {
            return "An unexpected error occurred";
        }
    }
}
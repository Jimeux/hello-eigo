package com.moobasoft.helloeigo.ui.presenters;

import com.moobasoft.helloeigo.R;
import com.moobasoft.helloeigo.rest.errors.CommentError;
import com.moobasoft.helloeigo.rest.models.Comment;
import com.moobasoft.helloeigo.rest.requests.CommentRequest;
import com.moobasoft.helloeigo.rest.services.PostService;
import com.moobasoft.helloeigo.ui.RxSubscriber;
import com.moobasoft.helloeigo.ui.presenters.base.RxPresenter;
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
            view.onError(R.string.comment_length_error);
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
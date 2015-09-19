package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.models.Comment;
import com.moobasoft.damego.rest.models.CommentError;
import com.moobasoft.damego.rest.requests.CommentRequest;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.ConnectException;

import retrofit.HttpException;

public class CommentPresenter extends RxPresenter<CommentPresenter.View> {

    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int UNAUTHORIZED         = 401;

    public interface View {
        void onCommentSubmitted(Comment comment);
        void onCommentError(String message);
        void onUnauthorized();
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

    private void handleError(Throwable throwable) {
        if (throwable instanceof ConnectException) {
            view.onCommentError("Error connecting to server.");
        } else if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            try {
                if (httpException.response().code() == UNPROCESSABLE_ENTITY)
                    displayCommentError(httpException);
                else if (httpException.response().code() == UNAUTHORIZED)
                    view.onUnauthorized();
                else
                    view.onCommentError("An unexpected error occurred");
            } catch (NullPointerException | IOException e) {
                view.onCommentError("An unexpected error occurred");
            }
        } else {
            view.onCommentError("An unexpected error occurred");
        }
    }

    private void displayCommentError(HttpException httpException) throws IOException {
        ResponseBody responseBody = httpException.response().errorBody();
        CommentError commentError = CommentError.CONVERTER.fromBody(responseBody);
        view.onCommentError(commentError.getBody().get(0));
    }

}
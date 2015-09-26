package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.models.Comment;
import com.moobasoft.damego.rest.models.CommentError;
import com.moobasoft.damego.rest.requests.CommentRequest;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.SocketTimeoutException;

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
    if (result.isError()) {
        handleError(result.error());
    } else {
        switch (result.response().code()) {
            case SUCCESS:
                view.onCommentSubmitted(result.response().body());
                break;
            case UNAUTHORIZED:
                view.promptForLogin();
                break;
            case UNPROCESSABLE_ENTITY:
                view.onError(getInputError(result.response().errorBody()));
                break;
            default:
                view.onError("An unexpected error occurred");
        }
    }
}

public void handleError(Throwable throwable) {
    String message = throwable.getMessage();

    if (message.contains(OFFLINE_CODE))
        view.onError("Not connected to the Internet.");
    else if (message.contains(SERVER_DOWN_CODE))
        view.onError("Couldn't connect to server.");
    else if (throwable instanceof SocketTimeoutException)
        view.onError("Request timed out.");
    else
        view.onError("An unexpected error occurred.");
}

private String getInputError(ResponseBody errorBody) {
    try {
        CommentError commentError = CommentError.CONVERTER.fromBody(errorBody);
        return commentError.getBody().get(0);
    } catch (IOException e) {
        return "An unexpected error occurred";
    }
}
}
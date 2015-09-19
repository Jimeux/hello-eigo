package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.models.Comment;
import com.moobasoft.damego.rest.models.CommentError;
import com.moobasoft.damego.rest.requests.CommentRequest;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.presenters.base.BasePresenter;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.ConnectException;

import retrofit.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentPresenter extends BasePresenter<CommentPresenter.View> {

    public interface View {
        void onCommentSubmitted(Comment comment);
        void onCommentError(String message);
    }

    private PostService postService;

    public CommentPresenter(PostService postService) {
        this.postService = postService;
    }

    public void createComment(int postId, String body) {
        postService.createComment(postId, new CommentRequest(body))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::onCommentSubmitted, this::handleError);
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof ConnectException) {
            view.onCommentError("Error connecting to server.");
        } else if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            try {
                ResponseBody responseBody = httpException.response().errorBody();
                CommentError commentError = CommentError.CONVERTER.fromBody(responseBody);
                view.onCommentError(commentError.getBody().get(0));
            } catch (NullPointerException | IOException e) {
                view.onCommentError("An unexpected error occurred");
            }
        } else {
            view.onCommentError("An unexpected error occurred");
        }
    }

}
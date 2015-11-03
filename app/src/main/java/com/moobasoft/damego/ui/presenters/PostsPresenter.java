package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;

import java.util.List;

import retrofit.Response;
import retrofit.Result;
import rx.Observable;
import rx.Subscription;

import static com.moobasoft.damego.ui.fragments.PostsFragment.Mode;

public class PostsPresenter extends RxPresenter<PostsPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onPostsRetrieved(List<Post> posts);
    }

    private final PostService postService;
    private Observable<Result<List<Post>>> request;
    private Subscription subscription;

    public PostsPresenter(PostService postService, RxSubscriber subscriptions) {
        super(subscriptions);
        this.postService = postService;
    }

    public void loadPosts(Mode mode, String tag, boolean refresh, int page) {
        if (request == null) request = setRequest(mode, tag, refresh, page);
        createSubscription();
    }

    private Observable<Result<List<Post>>> setRequest(Mode mode, String tag, boolean refresh, int page) {
        String cacheHeader = getCacheHeader(refresh);
        switch (mode) {
            case TAG:
                return postService.filterByTag(cacheHeader, tag, page).cache();
            case BOOKMARKS:
                return postService.getBookmarks(cacheHeader, page).cache();
            case SEARCH:
                return postService.search(tag, page).cache();
            default:
                return postService.index(cacheHeader, page).cache();
        }
    }

    private void createSubscription() {
        subscription = request
                .compose(subscriptions.applySchedulers())
                .subscribe(this::handleOnNext,
                        this::handleError);
    }

    private void handleOnNext(Result<List<Post>> result) {
        if (view == null) return;
        subscription = null;
        request = null;

        Response<List<Post>> response = result.response();

        if (result.isError())
            handleError(result.error());
        else if (response.code() == SUCCESS)
            view.onPostsRetrieved(response.body());
        else
            defaultResponses(response.code());
    }

}
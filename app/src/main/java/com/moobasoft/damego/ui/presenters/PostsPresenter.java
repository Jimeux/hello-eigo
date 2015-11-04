package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;

import java.util.List;

import retrofit.Response;
import retrofit.Result;
import rx.Observable;

import static com.moobasoft.damego.ui.fragments.PostsFragment.Mode;

public class PostsPresenter extends RxPresenter<PostsPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onPostsRetrieved(List<Post> posts);
    }

    private final PostService postService;

    public PostsPresenter(PostService postService, RxSubscriber subscriptions) {
        super(subscriptions);
        this.postService = postService;
    }

    public void loadPosts(Mode mode, String tag, boolean refresh, int page) {
        subscriptions.add(getRequest(mode, tag, refresh, page),
                          this::handleOnNext,
                          this::handleError);
    }

    private Observable<Result<List<Post>>> getRequest(Mode mode, String tag, boolean refresh, int page) {
        String cacheHeader = getCacheHeader(refresh);
        switch (mode) {
            case TAG:
                return postService.filterByTag(cacheHeader, tag, page);
            case BOOKMARKS:
                return postService.getBookmarks(cacheHeader, page);
            case SEARCH:
                return postService.search(tag, page);
            default:
                return postService.index(cacheHeader, page);
        }
    }

    private void handleOnNext(Result<List<Post>> result) {
        if (view == null) return;

        if (result.isError())
            handleError(result.error());
        else {
            Response<List<Post>> response = result.response();

            if (response.isSuccess())
                view.onPostsRetrieved(response.body());
            else
                defaultResponses(response.code());
        }
    }

}
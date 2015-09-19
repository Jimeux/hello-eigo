package com.moobasoft.damego.ui.presenters;

import android.util.Log;

import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IndexPresenter extends RxPresenter<IndexPresenter.IndexView> {

    public interface IndexView {
        void onPostsRetrieved(List<Post> posts);

        void onPostsError();
    }

    private PostService postService;

    public IndexPresenter(PostService postService, RxSubscriber subscriptions) {
        super(subscriptions);
        this.postService = postService;
    }

    public void postsIndex(int page) {
        subscriptions.add(postService.index(page),
                          view::onPostsRetrieved,
                          throwable -> view.onPostsError());

        postService.index(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::onPostsRetrieved,
                        throwable -> view.onPostsError());
    }

    public void filterByTag(String tag, int page) {
        postService.filterByTag(tag, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::onPostsRetrieved,
                        throwable -> {
                            Log.d("TAGGART", throwable.getMessage());
                            view.onPostsError();
                        });
    }

}
package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.presenters.base.BasePresenter;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShowPresenter extends BasePresenter<ShowPresenter.ShowView> {

    public interface ShowView {
        void onPostRetrieved(Post post);
        void onPostError();
    }

    private PostService postService;

    public ShowPresenter(PostService postService) {
        this.postService = postService;
    }

    public void getPost(int id) {
        postService.show(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::onPostRetrieved,
                        throwable -> view.onPostError());
    }

}
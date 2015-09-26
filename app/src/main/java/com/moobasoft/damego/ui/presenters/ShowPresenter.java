package com.moobasoft.damego.ui.presenters;

import android.util.Log;

import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;

public class ShowPresenter extends RxPresenter<ShowPresenter.ShowView> {

    public interface ShowView extends RxPresenter.RxView {
        void onPostRetrieved(Post post);
    }

    private PostService postService;

    public ShowPresenter(PostService postService, RxSubscriber rxSubscriber) {
        super(rxSubscriber);
        this.postService = postService;
    }

    public void getPost(int id) {
        subscriptions.add(postService.show(id),
                          postResult -> {
                              if (postResult.isError())
                                  Log.d("TAGGART", "" + postResult.error().getMessage());
                              else
                                  Log.d("TAGGART", "" + postResult.response().code());
                              view.onPostRetrieved(postResult.response().body());
                          },
                          this::handleError);
    }

}
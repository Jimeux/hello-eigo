package com.moobasoft.damego.di.modules;

import com.moobasoft.damego.CredentialStore;
import com.moobasoft.damego.di.scopes.PerActivity;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.rest.services.UserService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.CommentPresenter;
import com.moobasoft.damego.ui.presenters.ConnectPresenter;
import com.moobasoft.damego.ui.presenters.IndexPresenter;
import com.moobasoft.damego.ui.presenters.ShowPresenter;

import dagger.Module;
import dagger.Provides;
import retrofit.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class MainModule {

    @Provides
    public RxSubscriber rxSubscriber() {
        // TODO: provide these schedulers
        return new RxSubscriber(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    @PerActivity @Provides
    public ConnectPresenter loginPresenter(RxSubscriber subscriptionManager,
                                           UserService userService,
                                           CredentialStore credentialStore) {
        return new ConnectPresenter(subscriptionManager, userService, credentialStore);
    }

    @PerActivity
    @Provides
    public IndexPresenter indexPresenter(PostService postService, RxSubscriber rxSubscriber) {
        return new IndexPresenter(postService, rxSubscriber);
    }

    @PerActivity
    @Provides
    public ShowPresenter showPresenter(PostService postService, RxSubscriber rxSubscriber) {
        return new ShowPresenter(postService, rxSubscriber);
    }

    @PerActivity
    @Provides
    public CommentPresenter commentPresenter(PostService postService, RxSubscriber rxSubscriber) {
        return new CommentPresenter(postService, rxSubscriber);
    }

    @PerActivity
    @Provides
    public PostService postService(Retrofit retrofit) {
        return retrofit.create(PostService.class);
    }

    @PerActivity
    @Provides
    public UserService userService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }

}
package com.moobasoft.damego.di.modules;

import com.moobasoft.damego.di.scopes.PerActivity;
import com.moobasoft.damego.rest.services.PostService;
import com.moobasoft.damego.ui.presenters.CommentPresenter;
import com.moobasoft.damego.ui.presenters.IndexPresenter;
import com.moobasoft.damego.ui.presenters.ShowPresenter;

import dagger.Module;
import dagger.Provides;
import retrofit.Retrofit;

@Module
public class MainModule {

    @PerActivity
    @Provides
    public IndexPresenter mainPresenter(PostService postService) {
        return new IndexPresenter(postService);
    }

    @PerActivity
    @Provides
    public ShowPresenter showPresenter(PostService postService) {
        return new ShowPresenter(postService);
    }

    @PerActivity
    @Provides
    public CommentPresenter commentPresenter(PostService postService) {
        return new CommentPresenter(postService);
    }

    @PerActivity
    @Provides
    public PostService postService(Retrofit retrofit) {
        return retrofit.create(PostService.class);
    }

}
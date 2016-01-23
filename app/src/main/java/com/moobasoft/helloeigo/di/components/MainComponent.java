package com.moobasoft.helloeigo.di.components;

import com.moobasoft.helloeigo.di.modules.MainModule;
import com.moobasoft.helloeigo.di.scopes.PerActivity;
import com.moobasoft.helloeigo.ui.activities.ConnectActivity;
import com.moobasoft.helloeigo.ui.activities.CreateCommentActivity;
import com.moobasoft.helloeigo.ui.activities.MainActivity;
import com.moobasoft.helloeigo.ui.fragments.CommentsFragment;
import com.moobasoft.helloeigo.ui.fragments.IndexFragment;
import com.moobasoft.helloeigo.ui.fragments.SearchFragment;
import com.moobasoft.helloeigo.ui.fragments.ShowFragment;
import com.moobasoft.helloeigo.ui.fragments.PostsFragment;

import dagger.Component;

@PerActivity
@Component(
        dependencies = {
                AppComponent.class
        },
        modules = {
                MainModule.class
        }
)
public interface MainComponent {

    void inject(MainActivity mainIndexActivity);
    void inject(ShowFragment mainActivity);
    void inject(SearchFragment searchFragment);
    void inject(CreateCommentActivity createCommentActivity);
    void inject(ConnectActivity connectActivity);
    void inject(PostsFragment postsFragment);
    void inject(IndexFragment indexFragment);
    void inject(CommentsFragment commentsFragment);

}
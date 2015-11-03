package com.moobasoft.damego.di.components;

import com.moobasoft.damego.di.modules.MainModule;
import com.moobasoft.damego.di.scopes.PerActivity;
import com.moobasoft.damego.ui.activities.ConnectActivity;
import com.moobasoft.damego.ui.activities.CreateCommentActivity;
import com.moobasoft.damego.ui.activities.MainActivity;
import com.moobasoft.damego.ui.fragments.IndexFragment;
import com.moobasoft.damego.ui.fragments.SearchFragment;
import com.moobasoft.damego.ui.fragments.ShowFragment;
import com.moobasoft.damego.ui.fragments.PostsFragment;

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

}
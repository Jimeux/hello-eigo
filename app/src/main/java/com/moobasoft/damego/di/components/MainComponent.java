package com.moobasoft.damego.di.components;

import com.moobasoft.damego.di.modules.MainModule;
import com.moobasoft.damego.di.scopes.PerActivity;
import com.moobasoft.damego.ui.activities.CommentsActivity;
import com.moobasoft.damego.ui.activities.ConnectActivity;
import com.moobasoft.damego.ui.activities.CreateCommentActivity;
import com.moobasoft.damego.ui.activities.IndexActivity;
import com.moobasoft.damego.ui.fragments.ShowFragment;
import com.moobasoft.damego.ui.fragments.TagFragment;

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

    void inject(IndexActivity mainIndexActivity);
    void inject(ShowFragment mainActivity);
    void inject(CommentsActivity mainActivity);
    void inject(CreateCommentActivity createCommentActivity);
    void inject(ConnectActivity connectActivity);
    void inject(TagFragment tagFragment);

}
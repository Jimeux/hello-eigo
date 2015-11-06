package com.moobasoft.damego.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.MainManager;
import com.moobasoft.damego.ui.activities.base.BaseActivity;
import com.moobasoft.damego.ui.fragments.IndexFragment;
import com.moobasoft.damego.ui.fragments.PresenterRetainer;
import com.moobasoft.damego.ui.presenters.base.Presenter;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import static com.moobasoft.damego.ui.PostsAdapter.OnPostClickListener;

public class MainActivity extends BaseActivity implements
        OnPostClickListener, OnBackStackChangedListener, PresenterRetainer.PresenterHost {

    public interface ToolbarFragment {
        Toolbar getToolbar();
    }

    private FragmentManager fragmentManager;
    private MainManager manager;

    @Bind(R.id.container) ViewGroup container;

    @Override
    public void putPresenter(@NonNull UUID key, @NonNull Presenter presenter) {
        PresenterRetainer.put(fragmentManager, key, presenter);
    }

    @Override
    public Presenter getPresenter(@NonNull UUID key) {
        return PresenterRetainer.get(fragmentManager, key);
    }

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getComponent().inject(this);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        manager = new MainManager(fragmentManager, container.getId());

        if (state == null) manager.initialiseFragments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMainToolbar();
    }

    public void setMainToolbar() {
        Fragment mainFrag = fragmentManager.findFragmentById(container.getId());
        if (mainFrag != null) {
            setSupportActionBar(((ToolbarFragment) mainFrag).getToolbar());
            boolean notOnHomePage = !(mainFrag instanceof IndexFragment);
            getSupportActionBar().setDisplayHomeAsUpEnabled(notOnHomePage);
        }
    }

    @Override
    public void onSummaryClicked(Post post, boolean openComments, String tagName) {
        manager.openShowFragment(post, tagName, openComments);
    }

    @Override
    public void onTagClicked(String tag) {
        manager.openTagFragment(tag);
    }

    @Override
    public void onBackStackChanged() {
        setMainToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        boolean loggedIn = credentialStore.isLoggedIn();
        menu.findItem(R.id.action_logout).setVisible(loggedIn);
        menu.findItem(R.id.action_login).setVisible(!loggedIn);
        menu.findItem(R.id.action_register).setVisible(!loggedIn);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            /** IndexFragment items */
            case R.id.action_bookmarks:
                manager.openBookmarksFragment(getString(R.string.bookmarks_title));
                break;
            case R.id.action_search:
                manager.openSearchFragment();
                break;

            /** IndexActivity items */
            case R.id.action_login:
                Intent loginIntent = new Intent(this, ConnectActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                loginIntent.putExtra(ConnectActivity.REGISTER, false);
                startActivity(loginIntent);
                break;

            case R.id.action_register:
                Intent registerIntent = new Intent(this, ConnectActivity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                registerIntent.putExtra(ConnectActivity.REGISTER, true);
                startActivity(registerIntent);
                break;

            case R.id.action_logout:
                credentialStore.delete();
                Snackbar.make(container, getString(R.string.logout_success), Snackbar.LENGTH_SHORT).show();
                break;
        }
        supportInvalidateOptionsMenu();
        return false;
    }
}
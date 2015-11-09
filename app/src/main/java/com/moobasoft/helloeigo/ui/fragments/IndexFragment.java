package com.moobasoft.helloeigo.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moobasoft.helloeigo.R;
import com.moobasoft.helloeigo.ui.activities.MainActivity;
import com.moobasoft.helloeigo.ui.fragments.PostsFragment.Mode;
import com.moobasoft.helloeigo.ui.fragments.base.RxFragment;
import com.moobasoft.helloeigo.ui.presenters.IndexPresenter;
import com.moobasoft.helloeigo.util.DepthPageTransformer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IndexFragment extends RxFragment
        implements IndexPresenter.View, MainActivity.ToolbarFragment {

    @Inject IndexPresenter presenter;

    /** w1024p view */
    @Nullable @Bind(R.id.title_view) TextView titleView;

    @Bind(R.id.toolbar)    Toolbar toolbar;
    @Bind(R.id.app_bar)    AppBarLayout appBarLayout;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.view_pager) ViewPager viewPager;

    public static final String SHOW_ALL_TAG = "すべて";
    public static final String TAGS_KEY     = "tags_tag";
    public static final String ADAPTER_KEY  = "adapter_tag";

    private ArrayList<String> tags;
    private Adapter adapter;

    public IndexFragment() {}

    public static IndexFragment newInstance() {
        IndexFragment fragment = new IndexFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setCurrentTag(String tag) {
        viewPager.setCurrentItem(adapter.indexOf(tag));
    }

    @Override
    public Toolbar getToolbar() {
        toolbar.setTitle("");
        return toolbar;
    }

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
        adapter = new Adapter(getChildFragmentManager());
        tags = new ArrayList<>();
        getComponent().inject(this);
        if (state != null)
            tags = state.getStringArrayList(TAGS_KEY);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.removeGroup(R.id.fragment_specific_options);
        inflater.inflate(R.menu.menu_index, menu);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        presenter.bindView(this);
        ButterKnife.bind(this, view);
        tabLayout.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (tags.isEmpty())
            onRefresh();
        else
            setUpViewPager();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (adapter != null && tags != null) {
            state.putParcelable(ADAPTER_KEY, adapter.saveState());
            state.putStringArrayList(TAGS_KEY, tags);
        }
    }

    @Override
    public void onTagsRetrieved(List<String> tags) {
        this.tags = new ArrayList<>(tags);
        activateContentView();
        setUpViewPager();
    }

    private void loadTagFragments() {
        if (adapter.getCount() > 0) return;
        for (String tag : tags) {
            Mode mode =  (tag.equals(SHOW_ALL_TAG)) ? Mode.ALL : Mode.TAG;
            adapter.addFragment(PostsFragment.newInstance(mode, tag), tag);
        }
    }

    private void setUpViewPager() {
        loadTagFragments();

        viewPager.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= 11)
            viewPager.setPageTransformer(true, new DepthPageTransformer());

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getView() != null)
            TransitionManager.beginDelayedTransition(appBarLayout, new Slide());
        tabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        activateLoadingView();
        presenter.getTags();
    }

    @Override
    public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

        public int indexOf(String tag) {
            return fragmentTitles.indexOf(tag);
        }
    }

}
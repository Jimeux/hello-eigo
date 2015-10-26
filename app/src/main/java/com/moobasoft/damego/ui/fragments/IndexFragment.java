package com.moobasoft.damego.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.ui.activities.IndexActivity;
import com.moobasoft.damego.ui.presenters.MainPresenter;
import com.moobasoft.damego.util.DepthPageTransformer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IndexFragment extends BaseFragment implements MainPresenter.View  {
    @Inject MainPresenter presenter;

    @Nullable @Bind(R.id.app_bar) AppBarLayout appBarLayout;
    @Nullable @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.view_pager)           ViewPager viewPager;

    public static final String POSITION_STACK_KEY = "position_stack_key";
    public static final String TAGS_KEY = "tags_tag";
    public static final String ADAPTER_KEY = "adapter_tag";
    private ArrayList<String> tags;
    private Deque<Integer> positionStack;
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void savePagerPosition() {
        positionStack.push(viewPager.getCurrentItem());
    }

    public void restorePagerPosition() {
        if (!positionStack.isEmpty())
            viewPager.setCurrentItem(positionStack.pop());
        setDisplayHomeAsUpEnabled();
    }

    private void setDisplayHomeAsUpEnabled() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(positionStack.size() > 0);
    }

    public CharSequence getCurrentTitle() {
        return adapter.getPageTitle(viewPager.getCurrentItem());
    }

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        adapter = new Adapter(getChildFragmentManager());
        positionStack = new ArrayDeque<>();
        tags = new ArrayList<>();
        setHasOptionsMenu(true);
        if (state != null) {
            ArrayList<Integer> savedStack = state.getIntegerArrayList(POSITION_STACK_KEY);
            if (savedStack != null) positionStack.addAll(savedStack);
            //adapter.restoreState(state.getParcelable(ADAPTER_KEY), getActivity().getClassLoader());
            tags = state.getStringArrayList(TAGS_KEY);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.removeGroup(R.id.fragment_specific_options);
        inflater.inflate(R.menu.menu_index, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_bookmarks)
            // TODO: This should really be called through an interface
            ((IndexActivity)getActivity()).openBookmarks();
        /*else if (item.getId() == R.id.action_search)
                loadFragment(indexContainer.getId(), new SearchFragment(), SEARCH_TAG, true, true);*/
        return super.onOptionsItemSelected(item);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        getComponent().inject(this);
        presenter.bindView(this);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (tabLayout == null) {
            appBarLayout = ((IndexActivity)getActivity()).getAppBar(); //TODO: Use interface?
            toolbar = ((IndexActivity)getActivity()).getToolbar();
            tabLayout = ((IndexActivity)getActivity()).getTabLayout();
        }
        if (tabLayout != null) tabLayout.setVisibility(View.GONE);

        if (tags.isEmpty())
            onRefresh();
        else
            loadTagFragments();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (adapter != null && tags != null) {
            state.putParcelable(ADAPTER_KEY, adapter.saveState());
            state.putStringArrayList(TAGS_KEY, tags);
            state.putIntegerArrayList(POSITION_STACK_KEY, new ArrayList<>(positionStack));
        }
    }

    @Override
    public void setToolbar() {
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            setDisplayHomeAsUpEnabled();
        }
    }

    @Override
    public void onTagsRetrieved(List<String> tags) {
        this.tags = new ArrayList<>(tags);
        activateContentView();
        loadTagFragments();
    }

    private void loadTagFragments() {
        for (String tag : tags)
             adapter.addFragment(TagFragment.newInstance(TagFragment.MODE_TAG, tag), tag);

        viewPager.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= 11)
            viewPager.setPageTransformer(true, new DepthPageTransformer());
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getView() != null)
                TransitionManager.beginDelayedTransition(appBarLayout, new Slide());
            tabLayout.setVisibility(View.VISIBLE);
        }
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
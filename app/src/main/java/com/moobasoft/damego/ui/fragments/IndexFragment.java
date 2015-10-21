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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    @Nullable @Bind(R.id.search_bar) Toolbar searchBar;
    @Bind(R.id.view_pager)           ViewPager viewPager;

    public static final String POSITION_STACK_KEY = "position_stack_key";
    public static final String TAGS_KEY = "tags_tag";
    public static final String ADAPTER_KEY = "adapter_tag";
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private Deque<Integer> positionStack = new ArrayDeque<>();

    public void savePagerPosition() {
        positionStack.push(viewPager.getCurrentItem());
    }

    public void restorePagerPosition() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(positionStack.size() > 1);
        if (!positionStack.isEmpty())
            viewPager.setCurrentItem(positionStack.pop());
    }

    public CharSequence getCurrentTitle() {
        return adapter.getPageTitle(viewPager.getCurrentItem());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new Adapter(getChildFragmentManager());
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        getComponent().inject(this);
        presenter.bindView(this);
        ButterKnife.bind(this, view);

        if (state != null) {
            ArrayList<Integer> savedStack = state.getIntegerArrayList(POSITION_STACK_KEY);
            if (savedStack != null) positionStack.addAll(savedStack);
            adapter.restoreState(state.getParcelable(ADAPTER_KEY), getActivity().getClassLoader());
            tags = state.getStringArrayList(TAGS_KEY);
        }

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

        if (tags == null)
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
        if (toolbar != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onTagsRetrieved(List<String> tags) {
        this.tags = new ArrayList<>(tags);
        activateContentView();
        loadTagFragments();
    }

    private void loadTagFragments() {
        for (String tag : tags)
             adapter.addFragment(TagFragment.newInstance(tag), tag);

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
    public void onError(String message) {
        activateErrorView(message);
    }

    @Override
    public void onRefresh() {
        activateLoadingView();
        presenter.getTags();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getView() != null) {
                toggleVisibility(toolbar);
                TransitionManager.beginDelayedTransition(appBarLayout, new Slide());
                toggleVisibility(searchBar);
            }
        }
        return super.onOptionsItemSelected(item);
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
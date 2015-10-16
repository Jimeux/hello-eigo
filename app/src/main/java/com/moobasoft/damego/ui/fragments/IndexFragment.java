package com.moobasoft.damego.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.ui.activities.IndexActivity;
import com.moobasoft.damego.ui.presenters.MainPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IndexFragment extends BaseFragment implements MainPresenter.View  {
    @Inject MainPresenter presenter;

    @Nullable @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;

    public static final String TAGS_TAG = "tags";
    private List<String> tags;

    public IndexFragment() {}

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        getComponent().inject(this);
        ButterKnife.bind(this, view);
        presenter.bindView(this);
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        setToolbar();

        if (savedInstanceState == null && tags == null) {
            presenter.getTags();
            activateLoadingView();
        } else if (savedInstanceState != null) {
            tags = savedInstanceState.getStringArrayList(TAGS_TAG);
            loadTagFragments();
        } else {
            loadTagFragments();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(TAGS_TAG, new ArrayList<>(tags));
    }

    private void setToolbar() {
        if (toolbar == null) {
            toolbar = ((IndexActivity)getActivity()).getToolbar(); //TODO: Use interface?
            tabLayout = ((IndexActivity)getActivity()).getTabLayout();
        }
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onTagsRetrieved(List<String> tags) {
        this.tags = tags;
        activateContentView();
        loadTagFragments();
    }

    private void loadTagFragments() {
        Adapter adapter = new Adapter(getChildFragmentManager());
        for (String tag : tags) {
            Fragment frag = getChildFragmentManager().findFragmentByTag(tag);
            if (frag != null)
                adapter.addFragment(frag, tag);
            else
                adapter.addFragment(TagFragment.newInstance(tag), tag);
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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
    public void onDestroyView() {
        presenter.releaseView();
        super.onDestroyView();
        ButterKnife.unbind(this);
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
    }

}
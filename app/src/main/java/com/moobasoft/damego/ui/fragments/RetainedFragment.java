package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.moobasoft.damego.ui.presenters.base.BasePresenter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RetainedFragment extends Fragment {

    private final Map<UUID, BasePresenter> presenterMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void put(@NonNull final UUID key, @NonNull final BasePresenter presenter) {
        presenterMap.put(key, presenter);
    }

    @Nullable
    public BasePresenter get(@NonNull final UUID key) {
        return presenterMap.get(key);
    }

    public interface PresenterHost {
        void storePostsPresenter(UUID key, BasePresenter presenter);
        BasePresenter getPostsPresenter(UUID key);
    }

    public static final String TAG_RETAINED_FRAGMENT = "retained";

    private RetainedFragment getRetainer() {
        RetainedFragment retainer = (RetainedFragment)
                getFragmentManager().findFragmentByTag(TAG_RETAINED_FRAGMENT);

        if (retainer == null) {
            retainer = new RetainedFragment();
            getFragmentManager().beginTransaction()
                    .add(retainer, TAG_RETAINED_FRAGMENT)
                    .commit();
        }

        return retainer;
    }

    public void storePostsPresenter(UUID key, BasePresenter presenter) {
        getRetainer().put(key, presenter);
    }

    @Nullable
    public BasePresenter getPostsPresenter(UUID key) {
        return getRetainer().get(key);
    }
}
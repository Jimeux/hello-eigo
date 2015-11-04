package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.moobasoft.damego.ui.presenters.base.Presenter;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class PresenterRetainer extends Fragment {

    private final Map<UUID, Presenter> presenterMap = new WeakHashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void put(@NonNull final UUID key, @NonNull final Presenter presenter) {
        presenterMap.put(key, presenter);
    }

    @Nullable
    public Presenter get(@NonNull final UUID key) {
        return presenterMap.get(key);
    }

    /**
     * For Activity class
     */

    /*public static final String TAG_RETAINER = "retainer_tag";

    private PresenterRetainer getRetainer() {
        PresenterRetainer retainer = (PresenterRetainer)
                fragmentManager.findFragmentByTag(TAG_RETAINER);
        if (retainer == null) {
            retainer = new PresenterRetainer();
            fragmentManager.beginTransaction()
                    .add(retainer, TAG_RETAINER).commit();
        }
        return retainer;
    }

    @Override
    public void putPresenter(UUID key, Presenter presenter) {
        getRetainer().put(key, presenter);
    }

    @Override
    public Presenter getPresenter(UUID key) {
        return getRetainer().get(key);
    }*/


    /**
     * For fragment class (remember to save/restore UUID)
     */

    /*private UUID presenterUuid;

    public static final String UUID_KEY  = "uuid_key";

    public interface PresenterHost {
        void putPresenter(UUID key, Presenter presenter);
        Presenter getPresenter(UUID key);
    }

    presenter = (PostsPresenter)
            ((PresenterHost) getActivity()).getPresenter(presenterUuid);

    if (presenter == null) {
        getComponent().inject(this);
        presenterUuid = UUID.randomUUID();
        ((PresenterHost) getActivity()).putPresenter(presenterUuid, presenter);
    }*/
}
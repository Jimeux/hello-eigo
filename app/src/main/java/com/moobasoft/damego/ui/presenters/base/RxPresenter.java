package com.moobasoft.damego.ui.presenters.base;

import com.moobasoft.damego.ui.RxSubscriber;

public class RxPresenter<V> extends BasePresenter<V> {

    protected final RxSubscriber subscriptions;

    public RxPresenter(RxSubscriber subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public void releaseView() {
        super.releaseView();
        subscriptions.clear();
    }

}
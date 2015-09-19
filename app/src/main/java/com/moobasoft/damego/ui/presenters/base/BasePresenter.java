package com.moobasoft.damego.ui.presenters.base;

public class BasePresenter<V> {

    protected V view;

    public void bindView(V view) {
        this.view = view;
    }

    public void releaseView() {
        this.view = null;
    }
}
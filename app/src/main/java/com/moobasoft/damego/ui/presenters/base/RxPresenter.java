package com.moobasoft.damego.ui.presenters.base;

import com.moobasoft.damego.ui.RxSubscriber;

import java.net.SocketTimeoutException;

public abstract class RxPresenter<V extends RxPresenter.RxView> extends BasePresenter<V> {

    public interface RxView {
        void promptForLogin();
        void onError(String message);
    }

    protected final RxSubscriber subscriptions;

    public RxPresenter(RxSubscriber subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public void releaseView() {
        super.releaseView();
        subscriptions.clear();
    }

    public static final int SUCCESS              = 200;
    public static final int UNAUTHORIZED         = 401;
    public static final int UNPROCESSABLE_ENTITY = 422;

    public void defaultResponses(int responseCode) {
        switch (responseCode) {
            case UNAUTHORIZED:
                view.promptForLogin();
                break;
            default:
                view.onError("An unexpected error occurred");
        }
    }

    public static final String OFFLINE_CODE     = "ENETUNREACH";
    public static final String SERVER_DOWN_CODE = "ECONNREFUSED";

    public void handleError(Throwable throwable) {
        String message = throwable.getMessage();

        if (message.contains(OFFLINE_CODE))
            view.onError("Not connected to the Internet.");
        else if (message.contains(SERVER_DOWN_CODE))
            view.onError("Couldn't connect to server.");
        else if (throwable instanceof SocketTimeoutException)
            view.onError("Request timed out.");
        else
            view.onError("An unexpected error occurred.");
    }

}
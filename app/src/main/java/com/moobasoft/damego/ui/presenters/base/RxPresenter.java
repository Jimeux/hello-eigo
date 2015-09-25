package com.moobasoft.damego.ui.presenters.base;

import com.moobasoft.damego.ui.RxSubscriber;

import java.net.SocketTimeoutException;

import retrofit.HttpException;

public class RxPresenter<V extends RxPresenter.RxView> extends BasePresenter<V> {

    public interface RxView {
        void promptForLogin();
        void onError(String message);
    }

    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int UNAUTHORIZED = 401;
    public static final String OFFLINE_CODE = "ENETUNREACH";
    public static final String SERVER_DOWN_CODE = "ECONNREFUSED";

    protected final RxSubscriber subscriptions;

    public RxPresenter(RxSubscriber subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public void releaseView() {
        super.releaseView();
        subscriptions.clear();
    }

    public static final int OFFLINE_ERROR = 1;
    public static final int SERVER_ERROR = 2;
    public static final int INPUT_ERROR = 3;
    public static final int AUTH_ERROR = 4;
    public static final int UNKNOWN_ERROR = 5;

    protected int getErrorType(Throwable throwable) {
        String message = throwable.getMessage();
        int code = (throwable instanceof HttpException) ?
                ((HttpException) throwable).response().code() : -1;

        if (message.contains(OFFLINE_CODE))
            return OFFLINE_ERROR;
        else if (throwable instanceof SocketTimeoutException || message.contains(SERVER_DOWN_CODE))
            return SERVER_ERROR;
        else if (code == UNPROCESSABLE_ENTITY)
            return INPUT_ERROR;
        else if (code == UNAUTHORIZED)
            return AUTH_ERROR;
        else
            return UNKNOWN_ERROR;
    }

    public void handleError(Throwable throwable) {
        int errorType = getErrorType(throwable);

        switch (errorType) {
            case OFFLINE_ERROR:
                view.onError("Not connected to the Internet.");
                break;
            case SERVER_ERROR:
                view.onError("Couldn't connect to server.");
                break;
            case AUTH_ERROR:
                view.promptForLogin();
                break;
            case UNKNOWN_ERROR:
                view.onError("An unexpected error occurred.");
                break;
        }
    }

}
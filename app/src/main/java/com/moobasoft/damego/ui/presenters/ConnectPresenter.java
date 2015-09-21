package com.moobasoft.damego.ui.presenters;

import com.moobasoft.damego.CredentialStore;
import com.moobasoft.damego.rest.models.AccessToken;
import com.moobasoft.damego.rest.models.User;
import com.moobasoft.damego.rest.requests.UserRequest;
import com.moobasoft.damego.rest.services.UserService;
import com.moobasoft.damego.ui.RxSubscriber;
import com.moobasoft.damego.ui.presenters.base.RxPresenter;

import rx.Observable;

public class ConnectPresenter extends RxPresenter<ConnectPresenter.View> {

    private final UserService userService;
    private final CredentialStore credentialStore;

    public ConnectPresenter(RxSubscriber subscriptions,
                            UserService userService,
                            CredentialStore credentialStore) {
        super(subscriptions);
        this.userService = userService;
        this.credentialStore = credentialStore;
    }

    public void login(String username, String password) {
        subscriptions.add(
                userService.getAccessToken(username, password, "password"),
                accessToken -> {
                    credentialStore.saveToken(accessToken);
                    view.onLoginSuccess();
                },
                this::connectError);
        /*getUserAfterConnect(username,
                userService.getAccessToken(username, password, "password"));*/
    }

    public void register(String email, String username, String password) {
        subscriptions.add(
                userService.register(new UserRequest(email, username, password)),
                accessToken -> {
                    credentialStore.saveToken(accessToken);
                    view.onLoginSuccess();
                },
                this::connectError);
        /*getUserAfterConnect(username, userService
                .register(new UserRequest(email, username, password)));*/
    }

    private void getUserAfterConnect(String username, Observable<AccessToken> observable) {
        /*Observable<User> userObservable = observable.flatMap(accessToken -> {
            credentialStore.saveToken(accessToken);
            return userService.getUser(username);
        });
        subscriptions.add(userObservable, this::connectSuccess, this::connectError);*/
    }

    private void connectSuccess(User user) {
        credentialStore.saveUser(user);
        view.onLoginSuccess();
    }

    private void connectError(Throwable throwable) {
        credentialStore.delete();

        view.onError(throwable.getMessage());
/*
        final int errorStatus = getErrorStatus(throwable);

        if (errorStatus == 401)
            view.onLoginError();
        else if (errorStatus == 422) {
            final Map<String, String> errorMap = getErrorMap(throwable);
            view.onRegistrationError(errorMap.get("username"),
                                     errorMap.get("email"),
                                     errorMap.get("password"));
        } else if (errorStatus == 504)
            view.onError(context.getString(R.string.no_network));
        else
            view.onError(context.getString(R.string.unexpected_error));*/
    }

    public interface View {
        void onLoginSuccess();
        void onRegisterSuccess(String username);
        void onError(String error);
        void onLoginError();
        void onRegistrationError(String username, String email, String password);
    }
}
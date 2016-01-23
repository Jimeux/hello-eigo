package com.moobasoft.helloeigo.ui.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moobasoft.helloeigo.App;
import com.moobasoft.helloeigo.R;
import com.moobasoft.helloeigo.di.components.DaggerMainComponent;
import com.moobasoft.helloeigo.di.modules.MainModule;
import com.moobasoft.helloeigo.events.auth.LoginEvent;
import com.moobasoft.helloeigo.ui.activities.base.BaseActivity;
import com.moobasoft.helloeigo.ui.presenters.ConnectPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ConnectActivity extends BaseActivity implements ConnectPresenter.View {

    public static final String REGISTER = "form";

    @Inject ConnectPresenter presenter;

    @Bind(R.id.progress_bar)    ProgressBar progressBar;
    @Bind(R.id.btn_primary)     Button primaryBtn;
    @Bind(R.id.btn_secondary)   Button secondaryBtn;
    @Bind(R.id.processing_view) ViewGroup processingView;
    @Bind(R.id.email_tv)        TextView emailTv;
    @Bind(R.id.username_tv)     TextView usernameTv;
    @Bind(R.id.password_tv)     TextView passwordTv;
    @Bind(R.id.email_et)        EditText emailEt;
    @Bind(R.id.username_et)     EditText usernameEt;
    @Bind(R.id.password_et)     EditText passwordEt;
    @Bind({R.id.email_et, R.id.username_et, R.id.password_et})
    List<EditText> inputFields;

    @OnClick(R.id.btn_primary)
    public void clickPrimaryButton() {
        clearErrors();
        setProcessing(true);
        //TODO: validation
        if (isLoginForm()) {
            presenter.login(usernameEt.getText().toString(),
                            passwordEt.getText().toString());
        } else {
            presenter.register(emailEt.getText().toString(),
                               usernameEt.getText().toString(),
                               passwordEt.getText().toString());
        }
    }

    @OnClick(R.id.btn_secondary)
    public void clickSecondaryButton() {
        clearErrors();
        switchForms(isLoginForm());
    }

    @OnClick(R.id.btn_close)
    public void clickCloseBtn() { finish(); }

    private void switchForms(boolean loginForm) {
        if (loginForm) {
            emailTv.setVisibility(VISIBLE);
            emailEt.setVisibility(VISIBLE);
            primaryBtn.setText(getString(R.string.register));
            secondaryBtn.setText(getString(R.string.login));
        } else {
            emailTv.setVisibility(GONE);
            emailEt.setVisibility(GONE);
            primaryBtn.setText(getString(R.string.login));
            secondaryBtn.setText(getString(R.string.register));
        }
    }

    private void clearErrors() {
        emailEt.setError(null);
        usernameEt.setError(null);
        passwordEt.setError(null);
    }

    private boolean isLoginForm() {
        return emailEt.getVisibility() == GONE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);
        initialiseInjector();
        presenter.bindView(this);
        boolean isRegisterForm = getIntent().getBooleanExtra(REGISTER, true);
        switchForms(isRegisterForm);

        ViewCompat.setTranslationZ(progressBar, 5);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
    }

    private void initialiseInjector() {
        DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App) getApplication()).getAppComponent())
                .build().inject(this);
    }

    @Override
    protected void onDestroy() {
        presenter.releaseView();
        super.onDestroy();
    }

    @Override
    public void onLoginSuccess() {
        setProcessing(false);
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT)
                .show();
        eventBus.send(new LoginEvent());
        finish();
    }

    @Override
    public void onRegisterSuccess(String username) {
        setProcessing(false);
        Toast.makeText(this, getString(R.string.register_success, username), Toast.LENGTH_LONG).show();
        eventBus.send(new LoginEvent());
        finish();
    }

    @Override
    public void onLoginError() {
        setProcessing(false);
        //FIXME: Shows as a blank white box on API 10
        usernameEt.setError(getString(R.string.login_error));
    }

    @Override
    public void onError(int error) {
        setProcessing(false);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegistrationError(@Nullable String username,
                                    @Nullable String email,
                                    @Nullable String password) {
        setProcessing(false);
        usernameEt.setError(username);
        emailEt.setError(email);
        passwordEt.setError(password);
    }

    private void setProcessing(boolean processing) {
        for (EditText e : inputFields) e.setEnabled(!processing);
        processingView.setVisibility(processing ? VISIBLE : GONE);
        primaryBtn.setVisibility(processing ? GONE : VISIBLE);
    }
}
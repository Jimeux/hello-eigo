package com.moobasoft.damego.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.moobasoft.damego.App;
import com.moobasoft.damego.R;
import com.moobasoft.damego.di.components.DaggerMainComponent;
import com.moobasoft.damego.di.modules.MainModule;
import com.moobasoft.damego.rest.models.Comment;
import com.moobasoft.damego.ui.presenters.CommentPresenter;
import com.moobasoft.damego.util.Util;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateCommentActivity extends AppCompatActivity implements CommentPresenter.View {

    @Inject CommentPresenter presenter;

    @Bind(R.id.toolbar)  Toolbar toolbar;
    @Bind(R.id.input)    EditText input;

    private int postId;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initialiseInjector();
        initialiseSearchInput();
        postId = getIntent().getIntExtra(ShowActivity.POST_ID, -1);
        presenter.bindView(this);
    }

    private void initialiseInjector() {
        DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App) getApplication()).getAppComponent())
                .build().inject(this);
    }

    private void initialiseSearchInput() {
        input.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                input.setError(null);
            }
            @Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            @Override public void afterTextChanged(Editable arg0) {}
        });
        input.setOnFocusChangeListener(
                (v, hasFocus) -> Util.setImeVisibility(hasFocus, input));
        Util.setImeVisibility(true, input);
    }

    @Override
    protected void onDestroy() {
        presenter.releaseView();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_comment:

                String query = input.getText().toString();
                if (query.length() < 3) {
                    input.setError(getString(R.string.comment_error_too_short));
                    input.requestFocus();
                    return false;
                }

                input.clearFocus();
                final String inputText = input.getText().toString();
                presenter.createComment(postId, inputText);
                showProgressDialog();
                return true;
            case R.id.action_destroy_comment:
                new ConfirmCancelDialog().show(getSupportFragmentManager(), "confirmCancel");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showProgressDialog() {
        if (progress == null) {
            progress = ProgressDialog.show(this, null, null);
            progress.setContentView(R.layout.element_loading_vie);
            TextView message = (TextView) progress.findViewById(R.id.message);
            message.setText(getString(R.string.comment_submitting));
        }
        progress.show();
    }

    @Override
    public void onCommentSubmitted(Comment comment) {
        progress.dismiss();
        new ConfirmSubmitDialog()
                .show(getSupportFragmentManager(), "confirmCreated");
    }

    @Override
    public void onCommentError(String message) {
        progress.dismiss();
        Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG).show();
    }

    public static class ConfirmCancelDialog extends DialogFragment {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.destroy_comment_dialog))
                    .setPositiveButton("Yes", (dialog, id) -> {
                        ConfirmCancelDialog.this.getDialog().cancel();
                        getActivity().finish();
                    })
                    .setNegativeButton("No", (dialog, id) ->
                            ConfirmCancelDialog.this.getDialog().cancel())
                    .create();
        }
    }

    public static class ConfirmSubmitDialog extends DialogFragment {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.comment_success))
                    .setPositiveButton("OK", (dialog, id) -> {
                        getActivity().finish();
                        ConfirmSubmitDialog.this.getDialog().cancel();
                    })
                    .create();
        }
    }

}
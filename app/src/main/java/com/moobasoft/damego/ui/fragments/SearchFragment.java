package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.moobasoft.damego.R;
import com.moobasoft.damego.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchFragment extends BaseFragment {

    @Bind(R.id.search_input) EditText  searchInput;
    @Bind(R.id.clear_btn)    View      clearBtn;
    @Bind(R.id.content)      ViewGroup contentView;

    private final TextView.OnEditorActionListener onEditListener = (v, actionId, event) -> {
        String query = searchInput.getText().toString();

        if (actionId != EditorInfo.IME_ACTION_DONE)
            return false;
        else if (query.length() < 2) {
            searchInput.setError(getString(R.string.search_error_length));
            searchInput.requestFocus();
            return false;
        } else {
            searchInput.clearFocus();
            loadResults(query);
            return true;
        }
    };

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            boolean containsText = cs.length() > 0;
            searchInput.setError(null);
            clearBtn.setVisibility(containsText ? View.VISIBLE : View.INVISIBLE);
        }
        @Override public void beforeTextChanged(CharSequence a0, int a1, int a2, int a3) {}
        @Override public void afterTextChanged(Editable arg0) {}
    };

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void setToolbar() {
        if (toolbar != null) {
            toolbar.setTitle("");
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        initialiseSearchInput();
        return view;
    }

    private void initialiseSearchInput() {
        searchInput.setOnEditorActionListener(onEditListener);
        searchInput.addTextChangedListener(textWatcher);
        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (searchInput != null) Util.setImeVisibility(hasFocus, searchInput);
        });
        searchInput.requestFocus();
    }

    @Override
    public void onDestroyView() {
        searchInput.clearFocus();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @OnClick(R.id.clear_btn)
    public void clickClearButton() {
        searchInput.setError(null);
        searchInput.setText("");
        searchInput.requestFocus();
    }

    private void loadResults(String query) {
        TagFragment tagFragment = TagFragment.newInstance(TagFragment.MODE_SEARCH, query);
        getChildFragmentManager().beginTransaction()
                .replace(contentView.getId(), tagFragment)
                .commit();
    }

}
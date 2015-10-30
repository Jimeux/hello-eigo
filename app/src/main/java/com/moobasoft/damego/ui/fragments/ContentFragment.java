package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.activities.IndexActivity;
import com.moobasoft.damego.util.Util;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContentFragment extends Fragment {

    public static final String POST_KEY = "post_key";
    private Post post;

    @Bind(R.id.title)             TextView title;
    @Bind(R.id.body)              TextView body;
    @Bind(R.id.tags)              ViewGroup tags;
    @Bind(R.id.backdrop)          ImageView backdrop;

    public ContentFragment() {}

    public static ContentFragment newInstance(Post post) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putParcelable(POST_KEY, Parcels.wrap(post));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);

        post = Parcels.unwrap(getArguments().getParcelable(POST_KEY)); // TODO: Check and throw error
        title.setText(post.getTitle());
        body.setText(Html.fromHtml(post.getBody().trim().replaceAll("[\n\r]", "")));
        Glide.with(this)
                .load(post.getImageUrl())
                .into(backdrop);
        loadTags();

        return view;
    }

    private void loadTags() {
        Util.insertTags(post, getActivity().getLayoutInflater(), tags, true);
        for (int i = 0; i < tags.getChildCount(); i++) {
            TextView tag = (TextView) tags.getChildAt(i);
            tag.setOnClickListener(v ->
                    ((IndexActivity) getActivity()).onTagClicked(tag.getText().toString()));
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
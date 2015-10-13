package com.moobasoft.damego.util;

import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.activities.IndexActivity;

public class PostUtil {

    public static void insertTags(Post post, LayoutInflater inflater, ViewGroup tags, boolean largeText) {
        float textSize = inflater.getContext().getResources().getDimension(
                largeText ? R.dimen.show_tag_text : R.dimen.main_footer_text
        );
        int tagMargin = (int) inflater.getContext().getResources().getDimension(R.dimen.tag_margin);
        tags.removeAllViews();
        for (String tag : post.getTags()) {
            final TextView tagView = (TextView)
                    inflater.inflate(R.layout.element_tag, tags, false);
            tagView.setText(tag);
            tagView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tagView.setOnClickListener(v -> {
                Intent i = new Intent(inflater.getContext(), IndexActivity.class);
                //i.putExtra(IndexActivity.TAG_NAME, tag); //TODO: Fix
                inflater.getContext().startActivity(i);
            });
            if (largeText) tagView.setPadding(0, 0, tagMargin, 0);
            else           tagView.setPadding(tagMargin, 0, 0, 0);
                tags.addView(tagView);
        }
    }

}

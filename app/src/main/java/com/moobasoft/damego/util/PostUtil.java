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
                largeText ? R.dimen.tag_text_show : R.dimen.index_footer_text
        );
        tags.removeAllViews();
        for (String tag : post.getTags()) {
            final TextView tagView = (TextView)
                    inflater.inflate(R.layout.element_tag, tags, false);
            tagView.setText(tag);
            tagView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tagView.setOnClickListener(v -> {
                Intent i = new Intent(inflater.getContext(), IndexActivity.class);
                i.putExtra(IndexActivity.TAG_NAME, tag);
                inflater.getContext().startActivity(i);
            });
            tags.addView(tagView);
        }
    }

}

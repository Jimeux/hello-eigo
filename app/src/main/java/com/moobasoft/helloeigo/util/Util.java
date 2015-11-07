package com.moobasoft.helloeigo.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.moobasoft.helloeigo.R;
import com.moobasoft.helloeigo.rest.models.Post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    private Util() {}

    @NonNull
    public static String formatShortDate(Date date) {
        // Ugly regex to remove the year from the short date
        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(
                DateFormat.SHORT, Locale.getDefault());
        sdf.applyPattern(
                sdf.toPattern().replaceAll("[^\\p{Alpha}]*y+[^\\p{Alpha}]*", ""));
        return sdf.format(date);
    }

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
            if (largeText) tagView.setPadding(0, 0, tagMargin, 0);
            else           tagView.setPadding(tagMargin, 0, 0, 0);
            tags.addView(tagView);
        }
    }

    public static void setImeVisibility(final boolean visible, final EditText editText) {
        Runnable mShowImeRunnable = new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) editText.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(editText, 0);
            }
            @Override
            public boolean equals(Object o) {
                return o instanceof Runnable &&
                        o.toString().equals(this.toString());
            }
            @Override
            public String toString() {
                return "ime runnable";
            }
        };

        if (visible) {
            editText.post(mShowImeRunnable);
        } else {
            editText.removeCallbacks(mShowImeRunnable);
            InputMethodManager imm = (InputMethodManager) editText.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }

}

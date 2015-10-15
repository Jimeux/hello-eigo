package com.moobasoft.damego.util;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class Animate {
    public static void fadeOut(View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(100);
        fadeOut.setFillAfter(true);
        view.setAnimation(fadeOut);
    }

    public static void fadeIn(final View view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
            @Override public void onAnimationEnd(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
        });
        view.setAnimation(fadeIn);
    }

    public static void slideIn(final View view) {
        TranslateAnimation slideIn = new TranslateAnimation(-view.getWidth(), 0.0f, 0.0f, 0.0f);
        slideIn.setDuration(200);
        slideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
            @Override public void onAnimationEnd(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(slideIn);
    }

    public static void slideOut(View view) {
        TranslateAnimation slideOut = new TranslateAnimation(0.0f, -view.getWidth(), 0.0f, 0.0f);
        slideOut.setDuration(100);
        slideOut.setFillAfter(true);
        view.startAnimation(slideOut);
    }
}
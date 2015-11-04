package com.moobasoft.damego;

import com.moobasoft.damego.ui.presenters.base.Presenter;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {

        Observable<Integer> cache = Observable.just(111).cache();

        Subscription subscription = cache
                .delay(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(System.out::println);

        subscription.unsubscribe();

        cache.subscribe(System.out::println);

    }

    class Testenter extends Presenter {

        private final String name;

        public Testenter(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
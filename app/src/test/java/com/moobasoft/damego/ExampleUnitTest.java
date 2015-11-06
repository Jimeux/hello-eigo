package com.moobasoft.damego;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        BehaviorSubject<String> subject = BehaviorSubject.create();

        /** User loads view and network request begins */
        Observable.just("value")
                .delay(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribe(subject::onNext);

        Subscription portraitSub = subject.subscribe(
                s -> System.out.println("Portrait: " + s));

        /** onDestroy() */
        portraitSub.unsubscribe();

        /** Rotating... */
        Thread.sleep(300);

        /** onRestoreInstanceState **/
        Subscription landscapeSub = subject.subscribe(
                s -> System.out.println("Landscape: " + s));

        /** Output */
        // > Landscape: value
    }

}
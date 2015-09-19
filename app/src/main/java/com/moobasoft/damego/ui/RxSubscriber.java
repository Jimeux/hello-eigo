package com.moobasoft.damego.ui;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class RxSubscriber {

    private final CompositeSubscription subscriptions;
    private final Scheduler subscribeOnScheduler;
    private final Scheduler observeOnScheduler;

    public RxSubscriber(Scheduler subscribeOnScheduler,
                        Scheduler observeOnScheduler) {
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler   = observeOnScheduler;
        this.subscriptions        = new CompositeSubscription();
    }

    private <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public <T> void add(Observable<T> observable,
                        Action1<T> onNext,
                        Action1<Throwable> onError) {
        subscriptions.add(observable
                .compose(applySchedulers())
                .subscribe(onNext, onError));
    }


/*    public <T> void add(Observer<T> observer,
                        Observable<T> observable) {
        subscriptions.add(observable
                .compose(applySchedulers())
                .subscribe(observer));
    }

    public <T> void add(Observable<T> observable) {
        subscriptions.add(observable
                .compose(applySchedulers())
                .subscribe());
    }*/

    public void close() {
        subscriptions.unsubscribe();
    }

}
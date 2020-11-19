package com.judas.katachi.utils.async;

import io.reactivex.rxjava3.core.CompletableTransformer;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.core.MaybeTransformer;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.SingleTransformer;

import static io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.rxjava3.schedulers.Schedulers.io;

public class RxUtils {
    public static <T> ObservableTransformer<T, T> observableSchedulers() {
        return upstream -> upstream
                .onTerminateDetach()
                .subscribeOn(io())
                .observeOn(mainThread());
    }

    public static CompletableTransformer completableSchedulers() {
        return upstream -> upstream
                .onTerminateDetach()
                .subscribeOn(io())
                .observeOn(mainThread());
    }

    public static <T> SingleTransformer<T, T> singleSchedulers() {
        return upstream -> upstream
                .onTerminateDetach()
                .subscribeOn(io())
                .observeOn(mainThread());
    }

    public static <T> MaybeTransformer<T, T> maybeSchedulers() {
        return upstream -> upstream
                .onTerminateDetach()
                .subscribeOn(io())
                .observeOn(mainThread());
    }

    public static <T> FlowableTransformer<T, T> flowableSchedulers() {
        return upstream -> upstream
                .onTerminateDetach()
                .subscribeOn(io())
                .observeOn(mainThread());
    }
}

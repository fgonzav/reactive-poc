package com.globant.reactivepoc.service;

import io.reactivex.SingleEmitter;
import io.reactivex.annotations.NonNull;

public interface SingleOnSubscribe<T> {

    /**
     * Called for each SingleObserver that subscribes.
     * @param emitter the safe emitter instance, never null
     * @throws Exception on error
     */
    void subscribe(@NonNull SingleEmitter<T> emitter) throws Exception;
}

package com.globant.reactivepoc.service;

import com.globant.reactivepoc.domain.Thing;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@Slf4j
public class ThingsService implements InitializingBean {

    private final Map<String, Thing> thingsMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        IntStream.range(1, 10).forEach(
                index -> thingsMap.put("thing" + index, Thing.builder()
                        .id("thing" + index)
                        .value("value" + index)
                        .build()));
    }

    public Single<Thing> findById(final String transactionId, final String id) {
        return Single.create(emitter -> {
            log.debug("{}: Evaluating findById...", transactionId);
            if (thingsMap.containsKey(id)) {
                emitter.onSuccess(thingsMap.get(id));
            } else {
                emitter.onError(new IllegalArgumentException("Thing with id '" + id + "' not found"));
            }
        });
    }

    public Single<Thing> operateById(final String transactionId, final String id) {
        return this.findById(transactionId, id)
                .flatMap(thing -> doCoolOperationsA(transactionId, thing))
                .flatMap(thing -> doCoolOperationsB(transactionId, thing))
                .flatMap(thing -> logById(transactionId, thing));
    }

    public Single<Thing> operateByIdMergingOps(final String transactionId, final String id) {
        Thing thing = this.findById(transactionId, id).blockingGet();
        Single<Thing> operationA = doCoolOperationsA(transactionId, thing).subscribeOn(Schedulers.io());
        Single<Thing> operationB = doCoolOperationsB(transactionId, thing).subscribeOn(Schedulers.io());
        Single<Thing> operationC = logById(transactionId, thing).subscribeOn(Schedulers.io());

        return operationA.mergeWith(operationB).mergeWith(operationC).lastOrError();
    }

    private Single<Thing> logById(final String transactionId, Thing thing) {
        return Single.create(emitter -> {
            log.debug("{}: Evaluating logById...", transactionId);
            log.debug("{}: Logging Id -> {}", transactionId, thing.getId());
            emitter.onSuccess(thing);
        });
    }

    private Single<Thing> doCoolOperationsA(final String transactionId, Thing thing) {
        return Single.create(emitter -> {
            Thread.sleep(2000);
            log.debug("{}: Evaluating doCoolOperationsA...", transactionId);
            emitter.onSuccess(thing);
        });
    }

    private Single<Thing> doCoolOperationsB(final String transactionId, Thing thing) {
        return Single.create(emitter -> {
            Thread.sleep(1000);
            log.debug("{}: Evaluating doCoolOperationsB...", transactionId);
            emitter.onSuccess(thing);
        });
    }
}

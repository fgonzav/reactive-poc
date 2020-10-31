package com.globant.reactivepoc.controller;

import com.globant.reactivepoc.aop.PerformanceLog;
import com.globant.reactivepoc.domain.Thing;
import com.globant.reactivepoc.service.ThingsService;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
public class ThingsController {

    private final ThingsService thingsService;

    public ThingsController(@Autowired ThingsService thingsService) {
        this.thingsService = thingsService;
    }

    @PerformanceLog
    @GetMapping(value = "/api1/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<ResponseEntity<ThingResponse>> getThingById(@PathVariable String id) {
        String transactionId = UUID.randomUUID().toString();
        log.debug("{}: Request received for id: '{}'", transactionId, id);
        Single<Thing> response = thingsService.findById(transactionId, id);
        log.debug("{}: Request for id: '{}' processed.", transactionId, id);

        return response
                .doOnDispose(() -> log.debug("{}: Disposing", transactionId))
                .doOnSubscribe(disposable -> log.debug("{}: Subscribed", transactionId))
                .doOnSuccess(thing -> log.debug("{}: Success", transactionId))
                .doOnTerminate(() -> log.debug("{}: Terminate", transactionId))
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> Thing.builder().value(throwable.getMessage()).build())
                .map(ThingsController::mapThingResponse);
    }

    @PerformanceLog
    @GetMapping(value = "/api2/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<ResponseEntity<ThingResponse>> getAnotherThingById(@PathVariable String id) {
        String transactionId = UUID.randomUUID().toString();
        log.debug("{}: Request received for id: '{}'", transactionId, id);
        Single<Thing> response = thingsService.operateById(transactionId, id);
        log.debug("{}: Request for id: '{}' processed.", transactionId, id);

        return response
                .doOnDispose(() -> log.debug("{}: Disposing", transactionId))
                .doOnSubscribe(disposable -> log.debug("{}: Subscribed", transactionId))
                .doOnSuccess(thing -> log.debug("{}: Success", transactionId))
                .doOnTerminate(() -> log.debug("{}: Terminate", transactionId))
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> Thing.builder().value(throwable.getMessage()).build())
                .map(ThingsController::mapThingResponse);
    }

    @PerformanceLog
    @GetMapping(value = "/api3/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<ResponseEntity<ThingResponse>> getMergedThingById(@PathVariable String id) {
        String transactionId = UUID.randomUUID().toString();
        log.debug("{}: Request received for id: '{}'", transactionId, id);
        Single<Thing> response = thingsService.operateByIdMergingOps(transactionId, id);
        log.debug("{}: Request for id: '{}' processed.", transactionId, id);

        return response
                .doOnDispose(() -> log.debug("{}: Disposing", transactionId))
                .doOnSubscribe(disposable -> log.debug("{}: Subscribed", transactionId))
                .doOnSuccess(thing -> log.debug("{}: Success", transactionId))
                .doOnTerminate(() -> log.debug("{}: Terminate", transactionId))
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> Thing.builder().value(throwable.getMessage()).build())
                .map(ThingsController::mapThingResponse);
    }

    private static ResponseEntity<ThingResponse> mapThingResponse(Thing thing) {
        if (thing.getId() != null) {
            ThingResponse tResponse = ThingResponse.builder().thing(thing).message("OK").build();
            return new ResponseEntity<>(tResponse, HttpStatus.OK);
        } else {
            ThingResponse tResponse = ThingResponse.builder().message(thing.getValue()).build();
            return new ResponseEntity<>(tResponse, HttpStatus.BAD_REQUEST);
        }
    }
}

package com.yeta.reactor;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Part02Mono {

    @Test
    public void empty() {
        Mono<String> mono = emptyMono();
        StepVerifier.create(mono)
                .expectComplete()
                .verify();
    }

    private Mono<String> emptyMono() {
        return Mono.empty();
    }

    @Test
    public void noSignal() {
        Mono<String> mono = monoWithNoSignal();
        StepVerifier
                .create(mono)
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(1))
                .thenCancel()
                .verify();
    }

    private Mono<String> monoWithNoSignal() {
        return Mono.never();
    }

    @Test
    public void fromValue() {
        Mono<String> mono = fooMono();
        StepVerifier.create(mono)
                .expectNext("foo")
                .expectComplete()
                .verify();
    }

    private Mono<String> fooMono() {
        return Mono.just("foo");
    }

    @Test
    public void error() {
        Mono<String> mono = errorMono();
        StepVerifier.create(mono)
                .expectError(IllegalStateException.class)
                .verify();
    }

    private Mono<String> errorMono() {
        return Mono.error(new IllegalStateException());
    }


}

package com.yeta.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;

public class Part01Flux {

    @Test
    public void empty() {
        Flux<String> flux = emptyFlux();

        StepVerifier.create(flux)
                .expectComplete()
                .verify();
    }

    private Flux<String> emptyFlux() {
        return Flux.empty();
    }

    @Test
    public void fromValues() {
        Flux<String> flux = fooBarFluxFromValues();
        StepVerifier.create(flux, 2)
                .expectNext("foo", "bar")
                .expectComplete()
                .verify();
    }

    private Flux<String> fooBarFluxFromValues() {
        return Flux.just("foo","bar");
    }

    @Test
    public void fromList() {
        Flux<String> flux = foobBarFluxFromList();
        StepVerifier.create(flux)
                .expectNext("foo","bar")
                .expectComplete()
                .verify();
    }

    private Flux<String> foobBarFluxFromList() {
        return Flux.fromIterable(Arrays.asList("foo","bar"));
    }

    @Test
    public void error() {
        Flux<String> flux = errorFlux();
        StepVerifier.create(flux)
                .expectError(IllegalStateException.class)
                .verify();
    }

    private Flux<String> errorFlux() {
        return Flux.error(new IllegalStateException());
    }

    @Test
    public void countEach100ms() {
        Flux<Long> flux = counter();
        StepVerifier.create(flux)
                .expectNext(0L, 1L, 2L, 3L)
                .expectComplete()
                .verify();
    }

    private Flux<Long> counter() {
        return Flux.interval(Duration.ofMillis(100)).take(4);
    }
}

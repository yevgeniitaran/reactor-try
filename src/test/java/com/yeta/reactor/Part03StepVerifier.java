package com.yeta.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class Part03StepVerifier {

    @Test
    public void expectsElementsThenComplete() {
        expectFooBarComplete(Flux.just("foo", "bar"));
    }

    private void expectFooBarComplete(Flux<String> flux) {
        StepVerifier.create(flux)
                .expectNext("foo", "bar")
                .expectComplete()
                .verify();
    }

    @Test
    public void expect2ElementsThenError() {
        expectFooBarError(Flux.just("foo", "bar").concatWith(Mono.error(new RuntimeException())));
    }

    private void expectFooBarError(Flux<String> flux) {
        StepVerifier.create(flux)
                .expectNext("foo", "bar")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void expectElementsWithComplete() {
        expectSkylerJesseComplete(Flux.just(new User("swhite", null, null), new User("jpinkman",null, null)));
    }

    private void expectSkylerJesseComplete(Flux<User> flux) {
        StepVerifier.create(flux)
                .expectNextMatches(user -> user.getUsername().equals("swhite"))
                .consumeNextWith(user -> assertEquals("jpinkman", user.getUsername()))
                .expectComplete()
                .verify();
    }

    @Test
    public void count() {
        expect10Elements(Flux.interval(Duration.ofSeconds(1)).take(4));
    }

    private void expect10Elements(Flux<Long> flux) {
        StepVerifier.create(flux)
                .expectNextCount(4)
                .expectComplete()
                .verify();
    }
    
    @Test
    public void countWithVirtualTime() {
        expect3600Elements(() -> Flux.interval(Duration.ofSeconds(1)).take(3600));
    }

    private void expect3600Elements(Supplier<Flux<Long>> supplier) {
        StepVerifier.withVirtualTime(supplier)
                .thenAwait(Duration.ofHours(1))
                .expectNextCount(3600)
                .expectComplete()
                .verify();
    }
}

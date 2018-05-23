package com.yeta.reactor;

import org.junit.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.ConnectException;

public class Part07Errors {

    @Test
    public void monoWithValueInsteadOfError() {
        Mono<User> mono = betterCallSaulForBogusMono(Mono.error(new IllegalStateException()));
        StepVerifier.create(mono)
                .expectNext(User.SAUL)
                .expectComplete()
                .verify();

        mono = betterCallSaulForBogusMono(Mono.just(User.SKYLER));
        StepVerifier.create(mono)
                .expectNext(User.SKYLER)
                .expectComplete()
                .verify();
    }

    private Mono<User> betterCallSaulForBogusMono(Mono<User> mono) {
        return mono.otherwise(e -> Mono.just(User.SAUL));
    }

    @Test
    public void hanleCheckedExceptions() {
        Flux<User> flux = capitalizeMany(Flux.just(User.SAUL, User.JESSE));

        StepVerifier.create(flux)
                .expectError(GetOutOfHereException.class)
                .verify();
    }

    private Flux<User> capitalizeMany(Flux<User> flux) {
        return flux.map(user -> {
            try {
                return capitalizeUser(user);
            } catch (GetOutOfHereException e) {
                throw Exceptions.propagate(e);
            }
        });
    }

    User capitalizeUser(User user) throws  GetOutOfHereException {
        if (user.equals(User.SAUL)) {
            throw new GetOutOfHereException();
        }
        return new User(user.getUsername(), user.getFirstname(), user.getLastname());
    }

    private class GetOutOfHereException extends Exception {

    }
}

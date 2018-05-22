package com.yeta.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Part04Transform {

    ReactiveRepository<User> repository = new ReactiveUserRepository();

    @Test
    public void transformMono() {
        Mono<User> mono = repository.findFirst();
        StepVerifier.create(capitalizeOne(mono))
                .expectNext(new User("SWHITE", "SKYLER", "WHITE"))
                .expectComplete()
                .verify();
    }

    private Mono<User> capitalizeOne(Mono<User> mono) {
        return mono.map(user -> new User(user.getUsername().toUpperCase(), user.getFirstname().toUpperCase(),
                user.getLastname().toUpperCase()));
    }

    @Test
    public void transformFlux() {
        Flux<User> flux = repository.findAll();
        StepVerifier.create(capitalizeMany(flux))
                .expectNext(new User("SWHITE", "SKYLER", "WHITE"))
                .expectNext(new User("JPINKMAN", "JESSE", "PINKMAN"))
                .expectComplete()
                .verify();
    }

    private Flux<User> capitalizeMany(Flux<User> flux) {
        return flux.map(user -> new User(user.getUsername().toUpperCase(), user.getFirstname().toUpperCase(),
                user.getLastname().toUpperCase())).take(2);
    }

    @Test
    public void asyncTransformFlux() {
        Flux<User> flux = repository.findAll();
        StepVerifier.create(asyncCapitalizeMany(flux))
                .expectNext(new User("SWHITE", "SKYLER", "WHITE"))
                .expectNext(new User("JPINKMAN", "JESSE", "PINKMAN"))
                .expectComplete()
                .verify();

    }

    private Flux<User> asyncCapitalizeMany(Flux<User> flux) {
        return flux.flatMap(this::asyncCapitalizeUser).take(2);
    }

    private Mono<User> asyncCapitalizeUser(User user) {
        return Mono.just(new User(user.getUsername().toUpperCase(), user.getFirstname().toUpperCase(),
                user.getLastname().toUpperCase()));
    }

}

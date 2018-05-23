package com.yeta.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class Part06Request {
    
    ReactiveRepository<User> repository = new ReactiveUserRepository();
    
    @Test
    public void requestAll() {
        Flux<User> flux = repository.findAll();
        StepVerifier verifier = requestAllExpectFour(flux);
        verifier.verify();
    }

    private StepVerifier requestAllExpectFour(Flux<User> flux) {
        return StepVerifier.create(flux)
                .expectNextCount(4)
                .expectComplete();
    }

    @Test
    public void requestOneByOne() {
        Flux<User> flux = repository.findAll();
        StepVerifier verifier = requestOneExpectSkylerThenRequestOneExpectJesse(flux);
        verifier.verify();
    }

    private StepVerifier requestOneExpectSkylerThenRequestOneExpectJesse(Flux<User> flux) {
        return StepVerifier.create(flux, 1)
                .expectNext(User.SKYLER)
                .thenRequest(1)
                .expectNext(User.JESSE)
                .thenCancel();
    }

    @Test
    public void experimentWithLog() {
        Flux<User> flux = fluxWithLog();
        StepVerifier.create(flux, 0)
                .thenRequest(1)
                .expectNextMatches(user -> true)
                .thenRequest(1)
                .expectNextMatches(user -> true)
                .thenRequest(2)
                .expectNextMatches(user -> true)
                .expectNextMatches(user -> true)
                .expectComplete()
                .verify();
    }

    private Flux<User> fluxWithLog() {
        return repository.findAll().log();
    }

    @Test
    public void experimentWithDoOn() {
        Flux<User> flux = fluxWithDoOnPrintln();
        StepVerifier.create(flux)
                .expectNextCount(4)
                .expectComplete()
                .verify();
    }

    private Flux<User> fluxWithDoOnPrintln() {
        return repository.findAll()
                .doOnSubscribe(s -> System.out.println("Starring:"))
                .doOnNext(user -> System.out.println(user.getFirstname() + " " + user.getLastname()))
                .doOnComplete(() -> System.out.println("The end!"));
    }
}

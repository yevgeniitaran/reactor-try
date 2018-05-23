package com.yeta.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.yeta.reactor.Part05Merge.MARIE;
import static com.yeta.reactor.Part05Merge.MIKE;

public class Part08OtherOperations {

    @Test
    public void zipFirstNameAndLastName() {
        Flux<String> usernameFlux = Flux.just(User.SKYLER.getUsername(), User.JESSE.getUsername());
        Flux<String> firstnameFlux = Flux.just(User.SKYLER.getFirstname(), User.JESSE.getFirstname());
        Flux<String> lastnameFlux = Flux.just(User.SKYLER.getLastname(), User.JESSE.getLastname());
        Flux<User> userFlux = userFluxFromStringFlux(usernameFlux, firstnameFlux, lastnameFlux);
        StepVerifier.create(userFlux)
                .expectNext(User.SKYLER, User.JESSE)
                .expectComplete()
                .verify();
    }

    private Flux<User> userFluxFromStringFlux(Flux<String> usernameFlux, Flux<String> firstnameFlux, Flux<String> lastnameFlux) {
        return Flux.zip(usernameFlux, firstnameFlux, lastnameFlux)
                .map(t -> new User(t.getT1(), t.getT2(), t.getT3()));
    }

    @Test
    public void fastestMono() {
        ReactiveRepository<User> repository1 = new ReactiveUserRepository(MARIE);
        ReactiveRepository<User> repository2 = new ReactiveUserRepository(250, MIKE);
        Mono<User> mono = useFastestMono(repository1.findFirst(), repository2.findFirst());
        StepVerifier.create(mono)
                .expectNext(MARIE)
                .expectComplete()
                .verify();
    }

    private Mono<User> useFastestMono(Mono<User> mono1, Mono<User> mono2) {
        return Mono.first(mono1, mono2);
    }

    @Test
    public void compete() {
        ReactiveRepository<User> repository = new ReactiveUserRepository();
        Mono<Void> completion = fluxCompletion(repository.findAll());
        StepVerifier.create(completion)
                .expectComplete()
                .verify();
    }

    private Mono<Void> fluxCompletion(Flux<User> flux) {
        return flux.then();
    }

    @Test
    public void nullHandling() {
        Mono<User> mono = nullAwareUserToMono(User.SKYLER);
        StepVerifier.create(mono)
                .expectNext(User.SKYLER)
                .expectComplete()
                .verify();
        mono = nullAwareUserToMono(null);
        StepVerifier.create(mono)
                .expectComplete()
                .verify();
    }

    private Mono<User> nullAwareUserToMono(User user) {
        return Mono.justOrEmpty(user);
    }

    @Test
    public void emptyHandling() {
        Mono<User> mono = emptyToSkyler(Mono.just(User.WALTER));
        StepVerifier.create(mono)
                .expectNext(User.WALTER)
                .expectComplete()
                .verify();
        mono = emptyToSkyler(Mono.empty());
        StepVerifier.create(mono)
                .expectNext(User.SKYLER)
                .expectComplete()
                .verify();
    }

    private Mono<User> emptyToSkyler(Mono<User> mono) {
        return mono.switchIfEmpty(Mono.just(User.SKYLER));
    }
}

package com.yeta.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class Part05Merge {
    
    final static User MARIE = new User("mschrader", "Marie", "Schrader");
    final static User MIKE = new User("mehrmantraut", "Mike", "Ehrmantraut");
    
    ReactiveRepository<User> repository1 = new ReactiveUserRepository(500);
    ReactiveRepository<User> repository2 = new ReactiveUserRepository(MARIE, MIKE);
    
    @Test
    public void mergeWithInterleave() {
        Flux<User> flux = mergeFluxWithInterleave(repository1.findAll(), repository2.findAll());
        StepVerifier.create(flux)
                .expectNext(MARIE, MIKE, User.SKYLER, User.JESSE, User.WALTER, User.SAUL)
                .expectComplete()
                .verify();
    }

    private Flux<User> mergeFluxWithInterleave(Flux<User> flux1, Flux<User> flux2) {
        return Flux.merge(flux1, flux2);
    }

    @Test
    public void mergeWithNoInterleave() {
        Flux<User> flux = mergeFluxWithNoInterleave(repository1.findAll(), repository2.findAll());
        StepVerifier.create(flux)
                .expectNext(User.SKYLER, User.JESSE, User.WALTER, User.SAUL, MARIE, MIKE)
                .expectComplete()
                .verify();
    }

    private Flux<User> mergeFluxWithNoInterleave(Flux<User> flux1, Flux<User> flux2) {
        return Flux.concat(flux1, flux2);
    }
}

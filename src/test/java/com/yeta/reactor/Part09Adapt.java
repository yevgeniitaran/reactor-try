package com.yeta.reactor;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;

public class Part09Adapt {

    ReactiveRepository<User> repository = new ReactiveUserRepository();

    @Test
    public void adaptToCompletableFuture() {
        Mono<User> mono = repository.findFirst();
        CompletableFuture<User> future = fromMonoToCompletableFuture(mono);
        StepVerifier.create(fromCompletableFutureToMono(future))
                .expectNext(User.SKYLER)
                .expectComplete()
                .verify();
    }

    private CompletableFuture<User> fromMonoToCompletableFuture(Mono<User> mono) {
        return mono.toFuture();
    }

    private Mono<User> fromCompletableFutureToMono(CompletableFuture<User> future) {
        return  Mono.fromFuture(future);
    }

}

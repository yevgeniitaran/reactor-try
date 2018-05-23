package com.yeta.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class Part11BlockingToReactive {

    @Test
    public void slowPublisherFastSubscriber() {
        BlockingUserRepository repository = new BlockingUserRepository();
        Flux<User> flux = blockingRepositoryToFlux(repository);
        assertEquals("The call to   findAll must be deferred until the flux is subsribed", 0, repository.getCallCount());
        StepVerifier.create(flux)
                .expectNext(User.SKYLER, User.JESSE, User.WALTER, User.SAUL)
                .expectComplete()
                .verify();
    }

    private Flux<User> blockingRepositoryToFlux(BlockingRepository<User> repository) {
        return Flux.defer(() -> Flux.fromIterable(repository.findAll()).subscribeOn(Schedulers.elastic()));
    }

    @Test
    public void fastPublisherSlowSubscriber() {
        ReactiveRepository<User> reactiveRepository = new ReactiveUserRepository();
        BlockingUserRepository blockingRepository = new BlockingUserRepository(new User[]{});
        Mono<Void> complete = fluxToBlockingRepository(reactiveRepository.findAll(), blockingRepository);
        assertEquals(0, blockingRepository.getCallCount());
        StepVerifier.create(complete)
                .expectComplete()
                .verify();
        Iterator<User> it = blockingRepository.findAll().iterator();
        assertEquals(User.SKYLER, it.next());
    }

    private Mono<Void> fluxToBlockingRepository(Flux<User> flux, BlockingRepository<User> repository) {
        return flux.publishOn(Schedulers.elastic())
                .doOnNext(user -> repository.save(user))
                .then();
    }

    @Test
    public void fluxFindAllTest() {
        ReactiveRepository<User> reactiveRepository = new ReactiveUserRepository();
        Flux<User> flux = reactiveRepository.findAll();
        StepVerifier.create(flux)
                .expectNext(User.SKYLER)
                .expectNext(User.JESSE)
                .expectNext(User.WALTER)
                .expectNext(User.SAUL)
                .expectComplete()
                .verify();
    }
}

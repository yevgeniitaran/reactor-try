package com.yeta.reactor;

import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;

public class Part10ReactiveToBlocking {

    ReactiveRepository<User> repository = new ReactiveUserRepository();

    @Test
    public void mono() {
        Mono<User> mono = repository.findFirst();
        User user = monoToValue(mono);
        assertEquals(User.SKYLER, user);
    }

    private User monoToValue(Mono<User> mono) {
        return mono.block();
    }
}

package com.yeta.reactor;

public interface BlockingRepository<T> {

    void save(T value);

    T findFirst();

    Iterable<T> findAll();

    T findById(String id);
}
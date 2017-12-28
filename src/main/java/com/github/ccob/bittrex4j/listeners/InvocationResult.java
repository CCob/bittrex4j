package com.github.ccob.bittrex4j.listeners;

public interface InvocationResult<T> {
    void complete(T result);
}

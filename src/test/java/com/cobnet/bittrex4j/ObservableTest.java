package com.cobnet.bittrex4j;

import org.junit.Test;

public class ObservableTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenAddingNullObserver() throws Exception {
        Observable<Object> observable = new Observable<>();
        observable.addObserver(null);
    }
}
package com.github.ccob.bittrex4j;

import com.github.ccob.bittrex4j.listeners.Listener;
import org.junit.Test;

public class ObservableTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenAddingNullObserver() throws Exception {
        Observable<Object> observable = new Observable<>();
        observable.addObserver(null);
    }


    @Test
    public void shouldAllowSameObserverTwice() throws Exception {
        Observable<Object> observable = new Observable<>();
        Listener<Object> listener = new Listener<Object>() {
            @Override
            public void onEvent(Object o) {

            }
        };
        observable.addObserver(listener);
        observable.addObserver(listener);
    }
}
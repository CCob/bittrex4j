/*
 * *
 *  This file is part of the bittrex4j project.
 *
 *  @author CCob
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 * /
 */

package com.cobnet.bittrex4j;

import com.cobnet.bittrex4j.listeners.Listener;

import java.util.LinkedList;
import java.util.List;

public class Observable<ObservedType> {

    private List<Listener<ObservedType>> _observers =
            new LinkedList<>();

    public void addObserver(Listener<ObservedType> obs) {
        if (obs == null) {
            throw new IllegalArgumentException("Tried to add a null observer");
        }
        if (_observers.contains(obs)) {
            return;
        }
        _observers.add(obs);
    }

    public void notifyObservers(ObservedType data) {
        for (Listener<ObservedType> obs : _observers) {
            obs.onEvent(data);
        }
    }
}
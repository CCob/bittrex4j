package com.github.ccob.bittrex4j;

import com.github.signalr4j.client.ConnectionState;

/**
 * Connection state change representing the transition between an old state and a new state for the hub connection.
 */
public class ConnectionStateChange {

  private final ConnectionState oldState;
  private final ConnectionState newState;

  public ConnectionStateChange(ConnectionState oldState, ConnectionState newState) {
    this.oldState = oldState;
    this.newState = newState;
  }

  public ConnectionState getOldState() {
    return oldState;
  }

  public ConnectionState getNewState() {
    return newState;
  }
}

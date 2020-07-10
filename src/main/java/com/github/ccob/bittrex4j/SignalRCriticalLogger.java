package com.github.ccob.bittrex4j;

import com.github.signalr4j.client.LogLevel;
import com.github.signalr4j.client.Logger;

public class SignalRCriticalLogger implements Logger {
    org.slf4j.Logger log;

    SignalRCriticalLogger(org.slf4j.Logger log) {
        this.log = log;
    }

    @Override
    public void log(String message, LogLevel level) {
        switch (level){
            case Critical:
                log.error(message);
                break;
        }
    }
}

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

import donky.microsoft.aspnet.signalr.client.LogLevel;
import donky.microsoft.aspnet.signalr.client.Logger;

public class SingalRLoggerDecorator implements Logger {

    org.slf4j.Logger log;

    public SingalRLoggerDecorator(org.slf4j.Logger log) {
        this.log = log;
    }

    @Override
    public void log(String message, LogLevel level) {
        switch (level){
            case Critical:
                log.error(message);
                break;
            case Information:
                log.info(message);
                break;
            case Verbose:
                log.debug(message);
                break;
        }
    }
}

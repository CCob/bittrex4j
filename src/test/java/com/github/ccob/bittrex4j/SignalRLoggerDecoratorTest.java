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

package com.github.ccob.bittrex4j;

import com.github.signalr4j.client.LogLevel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SignalRLoggerDecoratorTest {

    @Mock
    Logger mockLogger;

    SignalRLoggerDecorator signalRLoggerDecorator;

    private static final String INFO_LOG = "info";
    private static final String INFO_ERROR = "error";
    private static final String INFO_DEBUG = "debug";

    @Before
    public void setUp(){
        signalRLoggerDecorator = new SignalRLoggerDecorator(mockLogger);
    }

    @Test
    public void shouldLogInfoLevel(){
        signalRLoggerDecorator.log(INFO_LOG, LogLevel.Information);
        verify(mockLogger).info(INFO_LOG);
    }

    @Test
    public void shouldLogErrorLevel(){
        signalRLoggerDecorator.log(INFO_ERROR, LogLevel.Critical);
        verify(mockLogger).error(INFO_ERROR);
    }

    @Test
    public void shouldLogDebugLevel(){
        signalRLoggerDecorator.log(INFO_DEBUG, LogLevel.Verbose);
        verify(mockLogger).debug(INFO_DEBUG);
    }

}
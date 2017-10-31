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

import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpFactory {

    public HttpClient createClient(){
        return HttpClientBuilder.create().build();
    }

    public HttpClientContext createClientContext(){
        return HttpClientContext.create();
    }
}

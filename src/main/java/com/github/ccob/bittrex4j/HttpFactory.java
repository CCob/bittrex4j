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

import com.github.signalr4j.client.Logger;
import com.github.signalr4j.client.Platform;
import com.github.signalr4j.client.hubs.HubConnection;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClients;

public class HttpFactory {



    public HttpClient createClient(){
        return HttpClients
                .custom()
                //hack, since we cant control the user agent inside the signalr library
                .setUserAgent(Utils.getUserAgent())
                .build();
    }

    public HttpClientContext createClientContext(){
        return HttpClientContext.create();
    }

    public HubConnection createHubConnection(String url, String queryString, boolean useDefaultUrl, Logger logger){ return new HubConnection(url,queryString,useDefaultUrl,logger);}
}

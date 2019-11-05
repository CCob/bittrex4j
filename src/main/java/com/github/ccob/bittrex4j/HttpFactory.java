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

import com.github.ccob.bittrex4j.cloudflare.CloudFlareAuthorizer;
import com.github.signalr4j.client.Logger;
import com.github.signalr4j.client.Platform;
import com.github.signalr4j.client.hubs.HubConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class HttpFactory {
    private static org.slf4j.Logger log = LoggerFactory.getLogger(CloudFlareAuthorizer.class);

    public HttpClient createClient() {

        HttpHost proxy = null;
        HttpRoutePlanner routePlanner = null;

        if (System.getProperty("http.proxyHost") != null) {
            proxy = new HttpHost(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty("http.proxyPort", "3128")));

            log.info("Proxy {}://{}:{} active", proxy.getSchemeName(), proxy.getHostName(), proxy.getPort());


            routePlanner = new DefaultProxyRoutePlanner(proxy) {
                @Override
                public HttpRoute determineRoute(
                        final HttpHost host,
                        final HttpRequest request,
                        final HttpContext context) throws HttpException {

                    log.info("determineRoute: {} {} {}", host.toHostString(), request, context);

                    String hostname = host.getHostName();
                    if (hostname.equals("127.0.0.1") || hostname.equalsIgnoreCase("localhost")) {
                        // Return direct route
                        return new HttpRoute(host);
                    }
                    if (System.getProperty("http.nonProxyHosts", "localhost|127.0.0.1|[::1]").toLowerCase().contains(hostname.toLowerCase())) {
                        // Return direct route
                        return new HttpRoute(host);
                    }

                    return super.determineRoute(host, request, context);
                }
            };
        }

        return HttpClients
                .custom()
                //HACK: since we cant control the user agent inside the signalr library
                .setUserAgent(Utils.getUserAgent())
                .setRoutePlanner(routePlanner)
                .setProxy(proxy)
                .build();
    }

    public HttpClientContext createClientContext(){
        return HttpClientContext.create();
    }

    public HubConnection createHubConnection(String url, String queryString, boolean useDefaultUrl, Logger logger){ return new HubConnection(url,queryString,useDefaultUrl,logger);}
}

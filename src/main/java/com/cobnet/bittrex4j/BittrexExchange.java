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

import com.cobnet.bittrex4j.dao.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.internal.LinkedTreeMap;
import donky.microsoft.aspnet.signalr.client.hubs.HubConnection;
import donky.microsoft.aspnet.signalr.client.hubs.HubProxy;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;

public class BittrexExchange  {

    private static Logger log = LoggerFactory.getLogger(BittrexExchange.class);

    private final String MARKET = "market", MARKETS = "markets", CURRENCY = "currency", CURRENCIES = "currencies", ACCOUNT = "account";
    private String apikey = "";
    private String secret = "";
    private ObjectMapper mapper;
    private HttpClient httpClient;
    private HubConnection hubConnection;
    private HubProxy hubProxy;
    private HttpClientContext httpClientContext;

    public BittrexExchange() throws IOException {
        this(null,null);
    }

    public BittrexExchange(String apikey, String secret) throws IOException {
        this(apikey,secret,new HttpFactory());
    }

    public BittrexExchange(String apikey, String secret, HttpFactory httpFactory) throws IOException {

        this.apikey = apikey;
        this.secret = secret;

        mapper = new ObjectMapper(); // can reuse, share globally
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ZonedDateTime.class, new DateTimeDeserializer());
        mapper.registerModule(module);

        httpClient = httpFactory.createClient();
        httpClientContext = httpFactory.createClientContext();
        HttpResponse response = httpClient.execute(new HttpGet("https://bittrex.com"),httpClientContext);
        log.info("Bittrex Cookies: " + httpClientContext.getCookieStore());

        //connectToWebsocket();

    }

    private void connectToWebsocket() {

        hubConnection = new HubConnection("https://socket.bittrex.com",null,true,
                new SingalRLoggerDecorator(log));

        hubProxy = hubConnection.createHubProxy("CoreHub");

        hubConnection.connected( () -> {
            log.info("Connected");
            //hubProxy.invoke("subscribeToExchangeDeltas","BTC-UBQ").done(result -> {
            //    int bp = 0;
            //});
        });

        hubProxy.on("updateExchangeState", exchangeState -> {

        }, LinkedTreeMap.class);

        hubConnection.error( er -> log.error("Error: " + er.toString()));
        hubConnection.start();
    }

    public Response<MarketSummary> getMarketSummary(String market) {
        return getResponse(new TypeReference<Response<MarketSummary>>(){}, UrlBuilder.v2()
                .withGroup(MARKET)
                .withMethod("getmarketsumary")
                .withArgument("marketname",market));
    }

    public Response<OrdersResult> getMarketOrderBook(String market) {
        return getResponse(new TypeReference<Response<OrdersResult>>(){}, UrlBuilder.v2()
                .withGroup(MARKET)
                .withMethod("getmarketorderbook")
                .withArgument("marketname",market));
    }

    public Response<CompletedOrder[]> getMarketHistory(String market) {
        return getResponse(new TypeReference<Response<CompletedOrder[]>>(){}, UrlBuilder.v2()
                .withGroup(MARKET)
                .withMethod("getmarkethistory")
                .withArgument("marketname",market));
    }

    public Response<MarketSummaryResult[]> getMarketSummaries() {
        return getResponse(new TypeReference<Response<MarketSummaryResult[]>>(){}, UrlBuilder.v2()
                .withGroup(MARKETS)
                .withMethod("getmarketsummaries"));
    }

    public Response<Market[]> getMarkets() {
        return getResponse(new TypeReference<Response<Market[]>>(){}, UrlBuilder.v2()
                .withGroup(MARKETS)
                .withMethod("getmarkets"));
    }

    public Response<Currency[]> getCurrencies() {
        return getResponse(new TypeReference<Response<Currency[]>>(){}, UrlBuilder.v2()
                .withGroup(CURRENCIES)
                .withMethod("getcurrenices"));
    }

    public Response<WalletHealthResult[]> getWalletHealth() {
        return getResponse(new TypeReference<Response<WalletHealthResult[]>>(){}, UrlBuilder.v2()
                .withGroup(CURRENCIES)
                .withMethod("getwallethealth"));
    }

    public Response<CompletedOrder[]> getOrderHistory(String market) {
        return getResponse(new TypeReference<Response<CompletedOrder[]>>(){}, UrlBuilder.v1_1()
                .withApiKey(apikey,secret)
                .withGroup(ACCOUNT)
                .withMethod("getorderhistory")
                .withArgument("marketname",market));
    }

    public Response<Balance[]> getBalances() {
        return getResponse(new TypeReference<Response<Balance[]>>(){}, UrlBuilder.v1_1()
                .withApiKey(apikey,secret)
                .withGroup(ACCOUNT)
                .withMethod("getbalances"));
    }

    public Response<Balance> getBalance(String currency) {
        return getResponse(new TypeReference<Response<Balance>>(){}, UrlBuilder.v1_1()
                .withApiKey(apikey,secret)
                .withGroup(ACCOUNT)
                .withMethod("getbalance")
                .withArgument("currency",currency));
    }

    public Response<UuidResult> buyLimit(String market, double quantity, double rate){
        return getResponse(new TypeReference<Response<UuidResult>>(){}, UrlBuilder.v1_1()
                .withApiKey(apikey,secret)
                .withGroup(MARKET)
                .withMethod("buylimit")
                .withArgument("market",market)
                .withArgument("quantity",Double.toString(quantity))
                .withArgument("rate",Double.toString(rate)));
    }

    public Response<UuidResult> sellLimit(String market, double quantity, double rate){
        return getResponse(new TypeReference<Response<UuidResult>>(){}, UrlBuilder.v1_1()
                .withApiKey(apikey,secret)
                .withGroup(MARKET)
                .withMethod("selllimit")
                .withArgument("market",market)
                .withArgument("quantity",String.format("%f",quantity))
                .withArgument("rate",String.format("%f",rate)));
    }

    public Response<?> cancel(String orderUuid){
        return getResponse(new TypeReference<Response<?>>(){}, UrlBuilder.v1_1()
                .withApiKey(apikey,secret)
                .withGroup(MARKET)
                .withMethod("cancel")
                .withArgument("uuid",orderUuid));
    }

    private <Result> Response<Result> getResponse(TypeReference resultType, UrlBuilder urlBuilder) {
        return getResponseBody(resultType, urlBuilder);
    }

    private <Result> Response<Result> getResponseBody(TypeReference resultType, UrlBuilder urlBuilder) {

        try {
            HttpGet request;
            String url;

            if(urlBuilder.isSecure()) {
                urlBuilder.withArgument("nonce",EncryptionUtility.generateNonce());
                url = urlBuilder.build();
                request = new HttpGet(url);
                request.addHeader("apisign", EncryptionUtility.calculateHash(secret, url, "HmacSHA512")); // Attaches signature as a header
            }else{
                request = new HttpGet(urlBuilder.build());
            }

            request.addHeader("accept", "application/json");
            HttpResponse httpResponse = httpClient.execute(request,httpClientContext);

            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == 200) {
                return mapper.readerFor(resultType).readValue(httpResponse.getEntity().getContent());
            }else{
                return new Response<>(false,httpResponse.getStatusLine().getReasonPhrase(),null);
            }

        } catch (IOException e) {
            return new Response<>(false,e.getMessage(),null);
        }
    }
}

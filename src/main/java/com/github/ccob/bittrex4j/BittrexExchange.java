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

import com.github.ccob.bittrex4j.dao.*;
import com.github.ccob.bittrex4j.listeners.UpdateSummaryStateListener;
import com.github.ccob.bittrex4j.listeners.UpdateExchangeStateListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import donky.microsoft.aspnet.signalr.client.hubs.HubConnection;
import donky.microsoft.aspnet.signalr.client.hubs.HubProxy;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.List;

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
    private HttpFactory httpFactory;

    private Observable<List<UpdateExchangeState>> updateExchangeStateBroker = new Observable<>();
    private Observable<ExchangeSummaryState> exchangeSummaryStateBroker = new Observable<>();

    JavaType updateExchangeStateType;
    JavaType exchangeSummaryStateType;

    public BittrexExchange() throws IOException {
        this(null,null);
    }

    public BittrexExchange(String apikey, String secret) throws IOException {
        this(apikey,secret,new HttpFactory());
    }

    public BittrexExchange(String apikey, String secret, HttpFactory httpFactory) throws IOException {

        this.apikey = apikey;
        this.secret = secret;
        this.httpFactory = httpFactory;

        mapper = new ObjectMapper(); // can reuse, share globally
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ZonedDateTime.class, new DateTimeDeserializer());
        mapper.registerModule(module);

        updateExchangeStateType = mapper.getTypeFactory().constructCollectionType(List.class,UpdateExchangeState.class);
        exchangeSummaryStateType = mapper.getTypeFactory().constructType(ExchangeSummaryState.class);

        httpClient = httpFactory.createClient();
        httpClientContext = httpFactory.createClientContext();
        httpClient.execute(new HttpGet("https://bittrex.com"),httpClientContext);
        log.debug("Bittrex Cookies: " + httpClientContext.getCookieStore());
    }

    public void onUpdateSummaryState(UpdateSummaryStateListener exchangeSummaryState){
        exchangeSummaryStateBroker.addObserver(exchangeSummaryState);
    }

    public void onUpdateExchangeState(UpdateExchangeStateListener listener){
        updateExchangeStateBroker.addObserver(listener);
    }

    private  void registerForEvent(String eventName, JavaType deltasType, Observable broker){
        hubProxy.on(eventName, deltas -> {
            try {
                //TODO: find better way to convert from Gson LinkedTreeMap to Jackson.  This method is inefficient
                broker.notifyObservers(mapper.readerFor(deltasType).readValue(new Gson().toJson(deltas)));
            } catch (IOException e) {
                log.error("Failed to parse response",e);
            }
        }, Object.class);
    }

    public void connectToWebSocket(Runnable connectedHandler) {

        hubConnection = httpFactory.createHubConnection("https://socket.bittrex.com",null,true,
                new SignalRLoggerDecorator(log));

        hubProxy = hubConnection.createHubProxy("CoreHub");
        hubConnection.connected(() -> hubProxy.invoke("subscribeToExchangeDeltas","BTC-UBQ"));
        
        registerForEvent("updateSummaryState", exchangeSummaryStateType,exchangeSummaryStateBroker);
        registerForEvent("updateExchangeState", updateExchangeStateType,updateExchangeStateBroker);

        hubConnection.error( er -> log.error("Error: " + er.toString()));
        hubConnection.start();
    }

    public Response<MarketSummary> getMarketSummary(String market) {
        return getResponse(new TypeReference<Response<MarketSummary>>(){}, UrlBuilder.v2()
                .withGroup(MARKET)
                .withMethod("getmarketsumary")
                .withArgument("marketname",market));
    }

    public Response<MarketOrdersResult> getMarketOrderBook(String market) {
        return getResponse(new TypeReference<Response<MarketOrdersResult>>(){}, UrlBuilder.v2()
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

    public Response<Order> getOrder(String uuid) {
        return getResponse(new TypeReference<Response<Order>>(){}, UrlBuilder.v1_1()
                .withApiKey(apikey,secret)
                .withGroup(ACCOUNT)
                .withMethod("getorder")
                .withArgument("uuid",uuid));
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

            log.debug("Executing HTTP request: {}",request.toString());
            HttpResponse httpResponse = httpClient.execute(request,httpClientContext);

            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == 200) {
                return mapper.readerFor(resultType).readValue(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
            }else{
                log.warn("HTTP request failed with error code {} and reason {}",responseCode,httpResponse.getStatusLine().getReasonPhrase());
                return new Response<>(false,httpResponse.getStatusLine().getReasonPhrase(),null);
            }

        } catch (NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            return new Response<>(false,e.getMessage(),null);
        }
    }
}

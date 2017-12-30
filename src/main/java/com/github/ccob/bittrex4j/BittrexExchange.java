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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.ccob.bittrex4j.cloudflare.CloudFlareAuthorizer;
import com.github.ccob.bittrex4j.dao.*;
import com.github.ccob.bittrex4j.listeners.InvocationResult;
import com.github.ccob.bittrex4j.listeners.UpdateExchangeStateListener;
import com.github.ccob.bittrex4j.listeners.UpdateSummaryStateListener;
import com.github.signalr4j.client.Platform;
import com.github.signalr4j.client.hubs.HubConnection;
import com.github.signalr4j.client.hubs.HubProxy;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class BittrexExchange  {

    public enum Interval{
        oneMin,
        fiveMin,
        thirtyMin,
        hour,
        day
    }

    private static Logger log = LoggerFactory.getLogger(BittrexExchange.class);
    private static Logger log_sockets = LoggerFactory.getLogger(BittrexExchange.class.getName().concat(".WebSockets"));

    private final String MARKET = "market", MARKETS = "markets", CURRENCY = "currency", CURRENCIES = "currencies", ACCOUNT = "account";
    private String apikey = "";
    private String secret = "";
    private ObjectMapper mapper;
    private HttpClient httpClient;
    private HubConnection hubConnection;
    private HubProxy hubProxy;
    private HttpClientContext httpClientContext;
    private HttpFactory httpFactory;
    private List<String> marketSubscriptions = new ArrayList<>();
    private Observable<UpdateExchangeState> updateExchangeStateBroker = new Observable<>();
    private Observable<ExchangeSummaryState> exchangeSummaryStateBroker = new Observable<>();
    private Runnable connectedHandler;

    private JavaType updateExchangeStateType;
    private JavaType exchangeSummaryStateType;

    private Timer reconnectTimer = new Timer();

    private int retries;

    private class ReconnectTimerTask extends TimerTask{
        @Override
        public void run() {
            startConnection();
        }
    }

    public BittrexExchange() throws IOException {
        this(5);
    }

    public BittrexExchange(String apikey, String secret) throws IOException {
        this(5,apikey,secret,new HttpFactory());
    }

    public BittrexExchange(int retries) throws IOException {
        this(retries,null,null);
    }

    public BittrexExchange(int retries, String apikey, String secret) throws IOException {
        this(retries,apikey,secret,new HttpFactory());
    }

    public BittrexExchange(int retries, String apikey, String secret, HttpFactory httpFactory) throws IOException {

        this.apikey = apikey;
        this.secret = secret;
        this.httpFactory = httpFactory;
        this.retries = retries;

        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ZonedDateTime.class, new DateTimeDeserializer());
        mapper.registerModule(module);

        updateExchangeStateType = mapper.getTypeFactory().constructType(UpdateExchangeState.class);
        exchangeSummaryStateType = mapper.getTypeFactory().constructType(ExchangeSummaryState.class);

        httpClient = httpFactory.createClient();
        httpClientContext = httpFactory.createClientContext();
    }

    private void performCloudFlareAuthorization() throws IOException {

        try {
            httpClientContext = httpFactory.createClientContext();
            CloudFlareAuthorizer cloudFlareAuthorizer = new CloudFlareAuthorizer(httpClient,httpClientContext);
            cloudFlareAuthorizer.getAuthorizationResult("https://bittrex.com");
        } catch (ScriptException e) {
            log.error("Failed to perform CloudFlare authorization",e);
        }
    }

    private void prepareHubConnectionForCloudFlare(){
        String cookies = httpClientContext.getCookieStore().getCookies()
                .stream()
                .map(cookie -> String.format("%s=%s", cookie.getName(), cookie.getValue()))
                .collect(Collectors.joining(";"));

        hubConnection.getHeaders().put("Cookie",cookies);
        hubConnection.getHeaders().put(HttpHeaders.USER_AGENT, Platform.getUserAgent());
    }

    @Override
    protected void finalize() throws Throwable {
        disconnectFromWebSocket();
        super.finalize();
    }

    public void onUpdateSummaryState(UpdateSummaryStateListener exchangeSummaryState){
        exchangeSummaryStateBroker.addObserver(exchangeSummaryState);
    }

    public void onUpdateExchangeState(UpdateExchangeStateListener listener){
        updateExchangeStateBroker.addObserver(listener);
    }

    @SuppressWarnings("unchecked")
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


    public void subscribeToExchangeDeltas(String marketName, InvocationResult<Boolean> invocationResult) {
        hubProxy.invoke(Boolean.class,"subscribeToExchangeDeltas", marketName)
                .done(result -> {
                    marketSubscriptions.add(marketName);
                    if(invocationResult != null) {
                        invocationResult.complete(result);
                    }
                });
    }

    public void subscribeToMarketSummaries(InvocationResult<Boolean> invocationResult) {
        hubProxy.invoke(Boolean.class, "SubscribeToSummaryDeltas")
                .done(result -> {if(invocationResult != null) invocationResult.complete(result);});
    }

    public void queryExchangeState(String marketName,UpdateExchangeStateListener updateExchangeStateListener){
        hubProxy.invoke(LinkedTreeMap.class,"queryExchangeState",marketName)
                .done(exchangeState -> updateExchangeStateListener
                        .onEvent(mapper.readerFor(updateExchangeStateType).readValue(new Gson().toJson(exchangeState))));
    }

    public void disconnectFromWebSocket(){
        hubConnection.stop();
    }

    private void startConnection(){
        try {

            hubConnection = httpFactory.createHubConnection("https://socket.bittrex.com",null,true,
                    new SignalRLoggerDecorator(log_sockets));

            hubProxy = hubConnection.createHubProxy("CoreHub");
            hubConnection.connected(connectedHandler);

            registerForEvent("updateSummaryState", exchangeSummaryStateType,exchangeSummaryStateBroker);
            registerForEvent("updateExchangeState", updateExchangeStateType,updateExchangeStateBroker);

            setupErrorHandler();

            performCloudFlareAuthorization();
            prepareHubConnectionForCloudFlare();

            hubConnection.start();

        } catch (IOException e) {
            if(log.isDebugEnabled()){
                log.error("Failed to perform CloudFlare authorization on startup", e);
            } else {
                log.error("Failed to perform CloudFlare authorization on startup: {}", e.getMessage());
            }
            reconnectTimer.schedule(new ReconnectTimerTask(),5000);
        }
    }

    private void setupErrorHandler(){
        hubConnection.error( er -> {
            //we must clear this error handler in case another error arrives on the
            //same hubConnection causing multiple reconnect timers to fire
            hubConnection.error(null);
            reconnectTimer.schedule(new ReconnectTimerTask(),5000);
            log.error("Error: " + er.toString() + ", attempting reconnect in 5 seconds");
        });
    }

    public void connectToWebSocket(Runnable connectedHandler) throws IOException {
        this.connectedHandler = connectedHandler;
        startConnection();
    }

    public Response<Tick[]> getTicks(String market, Interval tickInterval){
        return getResponse(new TypeReference<Response<Tick[]>>(){}, UrlBuilder.v2()
                .withGroup(MARKET)
                .withMethod("getticks")
                .withArgument("marketname",market)
                .withArgument("tickInterval",tickInterval.toString()));
    }

    public Response<Tick[]> getLatestTick(String market, Interval tickInterval){
        return getResponse(new TypeReference<Response<Tick[]>>(){}, UrlBuilder.v2()
                .withGroup(MARKET)
                .withMethod("getlatesttick")
                .withArgument("marketname",market)
                .withArgument("tickInterval",tickInterval.toString()));
    }

    public Response<MarketSummary> getMarketSummary(String market) {
        return getResponse(new TypeReference<Response<MarketSummary>>(){}, UrlBuilder.v2()
                .withGroup(MARKET)
                .withMethod("getmarketsummary")
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

    public Response<DepositAddress> getDepositAddress(String currency) {
        return getResponse(new TypeReference<Response<DepositAddress>>(){}, UrlBuilder.v1_1()
                .withApiKey(apikey,secret)
                .withGroup(ACCOUNT)
                .withMethod("getdepositaddress")
                .withArgument("currency",currency));
    }

    public Response<WithdrawalDeposit[]> getWithdrawalHistory(String currency) {
        return getResponse(new TypeReference<Response<WithdrawalDeposit[]>>(){}, UrlBuilder.v1_1()
                .withApiKey(apikey,secret)
                .withGroup(ACCOUNT)
                .withMethod("getwithdrawalhistory")
                .withArgument("currency",currency));
    }

    public Response<WithdrawalDeposit[]> getDepositHistory(String currency) {
        return getResponse(new TypeReference<Response<WithdrawalDeposit[]>>(){}, UrlBuilder.v1_1()
                .withApiKey(apikey,secret)
                .withGroup(ACCOUNT)
                .withMethod("getdeposithistory")
                .withArgument("currency",currency));
    }

    public Response<UuidResult> withdraw(String currency, double quantity, String address) {
        return getResponse(new TypeReference<Response<UuidResult>>(){}, UrlBuilder.v1_1()
                .withApiKey(apikey,secret)
                .withGroup(ACCOUNT)
                .withMethod("withdraw")
                .withArgument("currency",currency)
                .withArgument("quantity", BigDecimal.valueOf(quantity).toString())
                .withArgument("address",address));
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

        int triesLeft = retries;
        Response<Result> result = getResponseBody(resultType, urlBuilder);

        while(!result.isSuccess() && triesLeft-- > 0){
            log.warn("Request to URL {} failed with error {}, retries left: {}",urlBuilder.build(),result.getMessage(),triesLeft);
            result = getResponseBody(resultType, urlBuilder);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        return result;
    }

    private <Result> Response<Result> getResponseBody(TypeReference resultType, UrlBuilder urlBuilder) {

        CloseableHttpResponse httpResponse = null;

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
            httpResponse = (CloseableHttpResponse)httpClient.execute(request,httpClientContext);

            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == 200) {
                return mapper.readerFor(resultType).readValue(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
            }else{
                log.warn("HTTP request failed with error code {} and reason {}",responseCode,httpResponse.getStatusLine().getReasonPhrase());
                return new Response<>(false,httpResponse.getStatusLine().getReasonPhrase(),null);
            }

        } catch (NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            return new Response<>(false,e.getMessage(),null);
        } finally {

            if(httpResponse != null){
                try {
                    httpResponse.getEntity().getContent().close();
                    httpResponse.close();
                } catch (IOException e) {
                    log.debug("Failed to cleanup HttpResponse",e);
                }
            }
        }
    }
}

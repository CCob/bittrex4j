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
import com.github.ccob.bittrex4j.dao.OrderBook.TYPE;
import com.github.signalr4j.client.hubs.*;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BittrexExchangeTest {

    @Mock
    private
    HttpClient mockHttpClient;

    @Mock
    private
    HttpClientContext mockHttpClientContext;

    @Mock
    private HubConnection mockHubConnection;

    @Mock
    private HubProxy mockHubProxy;

    @Mock
    private HttpFactory mockHttpFactory;

    @Captor
    private ArgumentCaptor<SubscriptionHandler1<Object>> subscriptionHandlerArgumentCaptor;

    @Captor
    private ArgumentCaptor<SubscriptionHandler1<String>> subscriptionHandlerArgumentCaptorString;

    private static final String NOT_FOUND = "Not Found";
    private static final String IO_ERROR = "IO Error";
    private static final String STATUS_OK_TEXT = "OK";
    private BittrexExchange bittrexExchange;
    private boolean lambdaCalled;

    private HttpResponse createResponse(int httpStatus, String statusText, String responseText){

        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);

        BasicHttpEntity httpEntity = new BasicHttpEntity();
        httpEntity.setContent(new ByteArrayInputStream(responseText.getBytes()));

        when(response.getStatusLine()).thenReturn(new BasicStatusLine(
                new ProtocolVersion("HTTP", 1, 1), httpStatus, statusText));

        when(response.getEntity()).thenReturn(httpEntity);

        when(response.toString()).thenReturn("MockClosableHttpResponse");

        return response;
    }

    private String loadTestResourceAsString(String resourceName){
        return new Scanner(getClass().getResourceAsStream(resourceName), "UTF-8")
                .useDelimiter("\\A")
                .next();
    }

    @Before
    public void setUp() throws IOException {

        when(mockHubConnection.createHubProxy("c2")).thenReturn(mockHubProxy);

        HttpResponse mockResponse = createResponse(HttpStatus.SC_OK,STATUS_OK_TEXT,"Bittrex");

        when(mockHttpClientContext.getCookieStore()).thenReturn(new BasicCookieStore());
        when(mockHttpClient.execute(argThat(UrlMatcher.matchesUrl("https://bittrex.com")),eq(mockHttpClientContext)))
                .thenReturn(mockResponse);

        when(mockHttpFactory.createClient()).thenReturn(mockHttpClient);
        when(mockHttpFactory.createClientContext()).thenReturn(mockHttpClientContext);
        when(mockHttpFactory.createHubConnection(any(),any(),anyBoolean(),any())).thenReturn(mockHubConnection);

        bittrexExchange = new BittrexExchange(0,"apikey","secret", mockHttpFactory);
        lambdaCalled = false;
    }

    private void setExpectationForHubConnectionSuccess(){
        ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockHubConnection).connected(runnableArgumentCaptor.capture());
        runnableArgumentCaptor.getValue().run();
    }

    private void setExpectationForNon2xxErrorOnWebAPICall() throws IOException {
        HttpResponse mockResponse = createResponse(HttpStatus.SC_NOT_FOUND,NOT_FOUND,NOT_FOUND);
        when(mockHttpClient.execute(any(HttpGet.class),eq(mockHttpClientContext)))
                .thenReturn(mockResponse);
    }

    private void setExpectationForExceptionOnWebAPICall() throws IOException {
        when(mockHttpClient.execute(any(HttpGet.class),eq(mockHttpClientContext)))
                .thenThrow(new IOException(IO_ERROR));
    }

    private void setExpectationForJsonResultOnWebAPICall(String jsonResult) throws IOException {
        HttpResponse mockResponse = createResponse(HttpStatus.SC_OK,STATUS_OK_TEXT,jsonResult);
        when(mockHttpClient.execute(any(HttpGet.class),eq(mockHttpClientContext)))
                .thenReturn(mockResponse);
    }

    @Test
    public void shouldConstructAndSetupCookies(){
    }

    @Test
    public void shouldReturnErrorResultOnNon2xxCode() throws IOException {
        setExpectationForNon2xxErrorOnWebAPICall();
        Response<MarketSummaryResult[]> result = bittrexExchange.getMarketSummaries();

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getMessage(), equalTo(NOT_FOUND));
    }

    @Test
    public void shouldReturnErrorResultOnException() throws IOException {
        setExpectationForExceptionOnWebAPICall();
        Response<MarketSummaryResult[]> result = bittrexExchange.getMarketSummaries();

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getMessage(), equalTo(IO_ERROR));
    }

    @Test
    public void shouldReturnTicks() throws IOException {
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/GetTicks.json"));
        Response<Tick[]> result = bittrexExchange.getTicks("ANY", BittrexExchange.Interval.fiveMin);

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length, equalTo(5));
    }

    @Test
    public void shouldReturnLatestTick() throws IOException {
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/GetLatestTick.json"));
        Response<Tick[]> result = bittrexExchange.getLatestTick("ANY", BittrexExchange.Interval.fiveMin);

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length, equalTo(1));
    }

    @Test
    public void shouldReturnMarketHistory() throws IOException {
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/MarketHistory.json"));
        Response<CompletedOrder[]> result = bittrexExchange.getMarketHistory("ANY");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length, equalTo(4));
    }

    @Test
    public void shouldReturnMarkets() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/Markets.json"));
        Response<Market[]> result = bittrexExchange.getMarkets();

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length,equalTo(2));
    }

    @Test
    public void shouldReturnCurrencies() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/Currencies.json"));
        Response<Currency[]> result = bittrexExchange.getCurrencies();

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length,equalTo(2));
    }

    @Test
    public void shouldReturnTicker() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/Ticker.json"));
        Response<Ticker> result = bittrexExchange.getTicker("ANY");

        assertThat(result.isSuccess(), is(true));
        assertEquals(0, new BigDecimal("0.0483").compareTo(result.getResult().getAsk()));
    }

    @Test
    public void shouldReturnMarketSummaries() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/MarketSummaries.json"));
        Response<MarketSummaryResult[]> result = bittrexExchange.getMarketSummaries();

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length,equalTo(263));
    }

    @Test
    public void shouldReturnMarketSummary() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/MarketSummary.json"));
        Response<MarketSummary> result = bittrexExchange.getMarketSummary("ANY");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getMarketName(), equalTo("BTC-ETH"));
    }

    @Test
    public void shouldReturnOrderBook() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/OrderBook.json"));
        Response<OrderBook> result = (Response<OrderBook>) bittrexExchange.getOrderBook("ANY", TYPE.both);

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getBuy().size(), equalTo(100));
        assertThat(result.getResult().getSell().size(), equalTo(100));

        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/OrderBook-buy.json"));
        Response<OrderBookEntry[]> resultBuy = (Response<OrderBookEntry[]>) bittrexExchange.getOrderBook("ANY", TYPE.buy);


        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getBuy().size(), equalTo(100));
        assertThat(result.getResult().getSell().size(), equalTo(100));
    }

    @Test
    public void shouldReturnMarketOrderBook() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/MarketOrderBook.json"));
        Response<MarketOrdersResult> result = bittrexExchange.getMarketOrderBook("BTC-UBQ");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getBuys().length, equalTo(3));
        assertThat(result.getResult().getSells().length, equalTo(4));
    }

    @Test
    public void shouldReturnWalletHealth() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/WalletHealth.json"));
        Response<WalletHealthResult[]> result = bittrexExchange.getWalletHealth();

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length, equalTo(282));
    }

    @Test
    public void shouldReturnOrderHistory() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/OpenOrders.json"));
        Response<Order[]> result = bittrexExchange.getOrderHistory("ANY");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length, equalTo(3));
    }

    @Test
    public void shouldReturnOpenOrders() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/OpenOrders.json"));
        Response<Order[]> result = bittrexExchange.getOpenOrders("ANY");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length, equalTo(3));

        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/OpenOrders.json"));
        result = bittrexExchange.getOpenOrders();

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length, equalTo(3));
    }

    @Test
    public void shouldReturnBalances() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/Balances.json"));
        Response<Balance[]> result = bittrexExchange.getBalances();

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length, equalTo(2));
    }

    @Test
    public void shouldReturnBalance() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/Balance.json"));
        Response<Balance> result = bittrexExchange.getBalance("BTC-ETH");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getCurrency(), equalTo("BTC"));
    }

    @Test
    public void shouldReturnOrderIdOnBuyLimit() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/BuySellLimit.json"));
        Response<UuidResult> result = bittrexExchange.buyLimit("BTC-ETH",1,0.5);

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getUuid(), equalTo("e606d53c-8d70-11e3-94b5-425861b86ab6"));
    }

    @Test
    public void shouldReturnOrderIdOnSellLimit() throws IOException {
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/BuySellLimit.json"));
        Response<UuidResult> result = bittrexExchange.sellLimit("BTC-ETH", 1, 0.5);

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getUuid(), equalTo("e606d53c-8d70-11e3-94b5-425861b86ab6"));
    }

    @Test
    public void shouldReturnDepositAddress() throws IOException {
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/DepositAddress.json"));
        Response<DepositAddress> result = bittrexExchange.getDepositAddress("BTC");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getCurrency(), equalTo("VTC"));
        assertThat(result.getResult().getAddress(), equalTo("Vy5SKeKGXUHKS2WVpJ76HYuKAu3URastUo"));
    }

    @Test
    public void shouldReturnWithdrawalHistory() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/WithdrawalHistory.json"));
        Response<WithdrawalDeposit[]> result = bittrexExchange.getWithdrawalHistory("BTC");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length, equalTo(2));
    }

    @Test
    public void shouldReturnDepositHistory() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/DepositHistory.json"));
        Response<WithdrawalDeposit[]> result = bittrexExchange.getDepositHistory("BTC");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length, equalTo(2));
    }

    @Test
    public void shouldReturnIdOnWithdrawal() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/Withdrawal.json"));
        Response<UuidResult> result = bittrexExchange.withdraw("currency",1.0,"address");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getUuid(), equalTo("68b5a16c-92de-11e3-ba3b-425861b86ab6"));
    }

    @Test
    public void shouldReturnTrueOnCancel() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/Cancel.json"));
        Response<?> result = bittrexExchange.cancel("e606d53c-8d70-11e3-94b5-425861b86ab6");

        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void shouldReturnOrder() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/Order.json"));
        Response<Order> result = bittrexExchange.getOrder("e606d53c-8d70-11e3-94b5-425861b86ab6");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getOrderUuid(), equalTo("e606d53c-8d70-11e3-94b5-425861b86ab6"));
    }


    @Test
    public void shouldParseUpdateSummaryState() throws IOException{

        bittrexExchange.connectToWebSocket(() -> System.out.println("Connected"));

        verify(mockHubProxy).on(eq("uS"),subscriptionHandlerArgumentCaptorString.capture(),eq(String.class));

        bittrexExchange.onUpdateSummaryState(exchangeSummaryState -> {
            assertThat(exchangeSummaryState.getNounce(), equalTo(2458L));
            assertThat(exchangeSummaryState.getDeltas().length, equalTo(77));
            lambdaCalled=true;
        });

        subscriptionHandlerArgumentCaptorString.getValue().run(loadTestResourceAsString("/UpdateSummaryState.json"));
        assertThat(lambdaCalled,is(true));
    }


    @Test
    public void shouldParseUpdateExchangeState() throws IOException {

        bittrexExchange.connectToWebSocket(() -> System.out.println("Connected"));

        verify(mockHubProxy).on(eq("uE"), subscriptionHandlerArgumentCaptorString.capture(), eq(String.class));

        bittrexExchange.onUpdateExchangeState(updateExchangeState -> {
            assertThat(updateExchangeState.getNounce(), equalTo(34940L));
            assertThat(updateExchangeState.getBuys().length, equalTo(7));
            assertThat(updateExchangeState.getSells().length, equalTo(8));
            assertThat(updateExchangeState.getFills().length, equalTo(0));
            lambdaCalled=true;
        });

        subscriptionHandlerArgumentCaptorString.getValue().run(loadTestResourceAsString("/UpdateExchangeState.json"));
        assertThat(lambdaCalled,is(true));
    }

    @Test
    public void shouldParseOrderDelta() throws IOException{

        bittrexExchange.connectToWebSocket(() -> System.out.println("Connected"));

        verify(mockHubProxy).on(eq("uO"), subscriptionHandlerArgumentCaptorString.capture(), eq(String.class));

        bittrexExchange.onOrderStateChange(orderDelta -> {
            assertThat(orderDelta.getNonce(), equalTo(3));
            assertThat(orderDelta.getAccountUuid(),equalTo("74855331-e517-4ae7-bcf6-c5992c2db2ff"));
            assertThat(orderDelta.getOrder(),is(notNullValue()));
            assertThat(orderDelta.getOrder().getExchange(),equalTo("BTC-ETH"));
            lambdaCalled=true;
        });

        subscriptionHandlerArgumentCaptorString.getValue().run(loadTestResourceAsString("/OrderDelta.json"));
        assertThat(lambdaCalled,is(true));

    }
}

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

import com.cobnet.bittrex4j.BittrexExchange;
import com.cobnet.bittrex4j.HttpFactory;
import com.cobnet.bittrex4j.dao.*;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
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
    private
    HttpFactory mockHttpFactory;

    private static final String NOT_FOUND = "Not Found";
    private static final String IO_ERROR = "IO Error";
    private static final String STATUS_OK_TEXT = "OK";
    private BittrexExchange bittrexExchange;

    private HttpResponse createResponse(int httpStatus, String statusText, String responseText){

        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(
                new ProtocolVersion("HTTP",1,1),httpStatus,statusText));

        BasicHttpEntity httpEntity = new BasicHttpEntity();
        httpEntity.setContent(new ByteArrayInputStream(responseText.getBytes()));

        response.setEntity(httpEntity);
        return response;
    }

    private String loadTestResourceAsString(String resourceName){
        return new Scanner(getClass().getResourceAsStream(resourceName), "UTF-8")
                .useDelimiter("\\A")
                .next();
    }

    @Before
    public void setUp() throws IOException {

        when(mockHttpClientContext.getCookieStore()).thenReturn(new BasicCookieStore());
        when(mockHttpClient.execute(argThat(UrlMatcher.matchesUrl("https://bittrex.com")),eq(mockHttpClientContext)))
                .thenReturn(createResponse(HttpStatus.SC_OK,STATUS_OK_TEXT,"Bittrex"));

        when(mockHttpFactory.createClient()).thenReturn(mockHttpClient);
        when(mockHttpFactory.createClientContext()).thenReturn(mockHttpClientContext);

        bittrexExchange = new BittrexExchange("apikey","secret", mockHttpFactory);
    }

    private void setExpectationForNon2xxErrorOnWebAPICall() throws IOException {
        when(mockHttpClient.execute(any(HttpGet.class),eq(mockHttpClientContext)))
                .thenReturn(createResponse(HttpStatus.SC_NOT_FOUND,NOT_FOUND,NOT_FOUND));
    }

    private void setExpectationForExceptionOnWebAPICall() throws IOException {
        when(mockHttpClient.execute(any(HttpGet.class),eq(mockHttpClientContext)))
                .thenThrow(new IOException(IO_ERROR));
    }

    private void setExpectationForJsonResultOnWebAPICall(String jsonResult) throws IOException {
        when(mockHttpClient.execute(any(HttpGet.class),eq(mockHttpClientContext)))
                .thenReturn(createResponse(HttpStatus.SC_OK,STATUS_OK_TEXT,jsonResult));
    }

    @Test
    public void shouldConstructAndSetupCookies(){
    }

    @Test
    public void shouldReturnErrorResultOnNon2xxCode() throws IOException {
        setExpectationForNon2xxErrorOnWebAPICall();
        Response result = bittrexExchange.getMarkets();

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getMessage(), equalTo(NOT_FOUND));
    }

    @Test
    public void shouldReturnErrorResultOnException() throws IOException {
        setExpectationForExceptionOnWebAPICall();
        Response result = bittrexExchange.getMarkets();

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getMessage(), equalTo(IO_ERROR));
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
    public void shouldReturnMarketOrderBook() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/MarketOrderBook.json"));
        Response<OrdersResult> result = bittrexExchange.getMarketOrderBook("BTC-UBQ");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getBuys().length, equalTo(757));
        assertThat(result.getResult().getSells().length, equalTo(4454));
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
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/OrderHistory.json"));
        Response<CompletedOrder[]> result = bittrexExchange.getOrderHistory("ANY");

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().length, equalTo(2));
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
    public void shouldReturnOrderIdOnSellLimit() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/BuySellLimit.json"));
        Response<UuidResult> result = bittrexExchange.sellLimit("BTC-ETH",1,0.5);

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getResult().getUuid(), equalTo("e606d53c-8d70-11e3-94b5-425861b86ab6"));
    }

    @Test
    public void shouldReturnTrueOnCancel() throws IOException{
        setExpectationForJsonResultOnWebAPICall(loadTestResourceAsString("/Cancel.json"));
        Response<?> result = bittrexExchange.cancel("e606d53c-8d70-11e3-94b5-425861b86ab6");

        assertThat(result.isSuccess(), is(true));
    }
}

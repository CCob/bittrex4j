package com.github.ccob.bittrex4j.cloudflare;

import com.github.ccob.bittrex4j.PatternStreamer;
import com.github.ccob.bittrex4j.Utils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CloudFlareAuthorizer {

    private static Logger log = LoggerFactory.getLogger(CloudFlareAuthorizer.class);

    private CloseableHttpClient httpClient;
    private HttpClientContext httpClientContext;
    private Pattern jsChallenge = Pattern.compile("name=\"jschl_vc\" value=\"(.+?)\"");
    private Pattern password = Pattern.compile("name=\"pass\" value=\"(.+?)\"");
    private Pattern jsScript = Pattern.compile("var s,t,o,p,b,r,e,a,k,i,n,g,f, (.+?)=\\{\"(.+?)\"");
    private ScriptEngineManager engineManager = new ScriptEngineManager();
    private ScriptEngine engine = engineManager.getEngineByName("nashorn");

    private static class Response{
        private int httpStatus;
        private String responseText;

        public Response(int httpStatus, String responseText) {
            this.httpStatus = httpStatus;
            this.responseText = responseText;
        }
    }

    public CloudFlareAuthorizer(HttpClient httpClient, HttpClientContext httpClientContext) {
        this.httpClient = (CloseableHttpClient)httpClient;
        this.httpClientContext = httpClientContext;
    }

    public void getAuthorizationResult(String url) throws IOException, ScriptException, InterruptedException {

        URL cloudFlareUrl = new URL(url);
        Response initialResponse = getResponse(url,null);

        if(initialResponse.httpStatus != HttpStatus.SC_SERVICE_UNAVAILABLE){
            log.info("Cloudflare DDos doesn't appear to be active, got status {}",initialResponse.httpStatus);
            return;
        }

        int answer = getJsAnswer(cloudFlareUrl,initialResponse.responseText);
        String jschl_vc = new PatternStreamer(jsChallenge).results(initialResponse.responseText).findFirst().get();
        String pass =  new PatternStreamer(password).results(initialResponse.responseText).findFirst().get();

        String authUrl = String.format("https://%s/cdn-cgi/l/chk_jschl?jschl_vc=%s&pass=%s&jschl_answer=%d",
                cloudFlareUrl.getHost(),jschl_vc,pass,answer);

        Thread.sleep(5000);
        int retries = 5;
        Response authResponse = getResponse(authUrl, url);

        while(authResponse.httpStatus != HttpStatus.SC_OK && retries-- > 0) {
            authResponse = getResponse(authUrl, authUrl);
            Thread.sleep(100);
        }

        if (authResponse.httpStatus != HttpStatus.SC_OK) {
            log.error("Failed to perform Cloudflare DDos authorization, got status {}", authResponse.httpStatus);
            return;
        }

        log.info("Cloudflare DDos authorization success, cf_clearance: {}",
                httpClientContext.getCookieStore().getCookies()
                        .stream()
                        .filter(cookie -> cookie.getName().equals("cf_clearance"))
                        .findFirst().get().getValue());


    }

    private Response getResponse(String url, String referer) throws IOException {

        HttpGet getRequest = new HttpGet(url);

        if(referer != null)
            getRequest.setHeader(HttpHeaders.REFERER,referer);

        HttpResponse httpResponse = httpClient.execute(getRequest,httpClientContext);

        String responseText = Utils.convertStreamToString(httpResponse.getEntity().getContent());
        int httpStatus = httpResponse.getStatusLine().getStatusCode();

        httpResponse.getEntity().getContent().close();
        ((CloseableHttpResponse)httpResponse).close();
        return new Response(httpStatus,responseText);
    }

    private int getJsAnswer(URL url, String responseHtml) throws ScriptException, MalformedURLException {

        /** Example JS calculation from Cloudflare

         setTimeout(function() {
             var s, t, o, p, b, r, e, a, k, i, n, g, f,
             ANwodXX = {
             "TgQVrpgKBJG": +((!+[] + !![] + !![] + !![] + []) + (!+[] + !![] + !![] + !![] + !![] + !![] + !![]))
             };
             t = document.createElement('div');
             t.innerHTML = "<a href='/'>x</a>";
             t = t.firstChild.href;
             r = t.match(/https?:\/\//)[0];
             t = t.substr(r.length);
             t = t.substr(0, t.length - 1);
             a = document.getElementById('jschl-answer');
             f = document.getElementById('challenge-form');;
             ANwodXX.TgQVrpgKBJG *= +((!+[] + !![] + !![] + []) + (!+[] + !![] + !![] + !![] + !![] + !![]));
             ANwodXX.TgQVrpgKBJG += +((!+[] + !![] + []) + (+!![]));
             ANwodXX.TgQVrpgKBJG += +((!+[] + !![] + []) + (!+[] + !![] + !![] + !![] + !![] + !![] + !![] + !![] + !![]));
             ANwodXX.TgQVrpgKBJG += !+[] + !![] + !![] + !![] + !![];
             a.value = parseInt(ANwodXX.TgQVrpgKBJG, 10) + t.length;
             '; 121'
             f.action += location.hash;
             f.submit();
         }, 4000);

         */

        int answer = 0;
        Matcher result = jsScript.matcher(responseHtml);

        if(result.find()){
            String val1 = result.group(1);
            String val2 = result.group(2);

            Matcher matcher1 = Pattern.compile(val1+"=\\{\""+val2+"\":(.+?)}").matcher(responseHtml);

            if(matcher1.find()){

                String jsCode = String.format("a=%s;%s",
                        matcher1.group(1),
                        new PatternStreamer(val1+"\\."+val2+"([^,].+?);")
                                .results(responseHtml)
                                .collect(Collectors.joining(";a","a",";")));

                answer = (int)((double)engine.eval(jsCode));
                answer += url.getHost().length();
            }
        }
        return answer;
    }

}

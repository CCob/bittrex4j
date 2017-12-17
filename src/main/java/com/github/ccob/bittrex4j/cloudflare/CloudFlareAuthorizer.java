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
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.text.html.Option;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CloudFlareAuthorizer {

    private static Logger log = LoggerFactory.getLogger(CloudFlareAuthorizer.class);

    private HttpClient httpClient;
    private HttpClientContext httpClientContext;
    private Pattern jsChallenge = Pattern.compile("name=\"jschl_vc\" value=\"(.+?)\"");
    private Pattern password = Pattern.compile("name=\"pass\" value=\"(.+?)\"");
    private Pattern jsScript = Pattern.compile("var s,t,o,p,b,r,e,a,k,i,n,g,f, (.+?)=\\{\"(.+?)\"");
    private ScriptEngineManager engineManager = new ScriptEngineManager();
    private ScriptEngine engine = engineManager.getEngineByName("nashorn");

    private static class Response{
        private int httpStatus;
        private String responseText;

        Response(int httpStatus, String responseText) {
            this.httpStatus = httpStatus;
            this.responseText = responseText;
        }
    }

    public CloudFlareAuthorizer(HttpClient httpClient, HttpClientContext httpClientContext) {
        this.httpClient = httpClient;
        this.httpClientContext = httpClientContext;
    }

    public void getAuthorizationResult(String url) throws IOException, ScriptException {

        URL cloudFlareUrl = new URL(url);

        try {

            int retries = 5;
            Response response = getResponse(url,null);

            while (response.httpStatus == HttpStatus.SC_SERVICE_UNAVAILABLE && retries-- > 0) {

                int answer = getJsAnswer(cloudFlareUrl,response.responseText);
                String jschl_vc = new PatternStreamer(jsChallenge).results(response.responseText).findFirst().get();
                String pass =  new PatternStreamer(password).results(response.responseText).findFirst().get();

                String authUrl = String.format("https://%s/cdn-cgi/l/chk_jschl?jschl_vc=%s&pass=%s&jschl_answer=%d",
                        cloudFlareUrl.getHost(),jschl_vc,pass,answer);

                Thread.sleep(5000);
                response = getResponse(authUrl, url);
            }

            if (response.httpStatus != HttpStatus.SC_OK) {
                log.error("Failed to perform Cloudflare DDoS authorization, got status {}", response.httpStatus);
                return;
            }

        }catch(InterruptedException ie){
            log.error("Interrupted whilst waiting to perform CloudFlare authorization",ie);
            return;
        }

        Optional<Cookie> cfClearanceCookie = httpClientContext.getCookieStore().getCookies()
                .stream()
                .filter(cookie -> cookie.getName().equals("cf_clearance"))
                .findFirst();

        if(cfClearanceCookie.isPresent()) {
            log.info("Cloudflare DDos authorization success, cf_clearance: {}",
                    cfClearanceCookie.get().getValue());
        }else{
            log.info("Cloudflare DDoS is not currently active");
        }
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

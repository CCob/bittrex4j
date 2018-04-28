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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static javax.script.ScriptContext.ENGINE_SCOPE;

public class CloudFlareAuthorizer {

    private static Logger log = LoggerFactory.getLogger(CloudFlareAuthorizer.class);

    private HttpClient httpClient;
    private HttpClientContext httpClientContext;
    private Pattern jsChallenge = Pattern.compile("name=\"jschl_vc\" value=\"(.+?)\"");
    private Pattern password = Pattern.compile("name=\"pass\" value=\"(.+?)\"");
    private Pattern jsScript = Pattern.compile("setTimeout\\(function\\(\\)\\{\\s+(var s,t,o,p,b,r,e,a,k,i,n,g,f.+?\\r?\\n[\\s\\S]+?a\\.value =.+?)\\r?\\n");


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

    public boolean getAuthorizationResult(String url) throws IOException, ScriptException {

        URL cloudFlareUrl = new URL(url);

        try {

            int retries = 5;
            int timer = 4500;
            Response response = getResponse(url,url);

            while (response.httpStatus == HttpStatus.SC_SERVICE_UNAVAILABLE && retries-- > 0) {

                log.trace("CloudFlare response HTML:");
                log.trace(response.responseText);

                String answer = getJsAnswer(cloudFlareUrl,response.responseText);
                String jschl_vc = new PatternStreamer(jsChallenge).results(response.responseText).findFirst().orElse("");
                String pass =  new PatternStreamer(password).results(response.responseText).findFirst().orElse("");

                String authUrl = String.format("https://%s/cdn-cgi/l/chk_jschl?jschl_vc=%s&pass=%s&jschl_answer=%s",
                        cloudFlareUrl.getHost(),
                        URLEncoder.encode(jschl_vc,"UTF-8"),
                        URLEncoder.encode(pass,"UTF-8"),
                        answer);

                log.debug(String.format("CloudFlare auth URL: %s",authUrl));

                Thread.sleep(timer);
                response = getResponse(authUrl, url);
            }

            if (response.httpStatus != HttpStatus.SC_OK) {
                if(response.httpStatus == HttpStatus.SC_FORBIDDEN && response.responseText.contains("cf-captcha-container")){
                    log.warn("Getting CAPTCHA request from bittrex, throttling retries");
                    Thread.sleep(15000);
                }
                log.trace("Failure HTML: " + response.responseText);
                return false;
            }

        }catch(InterruptedException ie){
            log.error("Interrupted whilst waiting to perform CloudFlare authorization",ie);
            return false;
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

        return true;
    }

    private Response getResponse(String url, String referer) throws IOException {

        HttpGet getRequest = new HttpGet(url);

        if(referer != null)
            getRequest.setHeader(HttpHeaders.REFERER,referer);

        int hardTimeout = 30; // seconds
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                getRequest.abort();
            }
        };
        new Timer(true).schedule(task, hardTimeout * 1000);

        HttpResponse httpResponse = httpClient.execute(getRequest,httpClientContext);

        String responseText = Utils.convertStreamToString(httpResponse.getEntity().getContent());
        int httpStatus = httpResponse.getStatusLine().getStatusCode();

        task.cancel();
        httpResponse.getEntity().getContent().close();
        ((CloseableHttpResponse)httpResponse).close();
        return new Response(httpStatus,responseText);
    }
    private String getJsAnswer(URL url, String responseHtml) throws ScriptException, MalformedURLException {

        //Credit to Anarov to the improved Regex JS parsing here from https://github.com/Anorov/cloudflare-scrape

        Matcher result = jsScript.matcher(responseHtml);

        if(result.find()){
            String jsCode = result.group(1);
            jsCode = jsCode.replaceAll("a\\.value = (.+ \\+ t\\.length).+","$1");
            jsCode = jsCode.replaceAll("\\s{3,}[a-z](?: = |\\.).+","").replace("t.length",String.valueOf(url.getHost().length()));
            jsCode = jsCode.replaceAll("[\\n\\\\']","");

            if(!jsCode.contains("toFixed")){
                throw new IllegalStateException("BUG: could not find toFixed inside CF JS challenge code");
            }

            log.debug(String.format("CloudFlare JS challenge code: %s", jsCode));
            return new BigDecimal(engine.eval(jsCode).toString()).setScale(10,BigDecimal.ROUND_HALF_UP).toString();
        }
        throw new IllegalStateException("BUG: could not find initial CF JS challenge code");
    }

}

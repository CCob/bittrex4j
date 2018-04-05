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

                String answer = getJsAnswer(cloudFlareUrl,response.responseText);
                String jschl_vc = new PatternStreamer(jsChallenge).results(response.responseText).findFirst().get();
                String pass =  new PatternStreamer(password).results(response.responseText).findFirst().get();

                String authUrl = String.format("https://%s/cdn-cgi/l/chk_jschl?jschl_vc=%s&pass=%s&jschl_answer=%s",
                        cloudFlareUrl.getHost(),
                        URLEncoder.encode(jschl_vc,"UTF-8"),
                        URLEncoder.encode(pass,"UTF-8"),
                        answer);

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

        String answer = "";
        Matcher result = jsScript.matcher(responseHtml);

        if(result.find()){
            String val1 = result.group(1);
            String val2 = result.group(2);

            Matcher matcher1 = Pattern.compile(val1+"=\\{\""+val2+"\":(.+?)}").matcher(responseHtml);

            if(matcher1.find()){

                String jsCode = String.format("a=%s;%s",
                        matcher1.group(1),
                        new PatternStreamer(val1+"\\."+val2+"([^.,].+?);")
                                .results(responseHtml)
                                .collect(Collectors.joining(";a","a",";")));


                Bindings bindings = new SimpleBindings();
                bindings.put("t",url.getHost());
                jsCode += "+a.toFixed(10) + t.length;";
                answer = engine.eval(jsCode,bindings).toString();
            }
        }
        return answer;
    }

}

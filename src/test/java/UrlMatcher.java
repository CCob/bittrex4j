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

import org.apache.http.client.methods.HttpGet;
import org.mockito.ArgumentMatcher;

public class UrlMatcher extends ArgumentMatcher<HttpGet> {

    String urlToMatch;
    boolean partial;

    static UrlMatcher matchesUrl(String urlToMatch){
        return new UrlMatcher(urlToMatch);
    }

    public static UrlMatcher containsInUrl(String partialUrlToMatch){
        return new UrlMatcher(partialUrlToMatch,true);
    }

    private UrlMatcher(String urlToMatch){
        this(urlToMatch,false);
    }

    private UrlMatcher(String urlToMatch, boolean partial){
        this.urlToMatch = urlToMatch;
        this.partial = partial;
    }

    public boolean matches(Object httpGet) {

        if(urlToMatch == null || httpGet == null){
            return false;
        }

        if(!partial)
            return ((HttpGet)httpGet).getURI().toASCIIString().equals(urlToMatch);
        else
            return ((HttpGet)httpGet).getURI().toASCIIString().contains(urlToMatch);
    }
}
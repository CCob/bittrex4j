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

import java.util.HashMap;
import java.util.Map;

public class UrlBuilder {

    private static final String API_VERSION_2 = "v2.0";
    private static final String API_VERSION_1_1 = "v1.1";
    private static final String INITIAL_URL = "https://bittrex.com/api/";

    private String baseUrl;
    private String method;
    private String group;
    private Map<String,String> arguments = new HashMap<>();
    private String apiKey;
    private String apiSecret;
    boolean isV2;

    static UrlBuilder v1_1(){
        return new UrlBuilder(INITIAL_URL + API_VERSION_1_1,false);
    }

    static UrlBuilder v2(){
        return new UrlBuilder(INITIAL_URL + API_VERSION_2,true);
    }

    private UrlBuilder(String initialUrl, boolean isV2){
        this.baseUrl = initialUrl;
        this.isV2 = isV2;
    }

    UrlBuilder withGroup(String group){
        this.group = group;
        return this;
    }

    UrlBuilder withArgument(String name, String value){
        arguments.put(name,value);
        return this;
    }

    UrlBuilder withApiKey(String apiKey, String apiSecret){
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        withArgument("apikey",apiKey);
        return this;
    }

    UrlBuilder withMethod(String method){
        this.method = method;
        return this;
    }

    boolean isSecure(){
        return this.apiKey != null && this.apiSecret != null;
    }

    String build(){

        String result = baseUrl;

        if(isV2){
            if(isSecure()){
                throw new UnsupportedOperationException("v2 secure API currently not supported");
            }else{
                result += "/pub";
            }
        }

        result += "/" + group;
        result += "/" + method;

        if(!arguments.isEmpty()){
            result += "?";
            for(String name : arguments.keySet()){
                result += name + "=" + arguments.get(name) + "&";
            }
        }

        return result;
    }
}

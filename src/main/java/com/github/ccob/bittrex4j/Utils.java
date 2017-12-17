package com.github.ccob.bittrex4j;

import java.io.InputStream;

public class Utils {

    public static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

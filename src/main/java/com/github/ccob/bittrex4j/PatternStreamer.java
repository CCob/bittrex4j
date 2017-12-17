package com.github.ccob.bittrex4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class PatternStreamer {
    private final Pattern pattern;
    public PatternStreamer(String regex) {
        this.pattern = Pattern.compile(regex);
    }
    public PatternStreamer(Pattern regex){
        this.pattern=regex;
    }
    public Stream<String> results(CharSequence input) {
        List<String> list = new ArrayList<>();
        for (Matcher m = this.pattern.matcher(input); m.find(); )
            for(int idx = 1; idx<=m.groupCount(); ++idx){
                list.add(m.group(idx));
            }
        return list.stream();
    }
}
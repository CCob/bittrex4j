package com.github.ccob.bittrex4j.dao;


import org.junit.Test;

import java.time.ZonedDateTime;

public class FillTest {

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowWhenConstructedWithBothPriceAndRate(){
        new Fill(1L,"ORDER","FILL",1.0,2.0,1.0,1.0, ZonedDateTime.now(),2);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowWhenConstructedWithoutBothPriceAndRate(){
        new Fill(1L,"ORDER","FILL",null,null,1.0,1.0, ZonedDateTime.now(),2);
    }
}

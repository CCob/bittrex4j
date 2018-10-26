package com.github.ccob.bittrex4j.dao;


import org.junit.Test;

import java.time.ZonedDateTime;
import java.math.BigDecimal;

public class FillTest {

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowWhenConstructedWithBothPriceAndRate(){
        new Fill(1L,"ORDER","FILL",BigDecimal.valueOf(1.0),BigDecimal.valueOf(2.0),BigDecimal.valueOf(1.0),BigDecimal.valueOf(1.0), ZonedDateTime.now(),2);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowWhenConstructedWithoutBothPriceAndRate(){
        new Fill(1L,"ORDER","FILL",null,null,BigDecimal.valueOf(1.0),BigDecimal.valueOf(1.0), ZonedDateTime.now(),2);
    }
}

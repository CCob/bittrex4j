package com.cobnet.bittrex4j.dao;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import pl.pojo.tester.internal.field.AbstractFieldValueChanger;
import pl.pojo.tester.internal.field.DefaultFieldValueChanger;

import java.util.Collection;
import java.util.Set;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;
import static pl.pojo.tester.api.assertion.Method.CONSTRUCTOR;
import static pl.pojo.tester.api.assertion.Method.GETTER;


@RunWith(Parameterized.class)
public class DaoTests {

    private Class<?> classUnderTest;

    @Parameters(name="{0}")
    public static Collection<Class<? extends Object>> testParameters() {
        Set<Class<?>> classes = new Reflections(DaoTests.class.getPackage().getName(),
                new SubTypesScanner(false)).getSubTypesOf(Object.class);

        //Dont want to test the tester class itself
        classes.remove(DaoTests.class);

        //Problematic tests due to pojo-tester not able to
        //construct ZoneId for ZonedDateTime construction
        classes.remove(Order.class);
        classes.remove(WalletHealth.class);
        classes.remove(Fill.class);
        classes.remove(WalletHealthResult.class);
        classes.remove(Market.class);
        classes.remove(CompletedOrder.class);
        classes.remove(MarketSummaryResult.class);

        return classes;
    }

    public DaoTests(Class<?> classUnderTest){
        this.classUnderTest = classUnderTest;
    }

    @Test
    public void shouldConstructDaoPojos(){
        assertPojoMethodsFor(classUnderTest).testing(CONSTRUCTOR )
                .areWellImplemented();
    }

    @Test
    public void shouldImplementDaoPojoGetters(){
        assertPojoMethodsFor(classUnderTest).testing(GETTER )
                .areWellImplemented();

    }
}
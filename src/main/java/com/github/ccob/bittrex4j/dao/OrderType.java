package com.github.ccob.bittrex4j.dao;

public enum OrderType {
    Open(0),
    Partial(1),
    Filled(2),
    Cancelled(3);

    private final long typeId;

     OrderType(long typeId){
         this.typeId = typeId;

    }
}

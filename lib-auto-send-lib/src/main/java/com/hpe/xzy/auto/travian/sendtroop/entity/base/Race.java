package com.hpe.xzy.auto.travian.sendtroop.entity.base;

/**
 * Created by Administrator on 11/5/2016.
 */

public enum Race{
    None(-1),
    Rome(0),
    Germanic(1),
    Gaul(2);

    int intvalue=-1;
    private Race(int value){
        intvalue=value;
    }

    public int getIntvalue(){
        return intvalue;
    }

}
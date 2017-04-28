package com.hpe.xzy.auto.travian.sendtroop.entity.base;



/**
 * Created by Administrator on 10/30/2016.
 */

public class TaskInfoBase {
    protected Account account= null;
    protected String villageId=null;


    protected Object callbackobj=null;
    protected String callbackmethod=null;

    public TaskInfoBase(){
        this(null);
    }
    public TaskInfoBase(String villageId){
        this.villageId=villageId;
    }
    public Account getAccount(){
        return account;
    }

    public void setAccount(Account value){
        account = value;
    }

    public void setCallback(Object obj,String methodname){
        this.callbackobj=obj;
        this.callbackmethod=methodname;
    }

    public String getVillageId(){
        return villageId;
    }

    public void setVillageId(String value){
        villageId=value;
    }


}

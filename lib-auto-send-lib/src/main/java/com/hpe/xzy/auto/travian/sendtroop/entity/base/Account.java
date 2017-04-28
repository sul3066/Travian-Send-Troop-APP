package com.hpe.xzy.auto.travian.sendtroop.entity.base;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by xuzhenya on 10/27/2016.
 */



public class Account {

    protected String username;
    protected String password;
    protected String serverurl;
    protected Proxy httpproxy=null;

    protected Race race=Race.None;
    protected  List<Village> villageList=null;
    public String getUsername(){
        return username;
    }
    public void setUsername (String value){
        username=value;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword (String value){
        password=value;
    }

    public String getServerurl(){
        return serverurl;
    }
    public void setServerurl (String value){
        serverurl=value;
    }

    public Race getRace(){
        return race;
    }
    public void setRace (Race value){
        race=value;
    }

    public Proxy getHttpproxy(){
        return httpproxy;
    }

    public List<Village> getVillageList(){
        if (villageList==null){
            villageList=new ArrayList<Village>();
        }
        return villageList;
    }

    public void setHttpproxy(String host, int port){
        httpproxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, 8080));
    }
    public void clearHttpProxy(){
        httpproxy=null;
    }
}

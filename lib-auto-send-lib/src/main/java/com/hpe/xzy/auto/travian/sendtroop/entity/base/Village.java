package com.hpe.xzy.auto.travian.sendtroop.entity.base;


/**
 * Created by Administrator on 11/3/2016.
 */

public class Village {
    protected String villageName;
    protected String newdid;
    protected  int x;
    protected  int y;


    public String getVillageName(){
        return villageName;
    }
    public void setVillageName (String value){
        villageName=value;
    }

    public int getX(){
        return x;
    }
    public void setX (int value){
        x=value;
    }

    public int getY(){
        return y;
    }
    public void setY (int value){
        y=value;
    }

    public String getNewdid(){
        return newdid;
    }
    public void setNewdid (String value){
        newdid=value;
    }

}

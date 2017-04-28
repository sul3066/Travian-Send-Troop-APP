package com.hpe.xzy.auto.travian.sendtroop.entity.task;

import com.hpe.xzy.auto.travian.sendtroop.entity.base.TaskInfoBase;

import java.sql.Timestamp;


/**
 * Created by xuzhenya on 10/31/2016.
 */

public class AttackTaskInfo  extends TaskInfoBase{
    protected int targetx=-1;
    protected int targety=-1;
    protected Timestamp reachTime = null;
    protected String[] troopinfo=null;
    protected String target1=null;
    protected String target2=null;
    protected String sendType = null;
    public  AttackTaskInfo(int x,int y, Timestamp tm,String[] info,  String target1,  String target2, String sendType){
        targetx=x;
        targety=y;
        reachTime=tm;
        troopinfo=info;
        this.target1=target1;
        this.target2=target2;
        this.sendType=sendType;
    }

    public int getTargetx(){
        return targetx;
    }

    public int getTargety(){
        return targety;
    }

    public String getTarget1(){
        return target1;
    }

    public String getTarget2(){
        return target2;
    }

    public String getSendType(){
        return sendType;
    }

    public Timestamp getReachTime(){
        return reachTime;
    }

    public String[] getTroopinfo(){
        return troopinfo;
    }


}


package com.hpe.xzy.auto.travian.sendtroop.action.base;


import com.hpe.xzy.auto.common.utility.CallBackOnceTimer;

import com.hpe.xzy.auto.travian.sendtroop.entity.Context;


/**
 * Created by xuzhenya on 11/9/2016.
 */

public abstract class TimeBaseAction extends WorkFlowBase {
    protected CallBackOnceTimer tm = new CallBackOnceTimer(this.logger, this);

    public TimeBaseAction(Context context){
        super(context);
    }


    protected void startTimer(long interval, Object caller, String method, Class<?>[] c, Object[] params){
        tm.startTimer(interval, caller, method, c, params);
    }

}

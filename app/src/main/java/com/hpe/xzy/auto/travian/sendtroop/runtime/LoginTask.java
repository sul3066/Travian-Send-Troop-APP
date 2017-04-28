package com.hpe.xzy.auto.travian.sendtroop.runtime;

import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;
import com.hpe.xzy.auto.travian.sendtroop.action.CheckLoginAction;
import com.hpe.xzy.auto.travian.sendtroop.entity.Context;
import com.hpe.xzy.auto.travian.sendtroop.runtime.base.TaskBase;

/**
 * Created by Administrator on 11/1/2016.
 */

public class LoginTask extends TaskBase {
    public LoginTask(Context context)
    {
        super(context);
    }
    @Override
    public void start() {
        logger.info(this.getClass(), "login task run");
        try{
            action = new CheckLoginAction(this.getContext());
            action.run(this,"loginResult");
        } catch(Exception ex){
            logger.error(this.getClass(), ex);
        }

    }

    public void loginResult(IWorkFlowNode action){
        logger.info(this.getClass(),"loginResult =" + action.getStatus());
        this.end();
    }

}

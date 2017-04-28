package com.hpe.xzy.auto.travian.sendtroop.runtime;

import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;
import com.hpe.xzy.auto.travian.sendtroop.action.BuildVillageListAction;
import com.hpe.xzy.auto.travian.sendtroop.entity.Context;
import com.hpe.xzy.auto.travian.sendtroop.runtime.base.TaskBase;

/**
 * Created by xuzhenya on 11/4/2016.
 */

public class BuildVillageListTask extends TaskBase {
    String inputresponse = null;
    public BuildVillageListTask(Context context, String inresponse)
    {
        super(context);
        inputresponse = inresponse;
    }

    @Override
    public void start() {
        logger.info(this.getClass(), "login task run");
        try{
            action = new BuildVillageListAction(this.getContext(),inputresponse);
            action.run(this,"actionDone");
        } catch(Exception ex){
            logger.error(this.getClass(), ex);
        }

    }

    public void actionDone(IWorkFlowNode action){
        logger.info(this.getClass(),"actionDone =" + action.getStatus());
        this.end();
    }
}

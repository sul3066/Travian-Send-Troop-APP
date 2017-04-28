package com.hpe.xzy.auto.travian.sendtroop.runtime;


import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;
import com.hpe.xzy.auto.travian.sendtroop.action.AttackTimer;
import com.hpe.xzy.auto.travian.sendtroop.entity.Context;
import com.hpe.xzy.auto.travian.sendtroop.runtime.base.TaskBase;

/**
 * Created by xuzhenya on 10/31/2016.
 */

public class AttackTask extends TaskBase {
    public AttackTask(Context context)
    {
        super(context);
    }
    @Override
    public void start() {
        logger.info(this.getClass(), "task run");
        action = new AttackTimer(this.getContext());
        action.run(this, "dofinish");
    }

    public void dofinish(IWorkFlowNode action){
        logger.info(this.getClass(), "dofinish:" + action.getStatus());
        this.end();
    }




}

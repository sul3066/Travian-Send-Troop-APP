package com.hpe.xzy.auto.travian.sendtroop.action;

import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;
import com.hpe.xzy.auto.travian.sendtroop.action.base.TimeBaseAction;
import com.hpe.xzy.auto.travian.sendtroop.entity.Context;
import com.hpe.xzy.auto.travian.sendtroop.entity.task.AttackTaskInfo;


/**
 * Created by xuzhenya on 11/9/2016.
 */

public class AttackTimer extends TimeBaseAction{
    AttackTaskInfo taskinfo=null;
    IWorkFlowNode action = null;
    public AttackTimer(Context context){
        super(context);
        taskinfo=(AttackTaskInfo)context.getTaskinfo();
        action = new AttackAction(context,this.tm);
    }
    @Override
    public void start() {
        action.run(this,"attackResult");
    }

    public void attackResult(IWorkFlowNode action){
        logger.info(this.getClass(), "attackResult:"+ action.getStatus());
        if (action.getStatus() == ActionStatus.SUCCESS || action.getStatus() == ActionStatus.ERROR){
            this.end(action.getStatus());
            return;
        } else if (action.getStatus() == ActionStatus.RETRY){
            IWorkFlowNode laction = new CheckLoginAction(context);
            laction.run(this,"loginResult");
            return;
        } else if(action.getStatus() == ActionStatus.NOT_READY){
            AttackAction caction =(AttackAction) action;
            long wait=caction.getWaiting();
            wait=wait-SendTroopConstant.prepareTimeInadvance;
            logger.info(this.getClass(), "attackResult start timer:"+ wait);
            if (wait >0){
                startTimer(wait,this,"getTimeDiffer",new Class<?>[]{}, new Object[]{});
            }else{
                this.end(ActionStatus.ERROR);
            }
            return;
        } else{
            this.end(ActionStatus.ERROR);
            return;
        }


    }

    public void getTimeDiffer(){
        logger.info(this.getClass(), "getTimeDiffer start");
        GetTimeDiffAction taction = new GetTimeDiffAction(context);
        taction.run(this,"timeDiffResult");
    }

    public void  timeDiffResult(IWorkFlowNode raction){
        logger.info(this.getClass(), "timeDiffResult done");
        action = new AttackAction(context,this.tm);
        action.run(this,"attackResult");
    }
    public void loginResult(IWorkFlowNode action) {
        logger.info(this.getClass(), "loginResult:" + action.getStatus());
        if (action.getStatus() != ActionStatus.SUCCESS){
            this.end(action.getStatus());
            return;
        }
        startTimer(1000,this,"start",null,null);

    }
}

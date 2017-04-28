package com.hpe.xzy.auto.travian.sendtroop.action.base;

import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;
import com.hpe.xzy.auto.common.log.api.ILogger;
import com.hpe.xzy.auto.common.utility.Utility;
import com.hpe.xzy.auto.travian.sendtroop.entity.Context;

/**
 * Created by xuzhenya on 10/27/2016.
 */

public abstract class WorkFlowBase implements IWorkFlowNode {
    private IWorkFlowNode.ActionStatus status = ActionStatus.INIT;

    private String callback=null;
    private Object caller;
    protected Context context  = null;
    protected ILogger logger =null;
    protected String message  = null;
    public  int messageid=-1;


    public WorkFlowBase(Context runcontext) {
        context = runcontext;
        logger=context.getLogger();
    }
    protected abstract void  start();


    @Override
    public String getMessage(){
        return  message;
    }

    @Override
    public int getMessageId(){
        return messageid;
    }

    @Override
    public IWorkFlowNode.ActionStatus getStatus(){
        return status;
    }

    @Override
    public void run(Object caller, String callback){
        this.caller=caller;
        this.callback=callback;
        status=ActionStatus.RUNNING;
        this.start();
    }

    protected void doCallBack(){
        if (caller != null && this.callback != null){
            logger.info(this.getClass(),"do callback");
            invokeCallBackMethod(caller, callback, this);
        }else{
            logger.info(this.getClass(),"no callback");
        }
    }

    protected void invokeCallBackMethod(Object obj, String methodname, IWorkFlowNode param){
        invokeCallBackMethod(obj,methodname,new Class[]{IWorkFlowNode.class},new Object[]{param});
    }

    protected void invokeCallBackMethod(Object obj, String methodname, Class<?>[] types, Object[] params){
        logger.info(this.getClass(),"invoke method '"+methodname + "'  on " + obj.getClass().getName());

        try{
            Utility.callMethodOnObject(obj, methodname,types, params);
        } catch (Exception ex){
            this.end(ActionStatus.ERROR);
            logger.error(this.getClass(), ex);
        }

    }

    @Override
    public void  end(ActionStatus status){
        this.status=status;
        doCallBack();
    }


}

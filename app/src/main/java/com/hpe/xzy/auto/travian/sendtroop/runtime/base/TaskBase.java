package com.hpe.xzy.auto.travian.sendtroop.runtime.base;


import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;
import com.hpe.xzy.auto.common.log.api.ILogger;
import com.hpe.xzy.auto.common.task.api.ITask;
import com.hpe.xzy.auto.common.utility.Utility;
import com.hpe.xzy.auto.travian.sendtroop.entity.Context;
import com.hpe.xzy.auto.travian.sendtroop.entity.base.TaskInfoBase;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by xuzhenya on 10/31/2016.
 */

public abstract class TaskBase implements ITask {


    TaskStatus status = TaskStatus.INIT;
    Context context = null;
    String name=null;
    Object callbackobj=null;
    String callbackmethod=null;
    protected ILogger logger =null;
    protected IWorkFlowNode action;

    protected void doCallBack(){
        if (callbackobj != null && this.callbackmethod != null){
            try {
                Utility.callMethodOnObject(callbackobj, callbackmethod,new Class[]{ITask.class}, new Object[]{this});
            } catch (InvocationTargetException e) {
                logger.error(this.getClass(),e);
            } catch (IllegalAccessException e) {
                logger.error(this.getClass(),e);
            } catch (NoSuchMethodException e) {
                logger.error(this.getClass(),e);
            }
        }else{
            logger.info(this.getClass(),"no callback");
        }
    }

    public TaskBase(Context taskcontext){
        this.context=taskcontext;
        logger=context.getLogger();
    }
    public TaskInfoBase getTaskInfo(){return  context.getTaskinfo();}
    @Override
    public TaskStatus getStatus(){return status;}
    public Context getContext(){return context;};
    @Override
    public void run(){status=TaskStatus.RUNNING; start();}
    protected abstract void start();
    @Override
    public void stop(){status=TaskStatus.STOPPED;}
    @Override
    public void resume(){status=TaskStatus.RUNNING;}
    @Override
    public IWorkFlowNode.ActionStatus getActionStatus (){
        return action.getStatus();
    }
    public String getName(){
        return name;
    }
    @Override
    public void setCallback(Object obj, String method){
        this.callbackobj=obj;
        this.callbackmethod=method;
    }
    public void setName(String value){
        this.name=value;
    }
    @Override
    public void end(){
        this.status=TaskStatus.END;
        doCallBack();
    };
    @Override
    public String getMessage(){
        if (action !=null) {
            return action.getMessage();
        }
        return null;
    }
}

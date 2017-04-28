package com.hpe.xzy.auto.common.task.api;

import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;

/**
 * Created by xuzhenya on 10/31/2016.
 */

public interface ITask extends Runnable{

    enum TaskStatus{
        INIT,
        RUNNING,
        STOPPED,
        END
    }
    public TaskStatus getStatus();
    public IWorkFlowNode.ActionStatus getActionStatus();
    public void setCallback(Object obj, String method);
    public void run();
    public void stop();
    public void resume();
    public void end();
    public String getMessage();
}

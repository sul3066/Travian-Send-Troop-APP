package com.hpe.xzy.auto.common.engine.api;

/**
 * Created by xuzhenya on 10/27/2016.
 */


public interface IWorkFlowNode {
    enum ActionStatus{
        INIT,
        SUCCESS,
        ERROR,
        NOT_READY,
        RETRY,
        RUNNING
    }

    public void run(Object caller, String callbackname);

    public void  end(ActionStatus status);
    public ActionStatus getStatus ();
    public String getMessage();
    public int getMessageId();
}

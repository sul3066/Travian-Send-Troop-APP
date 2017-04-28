package com.hpe.xzy.auto.travian.sendtroop.action;


import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;
import com.hpe.xzy.auto.travian.sendtroop.action.base.HttpBaseAction;
import com.hpe.xzy.auto.travian.sendtroop.entity.Context;


/**
 * Created by Administrator on 10/31/2016.
 */

public class CheckLoginAction extends HttpBaseAction{
   // Account accinfo=null;

    public CheckLoginAction(Context context){
        super(context);
        //accinfo=context.getTaskinfo().getAccount();
    }

    @Override
    protected void start() {
        logger.info(this.getClass(),"CheckLoginAction start");
        this.sendRequest("dorf3.php",null,this, "CheckLoginHttpResult");
    }

    public void CheckLoginHttpResult(HtmlHttpCallBack callback){
        logger.info(this.getClass(), "CheckLoginHttpResult httpcode="+callback.getReponsecode());
        String response = callback.getResponseText();
        if (response.indexOf("玩家登入")> -1){
            logger.info(this.getClass(), "check login false start login");
            LoginAction lgaction = new LoginAction(this.context);
            lgaction.run(this,"loginResult");
            return;
        }
        if(response.indexOf("dorf1.php?ok") >-1){
            logger.info(this.getClass(), "check login false has notice, end");
            this.end(ActionStatus.ERROR);
            return;
        }

        if(response.indexOf("accesskey=\"2\" title=\"村莊中心||\"") >-1){
            logger.info(this.getClass(), "check login success end");
            this.message=response;
            this.end(ActionStatus.SUCCESS);
            return;
        } else {
            logger.info(this.getClass(), "check login false unknown error end");
            logger.trace(this.getClass(), callback.getResponseText());
            this.end(ActionStatus.ERROR);
        }
    }
    public void loginResult (IWorkFlowNode action){
        this.message=action.getMessage();
        this.end(action.getStatus());
    }

    @Override
    public void end(ActionStatus status){
        logger.info(this.getClass(),"CheckLoginAction end status:" + status);
        super.end(status);
    }
}

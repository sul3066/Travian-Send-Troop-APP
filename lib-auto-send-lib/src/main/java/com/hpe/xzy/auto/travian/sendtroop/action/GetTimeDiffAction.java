package com.hpe.xzy.auto.travian.sendtroop.action;

import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;
import com.hpe.xzy.auto.common.utility.Utility;
import com.hpe.xzy.auto.travian.sendtroop.action.base.HttpBaseAction;

import com.hpe.xzy.auto.travian.sendtroop.entity.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by xuzhenya on 11/11/2016.
 */

public class GetTimeDiffAction extends HttpBaseAction {

    public GetTimeDiffAction(Context context){
        super(context);
    }
    private static Map<String,TimeInfo> timediffmap = new ConcurrentHashMap<String,TimeInfo>();
    private static Map<String,Object> lockObj = new ConcurrentHashMap<String,Object>();
    private Long timdiffer = null;
    class TimeInfo {
        Long timDiffer;
        long updatetime=-1;
    }

    public Long getTimdiffer(){
        return timdiffer;
    }
    @Override
    public void start() {
        logger.info(this.getClass(), "GetTimeDiffAction start ");
        Object lock=getLockObj(context.getTaskinfo().getAccount().getServerurl());
        synchronized (lock){
            startCaculate(lock);
//            try {
//                lock.wait();
//            } catch (Exception ex){
//                logger.error(this.getClass(), ex);
//                end(ActionStatus.ERROR);
//                return;
//            }
//            if (this.getStatus()!=ActionStatus.SUCCESS){
//
//            }
        }

    }

    private void startCaculate(Object lock){
        logger.info(this.getClass(), "startCaculate");
        long ctime= Utility.getCurrentTimeTicket();
        String serverurl=context.getTaskinfo().getAccount().getServerurl();
        TimeInfo info= null;
        try{
            if (timediffmap.containsKey(serverurl)){
                info=timediffmap.get(serverurl);
            }else{
                info=new TimeInfo();
                info.updatetime=ctime;
                timediffmap.put(serverurl,info);
            }
            timdiffer = info.timDiffer;
            if (timdiffer != null && ctime-info.updatetime < SendTroopConstant.timeCacheTimeout && info.updatetime>0){
                // use current data
                end(ActionStatus.SUCCESS);
                return;
            }
            info.timDiffer=null;
            info.updatetime=-1;
            evaluateStart(info);
            if (info.timDiffer !=null){
                info.updatetime=ctime;
            }
            this.timdiffer=info.timDiffer;
            end(ActionStatus.SUCCESS);

        }finally {
            //lock.notifyAll();
        }
    }
    class HtmlCallBackImpl{
        private Long[] reqTime = new Long[SendTroopConstant.evalCount];
        private Long[] timeDiff=new Long[SendTroopConstant.evalCount];
        private HtmlHttpCallBack callback = null;
        private IWorkFlowNode action=null;
        public void doHtmlCallBack(HtmlHttpCallBack callback){
            logger.info( this.getClass(),"doHtmlCallBack");
            this.callback=callback;
        }
        public void getActionCallBack(IWorkFlowNode action){
            logger.info( this.getClass(),"getActionCallBack");
            this.action =action;
        }
        void reset(){
            callback = null;
            action=null;
        }

        Long[] getReqTime(){
            return reqTime;
        }

        Long[] getTimeDiff(){
            return timeDiff;
        }

        IWorkFlowNode getAction(){
            return action;
        }
        HtmlHttpCallBack getCallback(){
            return callback;
        }

    }


    private void evaluateStart(TimeInfo info){
        logger.info(this.getClass(), "evaluateStart");
        HtmlCallBackImpl callbackimpl=new HtmlCallBackImpl();
        for (int i=0; i<SendTroopConstant.evalCount; ++i ){
            logger.info(this.getClass(),"get time diff i="+i);
            try{
                callbackimpl.reset();
                long wait= 1000+100*i;
                long ctime=Utility.getCurrentTimeTicket();
                wait-=ctime%1000;
                if (wait < 1000)
                    wait += 1000;
                Thread.sleep(wait);
                callbackimpl.getReqTime()[i]=Utility.getCurrentTimeTicket();
                sendRequest("dorf1.php",null,callbackimpl,"doHtmlCallBack");
                logger.info(this.getClass(),"call back="+callbackimpl.getCallback().getReponsecode());

                if (callbackimpl.getCallback().getReponsecode() != 200){
                    end(ActionStatus.ERROR);
                    return;
                }

                String timedivstr=Utility.getStrBetweenOuter(callbackimpl.getCallback().getResponseText(),"<div id=\"servertime\" class=\"stime\">","</div>");
                if (timedivstr == null || timedivstr.length()<1){
                    // do login
                    CheckLoginAction laction=new CheckLoginAction(context);
                    laction.run(callbackimpl,"getActionCallBack");
                    if (laction.getStatus() != ActionStatus.SUCCESS){
                        end(ActionStatus.ERROR);
                        return;
                    }
                    timedivstr=Utility.getStrBetweenOuter(laction.getMessage(),"<div id=\"servertime\" class=\"stime\">","</div>");
                }

                Document doc= null;

                try{
                    doc =Utility.phaseToDom(timedivstr);
                }catch (ParserConfigurationException|SAXException|IOException ex){
                    logger.error(this.getClass(), ex);
                    this.message=ex.getMessage();
                }

                if (doc==null){
                    end(ActionStatus.ERROR);
                    return;
                }
                long ticket=-1;
                NodeList nodes=doc.getElementsByTagName("span");
                if (nodes.getLength()>0){
                    Element elem= (Element) nodes.item(0);
                    logger.info(this.getClass(), "ticket:"+elem.getTextContent());
                    ticket=Utility.parseTimeString(elem.getTextContent());
                }

                if (ticket < 1){
                    logger.error(this.getClass(), "cannot get time ignore" );
                    --i;
                    continue;
                }

                long realMs = ticket %3600000 +500;
                long estMs=callbackimpl.getReqTime()[i] %3600000;
                long diff=realMs-estMs;
                if (diff >  1800000)
                    diff -= 3600000;
                if (diff < -1800000)
                    diff += 3600000;
                logger.info(this.getClass(), " get time diff :"+ diff);

                if (diff > 60000 || diff < -60000)
                    logger.info(this.getClass(), "time diff exceed 1 mins" );

                callbackimpl.getTimeDiff()[i]=diff;
            }catch (InterruptedException|NumberFormatException ex){
                logger.error(this.getClass(), ex);
                end(ActionStatus.ERROR);
            }
        }

        // get min
        Long[] atimediff = callbackimpl.getTimeDiff();
        Long mindiff=atimediff[0];
        for (int i=1; i< SendTroopConstant.evalCount; ++i){
            if (atimediff[i] < mindiff)
                mindiff = atimediff[i];
        }
        info.timDiffer=mindiff;
        logger.info(this.getClass(), "time diff :"+mindiff );
        return;
    }

    private synchronized static Object getLockObj (String serveurl){
        Object obj=null;
        if (!lockObj.containsKey(serveurl)){
            obj = new Object();
            lockObj.put(serveurl,obj);
        }else{
            obj=lockObj.get(serveurl);
        }
        return obj;
    }
    public static Long getTimeDiffByServer(String serverurl){
        Object lock= getLockObj(serverurl);
        Long ret = null;
        synchronized (lock){
            if (timediffmap.containsKey(serverurl)){
                ret= timediffmap.get(serverurl).timDiffer;
            }
        }
        return ret;
    }
}

package com.hpe.xzy.auto.travian.sendtroop.action;


import com.hpe.xzy.auto.common.utility.Utility;

import com.hpe.xzy.auto.travian.sendtroop.action.base.HttpBaseAction;
import com.hpe.xzy.auto.travian.sendtroop.entity.Context;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Administrator on 10/31/2016.
 */

public class LoginAction extends HttpBaseAction{

    public LoginAction(Context context){
        super(context);
    }

    @Override
    protected void start() {

        this.sendRequest("login.php",null,this,"getLoginForm");

    }
    public void getLoginForm(HtmlHttpCallBack callback){
        logger.trace(this.getClass(),"getLoginForm:"+callback.getReponsecode());
        String form= Utility.getStrBetweenOuter(callback.getResponseText(),"<form","</form>");
        form=Utility.removeFromString(form,"<script","</script>");
        //form="<xml>"+form + "</xml>";
        Document doc = null;
        logger.trace(this.getClass(),"parse start");
        try {
            doc =Utility.phaseToDom(form);
        } catch (ParserConfigurationException e) {
            logger.error(this.getClass(),e);
        } catch (SAXException e) {
            logger.error(this.getClass(),e);
        } catch (IOException e) {
            logger.error(this.getClass(),e);
        }
        logger.trace(this.getClass(),"parse success");
        if (doc == null){
            this.end(ActionStatus.ERROR);
            return;
        }

        // get form input element
        Map<String,String> paramters=new HashMap<String,String>();
        NodeList nodes = doc.getElementsByTagName("input");
        int count = nodes.getLength();
        for (int i=0; i< count;++i){
            Node node = nodes.item(i);
            NamedNodeMap attrs = node.getAttributes();
            String pname = attrs.getNamedItem("name").getTextContent();
            String type = attrs.getNamedItem("type").getTextContent();
            String pvalue = "";
            if (attrs.getNamedItem("value")!=null){
                pvalue= attrs.getNamedItem("value").getTextContent();
            }
            if ("text".equalsIgnoreCase(type) ){
                // set request parameter username
                pvalue=context.getTaskinfo().getAccount().getUsername();
            }else if("password".equalsIgnoreCase(type)){
                // set request parameter password
                pvalue=context.getTaskinfo().getAccount().getPassword();
            }
             if (!"checkbox".equalsIgnoreCase(type)){
                 //pvalue="0";
                 paramters.put(pname,pvalue);
            }
        }

        // get submit button in form
        nodes = doc.getElementsByTagName("button");
        count = nodes.getLength();
        for (int i=0; i< count;++i){
            Node node = nodes.item(i);
            NamedNodeMap attrs = node.getAttributes();
            String pname = attrs.getNamedItem("name").getTextContent();
            String type = attrs.getNamedItem("type").getTextContent();
            String pvalue = "";
            if (attrs.getNamedItem("value")!=null){
                pvalue= attrs.getNamedItem("value").getTextContent();
            }
            if ("submit".equalsIgnoreCase(type)){
                paramters.put(pname,pvalue);
                break;
            }
        }

        this.sendRequestByMap("dorf1.php",paramters,this,"loginResult");
    }

    public void loginResult(HtmlHttpCallBack callback){
        logger.trace(this.getClass(),"loginResult:"+callback.getReponsecode());
        String responseText=callback.getResponseText();
        if (responseText == null){
            this.end(ActionStatus.ERROR);
            return;
        }

        this.message = responseText;
        if (responseText.indexOf("帳號不存在") > -1){
            logger.trace(this.getClass(),"loginResult: 帳號不存在");
            this.end(ActionStatus.ERROR);
            return;
        }

        if (responseText.indexOf("密碼錯誤") > -1){
            logger.trace(this.getClass(),"loginResult: 密碼錯誤");
            this.end(ActionStatus.ERROR);
            return;
        }

        if (responseText.indexOf("<a href=\"spieler.php\" title=\"玩家資料\">") <0){
            logger.trace(this.getClass(),"loginResult: unknown error");
            this.end(ActionStatus.ERROR);
            return;
        }

        logger.info(this.getClass(),"login success");
        this.end(ActionStatus.SUCCESS);

    }

}

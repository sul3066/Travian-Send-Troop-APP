package com.hpe.xzy.auto.travian.sendtroop.action;


import com.hpe.xzy.auto.common.utility.Utility;
import com.hpe.xzy.auto.travian.sendtroop.action.base.HttpBaseAction;
import com.hpe.xzy.auto.travian.sendtroop.entity.Context;
import com.hpe.xzy.auto.travian.sendtroop.entity.base.Account;
import com.hpe.xzy.auto.travian.sendtroop.entity.base.Race;
import com.hpe.xzy.auto.travian.sendtroop.entity.base.Village;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by xuzhenya on 11/4/2016.
 */

public class BuildVillageListAction extends HttpBaseAction {
    String response = null;

    public BuildVillageListAction(Context context){
        this(context, null);

    }

    public BuildVillageListAction(Context context, String inputResponse){
        super(context);
        response = inputResponse;
    }

    private Village buildVillageInfo(Element villageNode){
        Village village=null;
        NodeList cnodes=villageNode.getChildNodes();
        int cnsize=cnodes.getLength();

        if (cnsize > 0){
            village=new Village();
            // set village newdid
            String href= villageNode.getAttribute("href");
            village.setNewdid(Utility.getStrBetweenInner(href,"newdid=","&"));
            // get village name
            NodeList devList= villageNode.getElementsByTagName("div");
            if (devList.getLength() >0){
                village.setVillageName(devList.item(0).getTextContent());
            }
            // get x, y
            devList= villageNode.getElementsByTagName("span");
            int ssize= devList.getLength();

            for (int z=0; z< ssize; ++z){
                Node n=devList.item(z);
                String classname=n.getAttributes().getNamedItem("class").getTextContent();
                if ("coordinateX".equalsIgnoreCase(classname)){
                    String value=n.getTextContent();
                    value=value.substring(1);
                    value = value.replaceAll("[^\\p{ASCII}]", "");
                    int x= Integer.parseInt(value);
                    village.setX(x);
                } else if ("coordinateY".equalsIgnoreCase(classname)){
                    String value=n.getTextContent();
                    value=value.substring(0,value.length()-1);
                    value = value.replaceAll("[^\\p{ASCII}]", "");
                    int y= Integer.parseInt(value);
                    village.setY(y);
                }
            }
        }
        logger.info(this.getClass(), "add village="+village.getVillageName() + "  newdid="+ village.getNewdid() + " x="+village.getX()+"  y=" + village.getY());
        return village;
    }

    @Override
    protected void start() {
        logger.info(this.getClass(),"BuildVillageListAction start");
        if (response == null) {
            this.sendRequest("dorf3.php", null, this, "buildVillageList");
        } else{
            buildVillageList(null);
        }
    }

    public Account getAccount(){
        return context.getTaskinfo().getAccount();
    }

    public void buildVillageList(HtmlHttpCallBack callback){
        logger.info(this.getClass(),"buildVillageList");
        String responseText = response;
        if (callback != null && callback.getReponsecode() == HttpURLConnection.HTTP_OK){
            responseText=callback.getResponseText();
            this.message=responseText;
        }

        if (responseText==null || responseText.length() <1){
            this.end(ActionStatus.NOT_READY);
            return;
        }

        Document doc = null;
        String tmpstr = Utility.getStrBetweenInner(responseText,"playerName","</a>");
        tmpstr=Utility.getStrBetweenOuter(tmpstr,"<img","/>");
        if (tmpstr!=null && tmpstr.length() >0) {
            try{
                doc = Utility.phaseToDom(tmpstr);
            }catch(IOException ex){
                logger.error(this.getClass(),ex);
            }catch (ParserConfigurationException ex){
                logger.error(this.getClass(),ex);
            }catch (SAXException ex){
                logger.error(this.getClass(),ex);
            }
        }
        if (doc!=null){
            NodeList nodes = doc.getElementsByTagName("img");
            if (nodes.getLength()>0){
                String race = nodes.item(0).getAttributes().getNamedItem("title").getTextContent();
                if ("羅馬人".equals(race)){
                    context.getTaskinfo().getAccount().setRace(Race.Rome);
                }else if("條頓人".equals(race)){
                    context.getTaskinfo().getAccount().setRace(Race.Germanic);
                }else if("高盧人".equals(race)){
                    context.getTaskinfo().getAccount().setRace(Race.Gaul);
                }
                logger.info(this.getClass(), "race :" +race);
            }

        }

        String endstr="<div class=\"innerBox footer\">";
        tmpstr = Utility.getStrBetweenOuter(response,"<div class=\"expansionSlotInfo\"",endstr);
        if (tmpstr==null || tmpstr.length() <1) {
            this.end(ActionStatus.NOT_READY);
            return;
        }
        tmpstr= Utility.getStrBetweenOuter(tmpstr,"<div class=\"innerBox content\">",endstr);
        if (tmpstr==null || tmpstr.length() <1) {
            this.end(ActionStatus.NOT_READY);
            return;
        }

        tmpstr=tmpstr.substring(0,tmpstr.length()-endstr.length());

        if (tmpstr!=null && tmpstr.length() >0) {
            try{
                doc = Utility.phaseToDom(tmpstr);
            }catch(IOException ex){
                logger.error(this.getClass(),ex);
            }catch (ParserConfigurationException ex){
                logger.error(this.getClass(),ex);
            }catch (SAXException ex){
                logger.error(this.getClass(),ex);
            }
        }
        if (doc == null){
            this.end(ActionStatus.NOT_READY);
            return;
        }

        NodeList nodes=doc.getElementsByTagName("a");
        List<Village> vlist = context.getTaskinfo().getAccount().getVillageList();
        vlist.clear();
        int vsize=nodes.getLength();
        logger.info(this.getClass(),"total village = " + vsize);
        for (int y=0;y<vsize; ++y) {
            Node node = nodes.item(y);
            Village v= buildVillageInfo((Element)node);
            if (v!=null){
                vlist.add(v);
            }
        }
        this.end(ActionStatus.SUCCESS);
    }

}

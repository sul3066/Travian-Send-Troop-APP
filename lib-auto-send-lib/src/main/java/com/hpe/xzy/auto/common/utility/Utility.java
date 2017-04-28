package com.hpe.xzy.auto.common.utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Utility {

    public static Object createObjectByClassname(String classname){
        return new Object();
    }


    public static String getCurrentTimestampString(){
        return getCurrentTimestampString(null);
    }

    public static String getCurrentTimestampString(String format){
        //int id = res.getIdentifier("log_date_format","string", "");
        Timestamp tm  = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat fm = null;
        if (format == null){
            fm = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        } else{
            fm = new SimpleDateFormat(format);
        }

        return fm.format(tm);
    }

    public static long getCurrentTimeTicket(){
        //int id = res.getIdentifier("log_date_format","string", "");
        //Timestamp tm  = new Timestamp(calendar.getTime().getTime());
        return System.currentTimeMillis();
    }

    public static void callMethodOnObject(Object obj, String methodname, Class<?>[] types, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        Method method = null;

        method = obj.getClass().getMethod(methodname,types);

        method.invoke(obj, params);
    }

    public static  Document phaseToDom(String html) throws ParserConfigurationException, SAXException, IOException {
        InputStream stream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        return phaseToDom(stream);
    }

    public static  Document phaseToDom(InputStream html) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder= factory.newDocumentBuilder();
        Document doc = builder.parse(html);
        return doc;
    }

    public static String getStrBetweenOuter(String in, String start, String end){
        int startindex = in.indexOf(start);
        if (startindex <0){
            return "";
        }
        String retStr ="";
        int endindex = in.indexOf(end,startindex);
        if (endindex < 0){
            retStr=in.substring(startindex);
        }else{
            retStr=in.substring(startindex,endindex+end.length());
        }
        return retStr;
    }

    public static String getStrBetweenInner(String in, String start, String end){
        int startindex = in.indexOf(start);
        if (startindex <0){
            return "";
        }
        String retStr ="";
        int endindex = in.indexOf(end,startindex+1);
        if (endindex < 0){
            retStr=in.substring(startindex+start.length());
        }else{
            retStr=in.substring(startindex+start.length(),endindex);
        }
        return retStr;
    }

    public static String removeFromString(String in, String start, String end){
        int startindex = in.indexOf(start);
        if (startindex <0){
            return in;
        }
        String retStr ="";
        int endindex = in.indexOf(end,startindex);
        if (endindex < 0){
            retStr=in;
        }else{
            retStr=in.substring(0,startindex);
            retStr+=in.substring(endindex+end.length());
        }
        return retStr;
    }

    public static long getDuration(String dur){
        long ret=0;
        String[] split=dur.split(":");
        int leng=split.length;
        if (leng>0){
            try{
                int sec=Integer.valueOf(split[leng-1]);
                int min=0;
                int hour=0;

                if (leng>1){
                    min=Integer.valueOf(split[leng-2]);
                }
                if (leng>2){
                    hour=Integer.valueOf(split[leng-3]);
                }
                ret=(((hour *60) + min)*60+sec)*1000;
            }catch (Exception ex){

            }
        }
        return  ret;
    }

    public static long parseTimeString(String tstr){
        long ret=0;
        String[] split=tstr.split(":");
        int leng=split.length;
        if (leng>0){
            String sdate= getCurrentTimestampString("yyyy-MM-dd");
            sdate+= " " +tstr;
            ret= Timestamp.valueOf(sdate).getTime();
        }
        return  ret;
    }
}

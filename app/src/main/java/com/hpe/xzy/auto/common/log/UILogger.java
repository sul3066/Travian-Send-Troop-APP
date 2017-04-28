package com.hpe.xzy.auto.common.log;

import com.hpe.xzy.auto.common.log.api.ILogger;
import com.hpe.xzy.auto.common.utility.Utility;


import android.util.Log;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by xuzhenya on 10/28/2016.
 */

public class UILogger implements ILogger {
    private static TextView uitext = null;
    private int runlevel=LogLevel.INFO.getIntvalue();
    private  StringBuilder logmessage=new StringBuilder();
    private static ILogger instance = null;
    private boolean uiactive=false;
    protected UILogger(TextView view,LogLevel level){
        uitext = view;
        runlevel=level.getIntvalue();
    }
    public static synchronized ILogger getLogger(TextView view,LogLevel level){
        if (instance == null || uitext!=view){
            instance = new UILogger(view, level);
        }
        return instance;
    }
    public void setLogLevel(LogLevel level){
        runlevel=level.getIntvalue();
    }

    private void addMessageOnText(final String message){
        if (!uiactive){
            return;
        }
        uitext.post(new Runnable() {
            @Override
            public void run() {
                uitext.append(message);
            }
        });
    }

    private void resetMessageOnText(final  String messsage){
        if (!uiactive){
            return;
        }
        uitext.post(new Runnable() {
            @Override
            public void run() {
                uitext.setText(messsage);
            }
        });
    }
    private String buildMessage(String imessage, Class<?> c,LogLevel level){
        Thread th=Thread.currentThread();
        String appmessage = Utility.getCurrentTimestampString() + "["+level + "] (" +th.getName()+ ") " + c.getSimpleName()+": "+ imessage+ "\r\n";
        logmessage.append(appmessage);
        return appmessage;
    }

    public void trace(Class<?> c, String message){
        if (!(runlevel > LogLevel.TRACE.getIntvalue()) ){
            String addmessage = buildMessage(message, c, LogLevel.TRACE);
            if (uiactive && uitext != null){
                addMessageOnText(addmessage);
            }

            Log.v(c.getName(),addmessage);
        }
    }
    public void info(Class<?> c, String message){
        if (!(runlevel > LogLevel.INFO.getIntvalue()) && uitext != null){
            String addmessage =buildMessage(message, c, LogLevel.INFO);

            addMessageOnText(addmessage);
            Log.i(c.getName(),addmessage);
        }
    }
    public void debug(Class<?> c, String message){
        if (!(runlevel > LogLevel.DEBUG.getIntvalue())){

            String addmessage = buildMessage(message, c,LogLevel.DEBUG);
            if (uiactive && uitext != null){
                addMessageOnText(addmessage);
            }

            Log.d(c.getName(),addmessage);
        }
    }

    public void warn(Class<?> c, String message){
        if (!(runlevel > LogLevel.WARNING.getIntvalue())){
            String addmessage =buildMessage(message, c,LogLevel.WARNING);
            if (uiactive && uitext != null){
                addMessageOnText(addmessage);
            }

            Log.w(c.getName(),addmessage);
        }
    }

    @Override
    public void warn(Class<?> c, Throwable message) {

    }

    public void error(Class<?> c, String message){
        if (!(runlevel > LogLevel.ERROR.getIntvalue())){
            String addmessage =buildMessage(message, c,LogLevel.ERROR);
            if (uiactive && uitext != null){
                addMessageOnText(addmessage);
            }
            Log.e(c.getName(),addmessage);
        }
    }

    @Override
    public void error(Class<?> c, Throwable ex) {

    }

    public void error(Class<?> c, Exception ex){
        if (!(runlevel > LogLevel.ERROR.getIntvalue())){
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            String addmessage =buildMessage(exceptionAsString, c,LogLevel.ERROR);
            if (uiactive && uitext != null){
                addMessageOnText(addmessage);
            }

            Log.e(c.getName(), ex.getMessage(),ex);
        }
    }

    public void resetMessage(){
        logmessage=new StringBuilder();
        resetMessageOnText("");
    }

    public void setUIActiveFlg(boolean flg){
        uiactive=flg;
    }

    public void revokeUIMessage(){
        if (uiactive && uitext != null){
            addMessageOnText("");
        }
    }
}

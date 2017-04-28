package com.hpe.xzy.myapplication;

import android.content.res.Resources;
import android.widget.TextView;

/**
 * Created by xuzhenya on 11/1/2016.
 */

public class StatusMessageController {
    enum StatusLevel{
        INFO(0),
        WARN(1),
        ERROR(2);
        private int intlevel=0;
        StatusLevel(int intlevel){
            this.intlevel=intlevel;
        }
    }
    private  int[] colorinfo= null;
    private  int[] fontcolorinfo= null;
    private TextView view = null;
    private  Resources res = null;
    private static StatusMessageController instance=null;

    private StatusMessageController(TextView view){
        this.view=view;
        res = view.getResources();
        colorinfo = new int[]{res.getColor(R.color.colorStatusBackInfo),res.getColor(R.color.colorStatusBackWarn),res.getColor(R.color.colorStatusBackError)};
        fontcolorinfo = new int[]{res.getColor(R.color.colorStatusFontInfo),res.getColor(R.color.colorStatusFontWarn),res.getColor(R.color.colorStatusFontError)};
    }
    public static synchronized StatusMessageController getController(TextView view){
        if (instance == null && view !=null){
            instance=new StatusMessageController(view);
        }
        return instance;
    }

    public void setMessage(String messageid, Object[] messageobj, StatusLevel level){
        int mid=res.getIdentifier(messageid,"string",view.getContext().getPackageName());

    }
    public void setMessage(int messageid, Object[] messageobj, StatusLevel level){
        String dmessage = null;
        if (messageobj !=null && messageobj.length>0){
            dmessage=res.getString(messageid,messageobj);
        }else{
            dmessage= res.getString(messageid);
        }
        doSetMessage(dmessage,level);

    }

    private void doSetMessage(final String message, final StatusLevel level){
        view.post(new Runnable() {
            @Override
            public void run() {
                view.setText(message);
                view.setBackgroundColor(colorinfo[level.intlevel]);
                view.setTextColor(fontcolorinfo[level.intlevel]);
            }
        });
    }
    public void clearMessage(){
        doSetMessage("",StatusLevel.INFO);

    }
}

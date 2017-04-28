package com.hpe.xzy.auto.travian.sendtroop;

import com.hpe.xzy.auto.common.log.api.ILogger;
import com.hpe.xzy.auto.common.task.api.ITask;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.TextView;


import com.hpe.xzy.auto.common.log.UILogger;
import android.view.View;
import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;
import com.hpe.xzy.auto.common.utility.Utility;
import com.hpe.xzy.auto.travian.sendtroop.entity.base.Account;
import com.hpe.xzy.auto.travian.sendtroop.runtime.base.TaskBase;
import android.os.PowerManager.WakeLock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuzhenya on 10/27/2016.
 */
public class SendTroopPlatform {
    private ILogger.LogLevel loglevel = ILogger.LogLevel.TRACE;
    private static SendTroopPlatform ourInstance = new SendTroopPlatform();
    private View rootView = null;
    private ILogger currentLogger = null;
    private List<ITask> runTasks=new ArrayList<ITask>();
    public Activity mainAct=null;
    WakeLock wakeLock = null;
    Object lock = new Object();
    public static SendTroopPlatform getInstance() {
        return ourInstance;
    }
    private Map<String,Account> runAccounts = new HashMap<String,Account>();
    private Account currentAccount=null;
    private boolean isActive=false;
    private SendTroopPlatform() {
    }

    public void runFront(){
        isActive=true;

    }

    public void moveToBack(){
        isActive=false;

    }

    public boolean getActiveStatus(){
        return isActive;
    }

    public void setRootView (View view){
        this.rootView=view;
    }

    public View getRootView(){
        return rootView;
    }
    public IWorkFlowNode getWorkFlowByName(String actionname){
        return null;
    }

    public ILogger getLogger(TextView txt){
        if (txt != null)
            currentLogger =  UILogger.getLogger(txt, loglevel);

        return currentLogger;
    }
    public void setCurrentAccount(Account acc){
        this.currentAccount=acc;
        addRunAccount(acc);
    }
    public Account getCurrentAccount(){
        return currentAccount;
    }
    public void addRunAccount(Account acc){
        String key=acc.getUsername()+ "_" + acc.getServerurl();
        runAccounts.put(key,acc);
    }
    public Account getAccountForUser(String username,String serverurl){
        String key=username+ "_" + serverurl;
        return runAccounts.get(key);
    }
    public void runTask(TaskBase task){
        synchronized (lock){
            Thread th = new Thread(task);
            String name=task.getName();
            if (name != null){
                th.setName(task.getName());
            }
            AsyncTask.execute(th);
            runTasks.add(task);
            doWaitLock();
        }

    }

    public void removeTask(ITask task){
        synchronized (lock){
            if (runTasks.contains(task)) {
                runTasks.remove(task);
            }
            if (runTasks.isEmpty()){
                releaseWaitLock();
            }
        }
    }

    private void doWaitLock(){
        currentLogger.info(this.getClass(),"doWaitLock");
        if (mainAct == null){
            currentLogger.info(this.getClass(),"mainAct is null");
            return;
        }
        if (wakeLock == null){
            currentLogger.info(this.getClass(),"get wakeLock");
            PowerManager pm = (PowerManager)mainAct.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TravianSendTroop");
            if (null != wakeLock)
            {
                wakeLock.acquire();
                currentLogger.info(this.getClass(),"wakeLock acquire");
            } else{
                currentLogger.info(this.getClass(),"wakeLock is null");
            }
        }else{
            currentLogger.info(this.getClass(),"wakeLock exits");
        }
    }

    public void releaseWaitLock(){
        if (null != wakeLock)
        {
            currentLogger.info(this.getClass(),"do release");
            wakeLock.release();
            wakeLock = null;
        } else{
            currentLogger.info(this.getClass(),"releaseWaitLock wakeLock is null");
        }
    }
}

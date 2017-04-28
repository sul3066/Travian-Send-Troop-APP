package com.hpe.xzy.myapplication;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ScrollView;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;
import com.hpe.xzy.auto.common.log.api.ILogger;
import com.hpe.xzy.auto.common.task.api.ITask;
import com.hpe.xzy.auto.common.utility.Utility;
import com.hpe.xzy.auto.travian.sendtroop.SendTroopPlatform;

import com.hpe.xzy.auto.travian.sendtroop.entity.Context;
import com.hpe.xzy.auto.travian.sendtroop.entity.base.Account;
import com.hpe.xzy.auto.travian.sendtroop.entity.base.TaskInfoBase;
import com.hpe.xzy.auto.travian.sendtroop.entity.base.Village;
import com.hpe.xzy.auto.travian.sendtroop.entity.task.AttackTaskInfo;
import com.hpe.xzy.auto.travian.sendtroop.runtime.AttackTask;
import com.hpe.xzy.auto.travian.sendtroop.runtime.BuildVillageListTask;
import com.hpe.xzy.auto.travian.sendtroop.runtime.LoginTask;
import com.hpe.xzy.auto.travian.sendtroop.runtime.base.TaskBase;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    SendTroopPlatform plt = SendTroopPlatform.getInstance();
    Button btnLogin = null;
    Button btnAddTask = null;
    Application app = null;
    ScrollView scrollview = null;
    TextView txtmsg = null;
    ILogger logger = null;
    Spinner spinSendType = null;
    ImageView[] images = null;
    EditText[] txtTroops=null;
    EditText txtUser = null;
    EditText txtPassword = null;
    Spinner spinServer = null;
    Spinner spinVillage = null;
    Spinner spinTarget1 = null;
    Spinner spinTarget2 = null;
    EditText txtreachtime=null;
    EditText txtTargetX=null;
    EditText txtTargetY=null;
    EditText txtProxy=null;
    CheckBox chkUseProx=null;
    View vinlogin=null;
    View vinsend=null;
    int troopcount=11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plt.setRootView(findViewById(R.id.activity_main));

        txtmsg = (TextView) findViewById(R.id.txt_message);
        logger = plt.getLogger(txtmsg);
        plt.runFront();
        plt.mainAct=this;
        logger.info(this.getClass(), "Main activity on Create");
        StatusMessageController.getController((TextView)findViewById(R.id.txt_status));
        btnLogin = (Button)findViewById(R.id.btn_login);
        btnAddTask= (Button)findViewById(R.id.btn_addtask);
        app=getApplication();
        spinSendType= (Spinner)findViewById(R.id.lst_senttype);
        scrollview = (ScrollView)findViewById(R.id.scl_msg);
        txtUser = (EditText)findViewById(R.id.txt_username);
        txtPassword = (EditText)findViewById(R.id.txt_password);
        spinServer = (Spinner)findViewById(R.id.lst_server);
        spinVillage = (Spinner)findViewById(R.id.lst_village);
        spinTarget1= (Spinner)findViewById(R.id.lst_target1);
        spinTarget2= (Spinner)findViewById(R.id.lst_target2);
        txtProxy= (EditText)findViewById(R.id.txt_proxyhost);
        txtreachtime = (EditText)findViewById(R.id.txt_reachtime);
        txtTargetX=(EditText)findViewById(R.id.txt_targetx);
        txtTargetY=(EditText)findViewById(R.id.txt_targety);
        chkUseProx=(CheckBox) findViewById(R.id.chkProxy);
        vinlogin=findViewById(R.id.in_login);
        vinsend=findViewById(R.id.in_send_troop);



        images = new ImageView[troopcount];
        txtTroops = new EditText[troopcount];
        View vroot=findViewById(R.id.activity_main);
        for(int i=0; i < troopcount; ++i){
            images[i]= (ImageView)vroot.findViewWithTag("T"+i);
            txtTroops[i]=(EditText)vroot.findViewWithTag("IN_T"+i);
        }

        createSendTypeItems();
        createTargetItems();
        btnLogin.setOnClickListener(new LoginAction(logger));
        btnAddTask.setOnClickListener(new AddTaskAction(logger));
        btnAddTask= (Button)findViewById(R.id.btn_clearmessage);
        btnAddTask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //logger.resetMessage();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        logger.info(this.getClass(), "Main activity onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        logger.info(this.getClass(), "Main activity onPostCreate");
        txtmsg = (TextView) findViewById(R.id.txt_message);
        logger = plt.getLogger(txtmsg);
        plt.runFront();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        logger.info(this.getClass(), "Main activity onPostResume");
        super.onPostResume();
        plt.runFront();
    }

    @Override
    protected void onStart() {
        txtmsg = (TextView) findViewById(R.id.txt_message);
        logger = plt.getLogger(txtmsg);
        plt.runFront();
        logger.info(this.getClass(), "Main activity onStart");
        super.onStart();

    }

    @Override
    protected void onStop() {
        logger.info(this.getClass(), "Main activity onStop");
        super.onStop();
        plt.moveToBack();
    }

    @Override
    protected void onDestroy() {
        logger.info(this.getClass(), "Main activity onDestroy");
        plt.releaseWaitLock();
        super.onDestroy();

    }

    private void createSendTypeItems(){
        Resources res=getResources();
        String packagname=getPackageName();
        String[] sendtype= res.getStringArray(R.array.SendTypeList);
        int arraysize = sendtype.length;
        String[] labels = new String[arraysize];
        for (int i =0; i< arraysize; ++i){
            int rid =res.getIdentifier("sendtype_"+sendtype[i], "string", packagname);
            labels[i]=res.getString(rid);
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, labels);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinSendType.setAdapter(spinnerArrayAdapter);
        spinSendType.setTag(sendtype);
    }

    private void createTargetItems(){
        Resources res=getResources();
        String packagname=getPackageName();
        String[] targetlist= res.getStringArray(R.array.TargetList);
        int arraysize = targetlist.length;
        String[] labels = new String[arraysize];
        for (int i =0; i< arraysize; ++i){
            int rid =res.getIdentifier("target_"+targetlist[i], "string", packagname);
            labels[i]=res.getString(rid);
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, labels);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinTarget1.setAdapter(spinnerArrayAdapter);
        spinTarget1.setTag(targetlist);
        spinTarget2.setAdapter(spinnerArrayAdapter);
        spinTarget2.setTag(targetlist);
    }
    class LoginAction implements OnClickListener{
        StatusMessageController scontoller=null;
        ILogger logger = null;
        SendTroopPlatform plt = SendTroopPlatform.getInstance();

        public LoginAction(ILogger logger){
            scontoller= StatusMessageController.getController(null);
            this.logger=logger;
        }
        @Override
        public void onClick(View view) {
            try{
                setButtonEnabled(false);
                String username= txtUser.getText().toString();
                String password =txtPassword.getText().toString();
                if (username==null|| username.length()<1){
                    scontoller.setMessage(R.string.m_000_002,null, StatusMessageController.StatusLevel.WARN);
                    txtUser.requestFocus();
                    setButtonEnabled(true);
                    return;
                }

                if (password==null|| password.length()<1){
                    scontoller.setMessage(R.string.m_000_003,null, StatusMessageController.StatusLevel.WARN);
                    txtPassword.requestFocus();
                    setButtonEnabled(true);
                    return;
                }

                String proxyurl=txtProxy.getText().toString();
                String proxyhost=null;
                boolean useproxy=chkUseProx.isChecked();
                int proxyport=-1;
                if (proxyurl !=null && proxyurl.length()>0 && useproxy){
                    try {
                        URL uri=new URL(proxyurl);
                        proxyhost=uri.getHost();
                        proxyport=uri.getPort();
                    } catch (MalformedURLException e) {
                        logger.error(this.getClass(),e);
                        scontoller.setMessage(R.string.m_000_004,null, StatusMessageController.StatusLevel.WARN);
                        setButtonEnabled(true);
                        return;
                    }
                }
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                scontoller.clearMessage();
                String server = spinServer.getSelectedItem().toString();

                logger.info(this.getClass(), server);
                Account acc= new Account();

                acc.setUsername(username);
                acc.setPassword(password);
                acc.setServerurl(server);

                TaskInfoBase ibase = new TaskInfoBase();
                ibase.setAccount(acc);

                if (proxyhost!=null){
                    acc.setHttpproxy(proxyhost,proxyport);
                }

                Context context= new Context(logger,ibase);

                LoginTask task = new LoginTask(context);
                task.setName("LoginTask-" + acc.getUsername());

                task.setCallback(this, "loginFinish");
                plt.runTask(task);
            }catch (Exception ex){
                logger.error(this.getClass(), ex);
            }


        }

        public void loginFinish(ITask task){
            logger.info(this.getClass(), "loginFinish  task:" + task.getClass().getName() );
            plt.removeTask(task);
            setButtonEnabled(true);

            TaskBase btask=(TaskBase) task;
            IWorkFlowNode.ActionStatus status = task.getActionStatus();
            logger.info(this.getClass(),"login finished status=" + status);
            if (status != IWorkFlowNode.ActionStatus.SUCCESS){
                scontoller.setMessage(R.string.m_000_005,null, StatusMessageController.StatusLevel.WARN);
            }else{
                scontoller.setMessage(R.string.m_000_006,null, StatusMessageController.StatusLevel.INFO);

                BuildVillageListTask bvtask = new BuildVillageListTask(btask.getContext(), task.getMessage());
                bvtask.setName("Build-Village-"+btask.getContext().getTaskinfo().getAccount().getUsername());
                bvtask.setCallback(this,"buildVillageListForAccount");
                plt.runTask(bvtask);

            }
        }
        public void buildVillageListForAccount(ITask task){
            logger.info(this.getClass(),"buildVillageListForAccount");
            BuildVillageListTask btask = (BuildVillageListTask) task;
            plt.removeTask(task);
            changeToAddTaskUI(btask.getTaskInfo().getAccount());
        }
        private void setButtonEnabled(final boolean flg){
            btnLogin.post(new Runnable() {
                @Override
                public void run() {
                    btnLogin.setEnabled(flg);
                }
            });
        }

        private void changeToAddTaskUI(final Account acc){
            logger.info(this.getClass(),"set village list");
            View rootview= plt.getRootView();
            plt.setCurrentAccount(acc);

            rootview.post(new Runnable() {
                @Override
                public void run() {
                    View vroot = plt.getRootView();

                    // fill village drop down list
                    ArrayAdapter<String> sArrayAdapter=null;
                    Map<String,Village> vmap=null;
                    List<Village> vlist = acc.getVillageList();
                    int vsize = vlist.size();

                        if (vsize >0){
                            String[] slist=new String[vsize];

                            vmap=new HashMap<String,Village>();
                            for(int i=0; i< vsize; ++i){
                                Village vil=vlist.get(i);
                                String key=vil.getVillageName()+"/"+vil.getNewdid();
                                vmap.put(vil.getVillageName()+"/"+vil.getNewdid(),vil);
                                slist[i]=key;
                            }
                            sArrayAdapter = new ArrayAdapter<String>(vroot.getContext(),android.R.layout.simple_spinner_item, slist);
                        } else{
                            sArrayAdapter = new ArrayAdapter<String>(vroot.getContext(),android.R.layout.simple_spinner_item, new String[]{});
                        }

                        sArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                        spinVillage.setAdapter(sArrayAdapter);
                        spinVillage.setTag(vmap);

                        // set get time
                        txtreachtime.setText(Utility.getCurrentTimestampString());

                        Resources res= getResources();
                        String packagename=getPackageName();
                        int racevalue=acc.getRace().getIntvalue();
                        int setsize=troopcount-1;
                        // set images
                        for(int i=0; i< setsize; ++i){
                            ImageView im=images[i];
                            String resourcename="t"+(racevalue+1)+""+i;
                            int mid=res.getIdentifier(resourcename,"drawable",packagename);
                            try {
                                im.setImageResource(mid);
                            }catch (Exception ex){
                                logger.error(this.getClass(),ex);
                            }
                        }

                        // replace sub views
                        vinlogin.setVisibility(View.GONE);
                        vinsend.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    class AddTaskAction implements OnClickListener{
        StatusMessageController scontoller=null;
        ILogger logger = null;
        SendTroopPlatform plt = SendTroopPlatform.getInstance();
        public AddTaskAction(ILogger logger){
            this.logger=logger;
        }
        @Override
        public void onClick(View view) {
            scontoller= StatusMessageController.getController(null);

            // get selected village newdid
            String village=spinVillage.getSelectedItem().toString();
            Map<String,Village> vmap=(Map<String,Village>)spinVillage.getTag();
            String newdid=vmap.get(village).getNewdid();
            logger.info(this.getClass(),"newdid="+newdid);

            // get send type
            long sindex=spinSendType.getSelectedItemId();
            String[] lst=(String[])spinSendType.getTag();
            String sendtype=lst[Integer.parseInt(String.valueOf(sindex))];
            logger.info(this.getClass(),"sendtype="+sendtype);

            // get reach time
            String sertime=txtreachtime.getText().toString();
            Timestamp atm = null;
            try{
                atm = Timestamp.valueOf(sertime.replace("/","-"));
            } catch (Exception ex){
                logger.error(this.getClass(), ex);
                scontoller.setMessage(R.string.m_000_007,null, StatusMessageController.StatusLevel.WARN);
                txtreachtime.requestFocus();
                return;
            }

            //target x
            int targetX=-1;
            try{
                targetX=Integer.parseInt(txtTargetX.getText().toString());
            } catch (Exception ex){
                logger.error(this.getClass(), ex);
                scontoller.setMessage(R.string.m_000_008,null, StatusMessageController.StatusLevel.WARN);
                txtTargetX.requestFocus();
                return;
            }
            //target y
            int targetY=-1;
            try{
                targetY=Integer.parseInt(txtTargetY.getText().toString());
            } catch (Exception ex){
                logger.error(this.getClass(), ex);
                scontoller.setMessage(R.string.m_000_009,null, StatusMessageController.StatusLevel.WARN);
                txtTargetY.requestFocus();
                return;
            }
            if (targetX<-400 || targetX>400){
                scontoller.setMessage(R.string.m_000_008,null, StatusMessageController.StatusLevel.WARN);
                txtTargetX.requestFocus();
                return;
            }
            if (targetY<-400 || targetY>400){
                scontoller.setMessage(R.string.m_000_009,null, StatusMessageController.StatusLevel.WARN);
                txtTargetX.requestFocus();
                return;
            }

            // target 1
            String target1=null;
            sindex=spinTarget1.getSelectedItemId();
            lst=(String[])spinTarget1.getTag();
            target1=lst[Integer.parseInt(String.valueOf(sindex))];

            // target 2
            String target2=null;
            sindex=spinTarget2.getSelectedItemId();
            lst=(String[])spinTarget2.getTag();
            target2=lst[Integer.parseInt(String.valueOf(sindex))];

            String[] troopinfo=new String[troopcount];
            // gen troop info
            int sTroopCount=0;
            for(int i=0; i< troopcount; ++i){
               String tv=txtTroops[i].getText().toString();
                if (tv!=null && tv.length()>0){
                    troopinfo[i]=tv;
                    sTroopCount+=Integer.parseInt(tv);
                }
            }
            if (sTroopCount<1){
                scontoller.setMessage(R.string.m_000_010,null, StatusMessageController.StatusLevel.WARN);
                txtTroops[0].requestFocus();
                return;
            }

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            try{
                scontoller.clearMessage();

                AttackTaskInfo info=new AttackTaskInfo(targetX,targetY,atm,troopinfo,target1,target2,sendtype);
                info.setVillageId(newdid);
                Account acc=plt.getCurrentAccount();
                info.setAccount(acc);
                Context context= new Context(logger,info);
                AttackTask task = new AttackTask(context);
                task.setCallback(this, "finishAttack");
                task.setName("AttackTask-"+acc.getUsername());
                plt.runTask(task);
            }catch (Exception ex){
                logger.error(this.getClass(),ex);
            }


        }

        public void finishAttack(ITask task){
            logger.info(this.getClass(),"finishAttack");

        }

    }

    class BackAction implements OnClickListener{
        ILogger logger = null;
        SendTroopPlatform plt = SendTroopPlatform.getInstance();
        public BackAction(ILogger logger){
            this.logger=logger;
        }
        @Override
        public void onClick(View view) {
            View rootview= plt.getRootView();
            View vinlogin=rootview.findViewById(R.id.in_login);
            View vinsend=rootview.findViewById(R.id.in_send_troop);
            vinlogin.setVisibility(View.VISIBLE);
            vinsend.setVisibility(View.GONE);

        }

    }
}







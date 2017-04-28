package com.hpe.xzy;

import android.app.Application;
import android.util.Log;

/**
 * Created by xuzhenya on 11/18/2016.
 */

public class XzyApplication extends Application {

    public XzyApplication(){
        super();
        Log.i("MyApp", "new app");
    }

    public void onCreate() {
        Log.i(this.getClass().getName(), "on create");
    }

}
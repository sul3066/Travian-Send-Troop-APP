package com.hpe.xzy.auto.common.utility;

import com.hpe.xzy.auto.common.engine.api.IWorkFlowNode;
import com.hpe.xzy.auto.common.log.api.ILogger;

import java.lang.reflect.InvocationTargetException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 11/13/2016.
 */

public class CallBackOnceTimer {
	Timer tm = new Timer(Thread.currentThread().getName() + "-Timer");

	ILogger logger = null;
	IWorkFlowNode action = null;

	public CallBackOnceTimer(ILogger logger) {
		this(logger, null);
	}

	public CallBackOnceTimer(ILogger logger, IWorkFlowNode action) {
		this.logger = logger;
		this.action = action;
	}

	public void startTimer(long interval, Object caller, String method, Class<?>[] c, Object[] params) {
		TimerTask task = new CallBackTimerTask(caller, method, c, params);
		tm.schedule(task, interval);
	}

	class CallBackTimerTask extends TimerTask {
		Object callobj = null;
		String method = null;
		Object[] callparams = null;
		Class<?>[] c = null;

		public CallBackTimerTask(Object caller, String method, Class<?>[] c, Object[] params) {
			this.callobj = caller;
			this.method = method;
			if (params != null)
				this.callparams = params;
			else
				this.callparams = new Object[] {};

			if (c != null)
				this.c = c;
			else
				this.c = new Class<?>[] {};
		}

		@Override
		public void run() {

			try {
				Utility.callMethodOnObject(callobj, method, c, callparams);
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
				logger.error(this.getClass(), ex);
				if (action != null) {
					action.end(IWorkFlowNode.ActionStatus.ERROR);
				}
			}
		}

	}
}

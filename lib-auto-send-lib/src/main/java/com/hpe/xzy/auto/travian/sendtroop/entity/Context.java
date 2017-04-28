package com.hpe.xzy.auto.travian.sendtroop.entity;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.hpe.xzy.auto.common.log.api.ILogger;
import com.hpe.xzy.auto.travian.sendtroop.entity.base.TaskInfoBase;

/**
 * Created by xuzhenya on 10/31/2016.
 */

public class Context {
	private ILogger logger;
	protected static Map<String, Map<String, HttpCookie>> cookies;
	protected TaskInfoBase taskinfo;

	public Context(ILogger logger, TaskInfoBase taskinfo) {
		this.logger = logger;
		this.taskinfo = taskinfo;
		cookies = new ConcurrentHashMap<String, Map<String, HttpCookie>>();

	}

	public TaskInfoBase getTaskinfo() {
		return taskinfo;
	}

	public void setLogger(ILogger value) {
		logger = value;
	}

	public ILogger getLogger() {
		return logger;
	}

	public Map<String, HttpCookie> getCookieByAccount() {

		Map<String, HttpCookie> strcookie = null;
		String key = taskinfo.getAccount().getUsername() + "_" + taskinfo.getAccount().getServerurl();
		if (cookies.containsKey(key)) {
			strcookie = cookies.get(key);
		} else {
			strcookie = new HashMap<String, HttpCookie>();
			cookies.put(key, strcookie);
		}
		return strcookie;
	}

	public String getCookieStringByAccount() {
		String strCookie = null;
		Map<String, HttpCookie> cookies = getCookieByAccount();

		if (cookies != null) {
			Set<String> keys = cookies.keySet();
			Iterator<String> keyit = keys.iterator();
			while (keyit.hasNext()) {
				String key = keyit.next();
				HttpCookie c = cookies.get(key);
				String name = c.getName();
				String value = c.getValue();
				if (strCookie == null) {
					strCookie = name + "=" + value;
				} else {
					strCookie += ";" + name + "=" + value;
				}
			}
		}
		return strCookie;
	}
}

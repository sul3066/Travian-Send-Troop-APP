package com.hpe.xzy.auto.travian.sendtroop.action;

import com.hpe.xzy.auto.common.utility.CallBackOnceTimer;
import com.hpe.xzy.auto.common.utility.Utility;
import com.hpe.xzy.auto.travian.sendtroop.action.base.HttpBaseAction;
import com.hpe.xzy.auto.travian.sendtroop.entity.Context;
import com.hpe.xzy.auto.travian.sendtroop.entity.task.AttackTaskInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Administrator on 10/31/2016.
 */

public class AttackAction extends HttpBaseAction {
	AttackTaskInfo taskinfo = null;
	private long waiting = -1;

	private CallBackOnceTimer tm = null;

	public AttackAction(Context context) {
		this(context, null);
	}

	public long getWaiting() {
		return waiting;
	}

	public AttackAction(Context context, CallBackOnceTimer tm) {
		super(context);
		taskinfo = (AttackTaskInfo) context.getTaskinfo();
		if (tm != null) {
			this.tm = tm;
		} else {
			tm = new CallBackOnceTimer(this.logger, this);
		}
	}

	@Override
	public void start() {
		logger.info(this.getClass(), "Attack Action start ");
		sendRequest("build.php?id=39&gid=16&tt=2", null, this, "doStep1");

	}

	public void doStep1(HtmlHttpCallBack backobj) {
		logger.info(this.getClass(), "doStep1 response:" + backobj.getReponsecode());
		String responsetext = backobj.getResponseText();

		if (!responsetext.contains("派遣軍隊")) {
			logger.info(this.getClass(), "cannot open send troop screen");
			this.end(ActionStatus.RETRY);
			return;
		}

		String formstr = Utility.getStrBetweenOuter(responsetext, "<form method=\"post\"", "</form>");
		// remove script
		while (formstr.contains("<script")) {
			formstr = Utility.removeFromString(formstr, "<script", "</script>");
		}
		// remove table
		while (formstr.contains("<table")) {
			formstr = Utility.removeFromString(formstr, "<table", "</table>");
		}

		// remove other unnecessary tags
		formstr = Utility.removeFromString(formstr, "<div class=\"destination\"", "</button>");

		// logger.info(this.getClass(), "tablestr:" + tablestr);
		Document doc = null;
		try {
			doc = Utility.phaseToDom(formstr);
		} catch (ParserConfigurationException | SAXException | IOException ex) {
			logger.error(this.getClass(), ex);
		}

		if (doc == null) {
			this.end(ActionStatus.RETRY);
			return;
		}

		Map<String, String> params = new HashMap<String, String>();

		String[] troops = this.taskinfo.getTroopinfo();
		int tcount = troops.length;
		for (int i = 0; i < tcount; ++i) {
			if (troops[i] != null) {
				params.put("t" + (i + 1), troops[i]);
			} else {
				params.put("t" + (i + 1), "");
			}
		}

		NodeList nodes = doc.getElementsByTagName("input");
		int nsize = nodes.getLength();

		for (int i = 0; i < nsize; ++i) {
			Element elem = (Element) nodes.item(i);
			String type = elem.getAttribute("type");
			if ("hidden".equalsIgnoreCase(type)) {
				String name = elem.getAttribute("name");
				String value = elem.getAttribute("value");
				params.put(name, value);
			}
		}

		params.put("dname", "");
		params.put("c", this.taskinfo.getSendType());
		params.put("s1", "ok");
		params.put("x", String.valueOf(this.taskinfo.getTargetx()));
		params.put("y", String.valueOf(this.taskinfo.getTargety()));
		params.put("s1", "ok");

		this.sendRequestByMap("build.php?id=39&tt=2", params, this, "doStep2");
	}

	private Map<String, String> buildPostParameter(String formstr) {
		Document doc = null;
		try {
			doc = Utility.phaseToDom(formstr);
		} catch (ParserConfigurationException | SAXException | IOException ex) {
			logger.error(this.getClass(), ex);
		}

		if (doc == null) {
			this.end(ActionStatus.ERROR);
			return null;
		}

		Map<String, String> params = new HashMap<String, String>();

		NodeList nodes = doc.getElementsByTagName("input");
		int nsize = nodes.getLength();

		for (int i = 0; i < nsize; ++i) {
			Element elem = (Element) nodes.item(i);
			String type = elem.getAttribute("type");
			String name = elem.getAttribute("name");
			String value = elem.getAttribute("value");
			if ("hidden".equalsIgnoreCase(type) || "text".equalsIgnoreCase(type)) {
				params.put(name, value);
			} else if ("Radio".equalsIgnoreCase(type) && "spy".equalsIgnoreCase(name) && "2".equalsIgnoreCase(value)) {
				params.put(name, value);
			}
		}

		nodes = doc.getElementsByTagName("select");
		if (nodes.getLength() > 0) {
			params.put("kata", this.taskinfo.getTarget1());
			params.put("kata2", this.taskinfo.getTarget2());
		}
		String[] troops = this.taskinfo.getTroopinfo();
		int tcount = troops.length;
		for (int i = 0; i < tcount; ++i) {
			if (troops[i] != null) {
				params.put("t" + (i + 1), troops[i]);
			} else {
				params.put("t" + (i + 1), "");
			}
		}
		return params;
	}

	public void doStep2(HtmlHttpCallBack backobj) {
		logger.info(this.getClass(), "doStep2 response:" + backobj.getReponsecode());
		String responsetext = backobj.getResponseText();

		String formstr = Utility.getStrBetweenOuter(responsetext, "<form method=\"post\"", "</form>");
		if (formstr == null || formstr.length() < 1) {
			this.end(ActionStatus.SUCCESS);
			return;
		}

		if (!responsetext.contains("btn_edit")) {
			// error not ok.
			this.message = Utility.getStrBetweenInner(responsetext, "<p class=\"error\">", "</p>");
			logger.info(this.getClass(), "doStep2 error:" + this.message);
			end(ActionStatus.SUCCESS);
			return;
		}

		// remove script
		while (formstr.contains("<script")) {
			formstr = Utility.removeFromString(formstr, "<script", "</script>");
		}

		String tinfostr = Utility.getStrBetweenOuter(formstr, "<table class=\"troop_details\"", "</table>");

		String sDuration = Utility.getStrBetweenInner(tinfostr, "<div class=\"in\">", "</div>");
		sDuration = Utility.getStrBetweenInner(sDuration, " ", " ");
		String sreach = Utility.getStrBetweenOuter(tinfostr, "<span", "</span>");
		sreach = Utility.getStrBetweenInner(sreach, ">", "<");
		long iduration = Utility.getDuration(sDuration);
		logger.info(this.getClass(), "doStep2 duration:" + sDuration);
		logger.info(this.getClass(), "doStep2 sreach:" + sreach);
		logger.info(this.getClass(), "doStep2 iduration:" + iduration);
		if (iduration < 1000) {
			this.end(ActionStatus.ERROR);
			return;
		}
		long ctime = Utility.getCurrentTimeTicket();

		long expTime = this.taskinfo.getReachTime().getTime();
		waiting = expTime - iduration - ctime;
		logger.info(this.getClass(), "wait " + (waiting / 1000) + "s");
		long stticket = Utility.getCurrentTimeTicket();
		Long timeiff = GetTimeDiffAction.getTimeDiffByServer(this.taskinfo.getAccount().getServerurl());
		long edticket = Utility.getCurrentTimeTicket();
		waiting = waiting - edticket + stticket;
		// time not enough
		if (waiting < SendTroopConstant.prepareTime && timeiff == null) {

			logger.error(this.getClass(), "doStep2 error waiting:" + waiting);
			this.end(ActionStatus.ERROR);
			return;
		}

		if (waiting > SendTroopConstant.prepareTime) {
			logger.info(this.getClass(), "not ready wait " + (waiting / 1000) + "s");
			this.end(ActionStatus.NOT_READY);
			return;
		}

		if (timeiff == null) {
			logger.info(this.getClass(), "timeiff is null wait " + (waiting / 1000) + "s");
			this.end(ActionStatus.NOT_READY);
			return;
		}

		// remove table
		while (formstr.contains("<table")) {
			formstr = Utility.removeFromString(formstr, "<table", "</table>");
		}
		Map<String, String> params = null;
		try {
			params = buildPostParameter(formstr);
		} catch (Exception ex) {
			logger.error(this.getClass(), ex);
			end(ActionStatus.ERROR);
			return;
		}

		if (params == null) {
			return;
		}
		waiting -= timeiff;
		logger.info(this.getClass(), "final waiting " + (waiting / 1000) + "s");
		tm.startTimer(waiting, this, "preciseAttack", new Class<?>[] { Map.class }, new Object[] { params });
	}

	public void preciseAttack(Map<String, String> param) {
		logger.info(this.getClass(), "preciseAttack send");
		sendRequestByMap("build.php?id=39&tt=2", param, this, "attackDone");
	}

	public void attackDone(HtmlHttpCallBack callback) {
		logger.info(this.getClass(), "attackDone :" + callback.getReponsecode());
		String respText = callback.getResponseText();
		this.message = respText;
		if (!respText.contains("出擊軍團")) {
			this.end(ActionStatus.ERROR);
			return;
		}
		this.end(ActionStatus.SUCCESS);
	}
}

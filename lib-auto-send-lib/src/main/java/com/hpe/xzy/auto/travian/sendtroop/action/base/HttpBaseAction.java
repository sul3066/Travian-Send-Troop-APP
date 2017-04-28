package com.hpe.xzy.auto.travian.sendtroop.action.base;

import com.hpe.xzy.auto.common.utility.Utility;
import com.hpe.xzy.auto.travian.sendtroop.entity.Context;
import com.hpe.xzy.auto.travian.sendtroop.entity.base.TaskInfoBase;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;

import java.net.Proxy;
import java.net.URL;
import java.net.MalformedURLException;

import java.io.StringWriter;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.net.ProtocolException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Administrator on 10/30/2016.
 */

public abstract class HttpBaseAction extends WorkFlowBase {

	private enum OutPutFormat {
		Dom, HtmlString
	}

	public class HttpCallBack {
		private int responsecode;

		HttpCallBack(int code) {
			responsecode = code;
		}

		public int getReponsecode() {
			return responsecode;
		}

	}

	protected class HtmlHttpCallBack extends HttpCallBack {
		private String responseText;

		HtmlHttpCallBack(int code, String text) {
			super(code);
			responseText = text;
		}

		public String getResponseText() {
			return responseText;
		}
	}

	private class DomHttpCallBack extends HttpCallBack {

		private Document doc;

		DomHttpCallBack(int code, Document dom) {
			super(code);
			doc = dom;
		}

		public Document getDoc() {
			return doc;
		}
	}

	// private HttpCallBack hcallback = null;

	public HttpBaseAction(Context context) {
		super(context);
	}

	private void doHttpCallBack(Object obj, String method, HttpCallBack callbackobj) {
		logger.info(this.getClass(), "callback class= " + callbackobj.getClass().getName());
		// hcallback = callbackobj;
		invokeCallBackMethod(obj, method, new Class[] { callbackobj.getClass() }, new Object[] { callbackobj });
	}

	private void saveCookieInfo(String sCookie) {

		if (sCookie != null && sCookie.length() > 0) {
			logger.debug(this.getClass(), "save cookie=" + sCookie);
			List<HttpCookie> saveCookie = HttpCookie.parse(sCookie);
			Map<String, HttpCookie> oldCookies = context.getCookieByAccount();
			int csize = saveCookie.size();
			for (int i = 0; i < csize; ++i) {
				HttpCookie c = saveCookie.get(i);
				String value = c.getValue();
				if (value != null && value.length() > 0) {
					oldCookies.put(c.getName(), c);
				}
			}
		}
	}

	private void setConnectionHeaders(HttpURLConnection conn, byte[] pdata) throws ProtocolException {
		// set http method
		if (pdata != null && pdata.length > 0) {
			logger.debug(this.getClass(), "http method: POST");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(pdata.length));
		} else {
			logger.debug(this.getClass(), "http method: GET");
			conn.setRequestMethod("GET");
		}

		// set cookies
		String cookiestring = context.getCookieStringByAccount();
		if (cookiestring != null && cookiestring.length() > 0) {
			conn.setRequestProperty("Cookie", cookiestring);
			logger.debug(this.getClass(), "send cookies= " + cookiestring);
		}
		conn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		HttpURLConnection.setFollowRedirects(false);
	}

	private String createPostParameterFromMap(Map<String, String> pmap) {
		String pString = null;
		if (pmap == null) {
			return pString;
		}
		int size = pmap.size();
		if (size < 1) {
			return pString;
		}
		Set<String> keys = pmap.keySet();
		Iterator<String> keyit = keys.iterator();
		while (keyit.hasNext()) {
			String key = keyit.next();
			if (pString == null) {
				pString = key + "=" + pmap.get(key);
			} else {
				pString += "&" + key + "=" + pmap.get(key);
			}
		}
		return pString;
	}

	private String streamtoString(InputStream in) throws UnsupportedEncodingException, IOException {

		int n = 0;
		char[] buffer = new char[1024 * 4];
		InputStreamReader reader = new InputStreamReader(in, "UTF-8");
		StringWriter writer = new StringWriter();
		while (-1 != (n = reader.read(buffer)))
			writer.write(buffer, 0, n);
		return writer.toString();
	}

	private URL getUrl(String url) throws MalformedURLException {
		TaskInfoBase itask = context.getTaskinfo();
		String baseurl = itask.getAccount().getServerurl();

		String myurl = "http://" + baseurl + "/" + url;
		if (itask.getVillageId() != null) {
			myurl += (url.indexOf("?") > 0) ? "&" : "?";
			myurl += "newdid=" + itask.getVillageId();
		}
		return new URL(myurl);
	}

	protected void sendRequestByMap(String url, Map<String, String> parameters, Object caller, String method) {
		this.sendRequestByMap(url, parameters, OutPutFormat.HtmlString, caller, method);
	}

	private void sendRequestByMap(String url, Map<String, String> parameters, OutPutFormat format, Object caller,
			String method) {
		String parma = createPostParameterFromMap(parameters);
		this.sendRequest(url, parma, format, caller, method);

	}

	protected void sendRequest(String url, String parameters, Object caller, String method) {
		sendRequest(url, parameters, OutPutFormat.HtmlString, caller, method);
	}

	private void sendRequest(String url, String parameters, OutPutFormat format, Object caller, String method) {
		logger.info(this.getClass(), "send request");
		URL myurl = null;

		// compose url
		try {
			myurl = getUrl(url);
		} catch (MalformedURLException ex) {
			logger.error(this.getClass(), ex.getMessage());
			this.end(ActionStatus.ERROR);
			return;
		}
		logger.debug(this.getClass(), "send request to " + myurl.toString() + " post data:" + parameters);
		// compile post data
		byte[] postdata = null;
		if (parameters != null && parameters.length() > 0) {
			try {
				postdata = parameters.getBytes("UTF-8");
			} catch (java.io.UnsupportedEncodingException ex) {
				logger.error(this.getClass(), ex.getMessage());
				this.end(ActionStatus.ERROR);
				return;
			}
		}

		HttpURLConnection conn = null;
		Proxy proxy = context.getTaskinfo().getAccount().getHttpproxy();
		try {
			if (proxy != null) {
				logger.trace(this.getClass(), "new connection with proxy");
				conn = (HttpURLConnection) myurl.openConnection(proxy);
			} else {
				logger.trace(this.getClass(), "new connection with no proxy");
				conn = (HttpURLConnection) myurl.openConnection();
			}
		} catch (IOException ex) {
			logger.error(this.getClass(), ex.getMessage());
			this.end(ActionStatus.ERROR);
			return;
		}
		try {
			setConnectionHeaders(conn, postdata);
		} catch (ProtocolException ex) {
			logger.error(this.getClass(), ex.getMessage());
			this.end(ActionStatus.ERROR);
		}

		// start connect
		try {
			conn.connect();
		} catch (IOException ex) {
			logger.error(this.getClass(), ex.getMessage());
			this.end(ActionStatus.ERROR);
		}

		OutputStream sout = null;
		if (postdata != null && postdata.length > 0) {
			logger.trace(this.getClass(), "send paramter");
			try {
				sout = conn.getOutputStream();
				sout.write(postdata);
				sout.flush();
				sout.close();
				sout = null;
			} catch (IOException ex) {
				logger.error(this.getClass(), ex.getMessage());
				if (sout != null) {
					try {
						sout.close();
					} catch (IOException ex1) {
						logger.error(this.getClass(), "inside catch " + ex.getMessage());
					}
				}
				conn.disconnect();
				this.end(ActionStatus.ERROR);
				return;
			}
		}

		InputStream in = null;
		// get response code
		int response = -1;

		try {
			response = conn.getResponseCode();

			saveCookieInfo(conn.getHeaderField("Set-Cookie"));

			logger.debug(this.getClass(), "return http code=" + response);
			if (response > 299 && response < 400) {
				String nurl = conn.getHeaderField("Location");
				logger.debug(this.getClass(), "redirect url:=" + nurl);
				if (nurl != null) {
					URL newurl = null;
					if (nurl.contains(this.context.getTaskinfo().getAccount().getServerurl())) {
						newurl = new URL(nurl);
					} else {
						newurl = getUrl(nurl);
					}

					if (proxy != null) {
						logger.trace(this.getClass(), "redirect connection with proxy");
						conn = (HttpURLConnection) newurl.openConnection(proxy);
					} else {
						logger.trace(this.getClass(), "redirect connection with proxy");
						conn = (HttpURLConnection) newurl.openConnection();
					}
					setConnectionHeaders(conn, null);
					response = conn.getResponseCode();
					logger.debug(this.getClass(), "redirect return http code=" + response);
				}
			}

		} catch (IOException ex) {
			logger.error(this.getClass(), ex);
			conn.disconnect();
			this.end(ActionStatus.ERROR);
			return;
		}

		if (response == HttpURLConnection.HTTP_OK) {
			// save cookie information
			saveCookieInfo(conn.getHeaderField("Set-Cookie"));
			try {
				in = conn.getInputStream();
			} catch (IOException ex) {
				logger.error(this.getClass(), ex);
				conn.disconnect();
				this.end(ActionStatus.ERROR);
				return;
			}
		} else {
			this.end(ActionStatus.ERROR);
			return;
		}

		// get response html text
		String responseText = null;
		Document doc = null;
		HttpCallBack callbackobj = null;
		try {
			if (format == OutPutFormat.Dom) {
				doc = Utility.phaseToDom(in);
				callbackobj = new DomHttpCallBack(response, doc);
			} else if (format == OutPutFormat.HtmlString) {
				responseText = streamtoString(in);
				callbackobj = new HtmlHttpCallBack(response, responseText);
			}
		} catch (IOException | SAXException | ParserConfigurationException ex) {
			logger.error(this.getClass(), ex);
			this.end(ActionStatus.ERROR);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					logger.error(this.getClass(), ex);
					this.end(ActionStatus.ERROR);
				}

			}
			conn.disconnect();
		}
		doHttpCallBack(caller, method, callbackobj);
	}

}

package com.ryx.controller.wx;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;

import com.ryx.util.common.ConfigUtils;


@SuppressWarnings("deprecation")
public class wxopenidcallback extends HttpServlet implements Servlet {
	/**
	 * 微信获取OpenID
	 */
	private static final long serialVersionUID = 2146201574962029471L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	// 获取微信openid
	@SuppressWarnings("resource")
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		String openidjumppage = (String) request.getSession().getAttribute(
				"openidjumppage");
		String qds = request.getParameter("qds");
		System.out.println("callbackqds"+qds);
		try {
			String code = request.getParameter("code");
			String result = "";
			if (!StringUtils.isBlank(code)) {
				// 获取设置
				String appid = ConfigUtils.getProperty("wx_appid_"+qds,
						"wxb64de1546c7b9e62");
				String appsecret = ConfigUtils.getProperty("wx_app_secret_"+qds,
						"0cc5f8c95b4cb7228a8eab4edceb5946");

				String Url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
						+ appid
						+ "&secret="
						+ appsecret
						+ "&code="
						+ code
						+ "&grant_type=authorization_code";
				HttpClient client = new DefaultHttpClient();
				HttpGet HttpGet = new HttpGet(Url);
				HttpResponse httpResponse = client.execute(HttpGet);
				result = EntityUtils
						.toString(httpResponse.getEntity(), "UTF-8");
				// System.out.println("result:"+result);
				if (!StringUtils.isBlank(result)) {
					JSONObject jsonobj = new JSONObject(result);
					// 获取openid
					String openid = "";
					try {
						openid = jsonobj.getString("openid");
					} catch (Exception e) {
						System.out.println("获取openid为空");
						e.printStackTrace();
					}
					if (!StringUtils.isBlank(openid)) {
						request.getSession().setAttribute("openid", openid);
					}

					String scope = (String) request.getSession().getAttribute(
							"wxscope");
					if ("snsapi_userinfo".equals(scope)) { // 获取用户信息
						String access_token = "";
						try {
							access_token = jsonobj.getString("access_token");
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (!StringUtils.isBlank(access_token)&&!StringUtils.isBlank(openid)) {
							Url = "https://api.weixin.qq.com/sns/userinfo?access_token="
									+ access_token
									+ "&openid="
									+ openid
									+ "&lang=zh_CN";
							HttpGet = new HttpGet(Url);
							httpResponse = client.execute(HttpGet);
							result = EntityUtils.toString(
									httpResponse.getEntity(), "UTF-8");
							// System.out.println("result:"+result);
							if (!StringUtils.isBlank(result)) {
								jsonobj = new JSONObject(result);
								// 获取用户信息
								String nickname = "";
								try {
									nickname = jsonobj.getString("nickname");
								} catch (Exception e) {
								}
								if (!StringUtils.isBlank(nickname)) {
									request.getSession().setAttribute(
											"nickname", nickname);
								}
								String sex = "";
								try {
									sex = jsonobj.getString("sex");
								} catch (Exception e) {
								}
								// System.out.println("sex:"+sex);
								if (!StringUtils.isBlank(sex)) {
									request.getSession().setAttribute("sex",
											sex);
								}
								String headimgurl = "";
								try {
									headimgurl = jsonobj
											.getString("headimgurl");
								} catch (Exception e) {
								}
								// System.out.println("headimgurl:"+headimgurl);  
								if (!StringUtils.isBlank(headimgurl)) {
									request.getSession().setAttribute(
											"headimgurl", headimgurl);
								}
							}
						}
					}
				}
			}
			if (!StringUtils.isBlank(openidjumppage)) {
				request.getSession().setAttribute("openidcallback", "true");
				response.sendRedirect(openidjumppage);
				return;
			}
			response.getWriter().write(
					"code:" + code + "<br>" + "result:" + result);
		} catch (Exception e) {
			e.printStackTrace();
			if (!StringUtils.isBlank(openidjumppage)) {
				request.getSession().setAttribute("openidcallback", "true");
				response.sendRedirect(openidjumppage);
				return;
			}
		}
	}
}

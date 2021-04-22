package com.ryx.controller.wx;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.ryx.util.common.ConfigUtils;


public class wxopenid extends HttpServlet implements Servlet {
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
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		try {
			String scope = (String) request.getSession()
					.getAttribute("wxscope");
			if (StringUtils.isBlank(scope))
				scope = "snsapi_base";

			String portstr = "";
			int port = request.getServerPort();
			if (port != 80) {
				portstr = ":" + port;
			}
			String callbackurl = request.getScheme() + "://"
					+ request.getServerName() + request.getContextPath()
					+ "/wxopenidcallback";
			callbackurl = URLEncoder.encode(callbackurl, "UTF-8");

			// 获取设置
			String appid = ConfigUtils.getProperty("wx_appid",
					"wxb64de1546c7b9e62");
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
					+ appid
					+ "&redirect_uri="
					+ callbackurl
					+ "&response_type=code&scope=" + scope + "#wechat_redirect";
			response.sendRedirect(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

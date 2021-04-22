package com.ryx.controller.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.ryx.util.common.ConfigUtils;

/**
 * 获取OpenID过滤器
 */
public class OpenIDFilter implements Filter {

	public void init(FilterConfig config) {
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String currentURL = request.getRequestURI();
		if (!StringUtils.isBlank(request.getQueryString())) {
			currentURL = currentURL + "?" + request.getQueryString();
		}

		String openidcallback = (String) request.getSession().getAttribute("openidcallback");
		request.getSession().setAttribute("openidcallback", null);
		if ("true".equals(openidcallback)) {
			filterChain.doFilter(request, response);
			return;
		}
		if (currentURL.contains(".")) { // 非Servlet不获取OpenID
			filterChain.doFilter(request, response);
			return;
		}

		boolean needGetOpenid = false; // 需要获取OpenID
		List<String> needopenids = new ArrayList<String>(); // 需要获取OpenID
		needopenids.add("/IndexServlet");
		needopenids.add("/ProdServlet");
		needopenids.add("/LoginServlet");
		needopenids.add("/RegisterServlet");
		needopenids.add("/entrance");
		needopenids.add("/UserServlet");
		
		
		for (String needopenid : needopenids) {
			if (currentURL.indexOf(needopenid) >= 0) {
				needGetOpenid = true;
				break;
			}
		}
		if (!needGetOpenid) {
			filterChain.doFilter(request, response);
			return;
		}

		String openid = (String) request.getSession().getAttribute("openid");
		String scope = "snsapi_base";
		String user_type = check_userHeader(request);
		if ("WX".equals(user_type)) {
			try {
				List<String> needuserinfos = new ArrayList<String>(); // 微信需要用户授权页面
//				needuserinfos.add("/MyInfoServlet?action=myInfoInit");
				needuserinfos.add("/RegisterServlet?action=ShowRegister");
				needuserinfos.add("/RegisterServlet?action=saveRegister");
				needuserinfos.add("/LoginServlet?action=queryUser");
				needuserinfos.add("/LoginServlet?action=saveUser");
				needuserinfos.add("/UserServlet?action=init");
				for (String needuserinfo : needuserinfos) {
					if (currentURL.indexOf(needuserinfo) >= 0) {
						scope = "snsapi_userinfo";
						break;
					}
				}
				if ("snsapi_userinfo".equals(scope)) {
					String nickname = (String) request.getSession().getAttribute("nickname");
					if (!StringUtils.isBlank(openid) && !StringUtils.isBlank(nickname)) { // 已有OpenID及昵称不重复获取
						filterChain.doFilter(request, response);
						return;
					}
				}
			} catch (Exception e) {
				filterChain.doFilter(request, response);
				return;
			}
		}

		if ("snsapi_base".equals(scope)) {
			if (!StringUtils.isBlank(openid)) { // 已有OpenID不重复获取
				filterChain.doFilter(request, response);
				return;
			}
		}

//		System.out.println("调微信获取opendid");
		if ("WX".equals(user_type)) { // 微信
			try {
				// 获取openid
				String wx_safe_domain = ConfigUtils.getProperty("wx_safe_domain", "www.rongyixin.net");
				
				if (request.getServerName().equalsIgnoreCase(wx_safe_domain)) {// 此域名才可以取到openid
					// 当前地址放入session，方便回调
					String jumppage = request.getRequestURL().toString();
					if (!StringUtils.isBlank(request.getQueryString())) {
						jumppage = jumppage + "?" + request.getQueryString();
					}
					request.getSession().setAttribute("openidjumppage", jumppage);
					request.getSession().setAttribute("wxscope", scope); // scope
					// 存入session
					response.sendRedirect("wxopenid");
					return;
				} else {
					System.out.println("非安全回调域名，无法获取微信OpenID:" + currentURL);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("调微信获取opendid异常");
				filterChain.doFilter(request, response);
				return;
			}
			System.out.println("调微信获取opendid完成");
			 openidcallback = (String) request.getSession().getAttribute("openidcallback");
			 System.out.println("调微信获取opendid完成后openidcallback："+openidcallback);
			filterChain.doFilter(request, response);
			return;
		}

		if ("ZFB".equals(user_type)) { // 支付宝
			String zfb_safe_domain = ConfigUtils.getProperty("zfb_safe_domain", "www.rongyixin.net");
			if (request.getServerName().toLowerCase().contains(zfb_safe_domain)) {// 此域名才可以取到支付宝ID
				// 当前地址放入session，方便回调
				String jumppage = request.getRequestURL().toString();
				if (!StringUtils.isBlank(request.getQueryString())) {
					jumppage = jumppage + "?" + request.getQueryString();
				}
				request.getSession().setAttribute("openidjumppage", jumppage);
				response.sendRedirect("zfbpid");
				return;
			} else {
				System.out.println("非安全回调域名，无法获取支付宝ID:" + currentURL);
			}
			filterChain.doFilter(request, response);
			return;
		}
		filterChain.doFilter(request, response);
	}

	// 检查用户浏览器是什么类型
	public static String check_userHeader(HttpServletRequest request) {
		String user_type = "OTHERS";
		String user_header = request.getHeader("user-agent");

		if (user_header.indexOf("MicroMessenger") > 0) { // 微信
			user_type = "WX";
		} else if (user_header.indexOf("AlipayClient") > 0) { // 支付宝
			user_type = "ZFB";
		}
		;
		return user_type;
	}

	public void destroy() {
	}

}

package com.ryx.service.index;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import com.alibaba.fastjson.JSON;
import com.ryx.dao.HibernateSessionFactory;
import com.ryx.util.common.ConfigUtils;
import com.ryx.util.common.HttpUtil;
import com.ryx.util.common.SimpleValueFilter;
import com.ryx.util.common.WxUtil;

import net.sf.json.JSONObject;

public class IndexServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = -7538278814186489348L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		// 2.通知浏览器，以UTF-8的编码打开
		response.setContentType("text/html;charset=UTF-8");
		String action = request.getParameter("action");
		request.setCharacterEncoding("UTF-8");
		try {
			// 初始化不传参
			if (action == null || "".equals(action)) {
				init(request, response);
			} else if ("khInit".equals(action)) {
				khInit(request, response);
			} else if ("wdtdInit".equals(action)) {// 我的团队初始化入口
				wdtdInit(request, response);
			} else if ("queryInit".equals(action)) {// 首页初始化查询logo、标题、轮播图、放款数据
				queryInit(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 首页初始化查询logo、标题、轮播图、放款数据
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void queryInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		String qds = request.getParameter("qds");
		if(qds == null || "".equals(qds)){
			qds =  (String) request.getSession().getAttribute("qds");
		}
		try {
			Session session = HibernateSessionFactory.getSession();
			// 查询初始化需要的数据
			String sql = " select qds,title,logo,img_first,img_second,img_third,ljjj,ljrz,cpsl,kf_phone,kf_ewm from T_QDS_INIT t where t.qds=? ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, qds);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			ArrayList<Map<String, String>> bannerlist = new ArrayList<Map<String, String>>();
			if (list != null && list.size() > 0) {
				Map map = list.get(0);
				jsonObject.putAll(map);
				String imgFirst = (String) map.get("IMG_FIRST");
				if (imgFirst != null && !"".equals(imgFirst)) {
					Map<String,String> bmp = new HashMap<String,String>();
					bmp.put("banner",imgFirst);
					bannerlist.add(bmp);
				}
				String imgSecond = (String) map.get("IMG_SECOND");
				if (imgSecond != null && !"".equals(imgSecond)) {
					Map<String,String> bmp = new HashMap<String,String>();
					bmp.put("banner",imgSecond);
					bannerlist.add(bmp);
				}
				String imgThird = (String) map.get("IMG_THIRD");
				if (imgThird != null && !"".equals(imgThird)) {
					Map<String,String> bmp = new HashMap<String,String>();
					bmp.put("banner",imgThird);
					bannerlist.add(bmp);
				}
				String imgFourth = (String) map.get("IMG_FOURTH");
				if (imgFourth != null && !"".equals(imgFourth)) {
					Map<String,String> bmp = new HashMap<String,String>();
					bmp.put("banner",imgFourth);
					bannerlist.add(bmp);
				}
				String imgFifth = (String) map.get("IMG_FIFTH");
				if (imgFifth != null && !"".equals(imgFifth)) {
					Map<String,String> bmp = new HashMap<String,String>();
					bmp.put("banner",imgFifth);
					bannerlist.add(bmp);
				}
			}
			jsonObject.put("result", "success");
			jsonObject.put("msg", "查询成功！");
			jsonObject.element("bannerlist", JSON.toJSONString(bannerlist, new SimpleValueFilter()));
		} catch (Exception e) {// 异常跳转
			e.printStackTrace();
			jsonObject.put("result", "fail");
			jsonObject.put("msg", "查询失败！");
		} finally {
			HibernateSessionFactory.closeSession();
			PrintWriter out = response.getWriter();
			out.write(jsonObject.toString());
		}
	}

	private void wdtdInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("pages/team_data.html");// 首页
	}

	private void khInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("pages/ysryx/ysryx_index.html");// 首页
	}

	/**
	 * 初始化
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void init(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String subscribe = "1";// 默认跳转
		String openid = (String) request.getSession().getAttribute("openid");
		String qds = request.getParameter("qds");
		request.getSession().setAttribute("qds", qds);
		if (qds == null || "".equals(qds)) {
			response.sendRedirect("pages/fail.html");// 链接失效
		}
		// String openid = "ogERit6LtIv1lKcE1slyPZBKSgFQ";
		try {
			// 1、通过wx_appid与wx_app_secret调用微信接口获取accessToken
			HttpClient HttpClient = HttpUtil.CreateHttpsClient();
			// 获取微信access_token
			String accessToken = WxUtil.getAccessToken(false, qds);
			// 2、通过accessToken与openid判断是否关注公众号
			// 根据接口返回的subscribe字段判断是否关注公众号 1=关注；0=未关注；
			String wxUserUrl = "https://api.weixin.qq.com/cgi-bin/user/info?lang=zh_CN&access_token=" + accessToken
					+ "&openid=" + openid;
			Map resultMapUser = HttpUtil.Get(HttpClient, wxUserUrl, "UTF-8", false, null, null);
			String resultUser = (String) resultMapUser.get("result");
			org.codehaus.jettison.json.JSONObject objuser = new org.codehaus.jettison.json.JSONObject(resultUser);
			try {
				// 异常取不到关注状态可能是accessToken不是最新的，重新取最新的获取
				subscribe = objuser.getString("subscribe");
			} catch (Exception e) {
				// 获取微信access_token
				accessToken = WxUtil.getAccessToken(true, qds);
				// 2、通过accessToken与openid判断是否关注公众号
				// 根据接口返回的subscribe字段判断是否关注公众号 1=关注；0=未关注；
				wxUserUrl = "https://api.weixin.qq.com/cgi-bin/user/info?lang=zh_CN&access_token=" + accessToken
						+ "&openid=" + openid;
				resultMapUser = HttpUtil.Get(HttpClient, wxUserUrl, "UTF-8", false, null, null);
				resultUser = (String) resultMapUser.get("result");
				objuser = new org.codehaus.jettison.json.JSONObject(resultUser);
				subscribe = objuser.getString("subscribe");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!"1".equals(subscribe)) {// 未关注跳转关注公从号页面
			String phoneType = WxUtil.check_userHeader(request);
			String url = ConfigUtils.getProperty(qds + "_url");
			if ("iphone".equals(phoneType)) {// 由于苹果手机跳转扫码关注页关注按钮会隐藏，所以跳转长按识别关注二维码页面
				url = "pages/concern.html?qds=" + qds;
			}
			response.sendRedirect(url);
			return;
		} else {
			response.sendRedirect("pages/index.html?qds=" + qds);// 跳转首页
		}
	}

}

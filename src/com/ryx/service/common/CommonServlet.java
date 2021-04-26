package com.ryx.service.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import com.alibaba.fastjson.JSON;
import com.ryx.dao.HibernateSessionFactory;
import com.ryx.util.common.SimpleValueFilter;

import net.sf.json.JSONObject;

public class CommonServlet extends HttpServlet implements Servlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		try {
			request.setCharacterEncoding("UTF-8");
			String action = request.getParameter("action");
			if ("queryDmb".equals(action)) {// 查询公共代码表
				queryDmb(request, response);
			}
//			if ("queryEwmText".equals(action)) {// 查询二维码文字内容
//				queryEwmText(request, response);
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询二维码文字内容
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void queryEwmText(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取加密数据
		JSONObject returnObject = new JSONObject();
		String ewm = request.getParameter("ewm");
		ewm="sjdfiw";
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = "select text from t_ryx_ewm t where t.ewm=? ";

			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, ewm);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				returnObject.put("result", "success");
				returnObject.put("msg", "查询成功!");
				returnObject.element("dmList",  JSON.toJSONString(list,new SimpleValueFilter()));
			} else {
				returnObject.put("result", "fail");
				returnObject.put("msg", "无记录!");
			}

		} catch (Exception e) {
			returnObject.put("result", "fail");
			returnObject.put("msg", "系统异常!");
		} finally {
			PrintWriter out = response.getWriter();
			out.write(returnObject.toString());
			HibernateSessionFactory.closeSession();
		}
	}

	/**
	 * 共用查询代码表
	 * 
	 * @param request
	 * @param response
	 * @param dataJson
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public void queryDmb(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取加密数据
		JSONObject returnObject = new JSONObject();

		String dmType = request.getParameter("dmType");
		if (dmType != null && !"".equals(dmType)) {
			dmType = URLDecoder.decode(dmType, "UTF-8");
		}
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = "select dm.dm,dm.dmmc,px from c##db_rongyixin.t_ryx_dmb dm where dm.dm_type=? and  dm.yxbz='Y' order by px";

			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, dmType);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				returnObject.put("result", "success");
				returnObject.put("msg", "查询成功!");
				returnObject.element("dmList",  JSON.toJSONString(list,new SimpleValueFilter()));
			} else {
				returnObject.put("result", "fail");
				returnObject.put("msg", "无记录!");
			}

		} catch (Exception e) {
			returnObject.put("result", "fail");
			returnObject.put("msg", "系统异常!");
		} finally {
			PrintWriter out = response.getWriter();
			out.write(returnObject.toString());
			HibernateSessionFactory.closeSession();
		}

	}

}

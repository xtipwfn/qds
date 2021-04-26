package com.ryx.service.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

import com.ryx.dao.HibernateSessionFactory;

import net.sf.json.JSONObject;

public class LoginServlet extends HttpServlet implements Servlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9033598644995248608L;

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
			if ("login".equals(action)) {// 登陆
				login(request, response);
			}

			if ("checkLogin".equals(action)) {// 通过OPENID检查登陆信息
				checkLogin(request, response);
			}

//			if ("queryUser".equals(action)) {// 查询用户信息
//				queryUser(request, response);
//			}
//
//			if ("saveUser".equals(action)) {// 完善用户信息
//				saveUser(request, response);
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 完善用户信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void saveUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		try {
			String userUuid = request.getParameter("user_uuid");
			String xm = request.getParameter("xm");
			if (xm != null && !"".equals(xm)) {
				xm = URLDecoder.decode(xm, "UTF-8");
			}
			String zw = request.getParameter("zw");
			if (zw != null && !"".equals(zw)) {
				zw = URLDecoder.decode(zw, "UTF-8");
			}
			String pwd = request.getParameter("pwd");
			String sfzjhm = request.getParameter("sfzjhm");
			String zfbzh = request.getParameter("zfbzh");
			String headimgurl = (String) request.getSession().getAttribute("headimgurl");
			Session session = HibernateSessionFactory.getSession();
			String insetrSql = " update t_ryx_user t set t.xm=?,t.pwd=?,t.sfzjhm=?,t.zfbzh=?,t.zw=?,t.xgsj=sysdate,t.head_url=? where t.user_uuid=? ";
			Connection conn = HibernateSessionFactory.connection();
			PreparedStatement ps = conn.prepareStatement(insetrSql);
			ps.setString(1, xm);
			ps.setString(2, pwd);
			ps.setString(3, sfzjhm);
			ps.setString(4, zfbzh);
			ps.setString(5, zw);
			ps.setString(6, headimgurl);
			ps.setString(7, userUuid);
			ps.execute();
			conn.commit();
			request.getSession().setAttribute("userUuid", userUuid);
			jsonObject.put("result", "success");
			jsonObject.put("msg", "修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result", "fail");
			jsonObject.put("msg", "修改失败！" + e.toString());
		} finally {
			HibernateSessionFactory.closeSession();
			PrintWriter out = response.getWriter();
			out.write(jsonObject.toString());
		}
	}

	/**
	 * 通过用户UUID查询用户信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void queryUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String userUuid = request.getParameter("user_uuid");
		JSONObject jsonObject = new JSONObject();
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = " select  xm,phone,pwd,sfzjhm,zfbzh,zw,head_url from t_ryx_user t where t.user_uuid=? ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, userUuid);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				jsonObject.put("result", "success");
				jsonObject.put("msg", "查询成功！");
				jsonObject.putAll(list.get(0));
			} else {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "无记录！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result", "fail");
			jsonObject.put("msg", "无记录！");
		} finally {
			HibernateSessionFactory.closeSession();
			PrintWriter out = response.getWriter();
			out.write(jsonObject.toString());
		}
	}

	/**
	 * 校验登陆
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String openid = (String) request.getSession().getAttribute("openid");
		String headimgurl = (String) request.getSession().getAttribute("headimgurl");
		JSONObject jsonObject = new JSONObject();
		String qds = request.getParameter("qds");
		if(qds == null || "".equals(qds)){
			qds =  (String) request.getSession().getAttribute("qds");
		}
		Session session = HibernateSessionFactory.getSession();
		String sql = " select user_uuid,xm,(select dmmc from c##db_rongyixin.t_ryx_dmb dm where dm.dm_type='用户类型' and dm.dm=t.user_type) as user_type,head_url from t_qds_user t where t.openid=? and t.qds= ? order by lrsj desc";
		try {
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, openid);
			query.setParameter(1, qds);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				Map<String , String> map = list.get(0);
				String headUrl = map.get("HEAD_URL");
				if(headUrl == null || "".equals(headUrl)){
					map.put("HEAD_URL", headimgurl);
				}
				jsonObject.put("result", "success");
				jsonObject.put("msg", "查询成功！");
				jsonObject.putAll(map);
			} else {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "无记录！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result", "fail");
			jsonObject.put("msg", "无记录！");
		} finally {
			HibernateSessionFactory.closeSession();
			PrintWriter out = response.getWriter();
			out.write(jsonObject.toString());
		}

	}

	/**
	 * 登陆
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		try {
			String phone = request.getParameter("phone");
			String code = request.getParameter("code");
			String verifyCode = (String) request.getSession().getAttribute("verifyCode");
			String verifyphone = (String) request.getSession().getAttribute("verifyPhone");
			String qds = request.getParameter("qds");
			if(qds == null || "".equals(qds)){
				qds =  (String) request.getSession().getAttribute("qds");
			}
			if (phone == null || "".equals(phone)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "手机号不能为空！");
				return;
			}
			if (!phone.equals(verifyphone)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "登陆手机号与发送短信手机号不一致！");
				return;
			}
			if (code == null || "".equals(code)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "验证码不能为空！");
				return;
			}
			if (!code.equals(verifyCode)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "验证码不一致！");
				return;
			}
			Session session = HibernateSessionFactory.getSession();
			String sql = " select user_uuid,xm,openid from t_qds_user t where t.phone=? and t.qds=? ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, phone);
			query.setParameter(1, qds);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				jsonObject.put("result", "success");
				jsonObject.put("msg", "登陆成功！");
				Map map = list.get(0);
				String userUuid = (String) map.get("USER_UUID");
				request.getSession().setAttribute("userUuid", userUuid);
				jsonObject.put("userUuid", userUuid);
				jsonObject.put("xm", (String) map.get("XM"));
				String openid = (String) request.getSession().getAttribute("openid");
				String useroOpenid = (String) map.get("OPENID");
				if (openid != null && !"".equals(openid) && (useroOpenid == null || "".equals(useroOpenid))) {
					String updateSql = "update t_qds_user t set t.openid=? where t.user_uuid=? ";
					Connection conn = HibernateSessionFactory.connection();
					PreparedStatement ps = conn.prepareStatement(updateSql);
					ps.setString(1, openid);
					ps.setString(2, userUuid);
					ps.execute();
					conn.commit();
				}
			} else {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "未注册！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result", "fail");
			jsonObject.put("msg", "保存出错！" + e.toString());
		} finally {
			HibernateSessionFactory.closeSession();
			PrintWriter out = response.getWriter();
			out.write(jsonObject.toString());
		}
	}

}

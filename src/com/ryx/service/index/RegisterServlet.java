package com.ryx.service.index;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import com.alibaba.fastjson.JSON;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.ryx.dao.HibernateSessionFactory;
import com.ryx.util.common.DatetimeUtil;
import com.ryx.util.common.SimpleValueFilter;
import com.ryx.util.common.WxUtil;
import com.ryx.util.msg.MsgUtil;

import net.sf.json.JSONObject;

public class RegisterServlet extends HttpServlet implements Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5880044550936609209L;

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
		try {
			request.setCharacterEncoding("UTF-8");
			String action = request.getParameter("action");
			if ("sendCode".equals(action)) {// 发送短信验证码
				sendCode(request, response);
			}
			if ("saveRegister".equals(action)) {// 保存注册信息
				saveRegister(request, response);
			}
			if ("ShowRegister".equals(action)) {// 分享注册
				ShowRegister(request, response);
			}

			if ("queryTdxx".equals(action)) {// 查询团队信息
				queryTdxx(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ShowRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sj_user_uuid = request.getParameter("sj_user_uuid");
		String headimgurl = (String) request.getSession().getAttribute("headimgurl");
		response.sendRedirect("pages/register.html?sj_user_uuid=" + sj_user_uuid+"&headimgurl="+headimgurl);// 注册页
	}

	/**
	 * 查询团队信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void queryTdxx(HttpServletRequest request, HttpServletResponse response) throws IOException {

		JSONObject jsonObject = new JSONObject();
		String openid = (String) request.getSession().getAttribute("openid");
		String userUuid = request.getParameter("userUuid");
		// System.out.println("团队查询userUuid:"+userUuid);
		if (userUuid == null || "".equals(userUuid)) {
			userUuid = (String) request.getSession().getAttribute("user_uuid");
		}
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = "select * from ( select user_uuid,xm,phone,user_type,sfzjhm,zw,to_char(lrsj,'yyyy-mm-dd') as lrsj from t_qds_user t where t.sj_user_uuid=? "
					+ " union "
					+ " select user_uuid,xm,phone,user_type,sfzjhm,zw,to_char(lrsj,'yyyy-mm-dd') as lrsj from t_qds_user t where t.sj_user_uuid in (select user_uuid from t_qds_user u where u.openid=?) ) order by lrsj desc ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, userUuid);
			query.setParameter(1, openid);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				jsonObject.element("list", JSON.toJSONString(list, new SimpleValueFilter()));
				jsonObject.put("result", "success");
				jsonObject.put("msg", "查询成功！");
			} else {
				jsonObject.element("list", "");
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "无记录！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result", "fail");
			jsonObject.put("msg", "查询异常！" + e.toString());
		} finally {
			HibernateSessionFactory.closeSession();
			PrintWriter out = response.getWriter();
			out.write(jsonObject.toString());
		}

	}

	/**
	 * 保存用户信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void saveRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
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
				jsonObject.put("msg", "注册手机号与发送短信手机号不一致！");
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
			String xm = request.getParameter("xm");
			if (xm != null && !"".equals(xm)) {
				xm = URLDecoder.decode(xm, "UTF-8");
			}
			String zw = request.getParameter("zw");
			if (zw != null && !"".equals(zw)) {
				zw = URLDecoder.decode(zw, "UTF-8");
			}
			String sj_user_uuid = request.getParameter("sj_user_uuid");
			String openid = (String) request.getSession().getAttribute("openid");
			String headimgurl = (String) request.getSession().getAttribute("headimgurl");
			Session session = HibernateSessionFactory.getSession();
			String sql = " select user_uuid from t_qds_user t where t.phone=? and t.qds=? ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, phone);
			query.setParameter(1, qds);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				jsonObject.put("result", "success");
				jsonObject.put("msg", "已存在此用户,自动登陆成功");
				Map map = list.get(0);
				request.getSession().setAttribute("user_uuid", (String) map.get("USER_UUID"));
				jsonObject.put("userUuid", (String) map.get("USER_UUID"));
			} else {// 不存在则新增
				String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
				String insetrSql = "insert into t_qds_user (user_uuid,xm,phone,user_type,sj_user_uuid,zw,openid,head_url,qds)VALUES(?,?,?,'01',?,?,?,?,?) ";
				Connection conn = HibernateSessionFactory.connection();
				PreparedStatement ps = conn.prepareStatement(insetrSql);
				ps.setString(1, uuid);
				ps.setString(2, xm);
				ps.setString(3, phone);
				ps.setString(4, sj_user_uuid);
				ps.setString(5, zw);
				ps.setString(6, openid);
				ps.setString(7, headimgurl);
				ps.setString(8, qds);

				ps.execute();
				conn.commit();
				request.getSession().setAttribute("userUuid", uuid);
				jsonObject.put("userUuid", uuid);
				jsonObject.put("result", "success");
				jsonObject.put("msg", "注册成功！");
				// 推荐人不为空且有微信OPENID则推荐微信注册成功消息
				if (sj_user_uuid != null && !"".equals(sj_user_uuid)) {
					String openidSql = " select user_uuid,openid from t_qds_user t where t.user_uuid=? and t.qds=? and t.openid is not null";
					SQLQuery openidQuery = session.createSQLQuery(openidSql);
					openidQuery.setParameter(0, sj_user_uuid);
					openidQuery.setParameter(1, qds);
					openidQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
					List<Map<String, String>> openidlist = openidQuery.list();
					if (openidlist != null && openidlist.size()>0) {
						Map<String, String> map = openidlist.get(0);
						String sjopenid = map.get("OPENID");
						Map<String, Object> data = new HashMap<>();
						Map<String, Object> firstMap = new HashMap<String, Object>();
						firstMap.put("value", "您推荐的客户注册成功");
						firstMap.put("color", "#173177");
						data.put("first", firstMap);

						Map<String, Object> keyword1Map = new HashMap<String, Object>();
						keyword1Map.put("value", xm);
						keyword1Map.put("color", "#173177");
						data.put("keyword1", keyword1Map);

						Map<String, Object> keyword2Map = new HashMap<String, Object>();
						keyword2Map.put("value", phone);
						keyword2Map.put("color", "#173177");
						data.put("keyword2", keyword2Map);

						Map<String, Object> keyword3Map = new HashMap<String, Object>();
						keyword3Map.put("value", DatetimeUtil.getLongDatetimeString());
						keyword3Map.put("color", "#173177");
						data.put("keyword3", keyword3Map);

						Map<String, Object> remarkMap = new HashMap<String, Object>();
						remarkMap.put("value", "感谢您的使用");
						remarkMap.put("color", "#173177");
						data.put("remark", remarkMap);

						Map<String, Object> param = new HashMap<>();
						param.put("touser", sjopenid);
						param.put("template_id", "IaejrA1iZD-DD75cOZS5Xp36yg3m53GuKPOGn3KQpPU");
						param.put("url", "https://www.rongyixin.net/qds/index?action=wdtdInit");
						param.put("data", data);

//						Map<String, String> reMap = WxUtil.PushWx(JSON.toJSONString(param),qds);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HibernateSessionFactory.closeSession();
			PrintWriter out = response.getWriter();
			out.write(jsonObject.toString());
		}
	}

	/**
	 * 发送手机短信验证码
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws org.json.JSONException
	 * @throws HTTPException
	 */
	private void sendCode(HttpServletRequest request, HttpServletResponse response)
			throws IOException, HTTPException, org.json.JSONException {
		JSONObject jsonObject = new JSONObject();
		String phone = request.getParameter("phone");
		HttpSession session = request.getSession();

		// 生成六位数字的随机字符串
		int randNum = new Random().nextInt(899999) + 100000;
		String verifyCode = Integer.toString(randNum);
		// 发送短信通知
		SmsSingleSenderResult map = MsgUtil.sendMsg(phone, "【融亿鑫企服】您的验证码为：" + verifyCode + "，请于5分钟内填写。如非本人操作，请忽略本短信。");
		session.setMaxInactiveInterval(30 * 60);
		session.setAttribute("verifyCode", verifyCode);
		session.setAttribute("verifyPhone", phone);

		int result = (int) map.result;
		String msg = (String) map.errMsg;
		msg = URLDecoder.decode(msg, "UTF-8");
		if (result != 0) {
			jsonObject.put("result", "fail");
			jsonObject.put("msg", msg);
			jsonObject.put("re", result);
		} else {
			jsonObject.put("result", "success");
			jsonObject.put("msg", msg);
		}
		PrintWriter out = response.getWriter();
		out.write(jsonObject.toString());

	}

	public static void main(String[] args) {
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> firstMap = new HashMap<String, Object>();
		firstMap.put("value", "您推荐的代理注册成功");
		firstMap.put("color", "#173177");
		data.put("first", firstMap);

		Map<String, Object> keyword1Map = new HashMap<String, Object>();
		keyword1Map.put("value", "1232");
		keyword1Map.put("color", "#173177");
		data.put("keyword1", keyword1Map);

		Map<String, Object> keyword2Map = new HashMap<String, Object>();
		keyword2Map.put("value", "13119548507");
		keyword2Map.put("color", "#173177");
		data.put("keyword2", keyword2Map);

		Map<String, Object> keyword3Map = new HashMap<String, Object>();
		keyword3Map.put("value", DatetimeUtil.getLongDatetimeString());
		keyword3Map.put("color", "#173177");
		data.put("keyword3", keyword3Map);

		Map<String, Object> remarkMap = new HashMap<String, Object>();
		remarkMap.put("value", "感谢您的使用");
		remarkMap.put("color", "#173177");
		data.put("remark", remarkMap);

		Map<String, Object> param = new HashMap<>();
		param.put("touser", "ogERit6LtIv1lKcE1slyPZBKSgFQ");
		param.put("template_id", "IaejrA1iZD-DD75cOZS5Xp36yg3m53GuKPOGn3KQpPU");
		param.put("url", "https://www.rongyixin.net/ryx/IndexServlet?action=wdtdInit");
		param.put("data", data);

//		Map<String, String> reMap = WxUtil.PushWx(JSON.toJSONString(param),qds);
	}
}

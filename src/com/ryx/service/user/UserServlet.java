package com.ryx.service.user;

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

import com.alibaba.fastjson.JSON;
import com.ryx.dao.HibernateSessionFactory;
import com.ryx.util.common.SimpleValueFilter;
import com.ryx.util.common.StringUtil;

import net.sf.json.JSONObject;

/**
 * 用户相关类
 *
 */
public class UserServlet extends HttpServlet implements Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -319467129686676855L;

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
			if ("init".equals(action)) {// 合伙人初始化
				init(request, response);
			}

			if ("queryZsy".equals(action)) {// 查询总收益
				queryZsy(request, response);
			}

			if ("querySymx".equals(action)) {// 查询收益明细
				querySymx(request, response);
			}
			
			if ("querySrxx".equals(action)) {// 查询个人收入信息
				querySrxx(request, response);
			}
			
			if ("queryTxmx".equals(action)) {// 查询提现记录
				queryTxmx(request, response);
			}
			if ("saveTxsq".equals(action)) {// 保存提现申请
				saveTxsq(request, response);
			}
			
			if ("queryQuestion".equals(action)) {// 查询问题库
				queryQuestion(request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询问题库
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void queryQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = "  select question,answer,to_char(lrsj,'yyyy-mm-dd hh24:mi:ss') lrsj ,xh from c##db_rongyixin.T_RYX_QUESTION t where t.yxbz='Y' order by xh,uuid ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				jsonObject.put("list", JSON.toJSONString(list, new SimpleValueFilter()));
				jsonObject.put("result", "success");
				jsonObject.put("msg", "查询成功！");
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
	 * 保存提现申请
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void saveTxsq(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String openid = (String) request.getSession().getAttribute("openid");
//		String openid = "123";
		JSONObject jsonObject = new JSONObject();
		try {
			String je = request.getParameter("je");
			if (je == null || "".equals(je)) {
				jsonObject.put("result", "fail"); 
				jsonObject.put("msg", "金额不能为空！");
				return;
			} 
			if(!StringUtil.isNumeric(je) && Double.valueOf(je)<=0){
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "金额输入有误！");
				return;
			}
			String bz = request.getParameter("bz");
			if (bz != null && !"".equals(bz)) {
				bz = URLDecoder.decode(bz, "utf-8");
				if(bz.length()>300){//数据库备注字段长度只有300，超过部分不保存
					bz = bz.substring(0,300);
				}
			} 
			String yhzh  = request.getParameter("yhzh");
			String zhxm = request.getParameter("zhxm");
			if (zhxm != null && !"".equals(zhxm)) {
				zhxm = URLDecoder.decode(zhxm, "utf-8");
			} 
			String khhmc = request.getParameter("khhmc");
			if (khhmc != null && !"".equals(khhmc)) {
				khhmc = URLDecoder.decode(khhmc, "utf-8");
			} 
			Session session = HibernateSessionFactory.getSession();
			String sql = " select KTX from table(pkg_user.f_get_SRXX(?)) ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, openid);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if(list!=null && list.size()>0){
				Map map = list.get(0);
				String ktx = (String) map.get("KTX");
				if(Double.valueOf(je) > Double.valueOf(ktx)){
					jsonObject.put("result", "fail");
					jsonObject.put("msg", "超出可提现金额！");
					return;
				}
			}
			
			String insetrSql = "insert into t_qds_txsq (tx_uuid,user_uuid,tx_je,tx_zt,yxbz,bz,lrsj,lrry,xgsj,xgry,yhzh,zhxm,khhmc)  "
					+ " select sys_guid(),u.user_uuid,?,'01','Y',?,sysdate,user_uuid,sysdate ,user_uuid,?,?,?  "
					+ " from t_qds_user u where u.openid=? and u.yxbz='Y' and rownum=1 ";
			Connection conn = HibernateSessionFactory.connection();
			PreparedStatement ps = conn.prepareStatement(insetrSql);
			ps.setString(1, je);
			ps.setString(2, bz);
			ps.setString(3, yhzh);
			ps.setString(4, zhxm);
			ps.setString(5, khhmc);
			ps.setString(6, openid);
			ps.execute();
			conn.commit();
			jsonObject.put("result", "success");
			jsonObject.put("msg", "提交申请成功！");
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
	 * 查询提现明细
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void queryTxmx(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String openid = (String) request.getSession().getAttribute("openid");
//		String openid = "123";
		JSONObject jsonObject = new JSONObject();
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = "select tx_uuid,user_uuid,tx_je,F_GET_DMTOMC('提现申请状态',tx_zt) ZT,F_GET_DMTOMC('有效标志',yxbz) yxbz,"
					+ " bz,to_char(lrsj,'yyyy-mm-dd')lrsj from t_qds_txsq t "
					+ " where t.user_uuid in (select user_uuid from t_qds_user u where u.openid=?) and t.yxbz='Y'";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, openid);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				jsonObject.put("txmx", JSON.toJSONString(list, new SimpleValueFilter()));
				jsonObject.put("result", "success");
				jsonObject.put("msg", "查询成功！");
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
	 * 查询收入信息
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void querySrxx(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String openid = (String) request.getSession().getAttribute("openid");
//		String openid = "123";
		JSONObject jsonObject = new JSONObject();
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = " select ZSR,YJSR,PTJL,BYSR,YTX,KTX from table(pkg_user.f_get_SRXX(?)) ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, openid);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				Map map = (Map) list.get(0);
				jsonObject.put("result", "success");
				jsonObject.putAll(map);
				jsonObject.put("msg", "查询成功！");
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
	 * 查询合伙人收益明细
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void querySymx(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String openid = (String) request.getSession().getAttribute("openid");
//		String openid = "ogERitxuk4ZdrarMsawe1dadewNf8";
		String phone = request.getParameter("phone");
		String yjbz = request.getParameter("yjbz");
		JSONObject jsonObject = new JSONObject();
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = "  select user_uuid,xm,phone,head_url from t_qds_user t where t.user_uuid = (select sj_user_uuid from t_qds_user u where u.openid=? ) ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, openid);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				Map map = (Map) list.get(0);
				jsonObject.putAll(map);
			} else {
				jsonObject.put("XM", "");
				jsonObject.put("PHONE", "");
				jsonObject.put("HEAD_URL", "");
			}
			
			String sqltd = " select (select count(user_uuid) sl "+
					" from t_qds_user u start with u.user_uuid = t.user_uuid  "+
					"  connect by prior u.user_uuid = u.sj_user_uuid) as hhrtd, "+
					" NVL((select sum(syje)   from t_qds_user_zhmx zh  "+
					" where ZH.LRSJ>=trunc(add_months(last_day(sysdate), -1) + 1)  "+
					" and zh.lrsj<last_day(sysdate)+1   and   zh.user_uuid in (select user_uuid  "+
					" from t_qds_user u   start with u.user_uuid = t.user_uuid  "+
					" connect by prior u.user_uuid = u.sj_user_uuid)), 0) bytdzyj, "+
					" NVL((select sum(syje)   from t_qds_user_zhmx zh  "+
					" where ZH.LRSJ>=trunc(add_months(last_day(sysdate), -2) + 1)  "+
					" and zh.lrsj<trunc(add_months(last_day(sysdate), -1) + 1)    and   zh.user_uuid in (select user_uuid  "+
					" from t_qds_user u   start with u.user_uuid = t.user_uuid  "+
					" connect by prior u.user_uuid = u.sj_user_uuid)), 0) sytdzyj, "+
					" (select count(user_uuid) from t_qds_user r where r.sj_user_uuid=t.user_uuid ) as yjhhr  "+      
					" from t_qds_user t  where t.openid = ?  ";
			
			SQLQuery queryTd = session.createSQLQuery(sqltd);
			queryTd.setParameter(0, openid);
			queryTd.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> listtd = queryTd.list();
			if (listtd != null && listtd.size() > 0) {
				Map map = (Map) listtd.get(0);
				jsonObject.putAll(map);
			} else {
				jsonObject.put("HHRTD", "0");
				jsonObject.put("SYTDZYJ", "0");
				jsonObject.put("YJHHR", "0");
				jsonObject.put("BYTDZYJ", "");
			}
			
			String sqltdmx = " select * from (   select head_url,xm,phone,to_char(lrsj,'yyyy-mm-dd hh24:mi:ss') lrsj,NVL((select sum(syje) from t_qds_user_zhmx zh where zh.user_uuid=t.user_uuid ),0) AS GRYJ,"
					+ " NVL((select sum(syje) from t_qds_user_zhmx zh where zh.user_uuid in (select user_uuid from t_qds_user u start with u.user_uuid=t.user_uuid connect by prior u.user_uuid=u.sj_user_uuid) ),0) AS tdYJ,"
					+ "(select count(1)from t_qds_user r start with r.user_uuid=t.user_uuid connect by prior r.user_uuid=r.sj_user_uuid) tdrs "
					+ " from t_qds_user t where t.sj_user_uuid =(select user_uuid from  t_qds_user u where u.openid=?) )  where 1=1 ";
			if(phone!=null && !"".equals(phone)){
				sqltdmx += " and phone = ? ";
			}
			if(yjbz !=null && "Y".equals(yjbz)){
				sqltdmx += " and tdyj > 0 ";
			}
			sqltdmx +=  " order by gryj desc ,tdyj desc,lrsj desc ";
			SQLQuery queryTdmx = session.createSQLQuery(sqltdmx);
			queryTdmx.setParameter(0, openid);
			if(phone!=null && !"".equals(phone)){
				queryTdmx.setParameter(1, phone);
			}
			queryTdmx.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> listtdmx = queryTdmx.list();
			if (listtdmx != null && listtdmx.size() > 0) {
				jsonObject.put("tdmx", JSON.toJSONString(listtdmx, new SimpleValueFilter()));
			} else {
				jsonObject.put("tdmx", listtdmx);
			}
			jsonObject.put("result", "success");
			jsonObject.put("msg", "查询成功！");
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
	 * 查询合伙人总收益
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void queryZsy(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String openid = (String) request.getSession().getAttribute("openid");
//		String openid = "ogERitxuk4ZdrarMsawe1dadewNf8";
		JSONObject jsonObject = new JSONObject();
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = " select user_uuid, xm,phone, f_get_dmtomc('用户类型', t.user_type) as yhlx, t.head_url,"
					+ "  (select count(user_uuid) sl  from t_qds_user u "
					+ " where u.sj_user_uuid = t.user_uuid) as yqyh,   (select count(user_uuid) sl "
					+ " from t_qds_user u   start with u.user_uuid = t.user_uuid "
					+ " connect by prior u.user_uuid = u.sj_user_uuid) as hhrtd,   NVL((select sum(syje) "
					+ " from t_qds_user_zhmx zh   where zh.user_uuid = t.user_uuid),    0) ljsy, "
					+ "  NVL((select sum(tx_je)   from t_qds_txsq tx    where tx.user_uuid = t.user_uuid), "
					+ " 0) txje,   NVL((select sum(syje)   from t_qds_user_zhmx zh "
					+ " where zh.user_uuid = t.user_uuid   and zh.lrsj >= Trunc(SYSDATE)),    0) jrsy, "
					+ " NVL((select sum(syje)   from t_qds_user_zhmx zh "
					+ " where ZH.LRSJ>=trunc(add_months(last_day(sysdate), -1) + 1) "
					+ " and zh.lrsj<last_day(sysdate)+1   and   zh.user_uuid in   (select user_uuid "
					+ " from t_qds_user u   start with u.user_uuid = t.user_uuid "
					+ " connect by prior u.user_uuid = u.sj_user_uuid)),   0) tdzyj, to_char(sysdate, 'MM' ) yf "
					+ " from t_qds_user T where t.openid=?";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, openid);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				Map map = (Map) list.get(0);
				jsonObject.put("result", "success");
				String headUrl = (String) map.get("HEAD_URL");
				if (headUrl == null || "".equals(headUrl)) {// 头像为空则获取微信头像
					headUrl = (String) request.getSession().getAttribute("headimgurl");
					if(headUrl != null && !"".equals(headUrl)){
						map.put("HEAD_URL", headUrl);
					} else {
						map.put("HEAD_URL", "");
					}
				}
				jsonObject.putAll(map);
				jsonObject.put("msg", "查询成功！");
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
	 * 初始化
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void init(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String openid = (String) request.getSession().getAttribute("openid");
		String qds = request.getParameter("qds");
		if(qds == null || "".equals(qds)){
			qds =  (String) request.getSession().getAttribute("qds");
		}
		if (openid != null && !"".equals(openid)) {
			try {
				Session session = HibernateSessionFactory.getSession();
				String sql = "select USER_UUID from t_qds_user T WHERE T.OPENID=? ";
				SQLQuery query = session.createSQLQuery(sql);
				query.setParameter(0, openid);
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				List<Map<String, String>> list = query.list();
				if (list != null && list.size() > 0) {
					Map map = (Map) list.get(0);
					response.sendRedirect("pages/partner.html?qds="+qds);
				} else {
					response.sendRedirect("pages/register.html?qds="+qds);// 注册页
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				HibernateSessionFactory.closeSession();
			}
		} else {// 跳转注册
			response.sendRedirect("pages/register.html?qds="+qds);// 注册页
		}

	}
}

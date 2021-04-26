package com.ryx.service.prod;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import com.alibaba.fastjson.JSON;
import com.ryx.dao.HibernateSessionFactory;
import com.ryx.util.common.ConfigUtils;
import com.ryx.util.common.DatetimeUtil;
import com.ryx.util.common.SimpleValueFilter;
import com.ryx.util.common.WxUtil;
import com.ryx.util.qrcode.QRCodeUtil;

import net.sf.json.JSONObject;

public class ProdServlet extends HttpServlet implements Servlet {
	private static final long serialVersionUID = 1868349309976939999L;

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
			
			if ("querySf".equals(action)) {// 查询省份
				querySf(request, response);
			}
			if ("queryCs".equals(action)) {// 查询城市
				queryCs(request, response);
			}
			if ("queryCsName".equals(action)) {// 查询城市名称
				queryCsName(request, response);
			}
			if ("queryProd".equals(action)) {// 查询产品
				queryProd(request, response);
			}
			
			if ("prodDetilsInit".equals(action)) {// 产品明细初始化
				prodDetilsInit(request, response);
			}

			if ("queryProdDetils".equals(action)) {// 查询产品明细
				queryProdDetils(request, response);
			}

//			if ("prodInit".equals(action)) {// 渠道产品初始化跳转
//				prodInit(request, response);
//			}

//			if ("prodSqSave".equals(action)) {// 渠道产品申请保存
//				prodSqSave(request, response);
//			}

			if ("saveSq".equals(action)) {// 保存申请
				saveSq(request, response);
			}

			if ("saveBbxx".equals(action)) {// 保存报备信息
				saveBbxx(request, response);
			}
//			
//			if ("checkSbm".equals(action)) {// 校验识别码是否有效
//				checkSbm(request, response);
//			}

			if ("querySq".equals(action)) {// 查询申请
				querySq(request, response);
			}

			if ("getZbmmImg".equals(action)) {// 获取招兵买马海报图片
				getZbmmImg(request, response);
			}

			if ("getTghbImg".equals(action)) {// 获取产品推广海报图片
				getTghbImg(request, response);
			}
			
//			if ("tmpInit".equals(action)) {// 产品临时链接
//				tmpInit(request, response);
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 产品临时链接
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void tmpInit(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String sbm = request.getParameter("sbm");// 识别码
	
		if (sbm != null && !"".equals(sbm) ) {
			try {
				Session session = HibernateSessionFactory.getSession();
				// 查询有效的识别码
				String sql = " select t.prod_uuid,url from t_ryx_prod_code t where   t.sbm=? and t.yxbz='Y' and t.lrsj>=sysdate-1  ";
				SQLQuery query = session.createSQLQuery(sql);
				query.setParameter(0, sbm);
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				List<Map<String, String>> list = query.list();
				if (list != null && list.size() > 0) {// 存在则跳转产品详情
					Map map = (Map) list.get(0);
					String url = (String) map.get("URL");
					if(url != null && !"".equals(url)){
						response.sendRedirect(url);
					} else {
						response.sendRedirect("pages/fail.html");// 链接失效页
					}
				} else {// 不存在则跳转链接失效页
					response.sendRedirect("pages/fail.html");// 链接失效页
				}
			} catch (Exception e) {// 异常跳转
				e.printStackTrace();
				response.sendRedirect("pages/fail.html");// 链接失效页
			} finally {
				HibernateSessionFactory.closeSession();
			}
		} else {
			response.sendRedirect("pages/fail.html");// 链接失效页
		}
		
	}

	/**
	 * 校验识别码是否有效
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	
	private void checkSbm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		try {
			String prodUuid = request.getParameter("prodUuid");// 产品ID
			String sbm = request.getParameter("sbm");// 识别码
			Session session = HibernateSessionFactory.getSession();
			// 查询有效的识别码
			String sql = "select t.prod_uuid,prod_name from t_ryx_prod_code t,t_ryx_prod p where t.prod_uuid=p.prod_uuid and  t.prod_uuid=? and t.sbm=? and t.yxbz='Y' ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, prodUuid);
			query.setParameter(1, sbm);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				jsonObject.put("result", "success");
				jsonObject.put("msg", "校验成功！");
			} else {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "识别码无效！");
			}
		} catch (Exception e) {// 异常跳转
			e.printStackTrace();
			jsonObject.put("result", "fail");
			jsonObject.put("msg", "识别码无效！");
		} finally {
			HibernateSessionFactory.closeSession();
			PrintWriter out = response.getWriter();
			out.write(jsonObject.toString());
		}

	}

	/**
	 * 保存报备信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void saveBbxx(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		try {
			String xm = request.getParameter("xm");
			if (xm == null || "".equals(xm)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "姓名不能为空！");
				return;
			} else {
				xm = URLDecoder.decode(xm, "utf-8");
			}
			String sfzjhm = request.getParameter("sfzjhm");
			if (sfzjhm == null || "".equals(sfzjhm)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "身份证件号码不能为空！");
				return;
			}

			String phone = request.getParameter("phone");
			if (phone == null || "".equals(phone)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "手机号不能为空！");
				return;
			}

			String dz = request.getParameter("dz");
			if (dz == null || "".equals(dz)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "按揭房产地址不能为空！");
				return;
			} else {
				dz = URLDecoder.decode(dz, "utf-8");
			}

			String prodUuid = request.getParameter("prodUuid");
			String qds = request.getParameter("qds");
			if(qds == null || "".equals(qds)){
				qds =  (String) request.getSession().getAttribute("qds");
			}
			Session session = HibernateSessionFactory.getSession();
			String insetrSql = "insert into t_qds_bbxx ( uuid ,xm ,sfzjhm ,phone ,ajfcdz ,prod_uuid ,lrsj) values (sys_guid(),?,?,?,?,?,sysdate,?)  ";
			Connection conn = HibernateSessionFactory.connection();
			PreparedStatement ps = conn.prepareStatement(insetrSql);
			ps.setString(1, xm);
			ps.setString(2, sfzjhm);
			ps.setString(3, phone);
			ps.setString(4, dz);
			ps.setString(5, prodUuid);
			ps.setString(6, qds);
			ps.execute();

			conn.commit();
			String url = "";
			if (prodUuid != null && !"".equals(prodUuid)) {
				String sql = " select bank_url,kf_bz,kf_url from t_qds_prod t where t.prod_uuid=? and t.qds=?";
				SQLQuery query = session.createSQLQuery(sql);
				query.setParameter(0, prodUuid);
				query.setParameter(1, qds);
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				List<Map<String, String>> list = query.list();
				if (list != null && list.size() > 0) {
					Map map = list.get(0);
					url = (String) map.get("KF_URL");
				}
			}
			jsonObject.put("result", "success");
			jsonObject.put("url", url);
			jsonObject.put("msg", "保存成功！");
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
	 * 单独分享链接
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void prodDetilsInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String prodUuid = request.getParameter("prodUuid");
		String userUuid = request.getParameter("userUuid");
		String qds = request.getParameter("qds");
		String url;
		if(qds != null && !"".equals(qds)){
			request.getSession().setAttribute("qds", qds);
			url = "pages/prod_details.html?prodUuid=" + prodUuid + "&qdsid="
					+ userUuid+"&qds="+qds;
			
		} else {
			url = "pages/fail.html";
		}
		response.sendRedirect(url);
	}

	/**
	 * 渠道产品初始化跳转入口
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void prodInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String prodUuid = request.getParameter("prodUuid");// 产品ID
		String khbz = request.getParameter("khbz");// 客服标志,当传参为N时发票贷产品不跳转增加客服，直接跳转银行
		if (khbz != null && "N".equals(khbz)) {
			request.getSession().setAttribute("khbz", "N");
		}
		if (prodUuid == null || "".equals(prodUuid)) {
			prodUuid = "wsd";// 默认微业贷
		}
		String prodName = "";
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = "select prod_uuid,prod_name,prod_fl from t_ryx_prod t where t.prod_uuid=? and t.prod_zt='Y'";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, prodUuid);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				Map map = (Map) list.get(0);
				prodName = (String) map.get("PROD_NAME");
				String prodFl = (String) map.get("PROD_FL");
				response.sendRedirect("pages/prod_apply.html?prod_uuid=" + prodUuid + "&prodFl="+prodFl+ "&prod_name="
						+ URLEncoder.encode(prodName, "utf-8"));
			} else {
				response.sendRedirect("pages/fail.html");// 链接失效页
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HibernateSessionFactory.closeSession();
		}
	}

	/**
	 * 渠道产品申请保存
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void prodSqSave(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String openid = (String) request.getSession().getAttribute("openid");
		System.out.println("渠道申请产品成功");
		JSONObject jsonObject = new JSONObject();
		Session session = HibernateSessionFactory.getSession();
		Connection conn = HibernateSessionFactory.connection();
		try {
			String gsmc = request.getParameter("gsmc");
			if (gsmc != null && !"".equals(gsmc)) {
//				jsonObject.put("result", "fail");
//				jsonObject.put("msg", "公司名称不能为空！");
//				return;
//			} else {
				gsmc = URLDecoder.decode(gsmc, "utf-8");
			}
			String frXm = request.getParameter("frXm");
			if (frXm != null && !"".equals(frXm)) {
				frXm = URLDecoder.decode(frXm, "utf-8");
			}
			String frPhone = request.getParameter("frPhone");
			if (frPhone == null || "".equals(frPhone)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "法人手机号不能为空！");
				return;
			}

			String prodUuid = request.getParameter("prodUuid");
			if (prodUuid == null || "".equals(prodUuid)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "产品ID不能为空！");
				return;
			}
			String sbm = request.getParameter("sbm");
			String qdsUuid = request.getParameter("qdsUuid");
			String xzqhDm = request.getParameter("xzqhDm");
			
			String insetrSql = " insert into t_qds_dksqb (SQ_UUID,GSMC,FR_XM,FR_PHONE,PROD_UUID,QDS_UUID,SQZT,SQRQ,LRRY,XGSJ,XGRY,OPENID,bz) "
					+ "  values (sys_guid(),?,?,?,?,?,'0',sysdate,?,sysdate,?,?,'渠道引流') ";
			
			PreparedStatement ps = conn.prepareStatement(insetrSql);
			ps.setString(1, gsmc);
			ps.setString(2, frXm);
			ps.setString(3, frPhone);
			ps.setString(4, prodUuid);
			ps.setString(5, qdsUuid);
			ps.setString(6, qdsUuid);
			ps.setString(7, qdsUuid);
			ps.setString(8, openid);
			ps.execute();

			
			if ( sbm != null && !"".equals(sbm)){//识别码不为空则取消此识别码
				String updateSql = "  update t_ryx_prod_code t set t.yxbz='N',t.xgsj=sysdate,t.openid=? where t.prod_uuid=? and t.sbm=? and t.yxbz='Y' ";
				PreparedStatement psu = conn.prepareStatement(updateSql);
				psu.setString(1, openid);
				psu.setString(2, prodUuid);
				psu.setString(3, sbm);
				psu.execute();
			}
			conn.commit();
			String url = "";
			String prodName = "";
			if (prodUuid != null && !"".equals(prodUuid)) {
				String sql = "select nvl((select url from t_ryx_prod_xzqhurl where prod_uuid=? and xzqhsz_dm=?),bank_url)as bank_url,kf_bz,kf_url,prod_name"
						+ "  from t_ryx_prod t where t.prod_uuid=? ";
				SQLQuery query = session.createSQLQuery(sql);
				query.setParameter(0, prodUuid);
				query.setParameter(1, xzqhDm);
				query.setParameter(2, prodUuid);
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				List<Map<String, String>> list = query.list();
				if (list != null && list.size() > 0) {
					Map map = list.get(0);
					String kfBz = (String) map.get("KF_BZ");
					prodName = (String) map.get("PROD_NAME");
					String kfbz = (String) request.getSession().getAttribute("khbz");
					if (kfBz != null && "Y".equals(kfBz)) {// 数据库配置客服标志判断是否跳转添加客服微信URL
						// 客服标志为Y且session中有存不跳转客服标志则跳转银行
						if (kfbz != null && "N".equals(kfbz)) {
							url = (String) map.get("BANK_URL");
						} else {// 没有存不跳转标志则跳转客服
							url = (String) map.get("KF_URL");
						}
					} else {// 客服标志不为Y则跳银行
						url = (String) map.get("BANK_URL");
					}
				}
			}

			// 推送微信消息
//			pushDksq(gsmc, frXm, frPhone, qdsUuid, openid, prodName);
			jsonObject.put("result", "success");
			jsonObject.put("url", url);
			jsonObject.put("msg", "保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result", "fail");
			jsonObject.put("msg", "保存异常！" + e.toString());
		} finally {
			HibernateSessionFactory.closeSession();
			PrintWriter out = response.getWriter();
			out.write(jsonObject.toString());
		}
	}

	/**
	 * 推送贷款申请消息
	 * 
	 * @param gsmc
	 *            公司名称
	 * @param frXm
	 *            法人姓名
	 * @param frPhone
	 *            法人手机号
	 * @param qdsUuid
	 *            渠道商ID
	 * @param openid
	 *            申请人OPENID
	 * @param prodName
	 *            产品名称
	 */
	private static void pushDksq(String gsmc, String frXm, String frPhone, String qdsUuid, String openid, String prodName,String qds) {
		try {
			System.out.println("openid="+openid);
			if (openid != null && !"".equals(openid)) {// 推送贷款成功申请
				Map<String, Object> data = new HashMap<>();
				Map<String, Object> firstMap = new HashMap<String, Object>();
				firstMap.put("value", "您的申请已提交成功");
				firstMap.put("color", "#173177");
				data.put("first", firstMap);

				Map<String, Object> keyword1Map = new HashMap<String, Object>();
				keyword1Map.put("value", frXm);
				keyword1Map.put("color", "#173177");
				data.put("keyword1", keyword1Map);

				Map<String, Object> keyword2Map = new HashMap<String, Object>();
				keyword2Map.put("value", prodName);
				keyword2Map.put("color", "#173177");
				data.put("keyword2", keyword2Map);

				Map<String, Object> keyword3Map = new HashMap<String, Object>();
				keyword3Map.put("value", DatetimeUtil.getLongDatetimeString());
				keyword3Map.put("color", "#173177");
				data.put("keyword3", keyword3Map);

				Map<String, Object> remarkMap = new HashMap<String, Object>();
				remarkMap.put("value", "请等待审核通知！");
				remarkMap.put("color", "#173177");
				data.put("remark", remarkMap);

				Map<String, Object> param = new HashMap<>();
				param.put("touser", openid);
				param.put("template_id", "iLfKzvmDR5GZaeM6BRQii86aQq6OXG0jVgZIecMhCQw");// 申请提交成功通知
				param.put("data", data);
				Map<String, String> pushMap = WxUtil.PushWx(JSON.toJSONString(param),qds);
				System.out.println(pushMap.toString());

				Session session = HibernateSessionFactory.getSession();
				String sql = " select openid from t_ryx_user u where u.user_uuid in ( select sj_user_uuid from t_ryx_user t where t.openid=?) and openid is not null ";
				SQLQuery query = session.createSQLQuery(sql);
				query.setParameter(0, openid);
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				List<Map<String, String>> list = query.list();
				if (list != null && list.size() > 0) {
					Map map = list.get(0);
					String qdsopenid = (String) map.get("OPENID");
					firstMap.put("value", "您好，接受到一个新的贷款申请，内容如下");
					firstMap.put("color", "#173177");
					data.put("first", firstMap);

					keyword1Map.put("value", frXm);
					keyword1Map.put("color", "#173177");
					data.put("keyword1", keyword1Map);

					keyword2Map.put("value", frPhone);
					keyword2Map.put("color", "#173177");
					data.put("keyword2", keyword2Map);

					keyword3Map.put("value", DatetimeUtil.getLongDatetimeString());
					keyword3Map.put("color", "#173177");
					data.put("keyword4", keyword3Map);

					remarkMap.put("value", "申请产品：" + prodName + " \n  请及时处理！");
					remarkMap.put("color", "#173177");
					data.put("remark", remarkMap);

					param = new HashMap<>();
					param.put("touser", qdsopenid);
					param.put("template_id", "NHFHv6yzCc3i36XrUxQm6AmRXyNhaW_eRzkaGsLkYh8");// 贷款申请成功提醒
					param.put("data", data);
					WxUtil.PushWx(JSON.toJSONString(param),qds);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HibernateSessionFactory.closeSession();
		}
	}

	/**
	 * 查询城市名称
	 */
	private void queryCsName(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		try {
			String xzqhszDm = request.getParameter("xzqhszDm");
			if (xzqhszDm == null || "".equals(xzqhszDm)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "省份不能为空！");
				return;
			}
			Session session = HibernateSessionFactory.getSession();
			String sql = "select xzqhsz_dm,xzqhmc from c##db_rongyixin.dm_gy_xzqh t where t.xzqhsz_dm=? ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, xzqhszDm);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				Map map = (Map) list.get(0);
				jsonObject.putAll(map);
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
	 * 产品推广海报
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void getTghbImg(HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("utf-8");
			response.setHeader("Access-Control-Allow-Origin", "*");

			String userUuid = request.getParameter("userUuid");
			if (StringUtils.isBlank(userUuid)) {
				response.getWriter().write("用户ID不能为空");
				return;
			}
			String prodUuid = request.getParameter("prodUuid");
			if (StringUtils.isBlank(prodUuid)) {
				response.getWriter().write("产品ID不能为空");
				return;
			}
			String prodName = request.getParameter("prodName");
			String qds = request.getParameter("qds");
			if(qds == null || "".equals(qds)){
				qds =  (String) request.getSession().getAttribute("qds");
			}
			// 判断图片是否存在
			String uploadCodedir = ConfigUtils.getProperty("tghb_code_dir", "D:/code_img/");
			String imgpath = uploadCodedir + "/tghb" + userUuid + "_" + prodUuid + ".jpg";
			File imgfile = new File(imgpath);
			if (imgfile.exists()) { // 存在则直接显示到网页
				ShowImgToPage(response, imgfile);
				return;
			}

			// 访问生成二维码接口
			String url = "https://www.rongyixin.net/qds/ProdServlet?action=prodDetilsInit&prodUuid=" + prodUuid
					+ "&qdsid=" + userUuid+"&qds="+qds;

			// 开始生成二维码图片
			String filepath = request.getSession().getServletContext().getRealPath("/") + "\\pages\\img\\";
			// String bg = filepath + "back/tghb_back.png";
			// if("paxyd".equals(prodUuid)){
			// bg = filepath + "back/tghb_paxyd_back.png";
			// }
			// if("fsd".equals(prodUuid)){
			// bg = filepath + "back/tghb_fsd_back.png";
			// }
			// if("fjd".equals(prodUuid)){
			// bg = filepath + "back/tghb_fjd_back.png";
			// }
			// if("ghd".equals(prodUuid)){
			// bg = filepath + "back/tghb_ghd_back.png";
			// }
			// if("xwkd".equals(prodUuid)){
			// bg = filepath + "back/tghb_xwkd_back.png";
			// }
			// if("snwsd".equals(prodUuid)){
			// bg = filepath + "back/tghb_snwsd_back.png";
			// }
			// if("wsd".equals(prodUuid)){
			// bg = filepath + "back/tghb_wsd_back.png";
			// }

			BufferedImage image = QRCodeUtil.createImage(url, "", true);// 生成二维码
			QRCodeUtil.addBackGround(image, filepath, imgpath, prodName, prodUuid);// 加背景
			ShowImgToPage(response, imgfile);
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("系统异常:" + e.getMessage());
		}
	}

	/**
	 * 获取招兵买马海报图片
	 * 
	 * @param request
	 * @param response
	 * @param dataJson
	 * @throws IOException
	 * @throws Exception
	 */
	protected void getZbmmImg(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("utf-8");
			response.setHeader("Access-Control-Allow-Origin", "*");

			String userUuid = request.getParameter("userUuid");
			if (StringUtils.isBlank(userUuid)) {
				response.getWriter().write("用户ID不能为空");
				return;
			}
			String userName = request.getParameter("userName");
			String qds = request.getParameter("qds");
			if(qds == null || "".equals(qds)){
				qds =  (String) request.getSession().getAttribute("qds");
			}
			// 判断图片是否存在
			String uploadCodedir = ConfigUtils.getProperty("zbmm_code_dir", "D:/code_img/");
			String imgpath = uploadCodedir + "/zbmm" + userUuid + ".jpg";
			File imgfile = new File(imgpath);
			if (imgfile.exists()) { // 存在则直接显示到网页
				ShowImgToPage(response, imgfile);
				return;
			}

			// 访问生成二维码接口
			String url = "https://www.rongyixin.net/qds/RegisterServlet?action=ShowRegister&sj_user_uuid=" + userUuid+"qds="+qds;

			// 开始生成二维码图片
			String filepath = request.getSession().getServletContext().getRealPath("/") + "\\pages\\img\\";
			String bg = filepath + "back/zbmm_back.jpg";
			BufferedImage image = QRCodeUtil.createImage(url, "", true);// 生成二维码
			QRCodeUtil.addBackGround(image, bg, imgpath, userName);// 加背景
			ShowImgToPage(response, imgfile);
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("系统异常:" + e.getMessage());
		}
	}

	public void ShowImgToPage(HttpServletResponse response, File imgfile) throws UnsupportedEncodingException {
		FileInputStream fis = null;
		response.setContentType("image/jpeg");
		try {
			OutputStream out = response.getOutputStream();
			fis = new FileInputStream(imgfile);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			out.write(b);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 查询贷款申请
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void querySq(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		String openid = (String) request.getSession().getAttribute("openid");
		String userUuid = request.getParameter("userUuid");
		if (userUuid == null || "".equals(userUuid)) {
			userUuid = (String) request.getSession().getAttribute("user_uuid");
		}

		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = " select SQ_UUID,GSMC,FR_XM,FR_PHONE,FR_SFZJHM,SJFKJE,PROD_UUID,PROD_NAME, QDS_UUID,QDS_MC,SQZT,to_char(SQRQ,'yyyy-mm-dd') SQRQ,LRRY,XGSJ,XGRY,OPENID  "
					+ " from t_qds_dksqb t where t.qds_uuid = ? union "
					+ " select SQ_UUID,GSMC,FR_XM,FR_PHONE,FR_SFZJHM,SJFKJE,PROD_UUID,PROD_NAME, QDS_UUID,QDS_MC,SQZT,to_char(SQRQ,'yyyy-mm-dd') SQRQ,LRRY,XGSJ,XGRY,OPENID  "
					+ " from t_qds_dksqb t where  t.openid = ?  ";
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
	 * 保存申请信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void saveSq(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String openid = (String) request.getSession().getAttribute("openid");
		JSONObject jsonObject = new JSONObject();
		try {
			String gsmc = request.getParameter("gsmc");
			if (gsmc != null && !"".equals(gsmc)) {
				gsmc = URLDecoder.decode(gsmc, "utf-8");
			}
			String frXm = request.getParameter("frXm");
			if (frXm != null && !"".equals(frXm)) {
				frXm = URLDecoder.decode(frXm, "utf-8");
			}
			String frPhone = request.getParameter("frPhone");
			if (frPhone == null || "".equals(frPhone)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "法人手机号不能为空！");
				return;
			}
			String xzqhDm = request.getParameter("xzqhDm");
			String verifyCode = (String) request.getSession().getAttribute("verifyCode");
			String verifyphone = (String) request.getSession().getAttribute("verifyPhone");
			if (!frPhone.equals(verifyphone)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "法人手机号与发送短信手机号不一致！");
				return;
			}
			String code = request.getParameter("code");
			if (code == null || "".equals(code)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "短信验证码不能为空！");
				return;
			}
			if (!code.equals(verifyCode)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "验证码不一致！");
				return;
			}

			String frSfzjhm = request.getParameter("frSfzjhm");
			String prodUuid = request.getParameter("prodUuid");
			String qdsUuid = request.getParameter("qdsUuid");
			String qds = request.getParameter("qds");
			if(qds == null || "".equals(qds)){
				qds =  (String) request.getSession().getAttribute("qds");
			}
			Session session = HibernateSessionFactory.getSession();
			String insetrSql = " insert into t_qds_dksqb (SQ_UUID,GSMC,FR_XM,FR_PHONE,FR_SFZJHM,PROD_UUID,QDS_UUID,SQZT,SQRQ,LRRY,XGSJ,XGRY,OPENID,qds) "
					+ "  values (sys_guid(),?,?,?,?,?,?,'0',sysdate,?,sysdate,?,?,?) ";
			Connection conn = HibernateSessionFactory.connection();
			PreparedStatement ps = conn.prepareStatement(insetrSql);
			ps.setString(1, gsmc);
			ps.setString(2, frXm);
			ps.setString(3, frPhone);
			ps.setString(4, frSfzjhm);
			ps.setString(5, prodUuid);
			ps.setString(6, qdsUuid);
			ps.setString(7, qdsUuid);
			ps.setString(8, qdsUuid);
			ps.setString(9, openid);
			ps.setString(10, qds);
			ps.execute();

			conn.commit();
			String url = "";
			String prodName = "";
			if (prodUuid != null && !"".equals(prodUuid)) {
				String sql = "select bank_url,kf_bz,kf_url,prod_name"
						+ "  from t_qds_prod t where t.prod_uuid=? and t.qds=? ";
				SQLQuery query = session.createSQLQuery(sql);
				query.setParameter(0, prodUuid);
				query.setParameter(1, qds);
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				List<Map<String, String>> list = query.list();
				if (list != null && list.size() > 0) {
					Map map = list.get(0);
					String kfBz = (String) map.get("KF_BZ");
					prodName = (String) map.get("PROD_NAME");
					String kfbz = (String) request.getSession().getAttribute("khbz");
					if (kfBz != null && "Y".equals(kfBz)) {// 数据库配置客服标志判断是否跳转添加客服微信URL
						// 客服标志为Y且session中有存不跳转客服标志则跳转银行
						if (kfbz != null && "N".equals(kfbz)) {
							url = (String) map.get("BANK_URL");
						} else {// 没有存不跳转标志则跳转客服
							url = (String) map.get("KF_URL");
						}
					} else {// 客服标志不为Y则跳银行
						url = (String) map.get("BANK_URL");
					}
				}
			}
			
//			 推送微信消息
//			pushDksq(gsmc, frXm, frPhone, qdsUuid, openid, prodName,qds);
						
			jsonObject.put("result", "success");
			jsonObject.put("url", url);
			jsonObject.put("msg", "保存成功！");
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
	 * 查询产品明细
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void queryProdDetils(HttpServletRequest request, HttpServletResponse response) throws IOException {

		JSONObject jsonObject = new JSONObject();
		try {
			String prodUuid = request.getParameter("prodUuid");
			if (prodUuid == null || "".equals(prodUuid)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "产品ID不能为空！");
			}
			String qds = request.getParameter("qds");
			if(qds == null || "".equals(qds)){
				qds =  (String) request.getSession().getAttribute("qds");
			}
			Session session = HibernateSessionFactory.getSession();
			String sql = " select PROD_UUID,PROD_NAME,PROD_ZT,PROD_SM,PROD_TJ,PROD_TD,PROD_SXZL,PROD_FL,MAX_JE,JJED,CGFKS,ZKFKSJ,DKQS,DKLL,HKFS,DQ,BYFY,HJFY,ZSFY,JSZQ,JSTJ,JSSM,LC_PDF,BANK_URL,TGHB_URL,XH,LOGO_URL,LRSJ,LRRY,XGSJ,XGRY,YGMYHK,PROD_LCSM from t_qds_prod t where t.prod_zt='Y' and t.prod_uuid =? and qds=? ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, prodUuid);
			query.setParameter(1, qds);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
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
	 * 查询对应渠道产品列表
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void queryProd(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		try {
			String xzqhszDm = request.getParameter("xzqhszDm");
			String prodFl = request.getParameter("prodFl");
			String prodName = request.getParameter("prodName");
			if (prodName != null && !"".equals(prodName)) {
				prodName = URLDecoder.decode(prodName, "utf-8");
			}
			String qds = request.getParameter("qds");
			if(qds == null || "".equals(qds)){
				qds =  (String) request.getSession().getAttribute("qds");
			}
			Session session = HibernateSessionFactory.getSession();
			String sql = " select PROD_UUID,PROD_NAME,PROD_ZT,PROD_SM,PROD_TJ,PROD_TD,PROD_SXZL,PROD_FL,MAX_JE,JJED,CGFKS,ZKFKSJ,DKQS,DKLL,HKFS,DQ,BYFY,HJFY,ZSFY,JSZQ,JSTJ,JSSM,LC_PDF,BANK_URL,TGHB_URL,XH,LOGO_URL,LRSJ,LRRY,XGSJ,XGRY,YGMYHK from t_qds_prod t where t.prod_zt='Y' and qds=? ";
			if (xzqhszDm != null && !"".equals(xzqhszDm)) {
				sql += " and exists(select 1 from t_qds_prod_xzqh qy where qy.prod_uuid=t.prod_uuid and qy.qds=? and qy.xzqhsz_dm=? and qy.yxbz='Y') ";
			}
			if (prodFl != null && !"".equals(prodFl)) {
				sql += " and t.prod_fl= ?  ";
			}
			if (prodName != null && !"".equals(prodName)) {
				sql += " and t.prod_Name like  ?  ";
			}
			sql += " order by xh  ";
			SQLQuery query = session.createSQLQuery(sql);
			int index = 0;
			query.setParameter(index++, qds);
			if (xzqhszDm != null && !"".equals(xzqhszDm)) {
				query.setParameter(index++, qds);
				query.setParameter(index++, xzqhszDm);
			}
			if (prodFl != null && !"".equals(prodFl)) {
				query.setParameter(index++, prodFl);
			}
			if (prodName != null && !"".equals(prodName)) {
				query.setParameter(index++, "%" + prodName + "%");
			}
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
	 * 企业首页查询产品列表
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void queryProdIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		try {
			String xzqhszDm = request.getParameter("xzqhszDm");
			String prodFl = request.getParameter("prodFl");
			String prodName = request.getParameter("prodName");
			if (prodName != null && !"".equals(prodName)) {
				prodName = URLDecoder.decode(prodName, "utf-8");
			}
			String rmbz = request.getParameter("rmbz");
			String sxbz = request.getParameter("sxbz");
			int start = 0;
			String startStr = request.getParameter("start");
			if (startStr != null && !"".equals(startStr)) {
				start = (Integer.valueOf(startStr) - 1) * 10;
			}
			Session session = HibernateSessionFactory.getSession();
			
			String sqlCount = "  ,(select count(1) from t_ryx_prod d where d.prod_zt='Y'";

			if (xzqhszDm != null && !"".equals(xzqhszDm)) {
				sqlCount += " and exists(select 1 from t_ryx_prod_xzqh qy where qy.prod_uuid=d.prod_uuid and qy.xzqhsz_dm=? and qy.yxbz='Y') ";
			}
			if (prodFl != null && !"".equals(prodFl)) {
				sqlCount += " and d.prod_fl= ?  ";
			}
			if (rmbz != null && "Y".equals(rmbz)) {//热门标志
				sqlCount += " and d.rmbz= ?  ";
			}
			
			if (prodName != null && !"".equals(prodName)) {
				sqlCount += " and d.prod_Name like  ?  ";
			}

			sqlCount += " ) as zs  ";
			
			String sql = " select PROD_UUID,PROD_NAME,PROD_ZT,PROD_SM,PROD_TJ,PROD_TD,PROD_SXZL,PROD_FL,MAX_JE,JJED,CGFKS,ZKFKSJ,DKQS,DKLL,"
					+ " HKFS,DQ,BYFY,HJFY,ZSFY,JSZQ,JSTJ,JSSM,LC_PDF,BANK_URL,TGHB_URL,XH,LOGO_URL,LRSJ,LRRY,XGSJ,XGRY,YGMYHK " + sqlCount + 
					" from t_ryx_prod t where t.prod_zt='Y' ";
			if (xzqhszDm != null && !"".equals(xzqhszDm)) {
				sql += " and exists(select 1 from t_ryx_prod_xzqh qy where qy.prod_uuid=t.prod_uuid and qy.xzqhsz_dm=? and qy.yxbz='Y') ";
			}
			if (prodFl != null && !"".equals(prodFl)) {
				sql += " and t.prod_fl= ?  ";
			}
			if (rmbz != null && "Y".equals(rmbz)) {//热门标志
				sql += " and t.rmbz= ?  ";
			}
			
			if (prodName != null && !"".equals(prodName)) {
				sql += " and t.prod_Name like  ?  ";
			}
			
			if (sxbz != null && "Y".equals(sxbz)) {//上新标志
				sql += " order by lrsj desc,xh  ";
			} else {
				sql += " order by xh  ";
			}
			
			SQLQuery query = session.createSQLQuery(sql);
			int index = 0;
			if (xzqhszDm != null && !"".equals(xzqhszDm)) {
				query.setParameter(index++, xzqhszDm);
			}
			if (prodFl != null && !"".equals(prodFl)) {
				query.setParameter(index++, prodFl);
			}
			if (rmbz != null && "Y".equals(rmbz)) {
				query.setParameter(index++, rmbz);
			}
			
			if (prodName != null && !"".equals(prodName)) {
				query.setParameter(index++, "%" + prodName + "%");
			}
			if (xzqhszDm != null && !"".equals(xzqhszDm)) {
				query.setParameter(index++, xzqhszDm);
			}
			if (prodFl != null && !"".equals(prodFl)) {
				query.setParameter(index++, prodFl);
			}
			if (rmbz != null && "Y".equals(rmbz)) {
				query.setParameter(index++, rmbz);
			}
			
			if (prodName != null && !"".equals(prodName)) {
				query.setParameter(index++, "%" + prodName + "%");
			}
			
			query.setFirstResult(start);
			query.setMaxResults(10);
			
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
	 * 查询省份
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void querySf(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = "select xzqhsz_dm,xzqhmc from c##db_rongyixin.dm_gy_xzqh t where t.xzqhjc='1' and t.yxbz='Y' order by xzqhsz_dm ";
			SQLQuery query = session.createSQLQuery(sql);
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
	 * 查询城市
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void queryCs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObject = new JSONObject();
		try {
			String xzqhszDm = request.getParameter("xzqhszDm");
			if (xzqhszDm == null || "".equals(xzqhszDm)) {
				jsonObject.put("result", "fail");
				jsonObject.put("msg", "省份不能为空！");
				return;
			}
			Session session = HibernateSessionFactory.getSession();
			String sql = "select xzqhsz_dm,xzqhmc from c##db_rongyixin.dm_gy_xzqh t where t.xzqhjc='2' and t.yxbz='Y' and t.sjxzqhsz_dm=? order by xzqhsz_dm ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, xzqhszDm);
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

	public static void main(String[] args) {
		//String gsmc, String frXm, String frPhone, String qdsUuid, String openid, String prodName
//		pushDksq("普宁市靓驰花木有限公司", "章妙兰", "13202588998", null, null, "微税贷");
	}
}

package com.ryx.service.prod;

import java.io.IOException;
import java.net.URLEncoder;
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

/**
 * 产品唯一码入口，唯一码不对则不允许进入
 */
public class Entrance extends HttpServlet implements Servlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8311498363991061321L;

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
		request.setCharacterEncoding("UTF-8");
		init(request, response);
	}

	/**
	 * 渠道有效初始化校验
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void init(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String prodUuid = request.getParameter("prodUuid");// 产品ID
		String sbm = request.getParameter("sbm");// 识别码
		String khbz = request.getParameter("khbz");// 客服标志,当传参为N时发票贷产品不跳转增加客服，直接跳转银行
		if (khbz != null && "N".equals(khbz)) {
			request.getSession().setAttribute("khbz", "N");
		}
		if (sbm != null && !"".equals(sbm) && prodUuid != null && !"".equals(prodUuid)) {
			try {
				Session session = HibernateSessionFactory.getSession();
				// 查询有效的识别码
				String sql = "select t.prod_uuid,prod_name,p.bank_url from t_ryx_prod_code t,t_ryx_prod p where t.prod_uuid=p.prod_uuid and  t.prod_uuid=? and t.sbm=? and t.yxbz='Y' ";
				SQLQuery query = session.createSQLQuery(sql);
				query.setParameter(0, prodUuid);
				query.setParameter(1, sbm);
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				List<Map<String, String>> list = query.list();
				if (list != null && list.size() > 0) {// 存在则跳转产品详情
					Map map = (Map) list.get(0);
					String prodName = (String) map.get("PROD_NAME");
//					String bankUrl = (String) map.get("BANK_URL");
//					response.sendRedirect(bankUrl);
					response.sendRedirect("pages/channel_prod.html?sbm=" + sbm +"&prod_uuid=" + prodUuid + "&prod_name="
							+ URLEncoder.encode(prodName, "utf-8"));
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
}

package com.ryx.util.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;

import com.ryx.dao.BaseService;


/**
 * 微信相关方法公共类
 */
public class WxUtil {
		/**
		 * 检查用户浏览器是什么类型
		 * @param request
		 * @return
		 */
		public static String check_userHeader(HttpServletRequest request) {
			String user_type = "OTHERS";
			String userAgent = request.getHeader("user-agent").toLowerCase();;
			if(userAgent.indexOf("android") != -1){
				user_type = "android"; //安卓
			}else if(userAgent.indexOf("iphone") != -1 || userAgent.indexOf("ipad") != -1 || userAgent.indexOf("ipod") != -1){
				user_type = "iphone"; //苹果
			}
			return user_type;
		}
		
		/**
		 * 获取tokenid
		 * 
		 * @param needaccess_token
		 * @return
		 * @throws Exception
		 */
		public static String getAccessToken() throws Exception {
			String access_token = "";
			String appid = ConfigUtils.getProperty("wx_appid", "wx192c5420d83bfbc6");
			String appsecret = ConfigUtils.getProperty("wx_app_secret", "8a8d9c19ec6cc9a05e9a7239da0e36f4");
			String sendUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid
					+ "&secret=" + appsecret;
			String result = getHttpResult(sendUrl);
			JSONObject jsonobj = new JSONObject(result);
			try {
				access_token = jsonobj.getString("access_token");
			} catch (Exception e) {
			}
			return access_token;
		}
		
		/**
		 * 
		 * @param needaccess_token
		 *            为true时直接获取
		 * @return
		 * @throws Exception
		 */
		public static String getAccessToken(boolean needaccess_token ,String qds) throws Exception {
			String access_token = "";
			if (!needaccess_token) {
				BaseService baseService = new BaseService();
				String sql = "select access_token from t_qds_access_token t WHERE t.qds = :qds and and access_token is not null and  t.lrsj >SYSDATE-1/24 ORDER BY lrsj DESC ";
				Map param = new HashMap();
				param.put("qds", qds);
				List<Map> list = baseService.listBySql(sql , param);
				if (list != null && list.size() > 0) {
					Map map = list.get(0);
					access_token = (String) map.get("access_token");
				}
				if (access_token != null && !"".equals(access_token)) {
					return access_token;
				}
			}
			if (needaccess_token) {
				String appid = ConfigUtils.getProperty("wx_appid_"+qds, "wx192c5420d83bfbc6");
				String appsecret = ConfigUtils.getProperty("wx_app_secret_"+qds, "8a8d9c19ec6cc9a05e9a7239da0e36f4");
				String sendUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid
						+ "&secret=" + appsecret;
				String result = getHttpResult(sendUrl);
				// System.out.println("获取AccessToken返回："+result);
				JSONObject jsonobj = new JSONObject(result);
				try {
					access_token = jsonobj.getString("access_token");
				} catch (Exception e) {
				}
				System.out.println("access_token:" + access_token);
				BaseService baseServiceIsert = new BaseService();
				String sqlIsert = "INSERT INTO t_qds_access_token (uuid,access_token,lrsj,qds) VALUES(sys_guid(),:access_token,SYSDATE,:qds) ";
				Map param = new HashMap();
				param.put("access_token", access_token);
				param.put("qds", qds);
				try {
					baseServiceIsert.beginTransaction();
					baseServiceIsert.executeBySql(sqlIsert, param);
					baseServiceIsert.commit();
				} catch (Exception e) {
					e.printStackTrace();
					baseServiceIsert.rollback();
				}

			}
			return access_token;
		}
		
		/*
		 * 获取访问地址链接返回值
		 */
		public static String getHttpResult(String url) {
			try {
				HttpGet httpRequest = new HttpGet(url);
				HttpResponse httpResponse = HttpClients.createDefault().execute(httpRequest);
				return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
		
//		// 获取AccessToken
//		@SuppressWarnings("rawtypes")
//		public static String getAccessToken(HttpServletRequest request,String appid,String appsecret) throws Exception {
//			String access_token ="";
//			boolean needaccess_token = true; // 需要获取access_token
//			String wx_cache_token = ConfigUtils.getProperty("wx_cache_token","0");
//			if(request!=null&&"1".equals(wx_cache_token)){
//				access_token = (String) request.getSession().getServletContext().getAttribute("access_token");
//				Date access_token_time = (Date) request.getSession().getServletContext().getAttribute("access_token_time");
//				if (!StringUtils.isBlank(access_token) && access_token_time != null) {
//					Date now = new Date();
//					int secs = DatetimeUtil.SecBetween(access_token_time, now);
//					if (secs < 3600) {
//						needaccess_token = false;
//					}
//				}
//			}
//
//			if (needaccess_token) {
//				String sendUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + appsecret;
//				Map resultmap = HttpUtil.Get(HttpClients.createDefault(), sendUrl, "UTF-8", false, null, null);
//				String result = (String) resultmap.get("result");
//				System.out.println("获取AccessToken返回："+result);
//				JSONObject jsonobj = new JSONObject(result);
//				try {
//					access_token = jsonobj.getString("access_token");
//				} catch (Exception e) {
//				}
//				System.out.println("access_token:" + access_token);
//
//				if(request!=null){
//					request.getSession().getServletContext().setAttribute("access_token", access_token);
//					request.getSession().getServletContext().setAttribute("access_token_time", new Date());
//				}
//			}
//			return access_token;
//		}
		
		public static Map<String, String> PushWx(String message,String qds) {
			Map<String, String> reMap = new HashMap<String, String>();
			try {
				String access_token = getAccessToken(false,qds);
				String Url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token;

				HttpClient client = HttpClients.createDefault();
				Map resultmap = HttpUtil.PostString(client, Url, message, "UTF-8", false, null, null, null, null);
				int code = (int) resultmap.get("code");
				String result = (String) resultmap.get("result");
				if (code != 200) {
					reMap.put("code", String.valueOf(code));
					reMap.put("result", result);
					return reMap;
				}
				JSONObject jsonobj = new JSONObject(result);
				String errcode = jsonobj.getString("errcode");
				String errmsg = jsonobj.getString("errmsg");
				if ("0".equals(errcode)) {
					reMap.put("code", errcode);
					reMap.put("result", errmsg);
					return reMap;
				} else {
					reMap.put("code", errcode);
					reMap.put("result", errmsg);
					return reMap;
				}
			} catch (Exception e) {
				e.printStackTrace();
				reMap.put("code", "异常！");
				reMap.put("result", e.toString());
				return reMap;
			}
		}
		
		
		
//		//获取用户信息
//		@SuppressWarnings({ "rawtypes"})
//		public static String GetuserInfo(HttpServletRequest request,String appid,String appsecret,String openid){
//			try {
//				String access_token =  getAccessToken(request, appid, appsecret);
//				if(StringUtils.isBlank(access_token)){
//					return "{\"result\":\"error\",\"message\":\"获取AccessToken失败\"}";
//				}
//				String Url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
//				
//				System.out.println("微信获取用户信息："+Url);
//				HttpClient client = HttpClients.createDefault();
//				Map resultmap = HttpUtil.Get(client, Url, "UTF-8",false,null,null);
//				int code = (Integer) resultmap.get("code");
//				String result = (String) resultmap.get("result");
//				System.out.println("获取用户信息返回："+result);
//				if(code!=200){
//					return "{\"result\":\"error\",\"message\":\"获取用户信息失败\"}";
//				}
//				JSONObject jsonobj = new JSONObject(result);
//				try {
//					String errmsg = jsonobj.getString("errmsg");
//					if(!StringUtils.isBlank(errmsg)){
//						return "{\"result\":\"error\",\"message\":\""+errmsg+"\"}";
//					}
//				} catch (Exception e) {
//				}
//				return "{\"result\":\"ok\",\"message\":\"获取用户信息成功\",\"data\":"+result+"}";
//			} catch (Exception e) {
//				e.printStackTrace();
//				return "{\"result\":\"error\",\"message\":\"系统异常\"}";
//			}
//		}
}

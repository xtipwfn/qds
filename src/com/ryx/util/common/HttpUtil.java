package com.ryx.util.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("deprecation")
public class HttpUtil {
	
	/**
	 * 发送Get请求
	 */
	public static Map<String,Object> Get(HttpClient httpclient,String url,String encording,boolean GenIp,String Referer,String UserAgent){
		return Get(httpclient, url, encording, GenIp, Referer, UserAgent,null);
	}
	public static Map<String,Object> Get(HttpClient httpclient,String url,String encording,boolean GenIp,String Referer,String UserAgent,Map<String,String> HeaderMap){
		return Get(httpclient, url, encording, GenIp, Referer, UserAgent,HeaderMap,null);
	}
	public static Map<String,Object> Get(HttpClient httpclient,String url,String encording,boolean GenIp,String Referer,String UserAgent,Map<String,String> HeaderMap,HttpHost proxy){
		Map<String,Object> resultmap = new HashMap<String,Object>();
		try {
			HttpGet HttpGet=new HttpGet(url);
			if(GenIp){
				String ip = getRandomIp();
				System.out.println("生成随机IP："+ip);
				HttpGet.addHeader("x-forwarded-for",ip);
			}
			if(Referer!=null){
				HttpGet.addHeader("Referer",Referer);
			}
			if(UserAgent!=null){
				HttpGet.addHeader("User-Agent",UserAgent);
			}
			if(HeaderMap!=null){
				for (Map.Entry<String, String> entry : HeaderMap.entrySet()) { 
					HttpGet.addHeader(entry.getKey(),entry.getValue());
				}
			}
			
			Builder builder = RequestConfig.custom()
					.setConnectionRequestTimeout(60000)
					.setConnectTimeout(60000)
					.setSocketTimeout(60000);
			if(proxy!=null){
				builder = builder.setProxy(proxy);
			}
			RequestConfig RequestConfig = builder.build();
			HttpGet.setConfig(RequestConfig);
			
			HttpResponse httpResponse = httpclient.execute(HttpGet);
			resultmap.put("headers", httpResponse.getAllHeaders());
			
			int code = httpResponse.getStatusLine().getStatusCode();
			String result=EntityUtils.toString(httpResponse.getEntity(), encording);
			resultmap.put("code", code);
			resultmap.put("result", result);
		} catch (Exception e) {
			e.printStackTrace();
			resultmap.put("code", 500);
			resultmap.put("result", e.getMessage());
		}
		return resultmap;
	}
	
	/**
	 * 发送Post请求
	 */
	public static Map<String,Object> Post(HttpClient httpclient,String url,Map<String, String> params,String encording,boolean GenIp,String Referer,String UserAgent,String ContentType){
		return Post(httpclient, url, params, encording, GenIp, Referer, UserAgent, ContentType,null);
	}
	public static Map<String,Object> Post(HttpClient httpclient,String url,Map<String, String> params,String encording,boolean GenIp,String Referer,String UserAgent,String ContentType,Map<String,String> HeaderMap){
		return Post(httpclient, url, params, encording, GenIp, Referer, UserAgent, ContentType, HeaderMap,null);
	}
	public static Map<String,Object> Post(HttpClient httpclient,String url,Map<String, String> params,String encording,boolean GenIp,String Referer,String UserAgent,String ContentType,Map<String,String> HeaderMap,HttpHost proxy){
		Map<String,Object> resultmap = new HashMap<String,Object>();
		try {
			HttpPost httppost = new HttpPost(url);
			if(GenIp){
				String ip = getRandomIp();
				System.out.println("生成随机IP："+ip);
				httppost.addHeader("x-forwarded-for",ip);
			}
			if(Referer!=null){
				httppost.addHeader("Referer",Referer);
			}
			if(UserAgent!=null){
				httppost.addHeader("User-Agent",UserAgent);
			}
			if(ContentType!=null){
				httppost.addHeader("Content-Type",ContentType);
			}
			
			Builder builder = RequestConfig.custom()
					.setConnectionRequestTimeout(60000)
					.setConnectTimeout(60000)
					.setSocketTimeout(60000);
			if(proxy!=null){
				builder = builder.setProxy(proxy);
			}
			RequestConfig RequestConfig = builder.build();
			httppost.setConfig(RequestConfig);
			
			if(HeaderMap!=null){
				for (Map.Entry<String, String> entry : HeaderMap.entrySet()) { 
					httppost.addHeader(entry.getKey(),entry.getValue());
				}
			}
			List<NameValuePair> formParams = new ArrayList<NameValuePair>(); 
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			httppost.setEntity(new UrlEncodedFormEntity(formParams, encording));
			HttpResponse response = httpclient.execute(httppost);
			int code= response.getStatusLine().getStatusCode();
			String result = EntityUtils.toString(response.getEntity(),encording);
			if (code == 302) {  
				String locationUrl=response.getLastHeader("Location").getValue();  
				if(!locationUrl.startsWith("http")){
					if(locationUrl.startsWith("/")){ //绝对路径
						locationUrl = url.split("//")[0]+"//"+url.split("//")[1].split("/")[0] + locationUrl;
					}else{ //相对路径
						String url2 = url.split("\\?")[0].split("//")[1];
						if(url2.contains("/")){
							url2 = url2.substring(0,url2.lastIndexOf("/")+1);
						}
						locationUrl = url.split("//")[0]+"//"+url2 + locationUrl;
					}
				}
				return Get(httpclient,locationUrl,encording,GenIp,Referer,UserAgent);
			}
			resultmap.put("code", code);
			resultmap.put("result", result);
		} catch (Exception e) {
			e.printStackTrace();
			resultmap.put("code", 500);
			resultmap.put("result", e.getMessage());
		}
		return resultmap;
	}
	
	/**
	 * 发送Post请求 直接发送字符串
	 */
	public static Map<String,Object> PostString(HttpClient httpclient,String url,String content,String encording,boolean GenIp,String Referer,String UserAgent,String ContentType){
		return PostString(httpclient, url, content, encording, GenIp, Referer, UserAgent, ContentType,null);
	}
	public static Map<String,Object> PostString(HttpClient httpclient,String url,String content,String encording,boolean GenIp,String Referer,String UserAgent,String ContentType,Map<String,String> HeaderMap){
		return PostString(httpclient, url, content, encording, GenIp, Referer, UserAgent, ContentType, HeaderMap,null);
	}
	public static Map<String,Object> PostString(HttpClient httpclient,String url,String content,String encording,boolean GenIp,String Referer,String UserAgent,String ContentType,Map<String,String> HeaderMap,HttpHost proxy){
		Map<String,Object> resultmap = new HashMap<String,Object>();
		try {
			HttpPost httppost = new HttpPost(url);
			if(GenIp){
				String ip = getRandomIp();
				System.out.println("生成随机IP："+ip);
				httppost.addHeader("x-forwarded-for",ip);
			}
			if(Referer!=null){
				httppost.addHeader("Referer",Referer);
			}
			if(UserAgent!=null){
				httppost.addHeader("User-Agent",UserAgent);
			}
			if(ContentType!=null){
				httppost.addHeader("Content-Type",ContentType);
			}
			
			Builder builder = RequestConfig.custom()
					.setConnectionRequestTimeout(60000)
					.setConnectTimeout(60000)
					.setSocketTimeout(60000);
			
			if(proxy!=null){
				builder = builder.setProxy(proxy);
			}
			
			RequestConfig RequestConfig = builder.build();
			httppost.setConfig(RequestConfig);
			
			if(HeaderMap!=null){
				for (Map.Entry<String, String> entry : HeaderMap.entrySet()) { 
					httppost.addHeader(entry.getKey(),entry.getValue());
				}
			}
			StringEntity entity = new StringEntity(content,encording);
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			int code= response.getStatusLine().getStatusCode();
			String result = EntityUtils.toString(response.getEntity(),encording);
			resultmap.put("code", code);
			resultmap.put("result", result);
		} catch (Exception e) {
			e.printStackTrace();
			resultmap.put("code", 500);
			resultmap.put("result", e.getMessage());
		}
		return resultmap;
	}
	
	/**
	 * Post下载文件
	 */
	public static String DownloadFile(HttpClient httpclient,String url,String FilePath,Map<String, String> params,String encording,String contentType,boolean GenIp,String Referer,String UserAgent){
		try {
			HttpUriRequest request =null;
			if(params!=null){ //参数不为空则用post
				request= new HttpPost(url);
				List<NameValuePair> formParams = new ArrayList<NameValuePair>(); 
				for (Map.Entry<String, String> entry : params.entrySet()) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				((HttpPost) request).setEntity(new UrlEncodedFormEntity(formParams, encording));
			}else{
				request= new HttpGet(url);
			}
			
			if(GenIp){
				String ip = getRandomIp();
				System.out.println("生成随机IP："+ip);
				request.addHeader("x-forwarded-for",ip);
			}
			if(Referer!=null){
				request.addHeader("Referer",Referer);
			}
			if(UserAgent!=null){
				request.addHeader("User-Agent",UserAgent);
			}
			
			HttpResponse response = httpclient.execute(request);
			int code= response.getStatusLine().getStatusCode();
			if(code==200){
				if(contentType!=null){//检查content-type是否为预期
					String type = response.getHeaders("content-type")[0].getValue();
					if(!type.contains(contentType)){ //按文本返回
						String result = EntityUtils.toString(response.getEntity(),encording);
						return result;
					}
				}
				File file = new File(FilePath);
				if(file.exists())
					file.delete();
				FileOutputStream outputStream = new FileOutputStream(file);
				InputStream inputStream = response.getEntity().getContent();
				byte b[] = new byte[1024];
				int j = 0;
				while ((j = inputStream.read(b)) != -1) {
					outputStream.write(b, 0, j);
				}
				outputStream.flush();
				outputStream.close();
				return "成功";
			}else{
				return "失败:"+code;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "失败:"+e.getMessage();
		}
	}
	
	/**
	 * 下载文件字节
	 */
	public static byte[] DownloadFileByte(HttpClient httpclient,String url,Map<String, String> params,String encording,boolean GenIp,String Referer,String UserAgent){
		try {
			HttpUriRequest request =null;
			if(params!=null){ //参数不为空则用post
				request= new HttpPost(url);
				List<NameValuePair> formParams = new ArrayList<NameValuePair>(); 
				for (Map.Entry<String, String> entry : params.entrySet()) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				((HttpPost) request).setEntity(new UrlEncodedFormEntity(formParams, encording));
			}else{
				request= new HttpGet(url);
			}
			
			if(GenIp){
				String ip = getRandomIp();
				System.out.println("生成随机IP："+ip);
				request.addHeader("x-forwarded-for",ip);
			}
			if(Referer!=null){
				request.addHeader("Referer",Referer);
			}
			if(UserAgent!=null){
				request.addHeader("User-Agent",UserAgent);
			}
			HttpResponse httpResponse = httpclient.execute(request);
			return EntityUtils.toByteArray(httpResponse.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 发送Get请求 支持Https 废弃
	 */
	@Deprecated
	public static Map<String,Object> SSLGet(HttpClient httpclient,String url,String encording,boolean GenIp,String Referer,String UserAgent){
		Map<String,Object> resultmap = new HashMap<String,Object>();
		try {
			X509TrustManager xtm = new X509TrustManager() { 
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
				
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
				
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { xtm }, null);
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			httpclient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			HttpGet httpGet = new HttpGet(url);
			if(GenIp){
				String ip = getRandomIp();
				System.out.println("生成随机IP："+ip);
				httpGet.addHeader("x-forwarded-for",ip);
			}
			if(Referer!=null){
				httpGet.addHeader("Referer",Referer);
			}
			if(UserAgent!=null){
				httpGet.addHeader("User-Agent",UserAgent);
			}
			HttpResponse response = httpclient.execute(httpGet); 
			int code = response.getStatusLine().getStatusCode();
			String result=EntityUtils.toString(response.getEntity(), encording);
			resultmap.put("code", code);
			resultmap.put("result", result);
		} catch (Exception e) {
			e.printStackTrace();
			resultmap.put("code", 500);
			resultmap.put("result", e.getMessage());
		}
		return resultmap;
	}
	
	/**
	 * 发送Post请求 支持Https
	 */
	@Deprecated
	public static Map<String,Object> SSLPost(HttpClient httpclient,String url,Map<String, String> params,String encording,boolean GenIp,String Referer,String UserAgent){
		Map<String,Object> resultmap = new HashMap<String,Object>();
		try {
			X509TrustManager xtm = new X509TrustManager() { 
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { xtm }, null);
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			httpclient.getConnectionManager().getSchemeRegistry()
					.register(new Scheme("https", 443, socketFactory));
			HttpPost httpPost = new HttpPost(url); 
			if(GenIp){
				String ip = getRandomIp();
				System.out.println("生成随机IP："+ip);
				httpPost.addHeader("x-forwarded-for",ip);
			}
			if(Referer!=null){
				httpPost.addHeader("Referer",Referer);
			}
			if(UserAgent!=null){
				httpPost.addHeader("User-Agent",UserAgent);
			}
			List<NameValuePair> formParams = new ArrayList<NameValuePair>(); 
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, encording));
			HttpResponse response = httpclient.execute(httpPost); 
			int code = response.getStatusLine().getStatusCode();
			String result=EntityUtils.toString(response.getEntity(), encording);
			resultmap.put("code", code);
			resultmap.put("result", result);
		} catch (Exception e) {
			e.printStackTrace();
			resultmap.put("code", 500);
			resultmap.put("result", e.getMessage());
		}
		return resultmap;
	}
	
	//创建Httpclient 支持访问证书错误地址
	public static HttpClient CreateHttpsClient (){
        try {
        	SSLContextBuilder builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslsf)
                    .build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(200);//max connection
            
            return HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).setConnectionManagerShared(true).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
	/*
	 * 随机生成国内IP地址
	 */
	public static String getRandomIp() {
		// ip范围
		int[][] range = { { 607649792, 608174079 },// 36.56.0.0-36.63.255.255
				{ 1038614528, 1039007743 },// 61.232.0.0-61.237.255.255
				{ 1783627776, 1784676351 },// 106.80.0.0-106.95.255.255
				{ 2035023872, 2035154943 },// 121.76.0.0-121.77.255.255
				{ 2078801920, 2079064063 },// 123.232.0.0-123.235.255.255
				{ -1950089216, -1948778497 },// 139.196.0.0-139.215.255.255
				{ -1425539072, -1425014785 },// 171.8.0.0-171.15.255.255
				{ -1236271104, -1235419137 },// 182.80.0.0-182.92.255.255
				{ -770113536, -768606209 },// 210.25.0.0-210.47.255.255
				{ -569376768, -564133889 }, // 222.16.0.0-222.95.255.255
		};

		Random rdint = new Random();
		int index = rdint.nextInt(10);
		String ip = num2ip(range[index][0] + new Random().nextInt(range[index][1] - range[index][0]));
		return ip;
	}

	/*
	 * 将十进制转换成ip地址
	 */
	public static String num2ip(int ip) {
		int[] b = new int[4];
		String x = "";

		b[0] = (int) ((ip >> 24) & 0xff);
		b[1] = (int) ((ip >> 16) & 0xff);
		b[2] = (int) ((ip >> 8) & 0xff);
		b[3] = (int) (ip & 0xff);
		x = Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "." + Integer.toString(b[3]);

		return x;
	}

	/**
	 * get 实名认证get拼接
	 * 
	 * @param host
	 * @param path
	 * @param method
	 * @param headers
	 * @param querys
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doGet(String host, String path, String method,
			Map<String, String> headers, Map<String, String> querys)
			throws Exception {
		HttpClient httpClient = wrapClient(host);

		HttpGet request = new HttpGet(buildUrl(host, path, querys));
		for (Map.Entry<String, String> e : headers.entrySet()) {
			request.addHeader(e.getKey(), e.getValue());
		}

		return httpClient.execute(request);
	}

	private static HttpClient wrapClient(String host) {
		HttpClient httpClient = new DefaultHttpClient();
		if (host.startsWith("https://")) {
			sslClient(httpClient);
		}

		return httpClient;
	}

	private static void sslClient(HttpClient httpClient) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] xcs, String str) {

				}

				public void checkServerTrusted(X509Certificate[] xcs, String str) {

				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = httpClient.getConnectionManager();
			SchemeRegistry registry = ccm.getSchemeRegistry();
			registry.register(new Scheme("https", 443, ssf));
		} catch (KeyManagementException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static String buildUrl(String host, String path,
			Map<String, String> querys) throws UnsupportedEncodingException {
		StringBuilder sbUrl = new StringBuilder();
		sbUrl.append(host);
		if (!StringUtils.isBlank(path)) {
			sbUrl.append(path);
		}
		if (null != querys) {
			StringBuilder sbQuery = new StringBuilder();
			for (Map.Entry<String, String> query : querys.entrySet()) {
				if (0 < sbQuery.length()) {
					sbQuery.append("&");
				}
				if (StringUtils.isBlank(query.getKey())
						&& !StringUtils.isBlank(query.getValue())) {
					sbQuery.append(query.getValue());
				}
				if (!StringUtils.isBlank(query.getKey())) {
					sbQuery.append(query.getKey());
					if (!StringUtils.isBlank(query.getValue())) {
						sbQuery.append("=");
						sbQuery.append(URLEncoder.encode(query.getValue(),
								"utf-8"));
					}
				}
			}
			if (0 < sbQuery.length()) {
				sbUrl.append("?").append(sbQuery);
			}
		}

		return sbUrl.toString();
	}
}

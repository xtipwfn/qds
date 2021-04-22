package com.ryx.util.common;

import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.alibaba.druid.proxy.jdbc.ClobProxyImpl;
//import org.hibernate.lob.SerializableClob;

public class StringUtil {

	/**
	 * 字符串判空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (null == str || "".equals(str) || "".equals(str.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 邮箱格式是否合法
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (isEmpty(email))
			return false;
		boolean bool = true;
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		if (!matcher.matches()) {
			bool = false;
		}
		return bool;
	}

	/**
	 * 判断字符串是不是double型
	 * 
	 * @param str
	 * @return
	 */

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]+[.]{0,1}[0-9]*[dD]{0,1}");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 按长度打印字符串
	 * 
	 * @param msg
	 * @param maxlength
	 */
	public static void println(String msg, int maxlength) {
		try {
			if (msg != null && msg.length() > maxlength)
				msg = msg.substring(0, maxlength) + "...";
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 手机号验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][0-9]{10}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数 此方法中前三位格式有： 13+任意数 15+除4的任意数 18+除1和4的任意数
	 * 17+除9的任意数 147
	 */
	public static boolean isChinaPhoneLegal(String str) {
		String regExp = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(str);
		return m.matches();
	}

	public static boolean isMobile2(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean isMatch = false;
		// 制定验证条件，目前有些新卡会出现类似166开头的，需要适当调整
		String regex1 = "^[1][3,4,5,7,8][0-9]{9}$";
		String regex2 = "^((13[0-9])|(14[579])|(15([0-3,5-9]))|(16[6])|(17[0135678])|(18[0-9]|19[89]))\\d{8}$";

		p = Pattern.compile(regex2);
		m = p.matcher(str);
		isMatch = m.matches();
		return isMatch;
	}

	// clob转string
	/*
	 * public static String clobToString(SerializableClob clob){ Reader reader;
	 * StringBuffer sb = new StringBuffer(); try { reader =
	 * clob.getCharacterStream(); BufferedReader br = new
	 * BufferedReader(reader); String temp = null; while ((temp=br.readLine())
	 * != null) { sb.append(temp); } } catch (Exception e) {
	 * e.printStackTrace(); } return sb.toString(); }
	 */

	public static String clobToString(Object o) {
		Reader reader;
		StringBuffer sb = new StringBuffer();
		try {
			ClobProxyImpl clob = (ClobProxyImpl) getTarget(o);
			reader = clob.getCharacterStream();
			BufferedReader br = new BufferedReader(reader);
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 从代理类中获取原始类
	 * 
	 * @param proxy
	 * @return
	 * @throws Exception
	 */
	public static Object getTarget(Object proxy) throws Exception {
		Field field = proxy.getClass().getSuperclass().getDeclaredField("h");
		field.setAccessible(true);
		// 获取指定对象中此字段的值
		Object clob = field.get(proxy); // 获取Proxy对象中的此字段的值
		Field Field = clob.getClass().getDeclaredField("clob");
		Field.setAccessible(true);
		return Field.get(clob);
	}

	/**
	 * 生成手机号码
	 */
	public static String getTel() {
		String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
		int index = getNum(0, telFirst.length - 1);
		String first = telFirst[index];
		String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
		String third = String.valueOf(getNum(1, 9100) + 10000).substring(1);
		return first + second + third;
	}

	public static int getNum(int start, int end) {
		return (int) (Math.random() * (end - start + 1) + start);
	}

	/**
	 * 脱敏
	 * 
	 * @param str
	 * @return
	 */
	public static String tuoMin(String str, int start, int end) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		if (str.length() <= start) {
			return str;
		}
		String a = str.substring(0, start);
		int len = end - start;
		String b = "";
		for (int i = 0; i < len; i++) {
			b = b + "*";
		}
		String c = "";
		if (str.length() > end) {
			c = str.substring(end, str.length());
		}
		return a + b + c;
	}

}

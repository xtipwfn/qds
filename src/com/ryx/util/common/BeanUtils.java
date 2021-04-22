package com.ryx.util.common;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public abstract class BeanUtils {

	/**
	 * Bean转Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map toMap(Object javaBean) {
		Map result = new HashMap();
		Method[] methods = javaBean.getClass().getDeclaredMethods();
		for (Method method : methods) {
			try {
				if (method.getName().startsWith("get")) {
					String field = method.getName();
					field = field.substring(field.indexOf("get") + 3);
					field = field.toLowerCase().charAt(0) + field.substring(1);
					Object value = method.invoke(javaBean, (Object[]) null);
					result.put(field, null == value ? "" : value.toString());
				}

			} catch (Exception e) {
			}
		}
		return result;
	}

	/**
	 * Bean转Map
	 */
	public static Map<String, Object> objecttoMap(Object javaBean) {
		Map<String, Object> result = new HashMap<String, Object>();
		Method[] methods = javaBean.getClass().getDeclaredMethods();
		for (Method method : methods) {
			try {
				if (method.getName().startsWith("get")) {
					String field = method.getName();
					field = field.substring(field.indexOf("get") + 3);
					field = field.toLowerCase().charAt(0) + field.substring(1);
					Object value = method.invoke(javaBean, (Object[]) null);
					result.put(field, null == value ? "" : value);
				}
			} catch (Exception e) {
			}
		}
		return result;
	}

	/**
	 * Map转Bean
	 */
	@SuppressWarnings("rawtypes")
	public static Object toBean(Object javabean, Map data) {
		Method[] methods = javabean.getClass().getDeclaredMethods();
		for (Method method : methods) {
			try {
				if (method.getName().startsWith("set")) {
					String field = method.getName();
					field = field.substring(field.indexOf("set") + 3);
					// field = field.toLowerCase().charAt(0) +
					// field.substring(1);
					Object obj = data.get(field.toLowerCase());
					if (obj == null)
						continue;
					String ParamType = method.getParameterTypes()[0].getName()
							.toLowerCase();
					// 处理BigDecimal
					if (obj instanceof BigDecimal) { // 兼容Oracle,Java(int float
														// double)对应Oracle的number类型,查询返回均为BigDecimal
						BigDecimal bdobj = (BigDecimal) obj;
						if (ParamType.contains("int")) {
							obj = bdobj.intValue();
						}
						if (ParamType.contains("float")) {
							obj = bdobj.floatValue();
						}
						if (ParamType.contains("double")) {
							obj = bdobj.doubleValue();
						}
					}
					// 处理Boolean
					if (ParamType.contains("boolean")) {
						if (obj instanceof BigDecimal) { // 兼容Oracle,Java的boolean类型对应Oracle的number类型,查询返回BigDecimal
							BigDecimal bdobj = (BigDecimal) obj;
							if (bdobj.intValue() == 1)
								obj = true;
							else
								obj = false;
						}
						if (obj instanceof Byte) { // 兼容SqlServer,Java的boolean类型对应SqlServer的tinyint,查询返回Byte
							Byte btobj = (Byte) obj;
							if (btobj.intValue() == 1)
								obj = true;
							else
								obj = false;
						}
					}
					method.invoke(javabean, new Object[] { obj });
				}
			} catch (Exception e) {
			}
		}
		return javabean;
	}

	/**
	 * ResultSet转Bean
	 */
	@SuppressWarnings("rawtypes")
	public static Object ResultSetToBean(ResultSet rs, Class clazz) {
		Object obj = null;
		try {
			obj = clazz.newInstance();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String fieldName = rsmd.getColumnName(i).toLowerCase();
				// String firstLetter=fieldName.substring(0, 1).toUpperCase();
				// String setter = "set" + firstLetter + fieldName.substring(1);
				Method[] methods = clazz.getDeclaredMethods();
				for (Method method : methods) {
					if (("set" + fieldName).equalsIgnoreCase(method.getName())) {
						try {
							method.invoke(obj,
									rs.getObject(fieldName.toUpperCase()));
						} catch (Exception e) {
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return obj;
	}

	/**
	 * ResultSet转Map
	 */
	@SuppressWarnings("rawtypes")
	public static Map ResultSetToMap(ResultSet rs) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String fieldName = rsmd.getColumnName(i).toLowerCase();
				map.put(fieldName, rs.getObject(fieldName.toUpperCase()));
			}
		} catch (Exception e) {
		}
		return map;
	}

	/**
	 * Map转XML
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String MapToXml(List<Map> Maps) {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("GBK");
		Element body = doc.addElement("body");
		Element data = body.addElement("data");
		int i = 1;
		for (Map Map : Maps) {
			Element row = data.addElement("row");
			row.addAttribute("id", String.valueOf(i));
			Element main = row.addElement("main");
			Set set = Map.entrySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = (Entry<String, Object>) it
						.next();
				if (entry.getValue() != null)
					main.addElement(entry.getKey()).setText(
							String.valueOf(entry.getValue()));
			}
			i++;
		}
		return doc.asXML();
	}

	/**
	 * Map转XML
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String MapToXml(List<Map> BodyMaps, List<Map> DetailMaps,
			String JoinColum) {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("GBK");
		Element body = doc.addElement("body");
		Element data = body.addElement("data");
		int i = 1;
		for (Map BodyMap : BodyMaps) {
			Element row = data.addElement("row");
			row.addAttribute("id", String.valueOf(i));
			Element main = row.addElement("main");
			Set set = BodyMap.entrySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = (Entry<String, Object>) it
						.next();
				if (entry.getValue() != null)
					main.addElement(entry.getKey()).setText(
							String.valueOf(entry.getValue()));
			}
			if (JoinColum != null && !"".equals(JoinColum)) {
				String bodyid = (String) BodyMap.get(JoinColum);
				Element detail = row.addElement("detail");
				int j = 1;
				for (Map DetailMap : DetailMaps) {
					String detailbodyid = (String) DetailMap.get(JoinColum);
					if (bodyid != null && detailbodyid != null
							&& bodyid.equals(detailbodyid)) {
						Element item = detail.addElement("item");
						item.addAttribute("id", String.valueOf(j));
						set = DetailMap.entrySet();
						it = set.iterator();
						while (it.hasNext()) {
							Map.Entry<String, String> entry = (Entry<String, String>) it
									.next();
							if (entry.getValue() != null)
								item.addElement(entry.getKey()).setText(
										String.valueOf(entry.getValue()));
						}
						j++;
					}
				}
			}
			i++;
		}
		return doc.asXML();
	}

	/**
	 * XML转Map
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Map> XmlToMap(String XMLString) {
		List<Map> result = new ArrayList<Map>();
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new ByteArrayInputStream(XMLString
					.getBytes("GBK")));
			Element root = doc.getRootElement();
			Element data = root.element("data");
			List<Element> rows = data.elements("row");
			for (Element row : rows) {
				Element main = row.element("main");
				List<Element> attrs = main.elements();
				Map map = new HashMap();
				for (Element attr : attrs) {
					map.put(attr.getName(), attr.getText());
				}
				result.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Bean转XML
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String BeanToXml(List<Object> Beans) {
		List<Map> Maps = new ArrayList<Map>();
		for (Object Bean : Beans) {
			Maps.add(toMap(Bean));
		}
		return MapToXml(Maps);
	}

	/**
	 * XML转Bean
	 */
	@SuppressWarnings({ "rawtypes" })
	public static List XmlToBean(String XMLString, Object obj) {
		List<Map> Maps = XmlToMap(XMLString);
		List<Object> objs = new ArrayList<Object>();
		for (Map Map : Maps) {
			objs.add(toBean(obj, Map));
		}
		return objs;
	}

	/**
	 * Bean转Sql
	 */
	public static String BeanToSql(Object Bean) {
		String result = "";
		String fields = "";
		String values = "";
		Method[] methods = Bean.getClass().getDeclaredMethods();
		for (Method method : methods) {
			try {
				if (method.getName().startsWith("get")) {
					String field = method.getName();
					field = field.substring(field.indexOf("get") + 3);
					field = field.toLowerCase().charAt(0) + field.substring(1);
					fields += field + ",";
					Object value = method.invoke(Bean, (Object[]) null);
					if (value == null) {
						values += "null,";
					} else {
						if (value instanceof Date)
							value = DatetimeUtil.getDateString((Date) value,
									"yyyy-MM-dd HH:mm:ss");
						values += "'" + value + "',";
					}
				}
			} catch (Exception e) {
			}
		}
		if (fields.length() > 0)
			fields = fields.substring(0, fields.length() - 1);
		if (values.length() > 0)
			values = values.substring(0, values.length() - 1);
		result = " (" + fields + ") values (" + values + ") ";
		return result;
	}

	/**
	 * Map转SQL
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String MapToSql(Map Map) {
		String result = "";
		String fields = "";
		String values = "";
		Set set = Map.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Entry<String, Object>) it.next();
			String field = entry.getKey();
			fields += field + ",";
			Object value = entry.getValue();
			if (value == null) {
				values += "null,";
			} else {
				if (value instanceof Date)
					value = DatetimeUtil.getDateString((Date) value,
							"yyyy-MM-dd HH:mm:ss");
				values += "'" + value + "',";
			}
		}
		if (fields.length() > 0)
			fields = fields.substring(0, fields.length() - 1);
		if (values.length() > 0)
			values = values.substring(0, values.length() - 1);
		result = " (" + fields + ") values (" + values + ") ";
		return result;
	}

	/**
	 * JSON转Map
	 */
	public static Map<String, Object> JsonToMap(String JSONString) {
		if (JSONString != null)
			JSONString = JSONString.replace("[", "").replace("]", "");
		return JSON.parseObject(JSONString,
				new TypeReference<Map<String, Object>>() {
				});
	}

	/**
	 * JSON转Map
	 */
	public static Map<String, Object> JsonToHashMap(String JSONString) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (JSONString != null) {
			if (JSONString.startsWith("\"")) {
				JSONString = JSONString.substring(1, JSONString.length());
			}
			if (JSONString.endsWith("\"")) {
				JSONString = JSONString.substring(0, JSONString.length() - 1);
			}
			JSONString = JSONString.replace("\\", "");
			map = JSON.parseObject(JSONString,
					new TypeReference<Map<String, Object>>() {
					});
		}
		return map;
	}

	/**
	 * Map键转小写
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map MapKeyToLowerCase(Map Map) {
		if (Map != null) {
			Map newmap = new HashMap();
			Set set = Map.entrySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = (Entry<String, Object>) it
						.next();
				newmap.put(entry.getKey().toLowerCase(), entry.getValue());
			}
			return newmap;
		}
		return Map;
	}

	/**
	 * List<Map>键转小写
	 */
	@SuppressWarnings({ "rawtypes" })
	public static List<Map> MapKeyToLowerCase(List<Map> Maps) {
		if (Maps != null) {
			List<Map> newMaps = new ArrayList<Map>();
			for (Map Map : Maps) {
				newMaps.add(MapKeyToLowerCase(Map));
			}
			return newMaps;
		}
		return Maps;
	}

	/**
	 * 转换为boolean
	 */
	public static boolean parseBoolean(Object obj) {
		boolean bool = false;
		if(obj instanceof Boolean){
			Boolean boolObj = (Boolean) obj;
			return boolObj;
		}
		if (obj instanceof BigDecimal) {
			BigDecimal bdobj = (BigDecimal) obj;
			if (bdobj.intValue() == 1)
				return true;
		}
		if (obj instanceof Byte) {
			Byte btobj = (Byte) obj;
			if (btobj.intValue() == 1)
				return true;
		}
		if (obj instanceof String) {
			String strobj = (String) obj;
			return Boolean.parseBoolean(strobj);
		}
		return bool;
	}

	/**
	 * 转换为double
	 */
	public static double parseDouble(Object obj) {
		double db = 0d;
		if (obj instanceof Double) {
			return (double)obj;
		}
		if (obj instanceof BigDecimal) {
			BigDecimal bdobj = (BigDecimal) obj;
			return bdobj.doubleValue();
		}
		if (obj instanceof Float) {
			Float flobj = (Float) obj;
			return flobj.doubleValue();
		}
		if (obj instanceof String) {
			String strobj = (String) obj;
			return Double.parseDouble(strobj);
		}
		return db;
	}

	/**
	 * 转换为float
	 */
	public static float parseFloat(Object obj) {
		float ft = 0f;
		if (obj instanceof Float) {
			return (float)obj;
		}
		if (obj instanceof BigDecimal) {
			BigDecimal bdobj = (BigDecimal) obj;
			return bdobj.floatValue();
		}
		if (obj instanceof Double) {
			Double dbobj = (Double) obj;
			return dbobj.floatValue();
		}
		if (obj instanceof String) {
			String strobj = (String) obj;
			return Float.parseFloat(strobj);
		}
		return ft;
	}

	/**
	 * 转换为Integer
	 */
	public static int parseInteger(Object obj) {
		int i = 0;
		if (obj instanceof Integer) {
			return (int)obj;
		}
		if (obj instanceof BigDecimal) {
			BigDecimal bdobj = (BigDecimal) obj;
			return bdobj.intValue();
		}
		if (obj instanceof BigInteger) {
			BigInteger biobj = (BigInteger) obj;
			return biobj.intValue();
		}
		if (obj instanceof Long) {
			Long lobj = (Long) obj;
			return lobj.intValue();
		}
		if (obj instanceof String) {
			String strobj = (String) obj;
			return Integer.parseInt(strobj);
		}
		return i;
	}

	/**
	 * 转换为Long
	 */
	public static long parseLong(Object obj) {
		long l = 0l;
		if (obj instanceof Long) {
			return (long)obj;
		}
		if (obj instanceof BigDecimal) {
			BigDecimal bdobj = (BigDecimal) obj;
			return bdobj.longValue();
		}
		if (obj instanceof BigInteger) {
			BigInteger biobj = (BigInteger) obj;
			return biobj.longValue();
		}
		if (obj instanceof Integer) {
			Integer iobj = (Integer) obj;
			return iobj.longValue();
		}
		if (obj instanceof String) {
			String strobj = (String) obj;
			return Long.parseLong(strobj);
		}
		return l;
	}

}

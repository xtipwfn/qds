package com.ryx.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import com.ryx.util.common.BeanUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BaseService {
	private Transaction Transaction;
	private Transaction TempTransaction;
	private Connection conn;

	// 提交
	public void commit() {
		if (Transaction != null) {
			Transaction.commit();
			Transaction = null;
		}
		HibernateSessionFactory.closeSession();
	}

	// 回滚
	public void rollback() {
		if (Transaction != null) {
			Transaction.rollback();
			Transaction = null;
		}
		HibernateSessionFactory.closeSession();
	}

	// 开始hibernate事务
	public void beginTransaction() {
		Transaction = HibernateSessionFactory.getSession().beginTransaction();
	}

	// 关闭hibernate session
	public void closeSession() {
		HibernateSessionFactory.closeSession();
	}

	/**
	 * 保存
	 * 
	 * @throws Exception
	 */
	public boolean save(Object obj) throws Exception {
		try {
			HibernateSessionFactory.getSession().save(obj);
			HibernateSessionFactory.getSession().flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return true;
	}

	/**
	 * 更新
	 * 
	 * @throws Exception
	 */
	public boolean update(Object obj) throws Exception {
		try {
			HibernateSessionFactory.getSession().update(obj);
			HibernateSessionFactory.getSession().flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return true;
	}

	/**
	 * 保存更新
	 * 
	 * @throws Exception
	 */
	public boolean saveOrUpdate(Object obj) throws Exception {
		try {
			HibernateSessionFactory.getSession().saveOrUpdate(obj);
			HibernateSessionFactory.getSession().flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return true;
	}

	/**
	 * 删除
	 * 
	 * @throws Exception
	 */
	public boolean delete(Object obj) throws Exception {
		try {
			HibernateSessionFactory.getSession().delete(obj);
			HibernateSessionFactory.getSession().flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return true;
	}

	/**
	 * 清除
	 */
	public boolean evict(Object obj) {
		if (obj == null)
			return true;
		try {
			HibernateSessionFactory.getSession().evict(obj);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return true;
	}

	/**
	 * 获取
	 */
	public Object get(Class clazz, String id) {
		try {
			return HibernateSessionFactory.getSession().get(clazz, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	/**
	 * 列表
	 */
	public List list(Class clazz) {
		try {
			Query q = HibernateSessionFactory.getSession().createQuery(
					"from " + clazz.getName());
			return q.list();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public List list(String hql) {
		// System.out.println(hql);
		try {
			Query q = HibernateSessionFactory.getSession().createQuery(hql);
			return q.list();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public List list(String hql, int page, int row) {
		// System.out.println(hql);
		try {
			Query q = HibernateSessionFactory.getSession().createQuery(hql)
					.setFirstResult((page - 1) * row).setMaxResults(row);
			return q.list();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public List list(String hql, Map<String, Object> param) {
		// System.out.println(hql);
		try {
			Query q = HibernateSessionFactory.getSession().createQuery(hql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			return q.list();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public List list(String hql, Map<String, Object> param, int page, int row) {
		// System.out.println(hql);
		try {
			Query q = HibernateSessionFactory.getSession().createQuery(hql)
					.setFirstResult((page - 1) * row).setMaxResults(row);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			return q.list();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	/**
	 * 数量
	 */
	public Long count(String hql) {
		// System.out.println(hql);
		try {
			Query q = HibernateSessionFactory.getSession().createQuery(hql);
			Object count = q.uniqueResult();
			if (count instanceof BigDecimal) {
				BigDecimal bdcount = (BigDecimal) count;
				return bdcount.longValue();
			}
			if (count instanceof BigInteger) {
				BigInteger bicount = (BigInteger) count;
				return bicount.longValue();
			}
			if (count instanceof Integer) {
				Integer icount = (Integer) count;
				return icount.longValue();
			}
			return (Long) count;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public Long count(String hql, Map<String, Object> param) {
		// System.out.println(hql);
		try {
			Query q = HibernateSessionFactory.getSession().createQuery(hql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			Object count = q.uniqueResult();
			if (count instanceof BigDecimal) {
				BigDecimal bdcount = (BigDecimal) count;
				return bdcount.longValue();
			}
			if (count instanceof BigInteger) {
				BigInteger bicount = (BigInteger) count;
				return bicount.longValue();
			}
			if (count instanceof Integer) {
				Integer icount = (Integer) count;
				return icount.longValue();
			}
			return (Long) count;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	/**
	 * 执行更新
	 * 
	 * @throws Exception
	 */
	public boolean execute(String hql) throws Exception {
		// System.out.println(hql);
		try {
			Query q = HibernateSessionFactory.getSession().createQuery(hql);
			q.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return true;
	}

	public boolean execute(String hql, Map<String, Object> param)
			throws Exception {
		// System.out.println(hql);
		try {
			Query q = HibernateSessionFactory.getSession().createQuery(hql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			q.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return true;
	}

	/**
	 * SQL获取
	 */
	public Map getBySql(String sql) {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {
				return l.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return null;
	}

	public Object getBySql(String sql, Class clazz) throws Exception {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {

				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, l.get(0));
				return bean;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return null;
	}

	public Map getBySql(String sql, Map<String, Object> param) {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {
				return l.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return null;
	}

	public Object getBySql(String sql, Map<String, Object> param, Class clazz)
			throws Exception {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, l.get(0));
				return bean;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return null;
	}

	/**
	 * SQL列表
	 */
	public List<Map> listBySql(String sql) {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			return BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public List listBySql(String sql, Class clazz) throws Exception {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public List<Map> listBySql(String sql, int page, int row) {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			q.setFirstResult((page - 1) * row).setMaxResults(row);
			return BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public List listBySql(String sql, int page, int row, Class clazz)
			throws Exception {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			q.setFirstResult((page - 1) * row).setMaxResults(row);
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public List<Map> listBySql(String sql, Map<String, Object> param) {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			return BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public List listBySql(String sql, Map<String, Object> param, Class clazz)
			throws Exception {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public List<Map> listBySql(String sql, Map<String, Object> param, int page,
			int row) {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			q.setFirstResult((page - 1) * row).setMaxResults(row);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			return BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public List listBySql(String sql, Map<String, Object> param, int page,
			int row, Class clazz) throws Exception {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			q.setFirstResult((page - 1) * row).setMaxResults(row);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	/**
	 * SQL数量
	 */
	public Long countBySql(String sql) {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			Object count = q.uniqueResult();
			if (count instanceof BigDecimal) {
				BigDecimal bdcount = (BigDecimal) count;
				return bdcount.longValue();
			}
			if (count instanceof BigInteger) {
				BigInteger bicount = (BigInteger) count;
				return bicount.longValue();
			}
			if (count instanceof Integer) {
				Integer icount = (Integer) count;
				return icount.longValue();
			}
			return (Long) count;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	public Long countBySql(String sql, Map<String, Object> param) {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			Object count = q.uniqueResult();
			if (count instanceof BigDecimal) {
				BigDecimal bdcount = (BigDecimal) count;
				return bdcount.longValue();
			}
			if (count instanceof BigInteger) {
				BigInteger bicount = (BigInteger) count;
				return bicount.longValue();
			}
			if (count instanceof Integer) {
				Integer icount = (Integer) count;
				return icount.longValue();
			}
			return (Long) count;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
	}

	/**
	 * 执行SQL更新
	 * 
	 * @throws Exception
	 */
	public boolean executeBySql(String sql) throws Exception {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			q.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return true;
	}

	public boolean executeBySql(String sql, Map<String, Object> param)
			throws Exception {
		// System.out.println(sql);
		try {
			SQLQuery q = HibernateSessionFactory.getSession().createSQLQuery(
					sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			q.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (Transaction == null)
				HibernateSessionFactory.closeSession();
		}
		return true;
	}

	// 创建外部数据库连接
	public Connection createConnExt(String connectstring) throws Exception {
		Connection conn = null;
		try {
			String classname = "";
			if (connectstring.startsWith("jdbc:sqlserver"))
				classname = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			if (connectstring.startsWith("jdbc:mysql"))
				classname = "com.mysql.jdbc.Driver";
			if (connectstring.startsWith("jdbc:oracle"))
				classname = "oracle.jdbc.driver.OracleDriver";
			if ("".equals(classname))
				throw new Exception("不支持的数据库");
			String dbusername = connectstring.substring(
					connectstring.indexOf("?dbusername=") + 12,
					connectstring.indexOf("&dbpassword="));
			String dbpassword = connectstring.substring(
					connectstring.indexOf("&dbpassword=") + 12,
					connectstring.length());
			connectstring = connectstring.substring(0,
					connectstring.indexOf("?dbusername="));
			Class.forName(classname);
			conn = DriverManager.getConnection(connectstring, dbusername,
					dbpassword);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return conn;
	}

	public Map<Integer, Object> getParameterIndex(String sql,
			Map<String, Object> param) {
		Map<Integer, Object> result = new HashMap<Integer, Object>();
		Map<String, Integer> tmpm = new HashMap<String, Integer>();
		List<Integer> indexl = new ArrayList<Integer>();
		for (String name : param.keySet()) {
			String tmp = ":" + name;
			int index = sql.indexOf(tmp);
			if (index >= 0) {
				tmpm.put(name, index);
				indexl.add(index);
			}
		}
		Collections.sort(indexl);
		for (String key : tmpm.keySet()) {
			int i = tmpm.get(key);
			int index = indexl.indexOf(i);
			Object o = param.get(key);
			result.put(index + 1, o);
		}
		return result;
	}

	/**
	 * 获得PreparedStatement
	 * 
	 * @throws Exception
	 */
	public PreparedStatement prepare(Connection conn, String sql)
			throws Exception {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return ps;
	}

	/**
	 * 获得CallableStatement
	 * 
	 * @throws Exception
	 */
	public CallableStatement csprepare(Connection conn, String sql)
			throws Exception {
		CallableStatement cs = null;
		try {
			cs = conn.prepareCall(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return cs;
	}

	/**
	 * 关闭连接
	 * 
	 * @throws Exception
	 */
	public void close(Connection conn) throws Exception {
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 关闭Statement
	 * 
	 * @throws Exception
	 */
	public void close(Statement stmt) throws Exception {
		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 关闭ResultSet
	 * 
	 * @throws Exception
	 */
	public void close(ResultSet rs) throws Exception {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 获得表头
	 */
	public List<Map> getMetaData(String sql, int width) throws Exception {
		// System.out.println(sql);
		List MetaDatass = new ArrayList();
		List<Map> MetaDatas = new ArrayList<Map>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = HibernateSessionFactory.connection();
			ps = prepare(conn, sql);
			ps.setMaxRows(1);
			rs = ps.executeQuery();
			if (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String fieldName = rsmd.getColumnName(i);
					Map MetaData = new HashMap();
					MetaData.put("field", fieldName);
					MetaData.put("title", fieldName);
					MetaData.put("width", width);
					MetaDatas.add(MetaData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		MetaDatass.add(MetaDatas);
		return MetaDatass;
	}

	/**
	 * 获得表头
	 */
	public List<Map> getMetaData(String sql, int width, String connectstring)
			throws Exception {
		// System.out.println(sql);
		List MetaDatass = new ArrayList();
		List<Map> MetaDatas = new ArrayList<Map>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = createConnExt(connectstring);
			ps = prepare(conn, sql);
			ps.setMaxRows(1);
			rs = ps.executeQuery();
			if (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String fieldName = rsmd.getColumnName(i);
					Map MetaData = new HashMap();
					MetaData.put("field", fieldName);
					MetaData.put("title", fieldName);
					MetaData.put("width", width);
					MetaDatas.add(MetaData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		MetaDatass.add(MetaDatas);
		return MetaDatass;
	}

	/**
	 * 原生SQL数量
	 */
	public long countByNativeSql(String sql, String connectstring)
			throws Exception {
		// System.out.println(sql);
		long count = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = createConnExt(connectstring);
			ps = prepare(conn, sql);
			rs = ps.executeQuery();
			if (rs.next())
				count = rs.getLong(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return count;
	}

	public long countByNativeSql(String sql, Map<String, Object> param,
			String connectstring) throws Exception {
		// System.out.println(sql);
		long count = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = createConnExt(connectstring);
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return count;
	}

	/**
	 * 原生SQL对象
	 */
	public Map getByNativeSql(String sql, String connectstring)
			throws Exception {
		// System.out.println(sql);
		Map map = new HashMap();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = createConnExt(connectstring);
			ps = prepare(conn, sql);
			rs = ps.executeQuery();
			if (rs.next())
				map = BeanUtils.ResultSetToMap(rs);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return map;
	}

	public Map getByNativeSql(String sql, Map<String, Object> param,
			String connectstring) throws Exception {
		// System.out.println(sql);
		Map map = new HashMap();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = createConnExt(connectstring);
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			if (rs.next())
				map = BeanUtils.ResultSetToMap(rs);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return map;
	}

	public Object getByNativeSql(String sql, Class clazz, String connectstring)
			throws Exception {
		// System.out.println(sql);
		Object Object = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = createConnExt(connectstring);
			ps = prepare(conn, sql);
			rs = ps.executeQuery();
			if (rs.next())
				Object = BeanUtils.ResultSetToBean(rs, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return Object;
	}

	public Object getByNativeSql(String sql, Map<String, Object> param,
			Class clazz, String connectstring) throws Exception {
		// System.out.println(sql);
		Object Object = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = createConnExt(connectstring);
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			if (rs.next())
				Object = BeanUtils.ResultSetToBean(rs, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return Object;
	}

	/**
	 * 原生SQL列表
	 */
	public List<Map> listByNativeSql(String sql, String connectstring)
			throws Exception {
		// System.out.println(sql);
		List<Map> results = new ArrayList<Map>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = createConnExt(connectstring);
			ps = prepare(conn, sql);
			rs = ps.executeQuery();
			while (rs.next())
				results.add(BeanUtils.ResultSetToMap(rs));
		} catch (Exception e) {
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return results;
	}

	public List listByNativeSql(String sql, Class clazz, String connectstring)
			throws Exception {
		// System.out.println(sql);
		List results = new ArrayList();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = createConnExt(connectstring);
			ps = prepare(conn, sql);
			rs = ps.executeQuery();
			while (rs.next())
				results.add(BeanUtils.ResultSetToBean(rs, clazz));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return results;
	}

	public List listByNativeSql(String sql, Map<String, Object> param,
			String connectstring) throws Exception {
		// System.out.println(sql);
		List results = new ArrayList();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = createConnExt(connectstring);
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				results.add(BeanUtils.ResultSetToMap(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return results;
	}

	public List listByNativeSql(String sql, Map<String, Object> param,
			Class clazz, String connectstring) throws Exception {
		// System.out.println(sql);
		List results = new ArrayList();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = createConnExt(connectstring);
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				results.add(BeanUtils.ResultSetToBean(rs, clazz));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return results;
	}

	public List<Map> listByNativeSql(String sql, int page, int row,
			String connectstring) throws Exception {
		// System.out.println(sql);
		int first = (page - 1) * row;
		int i = 1;
		List<Map> results = new ArrayList<Map>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = createConnExt(connectstring);
			ps = prepare(conn, sql);
			ps.setMaxRows(page * row);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (i > first)
					results.add(BeanUtils.ResultSetToMap(rs));
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return results;
	}

	public List listByNativeSql(String sql, int page, int row, Class clazz,
			String connectstring) throws Exception {
		// System.out.println(sql);
		int first = (page - 1) * row;
		int i = 1;
		List results = new ArrayList();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = createConnExt(connectstring);
			ps = prepare(conn, sql);
			ps.setMaxRows(page * row);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (i > first)
					results.add(BeanUtils.ResultSetToBean(rs, clazz));
				i++;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return results;
	}

	public List listByNativeSql(String sql, Map<String, Object> param,
			int page, int row, String connectstring) throws Exception {
		// System.out.println(sql);
		List results = new ArrayList();
		int first = (page - 1) * row;
		int i = 1;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = createConnExt(connectstring);
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			ps.setMaxRows(page * row);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (i > first) {
					results.add(BeanUtils.ResultSetToMap(rs));
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return results;
	}

	public List listByNativeSql(String sql, Map<String, Object> param,
			int page, int row, Class clazz, String connectstring)
			throws Exception {
		// System.out.println(sql);
		List results = new ArrayList();
		int first = (page - 1) * row;
		int i = 1;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = createConnExt(connectstring);
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			ps.setMaxRows(page * row);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (i > first) {
					results.add(BeanUtils.ResultSetToBean(rs, clazz));
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return results;
	}

	/**
	 * 执行原生SQL
	 */
	public boolean executeByNativeSql(String sql, String connectstring)
			throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = createConnExt(connectstring);
			ps = prepare(conn, sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(ps);
			close(conn);
		}
		return flag;
	}

	/**
	 * 执行原生SQL
	 */
	public boolean executeByNativeSql(String sql, Map<String, Object> param,
			String connectstring) throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = createConnExt(connectstring);
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(ps);
			close(conn);
		}
		return flag;
	}

	/**
	 * 执行原生存储过程
	 */
	public boolean executeByNativeProc(String sql, String connectstring)
			throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		CallableStatement cs = null;
		Connection conn = null;
		try {
			conn = createConnExt(connectstring);
			cs = csprepare(conn, sql);
			cs.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(cs);
			close(conn);
		}
		return flag;
	}

	/**
	 * 执行原生存储过程
	 */
	public boolean executeByNativeProc(String sql, Map<String, Object> param,
			String connectstring) throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		CallableStatement cs = null;
		Connection conn = null;
		try {
			conn = createConnExt(connectstring);
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			cs = csprepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				cs.setObject(index, o);
			}
			cs.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(cs);
			close(conn);
		}
		return flag;
	}

	// 创建数据库连接 支持事务
	public Connection createConn(String connectstring, boolean autoCommit)
			throws Exception {
		try {
			String classname = "";
			if (connectstring.startsWith("jdbc:sqlserver"))
				classname = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			if (connectstring.startsWith("jdbc:mysql"))
				classname = "com.mysql.jdbc.Driver";
			if (connectstring.startsWith("jdbc:oracle"))
				classname = "oracle.jdbc.driver.OracleDriver";
			if ("".equals(classname)) {
				throw new Exception("不支持的数据库。");
			}
			String dbusername = connectstring.substring(
					connectstring.indexOf("?dbusername=") + 12,
					connectstring.indexOf("&dbpassword="));
			String dbpassword = connectstring.substring(
					connectstring.indexOf("&dbpassword=") + 12,
					connectstring.length());
			connectstring = connectstring.substring(0,
					connectstring.indexOf("?dbusername="));
			Class.forName(classname);
			conn = DriverManager.getConnection(connectstring, dbusername,
					dbpassword);
			conn.setAutoCommit(autoCommit);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return conn;
	}

	// 数据库连接提交
	public void commitConn() {
		if (conn != null) {
			try {
				conn.commit();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	// 数据库连接回滚
	public void roolbackConn() {
		if (conn != null) {
			try {
				conn.rollback();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	// 关闭数据库连接
	public void closeConn() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	/**
	 * 原生SQL数量
	 */
	public long countByNativeSql(String sql) throws Exception {
		// System.out.println(sql);
		long count = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = prepare(conn, sql);
			rs = ps.executeQuery();
			if (rs.next())
				count = rs.getLong(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return count;
	}

	public long countByNativeSql(String sql, Map<String, Object> param)
			throws Exception {
		// System.out.println(sql);
		long count = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return count;
	}

	/**
	 * 原生SQL对象
	 */
	public Map getByNativeSql(String sql) throws Exception {
		// System.out.println(sql);
		Map map = new HashMap();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = prepare(conn, sql);
			rs = ps.executeQuery();
			if (rs.next())
				map = BeanUtils.ResultSetToMap(rs);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return map;
	}

	public Map getByNativeSql(String sql, Map<String, Object> param)
			throws Exception {
		// System.out.println(sql);
		Map map = new HashMap();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			if (rs.next())
				map = BeanUtils.ResultSetToMap(rs);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return map;
	}

	public Object getByNativeSql(String sql, Class clazz) throws Exception {
		// System.out.println(sql);
		Object Object = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = prepare(conn, sql);
			rs = ps.executeQuery();
			if (rs.next())
				Object = BeanUtils.ResultSetToBean(rs, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return Object;
	}

	public Object getByNativeSql(String sql, Map<String, Object> param,
			Class clazz) throws Exception {
		// System.out.println(sql);
		Object Object = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			if (rs.next())
				Object = BeanUtils.ResultSetToBean(rs, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return Object;
	}

	/**
	 * 原生SQL列表
	 */
	public List<Map> listByNativeSql(String sql) throws Exception {
		// System.out.println(sql);
		List<Map> results = new ArrayList<Map>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = prepare(conn, sql);
			rs = ps.executeQuery();
			while (rs.next())
				results.add(BeanUtils.ResultSetToMap(rs));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return results;
	}

	public List listByNativeSql(String sql, Class clazz) throws Exception {
		// System.out.println(sql);
		List results = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = prepare(conn, sql);
			rs = ps.executeQuery();
			while (rs.next())
				results.add(BeanUtils.ResultSetToBean(rs, clazz));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return results;
	}

	public List listByNativeSql(String sql, Map<String, Object> param)
			throws Exception {
		// System.out.println(sql);
		List results = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				results.add(BeanUtils.ResultSetToMap(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return results;
	}

	public List listByNativeSql(String sql, Map<String, Object> param,
			Class clazz) throws Exception {
		// System.out.println(sql);
		List results = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				results.add(BeanUtils.ResultSetToBean(rs, clazz));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return results;
	}

	public List<Map> listByNativeSql(String sql, int page, int row)
			throws Exception {
		// System.out.println(sql);
		int first = (page - 1) * row;
		int i = 1;
		List<Map> results = new ArrayList<Map>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = prepare(conn, sql);
			ps.setMaxRows(page * row);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (i > first)
					results.add(BeanUtils.ResultSetToMap(rs));
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return results;
	}

	public List listByNativeSql(String sql, int page, int row, Class clazz)
			throws Exception {
		// System.out.println(sql);
		int first = (page - 1) * row;
		int i = 1;
		List results = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = prepare(conn, sql);
			ps.setMaxRows(page * row);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (i > first)
					results.add(BeanUtils.ResultSetToBean(rs, clazz));
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return results;
	}

	public List listByNativeSql(String sql, Map<String, Object> param,
			int page, int row) throws Exception {
		// System.out.println(sql);
		List results = new ArrayList();
		int first = (page - 1) * row;
		int i = 1;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			ps.setMaxRows(page * row);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (i > first) {
					results.add(BeanUtils.ResultSetToMap(rs));
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return results;
	}

	public List listByNativeSql(String sql, Map<String, Object> param,
			int page, int row, Class clazz) throws Exception {
		// System.out.println(sql);
		List results = new ArrayList();
		int first = (page - 1) * row;
		int i = 1;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			ps.setMaxRows(page * row);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (i > first) {
					results.add(BeanUtils.ResultSetToBean(rs, clazz));
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rs);
			close(ps);
		}
		return results;
	}

	/**
	 * 执行原生SQL
	 */
	public boolean executeByNativeSql(String sql) throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		PreparedStatement ps = null;
		try {
			ps = prepare(conn, sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(ps);
		}
		return flag;
	}

	/**
	 * 执行原生SQL
	 */
	public boolean executeByNativeSql(String sql, Map<String, Object> param)
			throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		PreparedStatement ps = null;
		try {
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			ps = prepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				ps.setObject(index, o);
			}
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(ps);
		}
		return flag;
	}

	/**
	 * 执行原生存储过程
	 */
	public boolean executeByNativeProc(String sql) throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		CallableStatement cs = null;
		try {
			cs = csprepare(conn, sql);
			cs.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(cs);
		}
		return flag;
	}

	/**
	 * 执行原生存储过程
	 */
	public boolean executeByNativeProc(String sql, Map<String, Object> param)
			throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		CallableStatement cs = null;
		try {
			String newsql = sql;
			if (conn.getClass().getName().toLowerCase().contains("sqlserver")
					|| conn.getClass().getName().toLowerCase()
							.contains("mysql")) {
				newsql = sql.replaceAll(":\\w+", "?");
			}
			cs = csprepare(conn, newsql);
			Map<Integer, Object> nparam = this.getParameterIndex(sql, param);
			for (int index : nparam.keySet()) {
				Object o = nparam.get(index);
				cs.setObject(index, o);
			}
			cs.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(cs);
		}
		return flag;
	}

	/**
	 * Hibernate SQL数量
	 */
	public long countByHibSql(String sql, String connectstring)
			throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			Object count = q.uniqueResult();
			if (count instanceof BigDecimal) {
				BigDecimal bdcount = (BigDecimal) count;
				return bdcount.longValue();
			}
			if (count instanceof BigInteger) {
				BigInteger bicount = (BigInteger) count;
				return bicount.longValue();
			}
			if (count instanceof Integer) {
				Integer icount = (Integer) count;
				return icount.longValue();
			}
			return (Long) count;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
	}

	public long countByHibSql(String sql, Map<String, Object> param,
			String connectstring) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			Object count = q.uniqueResult();
			if (count instanceof BigDecimal) {
				BigDecimal bdcount = (BigDecimal) count;
				return bdcount.longValue();
			}
			if (count instanceof BigInteger) {
				BigInteger bicount = (BigInteger) count;
				return bicount.longValue();
			}
			if (count instanceof Integer) {
				Integer icount = (Integer) count;
				return icount.longValue();
			}
			return (Long) count;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
	}

	/**
	 * Hibernate SQL对象
	 */
	public Map getByHibSql(String sql, String connectstring) throws Exception {
		// System.out.println(sql);
		Map map = new HashMap();
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {
				return l.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
		return map;
	}

	public Map getByHibSql(String sql, Map<String, Object> param,
			String connectstring) throws Exception {
		// System.out.println(sql);
		Map map = new HashMap();
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {
				return l.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
		return map;
	}

	public Object getByHibSql(String sql, Class clazz, String connectstring)
			throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, l.get(0));
				return bean;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
		return null;
	}

	public Object getByHibSql(String sql, Map<String, Object> param,
			Class clazz, String connectstring) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, l.get(0));
				return bean;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
		return null;
	}

	/**
	 * Hibernate SQL列表
	 */
	public List<Map> listByHibSql(String sql, String connectstring)
			throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			List list = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, int page, int rows,
			String connectstring) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			q.setFirstResult((page - 1) * rows).setMaxResults(rows);
			return BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, Map<String, Object> param,
			String connectstring) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			return BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, Map<String, Object> param, int page,
			int rows, String connectstring) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			q.setFirstResult((page - 1) * rows).setMaxResults(rows);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			return BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
	}

	public List<Map> listByHibSql(String sql, Class clazz, String connectstring)
			throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, int page, int rows, Class clazz,
			String connectstring) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			q.setFirstResult((page - 1) * rows).setMaxResults(rows);
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, Map<String, Object> param,
			Class clazz, String connectstring) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, Map<String, Object> param, int page,
			int rows, Class clazz, String connectstring) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			q.setFirstResult((page - 1) * rows).setMaxResults(rows);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
	}

	/**
	 * 执行Hibernate SQL
	 */
	public boolean executeByHibSql(String sql, String connectstring)
			throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			q.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
		return flag;
	}

	public boolean executeByHibSql(String sql, Map<String, Object> param,
			String connectstring) throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		Session session = null;
		try {
			TempSessionFactory.setConfiguration(connectstring);
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			q.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TempSessionFactory.closeSession();
		}
		return flag;
	}

	// 临时hibernate打开session
	public void openTempSession(String connectstring) throws Exception {
		TempSessionFactory.setConfiguration(connectstring);
	}

	// 临时hibernate开始事务
	public void beginTempTransaction() {
		Transaction = TempSessionFactory.getSession().beginTransaction();
	}

	// 临时hibernate提交事务
	public void commitTemp() {
		if (TempTransaction != null) {
			TempTransaction.commit();
			TempTransaction = null;
		}
		TempSessionFactory.closeSession();
	}

	// 临时Hibernate回滚事务
	public void rollbackTemp() {
		if (TempTransaction != null) {
			TempTransaction.rollback();
			TempTransaction = null;
		}
		TempSessionFactory.closeSession();
	}

	// 临时hibernate关闭session
	public void closeTempSession() {
		TempSessionFactory.closeSession();
	}

	/**
	 * Hibernate SQL数量
	 */
	public long countByHibSql(String sql) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			Object count = q.uniqueResult();
			if (count instanceof BigDecimal) {
				BigDecimal bdcount = (BigDecimal) count;
				return bdcount.longValue();
			}
			if (count instanceof BigInteger) {
				BigInteger bicount = (BigInteger) count;
				return bicount.longValue();
			}
			if (count instanceof Integer) {
				Integer icount = (Integer) count;
				return icount.longValue();
			}
			return (Long) count;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
	}

	public long countByHibSql(String sql, Map<String, Object> param)
			throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			Object count = q.uniqueResult();
			if (count instanceof BigDecimal) {
				BigDecimal bdcount = (BigDecimal) count;
				return bdcount.longValue();
			}
			if (count instanceof BigInteger) {
				BigInteger bicount = (BigInteger) count;
				return bicount.longValue();
			}
			if (count instanceof Integer) {
				Integer icount = (Integer) count;
				return icount.longValue();
			}
			return (Long) count;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
	}

	/**
	 * Hibernate SQL对象
	 */
	public Map getByHibSql(String sql) throws Exception {
		// System.out.println(sql);
		Map map = new HashMap();
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {
				return l.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
		return map;
	}

	public Map getByHibSql(String sql, Map<String, Object> param)
			throws Exception {
		// System.out.println(sql);
		Map map = new HashMap();
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {
				return l.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
		return map;
	}

	public Object getByHibSql(String sql, Class clazz) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, l.get(0));
				return bean;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
		return null;
	}

	public Object getByHibSql(String sql, Map<String, Object> param, Class clazz)
			throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> l = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			if (l != null && l.size() > 0) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, l.get(0));
				return bean;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
		return null;
	}

	/**
	 * Hibernate SQL列表
	 */
	public List<Map> listByHibSql(String sql) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			List list = BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, int page, int rows) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			q.setFirstResult((page - 1) * rows).setMaxResults(rows);
			return BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, Map<String, Object> param)
			throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			return BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, Map<String, Object> param, int page,
			int rows) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			q.setFirstResult((page - 1) * rows).setMaxResults(rows);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			return BeanUtils.MapKeyToLowerCase(q.setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP).list());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
	}

	public List<Map> listByHibSql(String sql, Class clazz) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, int page, int rows, Class clazz)
			throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			q.setFirstResult((page - 1) * rows).setMaxResults(rows);
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, Map<String, Object> param, Class clazz)
			throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
	}

	public List listByHibSql(String sql, Map<String, Object> param, int page,
			int rows, Class clazz) throws Exception {
		// System.out.println(sql);
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			q.setFirstResult((page - 1) * rows).setMaxResults(rows);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			List<Map> list = BeanUtils.MapKeyToLowerCase(q
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list());
			List beans = new ArrayList();
			for (Map Map : list) {
				Object bean = clazz.newInstance();
				BeanUtils.toBean(bean, Map);
				beans.add(bean);
			}
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
	}

	/**
	 * 执行Hibernate SQL
	 */
	public boolean executeByHibSql(String sql) throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			q.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
		return flag;
	}

	public boolean executeByHibSql(String sql, Map<String, Object> param)
			throws Exception {
		// System.out.println(sql);
		boolean flag = true;
		Session session = null;
		try {
			session = TempSessionFactory.getSession();
			SQLQuery q = session.createSQLQuery(sql);
			for (Entry<String, Object> MapString : param.entrySet()) {
				if (MapString.getValue() instanceof Collection<?>) {
					q.setParameterList(MapString.getKey(),
							(Collection<?>) MapString.getValue());
				} else if (MapString.getValue() instanceof Object[]) {
					q.setParameterList(MapString.getKey(),
							(Object[]) MapString.getValue());
				} else {
					q.setParameter(MapString.getKey(), MapString.getValue());
				}
			}
			q.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (TempTransaction == null)
				TempSessionFactory.closeSession();
		}
		return flag;
	}
}

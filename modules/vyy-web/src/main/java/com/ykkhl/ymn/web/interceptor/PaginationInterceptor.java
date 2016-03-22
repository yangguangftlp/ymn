/**
 * 
 */
package com.ykkhl.ymn.web.interceptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.DefaultParameterHandler;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.log4j.Logger;

import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.utils.ReflectUtil;

/**
 * 分页拦截器
 * 
 * @author tf
 * 
 */
@Intercepts({ @Signature(method = "prepare", type = StatementHandler.class, args = { Connection.class }) })
public class PaginationInterceptor implements Interceptor {

	private static final Logger LOGGER = Logger.getLogger(PaginationInterceptor.class);
	private static final String MYSQL_TYPE = "mysql";
	private static final String ORACLE_TYPE = "oracle";
	/**
	 * 数据库类型，不同的数据库有不同的分页方法
	 */
	private String databaseType;

	/**
	 * 拦截后要执行的方法
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
		BoundSql boundSql = handler.getBoundSql();
		// 拿到当前绑定Sql的参数对象，就是我们在调用对应的Mapper映射语句时所传入的参数对象
		Object obj = handler.getParameterHandler().getParameterObject();
		// 这里我们简单的通过传入的是Page对象就认定它是需要进行分页操作的。
		if (obj instanceof SqlQueryParameter) {
			SqlQueryParameter sqlQueryParameter = (SqlQueryParameter) obj;

			if (sqlQueryParameter.isPage() && sqlQueryParameter.getPageIndex() > 0
					&& sqlQueryParameter.getPageSize() > 0) {
				// 拦截到的prepare方法参数是一个Connection对象
				// 通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
				StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
				MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate,
						"mappedStatement");
				// 拦截到的prepare方法参数是一个Connection对象
				Connection connection = (Connection) invocation.getArgs()[0];
				// 获取当前要执行的Sql语句，也就是我们直接在Mapper映射语句中写的Sql语句
				// 给当前的sqlQueryParameter参数对象设置总记录数
				setTotalRecord(sqlQueryParameter, mappedStatement, connection);
				// 获取当前要执行的Sql语句，也就是我们直接在Mapper映射语句中写的Sql语句
				String sql = boundSql.getSql();
				// 获取分页Sql语句
				String pageSql = getPageSql(sqlQueryParameter, sql);
				// 利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
				ReflectUtil.setFieldValue(boundSql, "sql", pageSql);
			}
		}
		LOGGER.debug("the sql is :" + boundSql.getSql() + ",parameter is " + obj);
		return invocation.proceed();
	}

	/**
	 * 拦截器对应的封装原始对象的方法
	 */
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	/**
	 * 设置注册拦截器时设定的属性
	 */
	@Override
	public void setProperties(Properties properties) {
		this.databaseType = properties.getProperty("databaseType");
	}

	/**
	 * 根据page对象获取对应的分页查询Sql语句，这里只做了两种数据库类型，Mysql和Oracle 其它的数据库都 没有进行分页
	 * 
	 * @param page
	 *            分页对象
	 * @param sql
	 *            原sql语句
	 * @return
	 */
	private String getPageSql(SqlQueryParameter page, String sql) {
		StringBuffer sqlBuffer = new StringBuffer(sql);
		if (MYSQL_TYPE.equalsIgnoreCase(databaseType)) {
			return getMysqlPageSql(page, sqlBuffer);
		} else if (ORACLE_TYPE.equalsIgnoreCase(databaseType)) {
			return getOraclePageSql(page, sqlBuffer);
		}
		return sqlBuffer.toString();
	}

	/**
	 * 给当前的参数对象page设置总记录数
	 * 
	 * @param page
	 *            Mapper映射语句对应的参数对象
	 * @param mappedStatement
	 *            Mapper映射语句
	 * @param connection
	 *            当前的数据库连接
	 */
	private void setTotalRecord(SqlQueryParameter sqlQueryParameter, MappedStatement mappedStatement,
			Connection connection) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {

			// ReflectUtil.setFieldValue(boundSql, "sql", pageSql);
			BoundSql boundSql = mappedStatement.getBoundSql(sqlQueryParameter);
			String sql = boundSql.getSql();
			String countSql = this.getCountSql(sql);
			if (null == countSql) {
				return;
			}
			List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
			BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings,
					sqlQueryParameter);
			ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, sqlQueryParameter,
					countBoundSql);
			pstmt = connection.prepareStatement(countSql);
			parameterHandler.setParameters(pstmt);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				sqlQueryParameter.setTotalRecord(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据原Sql语句获取对应的查询总记录数的Sql语句
	 * 
	 * @param sql
	 * @return
	 */
	private String getCountSql(String sql) {
		StringBuffer sqlBuffer = new StringBuffer(sql);
		int index = (index = sqlBuffer.indexOf("from")) > 0 ? index : sqlBuffer.indexOf("FROM");
		if (index != -1) {
			return "select count(1) " + sqlBuffer.substring(index);
		}
		return null;
	}

	/**
	 * 获取Mysql数据库的分页查询语句
	 * 
	 * @param page
	 *            分页对象
	 * @param sqlBuffer
	 *            包含原sql语句的StringBuffer对象
	 * @return Mysql数据库分页语句
	 */
	private String getMysqlPageSql(SqlQueryParameter page, StringBuffer sqlBuffer) {
		// 计算第一条记录的位置，Mysql中记录的位置是从0开始的。
		int offset = (page.getPageIndex() - 1) * page.getPageSize();
		sqlBuffer.append(" limit ").append(offset).append(",").append(page.getPageSize());
		return sqlBuffer.toString();
	}

	/**
	 * 获取Oracle数据库的分页查询语句
	 * 
	 * @param page
	 *            分页对象
	 * @param sqlBuffer
	 *            包含原sql语句的StringBuffer对象
	 * @return Oracle数据库的分页查询语句
	 */
	private String getOraclePageSql(SqlQueryParameter page, StringBuffer sqlBuffer) {
		// 计算第一条记录的位置，Oracle分页是通过rownum进行的，而rownum是从1开始的
		int offset = (page.getPageIndex()) * page.getPageSize() + 1;
		sqlBuffer.insert(0, "select u.*, rownum r from (").append(") u where rownum < ")
				.append(offset + page.getPageSize());
		sqlBuffer.insert(0, "select * from (").append(") where r >= ").append(offset);
		return sqlBuffer.toString();
	}
}

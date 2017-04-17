package com.alienlab.db;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alienlab.common.TypeUtils;
import com.alienlab.utils.PropertyConfig;

public class DbPoolConnection {
	private static  Logger logger = Logger.getLogger(DbPoolConnection.class);
	//连接�??
	private  static  DbPoolConnection databasePool = null;
	//数据�??
	private static  DruidDataSource dds = null;

	static {
		Properties properties = new PropertyConfig("app.properties").getProperties();
		try{
			dds = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
			DruidPooledConnection conn=dds.getConnection();
			if(!TypeUtils.isEmpty(conn)){
				logger.info("======================数据库连接池创建成功=====================");
				conn.close();
			}
		} catch (Exception e){
			e.printStackTrace();
			logger.error("DruidDataSource Error 连接池创建失败！",e);
		}
	}

	private  DbPoolConnection(){}
	public static synchronized DbPoolConnection getInstance(){
		if (null == databasePool){
			databasePool = new DbPoolConnection();
		}
		return databasePool;
	}

	public DruidDataSource getDataSource() throws SQLException {
		return dds;
    }
			 
    public DruidPooledConnection getConnection() throws SQLException {
	     return dds.getConnection();
    }

	public static void main(String [] args){
		try{
			DAO dao=new DAO();
			List a=dao.getDataSet("select 1 from sys_apps");
			System.out.println(a.toString());
		}catch(Exception ex){
			ex.printStackTrace();

		}

	}
	
	
}

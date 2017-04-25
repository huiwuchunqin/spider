package com.baizhitong.spider;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class DBUtils {
	private static final String schemal = "dbo";

	public  static  String preTableName="bzt_century_";
	
	private static boolean inited = false;
	private static Connection conn;
	public static void start(){
		if(!inited)
		{
			conn = getConnection();
			inited = true;
		}
	}
	
	public static  Connection getConn(){
		return conn;
	}
	public static void close(){
		if(conn != null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			conn = null;
			inited = false;
		}
	}
	
	private static Connection getConnection() {
		try {
			Class clazz = Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String url = "jdbc:sqlserver://192.168.0.22:1433; DatabaseName=maindb";
			String username = "maindb";
			String password = "bzt@2016";
			Connection con = DriverManager.getConnection(url, username, password);
			return con;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * 创建表
	 * 
	 * @param tableName
	 * @param columns
	 * @param defaultValue
	 */
	public static void createTable(String tableName, Map<String, Object> columns) {
		try {
			Statement stmt = conn.createStatement();
			
			boolean exists = false;
			try{
				//判断当前表是否已经存在
				stmt.executeQuery("select * from [" + schemal + "].["+preTableName + tableName + "] where 1 = 2 ");
				 exists = true;
			}catch(Exception e){
			}finally{
				stmt.close();
			}
			//如果已经存在表，则不创建！！！
			if(exists)
			{
				return;
			}
			
			String creatTable = "CREATE TABLE [" + schemal + "].["+preTableName + tableName + "] (";

			for (Entry<String, Object> entry : columns.entrySet()) {
				String column = entry.getKey();
				String columnType = "varchar(255)";
				creatTable += "[" + column + "]" + columnType + "NULL ,";
			}

			// 自动截断最后一个，
			creatTable = creatTable.substring(0, creatTable.length() - 1);

			creatTable += ")";

			stmt = conn.createStatement();
			stmt.executeUpdate(creatTable);
			stmt.close();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void insert(String table, Map<String, Object> values) {
		insert(table,values,false);
	}
	
	/**
	 * 将数据插入到数据库中
	 * 
	 * @param table
	 * @param values
	 */
	public static void insert(String table, Map<String, Object> values,boolean checkExist) {
		if(checkExist)
		{
			createTable(table,values);
		}
		
		// INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
		String columns = "";
		String columnValues = "";
		Object[] columnValuesArray = new Object[values.size()];

		int i = 0;
		for (Entry<String, Object> entry : values.entrySet()) {
			String key = entry.getKey();
			Object object = entry.getValue();

			columns += key + ",";
			columnValues += "?,";
			columnValuesArray[i++] = object;
		}

		String insertSql = "INSERT INTO [" + schemal + "].["+preTableName + table + "] ("
				+ columns.substring(0, columns.length() - 1) + ") VALUES ("
				+ columnValues.substring(0, columnValues.length() - 1) + ")";

		try {
			PreparedStatement pstmt = conn.prepareStatement(insertSql);
			i = 1;
			for (Object o : columnValuesArray) {
				pstmt.setString(i++, (String)o);
			}
			
			pstmt.executeUpdate();
			pstmt.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public static void exec(){
		
	}
	
	/**
	 * 删除数据
	 * 
	 * @param table
	 * @param values
	 */
	public static void delete(String table, Map<String, Object> values) {
		//DELETE FROM Person WHERE LastName = 'Wilson' 
		String columnSQL = "1=1";
		Object[] columnValuesArray = new Object[values.size()];

		int i = 0;
		if(values!=null){
			for (Entry<String, Object> entry : values.entrySet()) {
				String key = entry.getKey();
				Object object = entry.getValue();

				columnSQL +="and "+ key + "=? ";
				columnValuesArray[i++] = object;
			}
		}
		String deleteSql = "DELETE FROM [" + schemal + "].["+preTableName + table + "]  where " + columnSQL.substring(0,columnSQL.length()) ;

		try {
			PreparedStatement pstmt = conn.prepareStatement(deleteSql);

			i = 1;
			if(values!=null){
				for (Object o : columnValuesArray) {
					pstmt.setString(i++, (String)o);
				}	
			}
			System.err.println(deleteSql);

			pstmt.executeUpdate();
			pstmt.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void update(){
		
	}
	
	public static List<Map> select(String sql,Object... values){

		List<Map> res=new ArrayList<Map>();
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);

			int i = 1;
			for (Object o : values) {
				pstmt.setString(i++, (String)o);
			}
			System.err.println(sql);

			ResultSet rs =pstmt.executeQuery();
			while(rs.next()){
				Map<String,Object> row = new HashMap<String,Object>();
				int columnCount = rs.getMetaData().getColumnCount();
				for ( i= 0; i < columnCount; i++) {
					row.put(rs.getMetaData().getColumnLabel(i+1).toString(), rs.getObject(i+1));				
				}
				res.add(row);
			}
			pstmt.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		return res;
	}
	
	
	
	@Test
	public void createTable() {
		Map<String, Object> columnsMap = new HashMap<String, Object>();
		columnsMap.put("ttt", "ccc");
		createTable("xxxxxxxxx", columnsMap);
	}

	@Test
	public void insert() {
		Map<String, Object> columnsMap = new HashMap<String, Object>();
		columnsMap.put("ttt", "ccc");

		insert("xxxxxxxxx", columnsMap);
	}

	@Test
	public void delete(){
		Map<String, Object> columnsMap = new HashMap<String, Object>();
		columnsMap.put("ttt", "ccc");

		delete("xxxxxxxxx", columnsMap);
	}
	
	public void execute(Connection con, String sql) {
		try {
			Statement stmt = con.createStatement();
			PreparedStatement pstmt = con.prepareStatement(sql);
			CallableStatement cstmt = con.prepareCall("{CALL demoSp(? , ?)}");
			ResultSet rs = stmt.executeQuery("SELECT * FROM ...");
			int rows = stmt.executeUpdate("INSERT INTO ...");
			// boolean flag = stmt.execute(String sql) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static void dropTable(String table) {
		// TODO Auto-generated method stub
		String dropSql="DROP TABLE [" + schemal + "].["+preTableName + table + "]";
		try {
			PreparedStatement pstmt = conn.prepareStatement(dropSql);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			
		}
	}
}

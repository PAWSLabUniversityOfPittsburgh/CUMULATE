package edu.pitt.sis.paws.cbum;

import java.sql.*;

public abstract class UIDManager
{
	protected Connection conn = null;
	protected boolean autocommit = false;
	protected static final String RESULT_OK = "OK";
	
	public UIDManager(Connection _conn) throws SQLException
	{
		conn = _conn;
		if(conn == null || conn.isClosed() || conn.isReadOnly())
		{
			SQLException sqle = new SQLException("The connection is not initialized correctly");
			throw sqle;
		}
		// save autocommit status
		autocommit = conn.getAutoCommit();
	}
	
	public abstract int userExists(String login) throws SQLException;
//	{
//		boolean result = true;
//		return result;
//	}
	
	public int groupExists(String group) throws SQLException
	{
		int result = 1;
		return result;
	}
	
	public abstract String addUser(String login, String fname,
			String lname, String md5password, String email, String organization,
			String city, String country, String how_ended_up_here, String group) throws SQLException;
//	{
//		String result = RESULT_OK;
//		conn.setAutoCommit(true);
//		return result;
//	}
	
	public void addUserCommit() throws SQLException { conn.commit(); conn.setAutoCommit(autocommit); }
	
	public void addUserRollBack() throws SQLException { conn.rollback(); conn.setAutoCommit(autocommit); }
	
	public abstract boolean authenticateUser(String login, String md5password, String group) throws SQLException;
//	{
//		boolean result = true;
//		return result;
//	}
	
	public void destroy() throws SQLException
	{
		if(conn == null && conn.isClosed())
		{
			conn.close();
			conn = null;
		}
	}
	
}

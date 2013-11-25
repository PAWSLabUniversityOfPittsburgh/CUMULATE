package edu.pitt.sis.paws.cbum2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class delete_SQLManager
{
	private DataSource ds;
	
	public delete_SQLManager(String context)
	{
		try
		{
			Context initCtx = new InitialContext();
			Object obj =  initCtx.lookup(context);
			ds = (DataSource)obj;
			
			if (ds == null)
				throw new Exception("CBUM: Failure to Create Datasource");
		}
		catch(SQLException ex)
		{
			while (ex != null)
			{
				System.out.println ("SQL Exception: " + ex.getMessage ());
				ex = ex.getNextException ();
			}
		}
		catch(NamingException ex) { ex.printStackTrace(); }
		catch(Exception ex) { ex.printStackTrace(); }
	}// end of -- SQLManager
	
	public Connection getConnection() throws Exception
	{
		if(ds == null)
			throw new Exception("KT: Failure to Create Datasource");
		return ds.getConnection();
	}

	public static ResultSet executeStatement(PreparedStatement statement)
			throws Exception
	{
		return statement.executeQuery();
	}
	
	public static void executeUpdate(Connection conn, String query)
			throws Exception
	{
		PreparedStatement statement = conn.prepareStatement(query);
		statement.executeUpdate();
		statement.close();
		statement = null;
	}
	
	public static void freeConnection(Connection conn)
	{
		try { if (conn != null) conn.close(); }
		catch (Exception e) { e.printStackTrace(System.err); }
	}
	

}

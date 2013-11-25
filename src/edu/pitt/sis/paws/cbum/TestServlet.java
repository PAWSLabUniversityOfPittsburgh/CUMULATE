package edu.pitt.sis.paws.cbum;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.pitt.sis.paws.core.utils.SQLManager;

/**
 * Servlet implementation class for Servlet: TestServlet
 *
 */
public class TestServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public TestServlet() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		SQLManager sql_manager = new SQLManager("java:comp/env/jdbc/main");
//		String sql1_c = "SELECT AppID, UserId, GroupID, AVG(Result) FROM ent_user_activity11 GROUP BY AppID, UserId, GroupID;";
//		String sql2_c = "SELECT AppID, UserId, GroupID, AVG(Result) FROM ent_user_activity22 GROUP BY AppID, UserId, GroupID;";
//		String sql1_s = "SELECT * FROM ent_user_activity11 WHERE DateNTime='2007-02-12 13:26:04.989';";
//		String sql2_s = "SELECT * FROM ent_user_activity22 WHERE DateNTime='2007-02-12 13:26:04.989';";
//		String sql1_u = "UPDATE ent_user_activity11 SET Result=-1 WHERE DateNTime='2007-02-12 13:26:04.989';";
//		String sql2_u = "UPDATE ent_user_activity22 SET Result=-1 WHERE DateNTime='2007-02-12 13:26:04.989';";
	
		String sql1 = "SELECT * FROM ent_user_activity11 WHERE UserID=200 AND AppID=3 AND ActivityID>1000;";
		String sql2 = "SELECT * FROM ent_user_activity22 WHERE UserID=200 AND AppID=3 AND ActivityID>1000;";
		String sql3 = "REPLACE INTO test_replace_stmt (UserID, GRoupID, ConceptID, Value) VALUES (1,1,1,.5), (1,1,2,.7), (1,1,3,.1), (2,1,1,.4), (2,1,2,.8);";
		
		int cycles = 1000;
		
		long st_1 = 0;
		long st_2 = 0;
		long st_3 = 0;
		long fn_1 = 0;
		long fn_2 = 0;
		long fn_3 = 0;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try
		{
			conn = sql_manager.getConnection();
			
			// InnoDB vs MEMORY
//			st_1 = System.nanoTime();
//			for(int i=0; i<cycles; i++)
//			{
//				stmt = conn.prepareStatement(sql1);
//
//				rs = stmt.executeQuery();
//				rs.close();
//				rs = null;
////				stmt.executeUpdate();
//	
//				stmt.close();
//				stmt = null;
//			}
//			fn_1 = System.nanoTime();
//
//			st_2 = System.nanoTime();
//			for(int i=0; i<cycles; i++)
//			{
//				stmt = conn.prepareStatement(sql2);
//
//				rs = stmt.executeQuery();
//				rs.close();
//				rs = null;
////				stmt.executeUpdate();
//
//				stmt.close();
//				stmt = null;
//			}
//			fn_2 = System.nanoTime();
			
			st_3 = System.nanoTime();
			for(int i=0; i<cycles; i++)
			{
				stmt = conn.prepareStatement(sql3);

//				rs = stmt.executeQuery();
//				rs.close();
//				rs = null;
				stmt.executeUpdate();

				stmt.close();
				stmt = null;
			}
			fn_3 = System.nanoTime();
			
			conn.close();
			conn = null;
		}
		catch(SQLException sqle) { sqle.printStackTrace(System.out); }
		
		System.out.println("\n---");
//		System.out.println("1: " + (float)(fn_1-st_1)/(cycles * 1000000) + "ms  2: " + (float)(fn_2-st_2)/(cycles * 1000000) + "ms  " + cycles);
		System.out.println("3: " + (float)(fn_3-st_3)/(cycles * 1000000) + "ms   " + cycles);
	}  	
	
}
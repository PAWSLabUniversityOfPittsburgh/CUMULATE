/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/**
 * 
 */
package edu.pitt.sis.paws.cbum;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that performes a user login lookup.
 * 
 * @author	Michael V. Yudelson
 * @since	1.5
 * @version %I%, %G%
 */
public class um_usrLookup extends HttpServlet 
{
	static final long serialVersionUID = -2L;
	
	// Constants
	//	Request parameters
	public static final String REQ_USER = "usr";
	
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}

	public void service(HttpServletRequest request, 
		HttpServletResponse response) throws ServletException, UnsupportedEncodingException, IOException
	{
//System.out.println("[CBUM] um received a request"); /// DEBUG
		
		// Retrieve all possible parameters
		String req_user = request.getParameter(REQ_USER);
//System.out.println("~~~ [CBUM] um_cache2 svc=" + req_svc);	
		
		// decode parameters
		req_user = (req_user!=null)?URLDecoder.decode(req_user, "UTF-8"):"";//req_user;

		Connection conn = null;
		PreparedStatement stmt = null;
		String qry;
		ResultSet rs = null;
		
		String result = "ERROR";

		try
		{
			conn = ServerDaemon.getConnection();
			
			// Submit acticvity into the database
			qry = "SELECT COUNT(*) AS Number FROM ent_user WHERE Login='"+req_user+"';"; 
			stmt = conn.prepareStatement(qry);
			rs = stmt.executeQuery();
			
			int count = rs.getInt("Number");
			result = (count==0)?"NOTFOUND":"FOUND";
			
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			conn.close();
			conn = null;
			
		}
		catch(Exception e) { e.printStackTrace(System.out); }
		finally
		{
			if (rs != null) 
			{
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (stmt != null) 
			{
				try { stmt.close(); } catch (SQLException e) { ; }
				stmt = null;
			}
			if (conn != null)
			{
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
		
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.println(result);
		out.close();
	}

}

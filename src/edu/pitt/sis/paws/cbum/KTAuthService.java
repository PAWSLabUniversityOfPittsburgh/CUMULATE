package edu.pitt.sis.paws.cbum;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class KTAuthService extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet{
	
	static final long serialVersionUID = -2L;
	
	private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
	private DataSource dsKT;		

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		
		// create connection pools
		try
		{
			Context initCtx = new InitialContext();
			// KT
			Object obj =  initCtx.lookup("java:comp/env/jdbc/portal_pwd");
			dsKT = (DataSource)obj;
			if (dsKT == null)
				throw new Exception("Remote PWD Manager: Failure to Create KT Datasource");
				
			// UM
			obj =  initCtx.lookup("java:comp/env/jdbc/cbum_pwd");
				
			obj = null;
			initCtx.close();
			initCtx = null;
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
		
	}
	
	public void destroy()
	{
		if(dsKT != null) dsKT = null;
	}
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{	
			
			String req_action = request.getParameter("action");
			//String req_group = request.getParameter("grp");
			String req_user = request.getParameter("usr");
			String req_password_md5 = request.getParameter("md5");
			String req_fwd_to = request.getParameter("fwd");
				
			String res_result = "0,Action parameter misspecified";
		
			KTUIDManager ktUIDM = null; 
			
			JSONObject jsonOutput = new JSONObject();
			
			if(req_action != null && req_action.equalsIgnoreCase("login"))
			{// login
				
		//System.out.println("[cbum] RemoteLogin/Create - Logging in");					
				
				res_result = "";
				if(req_user==null || req_user.equalsIgnoreCase(""))
					res_result += ",Login not specified";
				/*if(req_group==null || req_group.equalsIgnoreCase(""))
					res_result += ",User group not specified";
				if(req_password==null || req_password.equalsIgnoreCase(""))
					res_result += ",Password not specified";*/
				if(req_password_md5==null || req_password_md5.equalsIgnoreCase(""))
					res_result += ",MD5(password) not specified";
				
				if(res_result.length() == 0)
				{// proceed with login
					Connection connKT = null;
							
					try
					{
						connKT = dsKT.getConnection();
						
						ktUIDM = new KTUIDManager(connKT);
						
						// Check for existing user and group
						
						boolean login_KT = ktUIDM.authenticateUser(req_user, req_password_md5);
		
						if(login_KT)			
						{
							HttpSession session = request.getSession();
							session.invalidate();
							session = request.getSession(true);
							//res_result = session.getId().substring(session.getId().length()-5);
						}
						/*else
							res_result = "0, Login exists. KT: (" + login_KT + ")";*/
						
						try {
							jsonOutput.put("result",login_KT);
						} catch (JSONException ex) {
							// TODO Auto-generated catch block
							System.out.println ("JSON Exception: " + ex.getMessage ());
							String exceptionMessage = ex.getMessage();
							
							//returns an error back to the client
							response.setContentType(CONTENT_TYPE);
							PrintWriter out = response.getWriter();
							out.println(exceptionMessage);
							out.close();
						}
					}
					catch(SQLException ex)
					{
						while (ex != null)
						{
							System.out.println ("SQL Exception: " + ex.getMessage ());
							String exceptionMessage = ex.getMessage();
							ex = ex.getNextException ();
							
							//returns an error back to the client
							response.setContentType(CONTENT_TYPE);
							PrintWriter out = response.getWriter();
							out.println(exceptionMessage);
							out.close();
						}
					}
		//			finally
		//			{
		//				try
		//				{
		//					if(connKT != null)
		//					{
		//						connKT.close();
		//						connKT = null;
		//					}
		//					if(connCBUM != null)
		//					{
		//						connCBUM.close();
		//						connCBUM = null;
		//					}
		//					if(connWADEIN != null)
		//					{
		//						connWADEIN.close();
		//						connWADEIN = null;
		//					}
		//				}
		//				catch(Exception e) { e.printStackTrace(System.err); };
		//			}
					
				}// end -- proceed with login
				else
					res_result = "0" + res_result;
					
			}// end -- login
			else
			
			try
			{
				if(ktUIDM != null) ktUIDM.destroy();
			}
			catch(Exception e) { e.printStackTrace(System.err); };
			
			res_result = jsonOutput.toString();
			
			if(req_fwd_to == null || req_fwd_to.length() == 0)
			{// if no forward
				response.setContentType(CONTENT_TYPE);
				PrintWriter out = response.getWriter();
				out.println(res_result);
				out.close();
			}// end of -- if no forward
			else
			{// if forward
		//System.out.println("req_fwd_to=" + req_fwd_to);
		//System.out.println("res_result=" + res_result);
				response.sendRedirect(req_fwd_to +"?msg=" + res_result);
				
		//		RequestDispatcher disp;
		//		disp = request.getRequestDispatcher(req_fwd_to +"?msg=" + res_result);
		//		disp.forward(request, response);
			}// end of -- if forward
		}  	

}

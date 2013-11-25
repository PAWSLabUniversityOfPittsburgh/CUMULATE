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


/**
 * Servlet implementation class for Servlet: UIDManagerServlet
 *
 */
public class UIDManagerServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	static final long serialVersionUID = -2L;
	
	private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
	private DataSource dsKT;	
	private DataSource dsCBUM;	
//	private DataSource dsWADEIn;	

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
			dsCBUM = (DataSource)obj;
			if (dsCBUM == null)
				throw new Exception("Remote PWD Manager: Failure to Create CBUM Datasource");
				
			// WADEIn
//			obj =  initCtx.lookup("java:comp/env/jdbc/wadein_pwd");
//			dsWADEIn = (DataSource)obj;
//			if (dsWADEIn == null)
//				throw new Exception("Remote PWD Manager: Failure to Create WADEIn Datasource");
				
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
		if(dsCBUM != null) dsCBUM = null;
//		if(dsWADEIn != null) dsWADEIn = null;
	}
 
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String req_action = request.getParameter("action");
		String req_group = request.getParameter("grp");
		String req_user = request.getParameter("usr");
		String req_password = request.getParameter("pwd");
		String req_password_md5 = request.getParameter("md5");
		String req_first_name = request.getParameter("fname");
		String req_last_name = request.getParameter("lname");
		String req_email = request.getParameter("email");
		String req_organization = request.getParameter("org");
		String req_city = request.getParameter("city");
		String req_country = request.getParameter("country");
		String req_how = request.getParameter("how");
		String req_fwd_to = request.getParameter("fwd");
		
//System.err.println("[cbum] RemoteLogin/Create - received parameters");		
//System.err.println("	action=" + req_action);		
//System.err.println("	grp=" + req_group);	
//System.err.println("	usr=" + req_user);		
//System.err.println("	pwd=" + req_password);		
//System.err.println("	md5=" + req_password_md5);	
//System.err.println("	fname=" + req_first_name);		
//System.err.println("	lname=" + req_last_name);		
//System.out.println("	email=" + req_email);		
//System.out.println("	org=" + req_organization);	
//System.out.println("	city=" + req_city);		
//System.out.println("	country=" + req_country);		
//System.out.println("	how=" + req_how);	
			
		String res_result = "0,Action parameter misspecified";

		UIDManager ktUIDM = null, cbumUIDM = null;//,  wadeinUIDM = null;; 
		
		if(req_action != null && req_action.equalsIgnoreCase("login"))
		{// login
			
//System.out.println("[cbum] RemoteLogin/Create - Logging in");					
			
			res_result = "";
			if(req_user==null || req_user.equalsIgnoreCase(""))
				res_result += ",Login not specified";
			if(req_group==null || req_group.equalsIgnoreCase(""))
				res_result += ",User group not specified";
			if(req_password==null || req_password.equalsIgnoreCase(""))
				res_result += ",Password not specified";
			if(req_password_md5==null || req_password_md5.equalsIgnoreCase(""))
				res_result += ",MD5(password) not specified";
			
			if(res_result.length() == 0)
			{// proceed with login
				Connection connKT = null;
				Connection connCBUM = null;
//				Connection connWADEIN = null;
						
				try
				{
					connKT = dsKT.getConnection();
					connCBUM = dsCBUM.getConnection();
//					connWADEIN = dsWADEIn.getConnection();
					
					ktUIDM = new KTUIDManager(connKT);
					cbumUIDM = new CBUMUIDManager(connCBUM);
//					wadeinUIDM = new WADEInUIDManager(connWADEIN);
					
					// Check for existing user and group
					
					
					boolean login_KT = ktUIDM.authenticateUser(req_user, req_password_md5, req_group);
					boolean login_CBUM = cbumUIDM.authenticateUser(req_user, req_password_md5, req_group);
					boolean login_WADEIn = true; //wadeinUIDM.authenticateUser(req_user, req_password_md5, req_group);
					

					if(login_KT && login_CBUM && login_WADEIn)			
					{
						HttpSession session = request.getSession();
						session.invalidate();
						session = request.getSession(true);
						
						res_result = session.getId().substring(session.getId().length()-5);
					}
					else
						res_result = "0, Login exists. KT, CBUM, WADEIn: (" + login_KT + "," + login_CBUM + "," + login_WADEIn + ")";
				}
				catch(SQLException ex)
				{
					while (ex != null)
					{
						System.out.println ("SQL Exception: " + ex.getMessage ());
						ex = ex.getNextException ();
					}
				}
//				finally
//				{
//					try
//					{
//						if(connKT != null)
//						{
//							connKT.close();
//							connKT = null;
//						}
//						if(connCBUM != null)
//						{
//							connCBUM.close();
//							connCBUM = null;
//						}
//						if(connWADEIN != null)
//						{
//							connWADEIN.close();
//							connWADEIN = null;
//						}
//					}
//					catch(Exception e) { e.printStackTrace(System.err); };
//				}
				
			}// end -- proceed with login
			else
				res_result = "0" + res_result;
				
		}// end -- login
		else
		if(req_action != null && req_action.equalsIgnoreCase("registration"))
		{// registration
			
System.out.println("[cbum] RemoteLogin/Create - Registration");	

			res_result = "";
			if(req_user==null || req_user.equalsIgnoreCase(""))
				res_result += ",Login not specified";
			if(req_group==null || req_group.equalsIgnoreCase(""))
				res_result += ",User group not specified";
			if(req_password==null || req_password.equalsIgnoreCase(""))
				res_result += ",Password not specified";
			if(req_password_md5==null || req_password_md5.equalsIgnoreCase(""))
				res_result += ",MD5(password) not specified";

			if(req_first_name == null || req_first_name.equals(""))
				req_first_name = "";
			if(req_last_name == null || req_last_name.equals(""))
				req_last_name = "";
			if(req_email == null || req_email.equals(""))
				req_email = "";
			if(req_organization == null || req_organization.equals(""))
				req_organization = "";
			if(req_city == null || req_city.equals(""))
				req_city = "";
			if(req_country == null || req_country.equals(""))
				req_country = "";
			if(req_how == null || req_how.equals(""))
				req_how = "";
			if(res_result.length() == 0)
			{// proceed with registration
				Connection connKT = null;
				Connection connCBUM = null;
				Connection connWADEIN = null;
				try
				{// try to register
					connKT = dsKT.getConnection();
					connCBUM = dsCBUM.getConnection();
//					connWADEIN = dsWADEIn.getConnection();
										
					ktUIDM = new KTUIDManager(connKT);
					cbumUIDM = new CBUMUIDManager(connCBUM);
//					wadeinUIDM = new WADEInUIDManager(connWADEIN);
					
					int group_id_KT = ktUIDM.groupExists(req_group);
					int group_id_CBUM = cbumUIDM.groupExists(req_group);

					boolean group_exists_KT = group_id_KT != -1;
					boolean group_exists_CBUM = group_id_CBUM != -1;

					boolean login_free_KT = ktUIDM.userExists(req_user) == -1;
					boolean login_free_CBUM = cbumUIDM.userExists(req_user) == -1;
					boolean login_free_WADEIN = true; //wadeinUIDM.userExists(req_user) == -1;
//System.out.println("User IDs KT, CBUM, WADEIn: " + ktUIDM.userExists(req_user) + ", " + cbumUIDM.userExists(req_user) + ", " + wadeinUIDM.userExists(req_user));
					
					if(group_exists_CBUM && group_exists_KT)
					{// group(s) exists
						
						if(login_free_KT && login_free_CBUM && login_free_WADEIN)
						{// login free
//System.out.println("CBUM restart");
							
							// Create user in the group
							/*String kt_result =*/ ktUIDM.addUser(req_user, req_first_name, req_last_name,
									req_password_md5, req_email, req_organization, req_city,
									req_country, req_how, req_group);
							
							/*String cbum_result =*/ cbumUIDM.addUser(req_user, req_first_name, req_last_name,
									req_password_md5, req_email, req_organization, req_city,
									req_country, req_how, req_group);

							// Insert in CBUM cache
							ServerDaemon sd = ServerDaemon.getInstance(getServletContext());
							if(!sd.addNewUser(cbumUIDM.userExists(req_user), req_user, req_group))
								throw new IOException("Remote PWD Manager: Failure to Create user in CBUM Cache");
							
//							String wadein_result = wadeinUIDM.addUser(req_user, req_first_name, req_last_name,
//									req_password_md5, req_email, req_organization, req_city,
//									req_country, req_how, req_group);
							

							ktUIDM.addUserCommit();
							cbumUIDM.addUserCommit();
//							wadeinUIDM.addUserCommit();

							HttpSession session = request.getSession();
							session.invalidate();
							session = request.getSession(true);
							
							res_result = session.getId().substring(session.getId().length()-5);
							
						}// end -- login free
						else
							res_result = "0|Login specified is taken| KT, CBUM, WADEIn have it (" + login_free_KT + "," + login_free_CBUM + "," + login_free_WADEIN + ")";
						
					}// end -- group(s) exists
					else
						res_result = "0|Group doesn't exist| KT, CBUM have it (" + group_exists_KT + "," + group_exists_CBUM + ")";
					
				}// end -- try to register
				catch(SQLException  ex)
				{// catch try to register
					while (ex != null)
					{
						ex.printStackTrace(System.err);
//						System.out.println ("SQL Exception: " + ex.getMessage ());
						ex = ex.getNextException ();
					}
					try
					{
						connKT.rollback();
						connCBUM.rollback();
						connWADEIN.rollback();
					}
					catch(Exception e) { e.printStackTrace(System.err); };
				}// end - catch try to register
//				finally
//				{// finally after try to register
//					try
//					{
//						if(connKT != null)
//						{
//							connKT.close();
//							connKT = null;
//						}
//						if(connCBUM != null)
//						{
//							connCBUM.close();
//							connCBUM = null;
//						}
//						if(connWADEIN != null)
//						{
//							connWADEIN.close();
//							connWADEIN = null;
//						}
//						
//					}
//					catch(Exception e) { e.printStackTrace(System.err); };
//				}// end -- finally after try to register
			}// end - proceed with registration
		}// end -- registration
		
		try
		{
			if(ktUIDM != null) ktUIDM.destroy();
			if(cbumUIDM != null) cbumUIDM.destroy();
//			if(wadeinUIDM != null) wadeinUIDM.destroy();
		}
		catch(Exception e) { e.printStackTrace(System.err); };
		
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
			
//			RequestDispatcher disp;
//			disp = request.getRequestDispatcher(req_fwd_to +"?msg=" + res_result);
//			disp.forward(request, response);
		}// end of -- if forward
	}  	
		
}
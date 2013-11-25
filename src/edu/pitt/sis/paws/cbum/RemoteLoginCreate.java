package edu.pitt.sis.paws.cbum;

import java.io.IOException;
import java.io.PrintWriter;

//import java.net.URLDecoder;

import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.sql.DataSource;

public class RemoteLoginCreate extends HttpServlet
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
				
//			// WADEIn
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

	public void doGet(HttpServletRequest request, 
			  HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, 
			  HttpServletResponse response) throws ServletException, IOException
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

		String res_result = "0,Action parameter misspecified";
		
		if(req_action != null && req_action.equalsIgnoreCase("login"))
		{// login
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
				Connection connWADEIN = null;
				PreparedStatement stmt = null;
				ResultSet rs = null;
				try
				{
					connKT = dsKT.getConnection();
					connCBUM = dsCBUM.getConnection();
//					connWADEIN = dsWADEIn.getConnection();
					
					boolean login_KT = false;
					boolean login_CBUM = false;
//					boolean login_WADEIT = false;
					
//					String req_pass_decoded = URLDecoder.decode(req_password, "US-ASCII");
					
					// KT
					String qry = "SELECT * FROM ent_user u LEFT JOIN rel_user_user uu ON(u.UserID=uu.ChildUserID) " + 
						"LEFT JOIN ent_user u2 ON(u2.UserID=uu.ParentUserID) " +
						"WHERE u.Login='" + req_user + "' AND u.Pass='" + req_password_md5 + "' AND u2.Login='" + req_group + "' AND u.Sync=1 AND u2.Sync=1;";
					
					stmt = connKT.prepareStatement(qry);
					rs = stmt.executeQuery();
					while(rs.next())
					{
						login_KT = true;
						break;
					}
					
					// CBUM
					qry = "SELECT * FROM ent_user u LEFT JOIN rel_user_user uu ON(u.UserID=uu.UserID) " + 
						"LEFT JOIN ent_user u2 ON(u2.UserID=uu.GroupID) " +
						"WHERE u.Login='" + req_user + "' AND u.Pass='" + req_password_md5 + "' AND u2.Login='" + req_group + "' AND u.Sync=1 AND u2.Sync=1;";
					
					stmt = connCBUM.prepareStatement(qry);
					rs = stmt.executeQuery();
					while(rs.next())
					{
						login_CBUM = true;
						break;
					}
					
					//WADEIN
					
//					qry = "SELECT * FROM wadein.users_c WHERE login='"  + req_user + "' AND portal_user=1;";
//					stmt = connWADEIN.prepareStatement(qry);
//					rs = stmt.executeQuery();
//					while(rs.next())
//					{
//						login_WADEIT = true;
//						break;
//					}

					if(login_KT && login_CBUM/* && login_WADEIT*/)			
					{
						HttpSession session = request.getSession();
						session.invalidate();
						session = request.getSession(true);
						
						res_result = session.getId().substring(session.getId().length()-5);
					}
					else
						res_result = "0, Login failed. KT, CBUM, WADEIn: (" + login_KT + "," + login_CBUM + /*"," +login_WADEIT + */ ")";
				}
				catch(SQLException ex)
				{
					while (ex != null)
					{
						System.out.println ("SQL Exception: " + ex.getMessage ());
						ex = ex.getNextException ();
					}
				}
				finally
				{
					try
					{
						if(rs != null)
						{
							rs.close();
							rs = null;
						}
						if(stmt != null)
						{
							stmt.close();
							stmt = null;
						}
						if(connKT != null)
						{
							connKT.close();
							connKT = null;
						}
						if(connCBUM != null)
						{
							connCBUM.close();
							connCBUM = null;
						}
						if(connWADEIN != null)
						{
							connWADEIN.close();
							connWADEIN = null;
						}
					}
					catch(Exception e) { e.printStackTrace(System.err); };
				}
				
				// check in ;
			}// end -- proceed with login
			else
				res_result = "0" + res_result;
				
		}// end -- login
		else
		if(req_action != null && req_action.equalsIgnoreCase("registration"))
		{// registration
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
				req_first_name = "<unspecified>";
			if(req_last_name == null || req_last_name.equals(""))
				req_last_name = "<unspecified>";
			if(req_email == null || req_email.equals(""))
				req_email = "<unspecified>";
			if(req_organization == null || req_organization.equals(""))
				req_organization = "<unspecified>";
			if(req_city == null || req_city.equals(""))
				req_city = "<unspecified>";
			if(req_country == null || req_country.equals(""))
				req_country = "<unspecified>";
			if(req_how == null || req_how.equals(""))
				req_how = "<unspecified>";
			if(res_result.length() == 0)
			{// proceed with registration
				Connection connKT = null;
				Connection connCBUM = null;
				Connection connWADEIN = null;
				PreparedStatement stmt = null;
				ResultSet rs = null;
				try
				{// try to register
					connKT = dsKT.getConnection();
					connCBUM = dsCBUM.getConnection();
//					connWADEIN = dsWADEIn.getConnection();
					
					boolean group_exists_KT = false;
					boolean group_exists_CBUM = false;
					int group_id_KT = 0;
					int group_id_CBUM = 0;

					boolean login_free_KT = true;
					boolean login_free_CBUM = true;
//					boolean login_free_WADEIN = true;
					
					// check group existence
					String qry = "SELECT u.UserID FROM ent_user u WHERE u.Login='" + req_group + "' AND u.Sync=1 AND u.IsGroup=1;";
					
					stmt = connKT.prepareStatement(qry);
					rs = stmt.executeQuery();
					while(rs.next())
					{
						group_exists_KT = true;
						group_id_KT = rs.getInt("UserID");
					}
					
					stmt = connCBUM.prepareStatement(qry);
					rs = stmt.executeQuery();
					while(rs.next())
					{
						group_exists_CBUM = true;
						group_id_CBUM = rs.getInt("UserID");
					}
					
					if(group_exists_CBUM && group_exists_KT)
					{// group(s) exists
						qry = "SELECT * FROM ent_user u WHERE u.Login='" + req_user + "' AND u.IsGroup=0;";

						stmt = connKT.prepareStatement(qry);
						rs = stmt.executeQuery();
						while(rs.next())
						{
							login_free_KT = false;
							break;
						}
						
						stmt = connCBUM.prepareStatement(qry);
						rs = stmt.executeQuery();
						while(rs.next())
						{
							login_free_CBUM = false;
							break;
						}
						
//						qry = "SELECT * FROM wadein.users_c WHERE login='" + req_user + "';";
//
//						stmt = connWADEIN.prepareStatement(qry);
//						rs = stmt.executeQuery();
//						while(rs.next())
//						{
//							login_free_WADEIN = false;
//							break;
//						}
						
						if(login_free_KT && login_free_CBUM /*&& login_free_WADEIN*/)
						{// login free
							ServerDaemon sd = ServerDaemon.getInstance(getServletContext());
//System.out.println("CBUM restart");
							// Create user in the group
							int user_id_KT = 0;
							int user_id_CBUM = 0;
							int user_id_WADEIN = 0;
							
							//KT
							connKT.setAutoCommit(false);
							// insert user
							qry = "INSERT INTO ent_user (Login, Name, Pass, IsGroup, Sync, EMail, Organization, City, Country, How) VALUES(" +
								"'" + req_user + "', '" + req_last_name + ", " + req_first_name + "', '" + req_password_md5 + "', 0,1, '" + 
								req_email + "', '" + req_organization + "', '" + req_city + "', '" + req_country + "', '" +  req_how + "');";
							stmt = connKT.prepareStatement(qry);
							stmt.executeUpdate();
							
							// get user id
							qry = "SELECT MAX(LAST_INSERT_ID(UserID)) AS LastID FROM ent_user WHERE Login='" + req_user + "';";
							stmt = connKT.prepareStatement(qry);
							rs = stmt.executeQuery();
							while(rs.next())
							{
								user_id_KT = rs.getInt("LastID");
							}
							
							// insert into group
							qry = "INSERT INTO rel_user_user (ParentUserID, ChildUserID) VALUES(" + group_id_KT + ", " + user_id_KT + " );";
							stmt = connKT.prepareStatement(qry);
							stmt.executeUpdate();

							// insert authentication Realm record
							qry = "INSERT INTO seq_role (Login, Role) VALUES('" + req_user + "', 'user');";
							stmt = connKT.prepareStatement(qry);
							stmt.executeUpdate();
							
							// end -- KT
							
							//CBUM
							connCBUM.setAutoCommit(false);

							// insert user
							qry = "INSERT INTO ent_user (Login, Name, Pass, IsGroup, Sync, EMail, Organization, City, Country, How) VALUES(" +
								"'" + req_user + "', '" + req_last_name + ", " + req_first_name + "', '" + req_password_md5 + "', 0,1, '" + 
								req_email + "', '" + req_organization + "', '" + req_city + "', '" + req_country + "', '" +  req_how + "');";
							stmt = connCBUM.prepareStatement(qry);
							stmt.executeUpdate();
							
							// get user id
							qry = "SELECT MAX(LAST_INSERT_ID(UserID)) AS LastID FROM ent_user WHERE Login='" + req_user + "';";
							stmt = connCBUM.prepareStatement(qry);
							rs = stmt.executeQuery();
							while(rs.next())
							{
								user_id_CBUM = rs.getInt("LastID");
							}
							
							// insert into group
							qry = "INSERT INTO rel_user_user (GroupID, UserID) VALUES(" + group_id_CBUM + ", " + user_id_CBUM + " );";
							stmt = connCBUM.prepareStatement(qry);
							stmt.executeUpdate();
							
							// insert into CBUM Cache
//System.out.println("CBUM upload");
							if(!sd.addNewUser(user_id_CBUM, req_user, req_group))
								throw new IOException("Remote PWD Manager: Failure to Create user in CBUM Cache");
							// end -- CBUM

							//WADEIN
							connWADEIN.setAutoCommit(false);
							// insert user
							qry = "INSERT INTO users_c (login, name, `group`, superuser, portal_user, country, how) VALUES(" +
								"'" + req_user + "', '" + req_first_name + " " + req_last_name + "', '" + req_group + "', 0, 1, '" + 
								req_country + "', '" +  req_how + "');";
							stmt = connWADEIN.prepareStatement(qry);
							stmt.executeQuery();
							
							// get user id
//							qry = "SELECT MAX(LAST_INSERT_ID(user_id)) AS LastID FROM users_c WHERE login='" + req_user + "';";
//							stmt = connWADEIN.prepareStatement(qry);
//							rs = stmt.executeQuery();
//							while(rs.next())
//							{
//System.out.println("CBUM wadein: user is there");
//
//								user_id_WADEIN = rs.getInt("LastID");
//							}


							stmt = connWADEIN.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS);
							stmt.executeUpdate(qry);

							rs = stmt.getGeneratedKeys();
							if (rs.next())
				                       user_id_WADEIN = rs.getInt("GENERATED_KEY");
				
//System.out.println("CBUM wadein: " + user_id_WADEIN);




							// Getting the ids of operations:
							qry = "SELECT operation_id FROM operations";
							stmt = connWADEIN.prepareStatement(qry);
							rs = stmt.executeQuery(qry);
							
							// Adding the user to the 'user_model' table:
							Statement stmt2 = connWADEIN.createStatement();
							while (rs.next())
							{
								String operationId = rs.getString("operation_id");
								qry = "INSERT INTO user_model(user_id, operation_id) " +
									"VALUES (" + user_id_WADEIN + ", " + operationId + ");";
//System.out.println("Wadein shit: " + qry );									
								stmt2.executeUpdate(qry);
							}
							
							if(stmt2 == null)
							{
								stmt2.close();
								stmt2 = null;
							}
							
							// end -- WADEIN

							connKT.commit();
							connCBUM.commit();
							connWADEIN.commit();

							HttpSession session = request.getSession();
							session.invalidate();
							session = request.getSession(true);
							
							res_result = session.getId().substring(session.getId().length()-5);
							
							connKT.setAutoCommit(true);
							connCBUM.setAutoCommit(true);
							connWADEIN.setAutoCommit(true);

						}// end -- login free
						else
							res_result = "0,Login specified is taken. KT, CBUM, WADEIn have it (" + login_free_KT + "," + login_free_CBUM /*+ "," + login_free_WADEIN */ + ")";
						
					}// end -- group(s) exists
					else
						res_result = "0,Group doesn' exist. KT, CBUM have it (" + group_exists_KT + "," + group_exists_CBUM + ")";
					
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
				finally
				{// finally after try to register
					try
					{
						if(rs != null)
						{
							rs.close();
							rs = null;
						}
						if(stmt != null)
						{
							stmt.close();
							stmt = null;
						}
						if(connKT != null)
						{
							connKT.close();
							connKT = null;
						}
						if(connCBUM != null)
						{
							connCBUM.close();
							connCBUM = null;
						}
						if(connWADEIN != null)
						{
							connWADEIN.close();
							connWADEIN = null;
						}
					}
					catch(Exception e) { e.printStackTrace(System.err); };
				}// end -- finally after try to register
			}// end - proceed with registration
		}// end -- registration
		
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.println(res_result);
		out.close();
	}
}

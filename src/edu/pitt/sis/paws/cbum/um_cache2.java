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
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that implements user activity logging via Http GET request. CBUM singleton entity does not need to be running in order for this servlet to function properly.
 * 
 * @author	Michael V. Yudelson
 * @since	1.5
 * @version %I%, %G%
 */
public class um_cache2 extends HttpServlet 
{
	static final long serialVersionUID = -2L;
	
	// Constants
	//	Request parameters
	public static final String REQ_ACTIVITY = "act"; //quiz
	public static final String REQ_SUBACTIVITY = "sub"; //ques
	public static final String REQ_SESSION = "sid";
	public static final String REQ_GROUP = "grp";
	public static final String REQ_USER = "usr";
	public static final String REQ_APPLICATION = "app";
	public static final String REQ_SVC = "svc";
	public static final String REQ_RESULT = "res"; //answercheck
	public static final String REQ_IP = "ip"; //answercheck
	
	//	Other
	private static final String APPLICATION_UNKNOWN = "1";//"<Unknown Application>";
	private static final String GROUP_UNKNOWN = "anonymous_group";
	public static final int GROUP_UNKNOWN_ID = 1;
	private static final String USER_UNKNOWN = "anonymous_user";
	public static final int USER_UNKNOWN_ID = 2;
	private static final String ACTIVITY_UNKNOWN = "unknown_activity";
	public static final int ACTIVITY_UNKNOWN_ID = 1;
	
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}

	public void service(HttpServletRequest request, 
		HttpServletResponse response) throws ServletException, UnsupportedEncodingException, IOException
	{
//System.out.println("[CBUM] um received a request"); /// DEBUG
		
		// Retrieve all possible parameters
		String req_activity = request.getParameter(REQ_ACTIVITY);
		String req_subactivity = request.getParameter(REQ_SUBACTIVITY);
		String req_session = request.getParameter(REQ_SESSION);
		String req_group = request.getParameter(REQ_GROUP);
		String req_user = request.getParameter(REQ_USER);
		String req_application = request.getParameter(REQ_APPLICATION);
		String req_svc = request.getParameter(REQ_SVC);
		String req_result = request.getParameter(REQ_RESULT);
		String req_ip = request.getParameter(REQ_IP);
//System.out.println("~~~ [CBUM] um_cache2 svc=" + req_svc);	
		
		// decode parameters
		req_activity = (req_activity!=null)?URLDecoder.decode(req_activity, "UTF-8"):"";//req_activity;
		req_subactivity = (req_subactivity!=null)?URLDecoder.decode(req_subactivity, "UTF-8"):"";//req_subactivity;
		req_session = (req_session!=null)?URLDecoder.decode(req_session, "UTF-8"):"";//req_session;
		req_group = (req_group!=null)?URLDecoder.decode(req_group, "UTF-8"):"";//req_group;
		req_user = (req_user!=null)?URLDecoder.decode(req_user, "UTF-8"):"";//req_user;
		req_application = (req_application!=null)?URLDecoder.decode(req_application, "UTF-8"):"";//req_application;
		req_svc = (req_svc!=null)?/*URLDecoder.decode(*/req_svc/*, "UTF-8")*/:"";//req_svc;
		req_result = (req_result!=null)?URLDecoder.decode(req_result, "UTF-8"):"";//req_result;
		req_ip = (req_ip!=null)?URLDecoder.decode(req_ip, "UTF-8"):"";//req_ip;

		String all_parameters = "app=" + req_application + ";usr=" + req_user + 
			";grp="+ req_group + ";act=" + req_activity + ";sub=" + req_subactivity + 
			";res=" + req_result + ";sid=" + req_session + ";svc=" + req_svc + ";ip=" + req_ip;

		boolean app_found = false;
		boolean group_found = false;
		boolean user_found = false;
		boolean activity_found = false;
		boolean sub_activity_found = false;
		
		boolean app_open_core = false;
		boolean single_activity_report = false;
		boolean anonymous_report = false;

		String app_message = "";
		String group_message = "";
		String user_message = "";
		String anonymous_message = "";
		String activity_message = "";
		String sub_activity_message = "";
		
//		String message = "";

		ServerDaemon sd = ServerDaemon.getInstance(getServletContext());
		ResourceMap resmap = sd.getResourceMap();
		
		// ----- Parse "what" parameters -----
		
		// APPLICATION
		App app = null;
		if(req_application != null)
		{
			int temp_app_id = -1;
			try
			{
				temp_app_id = Integer.parseInt(req_application);
			}
			catch(java.lang.NumberFormatException e)
			{
				app_message = " AppId=" + req_application + " not a number.";
			}
			
			if(temp_app_id != -1)
			{
				app = resmap.apps.findById(temp_app_id);
				if(app != null)
				{
					app_found = true;
					app_open_core = app.getOpenCore();
					single_activity_report = app.getSingleActivityReport();
					anonymous_report = app.getAnonymousReport();
				}
				else
				{
					app_message = " AppId=" + req_application + " nod found.";
				}
			}
		}
		else
		{
			app_message = " AppId is NULL.";
		}
		// end of -- APPLICATION
		
		// ACTIVITY
		Activity act = null;
		if(req_activity != null)
		{
			if(app != null)
			{
				act = app.activities.findByTitle(req_activity);
				if(act != null)
				{
					if(act.app.getId() == app.getId())
					{
						activity_found = true;
					}
					else
					{
						activity_message = " Activity=" + req_activity + " does not belong to AppID=" + app.getId() + ".";
					}
				}
				else
				{
					activity_message = " Activity=" + req_activity + " not found.";
				}
			}
		}
		else
		{
			activity_message = " Activity is null.";
		}
		// end of -- ACTIVITY
		// SUBACTIVITY
		Activity sub_act = null;
		if(req_subactivity != null && act != null)
		{
			sub_act = act.children.findByTitle(req_subactivity);

			if(sub_act != null)
			{
				if(app != null)
				{
					if(sub_act.app.getId() == app.getId())
					{
						if((sub_act.parent != null) && (sub_act.parent.getTitle().equals(req_activity)))
						{
							sub_activity_found = true;
						}
						else
						{
							sub_activity_message += " SubActivity=" + req_subactivity + " is not a child of Activity=" + req_activity + ".";
						}
					}
					else
					{
						sub_activity_message += " SubActivity=" + req_subactivity + " does not belong to AppID=" + app.getId() + " instead it's " + sub_act.app.getId() + ".";
					}
				}
			}
			else
			{
				sub_activity_message += " SubActivity=" + req_subactivity + " not found.";
			}
		}
		else
		{
			sub_activity_message += " SubActivity is null.";
		}
		// end of -- SUBACTIVITY

//System.out.println("app_found="+app_found);		
//System.out.println("app_open_core="+app_open_core);		
//System.out.println("single_activity_report="+single_activity_report);		
//System.out.println("activity_found="+activity_found);		
//System.out.println("sub_activity_found="+sub_activity_found);		

//System.out.println("activity_message: " + activity_message);		
//System.out.println("sub_activity_message: " + sub_activity_message);		

		// CHECK "WHAT" FLAGS
		if(app_found /*&& group_found && user_found*/ && app_open_core && 
				( (!activity_found && (req_activity != null)) || 
				  (!sub_activity_found && !single_activity_report && (req_subactivity != null))
				)
			)
		{
			// First activity
			if(!activity_found)
			{// add activity
				//Generate URI
				String uri = (app.getURIPrefix().length() > 0)?app.getURIPrefix()+req_activity:"no uri";
				act = sd.addNewActivity(app.getId(), uri, req_activity, "open core addition");
//System.out.println("CHECK act " + resmap.activities.findByTitle(req_activity));				
				if(act != null)
					activity_found = true;
			}
			// then subactivity
			if(!sub_activity_found && !single_activity_report)
			{
				sub_act = sd.addNewActivity(app.getId(), "no uri", req_subactivity, "open core addition");
//System.out.println("CHECK sub_act " + resmap.activities.findByTitle(req_subactivity));				
				if(sub_act != null)
					sub_activity_found = true;
//System.out.println("add sub-activity");				
				sd.addActSubActLink(app, act, sub_act);
			}
		}
		// end of -- CHECK "WHAT" FLAGS
		// end of  ----- Parse "what" parameters -----
		
		// ----- Parse "who" parameters -----
		// GROUP
		User group = null;
		if(req_group != null)
		{
			group = resmap.groups.findByTitle(req_group);
			
			if(group != null)
			{
				if(app != null)
				{
					if(group.apps.findById(app.getId()) != null)
					{
						group_found = true;
					}
					else
					{
						group_message = " Group=" + req_group + " not registered for AppID=" + app.getId() + ".";
					}
				}
			}
			else
			{
				group_message = " Group=" + req_group + " nod found.";
			}
		}
		else
		{
			group_message = " Group is null.";
		}
		// end of -- GROUP
		// USER
		User user = null;
		if(req_user != null)
		{
			user = resmap.users.findByTitle(req_user);
			if(user != null)
			{
				if(group != null)
				{
					if(group.ordinates.findByTitle(req_user) != null)
					{
						user_found = true;
					}
					else
					{
						user_message += "User=" + req_user + " is not a member of Group=" + req_group + ".";
					}
				}
			}
			else
			{
				user_message += "User=" + req_user + " nod found.";
			}
		}
		else
		{
			user_message += "User is null.";
		}
		// end of -- USER
		
		if( (!group_found || !user_found) && anonymous_report )
		{
			user_message = "";
			group_message = "";
		}
		boolean isAnonymous = false;
		if(anonymous_report && !user_found && !group_found)
		{
			if(req_ip!=null && req_ip.length()>0)
			{
				user = resmap.users.findByTitle("anonymous_user");
				group = resmap.groups.findByTitle("anonymous_group");
				if(user!=null && group!=null)
				{
					isAnonymous = true;
					group_found = true;
					user_found = true;
				}
			}
//System.out.println("anonymous_report="+ anonymous_report+", user="+user+" group="+group);					
			if(!isAnonymous)
			{
				anonymous_message = " Anonymous logging is not configured correcly.";
			}
		}
		// set null user/group
		if(user == null) user = resmap.users.findByTitle("anonymous_user");
		if(group == null) group = resmap.groups.findByTitle("anonymous_group");
		// end of ----- Parse "who" parameters -----
		
		// ----- Parse "how" parameters -----
		req_session = (req_session != null)?req_session:"{unk}";
		req_svc = (req_svc != null)?req_svc:"";
		req_result = (req_result != null)?req_result:"{none}";
		// end of ----- Parse "how" parameters -----
		
		String result = "";
		
//		int app_id = -1;
		int group_id = -1;
		int user_id = -1;
		int activity_id = -1;
		
		// Set Unknowns
		if( (!app_found) || (!group_found) || (!user_found) ||
			(!activity_found) || (!sub_activity_found && !single_activity_report))
		{
			req_application = APPLICATION_UNKNOWN;
			req_group = GROUP_UNKNOWN;
			group_id = GROUP_UNKNOWN_ID;
			req_user = USER_UNKNOWN;
			user_id = USER_UNKNOWN_ID;
			req_activity = ACTIVITY_UNKNOWN;
			activity_id = ACTIVITY_UNKNOWN_ID;
			
			result = "Error." + app_message + anonymous_message + group_message + 
					user_message + activity_message + sub_activity_message;
		}
		else
		{
//			app_id = app.getId();
			group_id = group.getId();
			user_id = user.getId();
			activity_id = (single_activity_report)?act.getId():sub_act.getId();
			
			result = "OK."; // OK
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		String qry;

		try
		{
			conn = ServerDaemon.getConnection();
			
			// Find the ID's of User, Group, Activity
			java.util.Date date = new java.util.Date();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String s = formatter.format(date);
			// Submit acticvity into the database
			qry = "INSERT INTO ent_user_activity (AppID, UserID, " + 
				"GroupID, Result, ActivityID, Session, DateNTime, " + 
				"DateNTimeNS, SVC, AllParameters) VALUES (" + req_application+ "," + 
				user_id + "," + group_id + ",'" + req_result + "'," + 
				activity_id + ",'" + req_session + 
				"','" + s + "'," + System.nanoTime() + ", '" + req_svc + "','" + all_parameters + "');"; 
//System.out.println("um2:qry="+qry);
			stmt = conn.prepareStatement(qry);
			stmt.executeUpdate();
			
			stmt.close();
			stmt = null;
			conn.close();
			conn = null;
			
			// Add to Resource Map
			sd.addNewUserActivityInt(Integer.parseInt(req_application), user_id, group_id, activity_id, req_result, s, request);

		}
		catch(Exception e) { e.printStackTrace(System.out); }
		finally
		{
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
//System.out.println(result);
		out.close();
	}

}

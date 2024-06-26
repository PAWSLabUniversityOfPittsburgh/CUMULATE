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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.Provider;
import java.security.Security;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
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
	public static final String REQ_CODE = "cod";//submitted user code if any (Currently for only PCRS)
	public static final String REQ_FULL_CODE = "full-cod"; //Submitted user code in context (with surrounded code to run the submitted code. (Currenly for only PCRS)
	
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
		String req_code = request.getParameter(REQ_CODE);
		String req_full_code = request.getParameter(REQ_FULL_CODE);

		
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

		if(req_code!=null) {
			all_parameters = all_parameters + ";cod=" + req_code;
		}
		
		if(req_full_code!=null) {
			all_parameters = all_parameters + ";fullcod=" + req_full_code;
		}
		
		// Kamil: Workaround fix for integration with Utrecht for StudyLens system
		// Has to be replaced with LTI integration in near future (today: 01/17/2023)
		// Remove this if we are not collaborating with them anymore
		if(req_group != null && req_group.equals("CoTaPP23")) {
			// Redirect incoming traffic to Utrecht without storing our database due to data privacy laws in Europe
			
			String redirect_result = "";
			URL url = new URL("https://studylens.science.uu.nl/backend/api/postPawsContentLevels?"+ all_parameters.replaceAll(";","&"));
			
			String command = "curl " + url;

			Process process = Runtime.getRuntime().exec(command);
			try {
				process.waitFor();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				redirect_result += line + "\n";
			}
				
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.println(redirect_result);
			out.close();
			return;
		}
		
		notifyMasteryGrid(req_user, req_result);

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
			String formattedDate = formatter.format(date);
			// Submit activity into the database
			qry = "INSERT INTO ent_user_activity (AppID, UserID, " + 
				"GroupID, Result, ActivityID, Session, DateNTime, " + 
				"DateNTimeNS, SVC, AllParameters) VALUES (?,?,?,?,?,?,?,?,?,?);"; 
			//System.out.println("um2:qry="+qry);
			stmt = conn.prepareStatement(qry);
			stmt.setInt(1, Integer.parseInt(req_application));
			stmt.setInt(2, user_id);
			stmt.setInt(3, group_id);
			stmt.setString(4, req_result);
			stmt.setInt(5, activity_id);
			stmt.setString(6, req_session);
			stmt.setString(7, formattedDate);
			stmt.setLong(8, System.currentTimeMillis());
			stmt.setString(9, req_svc);
			stmt.setString(10, all_parameters);
			
			stmt.executeUpdate();
			
			stmt.close();
			stmt = null;
			conn.close();
			conn = null;
			
			// Add to Resource Map
			sd.addNewUserActivityInt(Integer.parseInt(req_application), user_id, group_id, activity_id, req_result, formattedDate, request);

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

	private void notifyMasteryGrid(String user, String result) {
		if(user != null && result!= null && (Double.parseDouble(result) == 0.0 || Double.parseDouble(result) == 1.0)) {
			HttpURLConnection con = null;
			try {
				URL url = new URL("http://localhost/mgnotificate/api/notifyuser?usr="+ user + "&res=" + result);
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.connect();
				int status = con.getResponseCode();
				if(status == HttpURLConnection.HTTP_OK) {
					System.out.println("MG User " + user + " result " + result + " notified");
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				System.out.println("Exception: Notify MG User " + user + " result " + result);
			} finally {
				if(con != null) {
					con.disconnect();
				}
			}
		}
	}

}

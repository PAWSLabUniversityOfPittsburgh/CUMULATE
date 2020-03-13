package edu.pitt.sis.paws.cbum;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.pitt.sis.paws.core.Item2Vector;

/**
 * Servlet implementation class for Servlet: Umc
 * 
 */
public class Umc extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	static final long serialVersionUID = 1L;

	public static final String REQ_ACTIVITY = "act";
	public static final String REQ_SUBACTIVITY = "sub";
	public static final String REQ_DOMAIN = "dom";
	public static final String REQ_CONCEPT = "con";
	public static final String REQ_SESSION = "sid";
	public static final String REQ_GROUP = "grp";
	public static final String REQ_USER = "usr";
	public static final String REQ_APPLICATION = "app";
	public static final String REQ_SVC = "svc";
	public static final String REQ_VALUE = "val"; 
	public static final String REQ_RESULT = "res";

	public static final int APP_UNKNOWN_ID = 1;
	public static final int GROUP_UNKNOWN_ID = 1;
	public static final int USER_UNKNOWN_ID = 2;
	public static final int DOMAIN_UNKNOWN_ID = 13;
	public static final int CONCEPT_UNKNOWN_ID = 2000;

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		String req_domain = request.getParameter(REQ_DOMAIN);
		String req_concept = request.getParameter(REQ_CONCEPT);
		String req_session = request.getParameter(REQ_SESSION);
		String req_user = request.getParameter(REQ_USER);
		String req_group = request.getParameter(REQ_GROUP);
		String req_app = request.getParameter(REQ_APPLICATION);
		String req_svc = request.getParameter(REQ_SVC);
		String req_value = request.getParameter(REQ_VALUE);
		
		String req_activity = request.getParameter(REQ_ACTIVITY);
		String req_subactivity = request.getParameter(REQ_SUBACTIVITY);
		String req_application = request.getParameter(REQ_APPLICATION);
		String req_result = request.getParameter(REQ_RESULT);
		
	
		/**
		 * For Spring 2020 AALTOSQL20 course, SQL-Tutor answers are also required to be inserted to um2.ent_user_activity.
		 * To achieve this requirement, here, um service called indirectly.
		 * This is required to have the progress update for the SQL-Tutor activity
		 * Since the concept-level update would be duplicate with activity-level, I have commented out the following line:
		 * sd.updateConceptKnowledge(cons.get(i), values.get(i).doubleValue(), user, group, app, s_date, request)
		 */
		String randomUUID = UUID.randomUUID().toString(); // To make the connection between um2.ent_user_activity and um2.ent_user_knowledge_updates
		req_svc = req_svc != null? req_svc + ";" + randomUUID:null;
		if(req_group.contains("aaltosql")) { // Due to inconsistency in SQL-Tutor side, the group parameter is being sent as lower case for some problems.
			req_group = req_group.toUpperCase();
		}
		callUM(req_user, req_group, req_app, req_subactivity,req_result,req_svc,req_session);
		
		// decode
		req_domain = (req_domain!=null)?URLDecoder.decode(req_domain,"UTF-8"):null;
		req_concept = (req_concept!=null)?URLDecoder.decode(req_concept,"UTF-8"):null;
		req_session = (req_session!=null)?URLDecoder.decode(req_session,"UTF-8"):null;
		req_user = (req_user!=null)?URLDecoder.decode(req_user,"UTF-8"):null;
		req_group = (req_group!=null)?URLDecoder.decode(req_group,"UTF-8"):null;
		req_app = (req_app!=null)?URLDecoder.decode(req_app,"UTF-8"):null;
		req_svc = (req_svc!=null)?URLDecoder.decode(req_svc,"UTF-8"):null;
		req_value = (req_value!=null)?URLDecoder.decode(req_value,"UTF-8"):null;
		req_result = (req_result!=null)?URLDecoder.decode(req_result, "UTF-8"):"";
		req_application = (req_application!=null)?URLDecoder.decode(req_application, "UTF-8"):"";
		req_activity = (req_activity!=null)?URLDecoder.decode(req_activity, "UTF-8"):"";
		req_subactivity = (req_subactivity!=null)?URLDecoder.decode(req_subactivity, "UTF-8"):"";
	
		String all_parameters = "app=" + req_app + ";dom=" + req_domain + ";con=" + req_concept +
			";val=" + req_value + ";usr=" + req_user + ";grp=" + req_group + ";sid=" + req_session +
			";svc=" + req_svc;
		
		boolean app_found = false;
		App app = null;

		boolean dom_found = false;
		Concept dom = null;
		
		int no_total_concepts = 0;
		int no_found_concepts = 0;
		int no_cons_in_dom = 0;
		Item2Vector<Concept> cons = new Item2Vector<Concept>();
		Vector<Boolean> cons_in_dom = new Vector<Boolean>();
		
		int no_total_values = 0;
		int no_proper_values = 0;
		Vector<Double> values = new Vector<Double>();
		Vector<Boolean> values_proper = new Vector<Boolean>();

		boolean user_found = false;
		boolean group_found = false;
		boolean user_is_in_group = false;

		
		ServerDaemon sd = ServerDaemon.getInstance(getServletContext());
		ResourceMap resmap = sd.getResourceMap();

		String app_message = "";
		String dom_message = "";
		String con_message = "";
		String val_message = "";
		String usr_message = "";
		String grp_message = "";
		
		// PARSE PARAMETERS
		// - APPLICATION
		if(req_app!=null)
		{
			int temp_app_id = -1;
			try
			{
				temp_app_id = Integer.parseInt(req_app);
			}
			catch(java.lang.NumberFormatException e)
			{
				app_message = " AppId=" + req_app + " not a number.";
			}
			
			if(temp_app_id != -1)
			{
				app = resmap.apps.findById(temp_app_id);
				if(app != null)
					app_found = true;
				else
					app_message = " AppId=" + req_app + " nod found.";
			}
		}
		else
		{
			app_message = "AppId is NULL";
		}
		
		// - DOMAIN
		if(req_domain!=null)
		{
			dom = resmap.domains.findByTitle(req_domain);
			if(dom != null)
				dom_found = true;
			else
				dom_message = " DomainId=" + req_domain + " not found.";
		}
		else
		{
			dom_message = "DomainId is NULL";
		}
		
		// - CONCEPTS & DOMAIN-CONCEPT LINK
		if(req_concept!=null)
		{// if concept not null
			String not_found = "";
			String not_in_dom = "";
			StringTokenizer st = new StringTokenizer(req_concept, ",");
			while(st.hasMoreTokens())
			{// for all tokens
				no_total_concepts++;
				String _concept_s = st.nextToken();
				
//				Concept con = resmap.concepts.findByTitle(_concept_s);
				Concept con = dom.getChildren().findByTitle(_concept_s);
				
				Concept _con_search_in_dom = null;
				if(con != null)
				{
					no_found_concepts++;
					if(dom_found)
					{
						_con_search_in_dom = dom.getChildren().findByTitle(_concept_s);
//						if(_con_search_in_dom!=null)
							no_cons_in_dom++;
//						else
//							not_in_dom += ((not_in_dom.length()>0)?",":"") + _concept_s;
					}
				}
				else
				{
					not_found +=  ((not_found.length()>0)?",":"") + _concept_s;
					not_in_dom += ((not_in_dom.length()>0)?",":"") + _concept_s;
				}
				cons.add(con); // even if null
				cons_in_dom.add(_con_search_in_dom!=null);
			}// end of -- for all tokens
			
			if(dom_found && no_cons_in_dom<no_found_concepts)
			{
				con_message += " Concept(s) not in domain " + dom.getTitle() + ":" + not_in_dom;
			}
			
			if(no_found_concepts<no_total_concepts)
			{
				con_message += "Concept(s) not found: " + not_found;
			}
			
		}// end of -- if concept not null
		else
		{
			con_message += "Concept(s) is(are) NULL";
		}
		
		// - VALUES
		if(req_value!=null)
		{// if values not null
			String not_double = "";
			
			StringTokenizer st = new StringTokenizer(req_value, ",");
			while(st.hasMoreTokens())
			{// for all tokens
				no_total_values++;
				String _value_s = st.nextToken();
				
				Double temp_value = Math.PI;
				try
				{
					temp_value = Double.parseDouble(_value_s);
				}
				catch(java.lang.NumberFormatException e)
				{
					not_double += ((not_double.length()>0)?",":"") + _value_s;
				}
				values.add(temp_value);
				
				if(temp_value != Math.PI)
					no_proper_values++;
				values_proper.add(temp_value != Math.PI);
				
			}// end of -- for all tokens
			
			if(no_proper_values < no_total_values)
			{
				val_message = "Values(s) not double: " + not_double + ".";
			}
			
			if(no_total_values != no_total_concepts)
			{
				val_message += " Number of Concepts and Values don't match";
			}
			
		}// end of -- if values not null
		else
		{
			val_message = "Value(s) is(are) NULL";
		}
		

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
						user_is_in_group = true;
					}
					else
					{
						grp_message = " Group=" + req_group + " not registered for AppID=" + app.getId() + ".";
					}
				}
			}
			else
			{
				grp_message = " Group=" + req_group + " nod found.";
			}
		}
		else
		{
			grp_message = " Group is null.";
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
						usr_message += "User=" + req_user + " is not a member of Group=" + req_group + ".";
					}
				}
			}
			else
			{
				usr_message += "User=" + req_user + " nod found.";
			}
		}
		else
		{
			usr_message += "User is null.";
		}
		// end of -- USER
		
		// Set Unknowns if necessary
		int good_concepts = 0;
		for(int i=0; i<cons_in_dom.size(); i++)
			good_concepts += (cons_in_dom.get(i).booleanValue() && values_proper.get(i).booleanValue())?1:0;
		boolean some_concepts_are_fine = good_concepts>0;
		boolean we_report = false;
		
		if( app_found &&  dom_found && some_concepts_are_fine && user_found && group_found && user_is_in_group)
		{
			// WE ARE GOOD AND REPORTING SOMETHING
			we_report = true;
		}
		else
		{
			// WE ARE REPORTING A FAILURE
			user = resmap.users.findById(USER_UNKNOWN_ID);
			group = resmap.groups.findById(GROUP_UNKNOWN_ID);
			app = resmap.apps.findById(APP_UNKNOWN_ID);
			dom = resmap.domains.findById(DOMAIN_UNKNOWN_ID);
			cons.clear();
			cons.add(resmap.concepts.findById(CONCEPT_UNKNOWN_ID));
			cons_in_dom.clear();
			cons_in_dom.add(true);
			values_proper.clear();
			values_proper.add(true);
			values.clear();
			values.add(0.0);
		}
		String messages = app_message + dom_message + con_message + val_message + usr_message + grp_message;
		String result = ((messages.length()>0)? (((we_report)?"Warning! ":"Error! ") + messages):"OK");
		
		Connection conn = null;
		PreparedStatement stmt = null;
		String qry;

		try
		{
			conn = ServerDaemon.getConnection();
			
			// Find the ID's of User, Group, Activity
			java.util.Date date = new java.util.Date();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String s_date = formatter.format(date);
			long time_ns = System.nanoTime();
			// Submit acticvity into the database
			String qry_values = "";
			
			for(int i=0; i<cons_in_dom.size(); i++)
			{
				if(cons_in_dom.get(i).booleanValue() && values_proper.get(i).booleanValue())
				{
					String active = "1";
					
					if(req_group.toLowerCase().contains("aaltosql20")) {
						active = "0";
					}
					
					qry_values += ((qry_values.length()>0)?",":"") + "(" + app.getId() + "," + user.getId() + "," + group.getId() +
					","+ dom.getId() + "," + cons.get(i).getId() + "," + values.get(i).doubleValue() + 
					",'" + req_session + "','" + s_date + "'," + time_ns + ",'" + req_svc + "','"+ 
					all_parameters + "','" + active + "')";
					
					
					
					if(values.get(i).doubleValue()>0) // ONLY UPDATE WITH POSITIVE
					{
						//sd.updateConceptKnowledge(cons.get(i), values.get(i).doubleValue(), user, group, app, s_date, request);
					}
				}
			}
			qry = "INSERT INTO ent_user_knowledge_updates (AppID, UserID, " + 
			"GroupID, DomainID, ConceptID, Value, Session, DateNTime, DateNTimeNS, " + 
			"SVC, AllParameters, active) VALUES" + qry_values + ";";
			
			stmt = conn.prepareStatement(qry);
			stmt.executeUpdate();
			
			stmt.close();
			stmt = null;
			conn.close();
			conn = null;
			
			// Add to Resource Map
//			sd.addNewUserActivityInt(Integer.parseInt(req_application), user_id, group_id, activity_id, req_result, s_date, request);
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
	
	
	private void callUM(String user, String group, String app, String sub, String result, String svc, String session) {
		HttpURLConnection con = null;
		StringBuilder urlParameterBuilder = new StringBuilder();
		
		try {
			urlParameterBuilder.append("http://adapt2.sis.pitt.edu/cbum/um?");
			urlParameterBuilder.append("usr=").append(user).append("&");
			urlParameterBuilder.append("grp=").append(group).append("&");
			urlParameterBuilder.append("app=").append(app).append("&");
			urlParameterBuilder.append("res=").append(result).append("&");
			urlParameterBuilder.append("act=0").append("&");
			urlParameterBuilder.append("sub=").append(sub).append("&");
			urlParameterBuilder.append("svc=").append(URLEncoder.encode(svc, "UTF-8")).append("&");
			urlParameterBuilder.append("sid=").append(session);
			
			URL url = new URL(urlParameterBuilder.toString());
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			int status = con.getResponseCode();
			if(status == HttpURLConnection.HTTP_OK) {
				System.out.println("Umc request is redirected to um:" + urlParameterBuilder.toString());
			} else {
				System.out.println("Umc request redirect to um failed:" + urlParameterBuilder.toString() + " HttpURLConnection status="+ status);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Exception in Umc request redirecting to um:" + urlParameterBuilder.toString());
		} finally {
			if(con != null) {
				con.disconnect();
			}
		}
		
	}
}
package edu.pitt.sis.paws.cbum;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
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

	public static final String REQ_DOMAIN = "dom";
	public static final String REQ_CONCEPT = "con";
	public static final String REQ_SESSION = "sid";
	public static final String REQ_GROUP = "grp";
	public static final String REQ_USER = "usr";
	public static final String REQ_APPLICATION = "app";
	public static final String REQ_SVC = "svc";
	public static final String REQ_VALUE = "val"; 

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
		
		// decode
		req_domain = (req_domain!=null)?URLDecoder.decode(req_domain,"UTF-8"):null;
		req_concept = (req_concept!=null)?URLDecoder.decode(req_concept,"UTF-8"):null;
		req_session = (req_session!=null)?URLDecoder.decode(req_session,"UTF-8"):null;
		req_user = (req_user!=null)?URLDecoder.decode(req_user,"UTF-8"):null;
		req_group = (req_group!=null)?URLDecoder.decode(req_group,"UTF-8"):null;
		req_app = (req_app!=null)?URLDecoder.decode(req_app,"UTF-8"):null;
		req_svc = (req_svc!=null)?URLDecoder.decode(req_svc,"UTF-8"):null;
		req_value = (req_value!=null)?URLDecoder.decode(req_value,"UTF-8"):null;
		
		String all_parameters = "app=" + req_app + ";dom=" + req_domain + ";con=" + req_concept +
			";val=" + req_value + ";usr=" + req_user + ";grp=" + req_group + ";sid=" + req_session +
			";svc=" + req_svc;
		
		boolean app_found = false;
		App app = null;

		boolean dom_found = false;
		Concept dom = null;
		
		boolean con_found = false;
		int no_total_concepts = 0;
		int no_found_concepts = 0;
		int no_cons_in_dom = 0;
		Item2Vector<Concept> cons = new Item2Vector<Concept>();
		Vector<Boolean> cons_in_dom = new Vector<Boolean>();
		
		boolean val_match_con = false;
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
				cons_in_dom.add(new Boolean(_con_search_in_dom!=null));
			}// end of -- for all tokens
			
			if(dom_found && no_cons_in_dom<no_found_concepts)
			{
				con_message += " Concept(s) not in domain " + dom.getTitle() + ":" + not_in_dom;
			}
			
			if(no_found_concepts<no_total_concepts)
			{
				con_message += "Concept(s) not found: " + not_found;
			}
			else
				con_found = true;
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
				values_proper.add(new Boolean(temp_value != Math.PI));
				
			}// end of -- for all tokens
			
			if(no_proper_values < no_total_values)
			{
				val_message = "Values(s) not double: " + not_double + ".";
			}
			
			if(no_total_values != no_total_concepts)
			{
				val_message += " Number of Concepts and Values don't match";
			}
			else
				val_match_con = true;
			
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
		
//System.out.println("app_found = " + app_found);
//System.out.println("dom_found = " + dom_found);
//System.out.println("some_concepts_are_fine = " + some_concepts_are_fine);
//System.out.println("user_found = " + user_found);
//System.out.println("group_found = " + group_found);
//System.out.println("user_is_in_group = " + user_is_in_group);

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
			cons_in_dom.add(new Boolean(true));
			values_proper.clear();
			values_proper.add(new Boolean(true));
			values.clear();
			values.add(new Double(0.0));
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
//System.out.println("cons_in_dom.size() = " + cons_in_dom.size());			
//System.out.println("values_proper.size() = " + values_proper.size());			
//System.out.println("cons.size() = " + cons.size());			
//System.out.println("values.size() = " + values.size());	
			for(int i=0; i<cons_in_dom.size(); i++)
			{
				if(cons_in_dom.get(i).booleanValue() && values_proper.get(i).booleanValue())
				{
					qry_values += ((qry_values.length()>0)?",":"") + "(" + app.getId() + "," + user.getId() + "," + group.getId() +
					","+ dom.getId() + "," + cons.get(i).getId() + "," + values.get(i).doubleValue() + 
					",'" + req_session + "','" + s_date + "'," + time_ns + ",'" + req_svc + "','"+ 
					all_parameters + "')";
					
					
					
					if(values.get(i).doubleValue()>0) // ONLY UPDATE WITH POSITIVE
					{
						sd.updateConceptKnowledge(cons.get(i), values.get(i).doubleValue(), user, group, app, s_date, request);
					}
				}
			}
			qry = "INSERT INTO ent_user_knowledge_updates (AppID, UserID, " + 
			"GroupID, DomainID, ConceptID, Value, Session, DateNTime, DateNTimeNS, " + 
			"SVC, AllParameters) VALUES" + qry_values + ";";
			
//System.out.println("um2:qry="+qry);
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
}
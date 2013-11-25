/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */

package edu.pitt.sis.paws.cbum;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.pitt.sis.paws.cbum.structures.Activity2ConceptMapping4Report;

/** mediator of all the report traffic
 * 
 * @author Michael V. Yudelson
 */
public class ReportManager extends HttpServlet 
{
	static final long serialVersionUID = -2L;
	
	// Constants
	public final static String PAR_REPORT_DIRECTION = "dir";
	public final static String VAL_REPORT_DIRECTION_IN = "in";
	public final static String VAL_REPORT_DIRECTION_OUT = "out";

	public final static String PAR_REPORT_TYPE = "typ";
	public final static String VAL_REPORT_TYPE_ACTIVITY = "act";
	public final static String VAL_REPORT_TYPE_CONCEPT = "con";
	public final static String VAL_REPORT_TYPE_ACTIVITY_TO_CONCEPT = "act2con";
	public final static String VAL_REPORT_TYPE_CONCEPT_TO_ACTIVITY = "con2act";
	
	public final static String PAR_REPORT_APPLICATION = "app";

	public final static String PAR_REPORT_USER = "usr";
	public final static String PAR_REPORT_GROUP = "grp";
	
	public final static String PAR_REPORT_FORMAT = "frm";
	public final static String VAL_REPORT_FORMAT_XML = "xml";
	public final static String VAL_REPORT_FORMAT_DAT = "dat";
	public final static String VAL_REPORT_FORMAT_RDF = "rdf";
	
	public final static String PAR_REPORT_COGLEVEL = "lev";
	public final static String PAR_REPORT_SOCIAL = "soc";
	public final static String PAR_REPORT_DOMAIN = "dom";
	public final static String PAR_REPORT_LIST = "lst";
	public final static String PAR_REPORT_CONCEPT = "con";
	public final static String PAR_REPORT_ACTIVITY = "act";

	public final static String PAR_REPORT_TOKEN = "token";

	
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}

	public void service(HttpServletRequest request, 
		HttpServletResponse response) throws ServletException
	{
		String result = "OK";
		String dir = "";
		String type = "";
		String app_s = "";
		int app_i = -1;
		String user = "";
		String group = "";
		String format = "";
		String level = "";
		String domain = "";
		String list = "";
		String concept = "";
		String activity = "";
		String token = "";
		ServerDaemon sd = ServerDaemon.getInstance(getServletContext());

		long start_ms = System.nanoTime(); //currentTimeMillis();
		long finish_ms = 0;
		
		try
		{// try block
			// Request parameters
			dir = request.getParameter(PAR_REPORT_DIRECTION);
			type = request.getParameter(PAR_REPORT_TYPE);
			app_s = request.getParameter(PAR_REPORT_APPLICATION);
			app_i = -1;
			user = request.getParameter(PAR_REPORT_USER);
			group = request.getParameter(PAR_REPORT_GROUP);
			format = request.getParameter(PAR_REPORT_FORMAT);
			level = request.getParameter(PAR_REPORT_COGLEVEL);
			domain = ServerDaemon.cleanString( request.getParameter(PAR_REPORT_DOMAIN) );
			list = request.getParameter(PAR_REPORT_LIST);
			
			concept = ServerDaemon.cleanString( request.getParameter(PAR_REPORT_CONCEPT) );
			concept = (concept!=null)?URLDecoder.decode(concept,"UTF-8"):concept; 
			
			activity = ServerDaemon.cleanString( request.getParameter(PAR_REPORT_ACTIVITY) );
			activity = (activity!=null)?URLDecoder.decode(activity,"UTF-8"):activity; 
				
			// list is a main param
			if(list == null || list.length() == 0)
				list = activity;
			
			token = ServerDaemon.cleanString( request.getParameter(PAR_REPORT_TOKEN) );

			int level_i = -1;
			if(iProgressEstimator.BLOOM_NAME_KNOWLEDGE.equalsIgnoreCase(level))
				level_i = iProgressEstimator.BLOOM_IDX_KNOWLEDGE;
			else if(iProgressEstimator.BLOOM_NAME_COMPREHENSION.equalsIgnoreCase(level))
				level_i = iProgressEstimator.BLOOM_IDX_COMPREHENSION;
			else if(iProgressEstimator.BLOOM_NAME_APPLICATION.equalsIgnoreCase(level))
				level_i = iProgressEstimator.BLOOM_IDX_APPLICATION;
			else if(iProgressEstimator.BLOOM_NAME_SYNTHESIS.equalsIgnoreCase(level))
				level_i = iProgressEstimator.BLOOM_IDX_SYNTHESIS;
			
			if(dir == null || type == null || //user == null ||
				format == null)
				return;
	
			if(app_s != null)
			{
				Pattern p = Pattern.compile("-?[0-9]+");
				Matcher m = p.matcher("");
				m.reset(app_s);
				if (m.matches())
				{
					app_i = Integer.parseInt(app_s);
				}
				else
					return;
			}
			
			if(VAL_REPORT_TYPE_ACTIVITY.equalsIgnoreCase(type))
			{// User Activity Report
				
				if(VAL_REPORT_DIRECTION_OUT.equalsIgnoreCase(dir))
				{// Get level request
					if(VAL_REPORT_FORMAT_XML.equalsIgnoreCase(format))
					{// XML format
						String report = sd.activityReport(user, group, app_i, list, request);
						
						finish_ms = System.nanoTime(); //currentTimeMillis();
						
						response.setContentType("text/xml");
						PrintWriter out = response.getWriter();
						out.println(report);
						out.close();
						out = null;
					}// end of -- XML format
					else if(VAL_REPORT_FORMAT_DAT.equalsIgnoreCase(format))
					{// Java serialized objects format
						ArrayList reqArray = new ArrayList();
						ArrayList resArray = sd.activityReport(reqArray, 
							user, group, app_i, request);
						
						finish_ms = System.nanoTime(); //currentTimeMillis();
						
						ObjectOutputStream oout = new ObjectOutputStream(
							response.getOutputStream());
						oout.writeObject(resArray);
						oout.flush();
						oout.close();
						oout = null;
						
						resArray.clear();
						resArray = null;
						
						reqArray.clear();
						reqArray = null;
						
					}// end of -- Java serialized objects format
				}// end of -- Get level request
				else if(VAL_REPORT_DIRECTION_IN.equalsIgnoreCase(dir))
				{// Post-Get level request
					if(VAL_REPORT_FORMAT_DAT.equalsIgnoreCase(format))
					{// DAT format
						// receive the request
						ObjectInputStream in = new ObjectInputStream(
							request.getInputStream());
						ArrayList reqArray = null;
						reqArray = (ArrayList)in.readObject();
						in.close();
						
						start_ms = System.nanoTime(); //currentTimeMillis(); // only here the receiving is completed
						
						ArrayList resArray = sd.activityReport(reqArray, 
							user, group, app_i, request);
						
						finish_ms = System.nanoTime(); //currentTimeMillis();
						
						ObjectOutputStream oout = new ObjectOutputStream(
							response.getOutputStream());
						oout.writeObject(resArray);
						oout.flush();
						oout.close();
						oout = null;
						
						resArray.clear();
						resArray = null;
						
						reqArray.clear();
						reqArray = null;
						
					}// end of -- DAT format		
				}// end of -- Post-Get level request
			}// end of -- User Activity Report
	
			if(VAL_REPORT_TYPE_CONCEPT.equalsIgnoreCase(type))
			{// User Concept Report
				if(VAL_REPORT_DIRECTION_OUT.equalsIgnoreCase(dir))
				{// Get level request
					if(VAL_REPORT_FORMAT_XML.equalsIgnoreCase(format))
					{// XML format
						String report = sd.conceptReport(app_i, user, group, level, domain, list, request);
						
						finish_ms = System.nanoTime(); //currentTimeMillis();
						
						response.setContentType("text/xml");
						PrintWriter out = response.getWriter();
						out.println(report);
						out.close();
						out = null;
					}// end of -- XML format
					else if(VAL_REPORT_FORMAT_DAT.equalsIgnoreCase(format))
					{// Java serialized objects format
						ArrayList resArray = sd.conceptReport(null, app_i, user, group,
								level_i, domain, request);
						
						finish_ms = System.nanoTime(); //currentTimeMillis();
						
						ObjectOutputStream oout = new ObjectOutputStream(
							response.getOutputStream());
						oout.writeObject(resArray);
						oout.flush();
						oout.close();
						oout = null;
						
						resArray.clear();
						resArray = null;
					}// end of -- Java serialized objects format
				}// end of -- Get level request
				else if(VAL_REPORT_DIRECTION_IN.equalsIgnoreCase(dir))
				{// Post-Get level request
					if(VAL_REPORT_FORMAT_DAT.equalsIgnoreCase(format))
					{// DAT format
						// receive the request
						ObjectInputStream in = new ObjectInputStream(
							request.getInputStream());
						ArrayList reqArray = null;
						reqArray = (ArrayList)in.readObject();
						in.close();
						
						start_ms = System.nanoTime(); //currentTimeMillis(); // only here the receiving is done
						
						ArrayList resArray = sd.conceptReport(reqArray, app_i, user, group,
							level_i, "<no domain>", request);
						
						finish_ms = System.nanoTime(); //currentTimeMillis();
						
						ObjectOutputStream oout = new ObjectOutputStream(response.getOutputStream());
						oout.writeObject(resArray);
						oout.flush();
						oout.close();
						oout = null;
						
						resArray.clear();
						resArray = null;

						reqArray.clear();
						reqArray = null;
					}// end of -- DAT format		
				}// end of -- Post-Get level request
			}// end of -- User Concept Report
			
			if(VAL_REPORT_TYPE_ACTIVITY_TO_CONCEPT.equalsIgnoreCase(type))
			{// Activity to Concepts Mapping report
				Vector<Activity2ConceptMapping4Report> report = sd.activityMapping(request, app_i, activity, domain );
				finish_ms = System.nanoTime(); //currentTimeMillis();
				response.setContentType("text/xml");
				PrintWriter out = response.getWriter();
				
				if(VAL_REPORT_FORMAT_XML.equalsIgnoreCase(format))
				{// XML format
					out.println(Activity2ConceptMapping4Report.serializeXML(report));
				}// end of -- XML format
				else if(VAL_REPORT_FORMAT_RDF.equalsIgnoreCase(format))
				{// RDF format
					out.println(Activity2ConceptMapping4Report.serializeRDF(report));
				}// end of -- RDF format
				else
				{
					result = "Requested format is not supported";
					out.println("Requested format is not supported");
				}
				
				report.clear();
				report = null;
				out.close();
				out = null;
			}// end of -- Activity to Concepts Mapping report
	
			if(VAL_REPORT_TYPE_CONCEPT_TO_ACTIVITY.equalsIgnoreCase(type))
			{// Concepts to Activity Mapping report
				if(VAL_REPORT_FORMAT_XML.equalsIgnoreCase(format))
				{// XML format
					String report = sd.conceptMappingXML(request, domain, concept, app_i );
					
					finish_ms = System.nanoTime(); //currentTimeMillis();
					
					response.setContentType("text/xml");
					PrintWriter out = response.getWriter();
					out.println(report);
					out.close();
					out = null;
				}// end of -- XML format
				else if(VAL_REPORT_FORMAT_RDF.equalsIgnoreCase(format))
				{// RDF format
					String report = sd.conceptMappingRDF(request, domain, concept, app_i );
					
					finish_ms = System.nanoTime(); //currentTimeMillis();;
					
					response.setContentType("text/xml");
					PrintWriter out = response.getWriter();
					out.println(report);
					out.close();
					out = null;
				}// end of -- RDF format
				else
				{
					finish_ms = System.nanoTime(); //currentTimeMillis();
					result = "Requested format is not supported";
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					out.println("");
					out.close();
					out = null;
				}
			}// end of -- Concepts to Activity Mapping report

		}// end of --  try block
		catch(ClassNotFoundException cnfe)
		{
			result = "ClassNotFoundException";
			finish_ms = System.nanoTime(); //currentTimeMillis();
			cnfe.printStackTrace(System.out);  //*** TEST ONLY
		}
		catch(IOException ioe)
		{
			result = "IOException";
			finish_ms = System.nanoTime(); //currentTimeMillis();
			ioe.printStackTrace(System.out);  //*** TEST ONLY
		}
		

		if(sd.isReportLogging())
		{// if logging report
			Connection conn = null;
			PreparedStatement stmt = null;
			String qry = "INSERT INTO ent_report_log (Dir, Type, Format, App, Token, UserGroup, Result, StartTS, FinishTS, Delay) VALUES" +
					"('" + dir + "','" + type + "','" + format + "'," + app_i + ",'" + token + "','" + user + ":"+ 
					group + "','" + result + "'," + start_ms + "," + finish_ms + "," + (finish_ms-start_ms) + ");";
			try
			{
				conn = ServerDaemon.getConnection();
				stmt = conn.prepareStatement(qry);
				stmt.executeUpdate();
//				ServerDaemon.executeUpdate(conn, qry);
				stmt.close();
				stmt = null;
				conn.close();
				conn = null;
			}
			catch(SQLException sqle)
			{
				sqle.printStackTrace(System.out); //*** TEST ONLY
				System.out.println("!!! [CBUM] ReportManager CANNOT LOG IN DB");
			}
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
		}// end of -- if logging report
	}
	
}
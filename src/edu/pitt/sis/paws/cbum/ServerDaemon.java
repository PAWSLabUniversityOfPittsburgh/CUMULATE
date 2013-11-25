/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/**
 * @author Michael V. Yudelson
 */
package edu.pitt.sis.paws.cbum;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import edu.pitt.sis.paws.cbum.structures.Activity2ConceptMapping4Report;
import edu.pitt.sis.paws.core.utils.SQLManager;

//import edu.pitt.sis.paws.cbum.*;

/**
 * a static field singleton responsible to deploy soncept-based
 * user modelling services and store the central cache of the user model
 */
public final class ServerDaemon 
{
	// Constants
//	public static final String CONTEXT_DB_DRIVER = "driver";
//	public static final String CONTEXT_DB_URL = "url";
//	public static final String CONTEXT_DB_USER = "user";
//	public static final String CONTEXT_DB_PASS = "pass";
////	public static final String CONTEXT_UM_RELAY = "umrelay";

	public static final String CONTEXT_USER_LOCKING = "user_locking";
	public static final String CONTEXT_REPORT_LOGGING = "report_logging";
	public static final String CONTEXT_CUTOUT_DATE = "cutout_date";
	
	
//	// Values
//	protected static String context_db_driver = null;
//	protected static String context_db_url = null;
//	protected static String context_db_user = null;
//	protected static String context_db_pass = null;
////	protected static String context_um_relay = null;
	
	/** singleton's entry point */
	private static ServerDaemon instance = new ServerDaemon();
	/** initialization flag */
	protected static boolean initialized = false;
	/** initialization flag */
	protected static ResourceMap resmap;
	
	private static boolean user_locking;
	private static boolean report_logging;
	private static String cutout_date;
	
	// Managing SQL connections
	protected static SQLManager sql_manager;
	private static final String db_context = "java:comp/env/jdbc/main";
	
	/** private constructor */
	private ServerDaemon() { ; }
	
	public void destroy()
	{
		
	}
	
	/** Initializes the singleton with deployment parameters and load data
	 * from database
	 * @param context - the context of the web application */
	private static void initIt(ServletContext context)
	{
		sql_manager = new SQLManager(db_context);
//		context_db_driver = context.getInitParameter(CONTEXT_DB_DRIVER);
//		context_db_url = context.getInitParameter(CONTEXT_DB_URL);
//		context_db_user = context.getInitParameter(CONTEXT_DB_USER);
//		context_db_pass = context.getInitParameter(CONTEXT_DB_PASS);
//		context_um_relay = context.getInitParameter(CONTEXT_UM_RELAY);
		String s_user_locking = context.getInitParameter(CONTEXT_USER_LOCKING);
		user_locking = (s_user_locking != null && s_user_locking.equals("true"))?true:false;
		
		String s_report_logging = context.getInitParameter(CONTEXT_REPORT_LOGGING);
		report_logging = (s_report_logging != null && s_report_logging.equals("true"))?true:false;
		
		String s_cutout_date = context.getInitParameter(CONTEXT_CUTOUT_DATE);
		cutout_date = (s_cutout_date != null && s_cutout_date.length() >0 )?s_cutout_date:null;
		if(cutout_date!=null)
			System.out.println("... [CBUM] WARNING CUTOUT DATE ACTIVE");

		resmap = new ResourceMap(context);
		System.out.println("... [CBUM] ServerDaemon Inited (locking=" + user_locking + ", report logging=" + report_logging + ")+++");
	}
	
	/** Function returns entry to the singleton
	 * @param context - the context of the web application
	 * @return - entry to the singleton */
	public static ServerDaemon getInstance(ServletContext context)
	{
		if (!initialized)
		{
			initialized = true;
			initIt(context);
		}
		return instance;
	}
	
	/** Returns connection to the Concept-Based User Modelling database
	 * @return - connection to the Concept-Based User Modelling database
	 * @throws Exception - sql or other exception */
	public static Connection getConnection() throws SQLException
	{
		if (instance == null)
			return null;
		return sql_manager.getConnection();
//		Class.forName(context_db_driver).newInstance();
//		return DriverManager.getConnection(context_db_url, context_db_user, 
//			context_db_pass);
	}
	
//	/** Returns connection to an arbitraty database
//	 * @param db_driver - database driver
//	 * @param db_url - database url
//	 * @param db_user - database user
//	 * @param db_pass - database password
//	 * @return - connection to an arbitraty database
//	 * @throws Exception - sql or other exception */
//	public static Connection getArbConnection(String db_driver,
//		String db_url, String  db_user, String db_pass) throws Exception
//	{
//		if (instance == null)
//		return null;
//		Class.forName(db_driver).newInstance();
//		return DriverManager.getConnection(db_url, db_user, db_pass);
//	}
	
//	/** Executes query against the Concept-Based User Modelling database
//	 * @param conn - connection to the Concept-Based User Modelling database
//	 * @param query - database query
//	 * @return - results of the query
//	 * @throws Exception - sql or other exception */
//	public static ResultSet executeStatement(Connection conn, String query) 
//		throws SQLException
//	{
//		PreparedStatement statement = conn.prepareStatement(query);
//		ResultSet resultset = statement.executeQuery();
//		return resultset;
//	}
	
//	/** Executes update against the Concept-Based User Modelling database
//	 * @param conn - connection to the Concept-Based User Modelling database
//	 * @param query - database query
//	 * @throws Exception - sql or other exception */
//	public static void executeUpdate(Connection conn, String query) throws SQLException
//	{
//		PreparedStatement statement = conn.prepareStatement(query);
//		statement.executeUpdate();
//		statement.close();
//	}

	/** Frees the connection and releases the corresponding resources
	 * @param conn - connection to the database */
//	public static void freeConnection(Connection conn)
//	{
//		sql_manager.freeConnection(conn);
////		try
////		{
////			if (conn != null)
////			{
////				conn.close();
////				conn = null;
////			}
////		}
////		catch (Exception e) { e.printStackTrace(System.err); }
//	}

	/** Function adds new User-Activity event to the repository using int Id's
	 * @param _u - Login of the User that fired the event
	 * @param _a - Activity that the event was triggered for
	 * @param _res_s - numeric score assigned to the event */
	public void addNewUserActivityInt(int _app, int _u, int _g, int _a,
			String _res_s, String date_n_time, HttpServletRequest _request)
	{
	//System.out.println("[cbum] daemon (resmap==null):" + (resmap==null)); /// DEBUG
	//System.out.println("[cbum] daemon (_res_s==null):" + (_res_s==null)); /// DEBUG
			resmap.addNewUserActivityInt(_app, _u, _g, _a, _res_s, date_n_time, user_locking, _request);
	}

	public void updateConceptKnowledge(Concept _con, double _score, User _user, User _group, App _app, String date_n_time,
			HttpServletRequest _request)
	{
			resmap.updateConceptKnowledge(_con, _score, _user, _group, _app, date_n_time, user_locking, _request);
	}

	/** Function adds new User-Activity event to the repository using String
	 * 	Id's
	 * @param _u - Login of the User that fired the event
	 * @param _a - Activity that the event was triggered for
	 * @param _res_s - numeric score assigned to the event */
	public void addNewUserActivityStr(String _app, String _u, String _g, String _a,
		String _res_s, String date_n_time, HttpServletRequest _request)
	{
		resmap.addNewUserActivityStr(_app, _u, _g, _a, _res_s, date_n_time, user_locking, _request);
	}

	/** Function prints an activity progress report to a JSP output stream
	 * @param out - JSP output stream 
	 * @param _u - the user Login who's progress is reported
	 * @param _app - application Id
	 * @param _act - Activity name */
	
	public void activityReportSimple(JspWriter out, String _u, int _app, 
		String _act) throws IOException
	{
//System.out.println("### [CBUM] ServerDaemon.activityReportSimple Starting..."); /// DEBUG
		resmap.activityReportSimple(out, _u, _app, _act);	
	}/**/

	/** Function prints an concept progress report to a JSP output stream
	 * @param out - JSP output stream 
	 * @param _u - the user Login who's progress is reported
	 * @param _app - application Id
	 * @param _conc - Concept name 
	 * @param _level - Bloom Level index */
	/*
	public void conceptReportSimple(JspWriter out, String _u, int _app, 
		String _conc, int _level) throws IOException
	{
		resmap.conceptReportSimple(out, _u, _app, _conc, _level);
	}/**/

	/** Function prints a concept progress report to a Servlet output stream
	 * @param out - Servlet output stream 
	 * @param _u - the user Login who's progress is reported
	 * @param _app - application Id
	 * @param _level - Bloom Level(s) */
	public String conceptReport(int _app, String _u, String _g,
		String _level, String _domain, String _list, HttpServletRequest _request) throws IOException
	{
		return resmap.conceptReport(_app, _u, _g, _level, _domain, _list, user_locking, _request);
	}

	/** Function prints a activity progress report to a Servlet output stream
	 * @param out - Servlet output stream 
	 * @param _u - the user Login who's progress is reported
	 * @param _app - application Id */
	public String activityReport(String _u, String _g, int _app, String _list, HttpServletRequest _request)
		throws IOException
	{
		return resmap.activityReport(_u, _g, _app, _list, user_locking, _request);
	}

	/** Function saves an activity progress report to a ArrayList
	 * @param _in - ArrayList with requested activities 
	 * @param _app - application id
	 * @param _u - the user Login who's progress is reported
	 * @return ArrayList populated with activity progress */
	public ArrayList activityReport(ArrayList _in, String _u, String _g, int _app, HttpServletRequest _request)
	{
//System.out.println("[cbum] ServerDaemon.activityReport");
		return resmap.activityReport(_in, _u, _g, _app, user_locking, _request);
	}

	/** Function saves an concepts progress report to a ArrayList
	 * @param _in - ArrayList with requested concepts 
	 * @param _u - the user Login who's progress is reported
	 * @param _app - application id
	 * @param _level - cognitive level of the concept knowledge
	 * @param _domain - domain of concepts (unused)
	 * @return ArrayList populated with concept progress */
	public ArrayList conceptReport(ArrayList _in, int _app, String _u, String _g,
		int _level, String _domain, HttpServletRequest _request)
	{
		return resmap.conceptReport(_in, _app, _u, _g, _level, _domain, user_locking, _request);
	}
	
	/**
	 * @param _id
	 * @param _login
	 * @param _group
	 * @return
	 * @since 1.5
	 */
	public boolean addNewUser(int _id, String _login,String _group)
	{
		boolean result = true;
		result = resmap.addNewUser(_id, _login, _group);
		return result;
	}
	
	public ResourceMap getResourceMap () { return resmap; }
	
	public Activity addNewActivity(int _app_id, String _uri, String _activity, String _desc)
	{
		Connection conn = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		
		int act_id = 0;
		Activity result = null;
		
		try
		{
			// add in DB
			conn = getConnection();
			String qry = "INSERT INTO ent_activity (URI, AppID, Activity, Description)" +
					" VALUES('" + _uri + "'," + _app_id + ",'" + _activity + "',"+
						"'" + _desc + "');";
			stmt1 = conn.prepareStatement(qry);
			stmt1.executeUpdate();
			stmt1.close();
			stmt1 = null;
			
			qry = "SELECT MAX(LAST_INSERT_ID(ActivityID)) AS LastID FROM ent_activity WHERE URI='" + _uri + 
					"' AND Activity='" + _activity + "' AND AppID='" + _app_id + 
					"' AND Description='" + _desc + "';";
			stmt2 = conn.prepareStatement(qry);
			rs = stmt2.executeQuery();
			if(rs.next())
			{
				act_id = rs.getInt("LastID");
			}
			
			rs.close();
			rs = null; 
			stmt2.close();
			stmt2 = null; 
			conn.close();
			conn = null; 
			
			// add in ResMap
			result = new Activity(act_id, _activity, _uri,
					resmap.apps.findById(_app_id));
			resmap.activities.add( result );
			resmap.apps.findById(_app_id).activities.add(result);
			
		}
		catch(Exception e) { e.printStackTrace(System.out); }
		finally
		{
			if (rs != null) 
			{
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (stmt1 != null) 
			{
				try { stmt1.close(); } catch (SQLException e) { ; }
				stmt1 = null;
			}
			if (stmt2 != null) 
			{
				try { stmt2.close(); } catch (SQLException e) { ; }
				stmt2 = null;
			}
			if (conn != null)
			{
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
		
		return result;
	}
	
	public void addActSubActLink(App _app, Activity _act, Activity _sub)
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		try
		{
			// link in DB
			conn = getConnection();
			String qry = "INSERT INTO rel_activity_activity (AppID, ParentActivityID, ChildActivityID)" +
					" VALUES(" + _app.getId() + "," + _act.getId() + "," + _sub.getId() + ");";
			stmt = conn.prepareStatement(qry);
			stmt.executeUpdate();
	
			// link in ResMap
			_sub.setParent(_act);
			_act.getChildren().add(_sub);
//System.out.println("CHECK LINK act=" + _act);			
//System.out.println("CHECK LINK sub=" + _sub);			
//System.out.println("CHECK LINK act->sub=" + ( _act.getId()==_sub.getParent().getId()) );			
//System.out.println("CHECK LINK act.app,sub.app=" + _act.getApp().getId() + "," +_sub.getApp().getId());			
			
			stmt.close();
			stmt = null; 
			conn.close();
			conn = null; 
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
	}
	
	public String conceptMappingXML(HttpServletRequest _request, String _domain, String _concepts, int _app_id)
			throws UnsupportedEncodingException
	{
		return resmap.conceptMappingXML(_request, _domain, _concepts, _app_id);
	}

	public String conceptMappingRDF(HttpServletRequest _request, String _domain, String _concepts, int _app_id)
			throws UnsupportedEncodingException
	{
		return resmap.conceptMappingRDF(_request, _domain, _concepts, _app_id);
	}

	public Vector<Activity2ConceptMapping4Report> activityMapping(HttpServletRequest _request, int _app_id, String _activities, String _domain)
		throws UnsupportedEncodingException
	{
			return resmap.activityMapping(_request, _app_id, _activities, _domain);
	}

	/** "Cleaning" of the string: set empty of null or empty, unchanged otherwise
	 * @param _str string to be cleaned
	 * @return cleaned string
	 */
	public static String cleanString(String _str)
	{
		return (_str != null && _str.length() >0)?_str:"";
	}

	public boolean isReportLogging() { return report_logging; }
	
	public String intrudeActivity(String _user, String _act, String _dom)
	{
		return resmap.intrudeActivity(_user, _act, _dom);
	}
	
}
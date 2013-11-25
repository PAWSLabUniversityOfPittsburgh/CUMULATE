/* Disclaimer:
 * Java code contained in this file is created as part of educational
 * research and development. It is intended to be used by researchers of
 * University of Pittsburgh, School of Information Sciences ONLY.
 * You assume full responsibility and risk of lossed resulting from compiling
 * and running this code.
 */
 
package edu.pitt.sis.paws.cbum.report;

import java.io.*;
import java.net.*;

/**
 * This class is a utility that wrapps an API for sending user activity reports
 * to the user modeling server (UMS).<br />
 * <p>
 * Example:<br />
 * <code>
 * import edu.pitt.sis.paws.cbum.*;<br />
 * ...<br />
 * String ums_url = "http://kt1.exp.sis.pitt.edu:8080/cbum/um";<br />
 * ReportAPI rapi = new ReportAPI(ums_url);<br />
 * int application_id = ReportAPI.APPLICATION_NAVEX;<br />
 * String activity = "helloworld";<br />
 * String subactivity = "line1";<br />
 * String session_id = "s123";<br />
 * double result = 0.5;<br />
 * String user = "joesmith";<br />
 * String group = "2006summer1";<br />
 * String service_param = "";<br />
 * rapi.report(application_id, activity, subactivity, session_id, result, group, user, service_param); <br />
 * </code>
 * 
 * @author	Michael V. Yudelson
 * @since	1.5
 * @version	%I%, %G%
 */
public final class ReportAPI
{
	/**
	 * the default url of the User Modeling Server
	 */
	private static final String UMS_URL_DEFAULT = "http://kt1.exp.sis.pitt.edu:8080/cbum/um";
	
	/**
	 * application ID for an Unknown application
	 */
	public static final int APPLICATION_UNKNOWN = 1;
	
	/**
	 * application ID for QuizPACK
	 */
	public static final int APPLICATION_QUIZPACK = 2;
	
	/**
	 * application ID for WebEx
	 */
	public static final int APPLICATION_WEBEX = 3;
	
	/**
	 * application ID for WADEIn
	 */
	public static final int APPLICATION_WADEIN = 4;
	
	/**
	 * application ID for KnowledgeSea
	 */
	public static final int APPLICATION_KNOWLEDGESEA = 5;
	
	/**
	 * application ID for AnnitatEd
	 */
	public static final int APPLICATION_ANNOTATED = 6;
	
	/**
	 * application ID for Venn
	 */
	public static final int APPLICATION_VENN = 7;
	
	/**
	 * application ID for KnowledgeTree
	 */
	public static final int APPLICATION_KNOWLEDGETREE = 8;
	
	/**
	 * application ID for NavEx
	 */
	public static final int APPLICATION_NAVEX = 9;
	
	/**
	 * application ID for CourseAgent
	 */
	public static final int APPLICATION_COURSEAGENT = 10;
	
	/**
	 * application ID for cWADEIn
	 */
	public static final int APPLICATION_CWADEIN = 11;
	
	/**
	 * application ID for VIBE
	 */
	public static final int APPLICATION_VIBE = 12;
	
	/**
	 * application ID for VirtPresenter
	 */
	public static final int APPLICATION_VIRTPRESENTER = 13;
	
	/**
	 * application ID for Problets
	 */
	public static final int APPLICATION_PROBLETS = 14;
	
	/**
	 * application ID for VarScope
	 */
	public static final int APPLICATION_UVARSCOPE = 15;
	
	/**
	 * application ID for Boolean Tool (by Maram Mecawy)
	 */
	public static final int APPLICATION_BOOLEANTOOL = 16;
	
	/**
	 * application ID for JEliot
	 */
	public static final int APPLICATION_JELIOT = 17;
	
	/**
	 * application ID for QuizGuide
	 */
	public static final int APPLICATION_QUIZGUIDE = 20;
	
	/**
	 * application ID for jWADEIn
	 */
	public static final int APPLICATION_JWADEIN = 21;

	/**
	 * application ID for jWADEIn
	 */
	public static final int APPLICATION_ENSEMBLE = 22;

	/**
	 * application ID for jWADEIn
	 */
	public static final int APPLICATION_SQLKNOT = 23;

	/**
	 * application ID for jWADEIn
	 */
	public static final int APPLICATION_QUIZJET = 25;

	/**
	 * application ID for jWADEIn
	 */
	public static final int APPLICATION_PERSEUS = 24;

	/**
	 * application ID for jWADEIn
	 */
	public static final int APPLICATION_IMPROVE = 26;

	/**
	 * application ID for jWADEIn
	 */
	public static final int APPLICATION_OOPS = 27;

	/**
	 * application ID for jWADEIn
	 */
	public static final int APPLICATION_RICCIONI_READING = 28;

	/**
	 * application ID for jWADEIn
	 */
	public static final int APPLICATION_RICCIONI_QUIZ = 29;

	/**
	 * User Modeling Server URL
	 */
	private String ums_url;

	/**
	 * Default constructor assumes default UMS is used
	 */
	public ReportAPI()
	{
		ums_url = UMS_URL_DEFAULT;
	}

	/**
	 * Constructor that accepts URL of thw UMS as parameter
	 * 
	 * @param _ums_url UMS URL
	 */
	public ReportAPI(String _ums_url)
	{
		if(_ums_url != null && !_ums_url.equals(""))
			ums_url = _ums_url;
		else
			ums_url = UMS_URL_DEFAULT;
	}

	/**
	 * Sends user activity report to User Model via an Http GET request
	 * 
	 * @param _app_id	application id
	 * @param _act		activity id
	 * @param _sub		sub-activity id
	 * @param _sid		session id (issued by application)
	 * @param _res		result (success, falure, partial success)
	 * @param _grp		user group
	 * @param _usr		user login name
	 * @param _svc		service parameter (context, state of the system)
	 * @throws IOException			thrown when general input-output exception occurs
	 * @throws MalformedURLException	thrown when the url specified does not conform to {@link URL} naming convention
	 */
	public void report(int _app_id, String _act, String _sub, String _sid,
		double _res, String _grp, String _usr, String _svc)
		throws IOException, MalformedURLException
	{
		String report_url = ums_url + "?usr=" + _usr +
					"&sid=" + _sid +
					"&app=" + _app_id +
					"&act=" + _act +
					"&sub=" + _sub +
					"&res=" + _res +
					"&grp=" + _grp +
					"&svc=" + _svc;
		URLConnection dbpc = (new URL(report_url)).openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
			dbpc.getInputStream()));
		in.close();

	}

	/**
	 * Sends user activity report to User Model via an Http GET request (NO SUBACTIVITY)
	 * 
	 * @param _app_id	application id
	 * @param _act		activity id
	 * @param _sid		session id (issued by application)
	 * @param _res		result (success, falure, partial success)
	 * @param _grp		user group
	 * @param _usr		user login name
	 * @param _svc		service parameter (context, state of the system)
	 * @throws IOException			thrown when general input-output exception occurs
	 * @throws MalformedURLException	thrown when the url specified does not conform to {@link URL} naming convention
	 */
	public void report(int _app_id, String _act, String _sid,
		double _res, String _grp, String _usr, String _svc)
		throws IOException, MalformedURLException
	{
		String report_url = ums_url + "?usr=" + _usr +
					"&sid=" + _sid +
					"&app=" + _app_id +
					"&act=" + _act +
					"&res=" + _res +
					"&grp=" + _grp +
					"&svc=" + _svc;
		URLConnection dbpc = (new URL(report_url)).openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
			dbpc.getInputStream()));
		in.close();

	}

	/**
	 * Sends user activity report to User Model via an Http GET request, including anonymous
	 * 
	 * @param _app_id	application id
	 * @param _act		activity id
	 * @param _sub		sub-activity id
	 * @param _sid		session id (issued by application)
	 * @param _res		result (success, falure, partial success)
	 * @param _grp		user group
	 * @param _usr		user login name
	 * @param _svc		service parameter (context, state of the system)
	 * @param _ip		IP of the calling party
	 * @throws IOException			thrown when general input-output exception occurs
	 * @throws MalformedURLException	thrown when the url specified does not conform to {@link URL} naming convention
	 */
	public void report(int _app_id, String _act, String _sub, String _sid,
		double _res, String _grp, String _usr, String _svc, String _ip)
		throws IOException, MalformedURLException
	{
		String report_url = ums_url + "?usr=" + _usr +
					"&sid=" + _sid +
					"&app=" + _app_id +
					"&act=" + _act +
					"&sub=" + _sub +
					"&res=" + _res +
					"&grp=" + _grp +
					"&svc=" + _svc +
					"&ip=" + _ip;
		URLConnection dbpc = (new URL(report_url)).openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
			dbpc.getInputStream()));
		in.close();

	}

	
	public String getUMS() { return ums_url; }
	
}

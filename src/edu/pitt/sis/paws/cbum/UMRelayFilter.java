/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/**
 * a relay filter to an external <em>plain</em> UM
 * 
 * @author Michael V. Yudelson
 */
 
package edu.pitt.sis.paws.cbum;

import java.io.*;
import java.net.*;
import javax.servlet.*;

public class UMRelayFilter implements Filter 
{
//	private FilterConfig _filterConfig = null;
	private String umrelay_url = null;

	// Constants
	//	Request parameters
	public static final String REQ_ACTIVITY = "act"; 
	public static final String REQ_SUBACTIVITY = "sub";
	public static final String REQ_SESSION = "sid";
	public static final String REQ_GROUP = "grp";
	public static final String REQ_USER = "usr";
	public static final String REQ_APPLICATION = "app";
	public static final String REQ_SVC = "svc";
	public static final String REQ_RESULT = "res";
	
	public static final String REQ_QUIZ = "quiz";
	public static final String REQ_QUES = "ques";
	public static final String REQ_ANSWERCHECK = "answercheck";

	public void init(FilterConfig filterConfig) throws ServletException
	{
//		_filterConfig = filterConfig;
		// get the Context parameter
		ServletContext context = filterConfig.getServletContext();
		umrelay_url = context.getInitParameter("umrelay");
//System.out.println("UMRelayFilter.init umrelay_url=" + umrelay_url);
	}

	public void destroy()
	{
//		_filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, 
		FilterChain chain) throws IOException, ServletException
	{
//System.out.println("UMRelayFilter.doFilter starting");
		if(umrelay_url != null) // if there's where to relay to
		{
			String req_activity = request.getParameter(REQ_ACTIVITY);
			String req_subactivity = request.getParameter(
				REQ_SUBACTIVITY);
			String req_session = request.getParameter(REQ_SESSION);
			String req_group = request.getParameter(REQ_GROUP);
			String req_user = request.getParameter(REQ_USER);
			String req_application = request.getParameter(
				REQ_APPLICATION);
			String req_svc = request.getParameter(REQ_SVC);
			String req_result = request.getParameter(REQ_RESULT);

			String req_quiz= request.getParameter(REQ_QUIZ);
			String req_ques= request.getParameter(REQ_QUES);
			String req_asnwercheck= request.getParameter(REQ_ANSWERCHECK);
			
			String ACTIVITY = REQ_ACTIVITY;
			String SUBACTIVITY = REQ_SUBACTIVITY;
			String RESULT = REQ_RESULT;
			if((req_activity == null) && (req_subactivity == null) &&
				(req_result == null)) // it's QUIZPACK
			{
//System.out.println("UMRelayFilter.doFilter it's quizpack");
				ACTIVITY = REQ_QUIZ;
				SUBACTIVITY = REQ_QUES;
				RESULT = REQ_ANSWERCHECK;
				req_activity = req_quiz;
				req_subactivity = req_ques;
				req_result = req_asnwercheck;
				req_application = "2";
				request.setAttribute(REQ_ACTIVITY, req_activity);
				request.setAttribute(REQ_SUBACTIVITY, req_subactivity);
				request.setAttribute(REQ_RESULT, req_result);
				request.setAttribute(REQ_APPLICATION, req_application);
				request.removeAttribute(REQ_QUIZ);
				request.removeAttribute(REQ_QUES);
				request.removeAttribute(REQ_ANSWERCHECK);
			}

			URLConnection dbpc = (new URL(umrelay_url + 
				"?kt_user=" + req_user 
				+"&kt_sid=" + req_session + 
				"&app=" + req_application +
				"&" + ACTIVITY + "=" + req_activity + 
				"&" + SUBACTIVITY + "=" + req_subactivity +
				"&" + RESULT + "=" +  req_result +
				"&svc=" +  req_svc +
				"&gid=" +  req_group)).openConnection();
//System.out.println("UMRelayFilter Relaying to: " + dbpc.getURL()); /// DEBUG
			BufferedReader in = new BufferedReader(new InputStreamReader(
				dbpc.getInputStream()));
			in.close();
		}
		chain.doFilter(request, response);
	}
}
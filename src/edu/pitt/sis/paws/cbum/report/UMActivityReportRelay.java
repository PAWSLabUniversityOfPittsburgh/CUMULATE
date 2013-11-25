package edu.pitt.sis.paws.cbum.report;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Servlet is to be used as a relay between a local applet and a remote UM server
 */
 
public class UMActivityReportRelay extends HttpServlet
{
	static final long serialVersionUID = -2L;
	
	/**
	 * Context parameter name in web.xml that stores the remote UM url
	 */
	private static final String UM_URL = "umrelay";
	
	/**
	 * Context parameter value that stores the remote UM url
	 */
	private String um_url;
	

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		um_url = getServletContext().getInitParameter(UM_URL);
	}

	public void doGet(HttpServletRequest request, 
			 HttpServletResponse response) throws ServletException, IOException
	{
		// read incoming parameters
		int app_id = Integer.parseInt(request.getParameter("app"));
		String act = request.getParameter("act");
		String sub = request.getParameter("sub");
		String sid = request.getParameter("sid");
		double res = Double.parseDouble(request.getParameter("res"));
		String grp = request.getParameter("grp");
		String usr = request.getParameter("usr");
		String svc = request.getParameter("svc");
//		String ip = request.getParameter("ip");
		
		// relay request to remote UM server
		ReportAPI r_api = new ReportAPI(um_url);
		r_api.report(app_id, act, sub, sid, res, grp, usr, svc);
	
	}
}

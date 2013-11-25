package edu.pitt.sis.paws.cbum;

import javax.servlet.ServletException;

/**
 * Servlet implementation class for Servlet: ServletDaemonStarter
 *
 */
 public class ServletDaemonStarter extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
 {
	 static final long serialVersionUID = 2L;
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ServletDaemonStarter()
	{
		super();
	}   	 	  	  	  
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException
	{
		super.init();
		ServerDaemon.getInstance(getServletContext());
	}  
	
	public void destroy()
	{
		super.destroy();
	}  
	
}
package edu.pitt.sis.paws.cbum;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;

public class conceptReport extends HttpServlet 
{
	static final long serialVersionUID = -2L;
	
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}

	public void doGet(HttpServletRequest request, 
		HttpServletResponse response) throws ServletException, IOException
	{
		/* App */
		String app = null;
		/* User Login */
		String user_login = null;
		/* Group Login */
		String group_login = null;
		/* Bloom Levels */
		String levels = null;
		try
		{
			app = request.getParameter("app");
			user_login = request.getParameter("u");
			group_login = request.getParameter("g");
			levels = request.getParameter("lev");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
 
		PrintWriter out = response.getWriter();
		response.setContentType("text/xml; charset=utf-8");
		ServerDaemon sd = ServerDaemon.getInstance(getServletContext());
		out.println( sd.conceptReport(Integer.parseInt(app), user_login, group_login, levels, null, null, request) );
		out.close();
	}

	public void doPost(HttpServletRequest request, 
		HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}
}
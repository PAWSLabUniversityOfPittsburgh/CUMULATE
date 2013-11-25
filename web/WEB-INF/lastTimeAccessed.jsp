<%@ page contentType="text/html;charset=windows-1252"
	language="java" 
	import="edu.pitt.sis.paws.cbum.*, java.sql.*"  %>
<%out.println("<?xml version = '1.0' encoding = 'windows-1252'?>");
	out.println("<report>");

//	SELECT u.Login, g.UserID AS GroupID, ua.AppID, ua.SVC, ua.Result, MAX(ua.DateNTime)
//	FROM ent_user_activity ua LEFT OUTER JOIN ent_user u ON (u.UserID=ua.UserID)
//	LEFT OUTER JOIN ent_user g ON (g.UserID=ua.GroupID)
//	WHERE u.Login = 'myudelson' AND g.Login = '200621' AND ua.AppID=3 AND ua.SVC NOT LIKE '%SS%'
//	AND ua.DateNTime<'2006-02-07 11:11:11.000' AND Result=-1
//	GROUP BY u.Login;

	String user = request.getParameter("usr");
	String group = request.getParameter("grp");
	String app = request.getParameter("app");
	String svc = request.getParameter("svc");
	String date = request.getParameter("dtt");
	String result = request.getParameter("res");

//	ServerDaemon sd = ServerDaemon.getInstance(application);
	Connection conn = ServerDaemon.getConnection();
	String app_clause = (app==null)?"":"AND ua.AppID=" + app + " ";
	String svc_clause = (svc==null)?"":"AND ua.SVC NOT LIKE '%" + svc + "%' ";
	String date_clause = (date==null)?"":"AND ua.DateNTime<'" + 
		date.substring(0,4) + "-" + date.substring(4,6) + "-" +
		date.substring(6,8) + " " + date.substring(8,10) + ":" +  
		date.substring(10,12) + ":" +  date.substring(12,14) + ".000' ";
	String result_clause = (result==null)?"":"AND Result=" + result + " ";
	
	String qry = "SELECT u.Login, g.UserID AS GroupID, ua.AppID, ua.SVC, ua.Result, MAX(ua.DateNTime) as Date " +
		"FROM ent_user_activity ua LEFT OUTER JOIN ent_user u ON (u.UserID=ua.UserID) " +
		"LEFT OUTER JOIN ent_user g ON (g.UserID=ua.GroupID) " +
		"WHERE u.Login = '" + user + "' AND g.Login = '" + group + "' " + 
		app_clause + svc_clause + date_clause + result_clause +
		"GROUP BY u.Login;"; 

///System.out.println(qry);
	PreparedStatement stmt = conn.prepareStatement(qry);
	ResultSet rs = stmt.executeQuery();
	while(rs.next())
	{
		date = rs.getString("Date");
		out.println("\t<user>" + user + "</user>");
		out.println("\t<last-date-accessed>" + date + "</last-date-accessed>");
	}
	rs.close();
	rs = null;
	stmt.close();
	stmt = null;
	out.println("</report>");
%>

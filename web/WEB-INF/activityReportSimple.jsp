<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"
	language="java" 
	import="edu.pitt.sis.paws.cbum.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252"></meta>
<%!
	// Declare variables
	String s_user = "";
	String s_app = "";
	int i_app = 0;
	String s_act = "";
%>
<%
	// Get PArameter values
	s_user = request.getParameter("u");
	s_app = request.getParameter("app");
	s_act = request.getParameter("act");
	i_app = Integer.parseInt(s_app);
%>
<title>Simple Activity Report</title>
</head>
<body>
<%
	ServerDaemon sd = ServerDaemon.getInstance(application);
	sd.activityReportSimple(out, s_user, i_app, s_act);
%>
</body>
</html>
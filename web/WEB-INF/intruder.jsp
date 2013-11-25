<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="edu.pitt.sis.paws.cbum.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>CUMULATE Intruder</title>
</head>
<body>
<%!
	String req_activity = null;
	String req_domain = null;
	String req_user = null;
	String result = "";
%>
<%
	req_activity = request.getParameter("activity");
	req_domain = request.getParameter("domain");
	req_user = request.getParameter("user");
	if(req_activity!=null && req_activity.length()>0 && req_domain!=null && req_domain.length()>0
			&& req_user!=null && req_user.length()>0)
	{
		ServerDaemon sd = ServerDaemon.getInstance(application);
		result = sd.intrudeActivity(req_user, req_activity, req_domain);
	}
%>
<h1>CUMULATE Intruder, Use it Wisely</h1>
<form action="<%=request.getContextPath()%>/the/intruder" method="get">
<table cellpadding="0" cellspacing="2" width="400">
<tr><td>User</td><td width="100%"><input name="user" type="text" value="<%=req_user%>" size="50" maxlength="50" width="100%" /></td></tr>
<tr><td>Activity/URI</td><td width="100%"><input name="activity" type="text" value="<%=req_activity%>" size="50" maxlength="256" width="100%" /></td></tr>
<tr><td>Domain</td><td width="100%"><input name="domain" type="text" value="<%=req_domain%>" size="50" maxlength="50" width="100%" /></td></tr>
<tr><td>&nbsp;</td><td width="100%"><input type="submit" /></td></tr>
</table>
</form>
<pre>
<%=result%>
</pre>
</body>
</html>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" 
	language="java" 
	import="java.util.*" 
	errorPage="" %>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon" href="assets/favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="assets/favicon.ico" type="image/x-icon" /> 
<title>Portal for Adaptive Teaching and Learning. Authentication</title>
<script type="text/javascript">
<!--
	if (top != self) 
		top.location.href = self.location.href;
-->
</script>
</head>

<body>
<%
	//	ClientDaemon cd = (ClientDaemon)ClientDaemon.getInstance(application);
	Enumeration enu = session.getAttributeNames();
	for(;enu.hasMoreElements();)
		session.removeAttribute((String)enu.nextElement());
%>
<form action="j_security_check" method="post">
	<table border="0" cellspacing="2" cellpadding="2" align="center">
		<tr> 
			<td width="50" style="color:#FF0000;">Login</td>
			<td width="150"><input id="j_username" name="j_username" type="text" value="" size="25" maxlength="15" /></td>
		</tr>
		<tr> 
			<td style="color:#FF0000;">Password</td>
			<td><input id="j_password" name="j_password" type="password" value="" size="25" maxlength="15" /></td>
		</tr>
		<tr> 
			<td><input type="reset" value="Reset" /></td>
			<td align="right"><input type="submit" value="Login" /></td>
		</tr>
	</table>
</form>

<script type="text/javascript">
	document.getElementById("j_username").focus();
</script>
</body>
</html>



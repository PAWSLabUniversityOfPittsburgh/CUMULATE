package edu.pitt.sis.paws.cbum2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

import edu.pitt.sis.paws.core.utils.SQLManager;

public class ServerDaemon
{
	private static ServerDaemon instance = new ServerDaemon();
	
	private SQLManager sqlManager;
	
	private ResourceMap resourceMap;
	
	private ServerDaemon()
	{
		Calendar start = null;
		Calendar finish = null;
		Date start_d = null;
		Date finish_d = null;
		start = new GregorianCalendar();
		start_d = new Date();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		System.out.println("### [CBUM] ServerDaemon starting " + formatter.format(start_d));

		sqlManager = new SQLManager("java:comp/env/jdbc/cbum");
		resourceMap = new ResourceMap(sqlManager);

		finish = new GregorianCalendar();
		finish_d = new Date();
		System.out.println("### [CBUM] ServerDaemon started " + formatter.format(finish_d));
		System.out.println("### [CBUM] ServerDaemon --- time to start " + 
				(start.getTimeInMillis() - finish.getTimeInMillis()) + "ms");
	}// end of -- ServerDaemon
	
	public static ServerDaemon getInstance() { return instance; }

	public SQLManager getSqlManager() { return sqlManager; }
	
	public static void main(String[] args)
	{
		ServerDaemon sd = ServerDaemon.getInstance();
		sd = ServerDaemon.getInstance();
	}
}

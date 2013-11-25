package edu.pitt.sis.paws.cbum.structures;

import java.util.Vector;

import edu.pitt.sis.paws.cbum.User;

public class ActivityReport
{
	public User user;
	public User group;
	public Vector<ActivityReportItem> ar_items;
	
	public ActivityReport(User _user, User _group)
	{
		user = _user;
		group = _group;
		ar_items = new Vector<ActivityReportItem>();
	}
	
}




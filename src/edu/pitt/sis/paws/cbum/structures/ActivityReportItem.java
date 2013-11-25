package edu.pitt.sis.paws.cbum.structures;

import java.util.Vector;
import edu.pitt.sis.paws.cbum.Activity;

public class ActivityReportItem
{
	public Activity activity;
	public int ind_count;
	public double ind_progress;
	public double grp_count;
	public double grp_progress;
	public Vector<ActivityReportItem> sub_activities;
	
	public ActivityReportItem(Activity _act)
	{
		activity = _act;
		ind_count = 0;
		ind_progress = 0;
		grp_count = 0;
		grp_progress = 0;
		sub_activities = new Vector<ActivityReportItem>();
	}
	
}
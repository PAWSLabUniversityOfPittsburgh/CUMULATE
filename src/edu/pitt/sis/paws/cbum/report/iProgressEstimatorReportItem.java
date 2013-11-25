package edu.pitt.sis.paws.cbum.report;

import java.io.Serializable;
import java.util.ArrayList;

import edu.pitt.sis.paws.cbum.UserProgressEstimator;

public interface iProgressEstimatorReportItem extends Serializable
{
	public String getId();
	public void setId(String _id);
	
	public int getCount(int _level);
	public void setCount(int _count, int _level);
//	public void setCounts(UserProgressEstimator _upa, int _level);
	
	public double getGroupCount(int _level);
	public void setGroupCount(double _group_count, int _level);
//	public void setGroupCounts(UserProgressEstimator _upe, int _level);
	
	public double getProgress(int _level);
	public void setProgress(double _progress, int _level);
//	public void setProgresses(UserProgressEstimator _upe, int _level);
	
	public double getGroupProgress(int _level);
	public void setGroupProgress(double _group_progress, int _level);
//	public void setGroupProgresses(UserProgressEstimator _upe, int _level);
	
	public void setKnowledgeInfo(UserProgressEstimator _upe, ArrayList<UserProgressEstimator> _peer_upe, int _level);
//	public void setActivityInfo(UserActivity _ua, ArrayList<UserActivity> _peer_ua);
	
	public ArrayList getSubs();	
	public void addSub(iProgressEstimatorReportItem _new_sub);	
}

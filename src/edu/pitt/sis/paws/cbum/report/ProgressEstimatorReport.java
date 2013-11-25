/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */

/** This is an item of the Progress report
 * @author Michael V. Yudelson */

package edu.pitt.sis.paws.cbum.report;

import java.io.Serializable;
import java.util.ArrayList;

import edu.pitt.sis.paws.cbum.UserActivity;
import edu.pitt.sis.paws.cbum.UserProgressEstimator;

public class ProgressEstimatorReport extends ProgressEstimatorReportItem implements Serializable, iProgressEstimatorReportItem
{
	static final long serialVersionUID = -23L;
		
	/** Counter of the individual actions on the reported entity*/
	private int count;

	/** individual progress on the reported entity*/
	private double progress;

	/** Mean counter of the group actions on the reported entity*/
	private double count_g;

	/** Mean group progress on the reported entity*/
	private double progress_g;

	/** Progress reports on the subordinate entities*/
	private ArrayList/*<ProgressEstimatorReport>*/ subs;

	public ProgressEstimatorReport(String _id)
	{
		super(_id);
		count = 0;
		progress = 0;
		count_g = 0;
		progress_g = 0;
		subs = new ArrayList/*<ProgressEstimatorReport>*/();
	}

//	public ProgressEstimatorReport(String _id, int _count, double _progress)
//	{
//		super(_id);
//		count = _count;
//		progress = _progress;
//		count_g = 0;
//		progress_g = 0;
//		subs = new ArrayList/*<ProgressEstimatorReport>*/();
//	}

//	public ProgressEstimatorReport(String _id, int _count, double _progress, double _count_g, double _progress_g)
//	{
//		super(_id);
//		count = _count;
//		progress = _progress;
//		count_g = _count_g;
//		progress_g = _progress_g;
//		subs = new ArrayList/*<ProgressEstimatorReport>*/();
//	}

	/** String representation */
	public String toString()
	{
		return "[ProgressEstimatorReport activity:'" + this.getId() + "' ind.progress:" + 
			progress + " ind.count:" + count + " grp.progress:" + progress_g + 
			" grp.count:" + count_g + "]";
	}
	

	public int getCount(int _level) { return count; }

	public double getGroupCount(int _level) { return count_g; }

	public double getGroupProgress(int _level) { return progress_g; }

	public double getProgress(int _level) { return progress; }

	public void setCount(int _count, int _level) { count = _count; }

	public void setGroupCount(double _group_count, int _level) { count_g = _group_count; }

	public void setGroupProgress(double _group_progress, int _level) { progress_g = _group_progress; }

	public void setProgress(double _progress, int _level) { progress = _progress; }

	public ArrayList getSubs() { return subs; }
	
	public void addSub(iProgressEstimatorReportItem _new_sub) { subs.add(_new_sub); }

	
	public void setKnowledgeInfo(UserProgressEstimator _upe, ArrayList<UserProgressEstimator> _peer_upe, int _level)
	{
		count = _upe.getKnowledgeLevels()[_level].getCount();
		progress = _upe.getKnowledgeLevels()[_level].getProgress();
		
		double counts = 0;
		double pregresses = 0;
		for(int i=0; i<_peer_upe.size(); i++)
		{
			counts += _peer_upe.get(i).getKnowledgeLevels()[_level].getCount();
			pregresses += _peer_upe.get(i).getKnowledgeLevels()[_level].getProgress();
		}
		count_g = counts/_peer_upe.size();
		progress_g = pregresses/_peer_upe.size();
	}

	public void setActivityInfo(UserActivity _ua, ArrayList<UserActivity> _peer_ua)
	{
		count = _ua.getCount();
		progress = _ua.getProgress();
		
		double counts = 0;
		double pregresses = 0;
		for(int i=0; i<_peer_ua.size(); i++)
		{
			counts += _peer_ua.get(i).getCount();
			pregresses += _peer_ua.get(i).getProgress();
		}
		count_g = counts/_peer_ua.size();
		progress_g = pregresses/_peer_ua.size();
	}

}
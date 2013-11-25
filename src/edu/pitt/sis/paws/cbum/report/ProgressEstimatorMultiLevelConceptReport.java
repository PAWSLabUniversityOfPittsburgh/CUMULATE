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
import edu.pitt.sis.paws.cbum.iProgressEstimator;

public class ProgressEstimatorMultiLevelConceptReport extends ProgressEstimatorReportItem implements iProgressEstimatorReportItem, Serializable
{
	static final long serialVersionUID = -22L;
		
	/** Counter of the individual actions on the reported entity*/
	private int count[];

	/** individual progress on the reported entity*/
	private double progress[];

	/** Mean counter of the group actions on the reported entity*/
	private double count_g[];

	/** Mean group progress on the reported entity*/
	private double progress_g[];

	/** Progress reports on the subordinate entities*/
	private ArrayList/*<ProgressEstimatorReport>*/ subs;

	public ProgressEstimatorMultiLevelConceptReport(String _id)
	{
		super(_id);
		count = new int[iProgressEstimator.BLOOM_COUNT];
		progress = new double[iProgressEstimator.BLOOM_COUNT];
		count_g = new double[iProgressEstimator.BLOOM_COUNT];
		progress_g = new double[iProgressEstimator.BLOOM_COUNT];
		subs = new ArrayList/*<ProgressEstimatorReport>*/();
		
		for(int i=0; i<iProgressEstimator.BLOOM_COUNT; i++)
		{
			count[i] = 0;
			progress[i] = 0;
			count_g[i] = 0;
			progress_g[i] = 0;
		}
	}

	/** String representation */
	public String toString()
	{
		return "[ProgressEstimatorReport activity:'" + this.getId() + "' ind.progress:" + 
			progress + " ind.count:" + count + " grp.progress:" + progress_g + 
			" grp.count:" + count_g + "]";
	}
	
	public int getCount(int _level) { return count[_level]; }

	public double getGroupCount(int _level) { return count_g[_level]; }

	public double getGroupProgress(int _level) { return progress_g[_level]; }

	public double getProgress(int _level) { return progress[_level]; }

	public void setCount(int _count, int _level) { count[_level] = _count; }

	public void setGroupCount(double _group_count, int _level) { count_g[_level] = _group_count; }

	public void setGroupProgress(double _group_progress, int _level) { progress_g[_level] = _group_progress; }

	public void setProgress(double _progress, int _level) { progress[_level] = _progress; }

	public ArrayList getSubs() { return subs; }
	
	public void addSub(iProgressEstimatorReportItem _new_sub) { subs.add(_new_sub); }

	public void setKnowledgeInfo(UserProgressEstimator _upe, ArrayList<UserProgressEstimator> _peer_upe, int _level)
	{
		for(int i=0; i<iProgressEstimator.BLOOM_COUNT; i++)
		{
//System.out.println("i="+i+" bCount="+iProgressEstimator.BLOOM_COUNT+" c.len="+count.length);
			count[i] = _upe.getKnowledgeLevels()[i].getCount();
			progress[i] = _upe.getKnowledgeLevels()[i].getProgress();
		}
		
		double counts[] = new double[iProgressEstimator.BLOOM_COUNT];
		double pregresses[] = new double[iProgressEstimator.BLOOM_COUNT];
		for(int j=0; j<iProgressEstimator.BLOOM_COUNT; j++)
		{
			for(int i=0; i<_peer_upe.size(); i++)
			{
				counts[j] += _peer_upe.get(i).getKnowledgeLevels()[j].getCount();
				pregresses[j] += _peer_upe.get(i).getKnowledgeLevels()[j].getProgress();
			}
		}
		for(int j=0; j<iProgressEstimator.BLOOM_COUNT; j++)
		{
			count_g[j] = counts[j]/_peer_upe.size();
			progress_g[j] = pregresses[j]/_peer_upe.size();
		}
	}

	/** not applicable for progress */
	public void setActivityInfo(UserActivity _ua, ArrayList<UserActivity> _peer_ua)
	{
		return;
	}

}
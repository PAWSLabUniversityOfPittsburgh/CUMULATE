/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/** Class wraps a  score/count link between an activity for and a particular
 * user the link can go in only one way. In case of User-Activity link 
 * <em>id</em> and <em>title</em> should be used those of User, in case of 
 * Activity-user link those of Activity should be used.
 * @author Michael V. Yudelson
 */

package edu.pitt.sis.paws.cbum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.pitt.sis.paws.cbum.report.ReportAPI;
import edu.pitt.sis.paws.cbum.structures.ActivityReportItem;
import edu.pitt.sis.paws.core.Item2;

public class UserActivity extends Item2
{
	static final long serialVersionUID = 4445L;
	
//	//Constants
//	public static final String UA_TOP_ELEMENT_USER = "U"; 
//	public static final String UA_TOP_ELEMENT_ACTIVITY = "A"; 
	
	/** User */
	protected User user;
	/** Activity */
	protected Activity activity;
//	/** Flag identifying the top element of the link: User or Activity */
//	protected String top_element;
	/** Progress level Estimator */
	protected iProgressEstimator progress_estimator;
	
	public UserActivity(int _id, String _title, User _user,
		Activity _activity/*, boolean top_element_user*/)
	{
		super(_id, _title, "no uri");
		user = _user;
		activity = _activity;
//		top_element = (top_element_user) ? UA_TOP_ELEMENT_USER :
//			UA_TOP_ELEMENT_ACTIVITY;

//		double fixed_sum_weights = (_activity.getChildren().size()>0)?
//			_activity.getChildren().size():1;
//		progress_estimator = new MeanEstimator(1, fixed_sum_weights);
		
//		double fixed_sum_weights = (_activity.getChildren().size()>0)?
//			_activity.getChildren().size():1;
		if(activity!=null && 
				(activity.app.getId() == ReportAPI.APPLICATION_ANNOTATED || activity.app.getId() == ReportAPI.APPLICATION_ENSEMBLE) )
		{
			progress_estimator = new TSREstimator(1, 1);
		}
		else
			progress_estimator = new MeanEstimator(1, 1);
	}
	
	public String toString()
	{
		return "[User-Activity link title:'" + this.getTitle() + "' id:" + this.getId() + "]";
	}
	
	//-- Setters and getters
	/** Getter for the User element of the User-Activity Link
	 * @return User element of the User-Activity Link
	 */
	public User getUser() { return user; }
	/** Getter for the Activity element of the User-Activity Link
	 * @return Activity element of the User-Activity Link
	 */
	public Activity getActivity() { return activity; }

	//-- Other Methods
	/** Function adds new scaled to the User-Activity link whapper
	 * @param _score - the score being added
	 */
	public void addScore(double _score, String date_n_time)
	{
//System.out.println("UA.addScore: user="+user.getTitle() + " activity=" + activity.getTitle() + " date_n_time=" + date_n_time);

		//check cache
		if(user.cached_activity != null && user.cached_datentime != null && 
				( (_score == 0.0 && user.cached_activity.activity.app.getId() == ReportAPI.APPLICATION_ANNOTATED) ||
				  (_score == -1  && user.cached_activity.activity.app.getId() == ReportAPI.APPLICATION_ENSEMBLE) 
				) 
		  )
		{
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			long ms_passed = 0;
			try
			{
				Date old_date = df.parse(user.cached_datentime);
				Date new_date = df.parse(date_n_time);
				ms_passed = new_date.getTime() - old_date.getTime();
			}
			catch (Exception e) { e.printStackTrace(System.out); }
			double new_score = 0.0;
			
			if(ms_passed>6000 && ms_passed<=65000)
				new_score = (double)Math.log((10*ms_passed)/((double)(65000))); //log(10*TSR/65)
			else if (ms_passed>65000 && ms_passed<=600000)
				new_score = 1;
//System.out.println("UA.addScore: user="+user.getTitle() + 
//		" activity=" + user.cached_activity.activity.getTitle() + " ms_pass=" + ms_passed + 
//		" new_score=" + new_score);

			user.cached_activity.progress_estimator.addProgress(new_score, 0, 0, date_n_time);
		}
		
		if(activity.app.getId() != ReportAPI.APPLICATION_ANNOTATED)
			progress_estimator.addProgress(_score, 0, 0, date_n_time);

		//cache
		user.cached_activity = this;
		user.cached_datentime = date_n_time;

	}

	/** Function returns the score of the User for the Activity
	 * @return - the score of the User for the Activity */
	public double getProgress()
	{
		return progress_estimator.getProgress();
	}

	/** Function returns the score of the User for the Activity
	 * @return - the score of the User for the Activity */
	public int getCount()
	{
		return progress_estimator.getCount();
	}

	/** Function prints a user activity progress report to a Servlet output 
	 * 	stream
	 * @param out - Servlet output stream 
	 * @param _prefix - identation for XML
	 * @param _group - whether to include group statistics too */
	public String progressReport(String _prefix,
		boolean _group)
	{// activityReport
		String result = "";
		result += _prefix + "<individual>\n";
		result += _prefix + "\t" + "<count>" + 
			progress_estimator.getCount() + "</count>\n";
		result += _prefix + "\t" + "<progress>" + 
			progress_estimator.getProgress() + "</progress>\n";
		result += _prefix + "</individual>\n";
		if(_group)
		{
			;
		}
		return result;	
	}// activityReport

	/** Function prints a user activity progress report to a Servlet output 
	 * 	stream
	 * @param out - Servlet output stream 
	 * @param _prefix - identation for XML
	 * @param _group - whether to include group statistics too */
	public void progressReportStruc(ActivityReportItem _repitem)
	{// activityReport
		_repitem.ind_count = progress_estimator.getCount();
		_repitem.ind_progress = progress_estimator.getProgress();
//		String result = "";
//		result += _prefix + "<individual>\n";
//		result += _prefix + "\t" + "<count>" + 
//			progress_estimator.getCount() + "</count>\n";
//		result += _prefix + "\t" + "<progress>" + 
//			progress_estimator.getProgress() + "</progress>\n";
//		result += _prefix + "</individual>\n";
//		if(_group)
//		{
//			;
//		}
//		return result;	
	}// activityReport

	/** Function prints an empty defaul user activity progress report to a
	 * 	Servlet output stream
	 * @param out - Servlet output stream 
	 * @param _prefix - identation for XML
	 * @param _group - whether to include group statistics too */
	public static String progressReportDefault(String _prefix,
		boolean _group)
	{// activityReport
		String result = "";
		result += _prefix + "<individual>\n";
		result += _prefix + "\t" + "<count>0</count>\n";
		result += _prefix + "\t" + "<progress>0.0</progress>\n";
		result += _prefix + "</individual>\n";
		if(_group)
		{
			result += _prefix + "<group>\n";
			result += _prefix + "\t" + "<count>0</count>\n";
			result += _prefix + "\t" + "<progress>0.0</progress>\n";
			result += _prefix + "</group>\n";
		}
		return result;	
	}// activityReport

}
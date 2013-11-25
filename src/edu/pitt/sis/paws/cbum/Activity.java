/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */

/** Class representing a single User or a Group of users
 * @author Michael V. Yudelson */

package edu.pitt.sis.paws.cbum;

import edu.pitt.sis.paws.cbum.report.iProgressEstimatorReportItem;
import edu.pitt.sis.paws.cbum.structures.Activity2ConceptMapping4Report;
import edu.pitt.sis.paws.cbum.structures.ActivityReportItem;
import edu.pitt.sis.paws.core.Item2;
import edu.pitt.sis.paws.core.Item2Vector;

public class Activity extends Item2
{
	static final long serialVersionUID = 2L; 
	/** Application activity is registered with */
	protected App app;
	/** A poiter to the superordinate activity */
	protected Activity parent;
	/** Array of subordinate activities */
	protected Item2Vector<Activity> children;
	/** An array of links to Users and count/average scores */
	protected Item2Vector<UserActivity> user_links;
//	/** An array of links to Groups and count/average scores */
//	protected Item2Vector<UserActivity> group_links;
	/** An array of links to Concepts */
	protected Item2Vector<ConceptActivity> concept_links;
	/** Sum of the prerequisite Concepts' weights */
	protected double prereq_w_sum;
	/** Sum of the outcome Concepts' weights */
	protected double outcome_w_sum;

	public Activity(int _id, String _activity, String _uri, App _app)
	{
		super(_id, _activity, _uri);
		app = _app;
		parent = null;
		children = new Item2Vector<Activity>();
		user_links = new Item2Vector<UserActivity>();
//		group_links = new Item2Vector<UserActivity>();
		concept_links = new Item2Vector<ConceptActivity>();
		prereq_w_sum = 0;
		outcome_w_sum = 0;
	}

	public String toString()
	{
		return "[Activity ext-l id:'" + this.getTitle() + "' int-l id:" + this.getId() + "]";
	}

	//-- Setters and Getters
	/** Getter for the Application activity is registered with
	 * @return - the Application activity is registered with */
	public App getApp() { return app; }

	/** Getter for the superordinate activity
	 * @return - the superordinate activity */
	public Activity getParent() { return parent; }

	/** Setter for the superordinate Activity
	 * @param _parent -  new superordinate Activity */
	public void setParent(Activity _parent) { parent = _parent; }

	/** Getter for an array of subordinate Activities
	 * @return - an array of subordinate Activities */
	public Item2Vector<Activity> getChildren() { return children; }

	/** Getter for an array of User links
	 * @return - an array of User links */
	public Item2Vector<UserActivity> getUserLinks()
	{
		return user_links;
	}

//	/** Getter for an array of Group links
//	 * @return - an array of Group links */
//	public Item2Vector<UserActivity> getGroupLinks()
//	{
//		return group_links;
//	}

	/** Getter for an array of Concept links
	 * @return - an array of Concept links */
	public Item2Vector<ConceptActivity> getConceptLinks()
	{
		return concept_links;
	}

	/** Getter for the sum of the prerequisite Concepts' weights
	 * @return - sum of the prerequisite Concepts' weights */
	public double getPrereqWeightSum() { return prereq_w_sum; }

	/** Getter for the sum of the outcome Concepts' weights
	 * @return - sum of the outcome Concepts' weights */
	public double getOutcomeWeightSum() { return outcome_w_sum; }

	//-- Other Methods
	/** Retrieves the sum of the prerequisite Concepts' weights for a specific domain
	 * @return - the sum of the prerequisite Concepts' weights for a specific domain */
	public double getPrereqWeightSum(Concept _domain)
	{
		if(_domain==null)
			return prereq_w_sum;
		double weight_sum = 0;
		for(int i=0; i<concept_links.size(); i++)
		{
			if(concept_links.get(i).getDirection() == ConceptActivity.DIR_PREREQ &&
			concept_links.get(i).getConcept().getDomain().getId() == _domain.getId())
			{
//if(concept_links.get(i).getConcept().getId()==190)		
				weight_sum += concept_links.get(i).getWeight();
			}
//if(this.getId()==970)		
//{
//	System.out.println("@@@ a:"+this.getTitle()+" c:"+concept_links.get(i).getConcept().getTitle() + " dir=" + concept_links.get(i).getDirection() +
//		" curr_weight_sum=" + weight_sum+ " dom=" + concept_links.get(i).getConcept().getDomain().getId() + " vs " + _domain.getId());		
//}
		}
		return weight_sum;
	}

	/** Retrieves the sum of the outcome Concepts' weights for a specific domain
	 * @return - the sum of the outcome Concepts' weights for a specific domain */
	public double getOutcomeWeightSum(Concept _domain)
	{
		if(_domain==null)
			return outcome_w_sum;
		double weight_sum = 0;
//if(this.getId()==970)		
//System.out.println("--- Activity.getOutcomeWeightSum 1, o+p = " + concept_links.size() + " outcome_w_sum=" +outcome_w_sum);
	
		for(int i=0; i<concept_links.size(); i++) 
		{
			if((concept_links.get(i).getConcept().getDomain().getId() == _domain.getId() && 
				(concept_links.get(i).getDirection() == ConceptActivity.DIR_OUTCOM)))
			{
	

//if(concept_links.get(i).getConcept().getId()==190)		
				weight_sum += concept_links.get(i).getWeight();
//if(this.getId()==970)		
//{
//	System.out.println("@@@ a:"+this.getTitle()+" c:"+concept_links.get(i).getConcept().getTitle() + " dir=" + concept_links.get(i).getDirection() +
//		" curr_weight_sum=" + weight_sum+ " dom=" + concept_links.get(i).getConcept().getDomain().getId() + " vs " + _domain.getId());		
//}
			}

		}
		return weight_sum;
	}

	/** Adds another user activity event
	 * @param _user - the User for which the event has been fired
	 * @param _score - the score assigned to the event 
	 * @return - newly inserted UserActivity object*/
	public UserActivity addUserActivity(User _user, double _score, String date_n_time)
	{
		UserActivity ua = null;
		UserActivity result = null;
		if(_user == null)
		{
			System.out.println("[CBUM] Activity.addUserActivity ERROR! " +
				"User specified is NULL (result=" + _score + ")");
			return null;
		}
		ua = user_links.findByTitle(_user.getTitle());
		if(ua==null)
		{
//System.out.println("### [CBUM] Activity.addUserActivity Adding new UserActivity to activity:" + this.getTitle() + " to Activity.Id=" + this.getId()); /// DEBUG
			ua = new UserActivity(_user.getId(), _user.getTitle(), _user,
				this/*, false*/);
			user_links.add(ua);
			result = ua;
		}
//		// IN-TIME ACTIVITY PROGRESS UPDATE
//		ua.addScore(_score, date_n_time);

		//System.out.println("### [CBUM] Activity.addUserActivity User="+_user.getTitle()+" activity="+this.getTitle()+" score="+_score); /// DEBUG
		
//System.out.println("### [CBUM] Activity.addUserActivity Concepts#" + concept_links.size()); /// DEBUG
		Activity _act = this;
		while((_act.concept_links.size() == 0) && (_act.getParent() != null))
		{
			_act = _act.getParent();
		}
		for(int i=0; ((i<_act.concept_links.size()) && (_act != null)); i++)
		{
//System.out.println("### [CBUM] Activity.addUserActivity Examining Concept.Id=" + _act.concept_links.get(i).getConcept().getId()); /// DEBUG
//			if(_act.concept_links.get(i).getDirection() == 
//				ConceptActivity.DIR_OUTCOM)
			{
//System.out.println("### [CBUM] Activity.addUserActivity Adding score to Concept-Activity-link"); /// DEBUG
				_act.concept_links.get(i).addScore(_score ,_user, date_n_time);
			}
		}
		// DELAY ACTIVITY PROGRESS UPDATE
		ua.addScore(_score, date_n_time);
		
		
		return result;
	}

//	/** Adds another group activity event
//	 * @param _group - the Group for which the event has been fired
//	 * @param _score - the score assigned to the event */
//	public void addGroupActivity(User _group, double _score)
//	{
//		UserActivity ua = null;
//		if(_group == null)
//		{
//			System.out.println("[CBUM] Activity.addGroupActivity ERROR! " +
//				"Group specified is NULL.");
//			return;
//		}
//		ua = group_links.findById(_group.getId());
//		if(ua==null)
//		{
//			ua = new UserActivity(_group.getId(), _group.getTitle(),
//				_group, this, false);
//			group_links.add(ua);
//		}
//		ua.addScore(_score);
//	}

	/** Adds another Concept link
	 * @param _ca - the ConceptActivity link */
	public void addConceptLink(ConceptActivity _ca)
	{
		if(_ca == null)
		{
			System.out.println("[CBUM] Activity.addConceptLink ERROR! " +
				"ConceptActivity specified is NULL.");
			return;
		}
		concept_links.add(_ca);
//if(this.getTitle().equals("4.9")) /// DEBUG		
//System.out.println("### [CBUM] Activity.addConceptLink concept_links.size()="+concept_links.size()); /// DEBUG
//		ConceptActivity ca = concept_links.findById(this.getId());
//		if(ca==null) { concept_links.add(_ca); }
//		else
//		{
//			System.out.println("[CBUM] Concept.addActivityLink ERROR! " +
//				"ConceptActivity already Exists.");
//			return;
//		}

		switch (_ca.getDirection())
		{
			case ConceptActivity.DIR_PREREQ:
			{
				prereq_w_sum += _ca.getWeight();
			}
			break;
			case ConceptActivity.DIR_OUTCOM:
			{
				outcome_w_sum += _ca.getWeight();
			}
			break;
		}
	}
//	public void addConceptLink(Concept _concept, double _weight,
//		int _direction)
//	{
//		ConceptActivity ca = null;
//		if(_concept == null)
//		{
//			System.out.println("[CBUM] Activity.addConceptLink ERROR! " +
//				"Concept specified is NULL.");
//			return;
//		}
//		ca = concept_links.findById(_concept.getId());
//		if(ca==null)
//		{
//			ca = new ConceptActivity(_concept.getId(),
//				_concept.getTitle(), _concept, this, _weight,
//				_direction,	true);
//			concept_links.add(ca);
//		}
//		switch (_direction)
//		{
//			case ConceptActivity.DIR_PREREQ: { prereq_w_sum += _weight;	}
//			break;
//			case ConceptActivity.DIR_OUTCOM: { outcome_w_sum += _weight; }
//			break;
//		}
//	}

	public void writeActivityInfo(User _user, User _group, iProgressEstimatorReportItem _pea)
	{
		if(_user == null || _group == null)
		{
			System.out.println("[CBUM] Concept.getCount ERROR! User or Group specified is NULL.");
			return;
		}
		
//		UserActivity _ua = user_links.findById(_user.getId() );
//		if(_ua!=null)
//		{
			_pea.setCount(this.getCount(_user), 1 /*fudge of single item*/);
			_pea.setProgress(this.getProgress(_user), 1 /*fudge of single item*/);
//		}
		_pea.setGroupCount(this.getGroupCount(_group, _user), 1 /*fudge of single item*/);
		_pea.setGroupProgress(this.getGroupProgress(_group, _user), 1 /*fudge of single item*/);
	}
	
	/** Function returns the progress for the specified User
	 * @param _user - the specified User
	 * @return the progress for the specified User */
	public double getProgress(User _user)
	{
		if(_user == null)
		{
			System.out.println("[CBUM] Activity.getProgress ERROR! " +
				"User specified is NULL.");
			return -1;
		}
		double result = 0;
		if(getChildren().size()==0)
		{
			UserActivity ua = user_links.findById(_user.getId());
			if(ua != null)
				result =  ua.getProgress();
		}
		else
		{
			double sum_child_scores = 0;
			for(int i=0; i<getChildren().size(); i++)
				sum_child_scores += getChildren().get(i).
					getProgress(_user);
			result =  (sum_child_scores/getChildren().size());
		}
		return result;
	}/**/

	/** Function returns the progress for the Group minus specified User
	 * @param _group - Group of users
	 * @param _user - the specified User
	 * @return the mean progress for the Group minus specified User */
	
	private double getGroupProgress(User _group, User _user)
	{
		if(_user == null || _group == null)
		{
			System.out.println("[CBUM] Activity.getGroupProgress ERROR! " +
				"User or Group specified is NULL.");
			return -1;
		}
		
		double result = 0;
		
		if(getChildren().size()==0)
		{
//System.out.println("[cbum] Activity.getGroupProgress has NO children");			
			double sum_progress = 0;
			// compute sum of user progresses
			for(int i=0; i<_group.getOrdinates().size(); i++)
			{
				UserActivity ga = user_links.findById(_group.getOrdinates().get(i).getId());
				if(ga==null || _group.getOrdinates().get(i).getId()==_user.getId())
					continue;
				sum_progress += ga.getProgress();
			}
			// average
			int size_group = _group.getOrdinates().size() - 1;
			size_group = ((size_group>0)?size_group:1);
			result =  sum_progress / size_group;
		}
		else
		{
//System.out.println("[cbum] Activity.getGroupProgress has children");			
			double sum_child_scores = 0;
			for(int i=0; i<getChildren().size(); i++)
				sum_child_scores += getChildren().get(i).getGroupProgress(_group, _user);
			int size_group = getChildren().size();
			size_group = ((size_group>0)?size_group:1);
			result =  (sum_child_scores/size_group);
		}
//System.out.println(" - exit (" + result + ")");			
		return result;
	}/**/

	/** Function returns the access count for the specified User
	 * @param _user - the specified User
	 * @return the access count for the specified User */
	
	public int getCount(User _user)
	{
		if(_user == null)
		{
			System.out.println("[CBUM] Activity.getProgress ERROR! " +
				"User specified is NULL.");
			return -1;
		}
		int result = 0;
		if(getChildren().size()==0)
		{
			UserActivity ua = user_links.findById(_user.getId());
			if(ua != null)
				result =  ua.getCount();
		}
		else
		{
			int sum_child_counts = 0;
			for(int i=0; i<getChildren().size(); i++)
				sum_child_counts += getChildren().get(i).
					getCount(_user);
			result =  sum_child_counts;
		}
		return result;
	}/**/

	/** Function returns the access count for Group minus specified User
	 * @param _group - Group of users
	 * @param _user - the specified User
	 * @return the mean access count the Group minus specified User */
	
	private double getGroupCount(User _group, User _user)
	{
		if(_user == null || _group == null)
		{
			System.out.println("[CBUM] Activity.getGroupCount ERROR! " +
				"User or Group specified is NULL.");
			return -1;
		}
		
		double result = 0;
		if(getChildren().size()==0)
		{
			double sum_count = 0;
			// compute sum of user conts
			for(int i=0; i<_group.getOrdinates().size(); i++)
			{
				UserActivity ga = user_links.findById(_group.getOrdinates().get(i).getId());
				if(ga==null || _group.getOrdinates().get(i).getId()==_user.getId())
					continue;
				sum_count += ga.getCount();
			}
			// average
			int size_group = _group.getOrdinates().size() - 1;
			size_group = ((size_group>0)?size_group:1);
			result =  sum_count / size_group;
		}
		else
		{
			double sum_child_scores = 0;
			for(int i=0; i<getChildren().size(); i++)
				sum_child_scores += getChildren().get(i).getGroupCount(_group, _user);
			int size_group = getChildren().size();
			size_group = ((size_group>0)?size_group:1);
			result =  (sum_child_scores/size_group);
		}

		return result;
	}/**/

	//-- Other Methods
	/** Function prints a progress report to a Servlet output stream
	 * @param out - Servlet output stream 
	 * @param _user - the user Login who's progress is reported
	 * @param _prefix - identation for XML */
	public ActivityReportItem progressReportStruct(User _user, User _group)
	{// progressReport
		ActivityReportItem result = new ActivityReportItem(this);
		if(_user == null)
		{
			System.out.println("[CBUM] Activity.progressReport ERROR! " +
				"User specified is NULL.");
		}
//		result += _prefix + "<" +
//			((getChildren().size()==0)?"subactivity":"activity") + ">\n";
//			result += _prefix + "\t" + "<name>" + this.getTitle() + "</name>\n";
		if(getChildren().size()==0)
		{
			// INDIVIDUAL
			UserActivity ua = user_links.findById(_user.getId());
			// GROUP
			double sum_progress = 0;
			double sum_count = 0;
//System.out.print("Activity=" + this.getTitle() + " Group of " + _group.getOrdinates().size() + " users");			
			for(int i=0; i<_group.getOrdinates().size(); i++)
			{
				if(_user.getId() == _group.getOrdinates().get(i).getId())
					continue;
				UserActivity ga = user_links.findById(_group.getOrdinates().get(i).getId());
				if(ga != null)
				{
					sum_progress += ga.getProgress();
					sum_count += ga.getCount();
				}
//else
//System.out.println(", user=" + _group.getOrdinates().get(i).getTitle() + " nothing");
			}
			sum_progress /= (_group.getOrdinates().size() - 1);
			sum_count /= (_group.getOrdinates().size() - 1);
			
//			String name = this.getTitle();
//			int count = 0;
//			double progress = 0;
			if(ua != null)
			{
				ua.progressReportStruc(result);
//				result += ua.progressReport(_prefix + "\t", false);
			}
//			else
//				result += UserActivity.progressReportDefault(_prefix + "\t", 
//					false);
			
			// add group stats
			result.grp_count  = sum_count;
			result.grp_progress = sum_progress;
			
//			result += _prefix + "\t" + "<group>\n";
//			result += _prefix + "\t\t" + "<count>" + sum_count + "</count>\n";
//			result += _prefix + "\t\t" + "<progress>" + sum_progress + "</progress>\n";
//			result += _prefix + "\t" + "</group>\n";
		}
		else
		{
			for(int i=0; i<getChildren().size(); i++)
			{
				result.sub_activities.add(getChildren().get(i).progressReportStruct(_user, _group));
//				result += getChildren().get(i).progressReport(_user, _group,
//					_prefix +"\t");
			}
		}
		
//		result += _prefix + "</" +
//			((getChildren().size()==0)?"subactivity":"activity") + ">\n";
		return result;
	}// end of -- progressReport
	/** Function prints a progress report to a Servlet output stream
	 * @param out - Servlet output stream 
	 * @param _user - the user Login who's progress is reported
	 * @param _prefix - identation for XML */
	public String progressReport(User _user, User _group, String _prefix)
	{// progressReport
		String result = "";
		if(_user == null)
		{
			System.out.println("[CBUM] Activity.progressReport ERROR! " +
				"User specified is NULL.");
		}
		result += _prefix + "<" +
			((getChildren().size()==0)?"subactivity":"activity") + ">\n";
			result += _prefix + "\t" + "<name>" + this.getTitle() + "</name>\n";
		if(getChildren().size()==0)
		{
			// INDIVIDUAL
			UserActivity ua = user_links.findById(_user.getId());
			// GROUP
			double sum_progress = 0;
			double sum_count = 0;
//System.out.print("Activity=" + this.getTitle() + " Group of " + _group.getOrdinates().size() + " users");			
			for(int i=0; i<_group.getOrdinates().size(); i++)
			{
				if(_user.getId() == _group.getOrdinates().get(i).getId())
					continue;
				UserActivity ga = user_links.findById(_group.getOrdinates().get(i).getId());
				if(ga != null)
				{
					sum_progress += ga.getProgress();
					sum_count += ga.getCount();
//System.out.println(", user=" + _group.getOrdinates().get(i).getTitle() + " acted");
				}
//else
//System.out.println(", user=" + _group.getOrdinates().get(i).getTitle() + " nothing");
			}
			sum_progress /= (_group.getOrdinates().size() - 1);
			sum_count /= (_group.getOrdinates().size() - 1);
			
//			String name = this.getTitle();
//			int count = 0;
//			double progress = 0;
			if(ua != null)
				result += ua.progressReport(_prefix + "\t", false);
			else
				result += UserActivity.progressReportDefault(_prefix + "\t", 
					false);
			
			// print group stats
			result += _prefix + "\t" + "<group>\n";
			result += _prefix + "\t\t" + "<count>" + sum_count + "</count>\n";
			result += _prefix + "\t\t" + "<progress>" + sum_progress + "</progress>\n";
			result += _prefix + "\t" + "</group>\n";
		}
		else
		{
			for(int i=0; i<getChildren().size(); i++)
				result += getChildren().get(i).progressReport(_user, _group,
					_prefix +"\t");
		}
		result += _prefix + "</" +
			((getChildren().size()==0)?"subactivity":"activity") + ">\n";
		return result;
	}// end of -- progressReport


	public Activity2ConceptMapping4Report mappingToConcepts(int _domain_id) 
	{
//		Activity2ConceptMapping result = new Activity2ConceptMapping(
//				((this.uri != null && this.uri.length()>0)?this.uri:"http://adap2.sis.pitt.edu/cbum/activity/" + this.title) );
		Activity2ConceptMapping4Report result = new Activity2ConceptMapping4Report(this);
		
		for(int i=0; i<this.concept_links.size(); i++)
		{
			if(this.concept_links.get(i).concept.getDomain().getId() == _domain_id)
			{
				result.concepts.add( this.concept_links.get(i).concept);
			}
		}
		
		// if no concepts for activity, then look in sub-activities (true for QuizPACK)
		if(result.concepts.size() == 0)
		{
			for(int i=0; i<this.children.size(); i++)
			{
				Activity subact = this.children.get(i);
				for(int j=0; j<subact.concept_links.size(); j++)
				{
					if(subact.concept_links.get(j).concept.getDomain().getId() == _domain_id)
					{
						result.concepts.add( subact.concept_links.get(j).concept);
					}
				}
			}
		}
		
		return result;
	}
	
}

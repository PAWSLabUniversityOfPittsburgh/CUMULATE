/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/** This class represents an Indexing Concept
 * @author Michael V. Yudelson */
package edu.pitt.sis.paws.cbum;

import java.util.ArrayList;
import edu.pitt.sis.paws.cbum.report.iProgressEstimatorReportItem;
import edu.pitt.sis.paws.core.Item2;
import edu.pitt.sis.paws.core.Item2Vector;

public class Concept extends Item2
{
	static final long serialVersionUID = 1234L;
	
	/** Superordinate concept */
	protected Concept parent;
	/** The domain concept belongs to */
	protected Concept domain;
	/** Subordinate concepts' array */
	protected Item2Vector<Concept> children;
	/** An array of links to Activities */
	protected Item2Vector<ConceptActivity> activity_links;
	/** Sum of the prerequisite Activities' weights */
	protected double prereq_w_sum;
	/** Sum of the outcome Activities' weights */
	protected double outcome_w_sum;
	/** Knowledge level Estimators per User */
	protected Item2Vector<UserProgressEstimator> user_knowledge_levels;
	
	// Constructor without URI
	public Concept(int _id, String _title)
	{
		super(_id, _title, "no uri");
		parent = null;
		domain = null;
		children = new Item2Vector<Concept>();
		activity_links = new Item2Vector<ConceptActivity>();
		prereq_w_sum = 0;
		outcome_w_sum = 0;
		user_knowledge_levels = new Item2Vector<UserProgressEstimator>();
	}

	// Constructor with URI
	public Concept(int _id, String _title, String _uri)
	{
		super(_id, _title, _uri);
		parent = null;
		domain = null;
		children = new Item2Vector<Concept>();
		activity_links = new Item2Vector<ConceptActivity>();
		prereq_w_sum = 0;
		outcome_w_sum = 0;
		user_knowledge_levels = new Item2Vector<UserProgressEstimator>();
	}

	//-- Setters and Getters
	/** Setter for the superordinate concept
	 * @param _parent - the new superordinate concept */
	public void setParent(Concept _parent) { parent = _parent; }

	/** Getter for the superordinate concept
	 * @return - the superordinate concept */
	public Concept getParent() { return parent; }

	/** Setter for the domain
	 * @param _domain - the domain concept belongs to */
	public void setDomain(Concept _domain) { domain = _domain; }

	/** Getter for the domain
	 * @return - the domain concept belongs to */
	public Concept getDomain() { return domain; }

	/** Getter for the subordinate concepts' array
	 * @return - the subordinate concepts' array */
	public Item2Vector<Concept> getChildren() { return children; }
	
	/** Getter for the array of links to Activities
	 * @return - the array of links to Activities */
	public Item2Vector<ConceptActivity> getConceptActivities()
	{
		return activity_links;
	}

	/** Getter for the sum of the prerequisite Activities' weights
	 * @return - sum of the prerequisite Activities' weights */
	public double getPrereqWeightSum() { return prereq_w_sum; }

	/** setter for the sum of the prerequisite Activities' weights
	 * @param _new_sum -  new sum of the prerequisite Activities' weights */
	public void setPrereqWeightSum(double _new_sum)
	{
		prereq_w_sum = _new_sum;
	}

	/** Getter for the sum of the outcome Activities' weights
	 * @return - sum of the outcome Activities' weights */
	public double getOutcomeWeightSum() { return outcome_w_sum; }

	/** setter for the sum of the outcome Activities' weights
	 * @param _new_sum -  new sum of the outcome Activities' weights */
	public void setOutcomeWeightSum(double _new_sum)
	{
		outcome_w_sum = _new_sum;
	}

	/** Getter for the User Knowledge level Estimators
	 * @return - User Knowledge level Estimators */
	public Item2Vector<UserProgressEstimator> getUserKnowledgeLevels()
	{
		return user_knowledge_levels;
	}
	
	//-- Other Methods
	/** Adds another Activity link
	 * @param _act - the Activity linked to this Concept
	 * @param _weight - weight of the link 
	 * @param _direction - direction of the link*/
	public ConceptActivity addActivityLink(Activity _act, double _weight, 
		int _direction)
	{
		ConceptActivity ca = null;
		if(_act == null)
		{
			System.out.println("[CBUM] Concept.addActivityLink ERROR! " +
				"Activity specified is NULL.");
			return null;
		}
		ca = activity_links.findById(_act.getId());
		if(ca==null)
		{
			ca = new ConceptActivity(_act.getId(), 
				_act.getTitle(), this, _act, _weight, 
				_direction/*, true*/);
			activity_links.add(ca);
		}
		else
		{
			System.out.println("[CBUM] Concept.addActivityLink ERROR! " +
				"ConceptActivity already Exists. C.Id:" + this.getId() +
				" A.Id:" + _act.getId());
			return null;
		}
//		switch (_direction)
//		{
//			case ConceptActivity.DIR_PREREQ: { prereq_w_sum += _weight;	}
//			break;
//			case ConceptActivity.DIR_OUTCOM: { outcome_w_sum += _weight; }
//			break;
//		}
		return ca;
	}
	
	/** Add new score to the estimator
	 * @param _score - the new score
	 * @param _user - User that got the score 
	 * @param _weight - the weight of the score */
	public void addScore(double _score, User _user, int _app, double _weight, String date_n_time)
	{
		UserProgressEstimator upe = user_knowledge_levels.findById(
			_user.getId() );
		if(upe==null) // add new UserProgressEstimator
		{
			int estimator_type = 0;
			switch (domain.getId())
			{
				// concepts
				case 1:
				case 8:
				case 9:
				case 11:
				case 12:
					estimator_type = iProgressEstimator.ESTIMATOR_ASSYMPTOTIC;
				break;
				case 0:
				break;
				// topics
				case 2: 
				case 6: 
				case 7: 
				case 10: 
					estimator_type = iProgressEstimator.ESTIMATOR_MEAN;
				break;
			}
			//t 
			//c 
			
			
			upe = new UserProgressEstimator(_user.getId(),
				_user.getTitle(), _user, 0, estimator_type);
			user_knowledge_levels.add(upe);
		}
		
		int level_idx = ResourceMap.mapToBloomLevelIndex(_app, _score);
//		if((level_idx==0) && _score<0)
//			_score = Math.abs(_score);
//if(this.getId()==14)			
//{
//System.out.println("### [CBUM] Concept.addScore C.Id=" + this.getId() +" _score=" + _score + " _weight= " + _weight + " outcome_w_sum=" + outcome_w_sum); /// DEBUG
//}

//if(this.getId()==190)
//{
//System.out.println("### [CBUM] Concept.addScore User="+_user.getTitle()+" score="+_score + " weight=" + _weight); /// DEBUG
//System.out.println("### [CBUM] Concept.addScore \tcog_level_name=" + iProgressEstimator.BLOOM_NAMES[level_idx] ); /// DEBUG
//System.out.println("### [CBUM] Concept.addScore \tcog_level_OLD=" + upe.getKnowledgeLevels()[level_idx].getProgress() ); /// DEBUG
//}
//System.out.println(this.getTitle() + " _app = " + _app + "  level_idx = " + level_idx);
		if(level_idx != -1)
			upe.getKnowledgeLevels()[level_idx].addProgress(_score, _weight,
				outcome_w_sum, date_n_time);
//if(this.getId()==190)
//{
//System.out.println("### [CBUM] Concept.addScore \tcog_level_NEW=" + upe.getKnowledgeLevels()[level_idx].getProgress() ); /// DEBUG
//System.out.println("### [CBUM] Concept.addScore \t domain=" + this.getDomain().getId() + " " + this.getDomain().getTitle()); /// DEBUG
//}			
		
	}
	
	/** Function returns the progress for the specified User
	 * @param _user - the specified User
	 * @param _level - Bloom Level index
	 * @return the progress for the specified User
	 * @deprecated 
	*/
	/*
	public double getProgress(User _user, int _level)
	{
		if(_user == null)
		{
			System.out.println("[CBUM] Concept.getProgress ERROR! " +
				"User specified is NULL.");
			return -1;
		}
		if((_level >= iProgressEstimator.BLOOM_COUNT) || (_level < 0))
		{
			System.out.println("[CBUM] Concept.getProgress ERROR! " +
				"Unsupported (#" + _level + 
				") Bloom Level is specified.");
			return -1;
		}
		
		double result = 0;
		UserProgressEstimator upe = user_knowledge_levels.findById(
			_user.getId() );
		if(upe!=null)
		{
			result = upe.getKnowledgeLevels()[_level].getProgress();
		}
		return result;
	}/**/
	
	/** Function writes the progress of a specific user to a progress estimator object
	 * @param _user - the specified User
	 * @param _level - Bloom Level index
	 * @param _pea - the progress estimator object
	 */
	public void writeKnowledgeInfo(User _user, User _group, int _level, iProgressEstimatorReportItem _pea)
	{
		if(_user == null || _group == null)
		{
			System.out.println("[CBUM] Concept.getCount ERROR! " +
				"User or Group specified is NULL.");
			return;
		}
		if((_level >= iProgressEstimator.BLOOM_COUNT)/* || (_level < 0)*/)
		{
			System.out.println("[CBUM] Concept.getProgress ERROR! " +
				"Unsupported (#" + _level + 
				") Bloom Level is specified.");
			return;
		}
		
		ArrayList<UserProgressEstimator> _peer_upe = new ArrayList<UserProgressEstimator>();
		for(int i=0; i<_group.getOrdinates().size(); i++)
		{
			User _peer = _group.getOrdinates().get(i);
			if(_peer.getId() != _user.getId())
			{
				UserProgressEstimator upe = user_knowledge_levels.findById(_peer.getId() );
				if(upe!=null)
					_peer_upe.add(upe);
			}
		}
		
		UserProgressEstimator upe = user_knowledge_levels.findById(_user.getId() );
		if(upe!=null)
		{
			_pea.setKnowledgeInfo(upe, _peer_upe, _level);
		}
	}
	
	
	/** Function returns the progress for the specified User
	 * @param _group - the specified Group
	 * @param _user - the specified User
	 * @param _level - Bloom Level index
	 * @return the progress for the specified User */
	/*
	public double getGroupProgress(User _group, User _user, int _level)
	{
		if(_user == null || _group == null)
		{
			System.out.println("[CBUM] Concept.getProgress ERROR! " +
				"User or Group specified is NULL.");
			return -1;
		}
		if((_level >= iProgressEstimator.BLOOM_COUNT) || (_level < 0))
		{
			System.out.println("[CBUM] Concept.getCount ERROR! " +
				"Unsupported (#" + _level + 
				") Bloom Level is specified.");
		}
		
		double result = 0;

		for(int i=0; i<_group.getOrdinates().size(); i++)
		{
			User _peer = _group.getOrdinates().get(i);
			if(_peer.getId() == _user.getId())
				continue;
			UserProgressEstimator upe = user_knowledge_levels.findById(
				_peer.getId() );
			if(upe!=null)
			{
				result += upe.getKnowledgeLevels()[_level].getProgress();
			}
		}
		result /= (_group.getOrdinates().size()-1);
		
		return result;
	}/**/
	
	/** Function returns the access count for the specified User
	 * @param _user - the specified User
	 * @param _level - Bloom Level index
	 * @return the count of number of accesses for the specified User */
/*	public int getCount(User _user, int _level)
	{
		if(_user == null)
		{
			System.out.println("[CBUM] Concept.getCount ERROR! " +
				"User specified is NULL.");
			return -1;
		}
		if((_level >= iProgressEstimator.BLOOM_COUNT) || (_level < 0))
		{
			System.out.println("[CBUM] Concept.getCount ERROR! " +
				"Unsupported (#" + _level + 
				") Bloom Level is specified.");
		}

		int result = 0;
		UserProgressEstimator upe = user_knowledge_levels.findById(
			_user.getId() );
		if(upe!=null)
		{
			result = upe.getKnowledgeLevels()[_level].getCount();
		}
		return result;
	}/**/
	
	/** Function returns the access count for the specified User
	 * @param _group - the specified Group
	 * @param _user - the specified User
	 * @param _level - Bloom Level index
	 * @return the count of number of accesses for the specified User */
	/*
	public int getGroupCount(User _group, User _user, int _level)
	{
		if(_user == null || _group == null)
		{
			System.out.println("[CBUM] Concept.getCount ERROR! " +
				"User or Group specified is NULL.");
			return -1;
		}
		if((_level >= iProgressEstimator.BLOOM_COUNT) || (_level < 0))
		{
			System.out.println("[CBUM] Concept.getCount ERROR! " +
				"Unsupported (#" + _level + 
				") Bloom Level is specified.");
		}

		int result = 0;
		
		for(int i=0; i<_group.getOrdinates().size(); i++)
		{
			User _peer = _group.getOrdinates().get(i);
			if(_peer.getId() == _user.getId())
				continue;
			UserProgressEstimator upe = user_knowledge_levels.findById(
					_peer.getId() );
			if(upe!=null)
			{
				result += upe.getKnowledgeLevels()[_level].getCount();
			}
		}
		result /= (_group.getOrdinates().size()-1);
		
		return result;
	}/**/
	
	/** Function returns progress estimator for a specific user
	 * @param _user - specific user
	 * @return user progress estimator */
	public UserProgressEstimator getUserProgressEstimator(User _user)
	{
		if(_user == null)
		{
			System.out.println("[CBUM] Concept.getUserProgress" + 
				"Estimator ERROR! User specified is NULL.");
		}
		for(int i=0; i<user_knowledge_levels.size(); i++)
			if(user_knowledge_levels.get(i).getUser().getId() == 
				_user.getId())
				return user_knowledge_levels.get(i);
		return null;
	}
	
	
	/** Function prints a progress report to a Servlet output stream
	 * @param out - Servlet output stream 
	 * @param _user - the user Login who's progress is reported
	 * @param _prefix - identation for XML */
	public String progressReport(User _user, String _prefix,
		int _level_mask)
	{// progressReport
		String result = "";
		if(_user == null)
		{
			result += "[CBUM] Concept.progressReport ERROR! " +
				"User specified is NULL.\n";
		}
		result += _prefix + "<concept>\n";
		result += _prefix + "\t" + "<name>" + this.getTitle() + "</name>\n";
		UserProgressEstimator upe = getUserProgressEstimator(_user);
		result += _prefix + "\t" + "<cog_levels>\n";
		if(upe != null)
			result += upe.progressReport(_prefix + "\t", _level_mask);
		else
			result += UserProgressEstimator.progressReportDefault( 
				_prefix + "\t\t", _level_mask);
		result += _prefix + "\t" + "</cog_levels>\n";
		result += _prefix + "</concept>\n";
		return result;
	}// end of -- progressReport

	public String mappingXML(int _app_id) 
	{
		String result = "";
		result += "		<mapping>\n";
		result += "			<concept>" + this.getTitle() + "</concept>\n";;
		for(int i=0; i<this.activity_links.size(); i++)
		{
			if(_app_id == -1 || this.activity_links.get(i).activity.getApp().getId() == _app_id)
			{
				result += "			<activity>" + this.activity_links.get(i).activity.getTitle() + "</activity>\n";
			}
		}
		result += "		</mapping>\n";
		return result;
	}
	
	public String mappingRDF(int _app_id) 
	{
		String result = "";
		result += "		<mapping>\n";
		result += "			<concept>" + this.getTitle() + "</concept>\n";
		for(int i=0; i<this.activity_links.size(); i++)
		{
			if(_app_id == -1 || this.activity_links.get(i).activity.getApp().getId() == _app_id)
			{
				result += "			<activity>" + this.activity_links.get(i).activity.getTitle() + "</activity>\n";
			}
		}
		result += "		</mapping>\n";
		return result;
	}
	
}




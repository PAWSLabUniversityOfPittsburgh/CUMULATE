/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/** Class representing a link between a Concept and Activity. In case of 
 * Concept-Activity link <em>id</em> and <em>title</em> should be used those of 
 * Concept, in case of Activity-Concept link those of Activity should be used.
 * @author Michael V. Yudelson */

package edu.pitt.sis.paws.cbum;

import edu.pitt.sis.paws.core.Item2;

public class ConceptActivity extends Item2
{
	static final long serialVersionUID = 2344L;
	//Constants
//	public static final String CA_TOP_ELEMENT_CONCEPT = "C"; 
//	public static final String CA_TOP_ELEMENT_ACTIVITY = "A"; 
	public static final int DIR_PREREQ = 0;
	public static final int DIR_OUTCOM = 1;

	/** The Activity that is linked to a Concept */
	protected Activity activity;
	/** The Concept that is linked to a Activity */
	protected Concept concept;
	/** Weight of the link */
	protected double weight;
	/** Direction of the link */
	protected int direction;
//	/** Flag identifying the top element of the link: Concept or Activity */
//	protected String top_element;
	
	public ConceptActivity(int _id, String _title, Concept _concept,
		Activity _activity, double _weight, int _direction/*,
		boolean top_element_concept*/)
	{
		super(_id, _title, "no uri");
		concept = _concept;
		
		activity = _activity;
		weight = _weight;
		direction = _direction;

//if(this.getTitle().equals("4.9")) /// DEBUG		
//System.out.print("### [CBUM] ConceptActivity Creating A.Id=" + activity.getId() + " C.Id=" + concept.getId() + " D=" + _direction + " W=" + _weight + " wasSumPre=" + concept.getPrereqWeightSum() + " wasSumOut=" + concept.getOutcomeWeightSum()); /// DEBUG
		switch (_direction) 
		{
			case DIR_PREREQ:
				concept.setPrereqWeightSum( 
					concept.getPrereqWeightSum() + _weight);
			break;
			case DIR_OUTCOM:
				concept.setOutcomeWeightSum( 
					concept.getOutcomeWeightSum() + _weight);
			break;
		}
//		top_element = (top_element_concept) ? CA_TOP_ELEMENT_CONCEPT :
//			CA_TOP_ELEMENT_ACTIVITY;
//if(this.getTitle().equals("4.9")) /// DEBUG		
//System.out.println(" isSumPre=" + concept.getPrereqWeightSum() + " isSumOut=" + concept.getOutcomeWeightSum()); /// DEBUG
	}

	//-- Setters and Getters
	/** Getter for the Activity element of the Link
	 * @return the Activity element of the Link */
	public Activity getActivity() { return activity; }

	/** Getter for the Concept element of the Link
	 * @return the Concept element of the Link */
	public Concept getConcept() { return concept; }

	/** Getter for the weight of the Link
	 * @return the weight of the Link */
	public double getWeight() { return weight; }

	/** Getter for the direction of the Link
	 * @return the direction of the Link */
	public int getDirection() { return direction; }

	//-- Other Methods
	/** Add new score to the estimator
	 * @param _score - the new score
	 * @param _user - User that got the score */
	public void addScore(double _score, User _user, String date_n_time)
	{
		if(_user == null)
		{
			System.out.println("[CBUM] ConceptActivity.addScore ERROR! " +
				"User specified is NULL.");
			return;
		}
//		concept.addScore(_score, _user, activity.getApp().getId(), 
//			this.weight*activity.getProgress(_user));
		double w = Math.pow(weight, .25);
		double sum_w = Math.pow/**/((activity.getProgress(_user)*activity.getCount(_user) +1)*
				( (this.getDirection()== DIR_OUTCOM) 
						? activity.getOutcomeWeightSum(this.concept.getDomain())
						: activity.getPrereqWeightSum(this.concept.getDomain())
				), .25/**/)+0; 
//		sum_w = (Double.isNaN(sum_w))?1:sum_w;
		
//		if(_user.getId()==3 && this.concept.getDomain().getId() == 12 && this.activity.getApp().getId()==23)
//		{
//			System.out.println("activity.getOutcomeWeightSum(this.concept.getDomain()= " + activity.getOutcomeWeightSum(this.concept.getDomain()));
//			System.out.println("activity.getPrereqWeightSum(this.concept.getDomain()= " + activity.getPrereqWeightSum(this.concept.getDomain()));
//			System.out.println("activity.getProgress(_user)= " + activity.getProgress(_user));
//			System.out.println("activity.getCount(_user)= " + activity.getCount(_user));
//			System.out.println("sum_w= " +sum_w);
//			System.out.println("");
//		}
			
		if( (concept.domain.getId() == 2) || (concept.domain.getId()==0) )
		{
			w *= w;
//			sum_w *= sum_w;
		}
//if(this.concept.getId()==190)
//{
//System.out.println("### [CBUM] ConceptActivity.addScore User="+_user.getTitle()+" score="+_score + " weight=" + w + " sum_weight=" + sum_w + " weight^=" + w/(sum_w+1)); /// DEBUG
//System.out.println("### [CBUM] ConceptActivity.addScore \t sumO=" + activity.getOutcomeWeightSum(this.concept.getDomain()) + " sumP=" + activity.getPrereqWeightSum(this.concept.getDomain())); /// DEBUG
//System.out.println("###@ [CBUM] ConceptActivity.addScore \t DOMAIN=" + this.concept.getDomain().getId() + " " + this.concept.getDomain().getTitle()); /// DEBUG
//}		
		
//// CUMULATE TRACING		
//		System.out.println("a-c " + activity.getId() + "-" + concept.getId() + " a.c=" + (activity.getProgress(_user)*activity.getCount(_user) +1) 
//				+ " w\"=" + w/sum_w + " w=" + weight + " sum=" + ((this.getDirection()== DIR_OUTCOM) 
//						? activity.getOutcomeWeightSum(this.concept.getDomain())
//						: activity.getPrereqWeightSum(this.concept.getDomain())
//				));
		
		concept.addScore(_score, _user, activity.getApp().getId(), 
			w/sum_w, date_n_time);
	}
}

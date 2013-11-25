/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/** Class representing a link between a User and his/her Progress Estimator with
 * respect to certain Concept-Activity link. Instances of this class are 
 * assigned to Concept-Activity links and distinguished by User. User's id and
 * title are used as search criteria.
 * @author Michael V. Yudelson */
 
package edu.pitt.sis.paws.cbum;

import edu.pitt.sis.paws.core.Item2;

public class UserProgressEstimator extends Item2
{
	static final long serialVersionUID = 321312L;

	/** User whose knowledge is being estimated*/
	protected User user;
	/** Knowledge level Estimators */
	protected iProgressEstimator knowledge_levels[];
	
	public UserProgressEstimator(int _id, String _title, User _user, 
		double _weight, int estimator_type)
	{
		super(_id, _title, "no uri");
		if(_user == null)
			System.out.println("[CBUM] UserProgressEstimator ERROR! " +
				"User specified is NULL.");
		user  = _user;
		knowledge_levels = 
			new iProgressEstimator[iProgressEstimator.BLOOM_COUNT];
		
//		switch (estimator_type) 
//		{
//			case iProgressEstimator.ESTIMATOR_MEAN:
//			{
//				knowledge_levels[iProgressEstimator.BLOOM_IDX_KNOWLEDGE] = 
//					new MeanEstimator(0, 0); 
//				knowledge_levels[iProgressEstimator.BLOOM_IDX_COMPREHENSION] = 
//					new MeanEstimator(0, 0); 
//				knowledge_levels[iProgressEstimator.BLOOM_IDX_APPLICATION] = 
//					new MeanEstimator(0, 0);  
//			}
//			break;
//			case iProgressEstimator.ESTIMATOR_ASSYMPTOTIC:
//			{
				knowledge_levels[iProgressEstimator.BLOOM_IDX_KNOWLEDGE] = 
					new AsypmthoticEstimator(0, 0); 
				knowledge_levels[iProgressEstimator.BLOOM_IDX_COMPREHENSION] = 
					new AsypmthoticEstimator(0, 0); 
				knowledge_levels[iProgressEstimator.BLOOM_IDX_APPLICATION] = 
					new AsypmthoticEstimator(0, 0);  
				knowledge_levels[iProgressEstimator.BLOOM_IDX_SYNTHESIS] = 
					new AsypmthoticEstimator(0, 0);  
//			}
//			break;
//		}
		
	}

	//-- Setters and Getters
	/** Getter for the Knowledge level Estimators
	 * @return - Knowledge level Estimators*/
	public iProgressEstimator[] getKnowledgeLevels()
	{
		return knowledge_levels;
	}
	
	/** Getter for the User whose Progress is being estimated
	 * @return - User whose Progress is being estimated */
	public User getUser() { return user; }


	/** Function prints a user progress report to a Servlet output stream
	 * @param out - Servlet output stream 
	 * @param _prefix - identation for XML
	 * @param _level_mask - cognitive levels to report mask */
	public String progressReport(String _prefix,
		int _level_mask)
	{// progressReport
		String result = "";
		
		for(int j=0; j<iProgressEstimator.BLOOM_COUNT; j++)
		{
			if(((int)Math.pow(2,j)&_level_mask)>0)
			{
				result += _prefix + "<cog_level>\n";
				result +=_prefix + "\t<name>\n";;
				result +=iProgressEstimator.
					BLOOM_NAMES[j].toLowerCase() + "\n";
				result +="</name>\n";
				result +=_prefix + "\t<value>\n";
				result += knowledge_levels[j].getProgress() + "\n";
				result +="</value>\n";
				result +="</cog_level>\n";
			}
		}
		return result;
	}// end of -- progressReport

	/** Function prints a default user progress report to a Servlet output
	 * 	stream
	 * @param out - Servlet output stream 
	 * @param _prefix - identation for XML
	 * @param _level_mask - cognitive levels to report mask */
	public static String progressReportDefault(String _prefix,
		int _level_mask)
	{// progressReportDefault
		String result = "";
		for(int j=0; j<iProgressEstimator.BLOOM_COUNT; j++)
		{
//System.out.println("[CBUM] UserProgressEstimator.progressReportDefault _level_mask=" + _level_mask); /// DEBUG
//System.out.println("[CBUM] UserProgressEstimator.progressReportDefault iProgressEstimator.BLOOM_NAMES[j]=" + iProgressEstimator.BLOOM_NAMES[j]); /// DEBUG
			if(((int)Math.pow(2,j)&_level_mask)>0)
			{
//System.out.println("[CBUM] UserProgressEstimator.progressReportDefault \there"); /// DEBUG
				result += _prefix + "<cog_level>\n";
				result += _prefix + "\t<name>\n";
				result += iProgressEstimator.
					BLOOM_NAMES[j].toLowerCase() + "\n";
				result += "</name>\n";
				result += _prefix + "\t<value>0.0</value>\n";
				result += _prefix + "</cog_level>\n";
			}
		}
		return result;
	}// end of -- progressReportDefault
	
}
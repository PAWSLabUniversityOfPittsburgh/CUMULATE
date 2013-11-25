/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/** Asympthotic knowledge/progress estimator
 * @author Michael V. Yudelson */

package edu.pitt.sis.paws.cbum;

public class AsypmthoticEstimator extends ProgressEstimator
{
	public AsypmthoticEstimator(double _fixed_weight, 
		double _fixed_sum_weights)
	{
		super(_fixed_weight, _fixed_sum_weights);
	}
	
	/** Function updates the estimation using an asymthotic formula
	 * @param new_score - new score that is rendered into the estimation
	 * @param weight - weight of the score
	 * @param sum_weights - sum of all other weights (in given context)
	 */
	public void addProgress(double new_score, double weight,
		double sum_weights, String date_n_time)
	{
		double current_weight = (fixed_weight>0)?fixed_weight:weight;
//		double current_sum_weights = 
//			(fixed_sum_weights>0)?fixed_sum_weights:sum_weights;
		progress += ((progress<.5)
				?Math.pow((1 - progress),2)/2
				:Math.pow((1 - progress),2)) * ( (Math.abs(new_score) * current_weight) / 1);

//		progress += ((progress<.5)?Math.pow((1 - progress),2)/2:Math.pow((1 - progress),2)) * ( (Math.abs(new_score) * current_weight) / (current_sum_weights * (current_activity_count*current_activity_progress + 1)))



//System.out.println("AsypmthoticEstimator.addProgress new progress=" + progress); //DEBUG
//		progress += ((progress==0)?0.5:Math.pow((1 - progress),2)) * ( (new_score * current_weight) / 
//			current_sum_weights);
		count++ ;
	}
	
}
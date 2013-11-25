/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/** Mean knowledge/progress estimator
 * @author Michael V. Yudelson */

package edu.pitt.sis.paws.cbum;

public class TSREstimator extends ProgressEstimator
{
	public TSREstimator(double _fixed_weight, double _fixed_sum_weights)
	{
		super(_fixed_weight, _fixed_sum_weights);
	}

	/** Function returns current estimation of the progress/knowledge level
	 * @return - current estimation of the progress/knowledge level */
	public double getProgress() { return (progress); }

	/** Function updates the estimation using average score
	 * @param new_score - new score that is rendered into the estimation
	 * @param weight - weight of the score
	 * @param sum_weights - sum of all other weights (in given context)
	 */
	public void addProgress(double new_score, double weight,
		double sum_weights, String date_n_time)
	{
		count++ ;
		progress += new_score;
	}

}
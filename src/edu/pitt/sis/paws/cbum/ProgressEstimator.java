/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/** Abstract progress estimator
 * @author Michael V. Yudelson */

package edu.pitt.sis.paws.cbum;

public abstract class ProgressEstimator implements iProgressEstimator
{
	/** The current progress or knowledge level */
	protected double progress;
	
	/** The number of estimation points */
	protected int count;

	/** The fixed weight */
	protected double fixed_weight;
	
	/** The fixed sum of weights */
	protected double fixed_sum_weights;
	
	public ProgressEstimator(double _fixed_weight, double _fixed_sum_weights)
	{
		progress = 0;
		count = 0;
		fixed_weight = _fixed_weight;
		fixed_sum_weights = _fixed_sum_weights;
	}
	
	//-- Setters and Getters
	/** Getter for the current estimation of the progress/knowledge level
	 * @return - current estimation of the progress/knowledge level */
	public double getProgress() { return progress; }

	/** Getter for the current count of events
	 * @return - current count of events */
	public int getCount() { return count; }

	/** Getter for the current fixed weight
	 * @return - current fixed weight */
	public double getFixedWeight() { return fixed_weight; }

	/** Setter for the current fixed weight
	 * @param _fixed_weight - new fixed weight */
	public void setFixedSumWeighst(double _fixed_weight)
	{
		fixed_sum_weights = _fixed_weight;
	}

	/** Getter for the current fixed sum of weights
	 * @return - current fixed sum of weights */
	public double getFixedSumWeights() { return fixed_sum_weights; }

	/** Setter for the current fixed sum of weights
	 * @param _fixed_sum_weights - new fixed sum of weights */
	public void setFixedSumWeights(double _fixed_sum_weights)
	{
		fixed_sum_weights = _fixed_sum_weights;
	}

	//-- Other Methods
}
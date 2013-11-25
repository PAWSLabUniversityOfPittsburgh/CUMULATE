/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/** The interface wrapps the functionality of the progress or knowledge measure
 * estimator
 * @author Michael V. Yudelson */

package edu.pitt.sis.paws.cbum;

public interface iProgressEstimator 
{
	// CONSTANTS
	// 	Bloom Levels
	//		Names
	public static final String BLOOM_NAME_KNOWLEDGE = "Knowledge"; 
	public static final String BLOOM_NAME_COMPREHENSION = "Comprehension"; 
	public static final String BLOOM_NAME_APPLICATION = "Application"; 
	public static final String BLOOM_NAME_SYNTHESIS = "Synthesis"; 
	public static final String[] BLOOM_NAMES = { BLOOM_NAME_KNOWLEDGE,
		BLOOM_NAME_COMPREHENSION, BLOOM_NAME_APPLICATION, BLOOM_NAME_SYNTHESIS};
	
	//		Array Indices
	public static final int BLOOM_IDX_KNOWLEDGE = 0; 
	public static final int BLOOM_IDX_COMPREHENSION = 1; 
	public static final int BLOOM_IDX_APPLICATION = 2; 
	public static final int BLOOM_IDX_SYNTHESIS = 3; 
	//		Count
	public static final int BLOOM_COUNT = BLOOM_NAMES.length; 
	
	
	// Estimator types
	public static final int ESTIMATOR_MEAN = 1; 
	public static final int ESTIMATOR_ASSYMPTOTIC = 2; 
	
	
	public void addProgress(double new_score, double weight, 
		double sum_weights, String date_n_time);
	public double getProgress();
	public int getCount();
	public void setFixedSumWeighst(double _fixed_sum_weights);
}
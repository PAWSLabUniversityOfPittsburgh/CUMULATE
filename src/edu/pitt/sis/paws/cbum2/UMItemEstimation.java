package edu.pitt.sis.paws.cbum2;

public class UMItemEstimation extends edu.pitt.sis.paws.core.BiItem<User, iUMItem>
{
	// Constants
	public static final int KNOWLEDGE_LEVEL_IDX_KNOWLEDGE = 0; 
	public static final int KNOWLEDGE_LEVEL_IDX_COMPREHENSION = 1; 
	public static final int KNOWLEDGE_LEVEL_IDX_APPLICATION = 2; 
	
	public static final String KNOWLEDGE_LEVEL_NAME_KNOWLEDGE = "knowledge"; 
	public static final String KNOWLEDGE_LEVEL_NAME_COMPREHENSION = "comprehension"; 
	public static final String KNOWLEDGE_LEVEL_NAME_APPLICATION = "application"; 

	public static final String[] KNOWLEDGE_LEVEL_NAMES = { 
			KNOWLEDGE_LEVEL_NAME_KNOWLEDGE, KNOWLEDGE_LEVEL_NAME_COMPREHENSION, 
			KNOWLEDGE_LEVEL_NAME_APPLICATION};
	
	public static final int KNOWLEDGE_LEVEL_COUNT = KNOWLEDGE_LEVEL_NAMES.length; 
	
	public static final int ESTIMATOR_TYPE_DEPTH_PROGRESS = 1;
	public static final int ESTIMATOR_TYPE_DEPTH_KNOWLEDGE = 
			KNOWLEDGE_LEVEL_NAMES.length;
	
	// properties
	
	protected int estimator_type;
	
	protected UMItemEstimationFacet[] estimator_levels;
	
	public UMItemEstimation()
	{
		super();
		estimator_type = UMComponent.UMCOMPONENT_TYPE_PROGRESS;
	}
	
	public UMItemEstimation(User _user, iUMItem _umitem)
	{
		super(_user, _umitem);
		estimator_type = UMComponent.UMCOMPONENT_TYPE_PROGRESS;
	}
	
	public UMItemEstimation(User _user, iUMItem _umitem, int _est_type)
	{
		super(_user, _umitem);
		estimator_type = _est_type;
		if(_est_type != UMComponent.UMCOMPONENT_TYPE_PROGRESS || 
				_est_type != UMComponent.UMCOMPONENT_TYPE_KNOWLEDGE)
		{
			System.err.println("!! CBUM::UMItemEstimator estimator type - " + 
					_est_type + " - doesn't exist, seting to default (progress)");
			estimator_type = UMComponent.UMCOMPONENT_TYPE_PROGRESS;
		}
		switch (estimator_type)
		{
			case UMComponent.UMCOMPONENT_TYPE_PROGRESS:
			{
				estimator_levels = 
						new UMItemEstimationFacet[ESTIMATOR_TYPE_DEPTH_PROGRESS];
			}
			break;
			case UMComponent.UMCOMPONENT_TYPE_KNOWLEDGE:
			{
				estimator_levels = 
						new UMItemEstimationFacet[ESTIMATOR_TYPE_DEPTH_KNOWLEDGE];
			}
			break;
		}
		
	}// end of -- UMItemEstimation
	
	public iUMItem getUMItem() { return getItem2(); }

	public void setUMItem(iUMItem _um_item) { setItem2(_um_item); }

	public User getUser() { return getItem1(); }

	public void setUser(User _user) { setItem1(_user); }
	
	public int getEstimationFacetCount(int _idx)
	{
		if( (_idx < 0) || (_idx > (estimator_levels.length - 1)) )
		{
			System.err.println("!! CBUM::getEstimationFacetCount facet index - " +
					_idx + " - misspecified, for " + estimator_levels.length + 
					" facets total.");
			return 0;
		}
		else
			return estimator_levels[_idx].count;
	}
	
	public float getEstimationFacetProgress(int _idx)
	{
		if( (_idx < 0) || (_idx > (estimator_levels.length - 1)) )
		{
			System.err.println("!! CBUM::getEstimationFacetCount facet index - " +
					_idx + " - misspecified, for " + estimator_levels.length + 
					" facets total.");
			return 0.0f;
		}
		else
			return estimator_levels[_idx].progress;
	}
	
}

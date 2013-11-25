package edu.pitt.sis.paws.cbum2;

import edu.pitt.sis.paws.core.WeightedItemVector;
import edu.pitt.sis.paws.core.BiItemVector;

public class UMComponent extends UMItem
{
	// Constants
	public static final int UMCOMPONENT_TYPE_PROGRESS = 0;
	public static final int UMCOMPONENT_TYPE_KNOWLEDGE = 1;

	protected Corpus corpus;
	
	protected BiItemVector<User, UMComponent> estimations;
	
	protected WeightedItemVector<iUMItem> metadata;

	public UMComponent()
	{
		super();
		estimations = new BiItemVector<User, UMComponent>();
		metadata = new WeightedItemVector<iUMItem>();
		corpus = null;
	}
	
	public UMComponent(int _id, String _title, String _uri)
	{
		super(_id, _title, _uri);
		estimations = new BiItemVector<User, UMComponent>();
		metadata = new WeightedItemVector<iUMItem>();
		corpus = null;
	}

	public BiItemVector<User, UMComponent> getEstimators() { return estimations; }

	public Corpus getCorpus() { return corpus; }
	
	public void setCorpus(Corpus _corpus) {  corpus = _corpus; }
	
	public WeightedItemVector<iUMItem> getMetadata() { return metadata; }
	
}

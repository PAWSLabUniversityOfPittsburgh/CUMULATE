package edu.pitt.sis.paws.cbum2;

import edu.pitt.sis.paws.core.Item2Vector;

public class Corpus extends UMItem
{
	static final long serialVersionUID = 9L;
	
	protected Item2Vector<UMComponent> content;

	public Corpus()
	{
		super();
		content = new Item2Vector<UMComponent>();
	}
	
	public Corpus(int _id, String _title, String _uri)
	{
		super(_id, _title, _uri);
		content = new Item2Vector<UMComponent>();
	}

	public Item2Vector<UMComponent> getCorpora() { return content; }

}

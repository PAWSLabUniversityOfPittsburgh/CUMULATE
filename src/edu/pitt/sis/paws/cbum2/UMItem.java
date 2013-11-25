package edu.pitt.sis.paws.cbum2;

import edu.pitt.sis.paws.core.Item2;
import edu.pitt.sis.paws.core.Item2Vector;

public abstract class UMItem extends Item2
{
	protected iUMItem parent;
	protected Item2Vector<iUMItem> children;

	public UMItem()
	{
		super();
		parent = null;
		children = new Item2Vector<iUMItem>();
	}
	
	public UMItem(int _id, String _title, String _uri)
	{
		super(_id, _title, _uri);
		parent = null;
		children = new Item2Vector<iUMItem>();
	}
	
	public iUMItem getParent() { return parent; }

	public void setParent(iUMItem _parent) { parent = _parent; }

	public Item2Vector<iUMItem> getChildren() { return children; }


}

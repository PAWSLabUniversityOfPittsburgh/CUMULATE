package edu.pitt.sis.paws.cbum2;

import edu.pitt.sis.paws.core.Item2Vector;
import edu.pitt.sis.paws.core.iItem2;

public interface iUMItem extends iItem2
{
	public iUMItem getParent();

	public void setParent(iUMItem _parent);

	public Item2Vector<iUMItem> getChildren();
	
}

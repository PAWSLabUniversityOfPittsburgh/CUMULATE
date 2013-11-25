package edu.pitt.sis.paws.cbum.report;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class ProgressEstimatorReportItem implements iProgressEstimatorReportItem, Serializable
{
	/** Identifier of the reported entity*/
	private String id;
	
	public ProgressEstimatorReportItem(String _id)
	{
		id = _id;
	}
	
	public String getId() { return id; }

	public void setId(String _id) { id = _id; }

	public static iProgressEstimatorReportItem PERIFactory(String _id, int _requested_level)
	{
		return (_requested_level>0)? new ProgressEstimatorReport(_id): new ProgressEstimatorMultiLevelConceptReport(_id);
	}
	
	/** Writing the object into the stream
	 * @param out - the output stream */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
	}
	
	/** Reading the object from the stream
	 * @param in - the input stream */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
	}

	
}

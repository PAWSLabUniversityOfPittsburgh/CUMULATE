/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/** Class representing an Application that wrapps Activities
 * @author Michael V. Yudelson */

package edu.pitt.sis.paws.cbum;

import edu.pitt.sis.paws.core.*;

public class App extends Item2
{
	static final long serialVersionUID = 488L;
	
	private boolean open_core;
	private boolean single_activity_report;
	private boolean anonymous_report;
	private String uri_prefix;
	protected Item2Vector<Activity> activities;
	
	public App(int _id, String _title, boolean _open_core, boolean _single_activity_report, 
			boolean _anonymous_report, String _uri_prefix)
	{
		super(_id, _title, "no uri");
		open_core = _open_core;
		single_activity_report = _single_activity_report;
		anonymous_report = _anonymous_report;
		uri_prefix = _uri_prefix;
		
		activities = new Item2Vector<Activity>();
	}
	
	public boolean getOpenCore() { return open_core; }
	public boolean getSingleActivityReport() { return single_activity_report; }
	public boolean getAnonymousReport() { return anonymous_report; }
	public String getURIPrefix() { return uri_prefix; }
}
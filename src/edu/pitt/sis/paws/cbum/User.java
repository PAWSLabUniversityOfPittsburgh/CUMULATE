/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */
 
/** Class representing a single User or a Group of users
 * @author Michael V. Yudelson */
 
package edu.pitt.sis.paws.cbum;

import java.util.GregorianCalendar;
import edu.pitt.sis.paws.core.Item2;
import edu.pitt.sis.paws.core.Item2Vector;

public class User extends Item2
{
	static final long serialVersionUID = 4776L;
	
	/** Application user is registered with */
	protected Item2Vector<App> apps;
	/** An array of links to Activities and count/average scores */
	protected Item2Vector<UserActivity> activity_links;
	/** An array of sub|super-ordinate Users/Groups */
	protected Item2Vector<User> ordinates;
	
	//cache for AnnotatEd
	public UserActivity cached_activity = null;
	public String cached_datentime = null;
	
	// Locking
	/** Timestamp in ms of the momend when user was locked for updates, unlocked = -1 */
	protected long locked_ms;
	
	// Udate hashes
	protected String [] update_hashes;
	
	public User(int _id, String _login, String _uri, int _max_app_id)
	{
		super(_id, _login, _uri);
		apps = new Item2Vector<App>();
		activity_links = new Item2Vector<UserActivity>();
		ordinates = new Item2Vector<User>();
		cached_activity = null;
		cached_datentime = null;
		// Lock
		locked_ms = 0;
		// Update hashes
		update_hashes = new String[_max_app_id+1];
		for(int i=0; i<=_max_app_id; i++) update_hashes[i] = "";
	}
	
	public void updateHash(String _hash, int _app_id)
	{
		if(update_hashes.length < (_app_id+1))
			System.out.println("!!! [CBUM] trying to hash out of range update  for user:" + this.getTitle() + " with app:" + _app_id + " hash buffer size:" + update_hashes.length);
		update_hashes[_app_id] = _hash;
		update_hashes[0] = _hash;
	}
	
	public String getHash(int _app_id)
	{
		if(update_hashes.length < (_app_id+1))
		{
			System.out.println("!!! [CBUM] trying to get out of range hash for user:" + this.getTitle() + " with app:" + _app_id + " hash buffer size:" + update_hashes.length);
			return "";
		}
		return update_hashes[_app_id];
	}
	
	/** Obtain timestamp of the lock
	 * @return timestamp of the lock
	 */
	public long getLock() { return locked_ms; }
	
	/** Set lock
	 */
	public long setLocked()
	{
		GregorianCalendar date = new GregorianCalendar();
		locked_ms = date.getTimeInMillis();
		return locked_ms;
	}
	
	/**
	 * Removes lock
	 */
	public long unLock()
	{
		locked_ms = 0;
		GregorianCalendar date = new GregorianCalendar();
		return date.getTimeInMillis();
	}

	public String toString()
	{
		return "[User login/name:'" + this.getTitle() + "' id:" + this.getId() + "]";
	}
	
	//-- Setters and Getters
	/** Getter for the Application user is registered with
	 * @return the Application user is registered with */
	public Item2Vector<App> getApps() { return apps; }

	/**Getter for an array of activity links
	 * @return an array of activity links */
	public Item2Vector<UserActivity> getActivityLinks()
	{
		return activity_links;
	}

	/** Getter for an array of sub|super-ordinate Users/Groups
	 * @return an array of sub|super-ordinate Users/Groups */
	public Item2Vector<User> getOrdinates()
	{
		return ordinates;
	}

	//-- Other Methods
//	/** Adds another user activity event
//	 * @param _act - the Activity for which the event has been fired
//	 * @param _score - the score assigned to the event */
//	public void addUserActivity(UserActivity _ua)
//	{
//		activity_links.add(_ua);
//		UserActivity ua = null;
//		if(_act == null)
//		{
//			System.out.println("[CBUM] User.addUserActivity ERROR! " +
//				"Activity specified is NULL.");
//			return;
//		}
//		ua = activity_links.findByTitle(_act.getTitle());
//		if(ua==null)
//		{
//			ua = new UserActivity(_act.getId(), _act.getTitle(), this,
//				_act/*, true*/);
//			activity_links.add(ua);
//		}
//	}
}
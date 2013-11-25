package edu.pitt.sis.paws.cbum2;

import edu.pitt.sis.paws.cbum.App;
import edu.pitt.sis.paws.cbum.UserActivity;
import edu.pitt.sis.paws.core.Item2;
import edu.pitt.sis.paws.core.Item2Vector;

public class User  extends Item2
{
	static final long serialVersionUID = 4667L;

	/** Application user is registered with */
	protected Item2Vector<App> apps;
	/** An array of links to Activities and count/average scores */
	protected Item2Vector<UserActivity> activity_links;
	/** An array of sub|super-ordinate Users/Groups */
	protected Item2Vector<User> ordinates;
	
	public User(int _id, String _login, String _uri)
	{
		super(_id, _login, _uri);
		apps = new Item2Vector<App>();
		activity_links = new Item2Vector<UserActivity>();
		ordinates = new Item2Vector<User>();
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

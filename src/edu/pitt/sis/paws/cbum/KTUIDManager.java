package edu.pitt.sis.paws.cbum;

import java.sql.*;
import edu.pitt.sis.paws.core.utils.SQLManager;

public class KTUIDManager extends UIDManager
{
	public KTUIDManager(Connection _conn) throws SQLException
	{
		super(_conn);
	}
	
	@Override
	public int userExists(String login) throws SQLException
	{
		int result = -1;
		String sql = "SELECT * FROM ent_user WHERE Login='" + login + "' AND IsGroup=0;";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while(rs.next())
		{
			result = rs.getInt("UserID");
			break;
		}
		rs.close();
		rs = null;
		stmt.close();
		stmt = null;
		
		return result;
	}
	
	@Override
	public int groupExists(String group) throws SQLException
	{
		int result = -1;
		String sql = "SELECT * FROM ent_user WHERE Login='" + group + "' AND IsGroup=1;";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while(rs.next())
		{
			result = rs.getInt("UserID");
			break;
		}
		rs.close();
		rs = null;
		stmt.close();
		stmt = null;
		
		return result;
	}
	
	@Override
	public String addUser(String login, String fname, String lname,
			String md5password, String email, String organization, String city,
			String country, String how_ended_up_here, String group) throws SQLException
	{
		String result = UIDManager.RESULT_OK;
		
		if(userExists(login) != -1)
		{
			result = "User '" + login + "' already exists in KnowledgeTree.";
			return result;
		}
		
		int group_id = groupExists(group);
		if(group_id == -1)
		{
			result = "Group '" + group + "' doesn't exists in KnowledgeTree.";
			return result;
		}
		
		conn.setAutoCommit(false);
		
		String qry = "INSERT INTO ent_user (Login, Name, Pass, IsGroup, Sync, EMail, Organization, City, Country, How) VALUES(" +
				"'" + login + "', '" + SQLManager.stringUnquote(lname) + ", " + SQLManager.stringUnquote(fname) + "', '" + md5password + "', 0,1, '" + 
				email + "', '" + SQLManager.stringUnquote(organization) + "', '" + SQLManager.stringUnquote(city) + "', '" + SQLManager.stringUnquote(country) + "', '" +   SQLManager.stringUnquote(how_ended_up_here) + "');";
		int user_id = 0;
		
		PreparedStatement stmt = conn.prepareStatement(qry);
		stmt.executeUpdate();
		
		// get user id
		qry = "SELECT MAX(LAST_INSERT_ID(UserID)) AS LastID FROM ent_user WHERE Login='" + login + "';";
		stmt = conn.prepareStatement(qry);
		ResultSet  rs = stmt.executeQuery();
		while(rs.next())
		{
			user_id = rs.getInt("LastID");
		}
		
		// insert into group
		qry = "INSERT INTO rel_user_user (ParentUserID, ChildUserID) VALUES(" + group_id + ", " + user_id + " );";
		stmt = conn.prepareStatement(qry);
		stmt.executeUpdate();
	
		// insert authentication Realm record
		qry = "INSERT INTO seq_role (Login, Role) VALUES('" + login + "', 'user');";
		stmt = conn.prepareStatement(qry);
		stmt.executeUpdate();
		
		stmt.close();
		stmt = null;
		rs.close();
		rs = null;
		
		return result;
	}

	@Override
	public boolean authenticateUser(String login, String md5password,
			String group) throws SQLException
	{
		boolean result = false;
		String sql = "SELECT * FROM ent_user u JOIN rel_user_user uu ON(u.UserID=uu.ChildUserID) " +
				"JOIN ent_user g ON(uu.ParentUserID=g.UserID) WHERE u.Login='" + login + "' AND u.IsGroup=0 " +
				"AND u.Pass='" + md5password + "' AND g.Login='" + group + "' AND g.IsGroup=1;";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while(rs.next())
		{
			result = true;
			break;
		}
		rs.close();
		rs = null;
		stmt.close();
		stmt = null;
		
		return result;
	}

}

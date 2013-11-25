package edu.pitt.sis.paws.cbum;

import java.sql.*;

public class WADEInUIDManager extends UIDManager
{
	public final static int ID_NONE = -1;
	public final static String CONN_STR = "jdbc:mysql://localhost/wadein?user=student&password=student";
	
	public WADEInUIDManager(Connection _conn) throws SQLException
	{
		super(_conn);
	}
	
	@Override
	public boolean authenticateUser(String login, String md5password,
			String group) throws SQLException
	{
		boolean result = false;
		
		String sql = "SELECT user_id FROM users WHERE login = '" + login +"';"; // + "' AND `group` = '" + group + "'";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();

		result = rs.next();

		rs.close();
		rs = null;
		stmt.close();
		stmt = null;
		
		return result;
	}
	
	@Override
	public int userExists(String login) throws SQLException
	{		
		int result = -1;
		String sql = "SELECT user_id FROM users WHERE login = '" + login + "'";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while(rs.next())
		{
			result = rs.getInt("user_id");
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
		
		// Getting the ids of operations:
		String query = "SELECT operation_id FROM operations";
		Statement statOperations = conn.createStatement();
		ResultSet rsOperations = statOperations.executeQuery(query);
		
		Statement statUserModel = conn.createStatement();
		
		// Adding the user to the 'users' table:
		query = 
			"INSERT INTO users (login, passwd, `group`) " +
			"VALUES ('" + login + "', '" + md5password + "', '" + group + "')";
		int userId = execAndGetId(conn, query);
		if (userId == WADEInUIDManager.ID_NONE) throw new SQLException("Adding user '" + login + "' to 'users' table failed.");
		
		// Adding the user to the 'user_model' table:
		while (rsOperations.next()) {
			String operationId = rsOperations.getString("operation_id");
			float klevel = 0;
			
			query = 
				"INSERT INTO user_model_c (user_id, klevel_init, operation_id) " +
				"VALUES (" + userId + ", " + klevel + ", " + operationId + ")";
			statUserModel.executeUpdate(query);
			
			query = 
				"INSERT INTO user_model_j (user_id, klevel_init, operation_id) " +
				"VALUES (" + userId + ", " + klevel + ", " + operationId + ")";
			statUserModel.executeUpdate(query);
		}
		
		// Clean-up:
		statUserModel.close();
		rsOperations.close();
		statOperations.close();
		
		return result;
	}	
	
	private static int execAndGetId(Connection conn, String query)
		throws SQLException {
		
		int retVal = ID_NONE;
		
		PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		ps.executeUpdate(query);
		
		ResultSet rs = ps.getGeneratedKeys();
		if (rs.next())
			retVal = rs.getInt("GENERATED_KEY");
		rs.close();
		
		ps.close();

		return retVal;
	}
}

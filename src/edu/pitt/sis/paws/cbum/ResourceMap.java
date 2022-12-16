/* Disclaimer:
 * 	Java code contained in this file is created as part of educational
 *    research and development. It is intended to be used by researchers of
 *    University of Pittsburgh, School of Information Sciences ONLY.
 *    You assume full responsibility and risk of lossed resulting from compiling
 *    and running this code.
 */

/** This class is a repository of resources for Concept-Based User Model
 * @author Michael V. Yudelson
 */

package edu.pitt.sis.paws.cbum;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import edu.pitt.sis.paws.cbum.report.ProgressEstimatorReport;
import edu.pitt.sis.paws.cbum.report.ProgressEstimatorReportItem;
import edu.pitt.sis.paws.cbum.report.iProgressEstimatorReportItem;
import edu.pitt.sis.paws.cbum.structures.Activity2ConceptMapping4Report;
import edu.pitt.sis.paws.cbum.structures.ActivityReport;
import edu.pitt.sis.paws.core.Item2Vector;

public class ResourceMap
{
	/** An array of Users on the C-B UM Server */
	protected Item2Vector<User> users;
	/** An array of Groups on the C-B UM Server */
	protected Item2Vector<User> groups;
	/** An array of Activities on the C-B UM Server */
	protected Item2Vector<Activity> activities;
	/** An array of Applications on the C-B UM Server */
	protected Item2Vector<App> apps;
	/** An array of Concepts on the C-B UM Server */
	protected Item2Vector<Concept> concepts;
	/** An array of Domains - concept sets on the C-B UM Server */
	protected Item2Vector<Concept> domains;
	
	public int max_app_id = 0;

	public ResourceMap(ServletContext context)
	{
		users = new Item2Vector<User>();
		groups = new Item2Vector<User>();
		activities = new Item2Vector<Activity>();
		apps = new Item2Vector<App>();
		concepts = new Item2Vector<Concept>();
		domains = new Item2Vector<Concept>();
		loadData(context);
	}

	/** Loading data from database into Resource Map */
	private void loadData(ServletContext context)
	{
		System.out.println("mem.total: " + Runtime.getRuntime().totalMemory());
		System.out.println("mem.max  : " + Runtime.getRuntime().maxMemory());
		System.out.println("mem.free : " + Runtime.getRuntime().freeMemory());
		
//		ServerDaemon sd = ServerDaemon.getInstance(context);
		Connection conn = null;
		ResultSet rs1 = null;
		PreparedStatement stmt1 = null;
		ResultSet rs2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs3 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs4 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs5 = null;
		PreparedStatement stmt5 = null;
		ResultSet rs6 = null;
		PreparedStatement stmt6 = null;
		ResultSet rs7 = null;
		PreparedStatement stmt7 = null;
		ResultSet rs8 = null;
		PreparedStatement stmt8 = null;
		ResultSet rs9 = null;
		PreparedStatement stmt9 = null;
		ResultSet rs10 = null;
		PreparedStatement stmt10 = null;
		ResultSet rs11 = null;
		PreparedStatement stmt11 = null;
		ResultSet rs12 = null;
		PreparedStatement stmt12 = null;
		ResultSet rs13 = null;
		PreparedStatement stmt13 = null;
		ResultSet rs14 = null;
		PreparedStatement stmt14 = null;
		
		long st_struc = 0;
		long fn_struc = 0;
		long st_ua = 0;
		long fn_ua = 0;
		
		try
		{// Try block of the loadData
			conn = ServerDaemon.getConnection();
			
			st_struc = System.nanoTime();
			
			// Load Applications
			String qry = "SELECT * FROM ent_app;";
			stmt1 = conn.prepareStatement(qry);
			rs1 = stmt1.executeQuery();
			int counter = 0;
			while(rs1.next())
			{
				int app_id = rs1.getInt("AppID");;
				String app_title = rs1.getString("Title");
				int app_open_core = rs1.getInt("OpenCorpus");
				int single_activity_report = rs1.getInt("SingleActivityReport");
				int anonymous_report = rs1.getInt("AnonymousUser");
				String uri_prefix = rs1.getString("URIPrefix");
				apps.add( new App(app_id, app_title, (app_open_core==1), (single_activity_report==1), (anonymous_report==1), uri_prefix));
				// calculate maximum id of the app
				max_app_id = (max_app_id<app_id)?app_id:max_app_id;
				counter ++;
			}
			rs1.close();
			stmt1.clearBatch();
			rs1 = null;
			stmt1 = null;
			
System.out.println("... [CBUM] ResMap.loadData Applications " + counter + " added."); /// DEBUG

			// Load Users
			qry = "SELECT * FROM ent_user WHERE IsGroup=0;";
			stmt2 = conn.prepareStatement(qry);
			rs2 = stmt2.executeQuery();
			counter = 0;
			while(rs2.next())
			{
				int user_id = rs2.getInt("UserID");;
				String user_uri = rs2.getString("URI");;
				String user_login = rs2.getString("Login");
				
				users.add( new User(user_id, user_login,user_uri, max_app_id));
				counter ++;
			}
			rs2.close();
			stmt2.clearBatch();
			rs2 = null;
			stmt2 = null;

			System.out.println("... [CBUM] ResMap.loadData Users " + counter + " added."); /// DEBUG

			// Load Groups
			qry = "SELECT * FROM ent_user WHERE IsGroup=1;";
			stmt3 = conn.prepareStatement(qry);
			rs3 = stmt3.executeQuery();
			counter = 0;
			while(rs3.next())
			{
				int group_id = rs3.getInt("UserID");
				String group_uri = rs3.getString("URI");;
				String group_title = rs3.getString("Login");
//				App group_app = apps.findById(app_id);
//				if(group_app == null)
//					System.out.println("[CBUM] ResMap.loadData " +
//						"ERROR: Group's App is null");
				groups.add( new User(group_id, group_title,group_uri,max_app_id));
				counter ++;
			}
			rs3.close();
			stmt3.clearBatch();
			rs3 = null;
			stmt3 = null;

			System.out.println("... [CBUM] ResMap.loadData Groups " + counter + " added."); /// DEBUG
			
			// Load User-Application links
			qry = "SELECT * FROM rel_app_user;";
			stmt4 = conn.prepareStatement(qry);
			rs4 = stmt4.executeQuery();
			counter = 0;
			while(rs4.next())
			{
				int app_id = rs4.getInt("AppID");;
				int user_id = rs4.getInt("UserID");;
				App app = apps.findById(app_id);
				User user = users.findById(user_id);
				if(user == null)
					user = groups.findById(user_id);
				user.getApps().add(app);
				counter ++;
			}
			rs4.close();
			stmt4.clearBatch();
			rs4 = null;
			stmt4 = null;

			System.out.println("... [CBUM] ResMap.loadData User-Application links " + counter + " added."); /// DEBUG
			
			// Load User-Group links
			qry = "SELECT * FROM rel_user_user;";
			stmt5 = conn.prepareStatement(qry);
			rs5 = stmt5.executeQuery();
			counter = 0;
			while(rs5.next())
			{
				int user_id = rs5.getInt("UserID");
				int group_id = rs5.getInt("GroupID");
//				int app_id = rs.getInt("AppID");
				User user = users.findById(user_id);
				User group = groups.findById(group_id);
//				App app = apps.findById(app_id);
				if( (user == null) || (group == null) )
				{
					System.out.println("[CBUM] ResMap.loadData " +
						"ERROR: Group-User link is brocken. " +
						"User or Group is NULL. U.Id=" + user_id +
						" G.Id=" + group_id);
					continue;
				}
//				if((app == null) ||
//					((user.getApp()!=null)&&(group.getApp()!=null)
//						&&(user.getApp().getId() !=
//							group.getApp().getId())
//						&&(user.getApp().getId() !=
//							app.getId())
//						&&(group.getApp().getId() !=
//							app.getId())
//					)
//				  )
//				{
//					System.out.println("[CBUM] ResMap.loadData " +
//						"ERROR: Group-User link is brocken. " +
//						"Application is NULL or User, Goup, & App" +
//						" Id's don't match. U.Id=" + 
//						user.getApp().getId() + " G.Id=" + 
//						group.getApp().getId() + " A.Id=" +
//						app.getId() );
//					continue;
//				}
				// link 2-ways
				user.getOrdinates().add(group);
				group.getOrdinates().add(user);
				counter ++;
			}
			rs5.close();
			stmt5.clearBatch();
			rs5 = null;
			stmt5 = null;

			System.out.println("... [CBUM] ResMap.loadData User-Group links " + counter + " added."); /// DEBUG
			
			// Load Activities
			qry = "SELECT * FROM ent_activity;";
			stmt6 = conn.prepareStatement(qry);
			rs6 = stmt6.executeQuery();
			counter = 0;
			while(rs6.next())
			{
				int activity_id = rs6.getInt("ActivityID");
				String activity_uri = rs6.getString("URI");
				int app_id = rs6.getInt("AppID");;
				String activity = rs6.getString("Activity");
				App activity_app = apps.findById(app_id);
				if(activity_app == null)
					System.out.println("[CBUM] ResMap.loadData " +
						"ERROR: Activity's App is null");
				Activity new_act = new Activity(activity_id, activity, activity_uri,
						activity_app);
				activities.add( new_act );
				activity_app.activities.add( new_act );
				counter ++;
			}
			rs6.close();
			stmt6.clearBatch();
			rs6 = null;
			stmt6 = null;

			System.out.println("... [CBUM] ResMap.loadData Activities " + counter + " added."); /// DEBUG
			
			// Load Activity-Subactivity links
			qry = "SELECT * FROM rel_activity_activity;";
			stmt7 = conn.prepareStatement(qry);
			rs7 = stmt7.executeQuery();
			counter = 0;
			while(rs7.next())
			{
				int parent_id = rs7.getInt("ParentActivityID");
				int child_id = rs7.getInt("ChildActivityID");
				int app_id = rs7.getInt("AppID");;
				Activity child = activities.findById(child_id);
				Activity parent = activities.findById(parent_id);
				App app = apps.findById(app_id);
				if( (child == null) || (parent == null) )
				{
					System.out.println("[CBUM] ResMap.loadData " +
						"ERROR: Activity-Activity link is brocken" +
						". Parent or Child is NULL. P.Id=" + 
						parent_id + " C.Id=" + child_id);
					continue;
				}
				if((app == null) ||
					((child.getApp()!=null)&&(parent.getApp()!=null)
						&&(child.getApp().getId() !=
							parent.getApp().getId())
						&&(child.getApp().getId() !=
							app.getId())
						&&(parent.getApp().getId() !=
							app.getId())
					)
				  )
				{
					System.out.println("[CBUM] ResMap.loadData " +
						"ERROR: Activity-Activity link is brocken" +
						". Application is NULL or Parent, Child, " +
						"& App Id's don't match. C.Id=" + 
						child.getApp().getId() + " P.Id=" + 
						parent.getApp().getId() + " A.Id=" +
						app.getId() );
					continue;
				}
				// link 2-ways
				child.setParent(parent);
				parent.getChildren().add(child);
				counter ++;
			}
			rs7.close();
			stmt7.clearBatch();
			rs7 = null;
			stmt7 = null;

			System.out.println("... [CBUM] ResMap.loadData Activity-Subactivity links " + counter + " added."); /// DEBUG

			// Load Concepts
			qry = "SELECT * FROM ent_concept;";
			stmt8 = conn.prepareStatement(qry);
			rs8 = stmt8.executeQuery();
			counter = 0;
			while(rs8.next())
			{
				int concept_id = rs8.getInt("ConceptID");
				String concept_title = rs8.getString("Title");
				concepts.add( new Concept(concept_id, concept_title) );
				counter ++;
			}
			rs8.close();
			stmt8.clearBatch();
			rs8 = null;
			stmt8 = null;
System.out.println("... [CBUM] ResMap.loadData Concepts " + counter + " added."); /// DEBUG

			// Load Concept-Concept Links
			qry = "SELECT * FROM rel_concept_concept;";
			stmt9 = conn.prepareStatement(qry);
			rs9 = stmt9.executeQuery();
			counter = 0;
			while(rs9.next())
			{
				int parent_concept_id = rs9.getInt("ParentConceptID");
				int child_concept_id = rs9.getInt("ChildConceptID");
				Concept parent = concepts.findById(parent_concept_id);
				Concept child = concepts.findById(child_concept_id);
				child.setParent(parent);
				parent.getChildren().add(child);
				counter ++;
			}
			rs9.close();
			stmt9.clearBatch();
			rs9 = null;
			stmt9 = null;
System.out.println("... [CBUM] ResMap.loadData Concept-Concept Links " + counter + " added."); /// DEBUG
			
			// Load Domains
			qry = "SELECT * FROM ent_domain;";
			stmt10 = conn.prepareStatement(qry);
			rs10 = stmt10.executeQuery();
			counter = 0;
			while(rs10.next())
			{
				int domain_id = rs10.getInt("DomainID");
				String concept_title = rs10.getString("Title");
				domains.add( new Concept(domain_id, concept_title) );
				counter ++;
			}
			rs10.close();
			stmt10.clearBatch();
			rs10 = null;
			stmt10 = null;

			System.out.println("... [CBUM] ResMap.loadData Domains " + counter + " added."); /// DEBUG

			// Load Domain-Concept Links
			qry = "SELECT * FROM rel_domain_concept;";
			stmt11 = conn.prepareStatement(qry);
			rs11 = stmt11.executeQuery();
			counter = 0;
			while(rs11.next())
			{
				int domain_id = rs11.getInt("DomainID");
				int concept_id = rs11.getInt("ConceptID");
				Concept domain = domains.findById(domain_id);
				Concept concept = concepts.findById(concept_id);
				if(domain!=null) domain.getChildren().add(concept);
				else System.out.println("!!! [CBUM] null domain by id= " + domain_id);
				if(concept!=null) concept.setDomain(domain);
				else System.out.println("!!! [CBUM] null concept by id= " + concept_id);
				counter ++;
			}
			rs11.close();
			stmt11.clearBatch();
			rs11 = null;
			stmt11 = null;
			
System.out.println("... [CBUM] ResMap.loadData Domain-Concept Links " + counter + " added."); /// DEBUG
			
			// Load Concept-Activity Links
			qry = "SELECT * FROM rel_concept_activity;";
			stmt12 = conn.prepareStatement(qry);
			rs12 = stmt12.executeQuery();
			counter = 0;
//System.out.println("... [CBUM] ResMap.loadData Concepts " + concepts.size() + " added.---2"); /// DEBUG
//for(int i=0; i<concepts.size(); i++) /// DEBUG
//{ /// DEBUG
//if(concepts.get(i).getPrereqWeightSum()>0) /// DEBUG
//System.out.println("... [CBUM] ResMap.loadData Concept.id="+concepts.get(i).getId()+ " name=" + concepts.get(i).getTitle() +" PrereqSum=" + concepts.get(i).getPrereqWeightSum()); /// DEBUG
//if(concepts.get(i).getOutcomeWeightSum()>0) /// DEBUG
//System.out.println("... [CBUM] ResMap.loadData Concept.id="+concepts.get(i).getId()+ " name=" + concepts.get(i).getTitle() +" OutcomeSum=" + concepts.get(i).getOutcomeWeightSum()); /// DEBUG
//} /// DEBUG
//System.out.println("... [CBUM] ResMap.loadData ---------2"); /// DEBUG
			while(rs12.next())
			{
//System.out.println("### [!!!CBUM] ResMap.loadData Concept.id="+concepts.findById(14).getId()+ " name=" + concepts.findById(14).getTitle() +" PrereqSum=" + concepts.findById(14).getPrereqWeightSum()); /// DEBUG
				int concept_id = rs12.getInt("ConceptID");
				int activity_id = rs12.getInt("ActivityID");
				double weight = rs12.getDouble("Weight");
				int direction = rs12.getInt("Direction");
				Concept concept = concepts.findById(concept_id);
				Activity activity = activities.findById(activity_id);
//if(concept.getPrereqWeightSum()>0 && activity.getTitle().equals("4.9")) /// DEBUG
//System.out.println("### [!CBUM] ResMap.loadData Concept.id="+concept.getId()+ " name=" + concept.getTitle() +" PrereqSum=" + concept.getPrereqWeightSum()); /// DEBUG
//if(concept.getOutcomeWeightSum()>0 && activity.getTitle().equals("4.9")) /// DEBUG
//System.out.println("### [!CBUM] ResMap.loadData Concept.id="+concept.getId()+ " name=" + concept.getTitle() +" OutcomeSum=" + concept.getOutcomeWeightSum()); /// DEBUG
				ConceptActivity ca = concept.addActivityLink(activity, 
					weight, direction);
				activity.addConceptLink(ca);
				counter ++;
			}
			
			fn_struc = System.nanoTime();
			
			rs12.close();
			stmt12.clearBatch();
			rs12 = null;
			stmt12 = null;
System.out.println("... [CBUM] ResMap.loadData Concept-Activity Links " + counter + " added."); /// DEBUG
System.out.println("... [CBUM] structure loaded in " + (float)(fn_struc-st_struc)/1000000 + " ms"); /// DEBUG
			

//			// Expand Sergey's RDF as foreign stuff
//			counter = 0;
//			int counter2 = 0;
//			try
//			{
//				CourseModel courseModel = new CourseModel("http://www.sis.pitt.edu/~paws/ont/peterb.rdf"); //"http://www.sis.pitt.edu/~paws/ont/peterb.rdf"
//				
//				// add domain
//				Concept new_domain = new Concept(0, "peterb.rdf");
//				this.domains.add(new_domain);
//				
//				// create topics
//				for(int i=0; i<courseModel.getTopicList().size(); i++)
//				{
//					String topic_uri =  courseModel.getTopicList().get(i);
//					String topic_name = courseModel.getTopicTitle(topic_uri);
//					
//					Concept new_topic = new Concept(0,topic_name,topic_uri);
//					
//					// connect in the ResMap
//					new_domain.children.add(new_topic);
//					new_topic.setDomain(new_domain);
//					
//					this.concepts.add(new_topic);
//					
//					counter++;
//					
//					for(int j=0; j<courseModel.getLoListByTopic(topic_uri).size(); j++)
//					{
//						String quiz_uri = courseModel.getLoListByTopic(topic_uri).get(j);
//						String quiz_url = courseModel.getLoURL(quiz_uri);
//						String quiz_name = courseModel.getLoTitle(quiz_uri);
//						
//						Activity old_activity = this.activities.findByURI(quiz_uri);
//						double lo_weigh = courseModel.getLoWeight(quiz_uri);
//						
//						if(old_activity!=null)// this is the quiz, then index with topics al of its questions
//						{
////System.out.println("--- foreign=old activity " + old_activity.getId() + " " + old_activity.getTitle());							
////							ConceptActivity ca = new_topic.addActivityLink(old_activity, 
////									1.0, ConceptActivity.DIR_OUTCOM);
////							old_activity.addConceptLink(ca);
////							counter2 ++;
//							for (int k=0; k<old_activity.children.size(); k++)
//							{
//								ConceptActivity ca = new_topic.addActivityLink(old_activity.children.get(k), 
//										lo_weigh, ConceptActivity.DIR_OUTCOM);
//								old_activity.children.get(k).addConceptLink(ca);
//								counter2 ++;
//							}
//						}
//						else
//							System.out.println("Quiz name=" + quiz_name+" uri=" + quiz_uri + " was not found");
//												
//					}
//				}
//				
////				for (String t : courseModel.getTopicList())
////				{
////					System.out.println(courseModel.getTopicTitle(t));
////					for (String lo : courseModel.getLoListByTopic(t))
////					{
////						System.out.println(courseModel.getLoTitle(lo) + " - " + courseModel.getLoType(lo) + " " + lo);
////					}
////				}
////				System.out.println("--------------------------------------------------------");
////				for (String lo : courseModel.getLoList())
////					System.out.println(lo);
//			}
//			catch(Exception e) { e.printStackTrace(System.out); }
//System.out.println("... [CBUM] ResMap.loadData Foreign topics " + counter + " quizzes " + counter2 + " added."); /// DEBUG

System.out.println("###  ###");
System.out.println("mem.total: " + Runtime.getRuntime().totalMemory());
System.out.println("mem.max  : " + Runtime.getRuntime().maxMemory());
System.out.println("mem.free : " + Runtime.getRuntime().freeMemory());

			st_ua = System.nanoTime();

			String s_cutout_date = context.getInitParameter(ServerDaemon.CONTEXT_CUTOUT_DATE);
			String cutout_date = (s_cutout_date != null && s_cutout_date.length() >0 )?s_cutout_date:null;
			if(cutout_date!=null)
				System.out.println("... [CBUM] WARNING CUTOUT DATE ACTIVE (" + cutout_date + ")");

			// Load User Activities
			//qry = "SELECT * FROM ent_user_activity WHERE UserID=3 ORDER BY DateNTime;";// WHERE DateNTime='2006-11-17 11:45:18.240';";
			
			// CUMULATE TRACING
//			qry = "SELECT * FROM ent_user_activity WHERE AppID=2 AND USERID =16  AND ACTIVITYID = 891 " + ((cutout_date!=null)?"WHERE DateNTime>='"+cutout_date+"' ":"") +"ORDER BY DateNTime;"; 
			qry = "SELECT * FROM ent_user_activity " + ((cutout_date!=null)?"WHERE DateNTime>='"+cutout_date+"' ":"") +"ORDER BY DateNTime;"; 
			//System.out.println(qry);
			
			String qryK = "SELECT * FROM ent_user_knowledge_updates WHERE active=1 " + ((cutout_date!=null)?"AND DateNTime<='"+cutout_date+"' ":"") +"ORDER BY DateNTime;"; 
//				"WHERE DateNTime BETWEEN '2007-01-01 00:00:00.000' AND '2007-01-17 00:00:00.000' ORDER BY DateNTime;";// WHERE DateNTime='2006-11-17 11:45:18.240';";
//				"WHERE DateNTime BETWEEN '2007-01-17 00:00:00.000' AND '2007-01-24 00:00:00.000' ORDER BY DateNTime;";// WHERE DateNTime='2006-11-17 11:45:18.240';";
//				"WHERE DateNTime BETWEEN '2007-01-24 00:00:00.000' AND '2007-01-31 00:00:00.000' ORDER BY DateNTime;";// WHERE DateNTime='2006-11-17 11:45:18.240';";
//				"WHERE DateNTime BETWEEN '2007-01-31 00:00:00.000' AND '2007-02-07 00:00:00.000' ORDER BY DateNTime;";// WHERE DateNTime='2006-11-17 11:45:18.240';";
//				"WHERE DateNTime BETWEEN '2007-02-07 00:00:00.000' AND '2007-02-14 00:00:00.000' ORDER BY DateNTime;";// WHERE DateNTime='2006-11-17 11:45:18.240';";
//				"WHERE DateNTime BETWEEN '2007-02-14 00:00:00.000' AND '2007-02-21 00:00:00.000' ORDER BY DateNTime;";// WHERE DateNTime='2006-11-17 11:45:18.240';";
			stmt13 = conn.prepareStatement(qry);
			rs13 = stmt13.executeQuery();
			stmt14 = conn.prepareStatement(qryK);
			rs14 = stmt14.executeQuery();
			int counter1 = 0;
			int counter2 = 0;
			boolean rs13hasNext = rs13.next();
			boolean rs14hasNext = rs14.next();
			boolean rs13go = false;
			boolean rs14go = false;
			while(rs13hasNext || rs14hasNext)
			{
				int A_user_id = -99;
				int A_group_id = -99;
				int A_activity_id = -99;
				String A_result_s = null;
				String A_svc = null;
				User A_user = null;
				User A_group = null;
				Activity A_act = null;
				String A_date_n_time = null;

				int K_app_id = -99;
				int K_user_id = -99;
				int K_group_id = -99;
				int K_domain_id = -99;
				int K_concept_id = -99;
				double K_value = 0.0;
				User K_user = null;
				User K_group = null;
				App K_app = null;
				Concept K_dom = null;
				Concept K_con = null;
				String K_date_n_time = null;

				if(rs13hasNext && !rs13go)
				{
					A_user_id = rs13.getInt("UserID");
					A_group_id = rs13.getInt("GroupID");
					A_activity_id = rs13.getInt("ActivityID");
					A_result_s = rs13.getString("Result");
					A_user = users.findById(A_user_id);
					A_group = groups.findById(A_group_id);
					A_act = activities.findById(A_activity_id);
					A_date_n_time = rs13.getString("DateNTime");
					A_svc = rs13.getString("SVC");
					if(A_user == null)
					{
						System.out.println("[CBUM] ResMap.loadData " + 
							"ERROR! Loading User-Activities User " +
							"specified is NULL!");
					}
//					if(group == null)
//					{
//						System.out.println("[CBUM] ResMap.loadData " + 
//							"ERROR! Loading User-Activities Group " +
//							"specified is NULL!");
//					}
					if(A_act == null)
					{
						System.out.println("[CBUM] ResMap.loadData " + 
							"ERROR! Loading User-Activities Activity " +
							"specified is NULL!");
					}
				}
				
				if(rs14hasNext && !rs14go)
				{
					K_app_id = rs14.getInt("AppID");
					K_user_id = rs14.getInt("UserID");
					K_group_id = rs14.getInt("GroupID");
					K_domain_id = rs14.getInt("DomainID");
					K_concept_id = rs14.getInt("ConceptId");
					K_value = rs14.getDouble("Value");
					K_app = apps.findById(K_app_id);
					K_user = users.findById(K_user_id);
					K_group = groups.findById(K_group_id);
					K_dom = domains.findById(K_domain_id);
					K_con = concepts.findById(K_concept_id);
					K_date_n_time = rs14.getString("DateNTime");
				}
				
				int decision = 0; // 1 activity 2 knowledge
				if(rs13hasNext && rs14hasNext)
				{// compare
					if(K_date_n_time.compareTo(A_date_n_time)<0)
						decision = 2;
					else // activity
						decision = 1;
				}// end of -- compare
				else if(rs13hasNext) // Activity only
					decision = 1;
				else if(rs14hasNext) // Knowledge only
					decision = 2;
				
				//System.out.println("Decision: "+decision);
				//System.out.println("A_date_n_time: "+ A_date_n_time);
				
			
				switch (decision)
				{
					case 1: // activity
						A_user.updateHash(A_date_n_time, A_act.getApp().getId());
						try {
							A_group.updateHash(A_date_n_time, A_act.getApp().getId());
						} catch(Exception e) {
							e.printStackTrace();
						}
						

						long tic = 0;
						if("--matador--".equalsIgnoreCase(A_svc))
							tic = System.nanoTime();
						
						addNewUserActivity(A_user, /*group,*/ A_act, A_result_s, A_date_n_time);
						
						if("--matador--".equalsIgnoreCase(A_svc))
							System.out.println("--matador-- " + (System.nanoTime()-tic));
						
						counter1 ++;	
						rs13go = true;
					break;
					case 2: // knowledge
						K_user.updateHash(K_date_n_time, K_app.getId());
						K_group.updateHash(K_date_n_time, K_app.getId());

						updateConceptKnowledge(K_con, K_value,K_user, K_group, K_app, K_date_n_time, false, null);
						counter2 ++;
						rs14go = true;
					break;
				}

			
				if(rs13go) { rs13hasNext = rs13.next(); rs13go = false; }
				if(rs14go) { rs14hasNext = rs14.next(); rs14go = false; }
			}			
			rs13.close();
			stmt13.clearBatch();
			rs13 = null;
			stmt13 = null;
			rs14.close();
			stmt14.clearBatch();
			rs14 = null;
			stmt14 = null;
			
			fn_ua = System.nanoTime();

			System.out.println("... [CBUM] ResMap.loadData User Activities " + counter1 + " added."); /// DEBUG
			System.out.println("... [CBUM] ResMap.loadData User Knowledge Updates " + counter2 + " added."); /// DEBUG
			System.out.println("... [CBUM] user activity loaded in " + (float)(fn_ua-st_ua)/1000000 + " ms"); /// DEBUG
			long no_act_ua = 0;
			for(int i=0; i<this.activities.size(); i++)
				no_act_ua += this.activities.get(i).user_links.size();
			long no_con_ua = 0;
			for(int i=0; i<this.concepts.size(); i++)
				no_con_ua += this.concepts.get(i).user_knowledge_levels.size();
			
			System.out.println("... [CBUM] " + no_act_ua + "/" + no_con_ua + " user act/con estimators created"); /// DEBUG

		}// end of -- Try block of the loadData
		catch(Exception e) { e.printStackTrace(System.out); }
		finally
		{
			if (rs1 != null)
			{
				try { rs1.close(); } catch (SQLException e) { ; }
				rs1 = null;
			}
			if (rs2 != null)
			{
				try { rs2.close(); } catch (SQLException e) { ; }
				rs2 = null;
			}
			if (rs3 != null)
			{
				try { rs3.close(); } catch (SQLException e) { ; }
				rs3 = null;
			}
			if (rs4 != null)
			{
				try { rs4.close(); } catch (SQLException e) { ; }
				rs4 = null;
			}
			if (rs5 != null)
			{
				try { rs5.close(); } catch (SQLException e) { ; }
				rs5 = null;
			}
			if (rs6 != null)
			{
				try { rs6.close(); } catch (SQLException e) { ; }
				rs6 = null;
			}
			if (rs7 != null)
			{
				try { rs7.close(); } catch (SQLException e) { ; }
				rs7 = null;
			}
			if (rs8 != null)
			{
				try { rs8.close(); } catch (SQLException e) { ; }
				rs8 = null;
			}
			if (rs9 != null)
			{
				try { rs9.close(); } catch (SQLException e) { ; }
				rs9 = null;
			}
			if (rs10 != null)
			{
				try { rs10.close(); } catch (SQLException e) { ; }
				rs10 = null;
			}
			if (rs11 != null)
			{
				try { rs11.close(); } catch (SQLException e) { ; }
				rs11 = null;
			}
			if (rs12 != null)
			{
				try { rs12.close(); } catch (SQLException e) { ; }
				rs12 = null;
			}
			if (rs13 != null)
			{
				try { rs13.close(); } catch (SQLException e) { ; }
				rs13 = null;
			}
			if (stmt1 != null) 
			{
				try { stmt1.close(); } catch (SQLException e) { ; }
				stmt1 = null;
			}
			if (stmt2 != null) 
			{
				try { stmt2.close(); } catch (SQLException e) { ; }
				stmt2 = null;
			}
			if (stmt3 != null) 
			{
				try { stmt3.close(); } catch (SQLException e) { ; }
				stmt3 = null;
			}
			if (stmt4 != null) 
			{
				try { stmt4.close(); } catch (SQLException e) { ; }
				stmt4 = null;
			}
			if (stmt5 != null) 
			{
				try { stmt5.close(); } catch (SQLException e) { ; }
				stmt5 = null;
			}
			if (stmt6 != null) 
			{
				try { stmt6.close(); } catch (SQLException e) { ; }
				stmt6 = null;
			}
			if (stmt7 != null) 
			{
				try { stmt7.close(); } catch (SQLException e) { ; }
				stmt7 = null;
			}
			if (stmt8 != null) 
			{
				try { stmt8.close(); } catch (SQLException e) { ; }
				stmt8 = null;
			}
			if (stmt9 != null) 
			{
				try { stmt9.close(); } catch (SQLException e) { ; }
				stmt9 = null;
			}
			if (stmt10 != null) 
			{
				try { stmt10.close(); } catch (SQLException e) { ; }
				stmt10 = null;
			}
			if (stmt11 != null) 
			{
				try { stmt11.close(); } catch (SQLException e) { ; }
				stmt11 = null;
			}
			if (stmt12 != null) 
			{
				try { stmt12.close(); } catch (SQLException e) { ; }
				stmt12 = null;
			}
			if (stmt13 != null) 
			{
				try { stmt13.close(); } catch (SQLException e) { ; }
				stmt13 = null;
			}
			if (stmt14 != null) 
			{
				try { stmt14.close(); } catch (SQLException e) { ; }
				stmt14 = null;
			}
			if (conn != null)
			{
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
		
		System.out.println("###  ###");
		System.out.println("mem.total: " + Runtime.getRuntime().totalMemory());
		System.out.println("mem.max  : " + Runtime.getRuntime().maxMemory());
		System.out.println("mem.free : " + Runtime.getRuntime().freeMemory());


	}
	
	/** Function adds new User-Activity event to the repository
	 * @param _u - User that fired the event
//	 * @param _g - Group User belongs to
	 * @param _a - Activity that the event was triggered for
	 * @param _res_s - numeric score assigned to the event
	 * @return - true if the score <em>is</em> in fact numeric and the event 
	 * 	has been successfully added, false - otherwise */
	public boolean addNewUserActivity(User _u/*, User _g*/, Activity _a,
		String _res_s, String date_n_time)
	{
		// If User or Activity are unknown - do not do anything - not worth it
		if( (_u != null && _u.getId()==um_cache2.USER_UNKNOWN_ID) || 
			(_a != null && _a.getId()==um_cache2.ACTIVITY_UNKNOWN_ID) )
			return false;
			
		Pattern p = Pattern.compile("-?[0-9]+(.[0-9]+)?");
		Matcher m = p.matcher("");
		m.reset(_res_s);
//System.out.println("... [CBUM] ResMap.addNewActivity result {" + _res_s + "} is numeric-"); /// DEBUG
		if (m.matches())
		// if result is a number
		{
//System.out.println("YES"); /// DEBUG
			double result = Double.parseDouble(_res_s);
			UserActivity ua = _a.addUserActivity(_u, result, date_n_time);
			if(ua != null)
				_u.getActivityLinks().add(ua);
			return true;
		}
		else 
		{
//System.out.println("NO"); /// DEBUG
			return false;
		}
	}

	private void writeLockLog(App _app, User _user, User _group, String _context, 
			long _lock, long _ts, String _action, String _desc, String _agent) 
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		
//		System.out.println("~~~ [CBUM] RM.writeLockLog _app " + _app);
//		System.out.println("~~~ [CBUM] RM.writeLockLog _user " + _user);
//		System.out.println("~~~ [CBUM] RM.writeLockLog _group " + _group);
//		System.out.println("~~~ [CBUM] RM.writeLockLog _lock " + _lock);
		
		if(_app==null)
		{
			System.out.println("~~~ [CBUM] RM.writeLockLog ERROR, app null");
			return;
		}
		
		String qry = "INSERT INTO ent_locking_log (AppID, UserID, GroupID, AdditionalContext, LockValue, " +
				"Action, TimeMS, Description, Agent) VALUES(" + _app.getId() + "," + + _user.getId() + "," +
				"" + _group.getId() + ",'" + _context + "'," + _lock + ",'" + _action + "'," + _ts + "," +
				"'" + _desc + "','" + _agent + "');";
		try
		{
			conn = ServerDaemon.getConnection();
			stmt = conn.prepareStatement(qry);
			stmt.executeUpdate();
			
			stmt.close();
			stmt = null;
			conn.close();
			conn = null;
		}
		catch(SQLException sqle) { sqle.printStackTrace(System.out); }
		catch(Exception e) { e.printStackTrace(System.out); }
		finally
		{
			if (stmt != null) 
			{
				try { stmt.close(); } catch (SQLException e) { ; }
				stmt = null;
			}
			if (conn != null)
			{
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}

		}
	}
	
	/** Function adds new User-Activity event to the repository using String 
	 * 	ids
	 * @param _u - Login of the User that fired the event
	 * @param _a - Activity that the event was triggered for
	 * @param _res_s - numeric score assigned to the event */
	public void addNewUserActivityStr(String _app, String _u, String _g, String _a,
		String _res_s, String date_n_time, boolean _user_locking, HttpServletRequest _request)
	{
//		// print headers
//		Enumeration headerNames = _request.getHeaderNames();
//    	System.out.println("### ResourceMap.addNewUserActivityStr Headers");
//	    while(headerNames.hasMoreElements());
//	    {
//	    	String headerName = (String)headerNames.nextElement();
//    		System.out.println("\t" + headerName + _request.getHeader(headerName) );
//	    }
//	    
		App app = apps.findById(Integer.parseInt(_app));
		User u = users.findByTitle(_u);
		User g = groups.findByTitle(_g);
		Activity a = activities.findByTitle(_a);
		// Lock User
		long lock_ts = 0;
		if(_user_locking)
		{
			lock_ts = u.setLocked();
			writeLockLog(app, u, g, "" /*_context*/, 
					lock_ts, lock_ts, "set", "" /*_desc*/, _request.getRemoteAddr());
		}
		u.updateHash(date_n_time, app.getId());
		g.updateHash(date_n_time, app.getId());
		
		// add new activity
		addNewUserActivity(u, a, _res_s, date_n_time);
		// Unlock User
		if(_user_locking)
		{
			long lock_ts_check = u.getLock();
			long unlock_ts = u.unLock();
			String _desc = (lock_ts_check==lock_ts)
				?"removed after " + (unlock_ts-lock_ts) + " ms"
				:"attempt to remove after " + (unlock_ts-lock_ts) + " ms, lock missing";
			writeLockLog(app, u, g, "" /*_context*/, 
					lock_ts, unlock_ts, "remove", _desc, _request.getRemoteAddr());
		}
	}

	/** Function adds new User-Activity event to the repository using int 
	 * 	ids
	 * @param _u - Login of the User that fired the event
	 * @param _a - Activity that the event was triggered for
	 * @param _res_s - numeric score assigned to the event */
	public void addNewUserActivityInt(int _app, int _u, int _g, int _a,
		String _res_s, String date_n_time, boolean _user_locking, HttpServletRequest _request)
	{
//		// print headers
//		Enumeration headerNames = _request.getHeaderNames();
//    	System.out.println("### ResourceMap.addNewUserActivityStr Headers");
//	    while(headerNames.hasMoreElements());
//	    {
//	    	String headerName = (String)headerNames.nextElement();
//    		System.out.println("\t" + headerName + _request.getHeader(headerName) );
//	    }
//	    
		App app = apps.findById(_app);
		User u = users.findById(_u);
		User g = groups.findById(_g);
		Activity a = activities.findById(_a);
		// Lock User
		long lock_ts = 0;
		if(_user_locking)
		{
			lock_ts = u.setLocked();
			writeLockLog(app, u, g, "" /*_context*/, 
					lock_ts, lock_ts, "set", "" /*_desc*/, _request.getRemoteAddr());
		}
		// Hash user update
//System.out.println("CBUM ResourceMap.addNewUserActivityInt: user_id=" + _u + " user=" + u );
		u.updateHash(date_n_time, _app);
		g.updateHash(date_n_time, _app);
		
		// add new activity
		addNewUserActivity(u, a, _res_s, date_n_time);
		// Unlock User
		if(_user_locking)
		{
			long lock_ts_check = u.getLock();
			long unlock_ts = u.unLock();
			String _desc = (lock_ts_check==lock_ts)
			?"removed after " + (unlock_ts-lock_ts) + " ms"
			:"attempt to remove after " + (unlock_ts-lock_ts) + " ms, lock missing";
			writeLockLog(app, u, g, "" /*_context*/, 
				lock_ts, unlock_ts, "remove", _desc, _request.getRemoteAddr());
		}
	}

	
	/** Updating concept knowledge in the mode of raw application knowledge update
	 * @param _con - updated concept
	 * @param _score - the new score
	 * @param _user - User that got the score
	 * @param _group - group user is part of
	 * @param _app - reporting application
	 * @param date_n_time - date 
	 * @param _user_locking - flag
	 * @param _request - http request
	 */
	public void updateConceptKnowledge(Concept _con, double _score, User _user, User _group, App _app, String date_n_time,
				boolean _user_locking, HttpServletRequest _request)
	{
		long lock_ts = 0;
		if(_user_locking)
		{
			lock_ts = _user.setLocked();
			writeLockLog(_app, _user, _group, "" /*_context*/, 
					lock_ts, lock_ts, "set", "" /*_desc*/, _request.getRemoteAddr());
		}

		_user.updateHash(date_n_time, _app.getId());
		_group.updateHash(date_n_time, _app.getId());

		if(_con==null)
			System.out.println("!!! [CBUM] ERROR ResourceMap.updateConceptKnowledge concept specified is null!");
		else
		{
			if(_con.getDomain()==null)
				System.out.println("!!! [CBUM] ERROR Faulty Domain for Concept date=" + date_n_time + " usr=" + _user.getId() + " concept=" + _con.getId());
			_con.addScore(_score, _user, _app.getId(), 1, date_n_time);
		}
		
		if(_user_locking)
		{
			long lock_ts_check = _user.getLock();
			long unlock_ts = _user.unLock();
			String _desc = (lock_ts_check==lock_ts)
			?"removed after " + (unlock_ts-lock_ts) + " ms"
			:"attempt to remove after " + (unlock_ts-lock_ts) + " ms, lock missing";
			writeLockLog(_app, _user, _group, "" /*_context*/, 
				lock_ts, unlock_ts, "remove", _desc, _request.getRemoteAddr());
		}
	}
	
	
	/** Function maps the {application, new_score} to the bloom level index
	 * @param _app_id - the Id of the Application
	 * @param _score - new score for User action
	 * @return - index of the bloom level in the levels array */
	public static int mapToBloomLevelIndex(int _app_id, double _score)
	{
//System.out.println("ResourceMap.mapToBloomLevelIndex _app_id=" + _app_id + " _score=" + _score); ///DEBUG
		if(_app_id == 30) // Anna Riccioni's synthesis exercises
			return iProgressEstimator.BLOOM_IDX_SYNTHESIS;
		else if ( (_app_id == 2 || (((_app_id == 11) || (_app_id == 21)) && (_score >=0) && (_score <=1))) || // QuizPack, c/jWADEIn test
			 ((_app_id == 17) && (_score >= 0) && (_score <= 1)) || // JEliot
			 ((_app_id == 23) && (_score >= 0) && (_score <= 1)) || // Xin's SQL-KnoT
			 ((_app_id == 25) && (_score >= 0) && (_score <= 1)) || // QuizJET
			 ((_app_id == 41) && (_score >= 0) && (_score <= 1)) || // QuizPet
			 ((_app_id == 51) && (_score >= 0) && (_score <= 1)) || // QuizJET table tracing
			 ((_app_id == 47) && (_score >= 0) && (_score <= 1)) || // PCEX Challenges
			 ((_app_id == 44) && (_score >= 0) && (_score <= 1)) ||   // PCRS (Coding problems)
			 ((_app_id == 19)) // SQL TUTOR from New Zealand
			) {
			return iProgressEstimator.BLOOM_IDX_APPLICATION;
		}else if ( 
			(((_app_id == 3) || (_app_id == 11) || (_app_id == 21)) && (_score == -1)) || // NAVEX, c/jWADEIN EXPLORE, VARSCOPE
			((_app_id == 5) && (_score >0)) || // KNOWLEDGE SEA II 
			((_app_id == 15) && (_score == -1)) || // VAR SCOPE
			((_app_id == 16) && (_score == -1)) || // Boolean tool
			//((_app_id == 19) ) || // SQL TUTOR from New Zealand
			((_app_id == 26) && (_score == -1)) || // Jaekyung's IMPROVE
			((_app_id == 29)) // Anna Riccioni's quizzes
			) {
			return iProgressEstimator.BLOOM_IDX_COMPREHENSION;
		}else if( ((_app_id == 5) && (_score == 0)) || // Knowledge Sea II exploration
				((_app_id == 22) && (_score == -1)) || // Ensemble clicks
				(_app_id == 28) // Anna Riccioni's readings
				) {
			return iProgressEstimator.BLOOM_IDX_KNOWLEDGE;
		}else { 
			return -1;
		}
	}

	/** Function prints an activity progress report to a JSP output stream
	 * @param out - JSP output stream 
	 * @param _u - the user Login who's progress is reported
	 * @param _app - application Id
	 * @param _act - Activity name */
	
	public void activityReportSimple(JspWriter out, String _u, int _app, 
		String _act) throws IOException
	{
//System.out.println("... [CBUM] ResMap.activityReportSimple Starting..."); /// DEBUG
		String message = "";
		boolean inputsOk = true;
		User u = users.findByTitle(_u);
		Activity act = activities.findByTitle(_act);
		if(u == null)
		{
			message += "ERROR! User " + _u + " not found<br />\n";
			inputsOk = false;
		}
		if( (u != null) && (u.getApps().findById(_app) == null) )
		{
			message += "ERROR! User " + _u + " is not registered with " +
				"application #" + _app + "<br />\n";
			inputsOk = false;
		}
		if(act == null)
		{
			message += "ERROR! Activity not found<br />\n";
			inputsOk = false;
		}
		if(act != null && act.getApp().getId() != _app)
		{
			message += "ERROR! Activity is not registered with " +
				"application #" + _app + "<br />\n";
			inputsOk = false;
		}
		if(inputsOk)
		{
//System.out.println("... [CBUM] ResMap.activityReportSimple all ok, getting progress from activity:"+act.getTitle()); /// DEBUG
			double progress = act.getProgress(u);
			out.println("User: " + _u + "<br /> \n");
			out.println("App: " + _app + "<br /> \n");
			out.println("Activity: " + _act + "<br /> \n");
			out.println("Progress: " + progress + "<br /> \n");
			message = "Ok!";
		}
		out.println(message);
	}/**/

	/** Function prints an concept progress report to a JSP output stream
	 * @param out - JSP output stream 
	 * @param _u - the user Login who's progress is reported
	 * @param _app - application Id
	 * @param _conc - Concept name 
	 * @param _level - Bloom Level index */
	/*
	public void conceptReportSimple(JspWriter out, String _u, int _app, 
		String _conc, int _level) throws IOException
	{
//System.out.println("... [CBUM] ResMap.activityReportSimple Starting..."); /// DEBUG
		String message = "";
		boolean inputsOk = true;
		User u = users.findByTitle(_u);
		Concept conc = concepts.findByTitle(_conc);
		if(u == null)
		{
			message += "ERROR! User " + _u + " not found<br />\n";
			inputsOk = false;
		}
		if( (u != null) && (u.getApps().findById(_app) == null) )
		{
			message += "ERROR! User " + _u + " is not registered with " +
				"application #" + _app + "<br />\n";
			inputsOk = false;
		}
		if(conc == null)
		{
			message += "ERROR! Concept not found<br />\n";
			inputsOk = false;
		}
		if(inputsOk)
		{
//System.out.println("... [CBUM] ResMap.activityReportSimple all ok, getting progress from activity:"+act.getTitle()); /// DEBUG
			double progress = conc.getProgress(u, _level);
			out.println("User: " + _u + "<br /> \n");
			out.println("App: " + _app + "<br /> \n");
			out.println("Concept: " + _conc + "<br /> \n");
			out.println("Out weight sum: " + conc.getOutcomeWeightSum() + "<br /> \n");
			out.println("Progress: " + progress + "<br /> \n");
			message = "Ok!";
		}
		out.println(message);
	}/**/

	/** Function prints a concept progress report to a Servlet output stream
	 * @param out - Servlet output stream 
	 * @param _u - the user Login who's progress is reported
	 * @param _app - application Id
	 * @param _level - Bloom Level(s) */
	public String conceptReport(int _app, String _u, String _g, 
		String _level, String _domain, String _list, boolean _user_locking, HttpServletRequest _request) throws IOException
	{
		String result = "";
		result += "<?xml version='1.0' encoding='UTF-8' ?>\n";
		result += "<report>\n";
		// find user
		User user = users.findByTitle(_u);
		User group = groups.findByTitle(_g);

		//System.out.println("RMap.conceptReport group:" + group + " _g:" + _g);		
		// Observe lock
		if(_user_locking)
		{
			App app = apps.findById(_app);
			long lock_ts = user.getLock();
			GregorianCalendar date = new GregorianCalendar();
			long ts = date.getTimeInMillis();
			String _desc = (lock_ts!=0)?"user is locked " + (ts-lock_ts) + " ms ago":"user is not locked";
			writeLockLog(app, user, group, "" /*_context*/, 
					lock_ts, ts, "observe", _desc, _request.getRemoteAddr());
		}
		
		
		ArrayList<String> concept_tokens= new ArrayList<String>();
		
		if(_list != null)
		{
			StringTokenizer st = new StringTokenizer(_list, ",");
			while(st.hasMoreTokens())
			{
				concept_tokens.add(URLDecoder.decode(st.nextToken(),"UTF-8"));
			} 
		}
		
		// check if user or group s/he's in is registered for the application
//		boolean registered = false;
//		registered = (user != null) && (registered || (user.getApps().findById(_app) != null));
//		registered = (group != null) && (registered || (group.getApps().findById(_app) != null));
//
//		if(group == null)
//		{
//			for(int i=0; i<user.getOrdinates().size(); i++)
//			{
//				registered = registered || (user.getOrdinates().get(i).getApps().findById(_app) != null);
//				if(registered) break;
//			}
//		}
		
//		if(registered)
//		{
			result += "\t<user>" + _u + "</user>\n";
			result += "\t<user-app-hash>" + user.getHash(_app) + "</user-app-hash>\n";
			result += "\t<user-hash>" + user.getHash(0) + "</user-hash>\n";
			result += "\t<concepts>\n";
			// Decode levels
			int level_mask = 0;
			if(_level == null)
				level_mask = 127;
			else 
			{
				_level = _level.toLowerCase();
//System.out.println("[CBUM] ResMap.conceptReport _level=" + _level); /// DEBUG
				for(int i=0; i<iProgressEstimator.BLOOM_COUNT; i++)
					if(_level.indexOf(iProgressEstimator.
						BLOOM_NAMES[i].toLowerCase()) != -1)
						level_mask |= (int)Math.pow(2,i);		
//System.out.println("[CBUM] ResMap.conceptReport level_mask=" + level_mask); /// DEBUG
			}
			Concept domain = null;
			Item2Vector<Concept> domain_array = null;
			if(_domain != null)
			{
				domain = domains.findByTitle(_domain);
			}
			domain_array = (_domain != null)?domain.getChildren():
				concepts;
			
			if(concept_tokens.size() == 0)
			{
				// whole domain
				for(int i=0; i<domain_array.size() ;i++)
				{
					Concept concept = domain_array.get(i);
					result +=  concept.progressReport(user, "\t\t", level_mask);
				}
			}
			else
			{
				for(int i=0; i<concept_tokens.size(); i++)
				{
					Concept concept = domain_array.findByTitle(concept_tokens.get(i));
					if(concept != null)
						result +=  concept.progressReport(user, "\t\t", level_mask);
				}
			}
			
			
			result += "\t</concepts>\n";
//		}
		result += "</report>\n";
		return result;
	}
	
	/** Function prints a activity progress report to a Servlet output stream
	 * @param out - Servlet output stream 
	 * @param _u - the user Login who's progress is reported
	 * @param _app - application Id */
	public String activityReport(String _u, String _g, int _app, String _list, boolean _user_locking, HttpServletRequest _request)
		throws IOException
	{
		String result = "";
		result += "<?xml version='1.0' encoding='UTF-8' ?>\n";
		result +="<report>\n" ;
		User user = users.findByTitle(_u);
		User group = groups.findByTitle(_g);
		
		if(user==null || group == null)
		{
			System.out.println("!!! [CBUM] ResourceMap.activityReport SEVERE user (" + (user==null) + ") or group (" + (group==null) + ") not found u/g=" + _u + "/" + _g + 
					"[app=" + _app + ", acts=" + _list + "]");
			return result;
		}
		
		// Observe lock
		if(_user_locking)
		{
			App app = apps.findById(_app);
			long lock_ts = user.getLock();
			GregorianCalendar date = new GregorianCalendar();
			long ts = date.getTimeInMillis();
			String _desc = (lock_ts!=0)?"user is locked " + (ts-lock_ts) + " ms ago":"user is not locked";
			writeLockLog(app, user, group, "" /*_context*/, 
					lock_ts, ts, "observe", _desc, _request.getRemoteAddr());
		}
		
		
		ArrayList<String> act_tokens= new ArrayList<String>();
//System.out.println("tokens: " + _list);		
		if(_list != null)
		{
			StringTokenizer st = new StringTokenizer(_list, ",");
			while(st.hasMoreTokens())
			{
				act_tokens.add(URLDecoder.decode(st.nextToken(),"UTF-8"));
			} 
		}

		if(user!=null && group!=null && (user.getApps().findById(_app) != null || group.getApps().findById(_app) != null))
		{
//System.out.println("act_tokens.size(): " + act_tokens.size());		
			result += "\t<user>" + _u + "</user>\n";
			result += "\t<user-app-hash>" + user.getHash(_app) + "</user-app-hash>\n";
			result += "\t<user-hash>" + user.getHash(0) + "</user-hash>\n";
			result += "\t<group member_count='" + group.ordinates.size() + "'>" + _g + "</group>\n";
			result += "\t<activities>\n";
			if(act_tokens.size() > 0)
			{// report selected
				for(int i=0; i<act_tokens.size() ;i++)
				{
//					boolean by_uri = false;
					Activity act = activities.findByTitle(act_tokens.get(i));
//System.out.println("act: " + act);					
					//byURI
					if(act == null)
					{
						act = activities.findByURI(act_tokens.get(i));
//						by_uri = true;
					}
					if(act != null && act.getApp().getId()==_app /*&& act.getParent() == null*/) // ALLOW NON PARENTS TO BE FOUND
					{
						result += act.progressReport(user, group, "\t\t");
//						if(by_uri)
//							System.out.println("~~~ [CBUM] attempt to find by URI act=" + act + "("+act_tokens.get(i)+") SUCCESS");
					}
//					else
//					{
//						if(by_uri)
//							System.out.println("~~~ [CBUM] attempt to find by URI act=" + act + "("+act_tokens.get(i)+") FAIL");
//						
//					}
				}
			}// end -- report selected
			else
			{// report all
				for(int i=0; i<activities.size() ;i++)
				{
					Activity act = activities.get(i);
					if(act.getApp().getId()==_app && act.getParent() == null)
					{
						result += act.progressReport(user, group, "\t\t");
					}
					
				}
			}// end -- report all
			result +=  "\t</activities>\n";
		}		
		result += "</report>\n";
		return result;
	}

	/** Function prints a activity progress report to a Servlet output stream
	 * @param out - Servlet output stream 
	 * @param _u - the user Login who's progress is reported
	 * @param _app - application Id */
	public ActivityReport activityReportStruct(String _u, String _g, int _app, String _list, boolean _user_locking, HttpServletRequest _request)
		throws IOException
	{
		ActivityReport result = null;
//		String result = "";
//		result += "<?xml version='1.0' encoding='UTF-8' ?>\n";
//		result +="<report>\n" ;
		User user = users.findByTitle(_u);
		User group = groups.findByTitle(_g);
		
		if(user==null || group == null)
		{
			System.out.println("!!! [CBUM] ResourceMap.activityReport SEVERE user (" + (user==null) + ") or group (" + (group==null) + ") not found u/g=" + _u + "/" + _g + 
					"[app=" + _app + ", acts=" + _list + "]");
			return new ActivityReport(null, null);
		}
		
		// Observe lock
		if(_user_locking)
		{
			App app = apps.findById(_app);
			long lock_ts = user.getLock();
			GregorianCalendar date = new GregorianCalendar();
			long ts = date.getTimeInMillis();
			String _desc = (lock_ts!=0)?"user is locked " + (ts-lock_ts) + " ms ago":"user is not locked";
			writeLockLog(app, user, group, "" /*_context*/, 
					lock_ts, ts, "observe", _desc, _request.getRemoteAddr());
		}
		
		
		ArrayList<String> act_tokens= new ArrayList<String>();
		
		if(_list != null)
		{
			StringTokenizer st = new StringTokenizer(_list, ",");
			while(st.hasMoreTokens())
			{
				act_tokens.add(URLDecoder.decode(st.nextToken(),"UTF-8"));
			} 
		}

		if(user!=null && group!=null && (user.getApps().findById(_app) != null || group.getApps().findById(_app) != null))
		{
			result = new ActivityReport(user, group);
//			result += "\t<user>" + _u + "</user>\n";
//			result += "\t<user-app-hash>" + user.getHash(_app) + "</user-app-hash>\n";
//			result += "\t<user-hash>" + user.getHash(0) + "</user-hash>\n";
//			result += "\t<group member_count='" + group.ordinates.size() + "'>" + _g + "</group>\n";
//			result += "\t<activities>\n";
			if(act_tokens.size() > 0)
			{// report selected
				for(int i=0; i<act_tokens.size() ;i++)
				{
//					boolean by_uri = false;
					Activity act = activities.findByTitle(act_tokens.get(i));
					//byURI
					if(act == null)
					{
						act = activities.findByURI(act_tokens.get(i));
//						by_uri = true;
					}
					if(act != null && act.getApp().getId()==_app && act.getParent() == null)
					{
						result.ar_items.add( act.progressReportStruct(user, group) );
//						result += act.progressReport(user, group, "\t\t");
//						if(by_uri)
//							System.out.println("~~~ [CBUM] attempt to find by URI act=" + act + "("+act_tokens.get(i)+") SUCCESS");
					}
//					else
//					{
//						if(by_uri)
//							System.out.println("~~~ [CBUM] attempt to find by URI act=" + act + "("+act_tokens.get(i)+") FAIL");
//						
//					}
				}
			}// end -- report selected
			else
			{// report all
				for(int i=0; i<activities.size() ;i++)
				{
					Activity act = activities.get(i);
					if(act.getApp().getId()==_app && act.getParent() == null)
					{
						result.ar_items.add( act.progressReportStruct(user, group) );
//						result += act.progressReport(user, group, "\t\t");
					}
					
				}
			}// end -- report all
//			result +=  "\t</activities>\n";
		}		
//		result += "</report>\n";
		return result;
	}
	
	
	/** Function saves an activity progress report to a ArrayList
	 * @param _in - ArrayList with requested activities 
	 * @param _app - application id
	 * @param _u - the user Login who's progress is reported
	 * @return ArrayList populated with activity progress */
	public ArrayList activityReport(ArrayList _in, String _u, String _g, int _app, boolean _user_locking, HttpServletRequest _request)
	{
		User user = users.findByTitle(_u);
		User group = groups.findByTitle(_g);
		
		// Observe lock
		if(_user_locking)
		{
			App app = apps.findById(_app);
			long lock_ts = user.getLock();
			GregorianCalendar date = new GregorianCalendar();
			long ts = date.getTimeInMillis();
			String _desc = (lock_ts!=0)?"user is locked " + (ts-lock_ts) + " ms ago":"user is not locked";
			writeLockLog(app, user, group, "" /*_context*/, 
					lock_ts, ts, "observe", _desc, _request.getRemoteAddr());
		}
		
//System.out.println("request u=" + _u + " g=" +_g+" app="+_app);
		if( (_in==null) || (_in.size()==0) )
		{// request for all activities
			_in = new ArrayList<ProgressEstimatorReport>();
			for(int i=0; i<activities.size() ;i++)
			{
				Activity act = activities.get(i);
				if(act.getApp().getId()==_app && act.getParent()==null)
				{
					iProgressEstimatorReportItem _pea = ProgressEstimatorReportItem.PERIFactory(act.getTitle(), 1/*fudge single item*/);
					act.writeActivityInfo(user, group, _pea);

//					ProgressEstimatorReport pea = new 
//						ProgressEstimatorReport(
//							act.getTitle(),
//							act.getCount(user),
//							act.getProgress(user),
//							(group!=null)?act.getGroupCount(group, user):0,
//								(group!=null)?act.getGroupProgress(group, user):0
//							);
					
					for(int j=0; j<act.getChildren().size(); j++)
					{
						iProgressEstimatorReportItem _sub_pea = ProgressEstimatorReportItem.PERIFactory(act.getChildren().get(j).getTitle(), 1/*fudge single item*/);
						act.getChildren().get(j).writeActivityInfo(user, group, _sub_pea);

//						ProgressEstimatorReport spea = new 
//							ProgressEstimatorReport(
//								act.getChildren().get(j).getTitle(), 
//								act.getChildren().get(j).getCount(user),
//								act.getChildren().get(j).getProgress(user),
//								(group!=null)?act.getChildren().get(j).getGroupCount(group, user):0,
//								(group!=null)?act.getChildren().get(j).getGroupProgress(group, user):0
//							);
						
						_pea.getSubs().add(_sub_pea);
						_in.add(_sub_pea);
					}
					
//System.out.println("[cbum~~] " + spea);
				}
				
			}
		}// end of -- request for all activities
		else
		{// request for selected activities
//System.out.println("[cbum] ResourceMap.activityReport Rin sz=" + _in.size());			
			for(int i=0; i<_in.size(); i++)
			{
//System.out.println("\t iteration " + i);				
				ProgressEstimatorReport pea = (ProgressEstimatorReport)_in.get(i);
//System.out.println("\t iteration " + i + " after cast");				
				String activity_name = pea.getId();
				Activity act = activities.findByTitle(activity_name);
				boolean by_uri = false;
				//byURI
				if(act == null)
				{
					act = activities.findByURI(activity_name);
					by_uri = true;
				}
				if(act != null)
				{
//					if(by_uri)
//						System.out.println("~~~ [CBUM] attempt to find by URI act=" + act + "("+activity_name+") SUCCESS");					
	//System.out.println("\t iteration " + i + " after act find, (act==null):"+(act==null));				
	//	if(act == null) { System.out.println("[cbum] ResourceMap.activityReport act:" + activity_name + " is null"); continue; }
	//	else System.out.println("\t iteration " + i + " children.sz=" + act.getChildren().size());
					act.writeActivityInfo(user, group, pea);
//					pea.progress = act.getProgress(user);
//					pea.count = act.getCount(user);
//					double prog_g = (group!=null)?act.getGroupProgress(group, user):0;
//					pea.progress_g = prog_g;
//					pea.count_g = (group!=null)?act.getGroupCount(group, user):0;
	//System.out.println("[~~cbum] " + pea ); 				
					
					for(int j=0; j<act.getChildren().size(); j++)
					{
		//				ProgressEstimatorReport spea = ProgressEstimatorReport("pointer1",0,0.0);
						iProgressEstimatorReportItem _sub_pea = ProgressEstimatorReportItem.PERIFactory(act.getChildren().get(j).getTitle(), 1/*single estimation*/);
						act.getChildren().get(j).writeActivityInfo(user, group, _sub_pea);
						
//						ProgressEstimatorReport spea = new ProgressEstimatorReport(
//							act.getChildren().get(j).getTitle(), 
//							act.getChildren().get(j).getCount(user),
//							act.getChildren().get(j).getProgress(user),
//							(group!=null)?act.getChildren().get(j).getGroupCount(group, user):0,
//							(group!=null)?act.getChildren().get(j).getGroupProgress(group, user):0);
						pea.getSubs().add(_sub_pea);
	
					}
				}
//				else
//				{
//					if(by_uri)
//						System.out.println("~~~ [CBUM] attempt to find by URI act=" + act + "("+activity_name+") FAILURE");					
//				}

			}
		}// end of -- request for selected activities
//System.out.println("[cbum] ResourceMap.activityReport OUT");		
		return _in;
	}

	/** Function saves an concept progress report to a ArrayList
	 * @param _in - ArrayList with requested concepts 
	 * @param _u - the user Login who's progress is reported
	 * @param _app - application id
	 * @param _level - cognitive level of the concept knowledge
	 * @param _domain - domain of concepts (unused)
	 * @return ArrayList populated with concept progress */
	public ArrayList conceptReport(ArrayList _in, int _app, String _u, String _g,
		int _level, String _domain, boolean _user_locking, HttpServletRequest _request)
	{
        System.out.println("[cbum] ResourceMap.conceptReport starting...");		
		User user = users.findByTitle(_u);
		User group = groups.findByTitle(_g);
		int good_count = 0;
		
		// Observe lock
		if(_user_locking)
		{
			App app = apps.findById(_app);
			long lock_ts = user.getLock();
			GregorianCalendar date = new GregorianCalendar();
			long ts = date.getTimeInMillis();
			String _desc = (lock_ts!=0)?"user is locked " + (ts-lock_ts) + " ms ago":"user is not locked";
			writeLockLog(app, user, group, "" /*_context*/, 
					lock_ts, ts, "observe", _desc, _request.getRemoteAddr());
		}
		
		if(_in != null) // if we are sent a precise query
		{
			for(int i=0; i<_in.size(); i++)
			{
				iProgressEstimatorReportItem pea = (iProgressEstimatorReportItem)_in.get(i);
				String concept_name = pea.getId();
				Concept con = concepts.findByTitle(concept_name);
				if(con == null)
				{
					continue;
				}
				
				con.writeKnowledgeInfo(user, group, _level, pea);
				// pea.progress = con.getProgress(user, _level);
				// pea.count = con.getCount(user, _level);
				// pea.count_g = con.getGroupCount(group, user, _level);
				// pea.progress_g = con.getGroupProgress(group, user, _level);
				good_count ++;
			}
		}
		else // otherwise the whole domain is in
		{
			_in = new ArrayList<Object>();
			Concept domain = this.domains.findByTitle(_domain);
			for(int i=0; i<domain.children.size(); i++)
			{
				Concept con = concepts.findByTitle(domain.children.get(i).getTitle());
				String concept_name = con.getTitle();
				iProgressEstimatorReportItem pea = ProgressEstimatorReportItem.PERIFactory(concept_name, _level);
				
				con.writeKnowledgeInfo(user,  group, _level, pea);
				//pea.progress = con.getProgress(user, _level);
				//pea.count = con.getCount(user, _level);
				//pea.count_g = con.getGroupCount(group, user, _level);
				//pea.progress_g = con.getGroupProgress(group, user, _level);
				
				_in.add(pea);
			}
		}
		return _in;
	}

	/**
	 * @param _id
	 * @param _login
	 * @param _group
	 * @return
	 * @since 1.5
	 */
	public boolean addNewUser(int _id, String _login, String _group)
	{
		User group = groups.findByTitle(_group);
		if(group == null)
			return false;
		User user = new User(_id, _login, "default uri", max_app_id);
		users.add(user);
		user.getOrdinates().add(group);
		group.getOrdinates().add(user);
		return true;
	}

	public String conceptMappingXML(HttpServletRequest _request, String _domain, String _concepts, int _app_id) 
			throws UnsupportedEncodingException
	{
		String result = ""; 
		// 0. Header
		result += "<?xml version='1.0' encoding='UTF-8'?>\n" +
			"<report>\n";
		Concept domain = this.domains.findByTitle(_domain);
		
		if(domain == null || _concepts.length() == 0)
		{
			result += "<error>Domain not found or concept(s) empty</error>\n";
			System.out.println("!!! [CBUM] ResourceMap.conceptMappingXML Domain not found or concept(s) empty");
		}
		else
		{
			result += "	<mappings>\n";
			StringTokenizer st = new StringTokenizer(_concepts, ",");
			while(st.hasMoreTokens())
			{
				String s_concept = st.nextToken();
				Concept concept = domain.getChildren().findByTitle(s_concept);
				if(concept!=null)
				{
					result += concept.mappingXML(_app_id);
				}
				else
				{
					concept = domain.getChildren().findByURI(s_concept);
					if(concept!=null)
						result += concept.mappingXML(_app_id);
					else
						System.out.println("!!! [CBUM] ResourceMap.conceptMappingXML concept '" + s_concept + "' not found");
				}
			}
			result += "	</mappings>\n";
		}
		
		// 100. Lead out
		result += "</report>\n";
		return result;
	}

	public String conceptMappingRDF(HttpServletRequest _request, String _domain, String _concepts, int _app_id)
			throws UnsupportedEncodingException
	{
		String result = "";
		// 0. Header
		result += "<?xml version='1.0' encoding='UTF-8'?>\n" +
				"	<rdf:RDF\n" +
				"		xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n" +
				"		xmlns:dc='http://purl.org/dc/elements/1.1/'>\n";	
			
		Concept domain = this.domains.findByTitle(_domain);
		
		if(domain == null || _concepts.length() == 0)
		{
//			_out.println("<error>Domain not found or concept(s) empty</error>");
			System.out.println("!!! [CBUM] ResourceMap.conceptMappingRDF Domain not found or concept(s) empty");
		}
		else
		{
			StringTokenizer st = new StringTokenizer(_concepts, ",");
			while(st.hasMoreTokens())
			{
				String s_concept = st.nextToken();
				Concept concept = domain.getChildren().findByTitle(s_concept);
				if(concept!=null)
				{
					result += concept.mappingRDF(_app_id);
				}
				else
				{
					concept = domain.getChildren().findByURI(s_concept);
					if(concept!=null)
						result += concept.mappingRDF(_app_id);
					else
						System.out.println("!!! [CBUM] ResourceMap.conceptMappingRDF concept '" + s_concept + "' not found");
				}
			}
		}
		
		// 100. Lead out
		result += "</rdf:RDF>\n";
		return result;
	}

	public Vector<Activity2ConceptMapping4Report> activityMapping(HttpServletRequest _request, int _app_id, String _activities, String _domain)
		throws UnsupportedEncodingException
	{
		Vector<Activity2ConceptMapping4Report> result = new Vector<Activity2ConceptMapping4Report>();
		
		Concept domain = this.domains.findByTitle(_domain);
		
		if(domain == null || _activities.length() == 0)
		{
//_out.println("<error>Activity is empty or target domain not specified</error>");
			if(domain == null)
				System.out.println("!!! [CBUM] ResourceMap.activityMapping Domain not found");
			else
				System.out.println("!!! [CBUM] ResourceMap.activityMapping activities(s) empty");
		}
		else
		{
			StringTokenizer st = new StringTokenizer(_activities, ",");
			while(st.hasMoreTokens())
			{
				String s_activity = st.nextToken();
				Activity activity = this.activities.findByTitle(s_activity);
				if(activity==null)//try to search by URI
					activity = this.activities.findByURI(s_activity);
				
//System.out.println("~~~ [CBUM] ResourceMap.activityMappingRDF activity=" + s_activity + " from app=" + _app_id + "~" + activity.getApp().getId() + " ccoun=" + activity.getConceptLinks().size());
				if(activity!=null && (_app_id==-1 || activity.getApp().getId() == _app_id))
				{
					result.add( activity.mappingToConcepts(domain.getId()) );
				}
				else if (activity==null)
				{
					System.out.println("!!! [CBUM] ResourceMap.activityMapping concept '" + s_activity + "' not found or wrong App Id");
				}
			}
		}
		
		return result;
	}

	public String intrudeActivity(String _user, String _act, String _dom)
	{
		String result = "";
		Activity act = activities.findByTitle(_act);
		act = (act==null)?activities.findByURI(_act):act;
		Concept dom = domains.findByTitle(_dom);
		User user = users.findByTitle(_user);
		if(act==null || dom== null || user == null)
			result = "";
		else
		{
			result += "Progress = " + act.getProgress(user) + "\n";
			result += "Count = " + act.getCount(user) + "\n";
			result += "Outcome Weight Sum = " + act.getOutcomeWeightSum(dom) + "\n";
			result += "Prerequisite Weight Sum = " + act.getPrereqWeightSum(dom) + "\n";
			result += "-----------------\n";
			String s_concepts_out = "";
			String s_concepts_pre = "";
			for(int i=0;i<act.concept_links.size(); i++)
			{
				ConceptActivity ca = act.concept_links.get(i);
				if(ca.concept.domain.getId() == dom.getId())
				{
//System.out.println("ca.concept=" + ca.concept);					
//System.out.println("ca.concept.getUserProgressEstimator(user)=" + ca.concept.getUserProgressEstimator(user));					
					double c_progress = ca.concept.getUserProgressEstimator(user).getKnowledgeLevels()[iProgressEstimator.BLOOM_IDX_APPLICATION].getProgress();
					double weight = ca.weight;
					weight /= Math.pow/**/(( (act.getProgress(user)*act.getCount(user) + 1)*
							( (ca.getDirection()== ConceptActivity.DIR_OUTCOM) 
									? act.getOutcomeWeightSum(ca.concept.getDomain())
									: act.getPrereqWeightSum(ca.concept.getDomain()))) , .25/**/) +0;
					double progress_upd = ((c_progress<.5)
												?Math.pow((1 - c_progress),2)/2
												:Math.pow((1 - c_progress),2)
											) * ( (Math.abs(1/*score*/) * weight) / 1);

					String rep = ca.concept.getTitle() + 
					" progress=" + c_progress +
					" count=" + ca.concept.getUserProgressEstimator(user).getKnowledgeLevels()[iProgressEstimator.BLOOM_IDX_APPLICATION].getCount() +
					" next increase=" + progress_upd + "(weight=" + weight + ")\n";
					
					if(ca.direction == ConceptActivity.DIR_OUTCOM)
						s_concepts_out += rep;
					else if(ca.direction == ConceptActivity.DIR_PREREQ)
						s_concepts_pre += rep;
					
				}
			}
			result += "Prerequisites --------\n" + s_concepts_pre;
			result += "Outcomes --------\n" + s_concepts_out;
		}
		return result;
	}

}					


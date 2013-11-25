package edu.pitt.sis.paws.cbum2;

import edu.pitt.sis.paws.core.BiItemVector;
import edu.pitt.sis.paws.core.Item2Vector;
import edu.pitt.sis.paws.core.utils.SQLManager;

public class ResourceMap
{
	protected Item2Vector<User> users;
	protected Item2Vector<User> groups;
	
	protected Item2Vector<Corpus> corpora; 
	protected Item2Vector<Corpus> apps;
	
	protected Item2Vector<UMComponent> knowledgeComponents;

	protected Item2Vector<UMComponent> learningObjects;
	
	protected BiItemVector<User, UMComponent> estimations;

	public ResourceMap()
	{
		users = new Item2Vector<User>();
		groups = new Item2Vector<User>();

		corpora = new Item2Vector<Corpus>();
		apps = new Item2Vector<Corpus>();
		knowledgeComponents = new Item2Vector<UMComponent>();
		learningObjects = new Item2Vector<UMComponent>();
		
		estimations = new BiItemVector<User, UMComponent>();
	}
	
	public ResourceMap(SQLManager sqlManager)
	{
		users = new Item2Vector<User>();
		groups = new Item2Vector<User>();

		corpora = new Item2Vector<Corpus>();
		apps = new Item2Vector<Corpus>();
		knowledgeComponents = new Item2Vector<UMComponent>();
		learningObjects = new Item2Vector<UMComponent>();
		
		estimations = new BiItemVector<User, UMComponent>();
		loadData(sqlManager);
	}
	
	
	// Setters & Getters
	
	public Item2Vector<Corpus> getCorpora() { return corpora; }

	public void setCorpora(Item2Vector<Corpus> _corpora) { corpora = _corpora; }

	
	public Item2Vector<Corpus> getApps() { return apps; }

	public void setApps(Item2Vector<Corpus> _apps) { apps = _apps; }

	
	public Item2Vector<UMComponent> getKnowledgeComponents() { return knowledgeComponents; }

	public Item2Vector<UMComponent> getLearningObjects() { return learningObjects; }

	public Item2Vector<User> getGroups() { return groups; }

	public Item2Vector<User> getUsers() { return users; }

	public BiItemVector<User, UMComponent> getEstimations() { return estimations; }
	
	// Other Methods
	
	private void loadData(SQLManager sqlManager)
	{
		;
	}
}

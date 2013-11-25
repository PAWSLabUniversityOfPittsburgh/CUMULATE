package edu.pitt.sis.paws.cbum.structures;

import java.util.Iterator;
import java.util.Vector;

import edu.pitt.sis.paws.cbum.Activity;
import edu.pitt.sis.paws.cbum.Concept;

public class Activity2ConceptMapping4Report
{
	public Activity activity;
	public String rdfID;
	public Vector<Concept> concepts;
	
	public Activity2ConceptMapping4Report(Activity _activity)
	{
		activity = _activity;
		concepts = new Vector<Concept>();
	}
	
	public static StringBuffer serializeRDF(Vector<Activity2ConceptMapping4Report> _mappings)
	{
		StringBuffer result = new StringBuffer();
		// 0. Header
		result.append("<?xml version='1.0' encoding='UTF-8'?>\n" +
			"	<rdf:RDF\n" +
			"		xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n" +
			"		xmlns:dc='http://purl.org/dc/elements/1.1/'>\n");	
		Iterator<Activity2ConceptMapping4Report> iter = _mappings.iterator();
		for(;iter.hasNext();)
		{
			Activity2ConceptMapping4Report item = iter.next();
			result.append( "		<rdf:Description rdf:about='" + item.activity.getURI().replaceAll("&", "&amp;") + "'>\n" );
			for(Iterator<Concept> iter2=item.concepts.iterator(); iter2.hasNext();)
				result.append( "			<dc:subject>" + iter2.next().getTitle() + "</dc:subject>\n" );
			result.append( "		</rdf:Description>\n" );
		}
		
		result.append("</rdf:RDF>\n");
		return result;
	}

	public static StringBuffer serializeXML(Vector<Activity2ConceptMapping4Report> _mappings)
	{
		StringBuffer result = new StringBuffer();
		// 0. Header
		result.append("<?xml version='1.0' encoding='UTF-8'?>\n" + "<report>\n" + "	<mappings>\n");	
		Iterator<Activity2ConceptMapping4Report> iter = _mappings.iterator();
		for(;iter.hasNext();)
		{
			Activity2ConceptMapping4Report item = iter.next();
			result.append( "		<mapping>\n" + 
					"			<activity>" + item.activity.getTitle() + "</activity>\n");
			for(Iterator<Concept> iter2=item.concepts.iterator(); iter2.hasNext();)
				result.append( "			<concept>" + iter2.next().getTitle() + "</concept>\n" );
			result.append( "		</mapping>\n" );
		}
		
		result.append("	</mappings>\n" + "</report>\n");
		return result;
	}
}

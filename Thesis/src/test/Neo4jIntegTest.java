package test;

import javax.print.attribute.standard.PresentationDirection;
import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.ErrorHandler;
import org.omg.CORBA.Current;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;


@SuppressWarnings("unused")
public class Neo4jIntegTest {
	
	private static final String DB_PATH = "C:\\Users\\mahmoud\\Desktop\\masters\\DHBW work\\neo4j-community-2.1.8";
	ArrayList<org.neo4j.graphdb.Node> nodes;
	GraphDatabaseService graphDb;
	ArrayList<org.neo4j.graphdb.Relationship> relations;
	Document XMLdoc;
	
	private static enum RelTypes implements RelationshipType
	{
		ACTIV, lEAD_TO, PRECEDS, BELONGS_TO, CTR
	}
	
	/**
	 * This is a method where one can enter a file and then the file is parsed into a document format which will be helpful for extracting
	 * the data in a later stage.
	 * @param selectedFile
	 * @return Document
	 * @author mahmoud
	 */
	public static Document ReadFileAndSave(final File selectedFile){
		 try {
			 	File fXmlFile = new File(selectedFile.getAbsolutePath());
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				return doc;
			    } catch (Exception e) {
				e.printStackTrace();
			    }
		 		return null;
			  }
	/**
	 * This method counts the number of objects in the document so that it can be used as a base for the loop to initialize the Nodes
	 * in a loop in order to save them in the database.
	 * @param file
	 * @return int number of the objects to be created
	 * @author mahmoud
	 */
	public static int NumObjToBeCreated(Document file){
		
		NodeList nList = file.getElementsByTagName("ObjDef"); 
		return nList.getLength();	
	}
	
	/**
	 * creates the database we will be working on, and initialize the nodes in the array list by creating them
	 * and adding their property.
	 * @param file
	 * @author mahmoud
	 */
	void createDatabase(Document file){
		
		this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		this.XMLdoc = file;
		try (Transaction transaction = graphDb.beginTx() )
		{
		    // Database operations go here
			for (int i=0; i < NumObjToBeCreated(XMLdoc) ; i++){
				org.neo4j.graphdb.Node n = graphDb.createNode();
				
				//to add the properties here
				
				nodes.add(n);
			}
			transaction.success();
		}
	}
	
//	void removeData(){
//		try (Transaction transaction = graphDb.beginTx() )
//		{
//		    firstNode.getSingleRelationship(RelTypes.KNOWS, Direction.OUTGOING).delete();
//		    System.out.println("Nodes are removed");
//		    firstNode.delete();
//		    secondNode.delete();
//		}
//	}
	
	
	void shutdown(){
		
		graphDb.shutdown();
		System.out.println("neo4j database is shuting down");
	}
 
  public static void main(String argv[]) {
	  final JFileChooser chooser = new JFileChooser();
      if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
    	 Document d =  ReadFileAndSave(chooser.getSelectedFile());
      }
  }
 
}
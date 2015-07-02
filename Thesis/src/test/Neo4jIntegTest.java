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
				
				NodeList nList = XMLdoc.getElementsByTagName("ObjDef");                            //all objects in a list.
				int ConnectCountertemp =0;
				for (int temp = 0; temp < nList.getLength(); temp++) {
				
					Node nNode = nList.item(temp);											//the object with all its information in the node at number which is equal counter.
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {       
						Element eElement = (Element) nNode;									// casting the object into type Element so we can use it as an element.
						
						String objectID = eElement.getAttribute("ObjDef.ID");
						n.setProperty("objectID", objectID);								//saving the objectID as a new property for the created node
						System.out.print("1 ");
						
						String objectType = eElement.getAttribute("SymbolNum") ;
						n.setProperty("objectType", objectType);
						System.out.print("2 ");
						if(eElement.getAttribute("SymbolNum").equals("ST_BPMN_SUBPROCESS")){
							String linkedModelID = eElement.getAttribute("LinkedModels.IdRefs"); 
							n.setProperty("linkedModelID", linkedModelID);
							System.out.print("3 ");
						}
						
						if(eElement.getAttribute("ToCxnDefs.IdRefs").contains("CxnDef")){								//this line of code i wrote so incase it doesn't have a connection the connection ID wouldn't show.
							String connectionRef= eElement.getAttribute("ToCxnDefs.IdRefs");
							String[] allCon = connectionRef.split(" ");													//since some of the connection are split using spaces i splited on these spaces to save the connections in the
												
								for(int allConCounter = 0; allConCounter < allCon.length; allConCounter++ ){
									String connectionNum = "connected With "+allConCounter;
									String connectionIDRef = allCon[allConCounter];
									n.setProperty(connectionNum, connectionIDRef);
									System.out.print("4 ");
								}
							
						}
						
						//this part of the code if for getting the names of the objects as in their types.
						NodeList NameList = eElement.getElementsByTagName("AttrDef");			//getting the AttrDef for the specific object its a LIST
						
						for(int i1 = 0 ; i1 <NameList.getLength(); i1++){
							Node NameNode = NameList.item(i1);									//getting the AttrDef at the specific position for the specific object
							Element NameElements = (Element) NameNode;
							Node parentNode = NameNode.getParentNode();							//gets the parent of the specific node to check that it is equal to the specific object so it wouldn't get another object
						
							if(NameElements.getAttribute("AttrDef.Type").equals("AT_NAME") && parentNode.equals(nNode)){		//gets the name of the object READ the NAME.txt  
								if(eElement.getAttribute("SymbolNum").equals("ST_BPMN_ANNOTATION_1")){
									NodeList AdditionalInfoTask = NameElements.getElementsByTagName("PlainText"); 		//getting the plain text elements list in the connection just incase there is a couple not only one
							
									for(int AddInfoCounter = 0; AddInfoCounter < AdditionalInfoTask.getLength();AddInfoCounter++){
										String AdditionalInfo ="Information text "+ AddInfoCounter;
										Node AddInfoNode = AdditionalInfoTask.item(AddInfoCounter);			//getting the specific plaintext element in a specific position
									
										if(AddInfoNode.getNodeType() == Node.ELEMENT_NODE){
											Element AddInfo = (Element) AddInfoNode;							//casting the object into an element so we can use it and get its value
											String theAddInfo= AddInfo.getAttribute("TextValue");
											n.setProperty(AdditionalInfo, theAddInfo);
											System.out.print("5 ");
												
											}
						}
							}
								else
							{
								Element e = (Element) NameElements.getElementsByTagName("PlainText").item(0);
								String infoText= e.getAttribute("TextValue");							
								n.setProperty("Information text", infoText);
								System.out.print("6 ");
							}
						}
			
//							if(NameElements.getAttribute("AttrDef.Type").equals("AT_ID")){
//								
//								Element e = (Element) NameElements.getElementsByTagName("PlainText").item(0);			//this is to be added beside tasks of subprocess to distinguish them from normal tasks
//								ObjectInfo[temp][3]= e.getAttribute("TextValue");
//							}
//							
//						}
						
						//this part is for getting the connections and their information
						 
						int ConnectCounter = 0;
						NodeList Connections = eElement.getElementsByTagName("CxnDef"); 		//getting the connections list for the specified element
						//System.out.println(Connections.getLength()); 
						while(ConnectCounter < Connections.getLength()){
							//System.out.println("\nThis is ConnectCountertemp number "+ ConnectCountertemp);
							Node connection = Connections.item(ConnectCounter);					//getting the a specific connection in the counters position
							for(int ConnectCounterj = 0; ConnectCounterj < 1;ConnectCounterj++){
							if(connection.getNodeType() == Node.ELEMENT_NODE){
								Element Conn = (Element) connection;							//changing the connection into the element so we can use it.
								if(Conn.getAttribute("CxnDef.ID").contains("CxnDef")){								//this line of code i wrote so incase it doesn't have a connection the connection ID wouldn't show.
									String connectionID =Conn.getAttribute("CxnDef.ID");
									n.setProperty("connectionID", connectionID);
									System.out.print("7");
									String connectionType=Conn.getAttribute("CxnDef.Type");
									n.setProperty("connectionType", connectionType);
									System.out.print("8");
									String connectingWithObject=Conn.getAttribute("ToObjDef.IdRef");
									n.setProperty("connectingWithObject", connectingWithObject);
									System.out.print("9");
									
									Element probability = (Element) Conn.getElementsByTagName("AttrDef").item(0);
									if( probability != null &&probability.getAttribute("AttrDef.Type").equals("AT_PROB")){
										String probabilityOfConnection = probability.getElementsByTagName("AttrValue").item(0).getTextContent(); //added the probability in the connection
										n.setProperty("probabilityOfConnection", probabilityOfConnection);
										System.out.print("10");
									}
								}
							}
						}
							ConnectCounter++;
							ConnectCountertemp++;
						}	
					}
				}
				
				
				//addinng properties end here
				
				nodes.add(n);
				System.out.println("node added");
			}
			transaction.success();
		}
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
    	 Neo4jIntegTest t = new Neo4jIntegTest();
    	 t.createDatabase(d);
    	 t.shutdown();
      }
  }
 
}
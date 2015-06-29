package test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class test2 {

	private static final String DB_PATH = "C:\\Users\\mahmoud\\Desktop\\masters\\DHBW work\\neo4j-community-2.1.8";
	GraphDatabaseService graphDb;
	Node firstNode;
	Node secondNode;
	Relationship relationship;
	
	private static enum RelTypes implements RelationshipType
	{
	    KNOWS
	}
	
	void createDatabase(){
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		
		
		
		try (Transaction transaction = graphDb.beginTx() )
		{
		    // Database operations go here
			
			firstNode = graphDb.createNode();
			firstNode.setProperty( "message", "Hello, " );
			secondNode = graphDb.createNode();
			secondNode.setProperty( "message", "World!" );
			relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
			relationship.setProperty( "message", "brave Neo4j " );
			
			System.out.print( firstNode.getProperty( "message" )+ " " );
			System.out.print( relationship.getProperty( "message" )+ " " );
			System.out.print( secondNode.getProperty( "message" ) +"\n ");
			
			transaction.success();
		}
	}
	
	void removeData(){
		try (Transaction transaction = graphDb.beginTx() )
		{
		    firstNode.getSingleRelationship(RelTypes.KNOWS, Direction.OUTGOING).delete();
		    System.out.println("Nodes are removed");
		    firstNode.delete();
		    secondNode.delete();
		}
	}
	
	void shutdown(){
		
		graphDb.shutdown();
		System.out.println("neo4j is shutdown");
	}
	
	public static void main(String[]args){
		test2 t = new test2();
		t.createDatabase();
		t.removeData();
		t.shutdown();

	}
	
}




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
 
@SuppressWarnings("unused")
public class ExperimentCodeV5 {
	
	public static void printBoard(String[][] x) {
		for(int i = 0; i < x.length; i++){
		    for(int j = 0; j < x[i].length; j++){
//		        if(x[i][j] != null){
		    	System.out.print(x[i][j]+ "  ");
	//	        if(j < x[i].length - 1) System.out.print(" ");
//		    }
		        }
		    System.out.println();
		}
	}
	
	public static void printArrayList(ArrayList<String> x){
		for(int i =0; i< x.size(); i++){
			System.out.print(x.get(i)+ " ");
		}
		System.out.println();
	}
	
	public static void printBoardLine(String[][] x, int verticalIndex) {
		for(int i = 0; i < x.length; i++){
			 System.out.print(x[i][verticalIndex]+ "  ");
			System.out.println();
		}
	}
	
	public static void printBoards(String[][] x,String[][] y,String[][] z,String[][] a ) {
		printBoard(x);
		System.out.println("----------------------------------");
		printBoard(y);
		System.out.println("----------------------------------");
		printBoard(z);
		System.out.println("----------------------------------");
		printBoard(a);
	}
	
	public static int[] SearchingTasks(String[][]x){		//used to get the index of every object that is a task

		int[] TaskArray = new int[x.length];
		int counter= 0;
		String checkTask = "";
		for(int i = 0; i < x.length; i++){					/*to get the task indexes*/
			checkTask= x[i][1];
			if(checkTask != null){
				if(checkTask.contains("TASK")){
					TaskArray[counter] = i;		//ST_BPMN_TASK  ST_BPMN_USER_TASK
					counter++;
				}
		    }	
		}
		return TaskArray;
	}
	
	
	
	public static String[][] RetrivingRefs(int[]x, String[][] connRefs){			//used for the normal process either normal or after the branching has been done
		int counter= 0;																//used to retrieve the connectionRefs for the tasks that are in the task Array.
		for(int i = 0; i < x.length; i++){
			if(x[i] != 0){
				counter++;
		    }	
		}
		int counter2 = 0;
		String[][] taskConRef = new String [counter][x.length];
		for (int f = 0; f < counter ; f++){
			int index = x[f];
			for(int g = 1; g < connRefs[index].length; g++){
				if(connRefs[index][g] != null){
				taskConRef[f][counter2] = connRefs[index][g];
				counter2++;
				}
			}
			counter2 =0;
		}
		
		return taskConRef;
	}
	
	public static String[][] RetrivingConnectionsObjects(int[]x, String[][] ConnArray, String[][] obj,String[][] ConnectionsRefrenced ){	//used for the normal process either normal or after the branching has been done
		int counter= 0;
		for(int i = 0; i < x.length; i++){																									/*to know the number of tasks avilable*/
			if(x[i] != 0){
				counter++;
		    }	
		}
		int counter2 = 1;
		String[][] taskConRef = new String [counter][ConnArray.length + ConnectionsRefrenced.length];										/*set up a 2D array with the length of number of tasks and the connectionArray
		  																																	lenght as well as the refrenced connectionLength because it can never be larger than both*/
		for (int f = 0; f < counter ; f++){
			int index = x[f];																//get the first task index
			String objId = obj[index][0];													///get the objectid from the obj 2D array for that index
			taskConRef[f][0]= objId;														// set the first place in the row for the objID
			counter2=1;																		//counter starts from 1 so we will be able to save in the next place not overwrite the ID
//			System.out.println(taskConRef[f][0]);
			for(int g = 0; g < ConnArray.length; g++){
				String objIDforConn = ConnArray[g][0];
				if(obj[index][3] != null && obj[index][3].equals("subprocess_task")){
					for(int i = 0; i < x.length; i++){
						if(obj[i][1]!=null && obj[i][1].equals("ST_BPMN_SUBPROCESS")){		//for the task with the subprocess this will replace the objID varriable to the ID of the Subprocess
																							//instead of the task so that we can retrive the swimlane name.
							objId = obj[i][0];
						}
					}
				}
				if(objIDforConn != null && objId != null && objIDforConn.equals(objId)){
					taskConRef[f][counter2] = ConnArray[g][3];								//as long as there is still objects in the connArray, and objID not equal null, and the current object in the
					counter2++;																//connection array is equal to the object ID then i get the third element from the object row(which is the refrenced object)
				}																			//and add it to the connections associated with the certain task
				
			}
			for(int k = 0; k < ConnectionsRefrenced[f].length; k++){						//connectionRefrenced is a table with the "CxnID" and it has as many records as the tasks, we search the row associated with the task
				if(ConnectionsRefrenced[f][k] != null){										//as long as the index is not null this "CxnID" is taken and compared to the "CxnIds" in the connectionsArray if a match is found then
					String ConnID = ConnectionsRefrenced[f][k];
					for(int h=0; h < ConnArray.length; h++){								//then the objectID of the object with this specific "CxnID" is added to the taskConRef which has all the objects ID of the objects connected to				
						if(ConnID.equals(ConnArray[h][1])){									// a certain object that is specified by the index.
							taskConRef[f][counter2] = ConnArray[h][0] ;
							counter2++;
						}
					}
				}
				
			}
			for(int z=0; z < ConnArray.length; z++){										//this loop is to save the probability of an element if it has a probability
				String temp = ConnArray[z][3];
				if(temp != null && objId != null && temp.equals(objId)){
					if(ConnArray[z][4] != null){
						taskConRef[f][counter2] = ConnArray[z][4];
						counter2++;
					}
				}
			}
//			counter2=1;
		}
		
		return taskConRef;
	}
	
	public static boolean isAnnotation(String x){
		if(x.equals("ST_BPMN_ANNOTATION_1")){
			return true;
		}else{
	return false;}
	}
	
	public static boolean isLane(String x){
		if(x.equals("ST_BPMN_LANE_1")){
			return true;
		}else{
	return false;}
	}
	
	public static int getNumberOfConnections(String[]objectsIDOfTheConnections){		
																						//used to get how much elements exist in an array
		int counter = 0;
			for(int i = 1; i < objectsIDOfTheConnections.length; i++){
				String connectObjId = objectsIDOfTheConnections[i];
				if(connectObjId != null){
					counter++;
				}
		
		}
		return counter;

	}
	
	public static void TokenData(String[][] connectionInfo, String[][] objectInfo, String[][]AddInfo, int[] taskIndex, String[][] connArray) throws IOException{
		int currentTask =1;
		int systemCounter = 6;
		String[] TokenData=new String[AddInfo[0].length+5];     							//creates a string array where the token data for one task will be saved.
		String[][] allTokenData=new String[connectionInfo.length][AddInfo[0].length+5];		//creates a 2D string array where the token data for all tasks will be saved. each row in the 2D array is a task
		for(int i = 0; i< connectionInfo.length; i++){										// loop on each task(row) in the connectionInfo which is the objects with it retrived associated connection object IDs
			int numberOfconnections = getNumberOfConnections(connectionInfo[i]);
			System.out.println("the number of connections is "+ numberOfconnections);
			currentTask = (i+1);
			TokenData = new String[AddInfo[0].length+3];									//the TokenData is intialized here again so with every iteration ie. every task a new array will be formed to taken in the information
			System.out.println(TokenData.length);
			int taskIndex1 = taskIndex[i]; 
			String anum = "" + (i+1);
			TokenData[0]= anum;                         //anum								//anum is saved in the first slot of the tokenData array and it resembles the task number.
			TokenData[1]= objectInfo[taskIndex1][2];	//aname								//aname is saved in the second slot and it resembles the name of the task which is found in column with index 2 in the objectInfo[][] 
			if(!connectionInfo[i][numberOfconnections].contains("ObjDef")){					//to make sure that the last place is not an objectID for the tasks who doesn't have probabilities
			TokenData[5] = connectionInfo[i][numberOfconnections];							//probability of the task
			}
			for(int j=1; j <connectionInfo[i].length;j++){									//this loop is to get the extra information from the annotation such as the systems, duration, priority and the resources from the swimlane object
				if(connectionInfo[i][j] != null){
					String objID = connectionInfo[i][j];									//here the objectID of the connected element is captured and saved to be used in comparison with the objectInfo[][] in the next loop
					for (int k = 0; k < objectInfo.length;k++){								//this loop on the objectInfo[][] to search for the connected objectID to check whether its an annotation or a swimlane.
						if(objectInfo[k][0] != null && objID.equals(objectInfo[k][0])){		//here a comparison is made between the objID and the current object ID if they are equal meaning that the current object is one of the connected elements its type is saved.
							String type = objectInfo[k][1];									//the type of the object is saved here
							if(isAnnotation(type)){											//the type is checked to be an annotation
								System.out.println("is annotation ");
								String theAnnObjID = objectInfo[k][0];						//the ID is saved
								for(int r = 0; r < AddInfo.length; r++){					// the AddInfo[][] is then looped searching for the row with the object ID
									if(AddInfo[r][0]!=null && theAnnObjID.equals(AddInfo[r][0])){
										for(int d =1; d < AddInfo[r].length; d++ ){			//when the row is found the columns are looped to capture the elements in it
											String element = AddInfo[r][d];					//the element in the column is saved
											if(element != null && element.contains("duration")){	// if the element contains the word duration
												String[] e = AddInfo[r][d].split(":");		//the element is split on the ":" which produce an array of length 2
//												System.out.println(e[1]);
												TokenData[3]= e[1];				//aduration	//element in index 1 is the actual duration as in numbers not the word duration and its saved in the tokenData at index 3
											}
											if(element != null && element.contains("priority")){	// if the element contains the word priority
												String[] e = AddInfo[r][d].split(":");				//the element is split on the ":" which produce an array of length 2
//												System.out.println(e[1]);
												TokenData[4]= e[1];				//priotity //element in index 1 is the actual priority as in numbers not the word priority and its saved in the tokenData at index 4
											}
											if(element != null && element.contains("system")){		// if the element contains the word system
												String[] e = AddInfo[r][d].split(":");				//the element is split on the ":" which produce an array of length 2
												if(systemCounter < TokenData.length){				//systemcounter is the index where the systems will begin to be saved here its check that it is less that tokenData.length
												TokenData[systemCounter]= e[1];						//then the system is saved in the systemcounter index which is incremented with each system addition
												systemCounter++;}
											}
											
//											System.out.println(AddInfo[r][d]);
										}
									}
								}
							}
							if(isLane(type)){												//the object here is checked if its a lane
								
								String LaneID = objectInfo[k][0];							//if it is then then its ID is saved in the LaneID
								String resource = objectInfo[k][2];        //aresource		//the name of the lane is saved as it would be the resource
								if(!isSuperLane(LaneID, connArray, objectInfo)){			//here we check if the lane is not a superLane 
									TokenData[2] = resource;								//if its not a superlane then the resource is saved in the tokenData at index 2
								}
							}
						}
					}
				}
			}
			systemCounter = 6; 												//now that we are done with the task and about to go to the next we reset the systemcounter to 6
			for(int k = 0; k < TokenData.length; k++){				//this loop saves the data of the tokenData[] into the allTokenData[][] 
				allTokenData[i][k]= TokenData[k];
			}
		}
		printBoard(allTokenData);
		generateTokens(allTokenData);
		
	}
	
	public static void generateTokens(String[][] tokenData) throws IOException{			//used to generate the tokens for the CPN tools into the specified file
		PrintWriter printer = new PrintWriter(new FileWriter("C:\\Users\\mahmoud\\Desktop\\masters\\DHBW work\\thesis\\test\\valueDeclarationsTESTing.sml")); 
		ArrayList<String> usedSystems = new ArrayList<String>();
		for(int row =0; row < tokenData.length; row++){
				String anum = tokenData[row][0];
				String activityName = tokenData[row][1];
				String resource = tokenData[row][2];
				String priority = tokenData[row][4].replaceAll("\\s","");
				String duration = tokenData[row][3].replaceAll("\\s","");
				String probability = "1.0";						
				if(tokenData[row][5] != null){
					probability = tokenData[row][5];
				}
				System.out.println("==================================For writing the avilable systems========================================");
				int numOfSystems = 0;
				int syscounter = 0;
				String[] systems = new String[tokenData[row].length-5];
//				System.out.println("this is the systems length " + systems.length);
				for(int i = 5; i < tokenData[row].length; i++){
					if(tokenData[row][i] != null){
					systems[syscounter] = tokenData[row][i].trim();
//					System.out.print(" "+ tokenData[row][i]);
					syscounter++;
					}
				}
				System.out.println("this is the number of systems avilable "+ syscounter);
				String systemsSt = "";
				int TheSystemNum =0;
				while(numOfSystems < systems.length){
					if(systems[numOfSystems] != null){
						String system = systems[numOfSystems];
						
						for(int i =0 ; i <= usedSystems.size(); i++){
							if(usedSystems.contains(system)){
								TheSystemNum = usedSystems.indexOf(system);
								break;
							}else{
								usedSystems.add(system);
								TheSystemNum = usedSystems.indexOf(system);
								break;
							}
						}
							
					
					if(numOfSystems < syscounter-1 ){
					String systemElement = "{n="+ TheSystemNum +",s=\""+ systems[numOfSystems] +"\"},";
					systemsSt = systemsSt + systemElement;
					}else{
					String systemElement = "{n="+ TheSystemNum +",s=\""+ systems[numOfSystems] +"\"}";
					systemsSt = systemsSt + systemElement;
					}
					}
					System.out.println(numOfSystems);
					numOfSystems++;
				}
//				System.out.println(systemsSt);
				System.out.println("================================For writing the avilable systems==========================================");
				System.out.println("\n================================The main String token that intilizes the Activity==========================================");
				String x = "";
				if(row == 0){
					 x = "fun initServers() = 1`[{act={anum="+anum+",aname=\""+ activityName +"\"\n,aresource=\""+ resource +"\","
							+ "\nasystems=[" +systemsSt + "],\naduration="+duration+",AT=intTime(),priority="+priority+"},prob="+ probability +"},";	
				}else if(row == (tokenData.length)-1){
					x = x + "{act={anum="+anum+",aname=\""+ activityName +"\"\n,aresource=\""+ resource +"\","
							+ "\nasystems=[" +systemsSt + "],\naduration="+duration+",AT=intTime(),priority="+priority+"},prob="+ probability +"}];";
				}else if( row < tokenData.length && row != 0){
					x = x + "{act={anum="+anum+",aname=\""+ activityName +"\"\n,aresource=\""+ resource +"\","
							+ "\nasystems=[" +systemsSt + "],\naduration="+duration+",AT=intTime(),priority="+priority+"},prob="+ probability +"},";
				}
				
				System.out.println(x);
				printer.println(x);
				
			
		}
		String getFirstElement = "fun getFirstElement(p: Process) = hd p ;";
		printer.println(getFirstElement);
		printer.close();
		System.out.println("\n================================The main String token that intilizes the Activity==========================================");	
	}

	
	
	public static boolean isSuperLane(String LaneID, String[][] connArray,String[][] objInfo){			//used to find out if a certain lane exist inside a bigger one not as its own superLane
		String[] LaneIDsArray = new String[connArray.length];
		int counter = 0;
		for(int i = 0; i < connArray.length; i++){
			String IDcompare = connArray[i][3];
			if(IDcompare != null && IDcompare.equals(LaneID)){
				LaneIDsArray[counter]= connArray[i][0];
				counter++;
			}
		}
		for(int j =0; j <LaneIDsArray.length; j++){
			if(LaneIDsArray[j] != null){
				String LaneObjID = LaneIDsArray[j];
				for(int k = 0; k < objInfo.length; k++){
					if(objInfo[k][0] != null && objInfo[k][0].equals(LaneObjID)){
						String ObjType = objInfo[k][1];
						if(isLane(ObjType)){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	
	public static String[][] ArrayListConvereter(String[][] MultiSequance, ArrayList<String> tempSequance, int index){
//		System.out.println("Conv  "+ tempSequance);
		for(int i = 0; i < tempSequance.size(); i++){
			MultiSequance[index][i] = tempSequance.get(i);
		}
		return MultiSequance;
		
	}
	
		
	
	
	public static void ReadFileAndSave(final File selectedFile){
		 try {
			 
				
//				File fXmlFile = new File("C:\\Users\\User\\Desktop\\gam3a\\7th semester\\eclipse\\Aris code testing\\testing1smlgen2.xml");
			 	
			 	File fXmlFile = new File(selectedFile.getAbsolutePath());
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				
			 //-----------------------------------------------------------------------------------------------------------------------------------------------
				String[][] ObjectInfo = new String [50][6];
				String[][] connectionsArray = new String [50][5];
				String[][] connectionsRefsArray = new String [50][10];
				String[][] AddInfoArray = new String [50][15];		
				String[][] ModelElements = new String [10][15];
			//--------------------------------------------------------------------------------------------------------------------------------------------------	
				//optional, but recommended
				//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
				doc.getDocumentElement().normalize();
//				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
//				System.out.println("----------------------------");
			 
				NodeList ModelsList = doc.getElementsByTagName("Model");
				for(int ModelTemp = 0; ModelTemp < ModelsList.getLength(); ModelTemp++){
					Node modTemp = ModelsList.item(ModelTemp);
					Element modTempEl = (Element) modTemp;
					ModelElements[ModelTemp][0] = modTempEl.getAttribute("Model.ID");
					System.out.println(modTempEl.getAttribute("Model.ID")+ "\n");
					NodeList modObj = modTempEl.getElementsByTagName("ObjOcc");
					for(int objOccCo = 0;objOccCo < modObj.getLength(); objOccCo++){
						Node objOccNode = modObj.item(objOccCo);
						Element EobjOccNode = (Element) objOccNode;
						String name =  EobjOccNode.getAttribute("ObjDef.IdRef");
						ModelElements[ModelTemp][objOccCo+1] = name;
						System.out.println(name);
					}
					System.out.println("----------------------------------");
//					printBoard(ModelElements);
				}
				
				NodeList nList = doc.getElementsByTagName("ObjDef");                            //all objects in a list.
				int ConnectCountertemp =0;
				for (int temp = 0; temp < nList.getLength(); temp++) {
				
					Node nNode = nList.item(temp);											//the object with all its information in the node at number which is equal counter.
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {       
						Element eElement = (Element) nNode;									// casting the object into type Element so we can use it as an element.
						ObjectInfo[temp][0] = eElement.getAttribute("ObjDef.ID"); 				//saving the objectID in the first array (Object Array)
						//connectionsArray[temp][0] = ObjectInfo[temp][0];							// using the exact same ID for the second array so that could be used as the Key to connect them.
						connectionsRefsArray[temp][0] = ObjectInfo[temp][0];
						AddInfoArray[temp][0] = ObjectInfo[temp][0];
//						System.out.println("\nobject ID : " + ObjectInfo[temp][0]);			//this is the object ID
						ObjectInfo[temp][1] = eElement.getAttribute("SymbolNum") ; 
						if(eElement.getAttribute("SymbolNum").equals("ST_BPMN_SUBPROCESS")){
							ObjectInfo[temp][5]= eElement.getAttribute("LinkedModels.IdRefs"); 
						}
//						System.out.println("Symbol: " +ObjectInfo[temp][1]);				//this is the object type
						if(eElement.getAttribute("ToCxnDefs.IdRefs").contains("CxnDef")){								//this line of code i wrote so incase it doesn't have a connection the connection ID wouldn't show.
							String connectionRef= eElement.getAttribute("ToCxnDefs.IdRefs");
							String[] allCon = connectionRef.split(" ");
//							System.out.println(allCon.length);															//since some of the connection are split using spaces i splited on these spaces to save the connections in the
							for(int allConCounteri = temp; allConCounteri <= temp; allConCounteri++ ){					// the 2D array so that it can be used for searching
								for(int allConCounterj = 1; allConCounterj <= allCon.length; allConCounterj++ ){
									connectionsRefsArray[allConCounteri][allConCounterj] = allCon[allConCounterj-1];
								}
							}
						}
						
						//this part of the code if for getting the names of the objects as in their types.
						NodeList NameList = eElement.getElementsByTagName("AttrDef");			//getting the AttrDef for the specific object its a LIST
						for(int i = 0 ; i <NameList.getLength(); i++){
							Node NameNode = NameList.item(i);									//getting the AttrDef at the specific position for the specific object
							Element NameElements = (Element) NameNode;
							Node parentNode = NameNode.getParentNode();							//gets the parent of the specific node to check that it is equal to the specific object so it wouldn't get another object
							if(NameElements.getAttribute("AttrDef.Type").equals("AT_NAME") && parentNode.equals(nNode)){		//gets the name of the object READ the NAME.txt  
								if(eElement.getAttribute("SymbolNum").equals("ST_BPMN_ANNOTATION_1")){
									NodeList AdditionalInfoTask = NameElements.getElementsByTagName("PlainText"); 		//getting the plain text elements list in the connection just incase there is a couple not only one
//									System.out.println("There is ADDITIONAL INFORMATION "+ AdditionalInfoTask.getLength());
									for(int AddInfoCounter = 0; AddInfoCounter < AdditionalInfoTask.getLength();AddInfoCounter++){
										Node AddInfoNode = AdditionalInfoTask.item(AddInfoCounter);			//getting the specific plaintext element in a specific position
										if(AddInfoNode.getNodeType() == Node.ELEMENT_NODE){
											Element AddInfo = (Element) AddInfoNode;							//casting the object into an element so we can use it and get its value
											AddInfoArray[temp][AddInfoCounter+1]= AddInfo.getAttribute("TextValue");
												
											}
								}
								}else{
								Element e = (Element) NameElements.getElementsByTagName("PlainText").item(0);
								ObjectInfo[temp][2]= e.getAttribute("TextValue");							//System.out.println("Name is : " + ObjectInfo[temp][3]);

								}
							}
							if(NameElements.getAttribute("AttrDef.Type").equals("AT_ID")){
								
								Element e = (Element) NameElements.getElementsByTagName("PlainText").item(0);			//this is to be added beside tasks of subprocess to distinguish them from normal tasks
								ObjectInfo[temp][3]= e.getAttribute("TextValue");
							}
							
						}
						
						//this part is for getting the connections and their information
						
						//NodeList n = eElement.getChildNodes(); 
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
									connectionsArray[ConnectCountertemp][ConnectCounterj]=eElement.getAttribute("ObjDef.ID");
									connectionsArray[ConnectCountertemp][ConnectCounterj+1]=Conn.getAttribute("CxnDef.ID");
									connectionsArray[ConnectCountertemp][ConnectCounterj+2]=Conn.getAttribute("CxnDef.Type");
									connectionsArray[ConnectCountertemp][ConnectCounterj+3]=Conn.getAttribute("ToObjDef.IdRef");
									Element probability = (Element) Conn.getElementsByTagName("AttrDef").item(0);
									if( probability != null &&probability.getAttribute("AttrDef.Type").equals("AT_PROB")){
										connectionsArray[ConnectCountertemp][ConnectCounterj+4] = probability.getElementsByTagName("AttrValue").item(0).getTextContent(); //added the probability in the connection
									}
								}
							}
						}
							ConnectCounter++;
							ConnectCountertemp++;
						}	
					}
				}
				  
//This is where the matching and getting the info will be done
				
//				  printBoard(connectionsRefsArray);
//				  printBoards(connectionsArray, ObjectInfo,AddInfoArray,connectionsRefsArray);
//				  System.out.println(ConnectCountertemp);
//				  int[] taskIndex = SearchingTasks(ObjectInfo);		//returns an int array with the positions of the tasks in the 2D arrays
//				  String[][] retrivedConnRefrences = RetrivingRefs(taskIndex,connectionsRefsArray);
//				  String[][] objectsIDOfTheConnections = RetrivingConnectionsObjects(taskIndex, connectionsArray, ObjectInfo, retrivedConnRefrences);
				  System.out.println("--------------------------------------------------------------------");
//				  printBoard(objectsIDOfTheConnections);
//				  System.out.println("--------------------------------------------------------------------");
//				  printBoard(retrivedConnRefrences);
//				  System.out.println("--------------------------------------------------------------------");
//				  TokenData(objectsIDOfTheConnections,ObjectInfo, AddInfoArray,taskIndex, connectionsArray);
//				  ShiftRows(ObjectInfo, 2, 2);
//				  printBoard(ObjectInfo);
//				  printBoard(connectionsArray);
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			  }
	
 
  public static void main(String argv[]) {
	  final JFileChooser chooser = new JFileChooser();
      if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
    	  ReadFileAndSave(chooser.getSelectedFile());
      }
  }
 
}
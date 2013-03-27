package rapgreen;
import java.net.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.sql.*;
//import com.mysql.jdbc.Driver;

/**
* Executable program to manage reconciliation database (add trees ...)
* @author	Dufayard Jean-François
* @version	1.0
* date : 06/2010
*/
public class ReconciliationDatabaseDaemon {

// *******************************************************************************
//ATTRIBUTS
// *******************************************************************************

	/**
	* List of species trees
	*/
	static Vector speciesTreeIds;

	/**
	* List of database specifications
	*/
	static Hashtable specifications;

	/**
	* Table of species tree objects
	*/
	static Hashtable speciesTreeStructures;

	/**
	* Table of species tree node identifier index, contains hashtables
	*/
	static Hashtable speciesTreeIndex;

	/**
	* Table of species dictionnaries
	*/
	static Hashtable speciesTreeDictionaries;

	/**
	* Table of gene tree id lists, contains vectors
	*/
	static Hashtable geneTreeIds;

	/**
	* Table of gene tree objects, contains hashtables
	*/
	static Hashtable geneTreeStructures;

	/**
	* The socket object, modelising the server
	*/
	static ServerSocket server;

	/**
	* The communication port
	*/
	static final int port=1666;

	/**
	* Verbose / quiet boolean
	*/
	static boolean quiet= false;

   private static final String NORMAL     = "\u001b[0m";
   private static final String BOLD       = "\u001b[1m";
   private static final String UNDERLINE  = "\u001b[4m";

// *******************************************************************************


// *******************************************************************************
//EXECUTABLE & CONSTRUCTOR
// *******************************************************************************
	public ReconciliationDatabaseDaemon(String args[]) {


		if (!quiet) System.out.println("\nStarting reconciliation bank tree daemon...\n");

		Vector argsDatabases= new Vector();
		Vector argsDirectories= new Vector();
		Vector argsDirectoriesDico= new Vector();


 		//Initialization of collections
		speciesTreeIds= new Vector();
		specifications= new Hashtable();
		speciesTreeStructures= new Hashtable();
		speciesTreeIndex= new Hashtable();
		speciesTreeDictionaries= new Hashtable();

		geneTreeIds= new Hashtable();
		geneTreeStructures= new Hashtable();

		for (int i=0;i<args.length;i=i+2) {
			if (args[i].contains("help")) {
				System.out.println(BOLD);
				System.out.println("NAME:");
				System.out.println(NORMAL);
				System.out.println("\t- Reconciliation Database Daemon v1.0 -");
				System.out.println(BOLD);
				System.out.println("SYNOPSIS:");
				System.out.println(NORMAL);
				System.out.println("\treconciliationdatabasedaemon [command args]");
				System.out.println(BOLD);
				System.out.println("OPTIONS:");
				System.out.println(BOLD);
				System.out.println("-database" + NORMAL + " " + UNDERLINE + "database_id" + NORMAL + " " + UNDERLINE + "species_tree_file" + NORMAL + " " + UNDERLINE + "gene_tree_directory" + NORMAL + " " + UNDERLINE + "gene_tree_file\n\t" + NORMAL + "The identifier of the database, the species tree file, the directory containing all gene tree files, and the tree file name eventually containing 'INPUT' marker for directory name.");
				System.out.println(BOLD);
				System.out.println("-directory" + NORMAL + " " + UNDERLINE + "database_id" + NORMAL + " " + UNDERLINE + "species_tree_file" + NORMAL + " " + UNDERLINE + "gene_tree_directory\n\t" + NORMAL + "Alternate version of database option: one simple directory full of gene trees. Arguments are: the identifier of the database, the species tree file and the directory containing all gene tree files.");
				System.out.println(BOLD);
				System.out.println("-directoryDico" + NORMAL + " " + UNDERLINE + "database_id" + NORMAL  + " " + UNDERLINE + "species_tree_file" + NORMAL + " " + UNDERLINE + "species_dictionary" + NORMAL + " " + UNDERLINE + "gene_tree_directory\n\t" + NORMAL + "Alternate version of database option: one simple directory full of gene trees, and using a species dictionary. Arguments are: the identifier of the database, the species tree file , the species dictionary, and the directory containing all gene tree files.");
				System.out.println(BOLD);
				System.out.println("-quiet\n\t" + NORMAL + "Activate this option to turn off\n\n");
				System.exit(0);
			}

			if (args[i].equalsIgnoreCase("-database")) {

				String[] localArgs= new String[5];
				localArgs[0]= args[i+1];
				localArgs[1]= args[i+2];
				localArgs[2]= args[i+3];
				localArgs[3]= args[i+4];
				localArgs[4]= args[i+5];

				argsDatabases.addElement(localArgs);

	        	i+=3;
			}

			if (args[i].equalsIgnoreCase("-directory")) {

				String[] localArgs= new String[4];
				localArgs[0]= args[i+1];
				localArgs[1]= args[i+2];
				localArgs[2]= args[i+3];
				localArgs[3]= args[i+4];

				argsDirectories.addElement(localArgs);

	        	i+=3;
			}
			if (args[i].equalsIgnoreCase("-directoryDico")) {

				String[] localArgs= new String[5];
				localArgs[0]= args[i+1];
				localArgs[1]= args[i+2];
				localArgs[2]= args[i+3];
				localArgs[3]= args[i+4];
				localArgs[4]= args[i+5];

				argsDirectoriesDico.addElement(localArgs);

	        	i+=3;
			}			
			if (args[i].equalsIgnoreCase("-quiet")) {
				quiet=true;
				i--;
			}
		}

		for (int t=0;t<argsDatabases.size();t++) {
			String[] localArgs= (String[])(argsDatabases.elementAt(t));
			String localId="";

	        try {


        		String speciesTreeId= localArgs[0];
        		String specification= localArgs[4];
        		speciesTreeIds.addElement(speciesTreeId);
        		specifications.put(speciesTreeId,specification);
				if (!quiet) System.out.print("Databank: " + speciesTreeId + "... ");

				SpeciesDictionary dico= new SpeciesDictionary();
				Hashtable localSpeciesIndex= new Hashtable();

				String test= (new BufferedReader(new FileReader(new File(localArgs[1])))).readLine();
				TreeReader read=null;
				if (test.endsWith(";")) {
					read= new TreeReader(new File(localArgs[1]),dico,localSpeciesIndex,TreeReader.NEWICK);
				} else {
					read= new TreeReader(new File(localArgs[1]),dico,localSpeciesIndex,TreeReader.XML,TreeReader.SIMPLE);
				}
				Tree speciesTree= read.nextTree();
				//System.out.println(speciesTree);
				speciesTree.pretreatment();
				speciesTreeStructures.put(speciesTreeId,speciesTree);
				speciesTreeIndex.put(speciesTreeId,localSpeciesIndex);
				speciesTreeDictionaries.put(speciesTreeId,dico);

				//System.out.println(dico.getScientificName("PITOR1"));

        		Vector geneIds= new Vector();
        		Hashtable geneStructures= new Hashtable();
        		File[] geneFiles = (new File(localArgs[2])).listFiles();
        		BufferedReader read2=null;
        		int lak=1;
        		for (int j=0;j<geneFiles.length;j++) {
        			if (geneFiles[j].isDirectory()) {
        				File[] geneInFiles = geneFiles[j].listFiles();


	        			for (int k=0;k<geneInFiles.length;k++) {
		        			if (geneInFiles[k].isDirectory()) {
								try {
									localId= geneInFiles[k].getName();
									geneIds.addElement(localId);
									File geneFile= new File(geneInFiles[k].getPath() + "/" + localArgs[3].replace("INPUT",geneInFiles[k].getName()));
									read2=new BufferedReader(new FileReader(geneFile));
									test= read2.readLine();
									if (test.endsWith(";")) {
										read= new TreeReader(geneFile,TreeReader.NEWICK);
									} else {
										read= new TreeReader(geneFile,TreeReader.XML);
									}
									read2.close();
									Tree tree= read.nextTree();
									tree.taxonomicPretreatment();
									geneStructures.put(localId,tree);
									lak++;
									if (lak%1000==0)
										System.out.println(lak);

								} catch (Exception E) {
									read2.close();
									//E.printStackTrace();
									//System.out.println(localId);
								}
		        			}
	        			}
        			}
        		}


				geneTreeIds.put(speciesTreeId,geneIds);
				geneTreeStructures.put(speciesTreeId,geneStructures);

				if (!quiet) System.out.println("Loaded.");



	        } catch (Exception E) {
	        	E.printStackTrace();
	        	System.out.println(localId);
	        }
		}
		
		
		
		for (int t=0;t<argsDirectories.size();t++) {
			String[] localArgs= (String[])(argsDirectories.elementAt(t));
			String localId="";

	        try {


        		String speciesTreeId= localArgs[0];
        		String specification= localArgs[3];
        		speciesTreeIds.addElement(speciesTreeId);
        		specifications.put(speciesTreeId,specification);
				if (!quiet) System.out.print("Databank: " + speciesTreeId + "... ");

				SpeciesDictionary dico= new SpeciesDictionary();
				Hashtable localSpeciesIndex= new Hashtable();

				String test= (new BufferedReader(new FileReader(new File(localArgs[1])))).readLine();
				TreeReader read=null;
				if (test.endsWith(";")) {
					read= new TreeReader(new File(localArgs[1]),dico,localSpeciesIndex,TreeReader.NEWICK);
				} else {
					read= new TreeReader(new File(localArgs[1]),dico,localSpeciesIndex,TreeReader.XML,TreeReader.SIMPLE);
				}
				Tree speciesTree= read.nextTree();
				//System.out.println(speciesTree);
				speciesTree.pretreatment();
				dico.setSpeciesTree(speciesTree);
				speciesTreeStructures.put(speciesTreeId,speciesTree);
				speciesTreeIndex.put(speciesTreeId,localSpeciesIndex);
				speciesTreeDictionaries.put(speciesTreeId,dico);

				//System.out.println(dico.getScientificName("PITOR1"));

        		Vector geneIds= new Vector();
        		Hashtable geneStructures= new Hashtable();
        		File[] geneFiles = (new File(localArgs[2])).listFiles();
        		BufferedReader read2=null;
        		int lak=1;
        		for (int j=0;j<geneFiles.length;j++) {

					try {
						localId= geneFiles[j].getName();
						geneIds.addElement(localId);
						File geneFile= new File(geneFiles[j].getPath());
						read2=new BufferedReader(new FileReader(geneFile));
						test= read2.readLine();
						if (test.endsWith(";")) {
							read= new TreeReader(geneFile,TreeReader.NEWICK);
						} else {
							read= new TreeReader(geneFile,TreeReader.XML);
						}
						read2.close();
						Tree tree= read.nextTree();
						tree.taxonomicPretreatment();
						//System.out.println(tree);
						//System.out.println(tree.nbLeaves());
						geneStructures.put(localId,tree);
						lak++;
						if (lak%1000==0)
							System.out.println(lak);

					} catch (Exception E) {
						read2.close();
						//E.printStackTrace();
						//System.out.println(localId);
					}



        		}


				geneTreeIds.put(speciesTreeId,geneIds);
				geneTreeStructures.put(speciesTreeId,geneStructures);

				if (!quiet) System.out.println("Loaded.");



	        } catch (Exception E) {
	        	E.printStackTrace();
	        	System.out.println(localId);
	        }
		}
for (int t=0;t<argsDirectoriesDico.size();t++) {
			String[] localArgs= (String[])(argsDirectoriesDico.elementAt(t));
			String localId="";

	        try {


        		String speciesTreeId= localArgs[0];
        		String specification= localArgs[4];
        		speciesTreeIds.addElement(speciesTreeId);
        		specifications.put(speciesTreeId,specification);
				if (!quiet) System.out.print("Databank: " + speciesTreeId + "... ");

				SpeciesDictionary dico= new SpeciesDictionary();
				Hashtable localSpeciesIndex= new Hashtable();

				TreeReader read= new TreeReader(new File(localArgs[1]),TreeReader.NEWICK);
				
				Tree speciesTree= read.nextTree();
				//System.out.println(speciesTree);
				speciesTree.pretreatment();
				dico.parseSpeciesDico(new File(localArgs[2]));
				dico.setSpeciesTree(speciesTree);
				speciesTreeStructures.put(speciesTreeId,speciesTree);
				speciesTreeIndex.put(speciesTreeId,localSpeciesIndex);
				speciesTreeDictionaries.put(speciesTreeId,dico);

				//System.out.println(dico.getScientificName("N5"));

        		Vector geneIds= new Vector();
        		Hashtable geneStructures= new Hashtable();
        		File[] geneFiles = (new File(localArgs[3])).listFiles();
        		BufferedReader read2=null;
        		int lak=1;
        		for (int j=0;j<geneFiles.length;j++) {

					try {
						localId= geneFiles[j].getName();
						geneIds.addElement(localId);
						File geneFile= new File(geneFiles[j].getPath());
						read2=new BufferedReader(new FileReader(geneFile));
						String test= read2.readLine();
						if (test.endsWith(";")) {
							read= new TreeReader(geneFile,TreeReader.NEWICK);
						} else {
							read= new TreeReader(geneFile,TreeReader.XML);
						}
						read2.close();
						Tree tree= read.nextTree();
						tree.taxonomicPretreatment();
						//System.out.println(tree);
						geneStructures.put(localId,tree);
						lak++;
						if (lak%1000==0)
							System.out.println(lak);

					} catch (Exception E) {
						read2.close();
						E.printStackTrace();
						//System.out.println(localId);
					}



        		}


				geneTreeIds.put(speciesTreeId,geneIds);
				geneTreeStructures.put(speciesTreeId,geneStructures);

				if (!quiet) System.out.println("Loaded.");



	        } catch (Exception E) {
	        	E.printStackTrace();
	        	System.out.println(localId);
	        }
		}

        // Open the server socket, and wait for connexion

        try {
        	server= new ServerSocket(port);
        	System.out.println("\nWaiting for clients...");
        	while (true) {
        		Socket socket=server.accept();
        		ManageSocket manageSocket= new ManageSocket(socket);
        		Thread t= new Thread(manageSocket);
        		t.start();
        	}


        } catch(Exception e) {
        	e.printStackTrace();

        }

	}

	public static void main(String args[]) {

        ReconciliationDatabaseDaemon daemon=new ReconciliationDatabaseDaemon(args);




	}
// *******************************************************************************
// SUBCLASSES
// *******************************************************************************
/**
* Class that implements multithreading
*/
	class ManageSocket implements Runnable {
	   	Socket socket;

	   	public ManageSocket(Socket socket) {
	   		this.socket=socket;
	   	}

   		public void run() {
	        	PrintWriter out = null;
        		BufferedReader in = null;
   			try {

	   			// lower the priority of this subthread
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

				// open streams
            	out = new PrintWriter(socket.getOutputStream(), true);
            	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


				// read the first instruction from client
				String s= in.readLine();

				if (s.equalsIgnoreCase("databanks")) {
				// case 1: clients asks for databank list
					for (int i=0;i<speciesTreeIds.size();i++) {
						out.println((String)(speciesTreeIds.elementAt(i)));

					}
				} else if (s.equalsIgnoreCase("specification")) {
				// case 1.5: clients asks for databank specification
					String databank=in.readLine();
					out.println((String)(specifications.get(databank)));


				} else if (s.equals("scientific")) {
				// case 2: clients asks for a scientific name
					String databank=in.readLine();
					String codesp=in.readLine();
					SpeciesDictionary dico= (SpeciesDictionary)(speciesTreeDictionaries.get(databank));
					out.println(dico.getScientificName(codesp));
				} if (s.equals("speciesPlus")) {
				// case 2: clients asks for species list
					String databank=in.readLine();
					String prefix=in.readLine();
					SpeciesDictionary dico= (SpeciesDictionary)(speciesTreeDictionaries.get(databank));
					Vector res= dico.containsScientificNameList(prefix);
					List list= new ArrayList();
					for (int i=0;i<res.size();i++) {
						String codesp= (String)(res.elementAt(i));
						//String splited[]= dico.getScientificName(codesp).split(" ");
						list.add(dico.getScientificName(codesp) + "|" + codesp);
					}
					Collections.sort(list);
					for (int i=0;i<res.size();i++) {
						out.println((String)(list.get(i)));
					}
				} else if (s.equals("species")) {
				// case 2: clients asks for species list
					String databank=in.readLine();
					String prefix=in.readLine();
					SpeciesDictionary dico= (SpeciesDictionary)(speciesTreeDictionaries.get(databank));
					Vector res= dico.prefixScientificNameList(prefix);
					List list= new ArrayList();
					for (int i=0;i<res.size();i++) {
						String codesp= (String)(res.elementAt(i));
						//String splited[]= dico.getScientificName(codesp).split(" ");
						list.add(dico.getScientificName(codesp) + "|" + codesp);
					}
					Collections.sort(list);
					for (int i=0;i<res.size();i++) {
						out.println((String)(list.get(i)));
					}
				} else if (s.equals("tree")) {
					String databank=in.readLine();
					//System.out.println(databank);
					Tree tree= (Tree)(speciesTreeStructures.get(databank));
					//System.out.println(tree);
					Hashtable speciesList= new Hashtable();
					s= in.readLine();

					//System.out.println(s);
					while (!s.equals("end")) {
						speciesList.put(s,1);
						s= in.readLine();

					//System.out.println(s);
					}
					out.println(tree.subtree(speciesList).getSimpleNewick());
					//System.out.println(tree.subtree(speciesList));
				} else if (s.equals("patternSearch")) {
					String databank=in.readLine();
					//System.out.println(databank);
					Tree tree= (Tree)(speciesTreeStructures.get(databank));
					Hashtable ind= (Hashtable)(speciesTreeIndex.get(databank));
					SpeciesDictionary dic= (SpeciesDictionary)(speciesTreeDictionaries.get(databank));

					Vector treeIds= (Vector)(geneTreeIds.get(databank));
					Hashtable trees= (Hashtable)(geneTreeStructures.get(databank));
					//Hashtable refTrees= (Hashtable)(referenceTreeStructures.get(databank));

					Tree pattern= new Tree(in.readLine());
					pattern.patternPretreatment(tree);
					//System.out.println("test:" + pattern.leafVector.size());

					//System.out.println(s);
					for (int i=0;i<treeIds.size();i++) {
						String id= (String)(treeIds.elementAt(i));
						Tree geneTree= (Tree)(trees.get(id));
						//GeneTree refTree= (GeneTree)(refTrees.get(id));
						//System.out.println(geneTree);
						try {
							if (geneTree!=null && geneTree.containsPattern(pattern,ind,dic)) {
								out.println(id);
							}/* else {
								out.println("Biip");
							}*/
						} catch(Exception e) {

						}
					//System.out.println(s);
					}
					//System.out.println(tree.subtree(speciesList));
				} else if (s.equals("patternDisplay")) {
					String databank=in.readLine();
					Tree tree= (Tree)(speciesTreeStructures.get(databank));
					String id=in.readLine();
					Hashtable trees= (Hashtable)(geneTreeStructures.get(databank));
					Hashtable ind= (Hashtable)(speciesTreeIndex.get(databank));
					SpeciesDictionary dic= (SpeciesDictionary)(speciesTreeDictionaries.get(databank));
					//Hashtable refTrees= (Hashtable)(referenceTreeStructures.get(databank));
					String patternString=in.readLine();
					System.out.println(patternString);
					Tree pattern= new Tree(patternString);
					pattern.patternPretreatment(tree);
					Tree geneTree= (Tree)(trees.get(id));
					//GeneTree refTree= (GeneTree)(refTrees.get(id));
					//Tree copyCat= new Tree(geneTree);
					Vector colored= new Vector();
					geneTree.colorPattern(pattern,pattern,ind,dic,colored);
					for (int i=0;i<colored.size();i++) {
						Tree node= (Tree)(colored.elementAt(i));

						if (node.label.startsWith("L_")) {

							out.println(node.label.substring(node.label.lastIndexOf("_")+1,node.label.length()));
						} else if (node.isLeaf()) {
							out.println(node.label.substring(0,node.label.indexOf("_")));

						} else {
							out.println(node.label.substring(node.label.lastIndexOf("_")+1,node.label.length()));

						}
					}
				} else if (s.equals("patternNewick")) {
					String databank=in.readLine();
					String id=in.readLine();
					Tree tree= (Tree)(speciesTreeStructures.get(databank));
					Hashtable trees= (Hashtable)(geneTreeStructures.get(databank));
					Hashtable ind= (Hashtable)(speciesTreeIndex.get(databank));
					SpeciesDictionary dic= (SpeciesDictionary)(speciesTreeDictionaries.get(databank));
					//Hashtable refTrees= (Hashtable)(referenceTreeStructures.get(databank));

					String patternString=in.readLine();
					System.out.println(patternString);
					Tree pattern= new Tree(patternString);
					pattern.patternPretreatment(tree);
					Tree geneTree= new Tree((Tree)(trees.get(id)));
					geneTree.taxonomicPretreatment();
					Vector colored= new Vector();
					geneTree.colorPattern(pattern,pattern,ind,dic,colored);
					//GeneTree refTree= (GeneTree)(refTrees.get(id));
					//Tree copyCat= new Tree(geneTree);
					//System.out.println(colored.size());


					for (int i=0;i<colored.size();i++) {
						Tree localTree= (Tree)(colored.elementAt(i));
						//System.out.println("\n" + localTree);
						localTree.label= "COLORED_" + localTree.label;
					}

					out.println(geneTree.getNewick());



				}

				in.close();
				out.close();
				socket.close();


   			} catch (Exception e) {
   				e.printStackTrace();
				try {
					in.close();
					out.close();
					socket.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
   			}
   		}



   }







}




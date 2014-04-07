package rapgreen;
import java.net.*;

import java.io.*;

import java.lang.reflect.*;

import java.util.*;
import java.sql.*;
//import com.mysql.jdbc.Driver;

import java.util.UUID;



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
	* Configuration file
	*/
	static File configFile=null;
	static Hashtable examples;
	
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
	* Historic of researches
	*/
	static Hashtable historyTable;
	static Vector historyVector;
	
	/**
	* Path to the result directory
	*/
	static File resultDirectory;

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
		Vector argsFilesDico= new Vector();


 		//Initialization of collections
		speciesTreeIds= new Vector();
		specifications= new Hashtable();
		speciesTreeStructures= new Hashtable();
		speciesTreeIndex= new Hashtable();
		speciesTreeDictionaries= new Hashtable();
		historyVector= new Vector();
		historyTable= new Hashtable();

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
				System.out.println("-directory" + NORMAL + " " + UNDERLINE + "database_id" + NORMAL + " " + UNDERLINE + "species_tree_file" + NORMAL + " " + UNDERLINE + "gene_tree_directory" + NORMAL + " " + UNDERLINE + "type\n\t" + NORMAL + "Alternate version of database option: one simple directory full of gene trees. Arguments are: the identifier of the database, the species tree file, the directory containing all gene tree files, and the type of the database (NR, DL or DTL).");

				System.out.println(BOLD);
				System.out.println("-directoryDico" + NORMAL + " " + UNDERLINE + "database_id" + NORMAL  + " " + UNDERLINE + "species_tree_file" + NORMAL + " " + UNDERLINE + "species_dictionary" + NORMAL + " " + UNDERLINE + "gene_tree_directory" + NORMAL + " " + UNDERLINE + "type\n\t" + NORMAL + "Alternate version of database option: one simple directory full of gene trees, and using a species dictionary. Arguments are: the identifier of the database, the species tree file , the species dictionary, the directory containing all gene tree files, and the type of the database (NR, DL or DTL).");
				System.out.println(BOLD);
				System.out.println("-fileDico" + NORMAL + " " + UNDERLINE + "database_id" + NORMAL  + " " + UNDERLINE + "species_tree_file" + NORMAL + " " + UNDERLINE + "species_dictionary" + NORMAL + " " + UNDERLINE + "gene_tree_file" + NORMAL + " " + UNDERLINE + "type" + NORMAL + " " + UNDERLINE + "[invert]\n\t" + NORMAL + "Alternate version of database option: one simple file full of gene trees, and using a species dictionary. Arguments are: the identifier of the database, the species tree file , the species dictionary, the directory containing all gene tree files, and the type of the database (NR, DL or DTL). Add invert keyword if the species identifier is at the begining of sequence names in phylogenetic trees.");
				
				//System.out.println(BOLD);
				//System.out.println("-results" + NORMAL + " " + UNDERLINE + "directory\n\t" + NORMAL + "Directory, visible from the web, to stock result files for users. Directory must exist.");
				
				System.out.println(BOLD);
				System.out.println("-config" + NORMAL + " " + UNDERLINE + "config_file\n\t" + NORMAL + "Configuration file for this occurrence of the server.");

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
				//System.out.println("echo1");
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


	        	i+=4;

			}	
			if (args[i].equalsIgnoreCase("-fileDico")) {
				String[] localArgs= new String[5];
				if (i+6<args.length && args[i+6].equals("invert")) {
					localArgs= new String[6];
					localArgs[5]= args[i+6];
				}
			
			
				localArgs[0]= args[i+1];
				localArgs[1]= args[i+2];
				localArgs[2]= args[i+3];
				localArgs[3]= args[i+4];
				localArgs[4]= args[i+5];

				argsFilesDico.addElement(localArgs);

	        	i+=3;
				if (i+6<args.length && args[i+6].equals("invert")) {
					i++;
				}
			}			

			if (args[i].equalsIgnoreCase("-config")) {

				configFile= new File(args[i+1]);


			}
			
			if (args[i].equalsIgnoreCase("-results")) {

				resultDirectory= new File(args[i+1]);


			}
			if (args[i].equalsIgnoreCase("-quiet")) {

				quiet=true;

				i--;

			}

		}
		for (int t=0;t<argsFilesDico.size();t++) {
			String[] localArgs= (String[])(argsFilesDico.elementAt(t));
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
				speciesTree.globalPretreatment();
				dico.parseSpeciesDico(new File(localArgs[2]));
				dico.setSpeciesTree(speciesTree);
				speciesTreeStructures.put(speciesTreeId,speciesTree);
				speciesTreeIndex.put(speciesTreeId,localSpeciesIndex);
				speciesTreeDictionaries.put(speciesTreeId,dico);

				//System.out.println(dico.getScientificName("N5"));

        		Vector geneIds= new Vector();
        		Hashtable geneStructures= new Hashtable();
				try {
        		BufferedReader geneFiles = new BufferedReader(new FileReader(new File(localArgs[3])));
        		//BufferedReader read2=null;
        		int lak=1;
				String s= geneFiles.readLine();
        		while (s!=null) {

					localId= s.substring(0,s.indexOf(" "));
					geneIds.addElement(localId);

					Tree tree= new Tree(s.substring(s.indexOf(" ")+1,s.length()));
					tree.pretreatment();
					if (localArgs.length>5 && localArgs[5].equals("invert")) {
						for (int i=0;i<tree.leafVector.size();i++) {
							try {
								Tree leaf= (Tree)(tree.leafVector.elementAt(i));
								leaf.label=leaf.label.substring(leaf.label.indexOf("_")+1,leaf.label.length()) + "_" + leaf.label.substring(0,leaf.label.indexOf("_"));
							} catch(Exception exp) {
								//System.out.println("Warning: " + localId);
							}
						}
					}
					if (specification.equals("NR")) {
						tree.addSpeciations();
					}
					tree.taxonomicPretreatment();
					//System.out.println(tree);
					geneStructures.put(localId,tree);
					lak++;
					if (lak%1000==0)
						System.out.println(lak);

			
					s= geneFiles.readLine();


        		}

				} catch (Exception E) {
					E.printStackTrace();
				}
				geneTreeIds.put(speciesTreeId,geneIds);
				geneTreeStructures.put(speciesTreeId,geneStructures);

				if (!quiet) System.out.println("Loaded.");



	        } catch (Exception E) {
	        	E.printStackTrace();
	        	System.out.println(localId);
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
		
		examples= new Hashtable();
		if (configFile!=null) {
			try {
				BufferedReader read= new BufferedReader(new FileReader(configFile));
				String s=read.readLine();
				while (s!=null && !s.startsWith("EXAMPLE")) {
					s=read.readLine();			
				}
				if (s!=null) {
					s=read.readLine();
					while (s!=null) {
						String[] sp= s.split("\t");
						if (examples.containsKey(sp[0])) {
							Vector localEx= (Vector)(examples.get(sp[0]));
							localEx.addElement(sp[1]);
							localEx.addElement(sp[2]);
						} else {
							Vector localEx= new Vector();
							localEx.addElement(sp[1]);
							localEx.addElement(sp[2]);
							examples.put(sp[0],localEx);
						
						}
						s=read.readLine();
					
					}
				
				}
			
			} catch(Exception expe) {
				expe.printStackTrace();
			
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

					//System.out.println(s);
				if (s.equalsIgnoreCase("databanks")) {
				// case 1: clients asks for databank list
					for (int i=0;i<speciesTreeIds.size();i++) {
						out.println((String)(speciesTreeIds.elementAt(i)));

					}
				} if (s.equalsIgnoreCase("examples")) {
				// case 1: clients asks for pattern example list for this databank
					String databank=in.readLine();
					if (examples!=null && examples.containsKey(databank)) {
						Vector localEx= (Vector)(examples.get(databank));
						for (int i=0;i<localEx.size();i++) {
							out.println((String)(localEx.elementAt(i)));

						}
					
					
					} else {
						out.println("N/A");
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
				} else if (s.equals("resultSplit")) {
				//System.out.println("echo1");
					String idRandom= in.readLine();
				//System.out.println(idRandom);
					int start= (new Integer(in.readLine())).intValue();
				//System.out.println(start);
					int range= (new Integer(in.readLine())).intValue();
				//System.out.println(range);
					Vector localRes= (Vector)(historyTable.get(idRandom));
					for (int i=start;i<start+range && i<localRes.size();i++) {
						out.println((String)(localRes.elementAt(i)));
				//System.out.println((String)(localRes.elementAt(i)));
					}
				} else if (s.equals("patternSearch")) {
					UUID idRandom = UUID.randomUUID();
					Vector localRes= new Vector();
					String databank=in.readLine();
					//System.out.println(databank);
					Tree tree= (Tree)(speciesTreeStructures.get(databank));
					Hashtable ind= (Hashtable)(speciesTreeIndex.get(databank));
					SpeciesDictionary dic= (SpeciesDictionary)(speciesTreeDictionaries.get(databank));

					Vector treeIds= (Vector)(geneTreeIds.get(databank));
					Hashtable trees= (Hashtable)(geneTreeStructures.get(databank));
					//Hashtable refTrees= (Hashtable)(referenceTreeStructures.get(databank));

					Tree pattern= new Tree(in.readLine());
					pattern.patternPretreatment(tree,dic);
					//System.out.println("test:" + pattern.leafVector.size());

					//System.out.println(s);
					for (int i=0;i<treeIds.size();i++) {
						String id= (String)(treeIds.elementAt(i));
						Tree geneTree= (Tree)(trees.get(id));
						//GeneTree refTree= (GeneTree)(refTrees.get(id));
						//System.out.println(geneTree);
						try {

							if (geneTree!=null && geneTree.containsPattern(pattern,ind,dic)) {
								//out.println(id);
								localRes.addElement(id);
							}/* else {
								out.println("Biip");
							}*/
						} catch(Exception e) {



						}

					//System.out.println(s);
					}
					out.println(idRandom);
					out.println(localRes.size());
					historyVector.addElement(idRandom.toString());
					historyTable.put(idRandom.toString(),localRes);
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
					pattern.patternPretreatment(tree,dic);
					Tree geneTree= (Tree)(trees.get(id));
					//GeneTree refTree= (GeneTree)(refTrees.get(id));
					//Tree copyCat= new Tree(geneTree);
					Vector colored= new Vector();
					Hashtable stickers= new Hashtable();
					geneTree.colorPattern(pattern,pattern,ind,dic,colored,stickers);
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
				} else if (s.equals("patternNewickPost")) {
					String databank=in.readLine();
					String id=in.readLine();
					Tree tree= (Tree)(speciesTreeStructures.get(databank));

					Hashtable trees= (Hashtable)(geneTreeStructures.get(databank));
					Hashtable ind= (Hashtable)(speciesTreeIndex.get(databank));
					SpeciesDictionary dic= (SpeciesDictionary)(speciesTreeDictionaries.get(databank));
					//Hashtable refTrees= (Hashtable)(referenceTreeStructures.get(databank));


					String patternString=in.readLine();

					//System.out.println(patternString);
					Tree pattern= new Tree(patternString);
					pattern.patternPretreatment(tree,dic);
					Tree geneTree= new Tree((Tree)(trees.get(id)));
					geneTree.taxonomicPretreatment();
					Vector colored= new Vector();
					Hashtable stickers= new Hashtable();
					geneTree.colorPattern(pattern,pattern,ind,dic,colored,stickers);
					//GeneTree refTree= (GeneTree)(refTrees.get(id));
					//Tree copyCat= new Tree(geneTree);
					//System.out.println(colored.size());


					for (int i=0;i<colored.size();i++) {
						Tree localTree= (Tree)(colored.elementAt(i));
						//System.out.println("\n" + localTree);
						localTree.label= "COLORED_" + localTree.label;
					}
					StringBuffer sb= new StringBuffer();
					for (int i=0;i<colored.size();i++) {
						Tree localTree= (Tree)(colored.elementAt(i));
						if (localTree.isLeaf()) {
							sb.append(localTree.label.substring(8,localTree.label.length()) + "\t");
							int countD=0;
							while (localTree.father!=null && localTree.father.label.startsWith("COLORED_")) {
								localTree=localTree.father;
								countD++;
							}
							sb.append(countD + "\t");
						}
						//System.out.println("\n" + localTree);
					}
					//out.println(geneTree.getNewick());
					out.println(sb.toString());



				} else if (s.equals("patternNewick")) {
					String databank=in.readLine();
					String id=in.readLine();
					Tree tree= (Tree)(speciesTreeStructures.get(databank));

					Hashtable trees= (Hashtable)(geneTreeStructures.get(databank));
					Hashtable ind= (Hashtable)(speciesTreeIndex.get(databank));
					SpeciesDictionary dic= (SpeciesDictionary)(speciesTreeDictionaries.get(databank));
					//Hashtable refTrees= (Hashtable)(referenceTreeStructures.get(databank));


					String patternString=in.readLine();

					//System.out.println(patternString);
					Tree pattern= new Tree(patternString);
					pattern.patternPretreatment(tree,dic);
					Tree geneTree= new Tree((Tree)(trees.get(id)));
					geneTree.taxonomicPretreatment();
					Vector colored= new Vector();
					Hashtable stickers= new Hashtable();
					geneTree.colorPattern(pattern,pattern,ind,dic,colored,stickers);
					//GeneTree refTree= (GeneTree)(refTrees.get(id));
					//Tree copyCat= new Tree(geneTree);
					//System.out.println(colored.size());


					for (int i=0;i<colored.size();i++) {
						Tree localTree= (Tree)(colored.elementAt(i));
						//System.out.println("\n" + localTree);
						localTree.label= "COLORED_" + localTree.label;
					}

					out.println(geneTree.getNewick());



				} else if (s.equals("saveResults")) {
					//System.out.println(s);
					String databank=in.readLine();
					//System.out.println(databank);
					String idRandom=in.readLine();
					//System.out.println(idRandom);
					String patternString=in.readLine();
					//System.out.println(patternString);
					
					
					Hashtable trees= (Hashtable)(geneTreeStructures.get(databank));
					Hashtable ind= (Hashtable)(speciesTreeIndex.get(databank));
					Tree tree= (Tree)(speciesTreeStructures.get(databank));
					SpeciesDictionary dic= (SpeciesDictionary)(speciesTreeDictionaries.get(databank));
					Tree pattern= new Tree(patternString);
					pattern.patternPretreatment(tree,dic);

					Vector localHistory= (Vector)(historyTable.get(idRandom));
					//Vector treeIds= (Vector)(geneTreeIds.get(databank));
					try {
						//BufferedWriter write= new BufferedWriter(new FileWriter(new File(resultDirectory.getPath() + "/" + idRandom + ".csv")));

						out.println("FAMILY;SEQUENCE;LABEL;PATTERN");
						for (int i=0;i<localHistory.size();i++) {
							String id = (String)(localHistory.elementAt(i));
							//write.write(id + "\n");
							//write.flush();
							Tree geneTree= (Tree)(trees.get(id));
							Vector colored= new Vector();
							Hashtable stickers= new Hashtable();
							geneTree.colorPattern(pattern,pattern,ind,dic,colored,stickers);
							//String prev=" ";
							int count=1;
							Hashtable trace= new Hashtable();
							for (int j=0;j<colored.size();j++) {						
								Tree localTree= (Tree)(colored.elementAt(j));
								trace.put(localTree," ");
							}
							Hashtable roots= new Hashtable();
							for (int j=0;j<colored.size();j++) {						
								Tree localTree= (Tree)(colored.elementAt(j));
								if (localTree.father==null || !trace.containsKey(localTree.father)) {
									roots.put(localTree," ");
									//System.out.println(localTree);
								}
							}
						
							for (int j=0;j<colored.size();j++) {
								Tree localTree= (Tree)(colored.elementAt(j));
								//System.out.println("\n" + localTree);
								//localTree.label= "COLORED_" + localTree.label;
								if (roots.containsKey(localTree)) {
									count++;
								}
								if (localTree.isLeaf()) {
									String stick=(String)(stickers.get(localTree.label));
									/*if (!stick.equals(prev) && (trace.containsKey(stick) || prev.equals(" "))) {
										count++;
										trace= new Hashtable();
										trace.put(stick," ");
									} else {
										trace.put(stick," ");
									}
									prev=stick;*/
									out.println(id + ";" + localTree.label + ";" + stick + ";" + count);
								}
							}

						}
						//write.close();
					} catch(Exception e) {
						e.printStackTrace();
					}



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





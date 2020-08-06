package rapgreen;
import java.util.*;
import java.io.*;
import java.util.regex.*;

/**
 * @author Jean-Francois Dufayard
 * @version 1.0
 * Display clusters from a tree
 */
public class TreeStats {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************
/**
* Input tree file
*/
	public static File treeFile;

/**
* Output cluster file
*/
	public static File outputFile;

/**
* Output id file
*/
	public static File ultraFile;

	public static File speciesFile;

	public static int minSeqNumber;
	public static double minSupport;

	public static String key=null;

	public static String rootTaxa;

	public static Vector subspecies;
	public static Vector subspeciesTags;
	public static Hashtable globalTable;

	public static boolean nbleaves=false;
	public static boolean depths=false;
	public static boolean speciesRepresentation=false;
	public static boolean fullRepresentation=false;
	public static boolean allStats=false;
	//public static boolean losses=false;
	public static boolean ultraparalogs=false;
	public static boolean orthologs=false;
	public static boolean aeluropus=false;
	public static boolean banana=false;
	public static boolean duplicationmatrix=false;
	public static boolean monodic=false;


   private static final String NORMAL     = "\u001b[0m";
   private static final String BOLD       = "\u001b[1m";
   private static final String UNDERLINE  = "\u001b[4m";

// ********************************************************************************************************************
// ***     MAIN     ***
// ********************
	public static void main(String[] args) {
		try {
			subspecies= new Vector();
			subspeciesTags= new Vector();
			globalTable= new Hashtable();
			for (int i=0;i<args.length;i=i+2) {
				//System.out.println(i + " " + args[i]);
				if (args[i].contains("help")) {
					System.out.println(BOLD);
					System.out.println("NAME:");
					System.out.println(NORMAL);
					System.out.println("\t- TreeStats v1.0 -");
					System.out.println(BOLD);
					System.out.println("SYNOPSIS:");
					System.out.println(NORMAL);
					System.out.println("\ttreestats [command args]");
					System.out.println(BOLD);
					System.out.println("MANDATORY OPTIONS:");
					System.out.println(BOLD);
					System.out.println("-input" + NORMAL + " " + UNDERLINE + "tree_directory\n\t" + NORMAL + "The input tree directory");
					System.out.println(BOLD);
					System.out.println("-output" + NORMAL + " " + UNDERLINE + "output_csv_file\n\t" + NORMAL + "The output statistic file, in CSV format");
					System.out.println(BOLD);
					System.out.println("REQUEST (you must choose one):");
					System.out.println(BOLD);
					System.out.println("-leaves\n\t" + NORMAL + "count the number of leaves of each tree");
					System.out.println(BOLD);
					System.out.println("-depths\n\t" + NORMAL + "compute average, median and standard deviation of every root to leaf depths in each tree");
					System.out.println(BOLD);
					System.out.println("-representation\n\t" + NORMAL + "count the number of sequences representing each species in each tree");
					System.out.println(BOLD);
					System.out.println("-fullRepresentation" + NORMAL + " "  + UNDERLINE + "species_tree\n\t" + NORMAL + "count the number of sequences representing each species, and each internal taxon, in each tree");
					System.out.println(BOLD);
					System.out.println("-ultraparalogs" + NORMAL + " " + UNDERLINE + "output_ultraparalogs_file" + NORMAL + " " + UNDERLINE + "min_number\n\t" + NORMAL + "provide for each species and each tree the size of the biggest ultraparalog group, and save sequence ids of groups in this file, if it contains the minimum number of genes");
					System.out.println(BOLD);
					System.out.println("-orthologs" + NORMAL + " " + UNDERLINE + "output_orthologs_file" + NORMAL + " " + UNDERLINE + "min_number\n\t" + NORMAL +  "number of orthologous clusters in the output file, and the list of sequence and depth for each cluster in the output orthologs file.");
					System.out.println(BOLD);
					System.out.println("-orthologsClade" + NORMAL + " " + UNDERLINE + "species_tree" + NORMAL + " " + UNDERLINE + "clade_id" + NORMAL + " " + UNDERLINE + "min_number" + NORMAL + " " + UNDERLINE + "min_support\n\t" + NORMAL +  "complete stats for ortholog groups around a specific clade with a minimum number of species under each subclade, and the minimum support to consider clades and duplications.");
					System.out.println(BOLD);
					System.out.println("-allstats" + NORMAL + " "  + UNDERLINE + "species_tree\n\t" + NORMAL + "Count the number of duplications, losses, and ancestral copies for each node of the species tree");
					System.out.println(BOLD);
					System.out.println("-duplicationmatrix\n\t" + NORMAL + "Compute a duplication matrix, regarding a species tree annotated by WGD and WGT. Quite confidential for GenFam Project.");
					System.out.println(BOLD);
					System.out.println("OPTIONS:");
					System.out.println(BOLD);
					System.out.println("-key" + NORMAL + " "  + UNDERLINE + "keyword\n\t" + NORMAL + "Parse only tree files containing this keyword");
					System.out.println(BOLD);
					System.out.println("-subspecies"  + NORMAL + " "  + UNDERLINE + "new_species_code" + NORMAL + " "  + UNDERLINE + "species_code*\n\t" + NORMAL + "Consider a set of species codes as one species with the new species code for the ultraparalog research\n\n");
					System.exit(0);
				} else if (args[i].equalsIgnoreCase("-input")) {
					treeFile= new File(args[i+1]);
				} else if (args[i].equalsIgnoreCase("-output")) {
					outputFile= new File(args[i+1]);
				} else if (args[i].equalsIgnoreCase("-key")) {
					key= args[i+1];
				} else if (args[i].equalsIgnoreCase("-leaves")) {
					nbleaves= true;
					i--;
				}  else if (args[i].equalsIgnoreCase("-depths")) {
					depths= true;
					i--;
				} else if (args[i].equalsIgnoreCase("-representation")) {
					speciesRepresentation= true;
					i--;
				} else if (args[i].equalsIgnoreCase("-fullRepresentation")) {
					fullRepresentation= true;
					speciesFile= new File(args[i+1]);
				} else if (args[i].equalsIgnoreCase("-allStats")) {
					allStats= true;
					speciesFile= new File(args[i+1]);
				} else if (args[i].equalsIgnoreCase("-orthologs")) {
					orthologs= true;
					ultraFile= new File(args[i+1]);
					minSeqNumber= (new Integer(args[i+2])).intValue();
					i+=1;
				} else if (args[i].equalsIgnoreCase("-ultraparalogs")) {
					ultraparalogs= true;
					ultraFile= new File(args[i+1]);
					minSeqNumber= (new Integer(args[i+2])).intValue();
					i+=1;
				} else if (args[i].equalsIgnoreCase("-orthologsOnClade")) {
					monodic= true;
					speciesFile= new File(args[i+1]);
					rootTaxa=args[i+2];
					minSeqNumber= (new Integer(args[i+3])).intValue();
					minSupport= (new Double(args[i+4])).doubleValue();
					i+=3;
				} else if (args[i].equalsIgnoreCase("-aeluropus")) {
					aeluropus= true;
					i--;
				} else if (args[i].equalsIgnoreCase("-banana")) {
					banana= true;
					i--;
				} else if (args[i].equalsIgnoreCase("-duplicationmatrix")) {
					duplicationmatrix= true;
					i--;
				} else if (args[i].equalsIgnoreCase("-subspecies")) {
					Hashtable localSub= new Hashtable();
					i++;
					String tag=args[i];
					subspeciesTags.addElement(tag);
					i++;
					while (i<args.length && !args[i].startsWith("-")) {
						localSub.put(args[i],new Integer(0));
						globalTable.put(args[i],tag);
						i++;
					}
					i--;
					i--;
					subspecies.addElement(localSub);
				}

			}

			if (allStats) {

	        	File[] treeFiles = treeFile.listFiles();

				Vector res= new Vector();
				Vector taxaVector= new Vector();
				Hashtable taxaTable= new Hashtable();
				Vector trees= new Vector();

				TreeReader read= null;
				BufferedReader buf= new BufferedReader(new FileReader(speciesFile));
				String test= buf.readLine();
				buf.close();
				if (test.endsWith(";")) {
					read= new TreeReader(speciesFile,TreeReader.NEWICK);
				} else {
					read= new TreeReader(speciesFile,TreeReader.XML);
				}
				Tree speciesTree= read.nextTree();
				speciesTree.pretreatment();

	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localRes= new Hashtable();
	        		if (treeFiles[i].getName().contains(key)) {
						read= null;
						buf= new BufferedReader(new FileReader(treeFiles[i]));
						test= buf.readLine();
						buf.close();
						if (test.endsWith(";")) {
							read= new TreeReader(treeFiles[i],TreeReader.NEWICK);
						} else {
							read= new TreeReader(treeFiles[i],TreeReader.XML);
						}
						Tree tree= read.nextTree();
						tree.pretreatment();
						trees.addElement(tree);

	        			Vector duplicationsVector= new Vector();
	        			tree.getLosses(duplicationsVector);

	        			System.out.println(treeFiles[i].getName() + " : " + duplicationsVector.size());

	        			for (int j=0;j<duplicationsVector.size();j++) {
	        				Tree g= (Tree)(duplicationsVector.elementAt(j));
							Tree s=g.speciesMapping(speciesTree);

	        				Tree runner= g;
	        				while (runner.label.indexOf("DUPLICATION")==-1) {
	        					runner=runner.father;
	        				}
	        				Vector allSp= runner.speciesVector();
	        				Vector traceSp= runner.traceVector();
	        				if (allSp.size()<=2 || traceSp.size()>=2) {

								if (!taxaTable.containsKey(s.label)) {
									//System.out.println(s.label);
									taxaTable.put(s.label,"1");
									taxaVector.addElement(s.label);
								}

								if (!localRes.containsKey(s.label)) {

									localRes.put(s.label,new Integer(1));

								} else {
									int localInt= (    (Integer)(localRes.get(s.label))     ).intValue();
									localInt++;
									localRes.put(s.label,new Integer(localInt));

								}
							}
	        			}
	        		}
	        		res.addElement(localRes);
	        	}

	        	BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
	       		write.write("Losses");


	       		Vector nodeVector=speciesTree.getNodes();
	       		taxaVector=new Vector();
	        	for (int j=0;j<nodeVector.size();j++) {
	        		Tree localNode= (Tree)(nodeVector.elementAt(j));
	        		taxaVector.addElement(localNode.label);
	        	}


	        	for (int j=0;j<taxaVector.size();j++) {
	        		write.write("\t");
	        		write.write((String)(taxaVector.elementAt(j)));
	        	}
	       		write.flush();
	   			write.write("\n");
	   			write.flush();
	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localRes= (Hashtable)(res.elementAt(i));
	        		if (treeFiles[i].getName().contains(key)) {
	       				write.write(treeFiles[i].getName());
		        		for (int j=0;j<taxaVector.size();j++) {
		        			String taxa= (String)(taxaVector.elementAt(j));
	        				int localInt= 0;
	        				if (localRes.containsKey(taxa)) {
	        					localInt = ((Integer)(localRes.get(taxa))).intValue();
	        				}
	        				write.write("\t");
	        				write.write((new Integer(localInt)).toString());
	   						write.flush();

		        		}
	   					write.write("\n");
	   					write.flush();
	        		}
	        	}

				Vector lossesRes= res;

				res= new Vector();
				taxaVector= new Vector();
				taxaTable= new Hashtable();
				buf= new BufferedReader(new FileReader(speciesFile));
				test= buf.readLine();
				buf.close();
				if (test.endsWith(";")) {
					read= new TreeReader(speciesFile,TreeReader.NEWICK);
				} else {
					read= new TreeReader(speciesFile,TreeReader.XML);
				}
				Tree speciesTree2= read.nextTree();
				speciesTree2.pretreatment();
	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localRes= new Hashtable();
	        		if (treeFiles[i].getName().contains(key)) {
						read= null;
						buf= new BufferedReader(new FileReader(treeFiles[i]));
						test= buf.readLine();
						buf.close();
						if (test.endsWith(";")) {
							read= new TreeReader(treeFiles[i],TreeReader.NEWICK);
						} else {
							read= new TreeReader(treeFiles[i],TreeReader.XML);
						}
						Tree tree= read.nextTree();
						tree.pretreatment();

	        			Vector duplicationsVector= new Vector();
	        			tree.getDuplications(duplicationsVector);

	        			System.out.println(treeFiles[i].getName() + " : " + duplicationsVector.size());

	        			for (int j=0;j<duplicationsVector.size();j++) {
	        				Tree g= (Tree)(duplicationsVector.elementAt(j));
							Tree s=g.speciesMapping(speciesTree2);
	        				Tree runner= g;
	        				Vector allSp= runner.speciesVector();
	        				Vector traceSp= runner.traceVector();
	        				if (allSp.size()<=2 || traceSp.size()>=2) {
								if (!taxaTable.containsKey(s.label)) {
									//System.out.println(s.label);
									taxaTable.put(s.label,"1");
									taxaVector.addElement(s.label);
								}

								if (!localRes.containsKey(s.label)) {

									localRes.put(s.label,new Integer(1));

								} else {
									int localInt= (    (Integer)(localRes.get(s.label))     ).intValue();
									localInt++;
									localRes.put(s.label,new Integer(localInt));

								}
							}
	        			}
	        		}
	        		res.addElement(localRes);
	        	}

	       		write.write("Duplications");
	       		nodeVector=speciesTree2.getNodes();
	       		taxaVector=new Vector();
	        	for (int j=0;j<nodeVector.size();j++) {
	        		Tree localNode= (Tree)(nodeVector.elementAt(j));
	        		taxaVector.addElement(localNode.label);
	        	}
	        	for (int j=0;j<taxaVector.size();j++) {
	        		write.write("\t");
	        		write.write((String)(taxaVector.elementAt(j)));
	        	}
	       		write.flush();
	   			write.write("\n");
	   			write.flush();
	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localRes= (Hashtable)(res.elementAt(i));
	        		if (treeFiles[i].getName().contains(key)) {
	       				write.write(treeFiles[i].getName());
		        		for (int j=0;j<taxaVector.size();j++) {
		        			String taxa= (String)(taxaVector.elementAt(j));
	        				int localInt= 0;
	        				if (localRes.containsKey(taxa)) {
	        					localInt = ((Integer)(localRes.get(taxa))).intValue();
	        				}
	        				write.write("\t");
	        				write.write((new Integer(localInt)).toString());
	   						write.flush();

		        		}
	   					write.write("\n");
	   					write.flush();
	        		}
	        	}


	       		write.write("Ancestral");
	        	for (int j=0;j<taxaVector.size();j++) {
	        		write.write("\t");
	        		write.write((String)(taxaVector.elementAt(j)));
	        	}
	       		write.flush();
	   			write.write("\n");
	   			write.flush();

	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localResLosses= (Hashtable)(lossesRes.elementAt(i));
	        		Hashtable localResDuplications= (Hashtable)(res.elementAt(i));
	        		if (treeFiles[i].getName().contains(key)) {

	       				write.write(treeFiles[i].getName());

						/*for (int j=0;j<taxaVector.size();j++) {
							String taxa= (String)(taxaVector.elementAt(j));
							copies.put(taxa,new Integer(1));
						}	        			*/
	        			Hashtable copies = speciesTree2.countCopies(localResDuplications,localResLosses);
	        			//Hashtable expansion = speciesTree2.computeExpansion(copies);

						for (int j=0;j<taxaVector.size();j++) {
							String taxa= (String)(taxaVector.elementAt(j));
							int localInt= ((Integer)(copies.get(taxa))).intValue();

	   						write.write("\t" + localInt);
						}

	       				write.flush();
	   					write.write("\n");
	   					write.flush();
	        		}
	        	}



	       		write.write("Expansion");
	        	for (int j=0;j<taxaVector.size();j++) {
	        		write.write("\t");
	        		write.write((String)(taxaVector.elementAt(j)));
	        	}
	       		write.flush();
	   			write.write("\n");
	   			write.flush();
				int localRunner=0;
	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localResLosses= (Hashtable)(lossesRes.elementAt(i));
	        		Hashtable localResDuplications= (Hashtable)(res.elementAt(i));
	        		if (treeFiles[i].getName().contains(key)) {
	        		//System.out.println(treeFiles[i].getName());
	        			Tree tree= (Tree)(trees.elementAt(localRunner));
	        			localRunner++;

	        			// find nodes that are not represented in the species tree
	        			Hashtable crossed= new Hashtable();
	        			for (int k=0;k<tree.leafVector.size();k++) {
	        				Tree leaf=(Tree)(tree.leafVector.elementAt(k));
	        				String taxa=leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length());
	        					//System.out.println(taxa);
	        				if (speciesTree2.leafHashtable.containsKey(taxa)) {
	        					Tree runner= (Tree)(speciesTree2.leafHashtable.get(taxa));
	        					//System.out.println("founded " + runner.label);
	        					if (!crossed.containsKey(runner.label)) {
	        						crossed.put(runner.label," ");
	        					}
	        					while (runner.father!=null) {
	        						runner=runner.father;
	        						if (!crossed.containsKey(runner.label)) {
	        							crossed.put(runner.label," ");
	        						}
	        						//System.out.println("jog " + runner.label);
	        					}
	        				}
	        			}
	        			Hashtable resCrossed=new Hashtable();
	        			fillRepresented(speciesTree2,crossed,resCrossed);

	       				write.write(treeFiles[i].getName());

						/*for (int j=0;j<taxaVector.size();j++) {
							String taxa= (String)(taxaVector.elementAt(j));
							copies.put(taxa,new Integer(1));
						}	        			*/
	        			Hashtable copies = speciesTree2.countCopies(localResDuplications,localResLosses);
	        			Hashtable expansion = speciesTree2.computeExpansion(copies,resCrossed);

						for (int j=0;j<taxaVector.size();j++) {
							String taxa= (String)(taxaVector.elementAt(j));
							double localInt= ((Double)(expansion.get(taxa))).doubleValue();

	   						write.write("\t" + localInt);
						}

	       				write.flush();
	   					write.write("\n");
	   					write.flush();
	        		}
	        	}



	        	write.close();

			} else if (duplicationmatrix) {
				BufferedReader buf= new BufferedReader(new FileReader(treeFile));
				String test= buf.readLine();
				buf.close();
				Tree speciesTree= new Tree(test);
				speciesTree.pretreatment();

				Hashtable res= new Hashtable();

				for (int i=0;i<speciesTree.leafVector.size();i++) {

					for (int j=i;j<speciesTree.leafVector.size();j++) {
						Tree leaf1=(Tree)(speciesTree.leafVector.elementAt(i));
						Tree leaf2=(Tree)(speciesTree.leafVector.elementAt(j));
						if (leaf1==leaf2) {
							int nb1=1;
							if (leaf1.nhx!=null) {
								int nbD=TreeStats.stringOccur(leaf1.nhx, "DUP");
								int nbT=TreeStats.stringOccur(leaf1.nhx, "TRI");
								for (int k=0;k<nbD;k++) {
									nb1=nb1*2;
								}
								for (int k=0;k<nbT;k++) {
									nb1=nb1*3;
								}
							}
							res.put(leaf1.label + ":" + leaf1.label,nb1 + ":" + nb1);
						} else {
							Tree lca=speciesTree.lastCommonAncestor(leaf1,leaf2);
							int nb1=1;
							Tree runner=leaf1;
							while (runner!=lca) {
								if (runner.nhx!=null) {
									int nbD=TreeStats.stringOccur(runner.nhx, "DUP");
									int nbT=TreeStats.stringOccur(runner.nhx, "TRI");
									/*if (runner.nhx!=null && runner.nhx.length()>1) {
										System.out.println(TreeStats.stringOccur(runner.nhx, "TRI"));
									}*/
									for (int k=0;k<nbD;k++) {
										nb1=nb1*2;
									}
									for (int k=0;k<nbT;k++) {
										nb1=nb1*3;
									}
								}

								runner=runner.father;
							}
							int nb2=1;
							runner=leaf2;
							while (runner!=lca) {
								if (runner.nhx!=null) {
									int nbD=TreeStats.stringOccur(runner.nhx, "DUP");
									int nbT=TreeStats.stringOccur(runner.nhx, "TRI");
									/*if (runner.nhx!=null && runner.nhx.length()>1) {
										System.out.println(TreeStats.stringOccur(runner.nhx, "TRI"));
									}*/
									for (int k=0;k<nbD;k++) {
										nb2=nb2*2;
									}
									for (int k=0;k<nbT;k++) {
										nb2=nb2*3;
									}
								}

								runner=runner.father;
							}
							//System.out.println(leaf1.label + ":" + leaf2.label + " " + nb1 + ":" + nb2);
							res.put(leaf1.label + ":" + leaf2.label,nb1 + ":" + nb2);
							res.put(leaf2.label + ":" + leaf1.label,nb2 + ":" + nb1);
						}
					}

				}


	        	BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
	        	write.write("species\t" + ((Tree)(speciesTree.leafVector.elementAt(0))).label);
				for (int i=1;i<speciesTree.leafVector.size();i++) {
	        		write.write("\t" + ((Tree)(speciesTree.leafVector.elementAt(i))).label);
				}
				write.write("\n");
				write.flush();
				for (int i=0;i<speciesTree.leafVector.size();i++) {
	        		write.write(((Tree)(speciesTree.leafVector.elementAt(i))).label);
					for (int j=0;j<speciesTree.leafVector.size();j++) {
						Tree leaf1=(Tree)(speciesTree.leafVector.elementAt(i));
						Tree leaf2=(Tree)(speciesTree.leafVector.elementAt(j));
						String localScore=(String)(res.get(leaf1.label + ":" + leaf2.label));
						write.write("\t" + localScore);
					}
					write.write("\n");
					write.flush();
				}

				write.close();



			} else if (monodic) {

	        	File[] treeFiles = treeFile.listFiles();

				Vector res= new Vector();
				Vector taxaVector= new Vector();
				Hashtable taxaTable= new Hashtable();

				TreeReader read= null;
				BufferedReader buf= new BufferedReader(new FileReader(speciesFile));
				String test= buf.readLine();
				buf.close();
				if (test.endsWith(";")) {
					read= new TreeReader(speciesFile,TreeReader.NEWICK);
				} else {
					read= new TreeReader(speciesFile,TreeReader.XML);
				}
				Tree speciesTree= read.nextTree();
				speciesTree.pretreatment();
				int clade=0;
				System.out.println("FILE\tCLADE\tGROUPE\tORTHOLOGS\tSEQUENCE");
	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localRes= new Hashtable();
	        		if (treeFiles[i].getName().contains(key)) {
						System.out.println(treeFiles[i].getName());
						read= null;
						buf= new BufferedReader(new FileReader(treeFiles[i]));
						test= buf.readLine();
						buf.close();
						if (test.endsWith(";")) {
							read= new TreeReader(treeFiles[i],TreeReader.NEWICK);
						} else {
							read= new TreeReader(treeFiles[i],TreeReader.XML);
						}
						Tree tree= read.nextTree();
						tree.pretreatment();

	        			Vector duplicationsVector= new Vector();
	        			tree.getSpeciations(duplicationsVector);

	        			for (int j=0;j<duplicationsVector.size();j++) {
	        				Tree g= (Tree)(duplicationsVector.elementAt(j));
							Tree s=g.speciesMapping(speciesTree);
							if (!s.isLeaf()) {
								Tree s1=(Tree)(s.sons.elementAt(0));
								Tree s2=(Tree)(s.sons.elementAt(1));
								//System.out.println(s.label);
								if (s.label.startsWith(rootTaxa)) {
									//System.out.println(g);
									Tree g1= (Tree)(g.sons.elementAt(0));
									Tree g2= (Tree)(g.sons.elementAt(1));

									int nsp1= g1.nbSpecies().size();
									int nsp2= g2.nbSpecies().size();
									double mainSupport=(new Double(g.label.substring(g.label.lastIndexOf("_")+1,g.label.length()-3))).doubleValue();
									double support1=1.0;
									if (!g1.isLeaf()) {
										support1=(new Double(g1.label.substring(g1.label.lastIndexOf("_")+1,g1.label.length()-3))).doubleValue();
									}
									double support2=1.0;
									if (!g2.isLeaf()) {
									try {
										support2=(new Double(g2.label.substring(g2.label.lastIndexOf("_")+1,g2.label.length()-3))).doubleValue();
									} catch(Exception efee) {
										System.out.println("ECHO:" + g2.label);
										System.exit(0);
									}
									}
									if (nsp1>=minSeqNumber && nsp2>=minSeqNumber && (mainSupport>=minSupport && (support1>=minSupport || support2>=minSupport))) {
										boolean right=true;
										boolean isOk=true;
										for (int k=0;k<g1.leafVector.size() && isOk;k++) {
											Tree leaf=(Tree)(g1.leafVector.elementAt(k));
											String taxa=leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length());
											if (k==0 && s2.leafHashtable.containsKey(taxa)) {
												right=false;
											}
											/*System.out.println(taxa);
											System.out.println(s1);
											System.out.println("***");
											System.out.println(s2);*/
											if (right && !s1.leafHashtable.containsKey(taxa)) {
												isOk=false;
											}
											if (!right && !s2.leafHashtable.containsKey(taxa)) {
												isOk=false;
											}

											//System.out.println(right + " " + isOk);
										}
										/*if (isOk) {
											System.out.println(g2);
											if (right) {
												System.out.println(s2);
											} else {
												System.out.println(s1);
											}
										}*/
										for (int k=0;k<g2.leafVector.size() && isOk;k++) {
											Tree leaf=(Tree)(g2.leafVector.elementAt(k));
											String taxa=leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length());

											if (right && !s2.leafHashtable.containsKey(taxa)) {
												isOk=false;
											}
											if (!right && !s1.leafHashtable.containsKey(taxa)) {
												isOk=false;
											}
										}
											//System.out.println(isOk);
										if (isOk) {
											clade++;
											String sublabel1=null;
											String sublabel2=null;
											System.out.print("\t" + clade);

											Tree runner=g;
											if (g.father!=null) {
												Tree fatherSon=(Tree)(runner.father.sons.elementAt(0));
												if (fatherSon==runner) {
													fatherSon=(Tree)(runner.father.sons.elementAt(1));
												}
												boolean isOut=true;
												String outString="";
												for (int k=0;k<fatherSon.leafVector.size() && isOut;k++) {
													Tree leaf=(Tree)(fatherSon.leafVector.elementAt(k));
													if (leaf.label.endsWith("PHYPA") || leaf.label.endsWith("SELML")) {
														outString=outString + "\t" + leaf.label;
													} else {
														isOut=false;
													}
												}
												if (isOut) {
													System.out.print(outString);
												}
												runner=runner.father;
												if (isOut && runner.father!=null) {
													fatherSon=(Tree)(runner.father.sons.elementAt(0));
													if (fatherSon==runner) {
														fatherSon=(Tree)(runner.father.sons.elementAt(1));
													}
													outString="";
													for (int k=0;k<fatherSon.leafVector.size() && isOut;k++) {
														Tree leaf=(Tree)(fatherSon.leafVector.elementAt(k));
														if (leaf.label.endsWith("PHYPA") || leaf.label.endsWith("SELML")) {
															outString=outString + "\t" + leaf.label;
														} else {
															isOut=false;
														}
													}
													if (isOut) {
														System.out.print(outString);
													}
												}

											}

											System.out.print("\n");

											if (right) {
												System.out.println("\t\t" + s1.label);
												sublabel1=s1.label;
												sublabel2=s2.label;
											} else {
												System.out.println("\t\t" + s2.label);
												sublabel1=s2.label;
												sublabel2=s1.label;
											}

											int nbOrth1=1;
											int nbOrthMin1=1;
											Vector dup1=new Vector();
											g1.getDuplications(dup1);
											for (int k=0;k<dup1.size();k++) {
												Tree node=(Tree)(dup1.elementAt(k));
												if (node.speciesMapping(speciesTree).label.equals(sublabel1)) {
													nbOrth1++;
													double supportNode=1.0;
													if (!node.isLeaf()) {
														supportNode=(new Double(node.label.substring(node.label.lastIndexOf("_")+1,node.label.length()-3))).doubleValue();
													}
													if (supportNode>=minSupport) {
														nbOrthMin1++;
													}
												}
											}
											System.out.println("\t\t\t" + nbOrthMin1 + "\t"  + nbOrth1);

											for (int k=0;k<g1.leafVector.size() && isOk;k++) {
												Tree leaf=(Tree)(g1.leafVector.elementAt(k));
												System.out.println("\t\t\t\t" + leaf.label);
											}
											if (!right) {
												System.out.println("\t\t" + s1.label);
											} else {
												System.out.println("\t\t" + s2.label);
											}

											int nbOrth2=1;
											int nbOrthMin2=1;
											Vector dup2=new Vector();
											g2.getDuplications(dup2);
											for (int k=0;k<dup2.size();k++) {
												Tree node=(Tree)(dup2.elementAt(k));
												if (node.speciesMapping(speciesTree).label.equals(sublabel2)) {
													nbOrth2++;
													double supportNode=1.0;
													if (!node.isLeaf()) {
														supportNode=(new Double(node.label.substring(node.label.lastIndexOf("_")+1,node.label.length()-3))).doubleValue();
													}
													if (supportNode>=minSupport) {
														nbOrthMin2++;
													}

												}
											}
											System.out.println("\t\t\t" + nbOrthMin2 + "\t" + nbOrth2);
											for (int k=0;k<g2.leafVector.size() && isOk;k++) {
												Tree leaf=(Tree)(g2.leafVector.elementAt(k));
												System.out.println("\t\t\t\t" + leaf.label);
											}

										}
									}
								}

	        				}
	        				/*if (!taxaTable.containsKey(s.label)) {
	        					//System.out.println(s.label);
	        					taxaTable.put(s.label,"1");
	        					taxaVector.addElement(s.label);
	        				}

	        				if (!localRes.containsKey(s.label)) {

	        					localRes.put(s.label,new Integer(1));

	        				} else {
	        					int localInt= (    (Integer)(localRes.get(s.label))     ).intValue();
	        					localInt++;
	        					localRes.put(s.label,new Integer(localInt));

	        				}*/
	        			}
	        		}
	        		res.addElement(localRes);
	        	}

	        	BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
	       		write.write("FILE");
	        	for (int j=0;j<taxaVector.size();j++) {
	        		write.write("\t");
	        		write.write((String)(taxaVector.elementAt(j)));
	        	}
	       		write.flush();
	   			write.write("\n");
	   			write.flush();
	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localRes= (Hashtable)(res.elementAt(i));
	        		if (treeFiles[i].getName().contains(key)) {
	       				write.write(treeFiles[i].getName());
		        		for (int j=0;j<taxaVector.size();j++) {
		        			String taxa= (String)(taxaVector.elementAt(j));
	        				int localInt= 0;
	        				if (localRes.containsKey(taxa)) {
	        					localInt = ((Integer)(localRes.get(taxa))).intValue();
	        				}
	        				write.write("\t");
	        				write.write((new Integer(localInt)).toString());
	   						write.flush();

		        		}
	   					write.write("\n");
	   					write.flush();
	        		}
	        	}
	        	write.close();


			} else/* if (duplications) {

	        	File[] treeFiles = treeFile.listFiles();

				Vector res= new Vector();
				Vector taxaVector= new Vector();
				Hashtable taxaTable= new Hashtable();

				TreeReader read= null;
				BufferedReader buf= new BufferedReader(new FileReader(speciesFile));
				String test= buf.readLine();
				buf.close();
				if (test.endsWith(";")) {
					read= new TreeReader(speciesFile,TreeReader.NEWICK);
				} else {
					read= new TreeReader(speciesFile,TreeReader.XML);
				}
				Tree speciesTree= read.nextTree();
				speciesTree.pretreatment();

	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localRes= new Hashtable();
	        		if (treeFiles[i].getName().contains(key)) {
						read= null;
						buf= new BufferedReader(new FileReader(treeFiles[i]));
						test= buf.readLine();
						buf.close();
						if (test.endsWith(";")) {
							read= new TreeReader(treeFiles[i],TreeReader.NEWICK);
						} else {
							read= new TreeReader(treeFiles[i],TreeReader.XML);
						}
						Tree tree= read.nextTree();
						tree.pretreatment();

	        			Vector duplicationsVector= new Vector();
	        			tree.getDuplications(duplicationsVector);

	        			System.out.println(treeFiles[i].getName() + " : " + duplicationsVector.size());

	        			for (int j=0;j<duplicationsVector.size();j++) {
	        				Tree g= (Tree)(duplicationsVector.elementAt(j));
							Tree s=g.speciesMapping(speciesTree);

	        				if (!taxaTable.containsKey(s.label)) {
	        					//System.out.println(s.label);
	        					taxaTable.put(s.label,"1");
	        					taxaVector.addElement(s.label);
	        				}

	        				if (!localRes.containsKey(s.label)) {

	        					localRes.put(s.label,new Integer(1));

	        				} else {
	        					int localInt= (    (Integer)(localRes.get(s.label))     ).intValue();
	        					localInt++;
	        					localRes.put(s.label,new Integer(localInt));

	        				}
	        			}
	        		}
	        		res.addElement(localRes);
	        	}

	        	BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
	       		write.write("FILE");
	        	for (int j=0;j<taxaVector.size();j++) {
	        		write.write("\t");
	        		write.write((String)(taxaVector.elementAt(j)));
	        	}
	       		write.flush();
	   			write.write("\n");
	   			write.flush();
	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localRes= (Hashtable)(res.elementAt(i));
	        		if (treeFiles[i].getName().contains(key)) {
	       				write.write(treeFiles[i].getName());
		        		for (int j=0;j<taxaVector.size();j++) {
		        			String taxa= (String)(taxaVector.elementAt(j));
	        				int localInt= 0;
	        				if (localRes.containsKey(taxa)) {
	        					localInt = ((Integer)(localRes.get(taxa))).intValue();
	        				}
	        				write.write("\t");
	        				write.write((new Integer(localInt)).toString());
	   						write.flush();

		        		}
	   					write.write("\n");
	   					write.flush();
	        		}
	        	}
	        	write.close();


			} else */ if (banana) {
	        	File[] treeFiles = treeFile.listFiles();
	        	BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
	       		write.write("FILE\tRES\tSUP");
	       		write.flush();
	   			write.write("\n");
	   			write.flush();

	        	for (int i=0;i<treeFiles.length;i++) {
	        		if (treeFiles[i].getName().contains(key)) {
						TreeReader read= null;
						BufferedReader buf= new BufferedReader(new FileReader(treeFiles[i]));
						String test= buf.readLine();
						buf.close();
						if (test.endsWith(";")) {
							read= new TreeReader(treeFiles[i],TreeReader.NEWICK);
						} else {
							read= new TreeReader(treeFiles[i],TreeReader.XML);
						}
						Tree tree= read.nextTree();
						tree.pretreatment();
						write.write(treeFiles[i].getName());
						write.write("\t");

						boolean res= false;
						Tree son= (Tree)(tree.sons.elementAt(0));
						//System.out.println(son);
						if (!son.isLeaf()) {
							//System.out.println("1");
							Tree son1= 	(Tree)(son.sons.elementAt(0));
							Tree son2= 	(Tree)(son.sons.elementAt(1));
							if (son1.isLeaf() && son2.isLeaf()) {
								String label1= son1.label.substring(son1.label.lastIndexOf("_")+1,son1.label.length());
								String label2= son2.label.substring(son2.label.lastIndexOf("_")+1,son2.label.length());
								if (label1.equals(label2)) {
									res=true;
								}
							}
						}

						if (res) {
		        			write.write("TRUE\t" + son.label + "\n");
		           			write.flush();
						} else {
		        			write.write("FALSE\t" + son.label + "\n");
		           			write.flush();

						}




	        		}


	        	}
	        	write.close();
			} else if (aeluropus) {
	        	TreeReader read= new TreeReader(treeFile,TreeReader.NEWICK);
	        	Tree tree= read.nextTree();
	        	Tree pattern=new Tree("((Maize,Sorgho)S,Aeluropus)S;");
	        	pattern.pretreatment();
	        	while (tree!=null) {
	        		tree.pretreatment();
	        		tree.addSpeciations();
	        		if (tree.containsPattern(pattern,null,null)) {
	        			//System.out.println("true");
	        			Vector res= new Vector();
	        			Hashtable res2= new Hashtable();
	        			tree.colorPattern(pattern,pattern,null,null,res,res2);
	        			for (int i=0;i<res.size();i++) {
	        				Tree local= (Tree)(res.elementAt(i));
	        				if (local.isLeaf() && local.label.equals("Sorgho")) {
	        					System.out.println("Sorgho\t" + local.length);
	        				}
	        				if (local.isLeaf() && local.label.equals("Maize")) {
	        					System.out.println("Maize\t" + local.length);
	        				}

	        			}
	        		} else {

	        			//System.out.println("false");
	        		}



	        		tree= read.nextTree();
	        	}


			} else if (depths) {
	        	File[] treeFiles = treeFile.listFiles();
	        	BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
	       		write.write("FILE\tNBSEQ\tAVERAGE\tMEDIAN\tDEVIATION");
	       		write.flush();
	   			write.write("\n");
	   			write.flush();

	        	for (int i=0;i<treeFiles.length;i++) {
	        		if (key==null || treeFiles[i].getName().contains(key)) {
						TreeReader read= null;
						BufferedReader buf= new BufferedReader(new FileReader(treeFiles[i]));
						String test= buf.readLine();
						buf.close();
						if (test.endsWith(";")) {
							read= new TreeReader(treeFiles[i],TreeReader.NEWICK);
						} else {
							read= new TreeReader(treeFiles[i],TreeReader.XML);
						}
						Tree tree= read.nextTree();
						tree.pretreatment();
						Vector depthsVect=new Vector();
						tree.getDepths(depthsVect,0.0);
						int nbL= tree.nbLeaves();
						double[] dAr= new double[depthsVect.size()];
						double sum=0.0;
						for (int j=0;j<depthsVect.size();j++) {
							dAr[j]= ((Double)(depthsVect.elementAt(j))).doubleValue();
							sum+=dAr[j];
						}
						Arrays.sort(dAr);

						write.write(treeFiles[i].getName());
						write.write("\t");
		        		write.write(nbL + "\t");
		        		double average=sum/(double)(nbL);
		        		write.write(average + "\t");
		        		double med=0;
		        		if (nbL%2==0) {
		        			med=(dAr[nbL/2]+dAr[nbL/2-1])/2.0;
		        		} else {
		        			med=dAr[nbL/2];
		        		}
		        		write.write(med + "\t");
		        		double stand=0.0;
						for (int j=0;j<dAr.length;j++) {
							if (dAr[j]<average) {
								stand+=average-dAr[j];
							} else {
								stand+=dAr[j]-average;
							}
						}
						stand=stand/(double)nbL;
		        		write.write(stand + "\t");
		           		write.flush();
			   			write.write("\n");
			   			write.flush();

	        		}


	        	}
	        	write.close();
			} else if (nbleaves) {
	        	File[] treeFiles = treeFile.listFiles();
	        	BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
	       		write.write("NBLEAVES\t");
	       		write.flush();
	   			write.write("\n");
	   			write.flush();

	        	for (int i=0;i<treeFiles.length;i++) {
	        		if (treeFiles[i].getName().contains(key)) {
						TreeReader read= null;
						BufferedReader buf= new BufferedReader(new FileReader(treeFiles[i]));
						String test= buf.readLine();
						buf.close();
						if (test.endsWith(";")) {
							read= new TreeReader(treeFiles[i],TreeReader.NEWICK);
						} else {
							read= new TreeReader(treeFiles[i],TreeReader.XML);
						}
						Tree tree= read.nextTree();
						tree.pretreatment();
						write.write(treeFiles[i].getName());
						write.write("\t");
		        		write.write(tree.nbLeaves() + "\t");
		           		write.flush();
			   			write.write("\n");
			   			write.flush();

	        		}


	        	}
	        	write.close();
			} else if (fullRepresentation) {


	        	File[] treeFiles = treeFile.listFiles();

				Vector res= new Vector();
				Vector taxaVector= new Vector();
				Hashtable taxaTable= new Hashtable();

				TreeReader read= null;
				BufferedReader buf= new BufferedReader(new FileReader(speciesFile));
				String test= buf.readLine();
				buf.close();
				if (test.endsWith(";")) {
					read= new TreeReader(speciesFile,TreeReader.NEWICK);
				} else {
					read= new TreeReader(speciesFile,TreeReader.XML);
				}
				Tree speciesTree= read.nextTree();
				speciesTree.pretreatment();

	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localRes= new Hashtable();
	        		if (treeFiles[i].getName().contains(key)) {
						read= null;
						buf= new BufferedReader(new FileReader(treeFiles[i]));
						test= buf.readLine();
						buf.close();
						if (test.endsWith(";")) {
							read= new TreeReader(treeFiles[i],TreeReader.NEWICK);
						} else {
							read= new TreeReader(treeFiles[i],TreeReader.XML);
						}
						Tree tree= read.nextTree();
						tree.pretreatment();

	        			Vector speciationsVector= new Vector();
	        			tree.getSpeciations(speciationsVector);

	        			System.out.println(treeFiles[i].getName() + " : " + speciationsVector.size());

	        			for (int j=0;j<speciationsVector.size();j++) {
	        				Tree g= (Tree)(speciationsVector.elementAt(j));
							Tree s=g.speciesMapping(speciesTree);

	        				if (!taxaTable.containsKey(s.label)) {
	        					//System.out.println(s.label);
	        					taxaTable.put(s.label,"1");
	        					taxaVector.addElement(s.label);
	        				}

	        				if (!localRes.containsKey(s.label)) {
	        					localRes.put(s.label,new Integer(1));
	        				} else {
	        					int localInt= (    (Integer)(localRes.get(s.label))     ).intValue();
	        					localInt++;
	        					localRes.put(s.label,new Integer(localInt));

	        				}
	        			}
	        		}
	        		res.addElement(localRes);
	        	}

	        	BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
	       		write.write("FILE");
	        	for (int j=0;j<taxaVector.size();j++) {
	        		write.write("\t");
	        		write.write((String)(taxaVector.elementAt(j)));
	        	}
	       		write.flush();
	   			write.write("\n");
	   			write.flush();
	        	for (int i=0;i<treeFiles.length;i++) {
	        		Hashtable localRes= (Hashtable)(res.elementAt(i));
	        		if (treeFiles[i].getName().contains(key)) {
	       				write.write(treeFiles[i].getName());
		        		for (int j=0;j<taxaVector.size();j++) {
		        			String taxa= (String)(taxaVector.elementAt(j));
	        				int localInt= 0;
	        				if (localRes.containsKey(taxa)) {
	        					localInt = ((Integer)(localRes.get(taxa))).intValue();
	        				}
	        				write.write("\t");
	        				write.write((new Integer(localInt)).toString());
	   						write.flush();

		        		}
	   					write.write("\n");
	   					write.flush();
	        		}
	        	}
	        	write.close();




			} else if (speciesRepresentation) {
				Hashtable speciesTable= new Hashtable();
				Vector speciesVector= new Vector();

				Vector resTables= new Vector();
				Vector resTables2= new Vector();
				Vector fileVector= new Vector();

	        	File[] treeFiles = treeFile.listFiles();

	        	for (int i=0;i<treeFiles.length;i++) {
	        		if (treeFiles[i].getName().contains(key)) {
	        			Hashtable localRes= new Hashtable();
	        			Hashtable localRes2= new Hashtable();
						TreeReader read= null;
						BufferedReader buf= new BufferedReader(new FileReader(treeFiles[i]));
						String test= buf.readLine();
						buf.close();
						if (test.endsWith(";")) {
							read= new TreeReader(treeFiles[i],TreeReader.NEWICK);
						} else {
							read= new TreeReader(treeFiles[i],TreeReader.XML);
						}
						Tree tree= read.nextTree();
						tree.pretreatment();

						for (int j=0;j<tree.leafVector.size();j++) {
							Tree leaf= (Tree)(tree.leafVector.elementAt(j));
							String tax= leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length());
							if (!speciesTable.containsKey(tax)) {
								speciesTable.put(tax,"1");
								speciesVector.addElement(tax);
							}
							if (!localRes.containsKey(tax)) {
								localRes.put(tax,new Integer(1));
							} else {
								int val= ((Integer)(localRes.get(tax))).intValue();
								localRes.put(tax,new Integer(val+1));
							}

						}
						resTables.addElement(localRes);
						tree.ultraParalogy();
						tree.getNbUltraparalogGroups(localRes2);
						resTables2.addElement(localRes2);
						fileVector.addElement(treeFiles[i].getName());

	        		}


	        	}
	        	BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
				write.write("FILE\t");
				for (int j=0;j<speciesVector.size();j++) {
					String localSp= (String)(speciesVector.elementAt(j));
					write.write(localSp);
					write.write("\t");
				}


	   			write.write("\n");
	   			write.flush();
	   			for (int i=0;i<fileVector.size();i++) {
	   				String fileName= (String)(fileVector.elementAt(i));
	   				write.write(fileName + "\t");
	   				Hashtable localTable= (Hashtable)(resTables.elementAt(i));
	   				Hashtable localTable2= (Hashtable)(resTables2.elementAt(i));
					for (int j=0;j<speciesVector.size();j++) {
						String localSp= (String)(speciesVector.elementAt(j));
						int localCount=0;
						int localUl=0;
						if (localTable.containsKey(localSp)) {
							localCount=((Integer)(localTable.get(localSp))).intValue();
							localUl=((Integer)(localTable2.get(localSp))).intValue();
						}

				        write.write(/*localUl + ";" + */localCount + "\t");
					}
					write.write("\n");
					write.flush();

	   			}


	        	write.close();


			} else if (orthologs) {


	        	File[] treeFiles = treeFile.listFiles();
	        	BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
	        	BufferedWriter write2= new BufferedWriter(new FileWriter(ultraFile));

	        	for (int i=0;i<treeFiles.length;i++) {
	        		if (treeFiles[i].getName().contains(key)) {
	        			Vector allRes= new Vector();
	        			Vector allDepth= new Vector();
						TreeReader read= null;
						BufferedReader buf= new BufferedReader(new FileReader(treeFiles[i]));
						String test= buf.readLine();
						buf.close();
						if (test.endsWith(";")) {
							read= new TreeReader(treeFiles[i],TreeReader.NEWICK);
						} else {
							read= new TreeReader(treeFiles[i],TreeReader.XML);
						}
						Tree tree= read.nextTree();
						tree.pretreatment();
						tree.ultraParalogy();

						tree.fillOrthologs(allRes,allDepth,new Vector());
						write.write(treeFiles[i].getName() + "\t" + (allRes.size()));

						for (int j=0;j<allRes.size();j++) {
							Vector localRes=(Vector)(allRes.elementAt(j));
							if (minSeqNumber<=localRes.size()) {
								write2.write(treeFiles[i].getName() + "\t" + (j+1));
								Vector localDepth=(Vector)(allDepth.elementAt(j));
								write.write("\t" + (localRes.size()));
								for (int k=0;k<localRes.size();k++) {
									write2.write("\t" + ((Tree)(localRes.elementAt(k))).label + "\t" + ((Double)(localDepth.elementAt(k))).doubleValue());

								}


								write2.write("\n");
								write2.flush();
							}
						}
						write.write("\n");
						write.flush();

	        		}


	        	}

	        	write.close();


	        	write2.close();



			} else if (ultraparalogs) {
				//System.out.println(key);
				Hashtable speciesTable= new Hashtable();
				Vector speciesVector= new Vector();

				Vector resTables= new Vector();
				Vector idsTables= new Vector();
				Vector fileVector= new Vector();

	        	File[] treeFiles = treeFile.listFiles();

	        	for (int i=0;i<treeFiles.length;i++) {
	        		if (treeFiles[i].getName().contains(key)) {
	        			Hashtable localRes= new Hashtable();
	        			Vector localIds= new Vector();
						TreeReader read= null;
						BufferedReader buf= new BufferedReader(new FileReader(treeFiles[i]));
						String test= buf.readLine();
						buf.close();
						if (test.endsWith(";")) {
							read= new TreeReader(treeFiles[i],TreeReader.NEWICK);
						} else {
							read= new TreeReader(treeFiles[i],TreeReader.XML);
						}
						Tree tree= read.nextTree();
						tree.pretreatment();






						for (int j=0;j<tree.leafVector.size();j++) {
							Tree leaf= (Tree)(tree.leafVector.elementAt(j));
							String tax= leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length());
							if (globalTable.containsKey(tax)) {
								String subtax= (String)(globalTable.get(tax));
								if (!speciesTable.containsKey(subtax)) {
									speciesTable.put(subtax,"1");
									speciesVector.addElement(subtax);
								}
							} else if (!speciesTable.containsKey(tax)) {
								speciesTable.put(tax,"1");
								speciesVector.addElement(tax);
							}
						}

						tree.ultraParalogy();
						tree.fillUltraParalogs(localRes, localIds, minSeqNumber,subspecies,subspeciesTags,globalTable);


						idsTables.addElement(localIds);
						resTables.addElement(localRes);
						fileVector.addElement(treeFiles[i].getName());


	        		}


	        	}
	        	BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
	        	BufferedWriter write2= new BufferedWriter(new FileWriter(ultraFile));
				write.write("FILE\t");
				for (int j=0;j<speciesVector.size();j++) {
					String localSp= (String)(speciesVector.elementAt(j));
					write.write(localSp);
					write.write("\t");
				}


	   			write.write("\n");
	   			write.flush();
	   			for (int i=0;i<fileVector.size();i++) {
	   				String fileName= (String)(fileVector.elementAt(i));
	   				write.write(fileName + "\t");
	   				Hashtable localTable= (Hashtable)(resTables.elementAt(i));
					for (int j=0;j<speciesVector.size();j++) {
						String localSp= (String)(speciesVector.elementAt(j));
						int localCount=0;
						if (localTable.containsKey(localSp)) {
							localCount=((Integer)(localTable.get(localSp))).intValue();
						}

				        write.write(localCount + "\t");
					}
					write.write("\n");
					write.flush();

	   			}
	        	write.close();

				write2.write("FILE\tNUM\tID_LISTS\n");
				write2.flush();
	   			for (int i=0;i<fileVector.size();i++) {
	   				String fileName= (String)(fileVector.elementAt(i));
	   				Vector localTable= (Vector)(idsTables.elementAt(i));
					int maxVectSize=0;
					Vector maxVect=null;
					for (int j=0;j<localTable.size();j++) {
						Vector localVect=((Vector)(localTable.elementAt(j)));
						write2.write(fileName + "\t" + (j+1) +"\t");
						write2.flush();
						for (int k=0;k<localVect.size();k++) {
							if (k!=0) {
								write2.write("\t");
								write2.flush();
							}
							write2.write((String)(localVect.elementAt(k)));
							write2.flush();

						}
						write2.write("\n");
						write2.flush();

					}
	   			}

	        	write2.close();



			}



		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Usage for standard tree stats:\n");
		}
	}


	private static void fillRepresented(Tree tree,Hashtable ref,Hashtable table) {
		if (tree.isLeaf()) {
			if (!ref.containsKey(tree.label)) {
				table.put(tree.label," ");
				//System.out.println("ex:" + tree.label);
			}
		} else {
			int count=0;
			int jog=-1;
			for (int i=0;i<tree.sons.size();i++) {
				Tree son=(Tree)(tree.sons.elementAt(i));
				if (ref.containsKey(son.label)) {
					count++;
					jog=i;
				}

			}
			if (count==1) {
				table.put(tree.label," ");
				//System.out.println("ex:" + tree.label);
				fillRepresented((Tree)(tree.sons.elementAt(jog)),ref,table);
			}
			//System.out.println(tree.label + " " + count);
		}
	}

	/**
	 * Renvoie le nombre d'occurrences de la sous-chaine de caracteres specifiee dans la chaine de caracteres specifiee
	 * @param text chaine de caracteres initiale
	 * @param string sous-chaine de caracteres dont le nombre d'occurrences doit etre compte
	 * @return le nombre d'occurrences du pattern specifie dans la chaine de caracteres specifiee
	 */
	 public static final int stringOccur(String text, String string) {
		return regexOccur(text, Pattern.quote(string));
	}

	 /**
	 * Renvoie le nombre d'occurrences du pattern specifie dans la chaine de caracteres specifiee
	 * @param text chaine de caracteres initiale
	 * @param regex expression reguliere dont le nombre d'occurrences doit etre compte
	 * @return le nombre d'occurrences du pattern specifie dans la chaine de caracteres specifiee
	 */
	 public static final int regexOccur(String text, String regex) {
		Matcher matcher = Pattern.compile(regex).matcher(text);
		int occur = 0;
		while(matcher.find()) {
			occur ++;
		}
		return occur;
	}


}

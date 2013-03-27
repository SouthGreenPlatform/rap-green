package rapgreen;
import java.util.*;
import java.io.*;

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

	public static String key;

	public static Vector subspecies;
	public static Vector subspeciesTags;
	public static Hashtable globalTable;
	
	public static boolean nbleaves=false;
	public static boolean speciesRepresentation=false;
	public static boolean duplications=false;
	public static boolean losses=false;
	public static boolean ultraparalogs=false;
	public static boolean orthologs=false;
	public static boolean aeluropus=false;
	public static boolean banana=false;


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
					System.out.println("-representation\n\t" + NORMAL + "count the number of sequences representing each species in each tree");
					System.out.println(BOLD);
					System.out.println("-ultraparalogs" + NORMAL + " " + UNDERLINE + "output_ultraparalogs_file" + NORMAL + " " + UNDERLINE + "min_number\n\t" + NORMAL + "provide for each species and each tree the size of the biggest ultraparalog group, and save sequence ids of groups in this file, if it contains the minimum number of genes");					
					System.out.println(BOLD);
					System.out.println("-orthologs" + NORMAL + " " + UNDERLINE + "output_orthologs_file" + NORMAL + " " + UNDERLINE + "min_number\n\t" + NORMAL +  "number of orthologous clusters in the output file, and the list of sequence and depth for each cluster in the output orthologs file.");					
					System.out.println(BOLD);
					System.out.println("-duplications" + NORMAL + " "  + UNDERLINE + "species_tree\n\t" + NORMAL + "Count the number of duplication for each node of the species tree");					
					System.out.println(BOLD);
					System.out.println("-losses" + NORMAL + " "  + UNDERLINE + "species_tree\n\t" + NORMAL + "Count the number of losses for each node of the species tree\n");
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
				} else if (args[i].equalsIgnoreCase("-representation")) {
					speciesRepresentation= true;
					i--;
				} else if (args[i].equalsIgnoreCase("-losses")) {
					losses= true;
					speciesFile= new File(args[i+1]);
				} else if (args[i].equalsIgnoreCase("-duplications")) {
					duplications= true;
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
				} else if (args[i].equalsIgnoreCase("-aeluropus")) {
					aeluropus= true;
					i--;
				} else if (args[i].equalsIgnoreCase("-banana")) {
					banana= true;
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


			if (losses) {
			
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
	        			tree.getLosses(duplicationsVector);
	        			
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
				
				
			} else 			if (duplications) {
			
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
				
				
			} else if (banana) {
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
	        			tree.colorPattern(pattern,pattern,null,null,res);
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

				        write.write(localUl + ";" + localCount + "\t");
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







}
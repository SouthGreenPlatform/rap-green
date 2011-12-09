package rapgreen;
import java.util.*;
import java.io.*;

/**
 * Entry point of RapGreen program
 * @author Jean-Francois Dufayard
 * @version 1.0
 */
public class RapGreen {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************
/**
* Trace
*/
	public static boolean debug=false;

/**
* Input gene files
*/
	public static File[] geneFiles;

/**
* Input species file
*/
	public static File speciesFile;

/**
* Output gene file
*/
	public static File outputGene=null;

/**
* Output gene file
*/
	public static File outputSpecies=null;

/**
* Output gene file
*/
	public static File outputReconciled=null;

/**
* Output phyloXML gene file
*/
	public static File outputPhyloXML=null;

/**
* Output rerooted simple gene file
*/
	public static File outputRerooted=null;	

/**
* Output stats file
*/
	public static File stats=null;

   private static final String NORMAL     = "\u001b[0m";
   private static final String BOLD       = "\u001b[1m";
   private static final String UNDERLINE  = "\u001b[4m";

/**
* Verbose mod
*/
	public static boolean verbose=false;

/**
* Tax id inversion mod
*/
	public static boolean invert=false;
	

/**
* Starting point of the input gene tree directory 
*/
	public static int start=0;	
	
/**
* Ending point (exclusive) of the input gene tree directory 
*/
	public static int end=-1;	
	
	public static boolean addOutparalogous=false;	
	
// ********************************************************************************************************************
// ***     MAIN     ***
// ********************
	public static void main(String[] args) {
		try {
			for (int i=0;i<args.length;i=i+2) {
				if (args[i].contains("help")) {
					System.out.println(BOLD);
					System.out.println("NAME:");
					System.out.println(NORMAL);
					System.out.println("\t- RAP-Green v1.0 -");
					System.out.println(BOLD);
					System.out.println("SYNOPSIS:");
					System.out.println(NORMAL);
					System.out.println("\trapgreen [command args]");
					System.out.println(BOLD);
					System.out.println("OPTIONS:");
					System.out.println(BOLD);
					System.out.println("-g" + NORMAL + " " + UNDERLINE + "gene_tree_file\n\t" + NORMAL + "The input gene tree file");
					System.out.println(BOLD);
					System.out.println("-invert\n\t" + NORMAL + "Activate this option if your taxa identifier is in front of the sequence identifier");
					System.out.println(BOLD);
					System.out.println("-start" + NORMAL + " "  + UNDERLINE + "starting_index\n\t" + NORMAL + "The starting index (0 default), if the gene tree input is a directory");
					System.out.println(BOLD);
					System.out.println("-end" + NORMAL + " "  + UNDERLINE + "ending_index\n\t" + NORMAL + "The ending exclusive index (directory size default), if the gene tree input is a directory");
					System.out.println(BOLD);
					System.out.println("-s" + NORMAL + " "  + UNDERLINE + "species_tree_file\n\t" + NORMAL + "The input species tree file");
					System.out.println(BOLD);
					System.out.println("-og" + NORMAL + " "  + UNDERLINE + "gene_tree_file\n\t" + NORMAL + "The output tree file (annotated with duplications)");
					System.out.println(BOLD);
					System.out.println("-rerooted" + NORMAL + " "  + UNDERLINE + "gene_tree_file\n\t" + NORMAL + "The simple unannotated rerooted gene tree file");
					System.out.println(BOLD);					
					System.out.println("-phyloxml" + NORMAL + " "  + UNDERLINE + "gene_tree_phyloxml_file\n\t" + NORMAL + "The output tree file (annotated with duplications) in phyloXML format");
					System.out.println(BOLD);
					System.out.println("-os" + NORMAL + " "  + UNDERLINE + "species_tree_file\n\t" + NORMAL + "The output species tree file (limited to gene tree species)");
					System.out.println(BOLD);
					System.out.println("-or" + NORMAL + " "  + UNDERLINE + "reconciled_tree_file\n\t" + NORMAL + "The output reconciled tree file (consensus tree, with reductions and losses)");
					System.out.println(BOLD);
					System.out.println("-stats" + NORMAL + " "  + UNDERLINE + "gene_tree_file\n\t" + NORMAL + "The output scoring statistic file");
					System.out.println(BOLD);
					System.out.println("-outparalogous\n\t" + NORMAL + "Add outparalogous informations in stats file.");
					System.out.println(BOLD);
					System.out.println("-gt" + NORMAL + " " + UNDERLINE + "gene_threshold\n\t" + NORMAL + "The support threshold for gene tree branch collapse (optional, default 80.0)");
					System.out.println(BOLD);
					System.out.println("-st" + NORMAL + " "  + UNDERLINE + "species_threshold\n\t" + NORMAL + "The length threshold for species tree branch collapse (optional, default 10.0)");
					System.out.println(BOLD);
					System.out.println("-pt" + NORMAL + " "  + UNDERLINE + "polymorphism_threshold\n\t" + NORMAL + "The length depth threshold to deduce to polymorphism, allelism ... (optional, default 0.05)");
					System.out.println(BOLD);
					System.out.println("-k" + NORMAL + " "  + UNDERLINE + "k_level\n\t" + NORMAL + "The k-level of the subtree-neighbor measure (optional, default 2)");
					System.out.println(BOLD);
					System.out.println("-idupw" + NORMAL + " "  + UNDERLINE + "i_duplication_weight\n\t" + NORMAL + "The weight of intersection duplication in functional orthology scoring (0.0 for maximum weight, 1.0 for no weight, optional, default 0.90)");
					System.out.println(BOLD);
					System.out.println("-tdupw" + NORMAL + " "  + UNDERLINE + "t_duplication_weight\n\t" + NORMAL + "The weight of topological duplication in functional orthology scoring (0.0 for maximum weight, 1.0 for no weight, optional, default 0.95)");
					System.out.println(BOLD);
					System.out.println("-specw" + NORMAL + " "  + UNDERLINE + "speciation_weight\n\t" + NORMAL + "The weight of speciation in functional orthology scoring (0.0 for maximum weight, 1.0 for no weight, optional, default 0.99)");
					System.out.println(BOLD);
					System.out.println("-ultraw" + NORMAL + " "  + UNDERLINE + "ultraparalogy_weight\n\t" + NORMAL + "The weight of an ultraparalogy node in functional orthology scoring (0.0 for maximum weight, 1.0 for no weight, optional, default 0.99)");					
					System.out.println(BOLD);
					System.out.println("-distw" + NORMAL + " "  + UNDERLINE + "distance_weight\n\t" + NORMAL + "The weight of evolutionary distance in functional orthology scoring (0.0 for maximum weight, 1.0 for no weight, optional, default 0.10)\n\n");
					System.exit(0);
				}

				if (args[i].equalsIgnoreCase("-g")) {
					if ((new File(args[i+1])).isDirectory()) {
						geneFiles = (new File(args[i+1])).listFiles();
					} else {
						geneFiles= new File[1];
						geneFiles[0]= new File(args[i+1]);
					}
				}
				if (args[i].equalsIgnoreCase("-start")) {
					start= (new Integer(args[i+1])).intValue();
				}
				if (args[i].equalsIgnoreCase("-end")) {
					end= (new Integer(args[i+1])).intValue();
				}
				if (args[i].equalsIgnoreCase("-invert")) {
					invert=true;
					i--;
				}
				if (args[i].equalsIgnoreCase("-verbose")) {
					verbose=true;
					i--;
				}
				if (args[i].equalsIgnoreCase("-s")) {
					speciesFile= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-og")) {
					outputGene= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-rerooted")) {
					outputRerooted= new File(args[i+1]);

				}
				if (args[i].equalsIgnoreCase("-phyloxml")) {
					outputPhyloXML= new File(args[i+1]);

				}
				if (args[i].equalsIgnoreCase("-k")) {
					TreeReconciler.kLevel= (new Integer(args[i+1])).intValue();
				}
				if (args[i].equalsIgnoreCase("-os")) {
					outputSpecies= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-or")) {
					outputReconciled= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-stats")) {
					stats= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-outparalogous")) {
					addOutparalogous=true;
					i--;
				}
				if (args[i].equalsIgnoreCase("-idupw")) {
					TreeScoring.iDupWeight=  (new Double(args[i+1])).doubleValue();
				}
				if (args[i].equalsIgnoreCase("-tdupw")) {
					TreeScoring.tDupWeight=  (new Double(args[i+1])).doubleValue();
				}
				if (args[i].equalsIgnoreCase("-ultraw")) {
					TreeScoring.uParaWeight=  (new Double(args[i+1])).doubleValue();
				}				
				if (args[i].equalsIgnoreCase("-specw")) {
					TreeScoring.specWeight=  (new Double(args[i+1])).doubleValue();
				}
				if (args[i].equalsIgnoreCase("-distw")) {
					TreeScoring.lengthWeight=  (new Double(args[i+1])).doubleValue();
				}
				if (args[i].equalsIgnoreCase("-gt")) {
					TreeReconciler.geneCollapseThreshold= (new Double(args[i+1])).doubleValue();
				}
				if (args[i].equalsIgnoreCase("-st")) {
					TreeReconciler.speciesCollapseThreshold= (new Double(args[i+1])).doubleValue();
				}
				if (args[i].equalsIgnoreCase("-pt")) {
					TreeReconciler.geneDepthThreshold= (new Double(args[i+1])).doubleValue();
				}
			}
			if (end==-1) {
				end = geneFiles.length;
			}
			for (int i=start;i<end;i++) {
				try {
					TreeReader read= new TreeReader(geneFiles[i],TreeReader.NEWICK);
					if (geneFiles.length>1) {
						System.out.println(geneFiles[i].getName() + " " + i + "/" + geneFiles.length);	
					}
					Tree geneTree= read.nextTree();
					
					if (invert) {
						geneTree.pretreatment();
						Vector vect= geneTree.leafVector;
						for (int x=0;x<vect.size();x++) {
							Tree leaf= (Tree)(vect.elementAt(x));	
							leaf.label=leaf.label.substring(leaf.label.indexOf("_")+1,leaf.label.length()) + "_" + leaf.label.substring(0,leaf.label.indexOf("_"));
						}
						
					}
	
					SpeciesDictionary dico= new SpeciesDictionary();
	
					String test= (new BufferedReader(new FileReader(speciesFile))).readLine();
					if (test.endsWith(";")) {
						read= new TreeReader(speciesFile,dico,TreeReader.NEWICK);
					} else {
						read= new TreeReader(speciesFile,dico,TreeReader.XML);
					}
					Tree speciesTree= read.nextTree();
					//System.out.println(speciesTree);
					TreeReconciler reconciler= new TreeReconciler(geneTree,speciesTree);
	
					if (outputRerooted!=null) {
						Tree copyCat= new Tree(reconciler.geneTree);
						if (invert) {
							copyCat.pretreatment();
							Vector vect= copyCat.leafVector;
							for (int x=0;x<vect.size();x++) {
								Tree leaf= (Tree)(vect.elementAt(x));	
								leaf.label=leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length()) + "_" + leaf.label.substring(0,leaf.label.lastIndexOf("_"));
							}
							
						}					
						// trying a masse reconciler
						if (geneFiles.length==1) {
							TreeWriter geneWriter= new TreeWriter(copyCat);
							geneWriter.writeSimpleTree(outputRerooted);
						} else {
							TreeWriter geneWriter= new TreeWriter(copyCat);
							geneWriter.writeSimpleTree(new File(outputRerooted.getPath().replace("INPUT",geneFiles[i].getName())));						
						}
					}				
	
					if (outputGene!=null) {
						Tree copyCat= new Tree(reconciler.geneTree);
						copyCat.pretreatment();
						copyCat.formatLabelsWithTrace();
						TreeWriter geneWriter= new TreeWriter(copyCat);
						geneWriter.writeTree(outputGene);
					}
					Tree copyCat=null;
					if (outputPhyloXML!=null) {
						copyCat= new Tree(reconciler.geneTree);
						copyCat.pretreatment();
	
					}
					if (outputSpecies!=null) {
						BufferedWriter xmlWrite= new BufferedWriter(new FileWriter(outputSpecies));
						xmlWrite.write(reconciler.speciesTree.toPhyloXMLString(dico) + "\n");
						xmlWrite.flush();
						xmlWrite.close();
					}
					if (outputReconciled!=null) {
						TreeWriter geneWriter= new TreeWriter(reconciler.reconciledTree);
						geneWriter.writeTree(outputReconciled);
					}
	
					//System.out.println(reconciler.speciesTree.getNewick() + "\n" + reconciler.geneTree.getNewick() + "\n" + reconciler.reconciledTree.getNewick());
	
					TreeScoring score= new TreeScoring(reconciler.geneTree);
					if (stats!=null) {
						score.writeScores(stats);
					}
	
					if (outputPhyloXML!=null) {
						BufferedWriter xmlWrite= new BufferedWriter(new FileWriter(outputPhyloXML));
						xmlWrite.write(copyCat.toPhyloXMLString(score.phyloXMLBuffer) + "\n");
						xmlWrite.flush();
						xmlWrite.close();
					}
					} catch(Exception e) {
						System.out.println("Error in this dataset.");
						e.printStackTrace();
						
					}	
				}
	

		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
	}







}
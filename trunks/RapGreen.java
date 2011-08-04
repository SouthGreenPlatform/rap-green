package rapgreen;
import java.util.*;
import java.io.*;

/**
 * NAME:
 * <p>
 * 		- RAP-Green v1.0 -
 * <p>
 * SYNOPSIS:
 * <p>
 * 		rapgreen [command args]
 * <p>
 * OPTIONS:
 * <p>
 * 		-g gene_tree_file :
 *
 * 			The input gene tree file
 * <p>
 * 		-s species_tree_file :
 *
 * 			The input species tree file
 * <p>
 * 		-og gene_tree_file :
 *
 * 			The output tree file (annotated with duplications)
* <p>
* 		-phyloxml gene_tree_phyloxml_file :
*
 * 			The output tree phyloXML file (annotated with duplications)
 * <p>
 * 		-os species_tree_file :
 *
 * 			The output species tree file (limited to gene tree species)
 * <p>
 * 		-or reconciled_tree_file :
 *
 * 			The output reconciled tree file (consensus tree, with reductions and losses)
 * <p>
 * 		-stats gene_tree_file :
 *
 * 			The output scoring statistic file
 * <p>
 * 		-gt gene_threshold :
 *
 * 			The support threshold for gene tree branch collapse (optional, default 80.0)
 * <p>
 * 		-st species_threshold :
 *
 * 			The length threshold for species tree branch collapse (optional, default 10.0)
 * <p>
 * 		-k k-level :
 *
 * 			The k-level of the subtree-neighbor measure (optional, default 2)
 * <p>
 * 		-pt polymorphism_threshold :
 *
 * 			The length depth threshold to deduce to polymorphism, allelism ... (optional, default 0.05)
 * <p>
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
					System.out.println("-s" + NORMAL + " "  + UNDERLINE + "species_tree_file\n\t" + NORMAL + "The input species tree file");
					System.out.println(BOLD);
					System.out.println("-og" + NORMAL + " "  + UNDERLINE + "gene_tree_file\n\t" + NORMAL + "The output tree file (annotated with duplications)");
					System.out.println(BOLD);
					System.out.println("-phyloxml" + NORMAL + " "  + UNDERLINE + "gene_tree_phyloxml_file\n\t" + NORMAL + "The output tree file (annotated with duplications) in phyloXML format");
					System.out.println(BOLD);
					System.out.println("-os" + NORMAL + " "  + UNDERLINE + "species_tree_file\n\t" + NORMAL + "The output species tree file (limited to gene tree species)");
					System.out.println(BOLD);
					System.out.println("-or" + NORMAL + " "  + UNDERLINE + "reconciled_tree_file\n\t" + NORMAL + "The output reconciled tree file (consensus tree, with reductions and losses)");
					System.out.println(BOLD);
					System.out.println("-stats" + NORMAL + " "  + UNDERLINE + "gene_tree_file\n\t" + NORMAL + "The output scoring statistic file");
					System.out.println(BOLD);
					System.out.println("-gt" + NORMAL + " " + UNDERLINE + "gene_threshold\n\t" + NORMAL + "The support threshold for gene tree branch collapse (optional, default 80.0)");
					System.out.println(BOLD);
					System.out.println("-st" + NORMAL + " "  + UNDERLINE + "species_threshold\n\t" + NORMAL + "The length threshold for species tree branch collapse (optional, default 10.0)");
					System.out.println(BOLD);
					System.out.println("-pt" + NORMAL + " "  + UNDERLINE + "polymorphism_threshold\n\t" + NORMAL + "The length depth threshold to deduce to polymorphism, allelism ... (optional, default 0.05)");
					System.out.println(BOLD);
					System.out.println("-k" + NORMAL + " "  + UNDERLINE + "k_level\n\t" + NORMAL + "The k-level of the subtree-neighbor measure (optional, default 2)\n\n");
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
			for (int i=0;i<geneFiles.length;i++) {
				TreeReader read= new TreeReader(geneFiles[i],TreeReader.NEWICK);
				Tree geneTree= read.nextTree();

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

				if (outputGene!=null) {
					Tree copyCat= new Tree(reconciler.geneTree);
					copyCat.pretreatment();
					copyCat.formatLabelsWithTrace();
					TreeWriter geneWriter= new TreeWriter(copyCat);
					geneWriter.writeTree(outputGene);
				}
				if (outputPhyloXML!=null) {
					Tree copyCat= new Tree(reconciler.geneTree);
					copyCat.pretreatment();
					BufferedWriter xmlWrite= new BufferedWriter(new FileWriter(outputPhyloXML));
					xmlWrite.write(copyCat.toPhyloXMLString() + "\n");
					xmlWrite.flush();
					xmlWrite.close();
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
				score.writeScores(stats);
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}







}
package rapgreen;
import java.util.*;
import java.io.*;

/**
 * @author Jean-Francois Dufayard
 * @version 1.0
 * Display rootings of a tree
 */
public class Rootings {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************
/**
* Input species file
*/
	public static File input;

/**
* Output rootings file
*/
	public static File output=null;

/**
* Output midpoint file
*/
	public static File midpoint=null;


   private static final String NORMAL     = "\u001b[0m";
   private static final String BOLD       = "\u001b[1m";
   private static final String UNDERLINE  = "\u001b[4m";
// ********************************************************************************************************************
// ***     MAIN     ***
// ********************
	public static void main(String[] args) {
		String s=null;
		try {
			for (int i=0;i<args.length;i=i+2) {
				if (args[i].contains("help")) {
					System.out.println(BOLD);
					System.out.println("NAME:");
					System.out.println(NORMAL);
					System.out.println("\t- Rootings v1.0 -");
					System.out.println(BOLD);
					System.out.println("SYNOPSIS:");
					System.out.println(NORMAL);
					System.out.println("\tjava -jar Rootings.jar [command args]");
					System.out.println(BOLD);
					System.out.println("OPTIONS:");
					System.out.println(BOLD);
					System.out.println("-input" + NORMAL + " " + UNDERLINE + "gene_tree_file\n\t" + NORMAL + "The input gene tree file");
					System.out.println(BOLD);
					System.out.println("-output" + NORMAL + " "  + UNDERLINE + "rootings_file\n\t" + NORMAL + "The output file containing input tree rootings");
					System.out.println(BOLD);
					System.out.println("-midpoint" + NORMAL + " "  + UNDERLINE + "midpoint_file\n\t" + NORMAL + "The output file containing the midpoint rooted input tree\n\n");
					System.exit(0);
				}				
				if (args[i].equalsIgnoreCase("-input")) {
					input= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-output")) {
					output= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-midpoint")) {
					midpoint= new File(args[i+1]);
				}
			}

			TreeReader reader= new TreeReader(input,TreeReader.NEWICK);
			Tree tree= reader.nextTree();

			tree.pretreatment();
			Vector roots= tree.getRootedTrees();



			BufferedWriter write = null;
			if (output!=null) {
				write = new BufferedWriter(new FileWriter(output));
			}
			double maxMidpoint=10000000.0;
			Tree midpointTree=null;
			for (int i=0;i<roots.size();i++) {
				Tree root= (Tree)(roots.elementAt(i));
				root.pretreatment();
				double localMidpoint= root.midpoint();
				if (localMidpoint<maxMidpoint) {
					maxMidpoint=localMidpoint;
					midpointTree= root;	
				}
				if (output!=null) {
					TreeWriter writer= new TreeWriter(root);
					writer.writeSimpleTree(write);
					//write.write(localMidpoint+"\n");
					write.flush();
				}
			}
			if (output!=null) {
				write.close();
			}
			if (midpoint!=null) {
				write = new BufferedWriter(new FileWriter(midpoint));
				TreeWriter writer= new TreeWriter(midpointTree);
				writer.writeSimpleTree(write);
				write.flush();
				write.close();
				
			}

		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(s);
		}
	}







}
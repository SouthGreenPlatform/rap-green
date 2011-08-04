package rapgreen;
import java.util.*;
import java.io.*;

/**
 * @author Jean-Francois Dufayard
 * @version 1.0
 * Display clusters from a tree
 */
public class TreeClustering {

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
* Cutting threshold
*/
	public static double threshold;

// ********************************************************************************************************************
// ***     MAIN     ***
// ********************
	public static void main(String[] args) {
		String s=null;
		try {
			for (int i=0;i<args.length;i=i+2) {
				if (args[i].equalsIgnoreCase("-input")) {
					treeFile= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-output")) {
					outputFile= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-cut")) {
					threshold= (new Double(args[i+1])).doubleValue();
				}
			}

			TreeReader reader= new TreeReader(treeFile,TreeReader.NEWICK);
			Tree tree= reader.nextTree();
			tree.pretreatment();


			Vector trees= new Vector();
			tree.clusteringNodes(trees,threshold);

			BufferedWriter write = new BufferedWriter(new FileWriter(outputFile));

			write.write(trees.size() + " clusters.\nSizes:");
			System.out.print(trees.size() + " clusters.\nSizes:");

			for (int i=0;i<trees.size();i++) {
				Tree current= (Tree)(trees.elementAt(i));
				write.write(" " + current.leafVector.size());
				System.out.print(" " + current.leafVector.size());
			}
			write.write("\n");
			System.out.print("\n");
			write.flush();


			for (int i=0;i<trees.size();i++) {
				Tree current= (Tree)(trees.elementAt(i));
				write.write("\nCLUSTER " + (i+1) + ":\n");
				write.flush();
				for (int j=0;j<current.leafVector.size();j++) {
					write.write(((Tree)(current.leafVector.elementAt(j))).label + "\n");
					write.flush();
				}
			}

			write.close();

		} catch(Exception e) {
			System.out.println("Usage:\ntreeclustering -input your_tree_file -output your_output_file -cut your_threshold\n");
		}
	}







}
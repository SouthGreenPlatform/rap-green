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
	
/**
* Dictionnary
*/
	public static File dictionary;

// ********************************************************************************************************************
// ***     MAIN     ***
// ********************
	public static void main(String[] args) {
		String s=null;
		String label=null;
		double length=-1.0;
		dictionary=null;
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
				if (args[i].equalsIgnoreCase("-length")) {
					length= (new Double(args[i+1])).doubleValue();
				}
				if (args[i].equalsIgnoreCase("-label")) {
					label= args[i+1];
				}
				if (args[i].equalsIgnoreCase("-dictionary")) {
					dictionary= new File(args[i+1]);
				}
			}
			if (dictionary==null) {
				TreeReader reader= new TreeReader(treeFile,TreeReader.NEWICK);
				Tree tree= reader.nextTree();
				tree.pretreatment();
				if (length==-1.0) {
	
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
				} else {
					BufferedWriter write = new BufferedWriter(new FileWriter(outputFile));
				
					Tree current= tree.getNode(length,label);
					for (int j=0;j<current.leafVector.size();j++) {
						write.write(((Tree)(current.leafVector.elementAt(j))).label + "\n");
						write.flush();
					}
					
		
					write.close();				
					
				}
			} else {
				TreeReader reader= new TreeReader(treeFile,TreeReader.NEWICK);
				Tree tree= reader.nextTree();
				tree.pretreatment();
				
				BufferedReader read= new BufferedReader(new FileReader(dictionary));	
				s= read.readLine();
				Hashtable d= new Hashtable();
				while (s!=null) {
					String[] elements= s.split("\t");	
					d.put(elements[0],elements[1]);
					s= read.readLine();
				}
				read.close();
				for (int j=0;j<tree.leafVector.size();j++) {
					Tree leaf=(Tree)(tree.leafVector.elementAt(j));
					leaf.label= (String)(d.get(leaf.label));
				}							
				
				BufferedWriter write = new BufferedWriter(new FileWriter(outputFile));
				write.write(tree.getNewick() + "\n");
				write.close();
			}

		} catch(Exception e) {
			System.out.println("Usage for standard tree clustering:\ntreeclustering -input your_tree_file -output your_output_file -cut your_threshold\ntreeclustering -input your_tree_file -output your_output_file -length target_length -label target_label\nUsage to replace labels in a tree, using a CSV dictionary:\ntreeclustering -input your_tree_file -output your_output_file -dictionary input_dictionary\n");
		}
	}







}
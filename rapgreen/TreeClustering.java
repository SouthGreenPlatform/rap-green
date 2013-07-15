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
	
/**
* Closer option
*/
	public static String closer=null;
	
	public static boolean toCut=false;

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
				if (args[i].equalsIgnoreCase("-closer")) {
					closer= args[i+1];
				}
				if (args[i].equalsIgnoreCase("-trunk")) {
					toCut= true;
				}
				if (args[i].equalsIgnoreCase("-dictionary")) {
					dictionary= new File(args[i+1]);
				}
			}
			if (closer!=null) {
				System.out.println("Computing average distances :\n");
				TreeReader reader= new TreeReader(new File(closer),TreeReader.NEWICK);
				Tree tree= reader.nextTree();
				tree.pretreatment();
				if (length!=-1 || label!=null) {
					tree=tree.getNode(length,label);
				}
				int bestIndex=-1;
				double bestDist=10000.0;
				for (int i=0;i<tree.leafVector.size();i++) {
					Tree leaf = (Tree)(tree.leafVector.elementAt(i));
					double localDist=0.0;
					for (int j=0;j<tree.leafVector.size();j++) {
						if (i!=j) {
							Tree leaf2 = (Tree)(tree.leafVector.elementAt(j));
							// Find the last common ancestor of the two target leaves
							Tree ancestor= tree.lastCommonAncestor(leaf,leaf2);
							// Compute the simple distance (sum of branch lengths)
							double d= ancestor.getDepth(leaf) + ancestor.getDepth(leaf2);
							
							localDist+=d;
							
							
							
						}						
					}
					localDist= localDist / ((double)tree.leafVector.size() - 1.0);
					
					System.out.println(leaf.label + " : " + localDist);
				
				
				
					if (localDist<bestDist) {
						bestIndex=i;
						bestDist=localDist;						
					}
				
				
					
				}
				
				System.out.println("\nMost representative sequence : " + ((Tree)(tree.leafVector.elementAt(bestIndex))).label);
			} else {
			
			
			
			
			
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
							
							
							int bestIndex=-1;
							double bestDist=10000.0;
							for (int k=0;k<current.leafVector.size();k++) {
								Tree leaf = (Tree)(current.leafVector.elementAt(k));
								double localDist=0.0;
								for (int j=0;j<current.leafVector.size();j++) {
									if (k!=j) {
										Tree leaf2 = (Tree)(current.leafVector.elementAt(j));
										// Find the last common ancestor of the two target leaves
										Tree ancestor= tree.lastCommonAncestor(leaf,leaf2);
										// Compute the simple distance (sum of branch lengths)
										double d= ancestor.getDepth(leaf) + ancestor.getDepth(leaf2);
							
										localDist+=d;
							
							
							
									}						
								}
								localDist= localDist / ((double)tree.leafVector.size() - 1.0);
					
								System.out.println(leaf.label + " : " + localDist);
				
				
				
								if (localDist<bestDist) {
									bestIndex=k;
									bestDist=localDist;						
								}
				
				
					
							}				
							
							
							
							
							write.write("Most representative sequence:\n" + ((Tree)(current.leafVector.elementAt(bestIndex))).label + "\n");
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
						if (toCut) {
							d.put(elements[0].substring(0,elements[0].indexOf("_")),elements[1]);
						} else {
							d.put(elements[0],elements[1]);
						}
						s= read.readLine();
					}
					read.close();
					for (int j=0;j<tree.leafVector.size();j++) {
						Tree leaf=(Tree)(tree.leafVector.elementAt(j));
						if (toCut) {
							if (!d.containsKey(leaf.label.substring(0,leaf.label.indexOf("_")))) {
								System.out.println(leaf.label + " not present in dictionary.");	
							}
							leaf.label= (String)(d.get(leaf.label.substring(0,leaf.label.indexOf("_"))));
						} else {
							if (!d.containsKey(leaf.label)) {
								System.out.println(leaf.label + " not present in dictionary.");	
							}
							leaf.label= (String)(d.get(leaf.label));
							
						}
					}							
					
					BufferedWriter write = new BufferedWriter(new FileWriter(outputFile));
					write.write(tree.getNewick() + "\n");
					write.close();
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Usage for standard tree clustering:\ntreeclustering -input your_tree_file -output your_output_file -cut your_threshold\ntreeclustering -input your_tree_file -output your_output_file -length target_length -label target_label\nUsage to replace labels in a tree, using a CSV dictionary:\ntreeclustering -input your_tree_file -output your_output_file -dictionary input_dictionary\nUsage to choose the most representative sequence in a tree:\ntreeclustering -closer your_tree_file [-length target_length -label target_label]\n");
		}
	}







}
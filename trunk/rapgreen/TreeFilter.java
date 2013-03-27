package rapgreen;
import java.util.*;
import java.io.*;

/**
 * @author Jean-Francois Dufayard
 * @version 1.0
 * Filter PhyloXML trees, removing specified taxa
 */
public class TreeFilter {

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
* List of taxids to remove
*/
	public static Vector idList;
	
/**
* Table of taxids to remove
*/
	public static Hashtable idTable;
	
/**
* Support threshold
*/

	//public static double support=null;
	
	public static boolean clean=false;

// ********************************************************************************************************************
// ***     MAIN     ***
// ********************
	public static void main(String[] args) {
		outputFile=null;
		treeFile=null;
		idList= new Vector();
		idTable= new Hashtable();
		try {
			for (int i=0;i<args.length;i=i+2) {
				if (args[i].equalsIgnoreCase("-input")) {
					treeFile= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-output")) {
					outputFile= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-remove")) {
					idList.addElement(args[i+1]);
					idTable.put(args[i+1],"");
				}
				if (args[i].equalsIgnoreCase("-clean")) {
					clean=true;
					i--;
				}
								
			}
			
			if (clean) {
				TreeReader reader= new TreeReader(treeFile,TreeReader.NEWICK);
				Tree tree= reader.nextTree();	
				tree.pretreatment();
				
				for (int i=0;i<tree.leafVector.size();i++) {
					Tree leaf= (Tree)(tree.leafVector.elementAt(i));	
					leaf.label= leaf.label.substring(0,leaf.label.indexOf("_"));
					while (leaf.label.startsWith("0")) {
						leaf.label= leaf.label.substring(1,leaf.label.length());
						
						
					}
					
				}
				String phylo= tree.getNewick();
				if (outputFile!=null) {
					BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
					write.write(phylo+"\n");
					write.flush();
					write.close();
				} else {
					System.out.println(phylo);				
				}
				System.exit(0);	
			}
			//System.out.println(tree);
			
			TreeReader reader= new TreeReader(treeFile,TreeReader.XML);
			Tree tree= reader.nextTree();	
			Hashtable excluded= new Hashtable();
			tree.taxonomicPretreatment();
			/*if (support!=null) {
				tree.collapseSupport(support);
				String phylo= tree.getNewick();
				if (outputFile!=null) {
					BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
					write.write(phylo+"\n");
					write.flush();
					write.close();
				} else {
					System.out.println(phylo);				
				}
				
			} else {*/
				if (tree.exclude(idList,idTable,excluded)) {
					StringBuffer relations= new StringBuffer();
					filterRelations(relations,excluded);
					String phylo= tree.toPhyloXMLString(relations);
					if (outputFile!=null) {
						BufferedWriter write= new BufferedWriter(new FileWriter(outputFile));
						write.write(phylo+"\n");
						write.flush();
						write.close();
					} else {
						System.out.println(phylo);				
					}
					
				} else {				
					System.out.println("Empty tree.");
				}
			//}

		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Usage for standard tree filtering:\njava -jar TreeFilter.jar -input your_tree_file -remove taxid_to_remove [-remove taxid_to_remove] [-output your_output_file]\n");
		}
	}

// ********************************************************************************************************************
// ***     PRIVATE CONSTRUCTOR TOOLS     ***
// *****************************************
	private static void filterRelations(StringBuffer relations, Hashtable excluded) {
		String s=null;
		try {
			BufferedReader read= new BufferedReader(new FileReader(treeFile));
			s= read.readLine();
			while (s!=null && !s.contains("<sequence_relation")) {
				s= read.readLine();
			}
			while (s!=null) {
				StringBuffer id1= new StringBuffer();
				StringBuffer id2= new StringBuffer();
				int i=0;
				while (s.charAt(i)!='"') {
					i++;				
				}
				i++;
				while (s.charAt(i)!='"') {
					id1.append(s.charAt(i));
					i++;				
				}
				i++;	
				while (s.charAt(i)!='"') {
					i++;				
				}
				i++;
				while (s.charAt(i)!='"') {
					id2.append(s.charAt(i));
					i++;				
				}
				if (excluded.containsKey(id1.toString()) || excluded.containsKey(id2.toString())) {
					s=read.readLine();					
					while (s!=null && !s.contains("<sequence_relation")) {
						s= read.readLine();
					}
				} else {
					relations.append(s + "\n");
					s=read.readLine();					
					while (s!=null && !s.contains("</sequence_relation")) {						
						relations.append(s + "\n");
						s= read.readLine();
					}						
					relations.append(s + "\n");
					while (s!=null && !s.contains("<sequence_relation")) {
						s= read.readLine();
					}
					
				}
				
				
			}
			
			
			
								
			read.close();
		} catch(Exception e) {
			System.out.println("bugged line: " + s);
			e.printStackTrace();
			
		}
	}



}
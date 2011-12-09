package rapgreen;
import java.util.*;
import java.io.*;


/**
 * Tree file reading class.
 * <p>
 * This class contains file reading tools, to preformat file.
 * @author Jean-Francois Dufayard
 * @version 1.0
 */
public class TreeReader {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************
/**
* Newick format. The newick file may contain several trees.
*/
	public static final int NEWICK=0;
/**
* XML format. The XML file is supposed to contain only one tree.
*/
	public static final int XML=1;
/**
* Trees
*/
	public Vector trees;

/**
* The index
*/
	public int treeIndex;

// ********************************************************************************************************************
// ***     CONSTRUCTORS      ***
// *****************************
/**
* Generic constructor, from several formats
* @param file	The tree file
* @param format	The encoding format
*/
	public TreeReader(File file, int format) {
		treeIndex=0;
		trees= new Vector();
		StringBuffer result= new StringBuffer();
		try {
			BufferedReader read= new BufferedReader(new FileReader(file));
			String s= read.readLine();
			while (s!=null) {
				result.append(s);
				s= read.readLine();
			}
		} catch(Exception e) {
			//e.printStackTrace();
		}

		//last index will not be informative because of the split mechanics
		String[] newicks=null;

		if (format==NEWICK) {
			newicks= result.toString().split(";");
		} else {
			//XML case, modify the string to newick format
			StringBuffer newick= new StringBuffer();
		//System.out.println("EchoA");
			toNewick(0,result.toString().replace("\t",""),newick,null);
		//System.out.println("EchoB");
			newicks= new String[1];
			newicks[0]=newick.toString();
			System.out.println(newicks[0]);
		}
		//Construct trees from newick strings
		for (int i=0;i<newicks.length;i++) {
			trees.addElement(new Tree(newicks[i]+";"));
		}
	}

// ********************************************************************************************************************
// ***     CONSTRUCTORS      ***
// *****************************
/**
* Generic constructor, from several formats, filling a species dictionary
* @param file	The tree file
* @param dico	The species dictionary to fill
* @param format	The encoding format
*/
	public TreeReader(File file,SpeciesDictionary dico, int format) {
		treeIndex=0;
		trees= new Vector();
		StringBuffer result= new StringBuffer();
		try {
			BufferedReader read= new BufferedReader(new FileReader(file));
			String s= read.readLine();
			while (s!=null) {
				result.append(s);
				s= read.readLine();
			}
		} catch(Exception e) {
			//e.printStackTrace();
		}

		//last index will not be informative because of the split mechanics
		String[] newicks=null;

		if (format==NEWICK) {
			newicks= result.toString().split(";");
			//Fill the dictionnary
			for (int i=0;i<newicks.length;i++) {
				Tree localTree= new Tree(newicks[i]+";");
				localTree.pretreatment();
				for (int j=0;j<localTree.leafVector.size();j++) {
					Tree leaf= (Tree)(localTree.leafVector.elementAt(j));	
					dico.addSpecies(leaf.label,"N/A","N/A");
				}
				trees.addElement(localTree);
			}
		} else {
			//XML case, modify the string to newick format
			StringBuffer newick= new StringBuffer();
			toNewick(0,result.toString().replace("\t",""),newick,dico);
			newicks= new String[1];
			newicks[0]=newick.toString();
			//System.out.println(newicks[0]);
			//Construct trees from newick strings
			for (int i=0;i<newicks.length;i++) {
				trees.addElement(new Tree(newicks[i]+";"));
			}
		}

	}
	
// ********************************************************************************************************************
// ***     CONSTRUCTORS      ***
// *****************************
/**
* Generic constructor, from several formats, filling a species dictionary, and an ID index
* @param file	The tree file
* @param dico	The species dictionary to fill
* @param index	ID index to fill
* @param format	The encoding format
*/
	public TreeReader(File file,SpeciesDictionary dico, Hashtable index,int format) {
		treeIndex=0;
		trees= new Vector();
		StringBuffer result= new StringBuffer();
		try {
			BufferedReader read= new BufferedReader(new FileReader(file));
			String s= read.readLine();
			while (s!=null) {
				result.append(s);
				s= read.readLine();
			}
		} catch(Exception e) {
			//e.printStackTrace();
		}

		//last index will not be informative because of the split mechanics
		String[] newicks=null;

		if (format==NEWICK) {
			newicks= result.toString().split(";");
			//Fill the dictionnary
			for (int i=0;i<newicks.length;i++) {
				Tree localTree= new Tree(newicks[i]+";");
				localTree.pretreatment();
				trees.addElement(localTree);
				localTree.fillAndCleanID(index);
				for (int j=0;j<localTree.leafVector.size();j++) {
					Tree leaf= (Tree)(localTree.leafVector.elementAt(j));						
					dico.addSpecies(leaf.label,"N/A","N/A");
				}
			}
		} else {
			//XML case, modify the string to newick format
			StringBuffer newick= new StringBuffer();
			toNewick(0,result.toString().replace("\t",""),newick,dico);
			newicks= new String[1];
			newicks[0]=newick.toString();
			//System.out.println(newicks[0]);
			//Construct trees from newick strings
			for (int i=0;i<newicks.length;i++) {
				trees.addElement(new Tree(newicks[i]+";"));
			}
		}

	}
	
// ********************************************************************************************************************
// ***     CONSTRUCTION PRIVATE METHODS     ***
// ********************************************
/**
* Private parser of a node, from a starting point in a newick string, to an ending point returned in an Integer object.
* @param index	The starting index in the newick string
* @param source	The newick string, encoding the information
* @param res	The buffer used to return the result
* @param dico	The species dictionary to fill
* @return The new index
*/
	public int toNewick(int index, String source, StringBuffer res, SpeciesDictionary dico) {
		StringBuffer common= new StringBuffer();
		StringBuffer id= new StringBuffer();
		StringBuffer scientific= new StringBuffer();
		StringBuffer code= new StringBuffer();
		StringBuffer nodeId= new StringBuffer();
		String event="";
		StringBuffer sp1= new StringBuffer();
		StringBuffer sp2= new StringBuffer();
		StringBuffer sp3= new StringBuffer();
		StringBuffer accession= new StringBuffer();
		while (!source.substring(index,source.length()).startsWith("<clade>")) {
			index++;
		}
		index++;
		while (source.charAt(index)!='<') {
			index++;
		}
		//Parse info between clade anchors
		while (!source.substring(index,source.length()).startsWith("</clade>")) {
			//System.out.println(source.substring(index,index+18));
			if (source.substring(index,source.length()).startsWith("</rec:event>")) {
				index++;
				while (source.charAt(index)!='<') {
					index++;
				}
			} else if (source.substring(index,source.length()).startsWith("<rec:event>")) {
				index++;
				while (source.charAt(index)!='<') {
					index++;
				}
			} else if (source.substring(index,source.length()).startsWith("</taxonomy>")) {
				index++;
				while (source.charAt(index)!='<') {
					index++;
				}
			} else if (source.substring(index,source.length()).startsWith("<taxonomy>")) {
				index++;
				while (source.charAt(index)!='<') {
					index++;
				}
			} else if (source.substring(index,source.length()).startsWith("<rec:speciation>")) {
				event="S";
				index+=16;
				//get duplication parameter
				while (!source.substring(index,source.length()).startsWith("</rec:speciation>")) {
					if (source.substring(index,source.length()).startsWith("<rec:locationSp>")) {
						index+=16;
						while (source.charAt(index)!='<') {
							sp1.append(source.charAt(index));
							index++;
						}
						index+=17;
					} 																
				}
				index+=17;
			} else if (source.substring(index,source.length()).startsWith("<rec:duplication>")) {
				event="D";
				index+=17;
				//get duplication parameter
				while (!source.substring(index,source.length()).startsWith("</rec:duplication>")) {
					if (source.substring(index,source.length()).startsWith("<rec:locationSp>")) {
						index+=16;
						while (source.charAt(index)!='<') {
							sp1.append(source.charAt(index));
							index++;
						}
						index+=17;
					} 																
				}
				index+=18;
			} else if (source.substring(index,source.length()).startsWith("<rec:loss>")) {
				event="L";
				index+=10;
				//get duplication parameter
				while (!source.substring(index,source.length()).startsWith("</rec:loss>")) {
					if (source.substring(index,source.length()).startsWith("<rec:locationSp>")) {
						index+=16;
						while (source.charAt(index)!='<') {
							sp1.append(source.charAt(index));
							index++;
						}
						index+=17;
					} 																
				}
				index+=11;
			} else if (source.substring(index,source.length()).startsWith("<rec:transfer>")) {
				event="T";
				index+=14;
				//get duplication parameter
				while (!source.substring(index,source.length()).startsWith("</rec:transfer>")) {
					if (source.substring(index,source.length()).startsWith("<rec:originSp>")) {
						index+=14;
						while (source.charAt(index)!='<') {
							sp1.append(source.charAt(index));
							index++;
						}
						index+=15;
					} else if (source.substring(index,source.length()).startsWith("<rec:recipientSp>")) {
						index+=17;
						while (source.charAt(index)!='<') {
							sp2.append(source.charAt(index));
							index++;
						}
						index+=18;
					} else if (source.substring(index,source.length()).startsWith("<rec:transferedChild>")) {
						index+=21;
						while (source.charAt(index)!='<') {
							sp3.append(source.charAt(index));
							index++;
						}
						index+=22;
					}														
				}
				index+=15;
			} else if (source.substring(index,source.length()).startsWith("<node_id>")) {
				index+=9;
				while (source.charAt(index)!='<') {
					nodeId.append(source.charAt(index));
					index++;
				}
				index+=10;
			} else if (source.substring(index,source.length()).startsWith("<common_name>")) {
				index+=13;
				while (source.charAt(index)!='<') {
					common.append(source.charAt(index));
					index++;
				}
				index+=14;
			} else if (source.substring(index,source.length()).startsWith("<accession source=\"Hogenom\">")) {
				index+=28;
				while (source.charAt(index)!='<') {
					accession.append(source.charAt(index));
					index++;
				}
				index+=12;
			} else if (source.substring(index,source.length()).startsWith("<scientific_name>")) {
				index+=17;
				while (source.charAt(index)!='<') {
					scientific.append(source.charAt(index));
					index++;
				}
				index+=18;
			} else if (source.substring(index,source.length()).startsWith("<id>")) {
				index+=4;
				while (source.charAt(index)!='<') {
					id.append(source.charAt(index));
					index++;
				}
				index+=5;
			} else if (source.substring(index,source.length()).startsWith("<code>")) {
				index+=6;
				while (source.charAt(index)!='<') {
					code.append(source.charAt(index));
					index++;
				}
				index+=7;
			} else if (source.substring(index,source.length()).startsWith("<clade>")) {
				//The node case, recursively parse the sons
				res.append('(');
				index= toNewick(index,source,res,dico);
				while (!source.substring(index,source.length()).startsWith("</clade>")) {
					res.append(',');
					index= toNewick(index,source,res,dico);
				}
				res.append(')');

			}
		}
		if (event.length()>0) {
			res.append(event);
			res.append("_");
			res.append(sp1.toString());
			if (event.equals("T")) {
				res.append("_");
				res.append(sp2.toString());
				res.append("_");
				res.append(sp3.toString());			
			}
			res.append("_");				
		}
		if (nodeId.length()>0) {
			res.append(nodeId.toString());
			if (accession.length()>0) {
				res.append("_");
			}			
		}		
		if (accession.length()>0) {
			res.append(accession.toString());
			res.append("_");
		}
		if (code.length()>0) {
			//System.out.println(code.toString() + " " + scientific.toString() + " " + id.toString());
			if (dico!=null)
				dico.addSpecies(code.toString(),scientific.toString(),id.toString());
			res.append(code.toString());
		}
		index++;
		while (index<source.length() && source.charAt(index)!='<') {
			index++;
		}
		return index;
	}

// ********************************************************************************************************************
// ***     OBJECT METHODS     ***
// ******************************
/**
* Return the next tree
* @return The next tree
*/
	public Tree nextTree() {
		if (treeIndex>=trees.size()) {
			return null;
		} else {
			treeIndex++;
			return((Tree)(trees.elementAt(treeIndex-1)));
		}
	}

}
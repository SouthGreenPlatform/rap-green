package rapgreen;
import java.util.*;
import java.io.*;


/**
 * Tree file writing class.
 * <p>
 * This class contains file writing tools.
 * @author Jean-Francois Dufayard
 * @version 1.0
 */
public class TreeWriter {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************
/**
*	Trees to write
*/
	Vector trees;

/**
*	Index of tree to write
*/
	int treeIndex;

// ********************************************************************************************************************
// ***     CONSTRUCTORS      ***
// *****************************
/**
* Generic constructor, from several formats
* @param tree	The tree to write
*/
	public TreeWriter(Tree tree) {
		treeIndex=0;
		trees= new Vector();
		trees.addElement(tree);

	}

// ********************************************************************************************************************
// ***     CONSTRUCTION PRIVATE METHODS     ***
// ********************************************


// ********************************************************************************************************************
// ***     OBJECT METHODS     ***
// ******************************
/**
* Write the next tree
* @param file	The destination file
*/
	public void writeTree(File file) {
		if (treeIndex<trees.size()) {
			try {
				BufferedWriter write= new BufferedWriter(new FileWriter(file));
				write.write(((Tree)(trees.elementAt(treeIndex))).getNewick() + "\n");
				write.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			treeIndex++;
		}
	}
// ******************************
/**
* Write unannotated tree, cleaning labels annotated after the reconciliation
* @param file	The destination file
*/
	public void writeSimpleTree(BufferedWriter write) {
		if (treeIndex<trees.size()) {
			try {
				Tree localTree=(Tree)(trees.elementAt(treeIndex));
				if (!localTree.isLeaf()) {
					Tree son1= (Tree)(localTree.sons.elementAt(0));
					Tree son2= (Tree)(localTree.sons.elementAt(1));
					if (son1.isLeaf()) {
						son2.label="";
					}
					if (son2.isLeaf()) {
						son1.label="";
					}
				} 
				String temp =localTree.getNewick();
				temp= temp.replaceAll("DUPLICATION","");
				temp= temp.replaceAll("'","");
				temp=temp.substring(0,temp.lastIndexOf(")")+1) + ";";
				
				write.write(temp + "\n");
			} catch(Exception e) {
				e.printStackTrace();
			}
			treeIndex++;
		}
	}
	

// ******************************
/**
* Write unannotated tree, cleaning labels annotated after the reconciliation
* @param file	The destination file
*/
	public void writeSimpleTree(File file) {
		if (treeIndex<trees.size()) {
			try {
				BufferedWriter write= new BufferedWriter(new FileWriter(file));
				Tree localTree=(Tree)(trees.elementAt(treeIndex));
				if (!localTree.isLeaf()) {
					Tree son1= (Tree)(localTree.sons.elementAt(0));
					Tree son2= (Tree)(localTree.sons.elementAt(1));
					if (son1.isLeaf()) {
						son2.label="";
					}
					if (son2.isLeaf()) {
						son1.label="";
					}
				} 
				String temp =localTree.getNewick();
				temp= temp.replaceAll("DUPLICATION","");
				temp= temp.replaceAll("'","");
				temp=temp.substring(0,temp.lastIndexOf(")")+1) + ";";
				
				write.write(temp + "\n");
				write.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			treeIndex++;
		}
	}
	
}

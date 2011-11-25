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

}
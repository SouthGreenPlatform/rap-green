package rapgreen;
import java.util.*;
import java.io.*;


/**
 * Tree scoring class.
 * <p>
 * This class contains several tools to compute gene to gene scores in an annotated phylogenetic tree.
 * @author Jean-Francois Dufayard
 * @version 1.0
 */
public class TreeScoring {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************
/**
*	The reference tree
*/
	public Tree tree;

/**
*	Distance between two genes
*/
	public double[][] distance;


/**
*	Number of topological incongruence duplications between two genes
*/
	public int[][] nbTopologicalDuplications;

/**
*	Number of taxonomic intersection duplications between two genes
*/
	public int[][] nbIntersectionDuplications;

/**
*	Number of spectiations between two genes
*/
	public int[][] nbSpeciations;

/**
*	Strict orthology relationship
*/
	public boolean[][] fitchOrthology;

/**
*	Ultraparalogy relationship
*/
	public boolean[][] ultraParalogy;

/**
*	PhyloXML relationship buffer
*/
	public StringBuffer phyloXMLBuffer;

/**
*	Global scoring attributs
*/
	public static double tDupWeight   = 0.95;
	public static double iDupWeight   = 0.90;
	public static double specWeight   = 0.99;
	public static double lengthWeight = 0.10;

// ********************************************************************************************************************
// ***     CONSTRUCTORS      ***
// *****************************
/**
* Generic constructor
* @param tree	The tree to score
*/
	public TreeScoring(Tree tree) {
		this.tree=tree;
		//System.out.println(tree);
		if (!tree.isLeaf()) {
			computeScores();
		}
	}

// ********************************************************************************************************************
// ***     CONSTRUCTION PRIVATE METHODS     ***
// ********************************************
/**
* Compute scores for every pair of genes, and stock it
*/

	public void computeScores() {
		//Initialize tables
		distance= new double[tree.leafVector.size()][tree.leafVector.size()];
		nbTopologicalDuplications= new int[tree.leafVector.size()][tree.leafVector.size()];
		nbIntersectionDuplications= new int[tree.leafVector.size()][tree.leafVector.size()];
		nbSpeciations= new int[tree.leafVector.size()][tree.leafVector.size()];
		fitchOrthology= new boolean[tree.leafVector.size()][tree.leafVector.size()];
		ultraParalogy= new boolean[tree.leafVector.size()][tree.leafVector.size()];

		phyloXMLBuffer= new StringBuffer();

		//Initialize duplication evaluation
		tree.initializeDuplicationNatures();
		//System.out.println(tree);

		for (int i=0;i<tree.leafVector.size();i++) {
			Tree leafI= (Tree)(tree.leafVector.elementAt(i));
			for (int j=i+1;j<tree.leafVector.size();j++) {
				Tree leafJ= (Tree)(tree.leafVector.elementAt(j));
				// Find the last common ancestor of the two target leaves
				Tree ancestor= tree.lastCommonAncestor(leafI,leafJ);
				// Compute the simple distance (sum of branch lengths)
				double localDistance= ancestor.getDepth(leafI) + ancestor.getDepth(leafJ);
				distance[i][j]= localDistance;
				distance[j][i]= localDistance;
				// Count the number of duplications infered by topological incongruence
				int localNbTopologicalDuplication= ancestor.nbTopologicalDuplications(leafI) + ancestor.nbTopologicalDuplications(leafJ);
				nbTopologicalDuplications[i][j]= localNbTopologicalDuplication;
				nbTopologicalDuplications[j][i]= localNbTopologicalDuplication;
				// Count the number of duplications infered by taxonomic intersection
				int localNbIntersectionDuplication= ancestor.nbIntersectionDuplications(leafI) + ancestor.nbIntersectionDuplications(leafJ);
				nbIntersectionDuplications[i][j]= localNbIntersectionDuplication;
				nbIntersectionDuplications[j][i]= localNbIntersectionDuplication;
				// Count the number of speciations
				int localNbSpeciations= ancestor.nbSpeciations(leafI) + ancestor.nbSpeciations(leafJ);
				nbSpeciations[i][j]= localNbSpeciations;
				nbSpeciations[j][i]= localNbSpeciations;
				//System.out.println(leafI.getNewick() + " | " + leafJ.getNewick() + " : " + localDistance + " " + localNbTopologicalDuplication + " " + localNbIntersectionDuplication);
				if (ancestor.label.contains("DUPLICATION")) {
					fitchOrthology[i][j]= false;
					fitchOrthology[j][i]= false;
				} else {
					fitchOrthology[i][j]= true;
					fitchOrthology[j][i]= true;
				}
				if (ancestor.ultraParalogy()) {
					ultraParalogy[i][j]= true;
					ultraParalogy[j][i]= true;
				} else {
					ultraParalogy[i][j]= false;
					ultraParalogy[j][i]= false;
				}
			}

		}


	}

// ********************************************************************************************************************
// ***     OBJECT METHODS     ***
// ******************************
/**
* Write in a CSV file every scores
* @param file	Destination file
*/
	public void writeScores(File file) {
		try {
			BufferedWriter write= new BufferedWriter(new FileWriter(file));
			write.write("GENE1\tkVALUE\tDIST\tSPEC\tT-DUP\tI-DUP\tORTHO\tULTRAP\tGENE2\n");
			write.flush();
			for (int i=0;i<tree.leafVector.size();i++) {
				Tree leafI= (Tree)(tree.leafVector.elementAt(i));
				for (int j=0;j<tree.leafVector.size();j++) {
					if (i!=j) {
						Tree leafJ= (Tree)(tree.leafVector.elementAt(j));
						double reduc= distance[i][j];
						reduc=reduc*10000.0;
						int reducInt= (int)reduc;
						reduc= (double)reducInt;
						reduc=reduc/10000.0;
						if (fitchOrthology[i][j]) {
							write.write(leafI.label + "\t" + leafI.subtreeNeighbor(TreeReconciler.kLevel) + "\t" + reduc + "\t" + (nbSpeciations[i][j]+1) + "\t" + nbTopologicalDuplications[i][j] + "\t" + nbIntersectionDuplications[i][j] + "\t" + fitchOrthology[i][j] + "\t" + ultraParalogy[i][j] + "\t" + leafJ.label + "\n");
							write.flush();


							phyloXMLBuffer.append("<sequence_relation id_ref_0=\"");
							phyloXMLBuffer.append(leafI.label.substring(0,leafI.label.lastIndexOf("_")));
							phyloXMLBuffer.append("\" id_ref_1=\"");
							phyloXMLBuffer.append(leafJ.label.substring(0,leafJ.label.lastIndexOf("_")));
							phyloXMLBuffer.append("\" type=\"orthology\">\n\t<confidence type=\"rap\">");

							//score computing, very prospective
							double s= 1.0;

							if (reduc>=1.0) {
								reduc=1.0;
							}


							double prop=1.0/(double)tree.leafVector.size();
							reduc=reduc*(1.0-lengthWeight);
							s=s*(1.0-reduc)*(1.0-prop)+prop;

							for (int k=0;k<nbTopologicalDuplications[i][j];k++) {
								s=s*tDupWeight;
							}
							for (int k=0;k<nbIntersectionDuplications[i][j];k++) {
								s=s*iDupWeight;
							}
							for (int k=0;k<nbSpeciations[i][j];k++) {
								s=s*specWeight;
							}


							s=s*1000.0;
							int sint= (int)s;
							s= (double)sint/1000.0;

							phyloXMLBuffer.append(s);
							phyloXMLBuffer.append("</confidence>\n</sequence_relation>\n");



						}
						if (ultraParalogy[i][j]) {
							write.write(leafI.label + "\t" + leafI.subtreeNeighbor(TreeReconciler.kLevel) + "\t" + reduc + "\t" + nbSpeciations[i][j] + "\t" + nbTopologicalDuplications[i][j] + "\t" + (nbIntersectionDuplications[i][j]+1) + "\t" + fitchOrthology[i][j] + "\t" + ultraParalogy[i][j] + "\t" + leafJ.label + "\n");
							write.flush();
						}
					}


				}


			}

			write.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
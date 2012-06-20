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
*	Number of ultraparalogy relationship under this node
*/
	public int[][] nbUltraParalogy;	

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
	public static double uParaWeight   = 0.99;
	public static double lengthWeight = 0.10;
	
	public static double lengthThreshold = 1.0;

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
		nbUltraParalogy= new int[tree.leafVector.size()][tree.leafVector.size()];

		phyloXMLBuffer= new StringBuffer();
		tree.ultraParalogy();

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
				if (ancestor.label.startsWith("'T_DUPLICATION")) {
					localNbTopologicalDuplication++;
				}
				nbTopologicalDuplications[i][j]= localNbTopologicalDuplication;
				nbTopologicalDuplications[j][i]= localNbTopologicalDuplication;
				// Count the number of duplications infered by taxonomic intersection
				int localNbIntersectionDuplication= ancestor.nbIntersectionDuplications(leafI) + ancestor.nbIntersectionDuplications(leafJ);
				if (ancestor.label.startsWith("'I_DUPLICATION")) {
					localNbIntersectionDuplication++;
				}				
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
				// Count the number of duplications infered by topological incongruence
				int localNbUltra= ancestor.nbUltra(leafI) + ancestor.nbUltra(leafJ);
				//System.out.println(ancestor.ultra);
				ultraParalogy[i][j]= ancestor.ultra;
				ultraParalogy[j][i]= ancestor.ultra;
				nbUltraParalogy[i][j]= localNbUltra;
				nbUltraParalogy[j][i]= localNbUltra;
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
			write.write("GENE1\tkVALUE\tDIST\tSPEC\tT-DUP\tI-DUP\tU-DUP\tORTHO\tULTRAP\tGENE2\tFSCORE\n");
			write.flush();
			for (int i=0;i<tree.leafVector.size();i++) {
				Tree leafI= (Tree)(tree.leafVector.elementAt(i));
				for (int j=0;j<tree.leafVector.size();j++) {
					if (i<j) {
						Tree leafJ= (Tree)(tree.leafVector.elementAt(j));
						double reduc= distance[i][j];
						reduc=reduc*10000.0;
						int reducInt= (int)reduc;
						reduc= (double)reducInt;
						reduc=reduc/10000.0;
						if (!ultraParalogy[i][j] && (fitchOrthology[i][j] || RapGreen.addOutparalogous)) {

							
							phyloXMLBuffer.append("<sequence_relation id_ref_0=\"");
							phyloXMLBuffer.append(leafI.label.substring(0,leafI.label.lastIndexOf("_")));
							phyloXMLBuffer.append("\" id_ref_1=\"");
							phyloXMLBuffer.append(leafJ.label.substring(0,leafJ.label.lastIndexOf("_")));
							if (fitchOrthology[i][j]) {
								phyloXMLBuffer.append("\" type=\"orthology\">\n\t<confidence type=\"rap\">");
							} else {
								phyloXMLBuffer.append("\" type=\"paralogy\">\n\t<confidence type=\"rap\">");
								
							}

							//score computing, very prospective
							double s= 1.0;

							if (reduc>=lengthThreshold) {
								reduc=lengthThreshold;
							}


							double prop=1.0/(double)tree.leafVector.size();
							
							double reducDis=reduc;
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
							for (int k=0;k<nbUltraParalogy[i][j];k++) {
								s=s*uParaWeight;
							}

							s=s*1000.0;
							int sint= (int)s;
							s= (double)sint/1000.0;

							phyloXMLBuffer.append(s);
							phyloXMLBuffer.append("</confidence>\n</sequence_relation>\n");
							write.write(leafI.label + "\t" + leafI.subtreeNeighbor(TreeReconciler.kLevel) + "\t" + reducDis + "\t" + (nbSpeciations[i][j]+1) + "\t" + nbTopologicalDuplications[i][j] + "\t" + nbIntersectionDuplications[i][j] + "\t" + nbUltraParalogy[i][j] + "\t"  + fitchOrthology[i][j] + "\t" + ultraParalogy[i][j] + "\t" + leafJ.label + "\t" + s + "\n");
							write.flush();
						} else if (ultraParalogy[i][j]) {
							phyloXMLBuffer.append("<sequence_relation id_ref_0=\"");
							phyloXMLBuffer.append(leafI.label.substring(0,leafI.label.lastIndexOf("_")));
							phyloXMLBuffer.append("\" id_ref_1=\"");
							phyloXMLBuffer.append(leafJ.label.substring(0,leafJ.label.lastIndexOf("_")));
							phyloXMLBuffer.append("\" type=\"ultra_paralogy\">\n\t<confidence type=\"rap\">");

							//score computing, very prospective
							double s= 1.0;

							if (reduc>=lengthThreshold) {
								reduc=lengthThreshold;
							}


							double prop=1.0/(double)tree.leafVector.size();
							double reducDis=reduc;
							reduc=reduc*(1.0-lengthWeight);
							s=s*(1.0-reduc)*(1.0-prop)+prop;

							for (int k=0;k<nbUltraParalogy[i][j];k++) {
								s=s*uParaWeight;
							}


							s=s*1000.0;
							int sint= (int)s;
							s= (double)sint/1000.0;

							phyloXMLBuffer.append(s);
							phyloXMLBuffer.append("</confidence>\n</sequence_relation>\n");
							write.write(leafI.label + "\t" + leafI.subtreeNeighbor(TreeReconciler.kLevel) + "\t" + reducDis + "\t" + nbSpeciations[i][j] + "\t" + nbTopologicalDuplications[i][j] + "\t" + nbIntersectionDuplications[i][j] + "\t" + nbUltraParalogy[i][j] + "\t" + fitchOrthology[i][j] + "\t" + ultraParalogy[i][j] + "\t" + leafJ.label + "\t" + s + "\n");
							write.flush();
							//System.out.println(nbIntersectionDuplications[i][j]);
						} /*else {
							
							phyloXMLBuffer.append("<sequence_relation id_ref_0=\"");
							phyloXMLBuffer.append(leafI.label.substring(0,leafI.label.lastIndexOf("_")));
							phyloXMLBuffer.append("\" id_ref_1=\"");
							phyloXMLBuffer.append(leafJ.label.substring(0,leafJ.label.lastIndexOf("_")));
							if (fitchOrthology[i][j]) {
								phyloXMLBuffer.append("\" type=\"orthology\">\n\t<confidence type=\"rap\">");
							} else {
								phyloXMLBuffer.append("\" type=\"paralogy\">\n\t<confidence type=\"rap\">");
								
							}

							//score computing, very prospective
							double s= 1.0;

							if (reduc>=lengthThreshold) {
								reduc=lengthThreshold;
							}


							double prop=1.0/(double)tree.leafVector.size();
							double reducDis=reduc;
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
							for (int k=0;k<nbUltraParalogy[i][j];k++) {
								s=s*uParaWeight;
							}

							s=s*1000.0;
							int sint= (int)s;
							s= (double)sint/1000.0;

							phyloXMLBuffer.append(s);
							phyloXMLBuffer.append("</confidence>\n</sequence_relation>\n");
							write.write(leafI.label + "\t" + leafI.subtreeNeighbor(TreeReconciler.kLevel) + "\t" + reducDis + "\t" + (nbSpeciations[i][j]+1) + "\t" + nbTopologicalDuplications[i][j] + "\t" + nbIntersectionDuplications[i][j] + "\t" + fitchOrthology[i][j] + "\t" + ultraParalogy[i][j] + "\t" + leafJ.label + "\t" + s + "\n");
							write.flush();							
						}*/
					}


				}


			}

			write.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
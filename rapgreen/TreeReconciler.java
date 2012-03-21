package rapgreen;
import java.util.*;
import java.io.*;


/**
 * Tree reconciliation class.
 * <p>
 * This class manages the 3 entities (species tree, gene tree and reconciled tree), during the reconciliation process.
 * @author Jean-Francois Dufayard
 * @version 1.0
 */
public class TreeReconciler {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************
/**
*	Gene tree branch collapse threshold, in term of branch support, initialized to 0.95
*/
	public static double geneCollapseThreshold=80;

/**
*	Species tree branch collapse threshold, in term of length, initialized to 10.0. No effect if species tree does not have branch lengths
*/
	public static double speciesCollapseThreshold=10.0;

/**
* Depth threshold, under which no duplications are predicted (polymorphism / sequencing error).
*/
	public static double geneDepthThreshold=0.05;

/**
* K-level for subtree neighbor measure.
*/
	public static int kLevel=2;

/**
*	The gene tree
*/
	public Tree geneTree;

/**
*	The species tree
*/
	public Tree speciesTree;

/**
*	The reconciled tree
*/
	public Tree reconciledTree;


// ********************************************************************************************************************
// ***     CONSTRUCTORS      ***
// *****************************
/**
* Standard constructor, from species and gene trees, constructing reconciled tree.
* @param geneTree		The tree representing gene family evolution history
* @param speciesTree	The global species tree
*/
	public TreeReconciler(Tree geneTree, Tree speciesTree) {
		this.geneTree= geneTree;
		this.speciesTree= speciesTree;
		//Pretreat trees
		geneTree.pretreatment();
		speciesTree.heavyPretreatment();
		//Check the existence of taxa in the species tree
		Vector unexistVector= new Vector();
		Hashtable unexistHashtable= new Hashtable();
		for (int i=0;i<geneTree.leafVector.size();i++) {
			Tree leaf= (Tree)(geneTree.leafVector.elementAt(i));
			String tax=leaf.label.substring(leaf.label.lastIndexOf('_')+1,leaf.label.length());
			if (!speciesTree.leafHashtable.containsKey(tax)) {
				if (!unexistHashtable.containsKey(tax)) {
					System.out.println("Warning: taxon " + tax + " not present in species tree. Adding this taxon to the root.");
					unexistVector.addElement(tax);
					unexistHashtable.put(tax,tax);
				}
			}
		}
		if (unexistVector.size()>0) {
			//Add unexisting taxa to the root
			Vector speciesSons= new Vector();
			for (int i=0;i<unexistVector.size();i++) {
				speciesSons.addElement(new Tree(null,(String)(unexistVector.elementAt(i)),-1.0,null));
			}
			speciesSons.addElement(speciesTree);
			speciesTree= new Tree(speciesSons,"null",-1.0,null);
			speciesTree.heavyPretreatment();
		}

		//Remove useless parts of the species tree
		//System.out.println(speciesTree);
		removeUselessSubtrees(speciesTree,geneTree.leafVector);
		//System.out.println(geneTree);
		//Reinitialize the pretreatment of the species tree
		speciesTree.pretreatment();
		//System.out.println("SPECIES:\n" + speciesTree + "\nEND SPECIES TREE");

		//For each possible root
		Vector roots= geneTree.getRootedTrees();
		int maxCost=-1;
		double maxMid=-1.0;
		int maxLoss=-1;
		for (int i=0;i<roots.size();i++) {
			if (RapGreen.verbose) {
				System.out.println("Root " + (i+1) + " on " + roots.size() + " possible roots.");
			}
			Tree rootedGeneTree= (Tree)(roots.elementAt(i));
			rootedGeneTree.pretreatment();
			//System.out.println("gene:\n" + rootedGeneTree);
			double localMid=rootedGeneTree.midpoint();
			//Refine pretreatment tables with parsed taxa labels for gene tree
			parseSpecies(rootedGeneTree);
			//Initialize reconciled tree with species tree topology
			Tree localReconciledTree= new Tree(speciesTree);
			localReconciledTree.pretreatment();
			//Execute the reconciliation recursive method
			int localCost=reconciliation(rootedGeneTree,localReconciledTree);
			int localLoss=localReconciledTree.nbLosses();



			//System.out.println(localCost);
			if (maxCost==-1 || localCost<maxCost || (localCost==maxCost && localLoss<maxLoss) || (localCost==maxCost && localLoss==maxLoss && localMid<maxMid)) {
				maxCost=localCost;
				this.geneTree=rootedGeneTree;
				maxMid=localMid;
				maxLoss=localLoss;
				this.reconciledTree=localReconciledTree;
			} else {
				roots.setElementAt(null,i);
			}

		}
		//System.out.println(maxLoss);
		this.speciesTree= speciesTree;
		//System.out.println(speciesTree.getNewick() + "\n" + geneTree.getNewick() + "\n" + reconciledTree.getNewick() + "\n");
	}

// ********************************************************************************************************************
// ***     CONSTRUCTION PRIVATE METHODS     ***
// ********************************************
/**
*	Parse labels to extract species name, and put it into pretreated structures
*/
	public void parseSpecies(Tree rootedGeneTree) {
		Vector nodes= rootedGeneTree.getNodes();
		for (int i=0;i<nodes.size();i++) {
			//For each node
			Tree node= (Tree)(nodes.elementAt(i));
			if (node.isLeaf()) {
				node.leafHashtable.put(node.label.substring(node.label.lastIndexOf('_')+1,node.label.length()),node);
			} else {
				//Read the leaf vector and add the species sublabel to the leaf hashtable
				for (int j=0;j<node.leafVector.size();j++) {
					Tree leaf= (Tree)(node.leafVector.elementAt(j));
					node.leafHashtable.put(leaf.label.substring(leaf.label.lastIndexOf('_')+1,leaf.label.length()),leaf);
				}
			}
		}
	}

// ********************************************************************************************************************
/**
*	Parse labels to extract species name, and put it into pretreated structures
* @param tree	The species tree to clean
* @param vector	The list of allowed taxa
*/
	public void removeUselessSubtrees(Tree tree,Vector vector) {
		if (!tree.isLeaf()) {

			/*if (tree.label.equals("ORYZA")) {
				System.out.println("okok\n" + tree);
			}*/
			//Internal node case
			//Check if this internal node is contained in usefull taxa
			boolean localyUsefull=false;
			int j=0;
			while (j<vector.size() && !localyUsefull) {
				Tree leaf= (Tree)(vector.elementAt(j));
				//A node is usefull if at least one leaf contains a represented taxon
				if (tree.label.equals(leaf.label.substring(leaf.label.lastIndexOf('_')+1,leaf.label.length()))) {
					localyUsefull=true;
				} else {
					j++;
				}
			}
			if (localyUsefull) {
				tree.sons= null;
				//search for subspecies inclusion, and replace in gene tree
				for (int i=0;i<tree.leafVector.size();i++) {
					Tree leaf= (Tree)(tree.leafVector.elementAt(i));

					for (int k=0;k<vector.size();k++) {
						Tree localLeaf= (Tree)(vector.elementAt(k));
						if (leaf.label.equals(localLeaf.label.substring(localLeaf.label.lastIndexOf('_')+1,localLeaf.label.length()))) {
							//System.out.print(tree.label  + " , " + localLeaf.label);
							localLeaf.label=localLeaf.label.substring(0,localLeaf.label.lastIndexOf('_')) + "_" + tree.label;
							//System.out.println(" ; " + localLeaf.label);
						}
					}
				}

			} else {


				//The invariant is that this node is not useless, so the leaf case consist to do nothing
				Vector usefullSons=new Vector();
				//Reference the usefull sons
				for (int i=0;i<tree.sons.size();i++) {
					Tree son= (Tree)(tree.sons.elementAt(i));
					/*if (son.label.equals("ORYZA")) {
						System.out.println("okdok\n" + tree);

					}*/
					boolean usefull=false;
					j=0;
					while (j<vector.size() && !usefull) {
						//A node is usefull if at least one leaf contains a represented taxon
						Tree leaf= (Tree)(vector.elementAt(j));
						if (son.leafHashtable.containsKey(leaf.label.substring(leaf.label.lastIndexOf('_')+1,leaf.label.length()))) {
							usefull=true;
						} else {
							j++;
						}
					}
					if (usefull) {
						usefullSons.addElement(son);
					}
				}
				if (usefullSons.size()==1) {
					//One usefull son, collapse the son branch
					Tree usefullSon=(Tree)(usefullSons.elementAt(0));
					tree.sons=usefullSon.sons;
					tree.label=usefullSon.label;
					if (tree.length!=-1.0) {
						tree.length=tree.length+usefullSon.length;
					}
					//Remove useless subtrees from this new node
					removeUselessSubtrees(tree,vector);
				} else {
					//Many usefull sons, replace the son list
					tree.sons=usefullSons;
					//Remove useless subtrees of sons.
					for (int i=0;i<tree.sons.size();i++) {
						Tree son= (Tree)(tree.sons.elementAt(i));
						removeUselessSubtrees(son,vector);
					}
				}
			}
		}
	}

// ********************************************************************************************************************
/**
*	Recursive method that reconcile gene and reconciled tree (initialized with species tree topology)
* @param g	The tree representing gene family evolution history
* @param r	The tree to reconcile
* @return	The number of duplications needed.
*/
	public int reconciliation(Tree g, Tree r) {
		int res=0;
		//System.out.println(g);
		if (g.isLeaf()) {
			//The leaf case, no need to reconcile anything. Just annote the correct label at the right reconciled leaf
			Tree leaf = (Tree)(r.leafHashtable.get(g.label.substring(g.label.lastIndexOf("_")+1,g.label.length())));
			//System.out.println(g.label);
			leaf.label=g.label;

		} else if (((Tree)(g.sons.elementAt(0))).maxDepth+((Tree)(g.sons.elementAt(1))).maxDepth<=TreeReconciler.geneDepthThreshold) {
			//System.out.println("$$$");
			//The "polymorphism/sequencing error" case. Stop the reconciliation, just anote the correct labels.
			for (int i=0;i<g.leafVector.size();i++) {
				//System.out.println(r + "\n--\n" + g);
				Tree geneLeaf= (Tree)(g.leafVector.elementAt(i));
				Tree reconciledLeaf = (Tree)(r.leafHashtable.get(geneLeaf.label.substring(geneLeaf.label.lastIndexOf("_")+1,geneLeaf.label.length())));

				reconciledLeaf.label = geneLeaf.label.substring(0,geneLeaf.label.lastIndexOf("_")) + "_" + reconciledLeaf.label;

			}




		} else {
			Vector congruentGeneNodes=new Vector();
			Vector congruentReconciledNodes= new Vector();
			Vector congruentReconciledLengths= new Vector();
			if (areCongruent(g,r,congruentGeneNodes,congruentReconciledNodes,congruentReconciledLengths)) {
				//Congruence case, replace sons and sons lengths, then pursue the reconciliation
				for (int i=0;i<congruentGeneNodes.size();i++) {
					res+=reconciliation((Tree)(congruentGeneNodes.elementAt(i)),advanceInLosses((Tree)(congruentReconciledNodes.elementAt(i))));
				}
			} else {
				//Incongruence case, duplicate the reconciliation structure, then pursue the reconciliation
				g.label="'DUPLICATION"+g.label+"'";
				double newLength=-1.0;
				if (r.length!=-1.0) {
					newLength=r.length/2.0;
				}
				Tree node0= new Tree(r.sons,r.label,newLength,r.nhx);
				Tree node1= new Tree(node0);
				r.label="DUPLICATION";
				Vector newSons=new Vector();
				newSons.addElement(node0);
				newSons.addElement(node1);
				r.length= newLength;
				r.sons=newSons;
				node0.pretreatment();
				node1.pretreatment();
				//Anote leaves
				Tree genenode0=(Tree)(g.sons.elementAt(0));
				Tree genenode1=(Tree)(g.sons.elementAt(1));
				for (int i=0;i<node0.leafVector.size();i++) {
					Tree leaf= (Tree)(node0.leafVector.elementAt(i));
					if (!genenode0.leafHashtable.containsKey(leaf.label)) {
						leaf.label="LOSS";
					}
				}
				for (int i=0;i<node1.leafVector.size();i++) {
					Tree leaf= (Tree)(node1.leafVector.elementAt(i));
					if (!genenode1.leafHashtable.containsKey(leaf.label)) {
						leaf.label="LOSS";
					}
				}
				res+=reconciliation(genenode0,advanceInLosses(node0));
				res+=reconciliation(genenode1,advanceInLosses(node1));
				res++;
			}
		}

		return res;
	}

// ********************************************************************************************************************
/**
*	Return true if the two nodes are congruent.
* @param g							The tree representing gene family evolution history
* @param r							The tree to reconcile
* @param congruentGeneNodes			the list of subnode after reduction in the gene tree
* @param congruentReconciledNodes	the list of subnode after reduction in the reconciled tree
* @param congruentReconciledLengths	the list of cumuled lengths of congruent reconciled nodes
* @return True if the two nodes are congruent
*/
	public boolean areCongruent(Tree g, Tree r, Vector congruentGeneNodes, Vector congruentReconciledNodes, Vector congruentReconciledLengths) {
		//System.out.println("go");
		//System.out.println("**********\n" + g + "\n/////\n" + r + "\n**");
		Vector savedGeneSons= new Vector();
		Vector savedReconciledSons= new Vector();
		//save sons
		for (int i=0;i<g.sons.size();i++) {
			savedGeneSons.addElement(g.sons.elementAt(i));
		}
		if (!r.isLeaf()) {
			for (int i=0;i<r.sons.size();i++) {
				savedReconciledSons.addElement(r.sons.elementAt(i));
				congruentReconciledLengths.addElement(new Double(((Tree)(r.sons.elementAt(i))).length));
			}
		}
		boolean possible=true;
		boolean congruent=false;
		while (!congruent && possible) {
			// While the two trees are not congruent, and it seems still possible to make them congruent
			//System.out.println(g.sons.size() + " ;" + cardinality(r));
			if (!r.isLeaf() && g.sons.size()==cardinality(r)) {
				//trees are congruent, until the proof of the contrary
				congruent=true;
				//define if the two nodes are directly congruents
				//for each fg sons of G, exists fr son of R where taxa(fg)Ctaxa(fr) and taxa(fr)Ctaxa(fg)
				int i=0;
				Tree fg=null;
				while (congruent && i<g.sons.size()) {
					fg=(Tree)(g.sons.elementAt(i));
					congruent=false;
					int j=0;
					while (!congruent && j<r.sons.size()) {
						Tree fr= (Tree)(r.sons.elementAt(j));
						//iteration on leaves
						congruent=areMutuallyIncluded(fg,fr);
						if (congruent) {
							congruentGeneNodes.addElement(fg);
							congruentReconciledNodes.addElement(fr);
						}
						j++;
					}
					i++;
				}
			}
			if (!congruent && !r.isLeaf()) {
				//System.out.println("***\n" + g + "\n/////\n" + r + "\n**");
				//try reduction
				//Delete congruence traces
				while (congruentGeneNodes.size()>0) {
					congruentGeneNodes.removeElementAt(0);
				}
				while (congruentReconciledNodes.size()>0) {
					congruentReconciledNodes.removeElementAt(0);
				}

				//Check the propertie of exclusion in the gene tree sons
				if (sonExclusion(g)) {
					int candidatIndex=findReductionCandidat(g,r);
					if (candidatIndex<-1) {
						//Candidat founded in reconciled tree
						candidatIndex=candidatIndex*(-1)-2;
						Tree removed= (Tree)(r.sons.elementAt(candidatIndex));
						if (removed.length==-1.0 || removed.length<=speciesCollapseThreshold) {
							r.sons.removeElementAt(candidatIndex);
							for (int i=0;i<removed.sons.size();i++) {
								r.sons.addElement(removed.sons.elementAt(i));
							}
						} else {
							possible=false;
						}

					} else if (candidatIndex>-1) {
						//Candidat founded in gene tree
						Tree removed= (Tree)(g.sons.elementAt(candidatIndex));
						double support=0.0;
						try {
							support=(new Double(removed.label)).doubleValue();
						} catch(Exception e) {

						}
						if (support<=geneCollapseThreshold) {
							try {
							g.sons.removeElementAt(candidatIndex);
							for (int i=0;i<removed.sons.size();i++) {
								g.sons.addElement(removed.sons.elementAt(i));
							}
							} catch(Exception e) {
								System.out.println("pan : " + removed);
								e.printStackTrace();
								System.exit(0);
							}
						} else {
							possible=false;
						}

					} else {
						possible=false;
					}
				} else {
					possible=false;
				}
			} else {
				if (!congruent) {
					possible=false;
				}
			}
		}
		g.sons=savedGeneSons;
		if (!congruent && !r.isLeaf()) {
			r.sons=savedReconciledSons;
		}
		//System.out.println(congruent);
		return congruent;
	}
// ********************************************************************************************************************
/**
*	Return true if g respect the condition of son exclusion (mandatory to be congruent to a species tree).
* @param g	The tree representing gene family evolution history
* @return	True g respect the son exclusion, or false otherwise.
*/
	public boolean sonExclusion(Tree g) {
		boolean res=true;
		for (int i=0;res && i<g.sons.size();i++) {
			Tree son1= (Tree)(g.sons.elementAt(i));
			for (int j=i+1;res && j<g.sons.size();j++) {
				Tree son2= (Tree)(g.sons.elementAt(j));
				for (int k=0;res && k<son1.leafVector.size();k++) {
					String label= ((Tree)(son1.leafVector.elementAt(k))).label;
					if (son2.leafHashtable.containsKey((label).substring(label.lastIndexOf("_")+1,label.length()))) {
						res=false;
					}
				}
				for (int k=0;res && k<son2.leafVector.size();k++) {
					String label= ((Tree)(son2.leafVector.elementAt(k))).label;
					if (son1.leafHashtable.containsKey((label).substring(label.lastIndexOf("_")+1,label.length()))) {
						res=false;
					}
				}
			}
		}
		return res;
	}
// ********************************************************************************************************************
/**
*	Return the number of not-loss sons
* @param r	The tree to reconcile
* @return	Number of sons.
*/
	public int cardinality(Tree r) {
		int res=0;
		for (int i=0;i<r.sons.size();i++) {
			Tree son= (Tree)(r.sons.elementAt(i));
			boolean notLoss=false;
			int j=0;
			while (!notLoss && j<son.leafVector.size()) {
				Tree leaf = (Tree)(son.leafVector.elementAt(j));
				if (!leaf.label.equals("LOSS")) {
					notLoss=true;
				}
				j++;
			}
			if (notLoss) {
				res++;
			}
		}
		return res;
	}

// ********************************************************************************************************************
/**
*	Return the index of a possible candidat
* @param g	The tree representing gene family evolution history
* @param r	The tree to reconcile
* @return	Index between positive for gene tree and negative for reconciled tree.
*/
	public int findReductionCandidat(Tree g, Tree r) {
		//System.out.println("***\n" + g + "\n--\n" + r +"***");
		int res=-1;
		boolean founded=false;
		for (int i=0;!founded && i<g.sons.size();i++) {
			Tree sonG= (Tree)(g.sons.elementAt(i));
			int nbIncluded=0;
			int j=0;
			while (nbIncluded<2 && j<r.sons.size()) {
				Tree sonR= (Tree)(r.sons.elementAt(j));
				int k=0;
				boolean foundInclusion=false;
				while (k<sonG.leafVector.size()) {
					String label= ((Tree)(sonG.leafVector.elementAt(k))).label;
					if (sonR.leafHashtable.containsKey((label).substring(label.lastIndexOf("_")+1,label.length()))) {
						foundInclusion=true;
					}
					k++;
				}
				if (foundInclusion) {
					nbIncluded++;
				}

				j++;
			}
			if (nbIncluded>=2) {
				res=i;
				founded=true;
			}
		}
		if (!founded) {
			//System.out.println("foundable in r");
			for (int i=0;!founded && i<r.sons.size();i++) {
				Tree sonR= (Tree)(r.sons.elementAt(i));
				int nbIncluded=0;
				int j=0;
				while (nbIncluded<2 && j<g.sons.size()) {
					Tree sonG= (Tree)(g.sons.elementAt(j));
					int k=0;
					boolean foundInclusion=false;
					while (k<sonR.leafVector.size()) {
						String label= ((Tree)(sonR.leafVector.elementAt(k))).label;
						if (sonG.leafHashtable.containsKey(label)) {
							foundInclusion=true;
						}
						k++;
					}
					if (foundInclusion) {
						nbIncluded++;
					}

					j++;
				}
				if (nbIncluded>=2) {
					res=i*(-1)-2;
					founded=true;
				}
			}
		} else {
			//System.out.println("founded in g " + res);
		}
		if (res>=0 && !g.isLeaf()) {
			int candidatIndex=res;
			Tree remo=(Tree)(g.sons.elementAt(candidatIndex));
			if (remo.sons==null || remo.sons.size()<1) {

				System.out.println("***\n" + g + "\n--\n" + r +"***");
				System.out.println(remo);
			}
		}
		return res;
	}

// ********************************************************************************************************************
/**
*	Return true if the taxa under the two nodes are mutually included
* @param g	The tree representing gene family evolution history
* @param r	The tree to reconcile
* @return	True of the taxa are mutually included, or false otherwise.
*/
	public boolean areMutuallyIncluded(Tree g, Tree r) {
		boolean res=true;
		int i=0;
		while (res && i<g.leafVector.size()) {
			String leafLabel= ((Tree)(g.leafVector.elementAt(i))).label;
			String taxa= leafLabel.substring(leafLabel.lastIndexOf("_")+1,leafLabel.length());
			res=r.leafHashtable.containsKey(taxa);
			i++;
		}
		i=0;
		while (res && i<r.leafVector.size()) {
			String taxa= ((Tree)(r.leafVector.elementAt(i))).label;
			if (!taxa.equals("LOSS"))
				res=g.leafHashtable.containsKey(taxa);
			i++;
		}
		return res;
	}

// ********************************************************************************************************************
/**
*	Return the next not-loss event
* @param r	The tree
* @return	The next not-loss event.
*/
	public Tree advanceInLosses(Tree r) {
		boolean founded=false;
		while (!founded) {
			//System.out.println("---\n" + r);
			if (!r.isLeaf()) {
				int count=0;
				int i=0;
				int last=-1;
				while (i<r.sons.size() && count<2) {
					Tree son= (Tree)(r.sons.elementAt(i));
					int j=0;
					while (j<son.leafVector.size() && ((Tree)(son.leafVector.elementAt(j))).label.equals("LOSS")) {
						j++;
					}
					if (j<son.leafVector.size()) {
						count++;
						last=i;
					}
					i++;
				}
				if (count>1) {
					founded=true;
				} else {
					if (last==-1)
						System.out.println(r);
					r=(Tree)(r.sons.elementAt(last));
				}
				//System.out.println(count);
			} else {
				founded=true;
			}

		}
		return r;
	}

// ********************************************************************************************************************
// ***     OBJECT METHODS     ***
// ******************************


}
package rapgreen;
import java.util.*;


/**
 * Global class of tree management.
 * <p>
 * This class contains every general method appliable to every type of trees, including pretreatment methods and fields to make quick comparisons.
 * <p>
 * A tree is defined recursively by a node. A node can be summered by a label, a length and a list of sons. So, the concept of node and tree are the same in this package.
 * <p>
 * The only supported format is the newick format. To construct trees from others format, please use the TreeReader class.
 * @author Jean-Francois Dufayard
 * @version 1.0
 */
public class Tree {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************
/**
* List of sons of this node. An empty of null list implies that this node is a leaf.
*/
	public Vector sons;

/**
* The label of a node. It can contains evolutionary, taxonomic or support information.
*/
	public String label;

/**
* The length of the branch upper this node.
*/
	public double length;
	public String stringLength;

/**
* The NHX String.
*/
	public String nhx;

/**
* Hashtable containing leaf labels as keys, and leaves as information. Available only after pretreatment.
*/
	public Hashtable leafHashtable;

/**
* Vector containing leaves, in order to quickly be able to access them without step into the tree. Available only after pretreatment.
*/
	public Vector leafVector;

/**
* Precomputed max depth
*/
	public double maxDepth;

/**
* The father of this node. Available only after pretreatment.
*/
	public Tree father;

/**
* True if this node is an ultraparalogy node.
*/
	public boolean ultra;

/**
* Allowed left constraints for tree pattern definition
*/
	public Hashtable allowedLeft;

/**
* Allowed right constraints for tree pattern definition
*/
	public Hashtable allowedRight;

/**
* v left constraints for tree pattern definition
*/
	public Hashtable forbiddenLeft;

/**
* Forbidden right constraints for tree pattern definition
*/
	public Hashtable forbiddenRight;

/**
* Minimum cardinality for tree patter definition
*/
	public int rightCardinalityMin;

/**
* Minimum cardinality for tree patter definition
*/
	public int rightCardinalityMax;

/**
* Allowed species for this pattern node / leaf
*/
	public Vector patternSpecies;

/**
* Existence of right and left constraints
*/
	public boolean hasLeftConstraint=false;
	public boolean hasRightConstraint=false;


// ********************************************************************************************************************
// ***     CONSTRUCTORS     ***
// ****************************
/**
* Generic constructor of a tree, from a newick string
* @param newick	The newick string, encoding the information
*/
	public Tree(String newick) {
		//Create storing objects
		Vector sonsParam= new Vector();
		StringBuffer labelParam= new StringBuffer();
		StringBuffer lengthParam= new StringBuffer();
		StringBuffer nhxParam= new StringBuffer();
		//Read the root node
		int noMatter= ReadNode(0,newick,sonsParam,labelParam,lengthParam,nhxParam);
		//Allocate the fields
		sons=sonsParam;
		label=labelParam.toString();
		nhx=nhxParam.toString();
		if (lengthParam.length()>0) {
			length= (new Double(lengthParam.toString())).doubleValue();
			stringLength= lengthParam.toString();
		} else {
			length= -1.0;
		}
	}
// ********************************************************************************************************************
/**
* Generic constructor of a node, directly from fields
* @param sons	The sons of this node
* @param label	The label of this node
* @param length	The length of the branch upper this node
*/
	public Tree(Vector sons, String label, double length, String nhx) {
		this.sons=sons;
		this.label=label;
		this.length=length;
		this.nhx=nhx;
	}

// ********************************************************************************************************************
/**
* Generic constructor of a node, directly from fields
* @param sons	The sons of this node
* @param label	The label of this node
* @param length	The length of the branch upper this node
*/
	public Tree(Vector sons, String label, double length, String stringLength, String nhx) {
		this.sons=sons;
		this.label=label;
		this.length=length;
		this.stringLength=stringLength;
		this.nhx=nhx;
	}

// ********************************************************************************************************************
/**
* Cloning contructor, clone only basic fields, not the pretreatment fields
* @param source	The node to clone
*/
	public Tree(Tree source) {
		if (!source.isLeaf()) {
			sons= new Vector();
			for (int i=0;i<source.sons.size();i++) {
				sons.addElement(new Tree((Tree)(source.sons.elementAt(i))));
			}
		}
		this.label=new String(source.label);
		this.length=source.length;
		this.nhx=source.nhx;
	}

// ********************************************************************************************************************
// ***     CONSTRUCTION PRIVATE METHODS     ***
// ********************************************
/**
* Private parser of a node, from a starting point in a newick string, to an ending point returned in an Integer object.
* @param starting		The starting index in the newick string
* @param newick			The newick string, encoding the information
* @param sonsParam		The vector used to return the list of sons
* @param labelParam		The buffer used to return the label
* @param lengthParam	The object used to return the length
* @return The ending index, deduced at the end.
*/
	private int ReadNode(int starting, String newick, Vector sonsParam, StringBuffer labelParam, StringBuffer lengthParam, StringBuffer nhxParam) {
		int index=starting;
		if (newick.charAt(index)=='(') {
			//The internal node case
			index++;
			//Create storing objects for sons
			Vector sonsSon= new Vector();
			StringBuffer labelSon= new StringBuffer();
			StringBuffer lengthSon= new StringBuffer();
			StringBuffer nhxSon= new StringBuffer();
			//Get the first son
			index= ReadNode(index,newick,sonsSon,labelSon,lengthSon,nhxSon);
			String nhxLocal=null;
			if (nhxSon.length()>0) {
				nhxLocal=nhxSon.toString();
			}
			if (lengthSon.length()>0 && lengthSon.toString().indexOf("nan")==-1) {
				sonsParam.addElement(new Tree(sonsSon,labelSon.toString(),(new Double(lengthSon.toString())).doubleValue(),lengthSon.toString(),nhxLocal));
			} else {
				sonsParam.addElement(new Tree(sonsSon,labelSon.toString(),-1.0,"-1.0",nhxLocal));
			}
			while (newick.charAt(index)==',') {
				index++;
				//Initialize storing objects for sons
				sonsSon= new Vector();
				labelSon= new StringBuffer();
				lengthSon= new StringBuffer();
				nhxSon= new StringBuffer();
				//Get the next son
				index= ReadNode(index,newick,sonsSon,labelSon,lengthSon,nhxSon);

				nhxLocal=null;
				if (nhxSon.length()>0) {
					nhxLocal=nhxSon.toString();
				}

				if (lengthSon.length()>0) {
					sonsParam.addElement(new Tree(sonsSon,labelSon.toString(),(new Double(lengthSon.toString())).doubleValue(),lengthSon.toString(),nhxLocal));
				} else {
					sonsParam.addElement(new Tree(sonsSon,labelSon.toString(),-1.0,"-1.0",nhxLocal));
				}
			}
			index++;
		}
		//Get the label
		while (newick.charAt(index)!='[' && newick.charAt(index)!=':' && newick.charAt(index)!=',' && newick.charAt(index)!=')' && newick.charAt(index)!=';') {
			labelParam.append(newick.charAt(index));
			index++;
		}
		/*if (labelParam.toString().equals("ORYZA"))
			System.out.println("founded");*/
		if (newick.charAt(index)==':') {
			//A length is specified
			index++;
			while (newick.charAt(index)!='[' && newick.charAt(index)!=',' && newick.charAt(index)!=')' && newick.charAt(index)!=';') {
				lengthParam.append(newick.charAt(index));
				index++;
			}
		}

		if (newick.charAt(index)=='[') {
		// NHX case, stock the Extended Newick information if needed
			index++;
			while (newick.charAt(index)!=']') {
				nhxParam.append(newick.charAt(index));
				index++;
			}
			index++;
			//System.out.println(nhxParam.toString());
		}

		return index;

	}

// ********************************************************************************************************************
// ***     OBJECT METHODS     ***
// ******************************
/**
* Check for each duplication if it is topological or intersection caused
*/
	public void initializeDuplicationNatures() {
		if (!isLeaf()) {
			if (label.startsWith("'DUPLICATION")) {
				Tree son1= (Tree)(sons.elementAt(0));
				Tree son2= (Tree)(sons.elementAt(1));
				int i=0;
				boolean intersection=false;
				while (!intersection && i<son1.leafVector.size()) {
					Tree leaf= (Tree)(son1.leafVector.elementAt(i));
					if (son2.leafHashtable.containsKey(leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length()))) {
						intersection=true;
					}
					i++;
				}
				if (ultra) {
					//System.out.println("echo");
					label = "'U_" + label.substring(1,label.length());

				} else {
					if (intersection) {
						label = "'I_" + label.substring(1,label.length());
					} else {
						label = "'T_" + label.substring(1,label.length());
					}
				}
			}
			for (int i=0;i<sons.size();i++) {
				((Tree)(sons.elementAt(i))).initializeDuplicationNatures();
			}
			//System.out.println(label);

		}
	}

// ********************************************************************************************************************
/**
* Clean labels, in order to fit the format P/S(trace)support
*/
	public void formatLabelsWithTrace() {
		if (!isLeaf()) {
			if (label.startsWith("'DUPLICATION")) {
				label= "'D_" + trace() + "_'";
			} else {
				label= "";
			}

			if (((Tree)(sons.elementAt(0))).isLeaf() && !((Tree)(sons.elementAt(1))).isLeaf()) {
				if (((Tree)(sons.elementAt(1))).label.startsWith("'D")) {
					((Tree)(sons.elementAt(1))).label="'DUPLICATION'";;
				} else {
					((Tree)(sons.elementAt(1))).label="";
				}
			}
			if (((Tree)(sons.elementAt(1))).isLeaf() && !((Tree)(sons.elementAt(0))).isLeaf()) {
				if (((Tree)(sons.elementAt(0))).label.startsWith("'D")) {
					((Tree)(sons.elementAt(0))).label="'DUPLICATION'";
				} else {
					((Tree)(sons.elementAt(0))).label="";
				}
			}
			for (int i=0;i<sons.size();i++) {
				((Tree)(sons.elementAt(i))).formatInternalLabelsWithTrace();
			}
		}
	}

// ******************************
	private void formatInternalLabelsWithTrace() {
		if (!isLeaf()) {
			if (label.startsWith("'DUPLICATION")) {
				label= "'D_" + trace() + "_" + label.substring(12,label.length());
			}
			for (int i=0;i<sons.size();i++) {
				((Tree)(sons.elementAt(i))).formatInternalLabelsWithTrace();
			}
		}
	}

// ********************************************************************************************************************
/**
* Compute the species vector of this node
* @return		The species vector
*/

	public Vector speciesVector() {
		Vector totalV = new Vector();
		Hashtable totalH= new Hashtable();
		Vector soloV= new Vector();

		Hashtable table0= new Hashtable();
		Vector vector0= ((Tree)(sons.elementAt(0))).leafVector;
		Hashtable table1= new Hashtable();
		Vector vector1= ((Tree)(sons.elementAt(1))).leafVector;
		for (int i=0;i<vector0.size();i++) {
			String leaf= ((Tree)(vector0.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			if (!leaf.startsWith("LOSS")) {
				table0.put(taxa,"");
			}
		}
		for (int i=0;i<vector1.size();i++) {
			String leaf= ((Tree)(vector1.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			if (!leaf.startsWith("LOSS")) {
				table1.put(taxa,"");
			}
		}
		for (int i=0;i<vector0.size();i++) {
			String leaf= ((Tree)(vector0.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			if (!leaf.startsWith("LOSS")) {

				if (!totalH.containsKey(taxa)) {
					totalV.addElement(taxa);
					totalH.put(taxa,"");
					if (table1.containsKey(taxa)) {
						soloV.addElement(taxa);
					}
				}
			}
		}
		for (int i=0;i<vector1.size();i++) {
			String leaf= ((Tree)(vector1.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			if (!leaf.startsWith("LOSS")) {
					if (!totalH.containsKey(taxa)) {
					totalV.addElement(taxa);
					totalH.put(taxa,"");
					if (table0.containsKey(taxa)) {
						soloV.addElement(taxa);
					}
				}
			}
		}
		return(totalV);
	}

// ********************************************************************************************************************
/**
* Compute the species intersection of this node
* @return		The duplication species intersection
*/

	public Vector traceVector() {
		Vector totalV = new Vector();
		Hashtable totalH= new Hashtable();
		Vector soloV= new Vector();

		Hashtable table0= new Hashtable();
		Vector vector0= ((Tree)(sons.elementAt(0))).leafVector;
		Hashtable table1= new Hashtable();
		Vector vector1= ((Tree)(sons.elementAt(1))).leafVector;
		for (int i=0;i<vector0.size();i++) {
			String leaf= ((Tree)(vector0.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			if (!leaf.startsWith("LOSS")) {
				table0.put(taxa,"");
			}
		}
		for (int i=0;i<vector1.size();i++) {
			String leaf= ((Tree)(vector1.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			if (!leaf.startsWith("LOSS")) {
				table1.put(taxa,"");
			}
		}
		for (int i=0;i<vector0.size();i++) {
			String leaf= ((Tree)(vector0.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			if (!leaf.startsWith("LOSS")) {

				if (!totalH.containsKey(taxa)) {
					totalV.addElement(taxa);
					totalH.put(taxa,"");
					if (table1.containsKey(taxa)) {
						soloV.addElement(taxa);
					}
				}
			}
		}
		for (int i=0;i<vector1.size();i++) {
			String leaf= ((Tree)(vector1.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			if (!leaf.startsWith("LOSS")) {
					if (!totalH.containsKey(taxa)) {
					totalV.addElement(taxa);
					totalH.put(taxa,"");
					if (table0.containsKey(taxa)) {
						soloV.addElement(taxa);
					}
				}
			}
		}
		return(soloV);
	}

// ********************************************************************************************************************
/**
* Compute the trace of a duplication node (intersection / union of species under the two sons)
* @return		The duplication trace
*/

	public double trace() {
		Vector totalV = new Vector();
		Hashtable totalH= new Hashtable();
		Vector soloV= new Vector();

		Hashtable table0= new Hashtable();
		Vector vector0= ((Tree)(sons.elementAt(0))).leafVector;
		Hashtable table1= new Hashtable();
		Vector vector1= ((Tree)(sons.elementAt(1))).leafVector;
		for (int i=0;i<vector0.size();i++) {
			String leaf= ((Tree)(vector0.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			table0.put(taxa,"");
		}
		for (int i=0;i<vector1.size();i++) {
			String leaf= ((Tree)(vector1.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			table1.put(taxa,"");
		}
		for (int i=0;i<vector0.size();i++) {
			String leaf= ((Tree)(vector0.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			if (!totalH.containsKey(taxa)) {
				totalV.addElement(taxa);
				totalH.put(taxa,"");
				if (table1.containsKey(taxa)) {
					soloV.addElement(taxa);
				}
			}
		}
		for (int i=0;i<vector1.size();i++) {
			String leaf= ((Tree)(vector1.elementAt(i))).label;
			String taxa= leaf.substring(leaf.lastIndexOf("_")+1,leaf.length());
			if (!totalH.containsKey(taxa)) {
				totalV.addElement(taxa);
				totalH.put(taxa,"");
				if (table0.containsKey(taxa)) {
					soloV.addElement(taxa);
				}
			}
		}
		/*System.out.print((((double)(soloV.size()))/((double)(totalV.size()))) + " ; ");
		for (int i=0;i<soloV.size();i++) {
			System.out.print(soloV.elementAt(i) + " ");
		}
			System.out.print(",");
		for (int i=0;i<totalV.size();i++) {
			System.out.print(totalV.elementAt(i) + " ");
		}
			System.out.print("\n");*/
		double res = ((double)(soloV.size()))/((double)(totalV.size()));
		res=res*1000.0;
		int resInt= (int)res;
		res=(double)resInt;
		res=res/1000.0;
		return(res);
	}

// ********************************************************************************************************************
/**
* @param leaf	The leaf to starts from
* @return		The number of topological incongruence duplication
*/
	public int nbIntersectionDuplications(Tree leaf) {
		int res=0;
		while (leaf!=this) {
			boolean included=false;
			if (leaf.label.startsWith("'I_DUPLICATION")) {
				res+=1;
			}
			leaf=leaf.father;
		}

		return res;

	}

// ********************************************************************************************************************
/**
* @param leaf	The leaf to starts from
* @return		The number of topological incongruence duplication
*/
	public int nbTopologicalDuplications(Tree leaf) {
		int res=0;
		while (leaf!=this) {
			boolean included=false;
			//System.out.println(leaf.label + " " + leaf.label.startsWith("'T_DUPLICATION"));
			if (leaf.label.startsWith("'T_DUPLICATION")) {
				res+=1;
			}
			leaf=leaf.father;
		}

		return res;

	}

// ********************************************************************************************************************
/**
* @param leaf	The leaf to starts from
* @return		The number of ultraparalogy nodes
*/
	public int nbUltra(Tree leaf) {
		int res=0;
		while (leaf!=this) {
			boolean included=false;
			//System.out.println(leaf.label + " " + leaf.label.startsWith("'T_DUPLICATION"));
			if (leaf.ultra) {
				res+=1;
			}
			leaf=leaf.father;
		}
		if (ultra) res+=1;
		return res;

	}

// ********************************************************************************************************************
/**
* Return an array of species
* @return		The represented species
*/
	public Vector nbSpecies() {
		Vector res= new Vector();
		Hashtable table= new Hashtable();
		for (int i=0;i<leafVector.size();i++) {
			String leafLabel= ((Tree)(leafVector.elementAt(i))).label;
			String taxon= leafLabel.substring(leafLabel.lastIndexOf("_")+1,leafLabel.length());
			if (!table.containsKey(taxon)) {
				table.put(taxon," ");
				res.addElement(taxon);
			}
		}

		return res;

	}

// ********************************************************************************************************************
/**
* @param leaf	The leaf to starts from
* @return		The number of speciation nodes
*/
	public int nbSpeciations(Tree leaf) {
		int res=0;
		while (leaf!=this) {
			boolean included=false;
			//System.out.println(leaf.label + " " + leaf.label.startsWith("'T_DUPLICATION"));
			if (!leaf.isLeaf() && !leaf.label.contains("DUPLICATION")) {
				res+=1;
			}
			leaf=leaf.father;
		}

		return res;

	}

// ********************************************************************************************************************
/**
* The subtree neighbor measure. The tree must be pretreated.
* @param level	The k-level
* @return		The k-value
*/
	public int subtreeNeighbor(int level) {
		int i=0;
		Tree jogger=this;
		while (jogger.father!=null && i<level) {
			jogger=jogger.father;
			i++;
		}
		return (jogger.leafVector.size()-1);

	}

// ********************************************************************************************************************
/**
* Fill a vector with every depths of each leaf of the tree, from this node, adding save
* @param res	the vector to fill
* @param save	the value to add, 0.0 if executed from the root
*/
	public void getDepths(Vector res,double save) {
		if (!isLeaf()) {
			for (int i=0;i<sons.size();i++) {
				Tree son= (Tree)(sons.elementAt(i));
				son.getDepths(res,save+son.length);
			}
		} else {
			res.addElement(new Double(save));
		}
	}
// ********************************************************************************************************************
/**
* @param s	The full species tree
* @return	The linked species tree node, LSA of this tree species
*/
	public Tree speciesMapping(Tree s) {
		Tree res=null;
		if (s.isLeaf()) {
			res=s;

		} else {
			Vector hitNodes= new Vector();
			for (int i=0;i<s.sons.size();i++) {
				Tree son= (Tree)(s.sons.elementAt(i));
				boolean hit= false;
				int j=0;
				while (!hit && j<leafVector.size()) {
					Tree leaf= (Tree)(leafVector.elementAt(j));
					//System.out.println(leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.lastIndexOf("_")+6));
					if (son.leafHashtable.containsKey(leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length()))) {
						//System.out.println("true");
						hit=true;
					}
					j++;
				}
				if (hit) {
					hitNodes.addElement(son);
				}


			}


			if (hitNodes.size()>1) {
				res=s;
			} else if (hitNodes.size()==0) {
				System.out.println("\nwarning\n" + this + s);
			} else {
				res=this.speciesMapping((Tree)(hitNodes.elementAt(0)));
			}
		}
		return res;

	}
// ********************************************************************************************************************
/**
* The subtree of a species tree, from this tree and a species list.
* @param species	the species table
* @return		the newly created subtree
*/
	public Tree subtree(Hashtable species) {
		Tree res=null;
		if (isLeaf()) {
			if (species.containsKey(label)) {
				res=new Tree(this);
			}
		} else {
			Vector subSons= new Vector();
			for (int i=0;i<sons.size();i++) {
				Tree localNode= ((Tree)(sons.elementAt(i))).subtree(species);
				if (localNode!=null) {
					subSons.addElement(localNode);
				}
			}
			if (subSons.size()==1) {
				res=(Tree)(subSons.elementAt(0));
			} else if (subSons.size()>1) {
				res=new Tree(subSons,label,length,nhx);
			}
		}

		return res;
	}

// ********************************************************************************************************************
/**
* The subtree of a species tree, from this tree and a species list.
* @param allRes	vector to fill
* @param localRes	must be initialised to new Vector
*/
	public void fillOrthologs(Vector allRes, Vector allDepth, Vector localRes) {
		if (isLeaf()) {
			localRes.addElement(this);
		} else {
			if (this.label.indexOf("D_")!=-1 || this.label.indexOf("DUPLICATION")!=-1) {
				for (int i=0;i<sons.size();i++) {
					Tree son= (Tree)(sons.elementAt(i));
					son.fillOrthologs(allRes,allDepth, new Vector());
				}
			} else {
				if (this.father==null || this.father.label.indexOf("D_")!=-1 || this.father.label.indexOf("DUPLICATION")!=-1) {

					for (int i=0;i<sons.size();i++) {
						Tree son= (Tree)(sons.elementAt(i));
						son.fillOrthologs(allRes, allDepth, localRes);
					}
					if (localRes.size()>1) {
						allRes.addElement(localRes);
					}
					Vector localDepth= new Vector();
					for (int i=0;i<localRes.size();i++) {
						double depth=0.0;
						Tree leaf= (Tree)(localRes.elementAt(i));
						while (leaf!=this) {
							if (leaf.length>0.0) {
								depth+=leaf.length;
							}
							leaf=leaf.father;
						}
						localDepth.addElement(new Double(depth));
					}
					if (localRes.size()>1) {
						allDepth.addElement(localDepth);
					}
				} else {
					for (int i=0;i<sons.size();i++) {
						Tree son= (Tree)(sons.elementAt(i));
						son.fillOrthologs(allRes, allDepth, localRes);
					}
				}
			}


		}

	}

// ********************************************************************************************************************
/**
* Fill a vector with every losses node of this tree.
* @param vect	the vector to fill-
*/
	public void getLosses(Vector vect) {
		if (isLeaf()) {
			if (label.indexOf("LOSS")!=-1) {
				vect.addElement(this);
			}
		} else {
			boolean isLoss=true;
			for (int i=0;i<leafVector.size() && isLoss;i++) {
				Tree leaf= (Tree)(leafVector.elementAt(i));
				if (leaf.label.indexOf("LOSS")==-1) {
					isLoss=false;
				}
			}
			if (isLoss) {
				vect.addElement(this);
			} else {
				for (int i=0;i<sons.size();i++) {
					Tree son= (Tree)(sons.elementAt(i));
					son.getLosses(vect);
				}
			}
		}
	}

// ********************************************************************************************************************
/**
* Fill a vector with every speciations of leaves node of this tree.
* @param vect	the vector to fill-
*/
	public void getSpeciations(Vector vect) {
		if (!isLeaf()) {
			if (!this.label.startsWith("D") && !this.label.startsWith("'D") && !this.label.startsWith("-D") && !this.label.startsWith("-'D") && !this.label.startsWith("'-D") && !this.label.startsWith("T") && !this.label.startsWith("'T") && !this.label.startsWith("-T") && !this.label.startsWith("-'T") && !this.label.startsWith("'-T")) {
				vect.addElement(this);
			}
			for (int i=0;i<sons.size();i++) {
				Tree son= (Tree)(sons.elementAt(i));
				son.getSpeciations(vect);
			}
		} else {
			vect.addElement(this);
		}
	}

// ********************************************************************************************************************
/**
* Fill a vector with every duplication node of this tree.
* @param vect	the vector to fill-
*/
	public void getDuplications(Vector vect) {
		if (!isLeaf()) {
			if (this.label.startsWith("D") || this.label.startsWith("'D") || this.label.startsWith("-D") || this.label.startsWith("-'D") || this.label.startsWith("'-D")) {
				vect.addElement(this);
			}
			for (int i=0;i<sons.size();i++) {
				Tree son= (Tree)(sons.elementAt(i));
				son.getDuplications(vect);
			}
		}
	}

// ********************************************************************************************************************
/**
* Return the node corresponding to length and label
* @return The target node
*/
	public Tree getNode(double targetLength, String targetLabel) {
		Tree res=null;
		if (isLeaf()) {
			if (targetLength==length && (targetLabel==null || targetLabel.indexOf(label)!=-1 || label.indexOf(targetLabel)!=-1)) {
				res=this;
			}
		} else {
			if (targetLength==length && (targetLabel==null || targetLabel.indexOf(label)!=-1 || label.indexOf(targetLabel)!=-1)) {
				res=this;
			} else {
				int i=0;
				while (res==null && i<sons.size()) {
					Tree son= (Tree)(sons.elementAt(i));
					res= son.getNode(targetLength,targetLabel);
					i++;

				}
			}
		}
		return res;
	}

// ********************************************************************************************************************
/**
* Return the number of losses of tree (LOSS label), reducing the number with internal losses
* @return The number of losses
*/
	public int nbLosses() {
		int res= nbLossesRecursive();
		if (res==-1)
			res=1;
		return res;
	}

// ******************************
/**
* Recursive version, using -1 tu communicate with public version
* @return The number of losses, or -1 if it is a full loss tree part.
*/
	private int nbLossesRecursive() {
		int res=0;
		if (isLeaf()) {
			if (label.equals("LOSS")) {
				res=-1;
			}
		} else {
			boolean fullLoss=true;
			int sum=0;
			for (int i=0;i<sons.size();i++) {
				Tree son= (Tree)(sons.elementAt(i));
				int localRes= son.nbLossesRecursive();
				if (localRes!=-1) {
					sum+=localRes;
					fullLoss=false;
				} else {
					sum+=1;
				}
			}
			if (fullLoss) {
				res=-1;
			} else {
				res=sum;
			}
		}
		return res;
	}

// ********************************************************************************************************************
/**
* Standard string conversion method
* @return The string translation.
*/
	public String toString() {
		StringBuffer res= new StringBuffer();
		toString(0,res);
		return(res.toString());
	}

// ******************************
/**
* Standard string conversion method
* @param tab			The number of tabulations
* @param lengthParam	The buffer used to store the result
*/
	private void toString(int tab,StringBuffer res) {
		for (int i=0;i<tab;i++) {
			res.append(".");
		}
		res.append("---");
		res.append(length);
		res.append("--- ");
		res.append(label);
		res.append("--- ");
		res.append(nhx);
		res.append("\n");
		if (!isLeaf()) {
			for (int i=0;i<sons.size();i++) {
				((Tree)(sons.elementAt(i))).toString(tab+1,res);
			}
		}
	}
// ********************************************************************************************************************
/**
* Add speciation events into every node
* @return The string translation.
*/
	public void addSpeciations() {
		if (!isLeaf()) {
			label="S_"+label;
			for (int i=0;i<sons.size();i++) {
				((Tree)(sons.elementAt(i))).addSpeciations();
			}
		}
	}

// ********************************************************************************************************************
	private Vector getTransferTarget() {
		String[] elements= label.split("_");
		Vector res= new Vector();

		Tree leaf0= (Tree)(sons.elementAt(0));
		String[] elements0=leaf0.label.split("_");
		Tree leaf1= (Tree)(sons.elementAt(1));
		String[] elements1=leaf1.label.split("_");

		if ((leaf0.isLeaf() && elements[2].equals(elements0[elements0.length-1])) || (!leaf0.isLeaf() && elements[2].equals(elements0[1]))) {
			res.addElement(leaf0);
			res.addElement(leaf1);
		} else {
			res.addElement(leaf1);
			res.addElement(leaf0);

		}

		return res;
	}

// ********************************************************************************************************************
/**
* Tree pattern detection
* @param pattern	The pattern to detect
* @param ind		The species index
* @param dic		The species dictionary
* @return True if the pattern is in the tree, false overthise
*/
// pattern example: ((THACI1,(PITOR1,THACI1)T)S,LOSS|THACI1|PITOR1)D
		public boolean containsPattern(Tree pattern, Hashtable ind, SpeciesDictionary dic) {
			boolean res=false;
			//System.out.println("pattern:\n" + pattern + "\n\ntree:\n" + this + "\n");
			boolean possible=true;
			for (int i=0;possible && i<pattern.leafVector.size();i++) {
				Tree leaf= (Tree)(pattern.leafVector.elementAt(i));

				//System.out.println(leaf.label + " " + leafHashtable.containsKey(leaf.label));
				//System.out.println("echo1");
				if (leaf.patternSpecies.size()<=leafVector.size()) {
					//System.out.println(leaf.patternSpecies.size());
					possible=false;
					for (int j=0;!possible && j<leaf.patternSpecies.size();j++) {
						//System.out.println(leaf.patternSpecies.size());
						String localLabel=(String)(leaf.patternSpecies.elementAt(j));
						//System.out.println(localLabel);
						if (leafHashtable.containsKey(localLabel)) {
							possible=true;
						}
						//System.out.println(possible);

					}
				} else {
					//System.out.println("echo3");
					possible=false;
					for (int j=0;!possible && j<leafVector.size();j++) {
						Tree thisLeaf= (Tree)(leafVector.elementAt(j));
						if (dic.isCompatible(thisLeaf.label,leaf.allowedRight,leaf.forbiddenRight)) {
							possible=true;
						}
					}
				}
			}

			/*if (!possible) {

				System.out.println("NOT POSSIBLE 1");
				for (int i=0;i<pattern.leafVector.size();i++) {
					Tree leaf= (Tree)(pattern.leafVector.elementAt(i));


					possible=false;
					for (int j=0;!possible && j<leafVector.size();j++) {
						Tree thisLeaf= (Tree)(leafVector.elementAt(j));
						if (dic.isCompatible(thisLeaf,leaf.allowedLeft,leaf.forbiddenLeft)) {
							possible=true;
						}
						System.out.println(leaf.label + " " + thisLeaf.label + " " + dic.isCompatible(thisLeaf,leaf.allowedLeft,leaf.forbiddenLeft));
					}
				}
			} else {
				System.out.println("possible 1");
			}*/

			if (possible) {
				if (pattern.label.startsWith("LOSS")) {
					if (label.startsWith("L_")) {
						//String[] lost=pattern.label.split("|");
						res=true;
						/*String[] refLost=taxon.split("|");
						res=true;
						Hashtable speciesTable= new Hashtable();
						for (int i=1;i<refLost.length;i++) {
							speciesTable.put(refLost[i],"ok");
						}
						res=true;
						for (int i=1;i<lost.length && res;i++) {
							if (!speciesTable.containsKey(lost[i])) {
								res=false;
							}
						}*/
					} else {
						Tree sonsZero= (Tree)(sons.elementAt(0));
						Tree sonsOne= (Tree)(sons.elementAt(1));
						if ((pattern.length==4.0 || pattern.length==2.0 || pattern.length==-1.0 || !label.startsWith("D_")) && (pattern.length==1.0 || pattern.length==-1.0 || !label.startsWith("T_"))) {
							res= sonsZero.containsPattern(pattern,ind,dic) || sonsOne.containsPattern(pattern,ind,dic);
						}
					}


				} else if (pattern.isLeaf()) {
					if (isLeaf()) {
						//System.out.println(this);
						//res=label.contains(pattern.label);
						res=(!hasLeftConstraint || dic.isCompatible(label,pattern.allowedLeft,pattern.forbiddenLeft)) && dic.isCompatible(label,pattern.allowedRight,pattern.forbiddenRight);
					} else {
					//System.out.print(pattern + "**" + this);
						Tree sonsZero= (Tree)(sons.elementAt(0));
						Tree sonsOne= (Tree)(sons.elementAt(1));

						if (!pattern.hasLeftConstraint || dic.isCompatible(this,pattern.allowedLeft,pattern.forbiddenLeft)) {
							if ((pattern.length!=4.0 || !!label.startsWith("T_") && !label.startsWith("D_")) && (pattern.length==4.0 || pattern.length==2.0 || pattern.length==-1.0 || !label.startsWith("D_")) && (pattern.length==4.0 || pattern.length==1.0 || pattern.length==-1.0 || !label.startsWith("T_"))) {
						//System.out.println("inter");
								res= sonsZero.containsPattern(pattern,ind,dic) || sonsOne.containsPattern(pattern,ind,dic);
							}
						}
						//System.out.println(res);
					}
				} else {
					if (!isLeaf()) {

					//if (pattern.nbLeaves()==2 && this.nbLeaves()==2) {System.out.println("control " + pattern.rightCardinalityMax + " " + pattern.rightCardinalityMin);}
					// if sons=null, the pattern cannot be contained
						Tree sonsZero= (Tree)(sons.elementAt(0));
						Tree sonsOne= (Tree)(sons.elementAt(1));
						Tree patternSonsZero= (Tree)(pattern.sons.elementAt(0));
						Tree patternSonsOne= (Tree)(pattern.sons.elementAt(1));
						//System.out.println("trace\n" + this);
						if ((pattern.rightCardinalityMax==-1 || nbLeaves()<=pattern.rightCardinalityMax) && (pattern.rightCardinalityMin==-1 || nbLeaves()>=pattern.rightCardinalityMin)) {
							if ((!pattern.hasLeftConstraint || dic.isCompatible(this,pattern.allowedLeft,pattern.forbiddenLeft)) && (!pattern.hasRightConstraint || dic.isCompatible(this,pattern.allowedRight,pattern.forbiddenRight))) {
							//if (pattern.nbLeaves()==2 && this.nbLeaves()==2) {System.out.println("echo1");}
								//System.out.println("rest2 " + label + " " + pattern.label);
								boolean neutralTransfer=false;
								if (pattern.label.startsWith("T") && pattern.nhx.indexOf("<T>")!=-1 && (new Integer(pattern.nhx.substring(pattern.nhx.indexOf("<T>")+3,pattern.nhx.indexOf("</T>")))).intValue()==2) {

							//if (pattern.nbLeaves()==2 && this.nbLeaves()==2) {System.out.println("echo2");}
									neutralTransfer=true;
								}
								if (!neutralTransfer && label.startsWith("T_") && pattern.label.startsWith("T")) {

							//if (pattern.nbLeaves()==2 && this.nbLeaves()==2) {System.out.println("echo3");}
									Vector sens=getTransferTarget();

									int patternSens= (new Integer(pattern.nhx.substring(pattern.nhx.indexOf("<T>")+3,pattern.nhx.indexOf("</T>")))).intValue();

									Tree patternTarget=null;
									Tree patternSource=null;

									if (patternSens==0) {
										patternTarget=(Tree)(pattern.sons.elementAt(0));
										patternSource=(Tree)(pattern.sons.elementAt(1));
									} else {
										patternTarget=(Tree)(pattern.sons.elementAt(1));
										patternSource=(Tree)(pattern.sons.elementAt(0));
									}

									//System.out.println("control4");
									Tree target=(Tree)(sens.elementAt(0));
									Tree source=(Tree)(sens.elementAt(1));
									res= (target.containsPattern(patternTarget,ind,dic) && source.containsPattern(patternSource,ind,dic) && (!patternTarget.hasLeftConstraint || dic.isCompatible(target,patternTarget.allowedLeft,patternTarget.forbiddenLeft)) && (!patternSource.hasLeftConstraint || dic.isCompatible(source,patternSource.allowedLeft,patternSource.forbiddenLeft)));
									//System.out.println(res);
								} else {
									if ((neutralTransfer && label.startsWith("T_") && pattern.label.startsWith("T")) || (label.startsWith("D_") && pattern.label.startsWith("D")) || (!label.startsWith("T_") && !label.startsWith("D_") && pattern.label.startsWith("S"))) {

							//if (pattern.nbLeaves()==2 && this.nbLeaves()==2) {System.out.println("echo4");}

										res= (sonsZero.containsPattern(patternSonsZero,ind,dic) && sonsOne.containsPattern(patternSonsOne,ind,dic) && (!patternSonsZero.hasLeftConstraint || dic.isCompatible(sonsZero,patternSonsZero.allowedLeft,patternSonsZero.forbiddenLeft)) && (!patternSonsOne.hasLeftConstraint || dic.isCompatible(sonsOne,patternSonsOne.allowedLeft,patternSonsOne.forbiddenLeft))) || (sonsZero.containsPattern(patternSonsOne,ind,dic) && sonsOne.containsPattern(patternSonsZero,ind,dic) && (!patternSonsOne.hasLeftConstraint || dic.isCompatible(sonsZero,patternSonsOne.allowedLeft,patternSonsOne.forbiddenLeft)) && (!patternSonsZero.hasLeftConstraint || dic.isCompatible(sonsOne,patternSonsZero.allowedLeft,patternSonsZero.forbiddenLeft)));
							//System.out.println(sonsZero + "---\n" + patternSonsZero + sonsZero.containsPattern(patternSonsZero,ind,dic));
							//System.out.println(sonsOne.containsPattern(patternSonsOne,ind,dic));

									}
								}
							}
						} else {
							//if (pattern.nbLeaves()==2 && this.nbLeaves()==2) {System.out.println("loose man");}
							//System.out.println("card=" + pattern.rightCardinalityMin + " leaves=" + nbLeaves() + "\n" + this);
						}
						if (!res) {
//if (pattern.nbLeaves()==2 && this.nbLeaves()==2) {System.out.println("echo5");}
							if (!pattern.hasLeftConstraint || dic.isCompatible(this,pattern.allowedLeft,pattern.forbiddenLeft)) {
								if ((pattern.length!=4.0 || label.startsWith("D_")) && (pattern.length==4.0 || pattern.length==2.0 || pattern.length==-1.0 || !label.startsWith("D_")) && (pattern.length==1.0 || pattern.length==-1.0 || !label.startsWith("T_"))) {
									res= sonsZero.containsPattern(pattern,ind,dic) || sonsOne.containsPattern(pattern,ind,dic);
								}
							}
						}

					}
				}
			}

			/*if (res) {
				coloring=true;
			}*/
			return res;
		}
// ********************************************************************************************************************
/**
* Tree pattern coloration
* @param pattern	The pattern to detect
* @param ind		The species index
* @param dic		The species dictionary
* @return True if the pattern is in the tree, false overthise
*/
// pattern example: ((THACI1,(PITOR1,THACI1)T)S,LOSS|THACI1|PITOR1)D
		public boolean colorPattern(Tree pattern, Tree rootPattern, Hashtable ind, SpeciesDictionary dic, Vector colored, Hashtable stickers) {
			boolean res=false;
			// test if the pattern taxa are present in this tree part
			boolean possible=true;
			for (int i=0;possible && i<pattern.leafVector.size();i++) {
				Tree leaf= (Tree)(pattern.leafVector.elementAt(i));
				if (leaf.patternSpecies.size()<=leafVector.size()) {
					//System.out.println(leaf.patternSpecies.size());
					possible=false;
					for (int j=0;!possible && j<leaf.patternSpecies.size();j++) {
						//System.out.println(leaf.patternSpecies.size());
						String localLabel=(String)(leaf.patternSpecies.elementAt(j));
						//System.out.println(localLabel);
						if (leafHashtable.containsKey(localLabel)) {
							possible=true;
						}
						//System.out.println(possible);

					}
				} else {
					//System.out.println(leaf.label + " " + leafHashtable.containsKey(leaf.label));

					/*if (!leaf.label.startsWith("LOSS") && leaf.isSimpleRight && !leafHashtable.containsKey(leaf.label)) {
						possible=false;
					}*/
					possible=false;
					for (int j=0;!possible && j<leafVector.size();j++) {
						Tree thisLeaf= (Tree)(leafVector.elementAt(j));
						if (dic.isCompatible(thisLeaf.label,leaf.allowedRight,leaf.forbiddenRight)) {
							possible=true;
						}
					}
				}
			}

			/*if (!possible) {
				System.out.println("NOT POSSIBLE 2");
			}*/
			if (possible) {
				if (pattern.label.startsWith("LOSS")) {
					if (label.startsWith("L_")) {
						//String[] lost=pattern.label.split("|");
						res=true;
						/*String[] refLost=taxon.split("|");
						res=true;
						Hashtable speciesTable= new Hashtable();
						for (int i=1;i<refLost.length;i++) {
							speciesTable.put(refLost[i],"ok");
						}
						res=true;
						for (int i=1;i<lost.length && res;i++) {
							if (!speciesTable.containsKey(lost[i])) {
								res=false;
							}
						}*/

						colored.addElement(this);
					} else {
						Tree sonsZero= (Tree)(sons.elementAt(0));
						Tree sonsOne= (Tree)(sons.elementAt(1));
						if ((pattern.length!=4.0 || !!label.startsWith("T_") && !label.startsWith("D_")) && (pattern.length==4.0 || pattern.length==2.0 || pattern.length==-1.0 || !label.startsWith("D_")) && (pattern.length==4.0 || pattern.length==1.0 || pattern.length==-1.0 || !label.startsWith("T_"))) {
							Vector localColored= new Vector();
							res= sonsZero.colorPattern(pattern,rootPattern,ind,dic,localColored,stickers);
							if (res) {
								for (int i=0;i<localColored.size();i++) {
									colored.addElement(localColored.elementAt(i));
								}


							} else {
								localColored= new Vector();
								res= sonsOne.colorPattern(pattern,rootPattern,ind,dic,localColored,stickers);
								if (res) {
									for (int i=0;i<localColored.size();i++) {
										colored.addElement(localColored.elementAt(i));
									}

								}
							}
							if (res && rootPattern!=pattern) {
								colored.addElement(this);
							}
						}
					}


				} else if (pattern.isLeaf()) {
					if (isLeaf()) {
						//res=label.contains(pattern.label);
						//System.out.println("ECHO:" + this + " " + dic.isCompatible(label,pattern.allowedRight,pattern.forbiddenRight));
						res=(!hasLeftConstraint || dic.isCompatible(label,pattern.allowedLeft,pattern.forbiddenLeft)) && dic.isCompatible(label,pattern.allowedRight,pattern.forbiddenRight);
						if (res) {
							colored.addElement(this);
	stickers.put(this.label,pattern.nhx.substring(pattern.nhx.indexOf("<S>")+3,pattern.nhx.indexOf("</S>")));
						}
					} else {
						Tree sonsZero= (Tree)(sons.elementAt(0));
						Tree sonsOne= (Tree)(sons.elementAt(1));
						if (!pattern.hasLeftConstraint || dic.isCompatible(this,pattern.allowedLeft,pattern.forbiddenLeft)) {
							//System.out.println(this + "\n" + pattern+ "\n" + pattern.hasLeftConstraint + "\n" + dic.isCompatible(this,pattern.allowedLeft,pattern.forbiddenLeft) + "\n+++++++");
							if ((pattern.length!=4.0 || label.startsWith("D_")) && (pattern.length==4.0 || pattern.length==2.0 || pattern.length==-1.0 || !label.startsWith("D_")) && (pattern.length==1.0 || pattern.length==-1.0 || !label.startsWith("T_"))) {
								Vector localColored= new Vector();
								res= sonsZero.colorPattern(pattern,rootPattern,ind,dic,localColored,stickers);
								if (res) {
									for (int i=0;i<localColored.size();i++) {
										colored.addElement(localColored.elementAt(i));
									}

								} //else {
									localColored= new Vector();
									boolean resLocal= sonsOne.colorPattern(pattern,rootPattern,ind,dic,localColored,stickers);
									if (resLocal) {
										for (int i=0;i<localColored.size();i++) {
											colored.addElement(localColored.elementAt(i));
										}
									}
									res= res || resLocal;
								//}
								if (res && rootPattern!=pattern) {
									colored.addElement(this);
								}


							}
						}
					}
				} else {
					if (!isLeaf()) {
					// if sons=null, the pattern cannot be contained
						Tree sonsZero= (Tree)(sons.elementAt(0));
						Tree sonsOne= (Tree)(sons.elementAt(1));
						Tree patternSonsZero= (Tree)(pattern.sons.elementAt(0));
						Tree patternSonsOne= (Tree)(pattern.sons.elementAt(1));
						if ((pattern.rightCardinalityMax==-1 || nbLeaves()<=pattern.rightCardinalityMax) && (pattern.rightCardinalityMin==-1 || nbLeaves()>=pattern.rightCardinalityMin)) {
							if ((!pattern.hasLeftConstraint || dic.isCompatible(this,pattern.allowedLeft,pattern.forbiddenLeft)) && (!pattern.hasRightConstraint || dic.isCompatible(this,pattern.allowedRight,pattern.forbiddenRight))) {
								boolean neutralTransfer=false;
								if (pattern.label.startsWith("T") && pattern.nhx.indexOf("<T>")!=-1 && (new Integer(pattern.nhx.substring(pattern.nhx.indexOf("<T>")+3,pattern.nhx.indexOf("</T>")))).intValue()==2) {
									neutralTransfer=true;
								}
								if (!neutralTransfer && label.startsWith("T_") && pattern.label.startsWith("T")) {
									Vector localColored= new Vector();
									Vector sens=getTransferTarget();
									int patternSens= (new Integer(pattern.nhx.substring(pattern.nhx.indexOf("<T>")+3,pattern.nhx.indexOf("</T>")))).intValue();
									Tree patternTarget=null;
									Tree patternSource=null;
									if (patternSens==0) {
										patternTarget=(Tree)(pattern.sons.elementAt(0));
										patternSource=(Tree)(pattern.sons.elementAt(1));
									} else {
										patternTarget=(Tree)(pattern.sons.elementAt(1));
										patternSource=(Tree)(pattern.sons.elementAt(0));
									}
									Tree target=(Tree)(sens.elementAt(0));
									Tree source=(Tree)(sens.elementAt(1));
									res= (target.colorPattern(patternTarget,rootPattern,ind,dic,localColored,stickers) && source.colorPattern(patternSource,rootPattern,ind,dic,localColored,stickers) && (!patternTarget.hasLeftConstraint || dic.isCompatible(target,patternTarget.allowedLeft,patternTarget.forbiddenLeft)) && (!patternSource.hasLeftConstraint || dic.isCompatible(source,patternSource.allowedLeft,patternSource.forbiddenLeft)));
									if (res) {
										colored.addElement(this);
										for (int i=0;i<localColored.size();i++) {
											colored.addElement(localColored.elementAt(i));
										}
									}
								} else {
									if ((neutralTransfer && label.startsWith("T_") && pattern.label.startsWith("T")) || (label.startsWith("D_") && pattern.label.startsWith("D")) || (!label.startsWith("T_") && !label.startsWith("D_") && pattern.label.startsWith("S"))) {
										Vector localColored= new Vector();
										res= sonsZero.colorPattern(patternSonsZero,rootPattern,ind,dic,localColored,stickers) && sonsOne.colorPattern(patternSonsOne,rootPattern,ind,dic,localColored,stickers) && (!patternSonsZero.hasLeftConstraint || dic.isCompatible(sonsZero,patternSonsZero.allowedLeft,patternSonsZero.forbiddenLeft)) && (!patternSonsOne.hasLeftConstraint || dic.isCompatible(sonsOne,patternSonsOne.allowedLeft,patternSonsOne.forbiddenLeft));
										if (res) {
											for (int i=0;i<localColored.size();i++) {
												colored.addElement(localColored.elementAt(i));
											}

										} else {
											localColored= new Vector();
											res= (sonsZero.colorPattern(patternSonsOne,rootPattern,ind,dic,localColored,stickers) && sonsOne.colorPattern(patternSonsZero,rootPattern,ind,dic,localColored,stickers)) && (!patternSonsZero.hasLeftConstraint || dic.isCompatible(sonsOne,patternSonsZero.allowedLeft,patternSonsZero.forbiddenLeft)) && (!patternSonsOne.hasLeftConstraint || dic.isCompatible(sonsZero,patternSonsOne.allowedLeft,patternSonsOne.forbiddenLeft));
											if (res) {
												for (int i=0;i<localColored.size();i++) {
													colored.addElement(localColored.elementAt(i));
												}

											}
										}
										if (res) {
											colored.addElement(this);
										}
									}
								}
							}
						}
						if (!res) {

							if (!pattern.hasLeftConstraint || dic.isCompatible(this,pattern.allowedLeft,pattern.forbiddenLeft)) {
								if ((pattern.length!=4.0 || label.startsWith("D_")) && (pattern.length==4.0 || pattern.length==2.0 || pattern.length==-1.0 || !label.startsWith("D_")) && (pattern.length==1.0 || pattern.length==-1.0 || !label.startsWith("T_"))) {
									Vector localColored= new Vector();
									res= sonsZero.colorPattern(pattern,rootPattern,ind,dic,localColored,stickers);
									if (res) {
										for (int i=0;i<localColored.size();i++) {
											colored.addElement(localColored.elementAt(i));
										}

									} //else {
										localColored= new Vector();
										boolean resLocal= sonsOne.colorPattern(pattern,rootPattern,ind,dic,localColored,stickers);
										if (resLocal) {
											for (int i=0;i<localColored.size();i++) {
												colored.addElement(localColored.elementAt(i));
											}


										}
										res= resLocal || res;

									//}
									if (res && rootPattern!=pattern) {
										colored.addElement(this);
									}
								}
							}
						}

					}
				}

			}
			return res;
		}

// ********************************************************************************************************************
/**
* Cut from the tree excluded taxa
* @param list	the vector of excluded taxa
* @param table	the hashtable of excluded taxa
* @param excluded	the hashtable hashtable of excluded sequences, filled during the process
* @return true if the tree is not empty, false otherwise.
*/
	public boolean exclude(Vector list, Hashtable table, Hashtable excluded) {
		boolean res= false;
		if (isLeaf()) {
			if (!table.containsKey(label.substring(label.lastIndexOf("_")+1,label.length()))) {
				res=true;
			} else {
				excluded.put(label.substring(0,label.lastIndexOf("_")),"");
			}
		} else {
			Vector keep= new Vector();

			for (int i=0;i<sons.size();i++) {
				Tree son= (Tree)(sons.elementAt(i));
				boolean local= son.exclude(list,table,excluded);
				if (local) {
					keep.addElement(son);
				}
			}

			if (keep.size()==1) {
				res=true;
				Tree son= (Tree)(keep.elementAt(0));
				length=length+son.length;
				if (!son.isLeaf()) {
					double support1=0.0;
					double support2=0.0;
					if (label.indexOf("_")==-1) {
						support1= (new Double(label)).doubleValue();
					} else {

						support1= (new Double(label.substring(label.lastIndexOf("_")+1,label.length()))).doubleValue();
					}

					if (son.label.indexOf("_")==-1) {
						support2= (new Double(son.label)).doubleValue();

					} else {
						support2= (new Double(son.label.substring(son.label.lastIndexOf("_")+1,son.label.length()))).doubleValue();


					}
					if (label.indexOf("_")!=-1) {
						label= label.substring(0,label.lastIndexOf("_")+1) + (new Double(Math.max(support1,support2))).toString();
					} else {
						label= (new Double(Math.max(support1,support2))).toString();
					}
					sons=son.sons;
				} else {
					label=son.label;
					sons=null;
				}


			} else if (keep.size()>1) {
				sons=keep;
				res=true;

			} else {
				//System.out.println("EX " + res + ":" + getNewick());
			}
		}
		return res;
	}

// ********************************************************************************************************************
/**
* Specialized phyloXML string conversion method
* @param buffer	sequence relations
* @return The string translation.
*/
	public String toPhyloXMLString(StringBuffer buffer) {
		//System.out.println(this);
		StringBuffer res= new StringBuffer();
		res.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<phyloxml xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.phyloxml.org http://www.phyloxml.org/1.10/phyloxml.xsd\" xmlns=\"http://www.phyloxml.org\">\n<phylogeny rooted=\"true\">\n");
		toPhyloXMLString(1,res);
		if (buffer!=null) {
			res.append(buffer.toString());
		}
		res.append("</phylogeny>\n</phyloxml>");
		return(res.toString());
	}

// ******************************
/**
* Specialized phyloXML string conversion method
* @param tab			The number of tabulations
* @param lengthParam	The buffer used to store the result
*/
	private void toPhyloXMLString(int tab,StringBuffer res) {
		StringBuffer blockBuffer= new StringBuffer();
		for (int i=0;i<tab;i++) {
			blockBuffer.append("\t");
		}
		String block= blockBuffer.toString();
		res.append(block);
		res.append("<clade>\n");

		res.append(block);
		res.append("\t<branch_length>");
		res.append(length);
		res.append("</branch_length>\n");

		if (!isLeaf()) {
			res.append(block);
			res.append("\t<confidence type=");
			res.append('"');
			double sup=0.0;
			if (label.contains("DUPLICATION")) {
				try {
					sup=(new Double(label.substring(label.indexOf("N")+1,label.length()-1))).doubleValue();
				} catch(Exception e) {
					sup=0.0;
				}
			} else if (label.contains("D_")) {
				try {
					sup=(new Double(label.substring(label.indexOf("_")+1,label.length()-1))).doubleValue();
				} catch(Exception e) {
					sup=0.0;
				}
			} else if (!isLeaf()) {
				try {
					sup=(new Double(label)).doubleValue();
				} catch(Exception e) {
					sup=0.0;
				}
			}
			if (sup<=1.0) {
				res.append("alrt");
			} else {
				res.append("bootstrap");
			}
			res.append('"');
			res.append(">");
			res.append(sup);
			res.append("</confidence>\n");
		}

		if (!isLeaf() && (label.contains("DUPLICATION") || label.contains("D_"))) {
			res.append(block);
			res.append("\t<events>\n");
			res.append(block);
			res.append("\t\t<duplications>1</duplications>\n");
			res.append(block);
			res.append("\t</events>\n");
		} else if (!isLeaf()) {
			res.append(block);
			res.append("\t<events>\n");
			res.append(block);
			res.append("\t\t<speciations>1</speciations>\n");
			res.append(block);
			res.append("\t</events>\n");

		}

		if (!isLeaf()) {
			for (int i=0;i<sons.size();i++) {
				((Tree)(sons.elementAt(i))).toPhyloXMLString(tab+1,res);
			}
		} else {
			res.append(block);
			res.append("\t<taxonomy>\n");
			res.append(block);
			res.append("\t\t<code>");
			res.append(label.substring(label.lastIndexOf("_")+1,label.length()));
			res.append("</code>\n");
			res.append(block);
			res.append("\t</taxonomy>\n");
			res.append(block);
			res.append("\t<sequence id_source=\"");
			//System.out.println(label);
			res.append(label.substring(0,label.lastIndexOf("_")));
			res.append("\">\n");
			res.append(block);
			res.append("\t\t<name>");
			res.append(label.substring(0,label.lastIndexOf("_")));
			res.append("</name>\n");
			res.append(block);
			res.append("\t</sequence>\n");
		}
		res.append(block);
		res.append("</clade>\n");
	}

// ********************************************************************************************************************
/**
* Specialized phyloXML string conversion method, used with a dictionary in order to generate a global species tree from NCBI files
* @param dico			The species dictionary
* @return The string translation.
*/
	public String toPhyloXMLString(SpeciesDictionary dico) {
		StringBuffer res= new StringBuffer();
		res.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<phyloxml xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.phyloxml.org http://www.phyloxml.org/1.10/phyloxml.xsd\" xmlns=\"http://www.phyloxml.org\">\n<phylogeny rooted=\"true\">\n");
		toPhyloXMLString(dico,0,res);
		res.append("</phylogeny>\n</phyloxml>");
		return(res.toString());
	}

// ******************************
/**
* Specialized phyloXML string conversion method
* @param dico			The species dictionary
*/
	private void toPhyloXMLString(SpeciesDictionary dico,int tab,StringBuffer res) {
		if (label.startsWith("INT")) {
			label=label.substring(3,label.length());
		}
		String code=dico.get5LettersCode(label);
		String taxa=dico.getScientificNameFromNumerical(label);
		if (code.equals("N/A")) {
			code=label;
			label=dico.getNumericalCode(code);
			taxa=dico.getScientificName(code);

			if (code==null || code.length()==0 || code.equals("null")) {
				code="N/A";
			}
			//System.out.println(label + " " + code + " " + taxa);
			StringBuffer blockBuffer= new StringBuffer();
			for (int i=0;i<tab;i++) {
				blockBuffer.append("\t");
			}
			String block= blockBuffer.toString();
			res.append(block);
			res.append("<clade>\n");

			res.append(block);
			res.append("\t<taxonomy>\n");

			res.append(block);
			res.append("\t\t<id>");




			if (label.equals("N/A")) {
				res.append("0");
			} else {
				res.append(label);
			}
			res.append("</id>\n");


			res.append(block);
			res.append("\t\t<code>");
			if (code.equals("N/A")) {
				res.append("NOAVA");
			} else {
				res.append(code);
			}
			res.append("</code>\n");


			res.append(block);
			res.append("\t\t<scientific_name>");
			res.append((String)(taxa));
			res.append("</scientific_name>\n");



			res.append(block);
			res.append("\t</taxonomy>\n");
			if (!isLeaf()) {
				for (int i=0;i<sons.size();i++) {
					((Tree)(sons.elementAt(i))).toPhyloXMLString(dico,tab+1,res);
				}
			}
			res.append(block);
			res.append("</clade>\n");
		} else {

			StringBuffer blockBuffer= new StringBuffer();
			for (int i=0;i<tab;i++) {
				blockBuffer.append("\t");
			}
			String block= blockBuffer.toString();
			res.append(block);
			res.append("<clade>\n");

			res.append(block);
			res.append("\t<taxonomy>\n");

			res.append(block);
			res.append("\t\t<id>");




			res.append(label);
			res.append("</id>\n");


			res.append(block);
			res.append("\t\t<code>");
			res.append((String)(code));
			res.append("</code>\n");


			res.append(block);
			res.append("\t\t<scientific_name>");
			res.append((String)(taxa));
			res.append("</scientific_name>\n");



			res.append(block);
			res.append("\t</taxonomy>\n");
			if (!isLeaf()) {
				for (int i=0;i<sons.size();i++) {
					((Tree)(sons.elementAt(i))).toPhyloXMLString(dico,tab+1,res);
				}
			}
			res.append(block);
			res.append("</clade>\n");
		}
	}

// ********************************************************************************************************************
/**
* Set root branch lengths as close as midpoint, and return a midpoint score.
* @return	The midpoint score
*/
	public double midpoint() {
		double res=0.0;
		if (!isLeaf()) {
			Tree ta=(Tree)(sons.elementAt(0));
			Tree tb=(Tree)(sons.elementAt(1));
			double a= ta.maxDepth-ta.length;
			double b= tb.maxDepth-tb.length;
			//System.out.println(a +" "+b);
			double marge=ta.length+tb.length;
			if (Math.abs(a-b)>marge) {
				if (a>b) {
					res=a-b-marge;
					tb.length=marge*0.95;
					ta.length=marge*0.05;
				} else {
					res=b-a-marge;
					ta.length=marge*0.95;
					tb.length=marge*0.05;
				}
			} else {
			//System.out.println("a:" + a + " b:" + b + " ta:" + ta.length + " tb:" + tb.length);
			ta.length=(b+marge-a)/2;
			tb.length=(a+marge-b)/2;
				/*if (a>b) {
					tb.length=a-b;
					ta.length=marge-(a-b);
					//ta.length=marge/2.0-res/2.0;
					//tb.length=marge/2.0+res/2.0;
				} else {
					ta.length=b-a;
					tb.length=marge-(b-a);
					//tb.length=marge/2.0-res/2.0;
					//ta.length=marge/2.0+res/2.0;
				}*/
			}
			//res=Math.abs((a+ta.length)-(b+tb.length));
		}
		return res;
	}

// ********************************************************************************************************************
/**
* Fill the father field, in order to enrich steppings.
*/
	public void lightPretreatment() {
		lightPretreatment(null);
	}

// ********************************************************************************************************************
/**
* Light pretreatment submethod
* @param father	The father of this node
*/
	private void lightPretreatment(Tree father) {
		this.father=father;
		if (!isLeaf()) {
			//The node case ; for each son
			for (int i=0;i<sons.size();i++) {
				Tree son= (Tree)(sons.elementAt(i));
				//Pretreat the son recursivly
				son.lightPretreatment(this);

			}
		}
	}
// ********************************************************************************************************************
/**
* Fill a vector with nodes under a depth threshold
* @param trees	The vector to fill
* @param threshold	The cutting threshold
*/
	public void clusteringNodes(Vector trees, double threshold) {
		if (!isLeaf()  && (((Tree)(sons.elementAt(0))).maxDepth + ((Tree)(sons.elementAt(1))).maxDepth)>=threshold) {
			//The node case ; for each son
			for (int i=0;i<sons.size();i++) {
				Tree son= (Tree)(sons.elementAt(i));
				//Pretreat the son recursivly
				son.clusteringNodes(trees,threshold);

			}
		} else {
			trees.addElement(this);
		}
	}

// ********************************************************************************************************************
/**
* Fill the Vector, Hashtable and pointer fields, in order to compare and reconcile this tree with others. Add node labels.
*/
	public void heavyPretreatment() {
		heavyPretreatment(null);
	}

// ********************************************************************************************************************
/**
* Pretreatment submethod
* @param father	The father of this node
*/
	private void heavyPretreatment(Tree father) {
		this.father=father;
		leafHashtable = new Hashtable();
		leafVector= new Vector();
		leafVector.addElement(this);
		if (label!=null)
			leafHashtable.put(label,this);
		if (isLeaf()) {
			//The leaf case, put this into the fields
			if (length==-1.0) {
				maxDepth=1.0;
			} else {
				maxDepth=length;
			}
		} else {
			//System.out.println(label);
			leafHashtable.put(label,this);
			//The node case ; for each son
			maxDepth=0.0;
			for (int i=0;i<sons.size();i++) {
				//Pretreat the son recursivly
				Tree son= (Tree)(sons.elementAt(i));
				son.heavyPretreatment(this);
				if (son.maxDepth>maxDepth) {
					maxDepth=son.maxDepth;
				}
				for (int j=0;j<son.leafVector.size();j++) {
					Tree leaf= (Tree)(son.leafVector.elementAt(j));
					leafVector.addElement(leaf);
					leafHashtable.put(leaf.label,leaf);
				}

			}
			if (father==null) {
				length=0.0;
			}
			if (length==-1.0) {
				maxDepth+=1.0;
			} else {
				maxDepth+=length;
			}
		}
	}

// ********************************************************************************************************************
/**
* Fill the Vector, Hashtable and pointer fields, in order to compare and a gene tree with a pattern tree. Store only taxonomic information (XX_TAXON) in data structures.
*/
	public void taxonomicPretreatment() {
		taxonomicPretreatment(null);
	}

// ********************************************************************************************************************
/**
* Pretreatment submethod
* @param father	The father of this node
*/
	private void taxonomicPretreatment(Tree father) {
		this.father=father;
		leafHashtable = new Hashtable();
		leafVector= new Vector();
		if (isLeaf()) {
			//The leaf case, put this into the fields
			leafVector.addElement(this);
			//System.out.println(label);
			//System.out.println(label.substring(label.lastIndexOf("_")+1,label.length()));
			if (this.nhx!=null && this.nhx.indexOf("S=")!=-1) {
					String cut1= this.nhx.substring(this.nhx.indexOf("S=")+2, this.nhx.length());
					String spec= cut1.substring(0,cut1.indexOf(":"));
					if (!this.label.endsWith(spec)) {
						this.label=this.label + "_" + spec;
					}
			}




			leafHashtable.put(label.substring(label.lastIndexOf("_")+1,label.length()),this);
			if (length==-1.0) {
				maxDepth=1.0;
			} else {
				maxDepth=length;
			}
			//System.out.println(label);
		} else {
			//The node case ; for each son
			if (this.nhx!=null && this.nhx.indexOf("D=Y")!=-1) {
				this.label="D_" + this.label;
			}
			if (this.nhx!=null && this.nhx.indexOf("D=N")!=-1) {
				this.label="S_" + this.label;
			}


			maxDepth=0.0;
			for (int i=0;i<sons.size();i++) {
				//Pretreat the son recursivly
				Tree son= (Tree)(sons.elementAt(i));
				son.taxonomicPretreatment(this);
				if (son.maxDepth>maxDepth) {
					maxDepth=son.maxDepth;
				}
				for (int j=0;j<son.leafVector.size();j++) {
					Tree leaf= (Tree)(son.leafVector.elementAt(j));
					leafVector.addElement(leaf);
			//System.out.println(leaf.label);
			//System.out.println(leaf.label.substring(label.lastIndexOf("_")+1,label.length()));
					leafHashtable.put(leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length()),leaf);
				}

			}
			if (father==null) {
				length=0.0;
			}
			if (length==-1.0) {
				maxDepth+=1.0;
			} else {
				maxDepth+=length;
			}
		}
	}

// ********************************************************************************************************************
/**
* Fill the Vector, Hashtable and pointer fields, in order to compare and reconcile this tree with others.
*/
	public void patternPretreatment(Tree speciesTree,SpeciesDictionary dico) {
		patternPretreatment(null,speciesTree,dico);
	}

// ********************************************************************************************************************
/**
* Pretreatment submethod
* @param father	The father of this node
*/
	private void patternPretreatment(Tree father,Tree speciesTree,SpeciesDictionary dico) {
		this.father=father;
		leafHashtable = new Hashtable();
		leafVector= new Vector();
		allowedLeft= new Hashtable();
		allowedRight= new Hashtable();
		forbiddenLeft= new Hashtable();
		forbiddenRight= new Hashtable();
		patternSpecies=new Vector();

		if (nhx!=null) {
			if (nhx.indexOf("<IR>")!=-1) {
				rightCardinalityMin= (new Integer(nhx.substring(nhx.indexOf("<IR>")+4,nhx.indexOf("</IR>")))).intValue();
				//System.out.println(rightCardinalityMin);
			} else {
				rightCardinalityMin=-1;
			}
			if (nhx.indexOf("<AR>")!=-1) {
				rightCardinalityMax= (new Integer(nhx.substring(nhx.indexOf("<AR>")+4,nhx.indexOf("</AR>")))).intValue();
			} else {
				rightCardinalityMax=-1;
			}
			if (nhx.indexOf("<L>")!=-1) {
				hasLeftConstraint=true;
				String leftString= nhx.substring(nhx.indexOf("<L>")+3,nhx.indexOf("</L>"));
				//System.out.println(leftString);
				String[] strings= leftString.split(";");
				for (int i=0;i<strings.length;i++) {
					if (strings[i].startsWith("Not ")) {
						forbiddenLeft.put(strings[i].substring(4,strings[i].length()),"0");
					} else {
						allowedLeft.put(strings[i],"0");
					}
				}
			}
			if (nhx.indexOf("<R>")!=-1) {
				hasRightConstraint=true;
				String rightString= nhx.substring(nhx.indexOf("<R>")+3,nhx.indexOf("</R>"));
				//System.out.println(rightString);
				String[] strings= rightString.split(";");
				/*if (strings.length>1 || !speciesTree.leafHashtable.containsKey(strings[0])) {
					isSimpleRight=false;
				}*/
				for (int i=0;i<strings.length;i++) {
					if (strings[i].startsWith("Not ")) {
						forbiddenRight.put(strings[i].substring(4,strings[i].length()),"0");
					} else {
						allowedRight.put(strings[i],"0");
					}
				}

				// Fill the allowed species list
				for (int i=0;i<speciesTree.leafVector.size();i++) {
					Tree speciesLeaf=(Tree)(speciesTree.leafVector.elementAt(i));
					if (dico.isCompatible(speciesLeaf.label,allowedRight,forbiddenRight)) {
						patternSpecies.addElement(speciesLeaf.label);
						//System.out.print(speciesLeaf.label + " ");
					}
				}

						//System.out.print("\n");
			}
		} else {

				rightCardinalityMin=-1;
				rightCardinalityMax=-1;
		}

		if (isLeaf()) {
			//The leaf case, put this into the fields
			leafVector.addElement(this);
			leafHashtable.put(label,this);
			if (length==-1.0) {
				maxDepth=1.0;
			} else {
				maxDepth=length;
			}
		} else {
			//The node case ; for each son
			maxDepth=0.0;
			for (int i=0;i<sons.size();i++) {
				//Pretreat the son recursivly
				Tree son= (Tree)(sons.elementAt(i));
				son.patternPretreatment(this,speciesTree,dico);
				if (son.maxDepth>maxDepth) {
					maxDepth=son.maxDepth;
				}
				for (int j=0;j<son.leafVector.size();j++) {
					Tree leaf= (Tree)(son.leafVector.elementAt(j));
					leafVector.addElement(leaf);
					leafHashtable.put(leaf.label,leaf);
				}

			}
			/*if (father==null) {
				length=0.0;
			}*/
			if (length==-1.0) {
				maxDepth+=1.0;
			} else {
				maxDepth+=length;
			}
		}
	}
// ********************************************************************************************************************
/**
* Fill the Vector, Hashtable and pointer fields, for species trees
*/
	public void globalPretreatment() {
		globalPretreatment(null);
	}

// ********************************************************************************************************************
/**
* Pretreatment submethod
* @param father	The father of this node
*/
	private void globalPretreatment(Tree father) {
		this.father=father;
		leafHashtable = new Hashtable();
		leafVector= new Vector();
		if (isLeaf()) {
			//The leaf case, put this into the fields
			leafVector.addElement(this);
			if (label.indexOf('|')!=-1)
				label=label.substring(0,label.indexOf('|'));
			leafHashtable.put(label,this);
			if (length==-1.0) {
				maxDepth=1.0;
			} else {
				maxDepth=length;
			}
		} else {
			//The node case ; for each son
			maxDepth=0.0;
			for (int i=0;i<sons.size();i++) {
				//Pretreat the son recursivly
				Tree son= (Tree)(sons.elementAt(i));
				son.globalPretreatment(this);
				if (son.maxDepth>maxDepth) {
					maxDepth=son.maxDepth;
				}
				for (int j=0;j<son.leafVector.size();j++) {
					Tree leaf= (Tree)(son.leafVector.elementAt(j));
					leafVector.addElement(leaf);
					leafHashtable.put(leaf.label,leaf);
				}

			}
			if (label.indexOf('|')!=-1)
				label=label.substring(0,label.indexOf('|'));
			leafHashtable.put(this.label,this);
			leafVector.addElement(this);
			/*if (father==null) {
				length=0.0;
			}*/
			if (length==-1.0) {
				maxDepth+=1.0;
			} else {
				maxDepth+=length;
			}
		}
	}

// ********************************************************************************************************************
/**
* Fill the Vector, Hashtable and pointer fields, in order to compare and reconcile this tree with others.
*/
	public void pretreatment() {
		pretreatment(null);
	}

// ********************************************************************************************************************
/**
* Pretreatment submethod
* @param father	The father of this node
*/
	private void pretreatment(Tree father) {
		this.father=father;
		leafHashtable = new Hashtable();
		leafVector= new Vector();
		if (isLeaf()) {
			//The leaf case, put this into the fields
			leafVector.addElement(this);
			leafHashtable.put(label,this);
			if (length==-1.0) {
				maxDepth=1.0;
			} else {
				maxDepth=length;
			}
		} else {
			//The node case ; for each son
			maxDepth=0.0;
			for (int i=0;i<sons.size();i++) {
				//Pretreat the son recursivly
				Tree son= (Tree)(sons.elementAt(i));
				son.pretreatment(this);
				if (son.maxDepth>maxDepth) {
					maxDepth=son.maxDepth;
				}
				for (int j=0;j<son.leafVector.size();j++) {
					Tree leaf= (Tree)(son.leafVector.elementAt(j));
					leafVector.addElement(leaf);
					leafHashtable.put(leaf.label,leaf);
				}

			}
			/*if (father==null) {
				length=0.0;
			}*/
			if (length==-1.0) {
				maxDepth+=1.0;
			} else {
				maxDepth+=length;
			}
		}
	}
// ********************************************************************************************************************
/**
* Fill an ID table, deduced from labels, and clean leaf labels from Ids. Usable for some specialized species trees
* @param table	The id table to fill
*/
	public void fillAndCleanID(Hashtable table) {
		if (isLeaf()) {
			table.put(label.substring(label.lastIndexOf("_")+1,label.length()),this);
			label=label.substring(0,label.lastIndexOf("_"));
		} else {
			table.put(label,this);
			for (int i=0;i<sons.size();i++) {
				Tree son= (Tree)(sons.elementAt(i));
				son.fillAndCleanID(table);
			}
		}
	}
// ********************************************************************************************************************
/**
* Return true if this subtree contains only paralogy, and false otherwise, and initialise the ultra attributes for the whole tree
* @return True if this is an ultraparalogy node, false otherwise
*/
	public boolean ultraParalogy() {
		boolean res= true;
		if (!isLeaf()) {
			int i=0;
			while (i<sons.size()) {
				Tree son= (Tree)(sons.elementAt(i));
				boolean localRes=son.ultraParalogy();
				res= localRes && res;
				i++;
			}

			if (!label.contains("DUPLICATION") && !label.contains("D_")) {
				res= false;
			}
			ultra=res;

		}

		//System.out.println("res:" + res);
		return res;
	}
// ********************************************************************************************************************
/**
* Fill a table with the largest ultraparalog group for each taxon
* @param table	The table to fill
* @param table2	The id table to fill
* @param limit	the minimum number of ultraparalog in a group to fill the ids table
*/
	public void fillUltraParalogs(Hashtable table,Vector table2, int limit, Vector subspecies, Vector subspeciesTags, Hashtable globalTable) {
		if (isLeaf()) {
			// fill table with 1
			String tax= label.substring(label.lastIndexOf("_")+1,label.length());


			if (!table.containsKey(tax) && !globalTable.containsKey(tax)) {
						//System.out.println("3 " + tax);
				table.put(tax,new Integer(1));

			}

		} else {
			boolean subUltra=false;
			int w=0;
			while (w<subspecies.size() && !subUltra) {
				boolean subUltra2=true;
				Hashtable tableLocal= (Hashtable)(subspecies.elementAt(w));
				String tag= (String)(subspeciesTags.elementAt(w));
				//System.out.println(tag);
				for (int i=0;i<leafVector.size() && subUltra2;i++) {
					Tree leaf = ((Tree)(leafVector.elementAt(i)));
					String tax= leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length());
					if (!tableLocal.containsKey(tax)) {
						subUltra2=false;
					}
				}
				subUltra=subUltra2;
				if (subUltra) {
				//System.out.println("ok");
					String tax= tag;
					if (!table.containsKey(tax)) {
						//System.out.println("1 " + tax);
						table.put(tax,new Integer(nbLeaves()));
					} else {
						int local= ((Integer)(table.get(tax))).intValue();
						if (local<nbLeaves()) {
							table.put(tax,new Integer(nbLeaves()));
						}

					}
					if (leafVector.size()>=limit) {
						Vector localIds= new Vector();
						for (int i=0;i<leafVector.size();i++) {
							localIds.addElement(((Tree)(leafVector.elementAt(i))).label);
						}
						table2.addElement(localIds);
					}

				}






				w++;
			}
			if (!subUltra && ultra) {
				Tree leaf= (Tree)(leafVector.elementAt(0));
				String tax= leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length());
				if (!table.containsKey(tax)) {
					//System.out.println("2 " + tax);
					table.put(tax,new Integer(nbLeaves()));
				} else {
					int local= ((Integer)(table.get(tax))).intValue();
					if (local<nbLeaves()) {
						table.put(tax,new Integer(nbLeaves()));
					}

				}
				if (leafVector.size()>=limit) {
					Vector localIds= new Vector();
					for (int i=0;i<leafVector.size();i++) {
						localIds.addElement(((Tree)(leafVector.elementAt(i))).label);
					}
					table2.addElement(localIds);
				}

			} else if (!subUltra)  {
				for (int i=0;i<sons.size();i++) {
					Tree son= (Tree)(sons.elementAt(i));
					son.fillUltraParalogs(table,table2,limit,subspecies,subspeciesTags,globalTable);
				}

			}

		}

	}

// ********************************************************************************************************************
/**
* Fill a table with the number of ultraparalogous groups for each taxon
* @param table	The table to fill
*/
	public void getNbUltraparalogGroups(Hashtable table) {
		if (isLeaf()) {
			// fill table with 1
			String tax= label.substring(label.lastIndexOf("_")+1,label.length());
			if (!table.containsKey(tax)) {
				table.put(tax,new Integer(1));

			} else {
				int local= ((Integer)(table.get(tax))).intValue();
				table.put(tax,new Integer(local+1));

			}

		} else {
			if (ultra) {
				Tree leaf= (Tree)(leafVector.elementAt(0));
				String tax= leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length());
				if (!table.containsKey(tax)) {
					table.put(tax,new Integer(1));
				} else {
					int local= ((Integer)(table.get(tax))).intValue();
					table.put(tax,new Integer(local+1));

				}
			} else {
				for (int i=0;i<sons.size();i++) {
					Tree son= (Tree)(sons.elementAt(i));
					son.getNbUltraparalogGroups(table);
				}

			}

		}
	}

// ********************************************************************************************************************
/**
* Return the newick representation of this node
* @return The newick standard representation of this node
*/
	public String getNHXNewick(Tree speciesTree, Hashtable duplications, String refgroupe) {
		StringBuffer res= new StringBuffer();
		getNHXNewick(res,speciesTree, duplications, refgroupe);
		res.append(';');
		return(res.toString());
	}

// ******************************
/**
* Standard string conversion method
* @param lengthParam	The buffer used to store the result
*/
	private void getNHXNewick(StringBuffer res,Tree speciesTree, Hashtable duplications, String refgroupe) {
		if (!isLeaf()) {
			res.append('(');
			((Tree)(sons.elementAt(0))).getNHXNewick(res,speciesTree, duplications, refgroupe);
			for (int i=1;i<sons.size();i++) {
				res.append(',');
				((Tree)(sons.elementAt(i))).getNHXNewick(res,speciesTree, duplications, refgroupe);
			}
			res.append(')');
		} else {
			res.append(label);
		}
		/*if (label!=null && !label.equals("null")) {
			if (!isLeaf() && label.startsWith("'DUPLICATION")) {
				res.append(label.substring(12,label.length()-1));
			} else {
				res.append(label);
			}
		}*/
		if (length!=-1.0) {
			res.append(':');
			res.append(length);
		}
		if (!isLeaf()) {
			//System.out.println("Echo");
			res.append("[&&NHX");
			if (label.startsWith("'D") || (duplications!=null && duplications.containsKey(refgroupe + "#" + label))) {
				res.append(":D=Y");
				res.append(":SIS=");
				res.append(trace());
			} else {
				res.append(":D=N");
			}
			res.append(":S=");
			//System.out.println("AAAAAAAA\n" + this.speciesMapping(speciesTree) + "\n" + "BBBBBB");
			res.append(this.speciesMapping(speciesTree).label.replace(" ","."));

			if (father!=null && label!=null && label.length()>0 && !label.equals("'D")) {
				//System.out.println(label);
				res.append(":B=");
				if (!label.startsWith("'D")) {
					//System.out.println("A");
					res.append(label);
				} else {
					//System.out.println("B");
					if (label.indexOf("_")==-1) {
						res.append(label.substring(label.indexOf("0")+1,label.length()-1));
					} else {
						res.append(label.substring(label.lastIndexOf("_")+1,label.length()-1));
					}
				}
			}
			res.append("]");
		} else {

			res.append("[&&NHX");
			res.append(":S=");
			res.append(this.speciesMapping(speciesTree).label.replace(" ","."));
			res.append("]");
		}
	}

// ********************************************************************************************************************
/**
* Return the newick representation of this node
* @return The newick standard representation of this node
*/
	public String getNewick() {
		StringBuffer res= new StringBuffer();
		getNewick(res);
		res.append(';');
		return(res.toString());
	}

// ******************************
/**
* Standard string conversion method
* @param lengthParam	The buffer used to store the result
*/
	private void getNewick(StringBuffer res) {
		if (!isLeaf()) {
			res.append('(');
			((Tree)(sons.elementAt(0))).getNewick(res);
			for (int i=1;i<sons.size();i++) {
				res.append(',');
				((Tree)(sons.elementAt(i))).getNewick(res);
			}
			res.append(')');
		}
		if (label!=null && !label.equals("null")) {
			res.append(label);
		}
		if (length!=-1.0) {
			res.append(':');
			res.append(length);
		}
		if (nhx!=null) {
			res.append("[" + nhx + "]");
		}
	}

// ********************************************************************************************************************
/**
* Return the simple (no length, no support) newick representation of this node
* @return The newick standard representation of this node
*/
	public String getSimpleNewick() {
		StringBuffer res= new StringBuffer();
		getSimpleNewick(res);
		res.append(';');
		return(res.toString());
	}

// ******************************
/**
* Standard string conversion method
* @param lengthParam	The buffer used to store the result
*/
	private void getSimpleNewick(StringBuffer res) {
		if (!isLeaf()) {
			res.append('(');
			((Tree)(sons.elementAt(0))).getSimpleNewick(res);
			for (int i=1;i<sons.size();i++) {
				res.append(',');
				((Tree)(sons.elementAt(i))).getSimpleNewick(res);
			}
			res.append(')');
		} else {
			res.append(label);
		}
	}
	// ********************************************************************************************************************
	/**
	* Return the species (nothing but the 5 letter code) newick representation of this node
	* @return The newick standard representation of this node
	*/
		public String getSpeciesNewick() {
			StringBuffer res= new StringBuffer();
			getSpeciesNewick(res);
			res.append(';');
			return(res.toString());
		}

	// ******************************
	/**
	* Standard string conversion method
	* @param lengthParam	The buffer used to store the result
	*/
		private void getSpeciesNewick(StringBuffer res) {
			if (!isLeaf()) {
				res.append('(');
				((Tree)(sons.elementAt(0))).getSpeciesNewick(res);
				for (int i=1;i<sons.size();i++) {
					res.append(',');
					((Tree)(sons.elementAt(i))).getSpeciesNewick(res);
				}
				res.append(')');
			} else {
				res.append(label.substring(label.lastIndexOf("_")+1,label.length()));
			}
		}

// ********************************************************************************************************************
/**
* Return the max depth of the tree, 1.0 per level if no branch length, sum of branch lengths otherwise.
* @return The max depth
*/
	public double maxDepth() {
		double res=0.0;
		if (length!=-1.0) {
			res+=1.0;
		} else {
			res+=length;
		}
		if (!isLeaf()) {
			double max=0.0;
			for (int i=0;i<sons.size();i++) {
				Tree son= (Tree)(sons.elementAt(i));
				double local= son.maxDepth();
				if (local>max) {
					max=local;
				}
			}
			res+=max;
		}
		return res;
	}
// ********************************************************************************************************************
/**
* Return the depth of the leaf in this tree, 1.0 per level if no branch length, sum of branch lengths otherwise.
* @param leaf	The target leaf
* @return The depth
*/
	public double getDepth(Tree leaf) {
		double res=0.0;
		while (leaf!=this) {
			if (leaf.length!=-1.0) {
				res+=leaf.length;
			} else {
				res+=1.0;
			}
			leaf=leaf.father;
		}

		return res;
	}

// ********************************************************************************************************************
/**
* Return the last common ancestor of two leaves.
* @param leafI	The first leaf
* @param leafJ	The second leaf
* @return The last common ancestor of leafI and leafJ
*/
	public Tree lastCommonAncestor(Tree leafI, Tree leafJ) {
		Tree res=null;
		Vector linkedSons= new Vector();
		for (int i=0;i<sons.size();i++) {
			Tree son= (Tree)(sons.elementAt(i));
			if (son.leafHashtable.containsKey(leafI.label) || son.leafHashtable.containsKey(leafJ.label)) {
				linkedSons.addElement(son);
			}
		}
		if (linkedSons.size()>1) {
			res=this;
		} else {
			res=((Tree)(linkedSons.elementAt(0))).lastCommonAncestor(leafI,leafJ);
		}

		return res;
	}
// ********************************************************************************************************************
/**
* Return the number of redundant nodes.
* @return The number of redundant nodes
*/
	public int getNbRedundancy() {
		int res=0;
		if (!isLeaf()) {
			Tree son1= (Tree)(sons.elementAt(0));
			Tree son2= (Tree)(sons.elementAt(1));
			boolean isRedundant=false;
			int i=0;
			while (!isRedundant && i<son1.leafVector.size()) {
				Tree son= (Tree)(son1.leafVector.elementAt(i));
				String localLabel=son.label.substring(son.label.lastIndexOf("_")+1,son.label.length());
				if (son2.leafHashtable.containsKey(localLabel)) {
					isRedundant=true;
				}
				i++;
			}

			if (isRedundant) {
				res=1;
			}
			for (i=0;i<this.sons.size();i++) {
				res+=((Tree)(sons.elementAt(i))).getNbRedundancy();
			}
		}
		return res;
	}
// ********************************************************************************************************************
/**
* Return true if this node is a leaf
* @return True if this node is a leaf and false if this node is internal
*/
	public boolean isLeaf() {
		return(sons==null || sons.size()==0);
	}
// ********************************************************************************************************************
/**
* Return a table of the expansion for each node of this species tree
* @return The tree nodes
*/
	public Hashtable computeExpansion(Hashtable d,Hashtable unrepresented) {
		Hashtable res= new Hashtable();
		computeExpansion(res,d,unrepresented);
		return(res);
	}

// ******************************
/**
* Fill a vector with this node and every node under this node
* @param res	The table to fill
*/
	private void computeExpansion(Hashtable res,Hashtable d,Hashtable unrepresented) {
		int ndi=0;
		if (d.containsKey(this.label)) {
			ndi=((Integer)(d.get(this.label))).intValue();
		}
		int nli=0;
		if (this.father!=null && !unrepresented.containsKey(this.father.label) && d.containsKey(this.father.label)) {
			nli=((Integer)(d.get(this.father.label))).intValue();
		}
		if (nli==0) {
			res.put(this.label,new Double(1.0));
		} else {
			res.put(this.label,new Double((double)ndi/(double)nli));
		}
		if (!isLeaf()) {
			for (int i=0;i<sons.size();i++) {
				((Tree)(sons.elementAt(i))).computeExpansion(res,d,unrepresented);
			}
		}
	}
// ********************************************************************************************************************
/**
* Add prefixes to nodes in order to obtain unique ids as labels
* @return The tree nodes
*/
	public void addUniquePrefix() {
		int nomatter=addUniquePrefix(1);
	}

// ******************************
	private int addUniquePrefix(int runner) {
		int local=runner;
		if (!isLeaf()) {
			if (label==null || label.length()==0) {
				label=(new Integer(local)).toString();
			} else {
				label=(new Integer(local)).toString()+"_"+label;
			}
			local++;
			for (int i=0;i<sons.size();i++) {
				local=((Tree)(sons.elementAt(i))).addUniquePrefix(local);
			}
		}
		return local;
	}
// ********************************************************************************************************************
/**
* Return a table of the number of ancestral copies for each node of this species tree
* @return The tree nodes
*/
	public Hashtable countCopies(Hashtable d, Hashtable l) {
		Hashtable res= new Hashtable();
		countCopies(res,d,l,0,0);
		return(res);
	}

// ******************************
/**
* Fill a vector with this node and every node under this node
* @param res	The table to fill
*/
	private void countCopies(Hashtable res,Hashtable d,Hashtable l,int nd,int nl) {
		int ndi=0;
		if (d.containsKey(this.label)) {
			ndi=((Integer)(d.get(this.label))).intValue();
		}
		int nli=0;
		if (l.containsKey(this.label)) {
			nli=((Integer)(l.get(this.label))).intValue();
		}
		res.put(this.label,new Integer(1+nd+ndi-nl-nli));
		if (!isLeaf()) {
			for (int i=0;i<sons.size();i++) {
				((Tree)(sons.elementAt(i))).countCopies(res,d,l,nd+ndi,nl+nli);
			}
		}
	}

// ********************************************************************************************************************
/**
* Return a vector containing all tree nodes under this node, this node included
* @return The tree nodes
*/
	public Vector getNodes() {
		Vector res= new Vector();
		getNodes(res);
		return(res);
	}

// ******************************
/**
* Fill a vector with this node and every node under this node
* @param res	The vector to fill
*/
	private void getNodes(Vector res) {
		res.addElement(this);
		if (!isLeaf()) {
			for (int i=0;i<sons.size();i++) {
				((Tree)(sons.elementAt(i))).getNodes(res);;
			}
		}
	}

// ********************************************************************************************************************
/**
* Return every possible rooted trees, from this tree, cloning the structures
* @return A vector of rooted trees
*/
	public Vector getRootedTrees() {
		Vector res= new Vector();
		if (sons.size()<=2) {
			res.addElement(this);
		}
		Vector nodes = getNodes();
		// for each node of this tree
		for (int i=0;i<nodes.size();i++) {
			//Clone the structure
			Tree tree= new Tree(this);
			Vector localNodes = tree.getNodes();
			//Get the root node in the cloned structure
			Tree localNode= (Tree)(localNodes.elementAt(i));
			tree.lightPretreatment();
			boolean rerooted=localNode.reroot();
			if (rerooted) {
				res.addElement(localNode);
			}
		}
		return(res);
	}
// ********************************************************************************************************************
/**
* Return the number of leaves.
* @return The number of leaves
*/

	public int nbLeaves() {
		return(leafVector.size());
	}

// ********************************************************************************************************************
/**
* Reroot the whole tree, father parents included, to the branch upper this node.
* @return True if the tree needed to be rerooted, false if it were already rooted at the right place
*/
	public boolean reroot() {
		boolean res=true;
		if (father!=null && (father.father!=null || father.sons.size()>2)) {
			// Build new node, containing this node subtopology
			Tree node = null;
			if (length==-1.0) {
				node=new Tree(sons,label,-1.0,nhx);
			} else {
				node=new Tree(sons,label,length/2.0,nhx);

			}
			// Define new sons, containing the new node and the upper flipped topology
			Vector newSons=new Vector();
			newSons.addElement(node);
			father.getUpperTopology(this);
			if (length!=-1.0) {
				father.length=length/2.0;
			}
			newSons.addElement(father);
			if (!father.isLeaf()) {
				father.label=this.label;
			}
			this.sons=newSons;
			this.length=-1.0;
		} else {
			// Tree is already well rooted
			res=false;
		}
		return res;
	}

// ******************************
/**
* Return the flipper upper topology
* @param sourceSon	The source son of the request, which will not be included in the results
*/
	private void getUpperTopology(Tree sourceSon) {
		if (father==null) {
			//Root case
			Vector newSons= new Vector();
			if (sons.size()>2) {
				//Return a new node with others sons
				for (int i=0;i<sons.size();i++) {
					Tree son= (Tree)(sons.elementAt(i));
					if (son!=sourceSon) {
						newSons.addElement(son);
					}
				}
				this.length=sourceSon.length;

			} else {
				//Return the other son, with branch length sum
				if ((sourceSon)==((Tree)(sons.elementAt(0)))) {
					Tree transferedSon= (Tree)(sons.elementAt(1));
					newSons=transferedSon.sons;
					if (transferedSon.length!=-1.0) {
						this.length=sourceSon.length +transferedSon.length;
					}
					if (transferedSon.isLeaf()) {
						sourceSon.label=transferedSon.label;
					}
				} else {
					Tree transferedSon= (Tree)(sons.elementAt(0));
					newSons=transferedSon.sons;
					if (transferedSon.length!=-1.0) {
						this.length=sourceSon.length + transferedSon.length;
					}
					if (transferedSon.isLeaf()) {
						sourceSon.label=transferedSon.label;
					}
				}
			}
			this.label=sourceSon.label;
			this.sons=newSons;

		} else {
			//Internal node case
			//Recursive contruction
			Vector newSons= new Vector();
			father.getUpperTopology(this);
			newSons.addElement(father);
			for (int i=0;i<sons.size();i++) {
				Tree son= (Tree)(sons.elementAt(i));
				if (son!=sourceSon) {
					newSons.addElement(son);
				}
			}
			this.length=sourceSon.length;
			this.label=sourceSon.label;
			this.sons=newSons;
		}
	}

}

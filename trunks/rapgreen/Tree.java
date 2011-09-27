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
		//Read the root node
		int noMatter= ReadNode(0,newick,sonsParam,labelParam,lengthParam);
		//Allocate the fields
		sons=sonsParam;
		label=labelParam.toString();
		if (lengthParam.length()>0) {
			length= (new Double(lengthParam.toString())).doubleValue();
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
	public Tree(Vector sons, String label, double length) {
		this.sons=sons;
		this.label=label;
		this.length=length;
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
	private int ReadNode(int starting, String newick, Vector sonsParam, StringBuffer labelParam, StringBuffer lengthParam) {
		int index=starting;
		if (newick.charAt(index)=='(') {
			//The internal node case
			index++;
			//Create storing objects for sons
			Vector sonsSon= new Vector();
			StringBuffer labelSon= new StringBuffer();
			StringBuffer lengthSon= new StringBuffer();
			//Get the first son
			index= ReadNode(index,newick,sonsSon,labelSon,lengthSon);
			if (lengthSon.length()>0) {
				sonsParam.addElement(new Tree(sonsSon,labelSon.toString(),(new Double(lengthSon.toString())).doubleValue()));
			} else {
				sonsParam.addElement(new Tree(sonsSon,labelSon.toString(),-1.0));
			}
			while (newick.charAt(index)==',') {
				index++;
				//Initialize storing objects for sons
				sonsSon= new Vector();
				labelSon= new StringBuffer();
				lengthSon= new StringBuffer();
				//Get the next son
				index= ReadNode(index,newick,sonsSon,labelSon,lengthSon);
				if (lengthSon.length()>0) {
					sonsParam.addElement(new Tree(sonsSon,labelSon.toString(),(new Double(lengthSon.toString())).doubleValue()));
				} else {
					sonsParam.addElement(new Tree(sonsSon,labelSon.toString(),-1.0));
				}
			}
			index++;
		}
		//Get the label
		while (newick.charAt(index)!=':' && newick.charAt(index)!=',' && newick.charAt(index)!=')' && newick.charAt(index)!=';') {
			labelParam.append(newick.charAt(index));
			index++;
		}
		/*if (labelParam.toString().equals("ORYZA"))
			System.out.println("founded");*/
		if (newick.charAt(index)==':') {
			//A length is specified
			index++;
			while (newick.charAt(index)!=',' && newick.charAt(index)!=')' && newick.charAt(index)!=';') {
				lengthParam.append(newick.charAt(index));
				index++;
			}
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
				if (intersection) {
					label = "'I_" + label.substring(1,label.length());
				} else {
					label = "'T_" + label.substring(1,label.length());
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
* @return		The number of topological incongruence duplication
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
		res.append("\n");
		if (!isLeaf()) {
			for (int i=0;i<sons.size();i++) {
				((Tree)(sons.elementAt(i))).toString(tab+1,res);
			}
		}
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

		if (!isLeaf() && label.contains("DUPLICATION")) {
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
			if (a>b) {
				res=a-b;
				double marge=ta.length+tb.length;
				if (res<marge*0.95) {
					ta.length=marge/2.0-res/2.0;
					tb.length=marge/2.0+res/2.0;
				} else {
					tb.length=marge*0.95;
					ta.length=marge*0.05;
				}
			} else {
				res=b-a;
				double marge=ta.length+tb.length;
				if (res<marge*0.95) {
					tb.length=marge/2.0-res/2.0;
					ta.length=marge/2.0+res/2.0;
				} else {
					ta.length=marge*0.95;
					tb.length=marge*0.05;
				}
			}
			res=Math.abs((a+ta.length)-(b+tb.length));
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
		if (!isLeaf() && (maxDepth-length)>=threshold) {
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
* Return true if this subtree contains only paralogy, and false otherwise
* @return True if this is an ultraparalogy node, false otherwise
*/
	public boolean ultraParalogy() {
		boolean res= true;
		if (!isLeaf()) {
			if (!label.contains("DUPLICATION")) {
				res= false;
			} else {
				int i=0;
				while (res && i<sons.size()) {
					Tree son= (Tree)(sons.elementAt(i));
					res= son.ultraParalogy();
					i++;
				}
			}
		}
		return res;
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
* Return true if this node is a leaf
* @return True if this node is a leaf and false if this node is internal
*/
	public boolean isLeaf() {
		return(sons==null || sons.size()==0);
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
		if (!isLeaf()) {
			for (int i=0;i<sons.size();i++) {
				((Tree)(sons.elementAt(i))).getNodes(res);;
			}
		}
		res.addElement(this);
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
* Reroot the whole tree, father parents included, to the branch upper this node.
* @return True if the tree needed to be rerooted, false if it were already rooted at the right place
*/
	public boolean reroot() {
		boolean res=true;
		if (father!=null && (father.father!=null || father.sons.size()>2)) {
			// Build new node, containing this node subtopology
			Tree node = null;
			if (length==-1.0) {
				node=new Tree(sons,label,-1.0);
			} else {
				node=new Tree(sons,label,length/2.0);

			}
			// Define new sons, containing the new node and the upper flipped topology
			Vector newSons=new Vector();
			newSons.addElement(node);
			father.getUpperTopology(this);
			if (length!=-1.0) {
				father.length=length/2.0;
			}
			newSons.addElement(father);
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
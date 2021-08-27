package rapgreen;
import java.util.*;
import java.io.*;


/**
 * Species dictionary.
 * <p>
 * Contains a collection of species, with scientific name, NCBI and TOL code.
 * @author Jean-Francois Dufayard
 * @version 1.0
 */
public class SpeciesDictionary {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************

	/**
	* Correspondance between 5 letters code and numerical code (NCBI for example)
	*/
	Hashtable numericalCode;

	/**
	* Correspondance between 5 letters code and scientific name
	*/
	Hashtable scientificName;

	/**
	* List of 5 letters codes, as unique identifier
	*/
	Vector code;

	/**
	* Correspondance between numerical code and 5 letters code
	*/
	Hashtable numericalCodeToCode;

	/**
	* Correspondance between numerical code and scientific name
	*/
	Hashtable numericalCodeToScientificName;

	/**
	* List of numerical codes, as unique identifier
	*/
	Vector code2;

	/**
	* Iterator to explore the dictionary
	*/
	private int iter;
	/**
	* Linked species phylogeny
	*/
	Tree speciesTree;

// ********************************************************************************************************************
// ***     CONSTRUCTORS     ***
// ****************************
/**
* Simple constructor, need to fill tables independantly
*/
	public SpeciesDictionary() {
		numericalCode= new Hashtable();
		scientificName= new Hashtable();
		numericalCodeToCode= new Hashtable();
		numericalCodeToScientificName= new Hashtable();
		code= new Vector();
		code2= new Vector();
	}

// ********************************************************************************************************************
// ***     OBJECT METHODS     ***
// ******************************
/**
* Add a new species in the dictionary
* @param c	The 5 letters string, coding for species
* @param s	The scientific name
* @param n	The numerical code
*/
	public void addSpecies(String c, String s, String n) {
		if (!scientificName.containsKey(c)) {
			// Do something only if the code doesn't yet exist in the dictionary
			code.addElement(c);
			if (s==null || s.length()==0) {
				scientificName.put(c,"N/A");
			} else {
				scientificName.put(c,s);
			}
			if (n==null || n.length()==0) {
				numericalCode.put(c,"N/A");
			} else {
				numericalCode.put(c,n);
			}


		}
		if (!numericalCodeToCode.containsKey(n)) {
			// Do something only if the code doesn't yet exist in the dictionary
			code2.addElement(n);
			if (c==null || c.length()==0) {
				numericalCodeToCode.put(n,"N/A");
			} else {
				numericalCodeToCode.put(n,c);
			}
			if (s==null || s.length()==0) {
				numericalCodeToScientificName.put(n,"N/A");
			} else {
				numericalCodeToScientificName.put(n,s);
			}
		}
	}

// ********************************************************************************************************************
/**
* Get the numerical code from the 5 letters code
* @param c	The 5 letters string, coding for species
* @return		The numerical code of the species c
*/
	public String getNumericalCode(String c) {
		String res=null;
		if (numericalCode.containsKey(c)) {
			res=(String)(numericalCode.get(c));
		} else {
			res="N/A";
		}
		return res;
	}

// ********************************************************************************************************************
/**
* Get the scientific name from the 5 letters code
* @param c	The 5 letters string, coding for species
* @return		The scientific name of the species c
*/
	public String getScientificName(String c) {
		String res=null;
		if (scientificName.containsKey(c)) {
			res=(String)(scientificName.get(c));
		} else {
			res="N/A";
		}
		return res;
	}

// ********************************************************************************************************************
/**
* Get the 5 letters code from the numerical code
* @param c	The numerical code of the species c
* @return		The 5 letters string, coding for species
*/
	public String get5LettersCode(String c) {
		String res=null;
		if (numericalCodeToCode.containsKey(c)) {
			res=(String)(numericalCodeToCode.get(c));
		} else {
			res="N/A";
		}
		return res;
	}

// ********************************************************************************************************************
/**
* Get the scientific name from the 5 letters code
* @param c	The numerical code, coding for species
* @return		The scientific name of the species c
*/
	public String getScientificNameFromNumerical(String n) {
		String res=null;
		if (numericalCodeToScientificName.containsKey(n)) {
			res=(String)(numericalCodeToScientificName.get(n));
		} else {
			res="N/A";
		}
		return res;
	}

// ********************************************************************************************************************
/**
* Get species list containing a parameter prefix
* @param prefix	The prefix
* @return		The species vector
*/
	public Vector prefixList(String prefix) {
		String pref= prefix.toUpperCase();
		Vector res= new Vector();
		for (int i=0;i<code.size();i++) {
			String local= (String)(code.elementAt(i));
			if (local.toUpperCase().startsWith(pref)) {
				res.addElement(local);
			}
		}
		return res;
	}

// ********************************************************************************************************************
/**
* Get species list containing a parameter prefix, regarding the scientific full name
* @param prefix	The prefix
* @return		The species vector
*/
	public Vector prefixScientificNameList(String prefix) {
		String pref= prefix.toUpperCase();
		Vector res= new Vector();
		for (int i=0;i<code.size();i++) {
			String local= (String)(code.elementAt(i));
			if (getScientificName(local).toUpperCase().startsWith(pref)) {
				res.addElement(local);
			}
		}
		return res;
	}

// ********************************************************************************************************************
/**
* Get species list containing a parameter prefix, regarding the scientific full name
* @param prefix	The prefix
* @return		The species vector
*/
	public Vector containsScientificNameList(String prefix) {
		String pref= prefix.toUpperCase();
		Vector res= new Vector();
		for (int i=0;i<code.size();i++) {
			String local= (String)(code.elementAt(i));
			if (getScientificName(local).toUpperCase().indexOf(pref)!=-1) {
				res.addElement(local);
			}
		}
		return res;
	}
// ********************************************************************************************************************
/**
* Test if a taxon is compatible with a constraint list, regarding a species tree
* @param taxonParam	The taxon to fit in constraints
* @param allowedConstraints	The table of positive constraints
* @param forbiddenConstraints	The table of positive constraints
* @param speciesTree	The reference species tree
* @return		True or false, regarding compatibility
*/
	public boolean isCompatible(String taxonParam,Hashtable allowedConstraints,Hashtable forbiddenConstraints) {
		boolean res= false;

		boolean founded=false;
		String taxon= taxonParam.substring(taxonParam.lastIndexOf("_")+1,taxonParam.length());
		//System.out.println(taxon);

		Tree runner= (Tree)(speciesTree.leafHashtable.get(taxon));
		//System.out.println(runner);
		//System.out.println(allowedConstraints.containsKey(runner.label) + " " + forbiddenConstraints.containsKey(runner.label));
		while (runner!=null && !founded) {
			if (allowedConstraints.containsKey(runner.label)) {
				founded=true;
				res=true;
			} else if (forbiddenConstraints.containsKey(runner.label)) {
				founded=true;
			}
			runner=runner.father;
		}

		//System.out.println(taxon);
		//System.out.println(" " + res);
		return res;
	}

// ********************************************************************************************************************
/**
* Test if a all taxa of a gene tree is compatible with a constraint list, regarding a species tree
* @param taxonParam	The taxon to fit in constraints
* @param allowedConstraints	The table of positive constraints
* @param forbiddenConstraints	The table of positive constraints
* @param speciesTree	The reference species tree
* @return		True or false, regarding compatibility
*/
	public boolean isCompatible(Tree treeParam,Hashtable allowedConstraints,Hashtable forbiddenConstraints) {
		boolean finalRes=true;

			//System.out.println(treeParam.leafVector.size());
		for (int i=0;i<treeParam.leafVector.size() && finalRes;i++) {

			String taxonParam=((Tree)(treeParam.leafVector.elementAt(i))).label;

			boolean res= false;

			boolean founded=false;
			String taxon= taxonParam.substring(taxonParam.lastIndexOf("_")+1,taxonParam.length());
			//System.out.print(taxon);

			Tree runner= (Tree)(speciesTree.leafHashtable.get(taxon));
			while (runner!=null && !founded) {
				if (allowedConstraints.containsKey(runner.label)) {
					founded=true;
					res=true;
				} else if (forbiddenConstraints.containsKey(runner.label)) {
					founded=true;
				}
				runner=runner.father;
			}

			finalRes=res;
			//System.out.println(taxon);
			//System.out.println(" " + res);
		}
		return finalRes;
	}

// ********************************************************************************************************************
/**
* Starts the iterator
*/
	public void start() {
		iter=0;
	}
// ********************************************************************************************************************
/**
* Link a species tree to a dictionnary
* @param the species tree value
*/
	public void setSpeciesTree(Tree speciesTree) {
		this.speciesTree=speciesTree;
	}

// ********************************************************************************************************************
/**
* Parse a dico file to fill this dictionary, parse the species tree if null
* @param the species dictionary
*/
	public void parseSpeciesDico(File speciesFile) {
		try {
			if (speciesFile==null) {
					Vector v= speciesTree.leafVector;
					for (int i=0;i<v.size();i++) {
							Tree n= (Tree)(v.elementAt(i));
							addSpecies(n.label,n.label,n.label);

					}

			} else {


					BufferedReader read= new BufferedReader(new FileReader(speciesFile));
					String s= read.readLine();
					while (s!=null) {
						String[] sp= s.split("\t");
						//System.out.println(sp[0] + " " + sp[2] + " " + sp[1]);
						addSpecies(sp[0],sp[2],sp[1]);
						s= read.readLine();
					}
					read.close();


			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

// ********************************************************************************************************************
/**
* Get the next species in the iterator
* @return		The next species
*/
	public String next() {
		String res=null;
		if (iter<code.size()) {
			res=(String)(code.elementAt(iter));
			iter++;
		}
		return res;
	}

}

package rapgreen;
import java.util.*;


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
* Starts the iterator
*/
	public void start() {
		iter=0;
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
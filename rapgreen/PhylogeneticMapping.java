package rapgreen;
import java.util.*;
import java.io.*;

/**
 * @author Jean-Francois Dufayard
 * @version 1.0
 */
public class PhylogeneticMapping {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************
/**
* Allow, or not, verbose mod
*/
	public static boolean verbose=false;
/** 
* Input tree files
*/
	public static String treeDirectory;

/**
* Input tree files
*/
	public static String clusterDirectory;

/**
* Input cluster file parsed names
*/
	public static String treeFileName;

/**
* Input cluster file parsed names
*/
	public static String clusterFileName;

/**
* Reference taxon name
*/
	public static String reference;

/**
* Number of families
*/
	public static int nb;

/**
* Support threshold
*/
	public static double support=0.8;


/**
* Output file
*/
	public static File output;

/**
* General stats file
*/
	public static File generalFile;

/**
* Trash file
*/
	public static File trashFile;

/**
* Congruence statistics
*/
	public static Hashtable congruenceTable;
	public static Hashtable mappingTable;
	public static Hashtable unmappingTable;
	public static Vector congruenceVector;
	public static Vector mappingVector;
	public static Vector unmappingVector;

/**
* USAGE:
* qsub -b y -q arcad.q /usr/local/jdk1.6.0_20/bin/java -mx4096m -cp /home/dufayard/RAP_green rapgreen/PhylogeneticMapping -clusterFiles /home/dufayard/uclust/clusters_0.6/ cluster#.fasta  -treeFiles /home/dufayard/uclust/clusters_0.6_rap/ cluster#_genetree.tre -n 37128 -reference SATIVA -output 0.6_allstats.csv -general 0.6_globalstats.txt -trash 0.6_trash.csv
*/

// ********************************************************************************************************************
// ***     MAIN     ***
// ********************
	public static void main(String[] args) {
		congruenceTable=new Hashtable();
		mappingTable=new Hashtable();
		unmappingTable=new Hashtable();
		congruenceVector= new Vector();
		mappingVector= new Vector();
		unmappingVector= new Vector();
		try {
			for (int i=0;i<args.length;i=i+2) {
				if (args[i].equalsIgnoreCase("-treeFiles")) {
					treeDirectory= args[i+1];
					treeFileName= args[i+2];
					i++;
				}
				if (args[i].equalsIgnoreCase("-clusterFiles")) {
					clusterDirectory= args[i+1];
					clusterFileName= args[i+2];
					i++;
				}
				if (args[i].equalsIgnoreCase("-n")) {
					nb= (new Integer(args[i+1])).intValue();
				}
				if (args[i].equalsIgnoreCase("-support")) {
					support= (new Double(args[i+1])).doubleValue();
				}
				if (args[i].equalsIgnoreCase("-reference")) {
					reference= args[i+1];
				}
				if (args[i].equalsIgnoreCase("-output")) {
					output= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-general")) {
					generalFile= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-trash")) {
					trashFile= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-verbose")) {
					verbose=true;
					i--;
				}
			}
			Vector idRef= new Vector();
			Vector idSeq= new Vector();
			Vector support= new Vector();
			Vector groupD= new Vector();
			Vector familyD= new Vector();
			Vector nbGr= new Vector();
			Vector congruence= new Vector();
			Vector trash= new Vector();
			Vector cluster= new Vector();

			String talk="";

			for (int i=0;i<nb;i++) {
				BufferedReader readCluster= new BufferedReader(new FileReader(new File(clusterDirectory + clusterFileName.replace("#",(new Integer(i)).toString()))));
				BufferedReader readTree=null;
				String newick=";";
				if ((new File(treeDirectory + treeFileName.replace("#",(new Integer(i)).toString()))).exists()) {
					readTree= new BufferedReader(new FileReader(new File(treeDirectory + treeFileName.replace("#",(new Integer(i)).toString()))));
					StringBuffer newickBuffer = new StringBuffer();
					String s= readTree.readLine();
					while (!s.endsWith(";")) {
						newickBuffer.append(s);
						s= readTree.readLine();
					}
					newickBuffer.append(s);
					newick= newickBuffer.toString();
				}
				if (newick.length()>2) {
					//we have a tree
					Tree current= new Tree(newick);
					current.pretreatment();
					parseTree(i,current,idRef,idSeq,support,groupD,familyD,nbGr,congruence,cluster,trash);


				} else {
					//we don't have a tree
					parseCluster(i,readCluster,idRef,idSeq,support,groupD,familyD,nbGr,congruence,cluster,trash);


				}
				if (i%10==0) {
					for (int j=0;j<talk.length();j++) {
						System.out.print("\b");
					}
					talk= i + "/" + nb + " clusters analysed.";
					System.out.print(talk);
				}
				if (readTree!=null) 
					readTree.close();
				readCluster.close();

			}
			BufferedWriter write= new BufferedWriter(new FileWriter(output));
			write.write("SEQUENCE\tREFERENCE\tSUP\tG-DIV\tF-DIV\tN-GR\tCONG\tCLUSTER\n");
			write.flush();
			for (int i=0;i<idRef.size();i++) {
				StringBuffer toWrite= new StringBuffer();
				toWrite.append((String)(idSeq.elementAt(i)));
				toWrite.append("\t");
				toWrite.append((String)(idRef.elementAt(i)));
				toWrite.append("\t");
				toWrite.append((String)(support.elementAt(i)));
				toWrite.append("\t");
				toWrite.append((String)(groupD.elementAt(i)));
				toWrite.append("\t");
				toWrite.append((String)(familyD.elementAt(i)));
				toWrite.append("\t");
				toWrite.append((String)(nbGr.elementAt(i)));
				toWrite.append("\t");
				toWrite.append((String)(congruence.elementAt(i)));
				toWrite.append("\t");
				toWrite.append((String)(cluster.elementAt(i)));
				toWrite.append("\n");
				write.write(toWrite.toString());
				write.flush();
			}



			write.close();
			write= new BufferedWriter(new FileWriter(trashFile));

			for (int i=0;i<trash.size();i++) {
				StringBuffer toWrite= new StringBuffer();
				toWrite.append((String)(trash.elementAt(i)));
				toWrite.append("\n");
				write.write(toWrite.toString());
				write.flush();
			}



			write.close();
			write= new BufferedWriter(new FileWriter(generalFile));
			write.write("Global incongruence statistics:\n");
			write.flush();
			for (int i=0;i<congruenceVector.size();i++) {
				String tax= (String)(congruenceVector.elementAt(i));
				write.write(tax + "\t" + ((Integer)(congruenceTable.get(tax))).intValue() + "\n");
				write.flush();
			}
			write.write("\nGlobal mapping statistics:\n");
			write.flush();
			for (int i=0;i<mappingVector.size();i++) {
				String tax= (String)(mappingVector.elementAt(i));
				write.write(tax + "\t" + ((Integer)(mappingTable.get(tax))).intValue() + "\n");
				write.flush();
			}
			write.write("\nGlobal unmapping statistics:\n");
			write.flush();
			for (int i=0;i<unmappingVector.size();i++) {
				String tax= (String)(unmappingVector.elementAt(i));
				write.write(tax + "\t" + ((Integer)(unmappingTable.get(tax))).intValue() + "\n");
				write.flush();
			}
			write.close();
		} catch(Exception e) {
			e.printStackTrace();
		}


	}



// ********************************************************************************************************************
/**
*	Cluster parsing, from a fasta file
*/
	private static void parseCluster(int idCluster,BufferedReader readCluster,Vector idRef,Vector idSeq,Vector support,Vector groupD,Vector familyD,Vector nbGr,Vector congruence, Vector cluster, Vector trash) {
		try {
			String s= readCluster.readLine();
			Vector target= new Vector();
			Vector source= new Vector();
			while (s!=null) {
				if (s.startsWith(">")) {
					String id= s.substring(1,s.length());
					String taxon= s.substring(s.lastIndexOf("_")+1,s.length());
					if (taxon.equalsIgnoreCase(reference)) {
						source.addElement(id);
					} else {
						target.addElement(id);
					}
				}
				s= readCluster.readLine();
			}
			if (source.size()==1 && target.size()==1) {
				idSeq.addElement((String)(target.elementAt(0)));
				idRef.addElement((String)(source.elementAt(0)));
				support.addElement("N/A");
				groupD.addElement("N/A");
				familyD.addElement("N/A");
				nbGr.addElement("N/A");
				congruence.addElement("N/A");
				cluster.addElement((new Integer(idCluster)).toString());
				String tax= ((String)(target.elementAt(0))).substring(((String)(target.elementAt(0))).lastIndexOf("_")+1,((String)(target.elementAt(0))).length());
				if (mappingTable.containsKey(tax)) {
					int loc= ((Integer)(mappingTable.get(tax))).intValue();
					loc++;
					mappingTable.put(tax,new Integer(loc));
				} else {
					mappingTable.put(tax,new Integer(1));
					mappingVector.addElement(tax);
				}

			} else {

				if (verbose) {
					System.out.print("Warning, no tree for unresolved cluster " + idCluster + ":");

					for (int i=0;i<source.size();i++) {
						System.out.print(" ");
						System.out.print((String)(source.elementAt(i)));
					}
				}
				for (int i=0;i<target.size();i++) {
					if (verbose) {
						System.out.print(" ");
						System.out.print((String)(target.elementAt(i)));
					}
					String tax= ((String)(target.elementAt(i))).substring(((String)(target.elementAt(i))).lastIndexOf("_")+1,((String)(target.elementAt(i))).length());
					if (unmappingTable.containsKey(tax)) {
						int loc= ((Integer)(unmappingTable.get(tax))).intValue();
						loc++;
						unmappingTable.put(tax,new Integer(loc));
					} else {
						unmappingTable.put(tax,new Integer(1));
						unmappingVector.addElement(tax);
					}
					trash.addElement((String)(target.elementAt(i)) + "\tnotree\t" + idCluster);
				}
				if (verbose) {
					System.out.print("\n");
				}

			}
		} catch(Exception e) {
			e.printStackTrace();
		}


	}

// *************************************
/**
*	Tree parsing, from a newick file
*/
	private static void parseTree(int idCluster,Tree current,Vector idRef,Vector idSeq,Vector support,Vector groupD,Vector familyD,Vector nbGr,Vector congruence, Vector cluster, Vector trash) {



		if (current.isLeaf()) {
			if (!current.label.endsWith(reference)) {
				trash.addElement(current.label + "\tnosignal\t" + idCluster);

				String tax= current.label.substring(current.label.lastIndexOf("_")+1,current.label.length());
				if (unmappingTable.containsKey(tax)) {
					int loc= ((Integer)(unmappingTable.get(tax))).intValue();
					loc++;
					unmappingTable.put(tax,new Integer(loc));
				} else {
					unmappingTable.put(tax,new Integer(1));
					unmappingVector.addElement(tax);
				}
			}
		} else if (unreferenced(current)) {
			for (int i=0;i<current.leafVector.size();i++) {
				Tree leaf= (Tree)(current.leafVector.elementAt(i));
				trash.addElement(leaf.label + "\tnosignal\t" + idCluster);
				String tax= leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length());
				if (unmappingTable.containsKey(tax)) {
					int loc= ((Integer)(unmappingTable.get(tax))).intValue();
					loc++;
					unmappingTable.put(tax,new Integer(loc));
				} else {
					unmappingTable.put(tax,new Integer(1));
					unmappingVector.addElement(tax);
				}
			}
		} else  {
			if (mappable(current)) {
				Vector target= new Vector();
				Vector source= new Vector();
				for (int i=0;i<current.leafVector.size();i++) {
					Tree leaf= (Tree)(current.leafVector.elementAt(i));
					if (leaf.label.endsWith(reference)) {
						source.addElement(leaf);
					} else {
						target.addElement(leaf);

					}

				}
				for (int i=0;i<target.size();i++) {
					Tree leafTarget=((Tree)(target.elementAt(i)));
					Tree leafSource=((Tree)(source.elementAt(0)));
					try {
						idSeq.addElement(leafTarget.label);
						idRef.addElement(leafSource.label);
					} catch(Exception e) {
						for (int j=0;j<current.leafVector.size();j++) {
							Tree leaf= (Tree)(current.leafVector.elementAt(j));
							System.out.println(leaf.label);
						}
						e.printStackTrace();
						System.exit(0);
					}
					String supString="";
					if (current.label.contains("_")) {
						supString=current.label.substring(current.label.lastIndexOf("_") + 1 , current.label.length()-1);
					} else {
						supString=current.label;
					}
					if (supString.length()>0) {
						if (supString.length()>5) {
							supString=supString.substring(0,5);
						}

						support.addElement(supString);
					} else {
						support.addElement("N/A");
					}


					double doubleGroupD=((Tree)(current.sons.elementAt(0))).maxDepth+((Tree)(current.sons.elementAt(1))).maxDepth;
					doubleGroupD=doubleGroupD*100000.0;
					int doubleGroupDInt=(int)doubleGroupD;
					doubleGroupD=((double)doubleGroupDInt)/100000.0;
					groupD.addElement((new Double(doubleGroupD)).toString());

					//go to root and get informations
					Tree root= current;
					while (root.father!=null && root.father!=root) {
						root=root.father;
					}
					double doubleFamilyD=((Tree)(root.sons.elementAt(0))).maxDepth+((Tree)(root.sons.elementAt(1))).maxDepth;
					doubleFamilyD=doubleFamilyD*100000.0;
					int doubleFamilyDInt=(int)doubleFamilyD;
					doubleFamilyD=((double)doubleFamilyDInt)/100000.0;
					familyD.addElement((new Double(doubleFamilyD)).toString());

					int nbGrInt=0;
					for (int j=0;j<root.leafVector.size();j++) {
						if (((Tree)(root.leafVector.elementAt(j))).label.endsWith(reference)) {
							nbGrInt++;
						}
					}
					nbGr.addElement((new Integer(nbGrInt)).toString());

					Boolean congBool= new Boolean(!current.lastCommonAncestor(leafTarget,leafSource).label.contains("D"));

					String tax= leafTarget.label.substring(leafTarget.label.lastIndexOf("_")+1,leafTarget.label.length());

					if (congBool.booleanValue()==false) {
						if (congruenceTable.containsKey(tax)) {
							int loc= ((Integer)(congruenceTable.get(tax))).intValue();
							loc++;
							congruenceTable.put(tax,new Integer(loc));
						} else {
							congruenceTable.put(tax,new Integer(1));
							congruenceVector.addElement(tax);
						}
					}
					if (mappingTable.containsKey(tax)) {
						int loc= ((Integer)(mappingTable.get(tax))).intValue();
						loc++;
						mappingTable.put(tax,new Integer(loc));
					} else {
						mappingTable.put(tax,new Integer(1));
						mappingVector.addElement(tax);
					}
					String congString=congBool.toString();

					congruence.addElement(congString);
					cluster.addElement((new Integer(idCluster)).toString());
				}

			} else {
				for (int i=0;i<current.sons.size();i++) {
					parseTree(idCluster,(Tree)(current.sons.elementAt(i)),idRef,idSeq,support,groupD,familyD,nbGr,congruence,cluster,trash);
				}
			}
		}


	}


	private static boolean unreferenced(Tree t) {
		boolean res=true;
		int i=0;
		while (i<t.leafVector.size() && res) {
			Tree leaf= (Tree)(t.leafVector.elementAt(i));
			if (leaf.label.endsWith(reference)) {
				res=false;
			}
			i++;
		}
		return res;
	}

	private static boolean mappable(Tree t) {
		boolean res=true;
		String supString="";
		if (t.label.contains("_")) {
			supString=t.label.substring(t.label.lastIndexOf("_") + 1 , t.label.length()-1);
		} else {
			supString=t.label;
		}
		double sup=-1.0;
		//try {
		if (supString.length()>0) {
			sup= (new Double(supString)).doubleValue();
		}
		//} catch(Exception e) {
		//	System.out.println(supString);
		//}
		if (sup!=-1.0 && sup<support) {
			res=false;
		}

		int i=0;
		Hashtable mem= new Hashtable();
		while (i<t.leafVector.size() && res) {
			Tree leaf= (Tree)(t.leafVector.elementAt(i));
			String taxon= leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length());
			if (mem.containsKey(taxon)) {
				res=false;
			} else {
				mem.put(taxon,"");
			}
			i++;
		}
		return res;
	}

}
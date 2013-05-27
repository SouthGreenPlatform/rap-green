
package rapgreen;
import java.util.*;
import java.io.*;

public class Temp {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************




// ********************************************************************************************************************
// ***     MAIN     ***
// ********************
	public static void main(String[] args) {
		try {
			if (args.length>1 && args[1].equals("classify")) {
				BufferedReader read= new BufferedReader(new FileReader(new File(args[2])));
				String s= read.readLine();
				Hashtable kingdom= new Hashtable();
				Hashtable phylum= new Hashtable();
				while (s!=null) {
					String[] elems= s.split("\t");
					kingdom.put(elems[0],elems[1]);
					phylum.put(elems[0],elems[2]);
					s= read.readLine();
				}
				read.close();
				
				Vector kingdomVector= new Vector();
				Hashtable nbTreesTable= new Hashtable();
				Hashtable nbSequencesTable= new Hashtable();
				
				File[] files= (new File(args[0])).listFiles();
				for (int j=0;j<files.length;j++) {
					try {						
						BufferedReader buf= new BufferedReader(new FileReader(files[j]));
						String test= buf.readLine();
						buf.close();
						if (test.length()>1 && !test.startsWith(";")) {
							Tree tree= new Tree(test);
							tree.pretreatment();
							String localKingdom="";
							for (int i=0;i<tree.leafVector.size() && !localKingdom.equals("multi");i++) {
								Tree leaf= (Tree)(tree.leafVector.elementAt(i));
								if (kingdom.containsKey(leaf.label)) {
									String l=(String)(kingdom.get(leaf.label));
									if (localKingdom.equals("")) {
										localKingdom=l;	
									}
									if (!localKingdom.equals(l)) {
										localKingdom="multi";
									}
									
								}
							}
													
							if (nbTreesTable.containsKey(localKingdom)) {
								int lTrees= ((Integer)(nbTreesTable.get(localKingdom))).intValue();
								int lSequences= ((Integer)(nbSequencesTable.get(localKingdom))).intValue();
								nbTreesTable.put(localKingdom,new Integer(1+lTrees));
								nbSequencesTable.put(localKingdom,new Integer(tree.leafVector.size()+lSequences));
							} else {
								kingdomVector.addElement(localKingdom);
								(new File(localKingdom)).mkdir();
								nbTreesTable.put(localKingdom,new Integer(1));
								nbSequencesTable.put(localKingdom,new Integer(tree.leafVector.size()));
							}	
							BufferedWriter write= new BufferedWriter(new FileWriter(new File(localKingdom + "/" + ((Integer)(nbTreesTable.get(localKingdom))).intValue() + ".tree")));
							write.write(test + "\n");
							write.flush();
							write.close();
						}
					} catch(Exception exp) {
						System.out.println(files[j].getName());
						exp.printStackTrace();
					}	
					if (j%1000==0) {
						System.out.println(j);
					}			
					
				}
				
				System.out.println("KINGDOM\tNBTREES\tNBSEQUENCES");
				for (int i=0;i<kingdomVector.size();i++) {
					String localKingdom=(String)(kingdomVector.elementAt(i));
					int lTrees= ((Integer)(nbTreesTable.get(localKingdom))).intValue();
					int lSequences= ((Integer)(nbSequencesTable.get(localKingdom))).intValue();
					System.out.println(localKingdom + "\t" + lTrees + "\t" + lSequences);
					
				}				
				
			} else if (args.length>1 && args[1].equals("dico")) {
				BufferedReader read= new BufferedReader(new FileReader(new File(args[2])));
				String s= read.readLine();
				Hashtable dico= new Hashtable();
				while (s!=null) {
					String[] splited= s.split("\t");
					dico.put(splited[1].substring(0,splited[1].lastIndexOf("_")),splited[3]);
					s= read.readLine();
				}
				read.close();
				TreeReader treeReader= new TreeReader(new File(args[0]),TreeReader.NEWICK);
				Tree tree= treeReader.nextTree();
				tree.pretreatment();
				
				for (int i=0;i<tree.leafVector.size();i++) {
					Tree leaf= (Tree)(tree.leafVector.elementAt(i));
					
					if (dico.containsKey(leaf.label.substring(0,leaf.label.lastIndexOf("_")))) {
						leaf.label=leaf.label.substring(0,leaf.label.lastIndexOf("_")+1) + (String)(dico.get(leaf.label.substring(0,leaf.label.lastIndexOf("_")))) + "LRR_" + leaf.label.substring(leaf.label.lastIndexOf("_")+1,leaf.label.length());
						
					} else {
						System.out.println("Warning: " + leaf.label);
					}
						
						
				}	
				System.out.println(tree.getNewick());		
			} else if (args.length>1 && args[1].equals("formatsimple")) {
				TreeReader treeReader= new TreeReader(new File(args[0]),TreeReader.NEWICKTITLED);
				Tree tree= treeReader.nextTree();
				int num=1;
				while (tree!=null) {
					tree.pretreatment();
					for (int i=0;i<tree.leafVector.size();i++) {
						Tree leaf= (Tree)(tree.leafVector.elementAt(i));					
						//leaf.label= leaf.label.substring(0,leaf.label.indexOf("_"));			
					}
					BufferedWriter write= new BufferedWriter(new FileWriter(new File(num + ".tree")));
					write.write(tree.getNewick() + "\n");
					write.flush();
					write.close();
					tree= treeReader.nextTree();
					num++;
					if (num%1000==0) {
						System.out.println(num);	
					}	
				}
				
						
			}  else if (args.length>1 && args[1].equals("format")) {
				TreeReader treeReader= new TreeReader(new File(args[0]),TreeReader.NEWICKTITLED);
				Tree tree= treeReader.nextTree();
				int num=1;
				while (tree!=null) {
					tree.pretreatment();
					for (int i=0;i<tree.leafVector.size();i++) {
						Tree leaf= (Tree)(tree.leafVector.elementAt(i));					
						leaf.label= leaf.label.substring(0,leaf.label.indexOf("_"));			
					}
					BufferedWriter write= new BufferedWriter(new FileWriter(new File(num + ".tree")));
					write.write(tree.getNewick() + "\n");
					write.flush();
					write.close();
					tree= treeReader.nextTree();
					num++;
					if (num%1000==0) {
						System.out.println(num);	
					}	
				}
				
						
			} else if (args.length>1 && args[1].equals("cut")) {
				TreeReader treeReader= new TreeReader(new File(args[0]),TreeReader.NEWICK);
				Tree tree= treeReader.nextTree();
				tree.pretreatment();
				
				for (int i=0;i<tree.leafVector.size();i++) {
					Tree leaf= (Tree)(tree.leafVector.elementAt(i));
					
					leaf.label= leaf.label.substring(0,leaf.label.indexOf("_"));
					while (leaf.label.charAt(0)=='0') {
						
						leaf.label= leaf.label.substring(1,leaf.label.length());
					}
						
						
				}	
				System.out.println(tree.getNewick());		
			} else if (args.length>2 && args[2].equals("check")) {
				BufferedReader read= new BufferedReader(new FileReader(new File(args[1])));
				Hashtable table= new Hashtable();
				
				String s= read.readLine();
				
				while (s!=null) {
					table.put(s,"1");
					
					s= read.readLine();
				}
				
				read.close();
				TreeReader treeReader= new TreeReader(new File(args[0]),TreeReader.NEWICK);
				Tree tree= treeReader.nextTree();
				tree.pretreatment();
				
				for (int i=0;i<tree.leafVector.size();i++) {
					Tree leaf= (Tree)(tree.leafVector.elementAt(i));
					
					if (!table.containsKey(leaf.label)) {
						System.out.println(leaf.label);
						
					}				
				}			
			} else {
				boolean prefix=false;
				if (args.length>1 && args[1].equals("prefix")) {
					prefix=true;
					
				}
				BufferedReader read= new BufferedReader(new FileReader(new File("types.csv")));
				Hashtable table= new Hashtable();
				
				String s= read.readLine();
				
				while (s!=null) {
					String[] splited= s.split("\t");
					table.put(splited[0].substring(0,splited[0].lastIndexOf("_")),splited[1]);
					
					s= read.readLine();
				}
				
				read.close();
				TreeReader treeReader= new TreeReader(new File(args[0]),TreeReader.NEWICK);
				Tree tree= treeReader.nextTree();
				tree.pretreatment();
				
				for (int i=0;i<tree.leafVector.size();i++) {
					Tree leaf= (Tree)(tree.leafVector.elementAt(i));
					String id= null;
					if (prefix) {
						id=leaf.label.substring(leaf.label.indexOf("_")+1,leaf.label.lastIndexOf("_"));
					} else {
						id=leaf.label.substring(0,leaf.label.lastIndexOf("_"));
					}
					//System.out.println(id);
					if (table.containsKey(id)) {
						leaf.label=id + "_" + table.get(id) + "_" + leaf.label.substring(leaf.label.lastIndexOf("_")+1, leaf.label.length());
						//System.out.println(leaf.label);
						
					}				
				}
				
				
				System.out.println(tree.getNewick());
			
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}







}
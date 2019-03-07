
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
			if (args.length>1 && args[1].equals("cleanAnne")) {
				BufferedReader read2= new BufferedReader(new FileReader(new File(args[0])));
				String s2= read2.readLine();
				

				
				
				BufferedReader read= new BufferedReader(new FileReader(new File(args[2])));
				String s= read.readLine();
				Vector ref= new Vector();
				while (s!=null) {
					/*if (s.indexOf("_SG")!=-1) {
						s=s.substring(1,s.indexOf("_SG"));
					} else {
						s=s.substring(1,s.indexOf("_Non_LRR"));
					}
					
					if (s2.indexOf(s.substring())!=-1) {
					
					}*/
					try {
					if (s.indexOf("_SG")!=-1) {
						ref.addElement(s.substring(1,s.indexOf("_SG")));
					
					} else {
						ref.addElement(s.substring(1,s.indexOf("_Non_LRR")+8));
					}
					} catch(Exception exp) {
						System.out.println("error in:"+s);
					}
					s= read.readLine();
				}		
				
				Tree tree= new Tree(s2);
				tree.pretreatment();	
				
				for (int i=0;i<tree.leafVector.size();i++) {
					Tree leaf= (Tree)(tree.leafVector.elementAt(i));
					for (int j=0;j<ref.size();j++) {
						String lab= (String)(ref.elementAt(j));
						if (lab.indexOf(leaf.label)!=-1 && !lab.equalsIgnoreCase(leaf.label)) {
							//System.out.println("Conversion:" + leaf.label + " >> " + lab);
							leaf.label=lab;
						}
					}
					if (leaf.label.endsWith("Non_LRR")) {
						leaf.label="No_LRR_"+leaf.label.substring(0,leaf.label.indexOf("_Non_LRR"));
					}
					leaf.label=leaf.label.replace("ARATH1","ARATH");
					leaf.label=leaf.label.replace("ORYSI1","ORYSI");
					leaf.label=leaf.label.replace("ORYSJ1","ORYSJ");
				}
							
				System.out.println(tree.getNewick());			
			
			} else if (args.length>1 && args[1].equals("classify")) {
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
				
			} else if (args.length>1 && args[1].equals("formatGenomicus")) {
				File[] files= (new File(args[0])).listFiles();
				
				BufferedReader buf= new BufferedReader(new FileReader(args[4]));
				Hashtable duplications= new Hashtable();
				String test= buf.readLine();
				test= buf.readLine();
				while (test!=null) {
					String[] tests=test.split("	");
					duplications.put(tests[0] + "#" + tests[2],"ok");
					test= buf.readLine();
				}
				buf.close();
				
				
				TreeReader treeReader= new TreeReader(new File(args[2]),TreeReader.NEWICK);
				Tree speciesTree= treeReader.nextTree();
				speciesTree.pretreatment();
				for (int j=0;j<files.length;j++) {
					try {						
						buf= new BufferedReader(new FileReader(files[j]));
						test= buf.readLine();
						buf.close();
						if (test.length()>1 && !test.startsWith(";")) {
							Tree tree= new Tree(test);
							tree.pretreatment();
							for (int i=0;i<tree.leafVector.size();i++) {
								Tree leaf= (Tree)(tree.leafVector.elementAt(i));
								int k=3;
								boolean founded=false;
								while (k+2<args.length && !founded) {
									k+=2;
									founded=leaf.label.startsWith(args[k]);
								}
								if (!founded) {
									System.out.println("Warning: " + files[j].getName() + " " + leaf.label + " ; unknown species");
								} else {
									leaf.label= leaf.label.substring(args[k].length()+1,leaf.label.length()) + "_" + args[k+1];
								}
							}
													
							
							BufferedWriter write= new BufferedWriter(new FileWriter(new File(args[3] + "/" + files[j].getName())));
							write.write(tree.getNHXNewick(speciesTree, duplications,files[j].getName().substring(0,files[j].getName().lastIndexOf("_")) ) + "\n");
							write.flush();
							write.close();
						}
					} catch(Exception exp) {
						System.out.println(files[j].getName());
						exp.printStackTrace();
					}	
					if (j%100==0) {
						System.out.println(j);
					}			
					
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
					table.put(splited[0].substring(0,splited[0].indexOf("_")),splited[1]);
					
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
					String trans="";
					if (id.indexOf(".")!=-1) {
						trans=id.substring(id.lastIndexOf("."),id.length());
						id= id.substring(0,id.lastIndexOf("."));
					}
					//System.out.println(id);
					if (table.containsKey(id)) {
						leaf.label=id + trans +  "_" + table.get(id) + "_" + leaf.label.substring(leaf.label.lastIndexOf("_")+1, leaf.label.length());
						//System.out.println("ok" + leaf.label);
						
					}				
				}
				
				
				System.out.println(tree.getNewick());
			
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}







}
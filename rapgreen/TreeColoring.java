package rapgreen;
import java.util.*;
import java.io.*;

/**
 * @author Jean-Francois Dufayard
 * @version 1.0
 * Display rootings of a tree
 */
public class TreeColoring {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************
/**
* Input species file
*/
	public static File input;
	

/**
* Input species file
*/
	public static File nexml;
/**
* Input species file
*/
	public static File species;
/**
* Duplications in CSV format as Orthofinder format
*/
	public static File duplicationsFile;
/**
* Output rootings file
*/
	public static File output;

	public static Hashtable codingForMeta;
	public static Hashtable codingForMetaAlt;
	public static Vector lines;


   private static final String NORMAL     = "\u001b[0m";
   private static final String BOLD       = "\u001b[1m";
   private static final String UNDERLINE  = "\u001b[4m";
// ********************************************************************************************************************
// ***     MAIN     ***
// ********************
	public static void main(String[] args) {
		String s=null;
		boolean uniqueid=false;
		boolean nexml2newick=false;
		boolean formatdb=false;
		boolean colordb=false;
		boolean formatorthofinderdb=false;
		Hashtable taxaTranslationTable= new Hashtable();
		Vector taxaTranslationVector= new Vector();
		int locArg=0;
		try {
			for (int i=0;i<args.length;i=i+2) {
				if (args[i].contains("help")) {
					System.out.println(BOLD);
					System.out.println("NAME:");
					System.out.println(NORMAL);
					System.out.println("\t- TreeColoring v1.0 -");
					System.out.println(BOLD);
					System.out.println("SYNOPSIS:");
					System.out.println(NORMAL);
					System.out.println("\tjava -jar TreeColoring.jar [command args]");
					System.out.println(BOLD);
					System.out.println("OPTIONS:");					
					System.out.println(BOLD);
					System.out.println("-colordb" + NORMAL + " " + UNDERLINE + "tree_file\n\t" + NORMAL + "The input multi-newick tree file that needs to be colored");
					System.out.println(BOLD);
					System.out.println("-formatdb" + NORMAL + " " + UNDERLINE + "tree_directory\n\t" + NORMAL + "The input newick tree directory to convert into databank");
					System.out.println(BOLD);
					System.out.println("-formatorthofinderdb" + NORMAL + " " + UNDERLINE + "tree_directory" + NORMAL + " " + UNDERLINE + "duplications" + NORMAL + " " + UNDERLINE + "[species_name Uniprot5lettersCode]*\n\t" + NORMAL + "The input newick tree directory to convert into databank, the species tree, the duplications, and optionally species names with their uniprot code correspondance");
					System.out.println(BOLD);					
					System.out.println("-newick" + NORMAL + " " + UNDERLINE + "tree_file\n\t" + NORMAL + "The input newick tree file");
					System.out.println(BOLD);
					System.out.println("-nexml" + NORMAL + " " + UNDERLINE + "tree_file\n\t" + NORMAL + "The input nexml tree file");
					System.out.println(BOLD);
					System.out.println("-output" + NORMAL + " "  + UNDERLINE + "tree_file\n\t" + NORMAL + "The output tree file or tree databank depending input option");					
					System.out.println(BOLD);
					System.out.println("-prefix" + NORMAL + " "  + UNDERLINE + "prefix" + NORMAL + " "  + UNDERLINE + "taxa\n\t" + NORMAL + "A prefix to be translated to a specific taxa, in sequence name.");
					System.out.println(BOLD);					
					System.out.println("-uniqueid\n\t" + NORMAL + "produce a tree with unique labels, before coloration");
					System.out.println(BOLD);
					
					System.out.println("-species" + NORMAL + " "  + UNDERLINE + "species_tree_file\n\t" + NORMAL + "The input file containing colored extended-newick tree\n\n");
					System.exit(0);
				}
				if (args[i].equalsIgnoreCase("-colordb")) {
					input= new File(args[i+1]);
					colordb=true;
				}				
				if (args[i].equalsIgnoreCase("-newick")) {
					input= new File(args[i+1]);
				}					
				if (args[i].equalsIgnoreCase("-formatdb")) { 
					input= new File(args[i+1]);
					formatdb=true;
				}				
				if (args[i].equalsIgnoreCase("-formatorthofinderdb")) {
					input= new File(args[i+1]);
					duplicationsFile= new File(args[i+2]);
					locArg= i+1;
					formatorthofinderdb=true;
					while (i+2<args.length && !args[i+2].startsWith("-"))
						i++;
				}			
				/*if (args[i].equalsIgnoreCase("-nexml")) {
					nexml= new File(args[i+1]);
				}		*/		
				if (args[i].equalsIgnoreCase("-species")) {
					species= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-prefix")) {
					taxaTranslationTable.put(args[i+1],args[i+2]);
					taxaTranslationVector.addElement(args[i+1]);
					i++;
				}				
				/*if (args[i].equalsIgnoreCase("-uniqueid")) {
					uniqueid= true;
					i--;
				}			*/	
				if (args[i].equalsIgnoreCase("-output")) {
					output= new File(args[i+1]);
				}
			}

			if (colordb) {
				BufferedReader read= new BufferedReader(new FileReader(input));	
				BufferedWriter write= new BufferedWriter(new FileWriter(output));
				s=read.readLine();
				TreeReader reader= new TreeReader(species,TreeReader.NEWICK);
				Tree speciesTree= reader.nextTree();
				speciesTree.pretreatment();
				while (s!=null) {
					String localname= s.substring(0,s.indexOf(" "));
					String localnewick= s.substring(s.indexOf(" ")+1,s.length());
					System.out.print("Coloring " + localname + " file... ");	
			
					Tree tree= new Tree(localnewick);
					tree.pretreatment();
			
					color(tree,speciesTree);			
			
					write.write(localname + " " + tree.getNewick() + "\n");
					write.flush();
					System.out.println("Done.");				
					s=read.readLine();
				
				}
				
				read.close();
				write.close();
				System.exit(0);
			} else if (formatdb) {
				File[] entries= input.listFiles();	
				BufferedWriter write= new BufferedWriter(new FileWriter(output));
				TreeReader reader= new TreeReader(species,TreeReader.NEWICK);
				Tree speciesTree= reader.nextTree();
				speciesTree.pretreatment();
				for (int i=0; i< entries.length;i++) {
				
					System.out.print("Coloring " + entries[i].getName() + " file... ");	
			
					reader= new TreeReader(entries[i],TreeReader.NEWICK);
					Tree tree= reader.nextTree();
					tree.pretreatment();
			
					color(tree,speciesTree);			
			
					write.write(entries[i].getName().substring(0,entries[i].getName().lastIndexOf(".")) + " " + tree.getNewick() + "\n");
					write.flush();
					System.out.println("Done.");				
				
				
				}
				
			
				write.close();
				System.exit(0);
			} else if (formatorthofinderdb) {
				File[] entries= input.listFiles();	
				BufferedWriter write= new BufferedWriter(new FileWriter(output));
				TreeReader reader= new TreeReader(species,TreeReader.NEWICK);
				Tree speciesTree= reader.nextTree();
				speciesTree.pretreatment();
				
				BufferedReader buf= new BufferedReader(new FileReader(args[2]));
				Hashtable duplications= new Hashtable();
				String test= buf.readLine();
				test= buf.readLine();
				while (test!=null) {
					String[] tests=test.split("	");
					duplications.put(tests[0] + "#" + tests[2],"ok");
					test= buf.readLine();
				}
				buf.close();
				
				for (int i=0; i< entries.length;i++) {
					
					
					
					//System.out.print("Coloring " + entries[i].getName() + " file... ");	
			
					reader= new TreeReader(entries[i],TreeReader.NEWICK);
					Tree tree= reader.nextTree();
					tree.pretreatment();
			
					for (int j=0;j<tree.leafVector.size();j++) {
						Tree leaf= (Tree)(tree.leafVector.elementAt(j));
						int k=locArg;
						boolean founded=false;
						while (k+2<args.length && !founded) {
							k+=2;
							founded=leaf.label.startsWith(args[k]);
						}
						if (!founded) {
							System.out.println("Warning: " + entries[j].getName() + " " + leaf.label + " ; unknown species");
						} else {
							leaf.label= leaf.label.substring(args[k].length()+1,leaf.label.length()) + "_" + args[k+1];
						}
					}			
								
						
					annoteOrthofinderDuplications(tree,duplications,entries[i].getName().substring(0,entries[i].getName().lastIndexOf("_")));						
			
					color(tree,speciesTree);			
			
					write.write(entries[i].getName().substring(0,entries[i].getName().lastIndexOf(".")) + " " + tree.getNewick() + "\n");
					write.flush();
					//System.out.println("Done.");	
					if (i%1000==0) System.out.println(i);				
				
				
				}
				
			
				write.close();
				System.exit(0);			
			
			}

			TreeReader reader= new TreeReader(input,TreeReader.NEWICK);
			Tree tree= reader.nextTree();
			tree.pretreatment();
			
			Vector vect= tree.leafVector;
		/*	if (uniqueid) {
				tree.addUniquePrefix();
				
				BufferedWriter write= new BufferedWriter(new FileWriter(output));
				
				write.write(tree.getNewick() + "\n");
				write.flush();
				
				
				write.close();
				
				
				System.exit(0);
			}*/


			/*BufferedReader read= new BufferedReader(new FileReader(nexml));
			// First pass: getting ids
			System.out.print("Parsing NexML identifiers... ");	
			lines= new Vector();
			Hashtable nodes= new Hashtable();
			Hashtable edges= new Hashtable();
			Vector nodesV= new Vector();
			Vector edgesV= new Vector();
			s= read.readLine();
			int counter=0;
			while (s!=null) {
				lines.addElement(s);
				
				if (s.indexOf("<node")!=-1) {
					int i=0;
					while (!s.substring(i,s.length()).startsWith("id=")) {
						i++;
					}
					while (s.charAt(i)!='"') {
						i++;	
					}
					i++;
					StringBuffer localId= new StringBuffer();
					
					while (s.charAt(i)!='"') {
						localId.append(s.charAt(i));
						i++;	
					}
					nodes.put(localId.toString(),new Integer(counter));	
					nodesV.addElement(localId.toString());		
					//System.out.println("Node: " + localId.toString());	
				}
				if (s.indexOf("<edge")!=-1) {
					int i=0;
					while (!s.substring(i,s.length()).startsWith("id=")) {
						i++;
					}
					while (s.charAt(i)!='"') {
						i++;	
					}
					i++;
					StringBuffer localId= new StringBuffer();
					
					while (s.charAt(i)!='"') {
						localId.append(s.charAt(i));
						i++;	
					}
					edges.put(localId.toString(),new Integer(counter));
					edgesV.addElement(localId.toString());		
					//System.out.println("Edge: " + localId.toString());				
				}				
				
				s= read.readLine();
				counter++;
			}
			System.out.println("Done.");	
			
			// Second pass: getting lengths and labels
			codingForMeta= new Hashtable();
			codingForMetaAlt= new Hashtable();
			System.out.print("Getting lengths and labels... ");	
			for (int i=0;i<edgesV.size();i++) {
				String localId= (String)(edgesV.elementAt(i));
				int index= ((Integer)(edges.get(localId))).intValue();
				String data1= (String)(lines.elementAt(index));
				String data2= (String)(lines.elementAt(index+1));
				int k=0;
				//System.out.println(data1);
				while (!data1.substring(k,data1.length()).startsWith("length=")) {
					k++;
				}
				while (data1.charAt(k)!='"') {
					k++;	
				}
				k++;
				StringBuffer localLength= new StringBuffer();
				
				while (data1.charAt(k)!='"') {
					localLength.append(data1.charAt(k));
					k++;	
				}
				//System.out.println(localLength.toString());
				
				k=0;
				//System.out.println(data1);
				while (!data1.substring(k,data1.length()).startsWith("target=")) {
					k++;
				}
				while (data1.charAt(k)!='"') {
					k++;	
				}
				k++;
				StringBuffer localTarget= new StringBuffer();
				
				while (data1.charAt(k)!='"') {
					localTarget.append(data1.charAt(k));
					k++;	
				}
				
				data1= (String)(lines.elementAt(((Integer)(nodes.get(localTarget.toString()))).intValue()));
				
				//System.out.println(data1);
				k=0;
				while (k<data1.length() && !data1.substring(k,data1.length()).startsWith("label=")) {
					k++;
				}
				while (k<data1.length() && data1.charAt(k)!='"') {
					k++;	
				}
				k++;
				StringBuffer localLabel= new StringBuffer();
				if (k<data1.length()) {
					while (data1.charAt(k)!='"') {
						localLabel.append(data1.charAt(k));
						k++;	
					}	
				} else {
					localLabel.append("NA");
				}				
				
				String label=localLabel.toString();
				/*if (label.startsWith("-")) {
					label=label.substring(1,label.length());	
				}
				if (label.endsWith("-")) {
					label=label.substring(0,label.length()-1);	
				}
				
				if (label.charAt(label.length()-2)=='.') {
					label=label.substring(0,label.length()-2);
					
				}
				if (label.startsWith("D")) {
					label="'" + label + "'";
				}*/
				/*//System.out.println(label);
				String length=localLength.toString();
				
				//System.out.println("\n" + label + " " + length);
				
				codingForMeta.put(label,index+1);
				codingForMetaAlt.put(length,index+1);
				//System.out.println("\n" + label+"#"+length + " " + (index+1));
			}
			System.out.println("Done.");	*/
			
			if (taxaTranslationVector.size()>0) {
				//System.out.println("Prefix entry");
				//geneTree.pretreatment();
				//vect= geneTree.leafVector;
				for (int x=0;x<vect.size();x++) {
					Tree leaf= (Tree)(vect.elementAt(x));	
					for (int k=0;k<taxaTranslationVector.size();k++) {
						String prefix= (String)(taxaTranslationVector.elementAt(k));
						String suffix= (String)(taxaTranslationTable.get(prefix));
					//System.out.print("Prefix " + leaf.label + " ");
						if (leaf.label.startsWith(prefix) && !leaf.label.endsWith("_" + suffix)) {
							leaf.label=leaf.label+"_"+suffix;
						}
					//System.out.println(leaf.label);
						
					}
				}
				
				
				
			}			
			
			System.out.print("Coloration following species tree... ");	
			

			reader= new TreeReader(species,TreeReader.NEWICK);
			Tree speciesTree= reader.nextTree();
			speciesTree.pretreatment();
			
			color(tree,speciesTree);			
			
			BufferedWriter write= new BufferedWriter(new FileWriter(output));
			
			/*for (int i=0;i<lines.size();i++) {
				write.write((String)(lines.elementAt(i)) + "\n");	
				
				write.flush();
			}
			write.close();*/
			write.write(tree.getNewick() + "\n");
			write.flush();
			write.close();
			System.out.println("Done.");
			
			
			

		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(s);
		}
	}


	public static void color(Tree g, Tree s) {
		/*if (g.isLeaf()) {
			//System.out.println(g);
			String taxon= g.label.substring(g.label.lastIndexOf("_")+1,g.label.lastIndexOf("_")+6);
			Tree sNode=(Tree)(s.leafHashtable.get(taxon));
			//System.out.println(g);
			if (codingForMeta.containsKey(g.label)) {
				int index= (  new Integer(    codingForMeta.get(g.label).toString()   )   ).intValue();
				String line= (String)(lines.elementAt(index));
				String color=null;
				try {
					System.out.println(sNode.nhx);
					color= sNode.nhx.substring(sNode.nhx.lastIndexOf("=")+1,sNode.nhx.length());
					if (color!=null) {
						color=color.replace("."," ");
			
					} else {
						color="158 253 56";
					}
				} catch(Exception expe) {
					System.out.println("SNODE:"+sNode);
					System.out.println("g:"+g);
					System.exit(0);
				}
				//System.out.println(color);
				if (line.indexOf("fg")==-1) {
					line= line.substring(0,line.indexOf("\"")+1) + " fg=" + color + line.substring(line.indexOf("\"")+1,line.length()) ;
					lines.setElementAt(line,index);
				}
			}
		} else {*/
			//System.out.println("echo");
			if (g.father!=null) {
				//System.out.print("label=" + g);
				//String taxon= g.label.substring(g.label.lastIndexOf("_")+1,g.label.lastIndexOf("_")+6);
				Tree sNode=g.speciesMapping(s);
				//System.out.println("\nante\n" + g);
				while (sNode.father!=null && (sNode.nhx==null || sNode.nhx.length()<2)) {
					sNode=sNode.father;
				}
				//System.out.println("\npost\n" + sNode);
				//System.out.println(sNode.nhx);
				/*int index=-1;
				if (!codingForMeta.containsKey(g.label)) {
					if (!codingForMetaAlt.containsKey(g.stringLength)) {
						System.out.println("warning, unknown length " + g.label);
					} else {
						index=((Integer)(codingForMetaAlt.get(g.stringLength))).intValue();
					}
				} else {
					Integer stringIndex=(Integer)(codingForMeta.get(g.label));
					index= (stringIndex).intValue();
				}
				if (index!=-1) {
					String line= (String)(lines.elementAt(index));
					//System.out.print(sNode.nhx);
					String color= null;
					if (sNode.nhx==null) {
					//System.out.println("-moins");
						color ="0 0 0";
					} else {
					//System.out.println("-plus");
						color=sNode.nhx.substring(sNode.nhx.lastIndexOf("=")+1,sNode.nhx.length());
						color=color.replace("."," ");
					}
					//System.out.println(color);
					//System.out.println("\n" + line+"\n***");
					if (line.indexOf("fg")==-1) {
						line= line.substring(0,line.indexOf("\"")+1) + " fg=" + color + line.substring(line.indexOf("\"")+1,line.length()) ;
						//System.out.println(line);
						if (!color.equals("0 0 0")) {
							lines.setElementAt(line,index);
						}
					} else {
						//System.out.println("warning " + line);
					}
				}*/
				if (g.nhx!=null)
					g.nhx=g.nhx+":"+sNode.nhx.substring(6,sNode.nhx.length());
				else
					g.nhx=sNode.nhx;
				//System.out.println(g.nhx);
			}
			if (!g.isLeaf()) {
				for (int i=0;i<g.sons.size();i++) {
					color((Tree)(g.sons.elementAt(i)),s);				
				}
			}
			
			
		//}
		
		
	}

	private static void annoteOrthofinderDuplications(Tree runner, Hashtable duplications, String refgroup) {
		if (!runner.isLeaf()) {
			if (duplications.containsKey(refgroup + "#" + runner.label)) {
				runner.label="D_" + runner.label;		
			}		
			for (int i=0;i<runner.sons.size();i++) {
				annoteOrthofinderDuplications((Tree)(runner.sons.elementAt(i)),duplications,refgroup);				
			}		
		
		}
	}


}
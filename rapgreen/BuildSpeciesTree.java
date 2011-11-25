package rapgreen;
import java.util.*;
import java.io.*;

/**
 * @author Jean-Francois Dufayard
 * @version 1.0
 * Species tree builder, from a NCBI reference and a dictionary
 */
public class BuildSpeciesTree {

// ********************************************************************************************************************
// ***     ATTRIBUTS      ***
// **************************
/**
* Input species file
*/
	public static File speciesFile;

/**
* Input dictionnary file
*/
	public static File dicoFile;


/**
* Input dictionnary
*/
	public static SpeciesDictionary dico;

/**
* Output gene file
*/
	public static File outputSpecies=null;




// ********************************************************************************************************************
// ***     MAIN     ***
// ********************
	public static void main(String[] args) {
		String s=null;
		try {
			for (int i=0;i<args.length;i=i+2) {
				if (args[i].equalsIgnoreCase("-input")) {
					speciesFile= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-dico")) {
					dicoFile= new File(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-output")) {
					outputSpecies= new File(args[i+1]);
				}
			}

			TreeReader reader= new TreeReader(speciesFile,TreeReader.NEWICK);
			Tree speciesTree= reader.nextTree();

			dico= new SpeciesDictionary();
			BufferedReader read= new BufferedReader(new FileReader(dicoFile));
			s = read.readLine();


			while (s!=null) {
				if (s.length()>1) {
					String[] infos= s.split("\t");
					dico.addSpecies(infos[1],infos[2],infos[0]);
					s = read.readLine();
				}
			}


			read.close();


			BufferedWriter write = new BufferedWriter(new FileWriter(outputSpecies));

			write.write(speciesTree.toPhyloXMLString(dico)+"\n");
			write.flush();

			write.close();

		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(s);
		}
	}







}
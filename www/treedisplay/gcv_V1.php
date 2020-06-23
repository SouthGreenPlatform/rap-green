<?php
/*$databaseIdField="ID_Bioentity";
$databaseField="bioentity";
$database="genomicus_old2";
$databaseServer="marquenterre.cirad.fr";
$databaseLogin="root";
$databasePass="s0AlesJY";

$link = mysqli_connect($databaseServer, $databaseLogin, $databasePass, $database);

function geneInformations($link,$gene_name){
    $query =  "SELECT * from Gene  where  gene_name='$gene_name';";
    if($result= mysqli_query($link, $query)){
        return $result;
    }
}

//Fonction qui retourne les orthologues d'un gene ; input= gene_name et species_id du gene. output= tableau de orthologues
function othologueGene($link,$gene_name,$species_id){
    $query =  "SELECT * from Orthologs o  where o.orth_id1= (Select orth_id from Gene  where gene_name='$gene_name' and species_id='$species_id')";
    if($result= mysqli_query($link, $query)){
        return $result;
    }
}

//Fonction qui retourne les orthologues d'un gene connaissant son orth_id2
function orthologues($link,$orth_id2){
    $query="Select * from Gene as g, Species as s where g.orth_id= '$orth_id2' and g.species_id=s.species_id and g.gene_name NOT LIKE 'FAM%';";
    if($result= mysqli_query($link,$query)){
        return $result;
    }
}

//Function qui retourne les genes voisines connaissant le nom du gene son chromosome, sa position et le gap
function geneAll($link,$species_id,$chromosome,$position,$gap){
    $query="select * from Gene as g, Species as s where s.species_id='$species_id' and g.species_id='$species_id' and g.chromosome='$chromosome' and g.gene_position between ($position-$gap) and ($gap+$position)  and g.gene_name NOT LIKE 'FAM%';";
    if ($result = mysqli_query($link, $query)) {
        return $result;
    }
}


if( isset($_POST['geneName']) && isset($_POST['gap']) ){
    //Recuperation des paramètres
    $gap=$_POST['gap'];
    $geneRef = $_POST['geneName'];

    //Définition des variables
    $listeGeneHomologues= array();
    $positionGeneRef="";
    $chromozome="";
    $species="";

    //stocke toutes les information des genes homologues du gène de reference
    $geneHomologuesRef= array();
    $listeAutresGene = array();

    //Gene informations
    $geneRefInformation = geneInformations($link,$geneRef);
    while ($g = $geneRefInformation->fetch_assoc())  {
        $positionGeneRef=$g["gene_position"];
        $chromosome=$g["chromosome"];
        $species=$g["species_id"];
    }
    //if($positionGeneRef!=""){
        //renvoie les orth_id de la table orthologues
        $homologueGeneRef = othologueGene($link,$geneRef,$species);

        //Gene homologue du  gene de reference
        while ($row = $homologueGeneRef->fetch_assoc())  {
            $orthologues=orthologues($link,$row['orth_id2']);
            while ($gene = $orthologues->fetch_assoc())  {
                $gene["numeroAffichage"]=$gap;
                $geneHomologuesRef[] = $gene;
                if($gene["gene_name"]!=$geneRef){
                    $geneVoisin = geneAll($link,$gene["species_id"],$gene["chromosome"],$gene["gene_position"],$gap);
                    while ($g = $geneVoisin->fetch_assoc())  {
                        if($gene["gene_name"]!=$g["gene_name"]) {
                            $g['numeroAffichage']=$g['gene_position']-$gene["gene_position"]+$gap;
                            $listeAutresGene[]=$g ;
                        }
                    }
                }
            }mysqli_free_result($orthologues);
        }
        //Renvoie les genes homologues du gene de reference
        $geneVoisinRef=geneAll($link,$species,$chromosome,$positionGeneRef,$gap);

        //Gene Homologue des voisins
        while ($row = $geneVoisinRef->fetch_assoc())  {
            $geneHomologue=array();
            $orthIdOrthologue=othologueGene($link,$row["gene_name"],$row["species_id"]);
            $row['numeroAffichage']=$row['gene_position']+$gap-$positionGeneRef;
            $geneHomologue[]=$row;
            while ($orthId=$orthIdOrthologue->fetch_assoc()){
                $orthologuesGeneVoisin=orthologues($link,$orthId['orth_id2']);
                while ($gene = $orthologuesGeneVoisin->fetch_assoc()){
                    foreach($geneHomologuesRef as $cle=>$valeur){
                        $boolGeneExist=0;
                        if ($valeur["chromosome"]==$gene["chromosome"] && $valeur["species_id"]==$gene["species_id"] && $valeur["gene_position"]<=($gene["gene_position"]+$gap) && $valeur["gene_position"]>=($gene["gene_position"]-$gap)){
                            foreach($geneHomologue as $id=>$valeurGene){
                                if($valeurGene["gene_name"]==$gene["gene_name"]){$boolGeneExist=1;}
                            }
                            if($boolGeneExist==0){
                                $gene['numeroAffichage']= $gene['gene_position']+$gap-$valeur["gene_position"];
                                $gene["ref"]=$row["gene_name"];
                                $geneHomologue[]=$gene;
                                foreach($listeAutresGene as $key=>$val){
                                    if($val["gene_name"]==$gene["gene_name"]){ unset($listeAutresGene[$key]);}
                                }
                            }
                        }
                    }
                }mysqli_free_result($orthologuesGeneVoisin);

            }mysqli_free_result($orthIdOrthologue);
            $listeGeneHomologues[]= $geneHomologue;
        }mysqli_free_result($geneVoisinRef);

        /* Fermeture de la connexion 
        mysqli_close($link);

        $listeAutresGene=array_merge($listeAutresGene);
        $resultat=array();
        $resultat["autreGene"]=$listeAutresGene;
        $resultat["listeGeneHomologues"]=$listeGeneHomologues;
        $resultat["homologueGeneRef"]=$geneHomologuesRef;

        echo json_encode($resultat);
   // }
   // else{echo "Gene n'existe pas";}
}else{echo "Gene n'existe pas";}*/
        
        
        
        
        $databaseIdField="ID_Bioentity";
$databaseField="bioentity";
$database="genomicus";
$databaseServer="marquenterre.cirad.fr";
$databaseLogin="root";
$databasePass="s0AlesJY";

$link = mysqli_connect($databaseServer, $databaseLogin, $databasePass, $database);

//Recherche  des information d'un gene en utilisant son gene_nale
function geneInformations($link,$gene_name){
    $query =  "SELECT * from Gene  where  gene_name like '$gene_name%';";
    if($result= mysqli_query($link, $query)){
        return $result;
    }
}

//Fonction qui retourne les gene de meme famille ; input= root_id . output= Liste des genes de meme familles(NB ils ont le meme root_id)
function geneFamille($link,$root_id){
    $query ="SELECT *
            FROM Chromosome as c,Gene AS g, Tree AS t, Species AS s 
            WHERE t.root_id ='$root_id'
                AND g.gene_name NOT LIKE 'FAM%'
                AND g.gene_id = t.gene_id
                AND g.species_id = c.species_id
                
                AND s.species_id = g.species_id group by g.gene_id; ";
    if($result= mysqli_query($link, $query)){
        return $result;
    }
}

//Function qui retourne les genes voisins connaissant le nom du gene son chromosome, sa position et le nombre de voisin
function voisinGene($link,$species_id,$chromosome,$position,$nbVoisin){
    $query="select * from Gene as g,Chromosome as c, Species as s where g.species_id = c.species_id and c.chromosome = g.chromosome and s.species_id='$species_id' and g.species_id='$species_id' and g.chromosome='$chromosome' and g.gene_position between ($position-$nbVoisin) and ($nbVoisin+$position)
    and g.gene_name NOT LIKE 'FAM%';";
    if ($result = mysqli_query($link, $query)) {
        //print_r($result);
        return $result;
    }
}
//renvoie les gene_id des orthologues d'un gene
function tousORT($link,$gene_id){
    $query="SELECT * FROM Gene as g
    WHERE g.gene_id IN (
            SELECT g2.gene_id AS gene_id2
            FROM Gene g1
            JOIN Orthologs ON g1.orth_id = Orthologs.orth_id1
            JOIN Gene g2 ON Orthologs.orth_id2 = g2.orth_id
            WHERE g1.gene_id ='$gene_id' ) and g.gene_name NOT LIKE 'FAM%';";
    if ($result = mysqli_query($link, $query)) {
        return $result;
    }
}

//Recuperation des paramètres
    $nbVoisin=$_POST['nbVoisin'];//15;//
    $geneRef =$_POST['geneName'];//"Zm00001d004719_P001";


//Definition des variables
$familleGeneRef = array();
$geneRefVoisin = array();
$genesAll= array();
$geneRefId="";
$geneRefChromosome="";
$geneRefPosition="";
$geneRefSpeciesId="";
$geneRefRootId="";
$allOrthologue=array();
 $allFamille=array();
 
//Récuperation des informations du gene de reference
$geneRefInformations=geneInformations($link,$geneRef);
while ($information = $geneRefInformations->fetch_assoc())  {
        $geneRefPosition = $information["gene_position"];
        $geneRefChromosome = $information["chromosome"];
        $geneRefSpeciesId = $information["species_id"];
        $geneRefRootId = $information["root_id"];
        $geneRef=$information["gene_name"];
}mysqli_free_result($geneRefInformations);


//Recuperation des genes de meme famille que le gene de referrence: $familleGeneRef
//Recuperation de tous les gene qui seront affiché lors de la visualisation: $genesAll
$voisinReference=array();  
$familleRef = geneFamille($link,$geneRefRootId);
while ($gene = $familleRef->fetch_assoc())  {
    $gene["RefFamille"]=$geneRef;
    $gene["positionRef"]=$gene["gene_position"];
    $gene["strandRef"]=$gene["strand"];
    /*Voisins */
    $geneVoisin = voisinGene($link,$gene["species_id"],$gene["chromosome"],$gene["gene_position"],$nbVoisin);
    while ($voisin = $geneVoisin->fetch_assoc())  {
        $voisin["positionRef"]=$gene["gene_position"];
        $voisin["strandRef"]=$gene["strand"];
        $voisinGene[]=$voisin;
        if($gene["gene_name"]==$geneRef){$voisinReference[]=$voisin["gene_name"];}
    }mysqli_free_result($geneVoisin);}
    $genesAll[$gene["gene_name"]]= $voisinGene;  
}mysqli_free_result($familleRef);


//Calcul des voisin du gene de la reference
//Pour chaque voisin on recherche les autres membres de sa famille
//
$voisinRef = voisinGene($link,$geneRefSpeciesId,$geneRefChromosome,$geneRefPosition,$nbVoisin);
while ($voisin = $voisinRef->fetch_assoc())  {
    $geneRefVoisin[]=$voisin;
    $familleVoisin = geneFamille($link,$voisin["root_id"]);
    
    //Pour chaque chaque voisin on recherche ses orthologue et les enregistre dans $allOrthologues
    $orthologue=tousORT($link,$voisin["gene_id"]);
    while($orth=$orthologue->fetch_assoc()){
       $allOrthologue[]=$orth; 
    }
    while ($gene = $familleVoisin->fetch_assoc())  {
        $allFamille[]=$gene;
        foreach($genesAll as $cle=>$geneF){
            $homologue=-1;
            $boolVoisinRef=0;
            for( $i=0;$i<sizeof($geneF);$i++){
                if ($geneF[$i]["gene_id"]==$gene["gene_id"] ){$homologue=$i;}
            }  
            if($homologue!=-1 ){
                if ($cle==$geneRef){
                    $genesAll[$cle][$homologue]["RefHomologue"]=$genesAll[$cle][$homologue]["gene_name"];
                }
                else{
                    for($j=0;$j<sizeof($voisinReference);$j++){ 
                        if($genesAll[$cle][$homologue]["gene_name"]==$voisinReference[$j]){$boolVoisinRef=1;}
                    }
                    if($boolVoisinRef==1){
                        $genesAll[$cle][$homologue]["RefHomologue"]=$genesAll[$cle][$homologue]["gene_name"];
                    }
                    else{
                        $genesAll[$cle][$homologue]["RefHomologue"]=$voisin["gene_name"];
                    }
                }
            } 
        }
    }
}mysqli_free_result($voisinRef);

//Ajout de l'attribut paralogue si le gene se trouve dans la famille mais pas dans les orthologues
    foreach($genesAll as $cle=>$geneF){
        
        for( $w=0;$w<sizeof($geneF);$w++){
            $orthologue=0;
            
        $v=0;
        if(isset($geneF[$w]["RefHomologue"])){
            for ($m=0;$m<sizeof($allOrthologue);$m++){
             if ($geneF[$w]["gene_id"]==$allOrthologue[$m]["gene_id"] ){$orthologue=1;}
        } 
        if($orthologue==0 && $cle!=$geneRef ){ 
            
            //if($v==0){
               
                $genesAll[$cle][$w]["Paralogue"]=1;
               
            //}  
        }}
    }}



    $resultat["gene"]=$genesAll;
    $resultat["voisinRef"]=$geneRefVoisin;
    
    $resultat=array_merge($resultat);
    echo( json_encode($resultat));
    mysqli_close($link)
?>

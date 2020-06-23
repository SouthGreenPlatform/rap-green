<?php
$databaseIdField="ID_Bioentity";
$databaseField="bioentity";
$database="genomicus_27.1";
$databaseServer="marquenterre.cirad.fr";
$databaseLogin="root";
$databasePass="s0AlesJY";

$link = mysqli_connect($databaseServer, $databaseLogin, $databasePass, $database);

//Recherche  des informations d'un gene en utilisant son gene_name
function geneInformations($link,$gene_name){
    $query =  "SELECT * from Gene  where  gene_name like '%$gene_name%';";
    if($result= mysqli_query($link, $query)){
        return $result;
    }
}

//Fonction qui retourne les genes de meme famille qu'un gène sauf celui de la referrence; input= root_id . output= Liste des genes de meme familles(NB ils ont le meme root_id)
function geneFamille($link,$root_id){
    $query ="SELECT *
            FROM Tree AS t, Gene AS g,Species AS s,Chromosome as c
            WHERE t.root_id ='$root_id'
                AND g.gene_id = t.gene_id
                AND g.gene_name NOT LIKE 'FAM%'
                AND g.species_id = c.species_id 
                AND s.species_id = g.species_id
                AND c.chromosome = g.chromosome;";
    if($result= mysqli_query($link, $query)){
        return $result;
    }
}

//Fonction qui retourne les gènes de la même famille que le gène de refence
function geneFamilleRef($link,$root_id){
    $query ="SELECT *
            FROM Tree AS t, Gene AS g,Species AS s
            WHERE t.root_id ='$root_id'
                AND g.gene_id = t.gene_id
                AND g.gene_name NOT LIKE 'FAM%'
                AND s.species_id = g.species_id;";
    if($result= mysqli_query($link, $query)){
        return $result;
    }
}

//Fonction qui retourne les genes voisins connaissant le nom du gene son chromosome, sa position et le nombre de voisin
function voisinGene($link,$species_id,$chromosome,$position,$nbNeighbors){
    $query="select * from Gene as g,Chromosome as c, Species as s where g.species_id = c.species_id and c.chromosome = g.chromosome and s.species_id='$species_id' and g.species_id='$species_id' and g.chromosome='$chromosome' and g.gene_position between ($position-$nbNeighbors) and ($nbNeighbors+$position)
    and g.gene_name NOT LIKE 'FAM%';";
    if ($result = mysqli_query($link, $query)) {
        //print_r($result);
        return $result;
    }
}

//Fonction qui retourne les orthologues d'un gène en utilisant l'identifiant de ce dernier 
function allORT($link,$gene_id){
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
 $nbNeighbors=$_POST['nbNeighbors'];
  $geneRef =$_POST['geneName'];


//Definition des variables
$familleGeneRef = array(); //liste des gene de la même famille que le gène de reference
$geneRefNeighbors = array(); //liste des gènes voisins au gène de la reference 
$genesAll= array(); //Liste de tous les gènes nécessaires à la representation du contexte

//Variables relatives au gène de reference: son id, cgromosome, position root_id
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
//Recuperation de tous les genes qui seront affiché lors de la visualisation: $genesAll
$neighborsReference=array();  
$familleRef = geneFamilleRef($link,$geneRefRootId);
while ($gene = $familleRef->fetch_assoc())  {
    $familleGeneRef[]=$gene;
    $neighborsGene=[];
      if ($gene["chromosome"]==""){
        $gene["positionRef"]=$gene["gene_position"];
        $gene["strandRef"]=$gene["strand"];
        $gene["familyRef"]=$geneRef;
        $neighborsGene[]=$gene;
         $geneRefNeighbors[]=$gene;
    }
    else{
    $geneVoisin = voisinGene($link,$gene["species_id"],$gene["chromosome"],$gene["gene_position"],$nbNeighbors);
    while ($voisin = $geneVoisin->fetch_assoc())  {
        $voisin["positionRef"]=$gene["gene_position"];
        $voisin["strandRef"]=$gene["strand"];       
        if($gene["gene_name"]==$voisin["gene_name"]){$voisin["familyRef"]=$geneRef;}
        $neighborsGene[]=$voisin;
        
        if($gene["gene_name"]==$geneRef){$neighborsReference[]=$voisin["gene_name"];}
    }mysqli_free_result($geneVoisin);} 
    $genesAll[$gene["gene_name"]]= $neighborsGene; 
}mysqli_free_result($familleRef);


//Calcul des voisins du gene de la reference
//Pour chaque voisin on recherche les autres membres de sa famille
//
$neighborsRef = voisinGene($link,$geneRefSpeciesId,$geneRefChromosome,$geneRefPosition,$nbNeighbors);
while ($voisin = $neighborsRef->fetch_assoc())  {
 
    $geneRefNeighbors[]=$voisin;
    $familleVoisin = geneFamille($link,$voisin["root_id"]);
    
    //Pour chaque chaque voisin on recherche ses orthologues et on les enregistre dans $allOrthologues
    $orthologue=allORT($link,$voisin["gene_id"]);
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
                    $genesAll[$cle][$homologue]["familyRef"]=$genesAll[$cle][$homologue]["gene_name"];
                }
                else{
                    for($j=0;$j<sizeof($neighborsReference);$j++){ 
                        if($genesAll[$cle][$homologue]["gene_name"]==$neighborsReference[$j]){$boolVoisinRef=1;}
                    }
                    if($boolVoisinRef==1){
                        $genesAll[$cle][$homologue]["familyRef"]=$genesAll[$cle][$homologue]["gene_name"];
                    }
                    else{
                        $genesAll[$cle][$homologue]["familyRef"]=$voisin["gene_name"];
                    }
                }
            } 
        }
    }
}mysqli_free_result($neighborsRef);

//Ajout de l'attribut paralogue si le gene se trouve dans la famille mais pas dans les orthologues on utilise la variable $allOrthologue et $genesAll
// si un gène a la propriété familyRef donc il est de la même famille q'un gène voisin de la reference donc s'il est de la même famille et q'il se trouve dans la liste des gènes orthologues du contexte donc c'est un PARALOGUE
    foreach($genesAll as $cle=>$geneF){
        
        for( $w=0;$w<sizeof($geneF);$w++){
            $orthologue=0;
            
            if(isset($geneF[$w]["familyRef"])){
                for ($m=0;$m<sizeof($allOrthologue);$m++){
                  if ($geneF[$w]["gene_id"]==$allOrthologue[$m]["gene_id"] ){$orthologue=1;}
                } 
                if($orthologue==0 && $cle!=$geneRef ){ 
                   $genesAll[$cle][$w]["Paralogue"]=1;
                }
             }
       }
    }

 //Preparation de l'encodage en JSON
 $resultat["gene"]=$genesAll;
 $resultat["neighborsRef"]=$geneRefNeighbors;
 
 $resultat=array_merge($resultat);
 echo( json_encode($resultat)); // Encodage et renvoie du fichier JSON
 mysqli_close($link) //Fermeture de la connection
?>

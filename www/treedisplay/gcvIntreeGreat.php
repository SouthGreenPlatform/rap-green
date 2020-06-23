<?php
$databaseIdField="ID_Bioentity";
$databaseField="bioentity";
$database="genomicus_27.1";
$databaseServer="marquenterre.cirad.fr";
$databaseLogin="root";
$databasePass="s0AlesJY";

$link = mysqli_connect($databaseServer, $databaseLogin, $databasePass, $database);

function geneInformations($link,$gene_name){
    $query =  "SELECT * from Gene  where  gene_name like '$gene_name%';";
    if($result= mysqli_query($link, $query)){
        return $result;
    }
}

//Fonction qui retourne les gene de meme famille ; input= root_id . output= Liste des genes de meme familles(NB ils ont le meme root_id)
function geneFamille($link,$root_id){
    $query ="SELECT *
            FROM  Gene AS g,Species AS s,Chromosome as c
            WHERE g.root_id ='$root_id'
                AND g.gene_name NOT LIKE 'FAM%'
                AND g.species_id = c.species_id 
                AND s.species_id = g.species_id
                AND c.chromosome = g.chromosome;";
    if($result= mysqli_query($link, $query)){
        return $result;
    }
}

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


//Function qui retourne les genes voisines connaissant le nom du gene son chromosome, sa position et le nombre de voisin
function voisinGene($link,$species_id,$chromosome,$position,$nbNeigbors){
    $query="select * from Gene as g,Chromosome as c, Species as s where g.species_id = c.species_id and c.chromosome = g.chromosome and s.species_id='$species_id' and g.species_id='$species_id' and g.chromosome='$chromosome' and g.gene_position between ($position-$nbNeigbors) and ($nbNeigbors+$position)
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
    $nbNeigbors=$_POST['nbNeigbors'];//15;//
    $geneRef = $_POST['geneName'];//"Zm00001d004719_P001";
    //$familleRef = $_POST['familleRef'];

//Definition des variables
$familleGeneRef = array();
$geneRefVoisin = array();
$genesAll= array();
$geneRefId="";
$geneRefChromosome="";
$geneRefPosition="";
$geneRefSpeciesId="";
$geneRefRootId="";

//Récuperation des informations du gene de reference
$geneRefInformations=geneInformations($link,$geneRef);
while ($information = $geneRefInformations->fetch_assoc())  {
        $geneRefPosition = $information["gene_position"];
        $geneRefChromosome = $information["chromosome"];
        $geneRefSpeciesId = $information["species_id"];
        $geneRefRootId = $information["root_id"];
}mysqli_free_result($geneRefInformations);


$voisinReference=array();  
$familleRef = geneFamilleRef($link,$geneRefRootId);
while ($gene = $familleRef->fetch_assoc())  {
    $familleGeneRef[]=$gene;
    $voisinGene=[];
     
      if ($gene["chromosome"]==""){
        
        $gene["positionRef"]=$gene["gene_position"];
        $gene["strandRef"]=$gene["strand"];
        $gene["familyRef"]=$geneRef;
        $voisinGene[]=$gene;
        
    }
    else{
     $geneVoisin = voisinGene($link,$gene["species_id"],$gene["chromosome"],$gene["gene_position"],$nbNeigbors);
    while ($voisin = $geneVoisin->fetch_assoc())  {
        $voisin["positionRef"]=$gene["gene_position"];
        $voisin["strandRef"]=$gene["strand"];
        $voisinGene[]=$voisin;
        if($gene["gene_name"]==$geneRef){$voisinReference[]=$voisin["gene_name"];}
    }mysqli_free_result($geneVoisin);} 
    $genesAll[$gene["gene_name"]]= $voisinGene; 
}mysqli_free_result($familleRef);

//Voisin de la reference
$voisinRef = voisinGene($link,$geneRefSpeciesId,$geneRefChromosome,$geneRefPosition,$nbNeigbors);

while ($voisin = $voisinRef->fetch_assoc())  {
    $geneRefVoisin[]=$voisin;
    $familleVoisin = geneFamille($link,$voisin["root_id"]);
    while ($gene = $familleVoisin->fetch_assoc())  {
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
                    for($j=0;$j<sizeof($voisinReference);$j++){ 
                        if($genesAll[$cle][$homologue]["gene_name"]==$voisinReference[$j]){$boolVoisinRef=1;}
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
}mysqli_free_result($voisinRef);

    $resultat["gene"]=$genesAll;
    $resultat["voisinRef"]=$geneRefVoisin;
    
    $resultat=array_merge($resultat);
    echo( json_encode($resultat));
    mysqli_close($link)
?>

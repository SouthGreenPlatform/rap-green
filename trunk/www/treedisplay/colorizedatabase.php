<?php
$databaseIdField="ID_Bioentity";
$databaseField="bioentity";
$database="MSDMind";
$databaseServer="medoc.cirad.fr";
$databaseLogin="root";
$databasePass="s0AlesJY";
mysql_connect($databaseServer, $databaseLogin, $databasePass);
mysql_select_db($database);


$result = mysql_query("SELECT distinct bc.ID_Bioentity FROM bioentity_crossreference bc inner join cross_reference c on c.ID_CrossReference=bc.ID_CrossReference left outer join biblio b on b.Id_Biblio=c.Id_RefBiblio where c.Database_ID like '%".$_REQUEST['word']."%' or c.Database_EntryName like '%".$_REQUEST['word']."%' or c.Evidence like '%".$_REQUEST['word']."%' or b.AuthorsDate like '%".$_REQUEST['word']."%' or b.DOI like '%".$_REQUEST['word']."%' or c.AdditionalInfo like '%".$_REQUEST['word']."%'");


	
while ($row=mysql_fetch_row($result)) {
	
	echo $row[0]." ";
}

?>
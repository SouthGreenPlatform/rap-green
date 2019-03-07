<?php
$databaseIdField="ID_Bioentity";
$databaseField="bioentity";
$database="MSDMind";
$databaseServer="marquenterre.cirad.fr";
$databaseLogin="root";
$databasePass="s0AlesJY";
mysql_connect($databaseServer, $databaseLogin, $databasePass);

mysql_select_db($database);

$result = mysql_query("SELECT ".$databaseIdField.", Name, nsLTP_tree_code FROM ".$databaseField);






?>


<script type="text/javascript">
// Information table, global variable
var infos= new Array();

<?php
while ($row=mysql_fetch_row($result)) {
?>

infos["<?php echo $row[0]; ?>"]= new Array();
infos["<?php echo $row[0]; ?>"]["nameMSDMin"]="<?php echo $row[1]; ?>";
infos["<?php echo $row[0]; ?>"]["primTypeN"]="<?php echo $row[2]; ?>";

<?php
}
?>
//alert('infos["355__RICCO"]["name"] ' + infos["355__RICCO"]["name"]);

</script>




<?php
// prototype requete info par ref et par id
//SELECT c.* FROM bioentity_crossreference bc inner join cross_reference c on c.ID_CrossReference=bc.ID_CrossReference where c.`Database`='PO' and bc.ID_Bioentity=1

// Insertion infos PO
$resultPO=mysql_query("SELECT distinct(bc.ID_Bioentity) FROM bioentity_crossreference bc inner join cross_reference c on c.ID_CrossReference=bc.ID_CrossReference where c.`Database_name`='PO'");


?>
<script type="text/javascript">
<?php
while ($row=mysql_fetch_row($resultPO)) {
?>

infos["<?php echo $row[0]; ?>"]["PO"]="1";

<?php
}
?>
</script>

<?php

// Insertion infos GO
$resultGO=mysql_query("SELECT distinct(bc.ID_Bioentity) FROM bioentity_crossreference bc inner join cross_reference c on c.ID_CrossReference=bc.ID_CrossReference where c.`Database_name`='GO'");


?>
<script type="text/javascript">
<?php
while ($row=mysql_fetch_row($resultGO)) {
?>

infos["<?php echo $row[0]; ?>"]["GO"]="1";

<?php
}
?>

</script>






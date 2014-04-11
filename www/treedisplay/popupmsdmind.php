<!-- Standard definition page -->
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:html="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<meta http-equiv="Content-Language" content="en-gb">
<head>
<!-- Standard headers (CSS...) -->
<?php

 include('header.php');

?>
</head>
<body>

<?php
$databaseIdField="ID_Bioentity";
$databaseField="bioentity";
$database="MSDMind";
$databaseServer="medoc.cirad.fr";
$databaseLogin="root";
$databasePass="s0AlesJY";
mysql_connect($databaseServer, $databaseLogin, $databasePass);
mysql_select_db($database);
$result = mysql_query("SELECT c.*, b.* FROM bioentity_crossreference bc inner join cross_reference c on c.ID_CrossReference=bc.ID_CrossReference left outer join biblio b on b.Id_Biblio=c.Id_RefBiblio where c.`Database_name`='".$_REQUEST['tag']."' and bc.ID_Bioentity=".$_REQUEST['id']);


	echo "<table id=standardmenu><tr><td id=title>Term</td><td id=title>Description</td><td id=title>Evidence</td><td id=title>Bibliography</td><td id=title>More information</td></tr>";

while ($row=mysql_fetch_row($result)) {
	if (strlen($row[8])>1) {
		echo "<tr><td id=field>".$row[2]."</td><td id=field>".$row[3]."</td><td id=field>".$row[4]."</td><td id=field>".$row[8]." ; ".$row[9]."</td><td id=field>".$row[6]."</td></tr>";
	} else {
		echo "<tr><td id=field>".$row[2]."</td><td id=field>".$row[3]."</td><td id=field>".$row[4]."</td><td id=field>No reference</td><td id=field>".$row[6]."</td></tr>";
	}
}

echo "</table>";
?>



</body>
</html>
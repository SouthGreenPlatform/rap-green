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
if (isSet($_REQUEST['gene'])) {
echo "<table id=standardmenu><tr>";
for ($i = 0; isSet($_REQUEST['a'.$i]); $i++) {
	$arreq = explode("§", $_REQUEST['a'.$i]);
    echo "<td id=title>".$arreq[0]."</td>";
}
	//echo "<table id=standardmenu><tr><td id=title>Sequence</td><td id=title>Code</td><td id=title>Function</td><td id=title>Reviewed</td><td id=title>Score</td></tr>";
echo "</tr><tr><td id=field>".$_REQUEST['gene']."</td>";
for ($i = 1; isSet($_REQUEST['a'.$i]); $i++) {
	$arreq = explode("§", $_REQUEST['a'.$i]);
    echo "<td id=field>".$arreq[1]."</td>";
}

echo "</tr></table>";
	//echo "<tr><td id=field>".$_REQUEST['gene']."</td><td id=field>".$_REQUEST['code']."</td><td id=field>".str_replace(";","<BR>",$_REQUEST['function'])."</td><td id=field>".$_REQUEST['reviewed']."</td><td id=field>".$_REQUEST['score']."</td></tr>";


	echo "</table>";
} else {
	$spRel= preg_split('/;/',$_REQUEST['relation']);
	$spSco= preg_split('/;/',$_REQUEST['score']);
	$spSco2= preg_split('/;/',$_REQUEST['score2']);
	$spSco3= preg_split('/;/',$_REQUEST['score3']);
	echo "<table id=standardmenu><tr><td id=title>Sequence 1</td><td id=title>Sequence 2</td><td id=title>Relationship</td><td id=title>dS</td><td id=title>Mean dS</td><td id=title>Block size</td></tr>";
	for ($i = 0; $i < count($spRel); $i++) {
		echo "<tr><td id=field>".$_REQUEST['tag1']."</td><td id=field>".$_REQUEST['tag2']."</td><td id=field>".$spRel[$i]."</td><td id=field>".$spSco[$i]."</td><td id=field>".$spSco2[$i]."</td><td id=field>".$spSco3[$i]."</td></tr>";
	}

	echo "</table>";
	
}
?>



</body>
</html>
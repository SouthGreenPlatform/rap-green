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
$range=30;
$resfiles=split(";", $_POST['hiddenlist']);
$count=count($resfiles);
	echo "<blockquote><table id='organise'>";
	if ($resfiles[0]=="No matching tree for this pattern") {
		echo "<tr><td>No matching tree for this pattern.</td></tr>";
	} else {

		echo "<tr><td colspan=2 id='lefted'><p><b>".$count." matching trees.</b></p></td></tr>";
		echo "<tr><td id='lefted'><b>Results ".($_POST['start']+1)." to ".($_POST['start']+$range).":</b>&nbsp;&nbsp;&nbsp;</td><td id='lefted'>";
		if ($_POST['start']>0) {
			echo "<a onclick='refreshList(0);'><< Prev</a>";
			echo "&nbsp;";
		}
		if (($_POST['start']+$range)<=$count) {
			echo "<a onclick='refreshList(1);'>Next >></a>";
		}
		echo "</td></tr>";
		for ($i=$_POST['start'];$i<count($resfiles) && $i<$range+$_POST['start'];$i++) {
			echo "<tr><td id='lefted'>".$resfiles[$i]."&nbsp;&nbsp;&nbsp;</td><td id='lefted'><a href='resultsDisplay.php?databank=".$_REQUEST["databank"]."&pattern=".$_REQUEST["pattern"]."&id=".$resfiles[$i]."'>display</a></td></tr>";
		}
		
	}
	echo "</table></blockquote>";


?>

<script type="text/javascript">

 function refreshList(e) {
 	if (e==0) {
 		
 		document.forms['resultForm'].start.value='<?php echo ($_POST["start"]-$range); ?>';
 	} else {
 		document.forms['resultForm'].start.value='<?php echo ($_POST["start"]+$range); ?>';
 	}
	document.forms['resultForm'].submit();

 }

</script>

<form name="resultForm" action="dynamiclist.php?start=0&databank=<?php echo $_REQUEST['databank']; ?>&pattern=<?php echo $_REQUEST['pattern']; ?>" method="post">
<input type="hidden" name="hiddenlist" value="<?php

if ($count==0) {
	echo "No matching tree for this pattern";
} else{
	for ($i=0;$i<$count;$i++) {
		if ($i!=0)
			echo ";";
		echo $resfiles[$i];
		
	}		
}


?>">
<input type="hidden" name="start" value="0"></form> 


</body>
</html>
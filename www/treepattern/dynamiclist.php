<!-- Standard definition page -->
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:html="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<meta http-equiv="Content-Language" content="en-gb">
<head>
<!-- Standard headers (CSS...) -->
<?php

 include('header.php');

?>
<?php

 include('config.php');

?>
</head>
<body>
<?php


$range=30;


$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
$resultat = socket_connect($socket, $server, $port);
// send the signal to the daemon
$envoi="resultSplit\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_POST['id']."\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_POST['start']."\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$range."\n";
socket_write($socket, $envoi, strlen($envoi));
$count=0;

while ($reception = socket_read($socket, 2048)) {
	// split the buffer
	$families=preg_split("/\n/", $reception);
	for ($i=0;$i<count($families);$i++) {
		// if the current word is significant
		if (strlen($families[$i])>0) {



			$resfiles[$count]=$families[$i];
			$count++;
		}
	}

}

socket_close($socket);


//$resfiles=split(";", $_POST['hiddenlist']);
//$count=count($resfiles);
	$count=$_POST['size'];
	echo "<blockquote><table id='organise'>";
	if ($resfiles[0]=="No matching tree for this pattern") {
		echo "<tr><td>No matching tree for this pattern.</td></tr>";
	} else {

		echo "<tr><td colspan=2 id='lefted'><p><b>".$count." matching trees.</b></p></td></tr>";

		echo "<tr><td colspan=4 id='lefted'><p>Compute <a target='_blank' href='wait2.php?databank=".$_REQUEST["databank"]."&pattern=".$_POST['hiddenpatternwait']."&id=".$_POST['id']."'>full results</a>  in CSV format<br>(A new window will pop, and it could take a few minutes for the file to be generated).</p></td></tr>";
		echo "</table><table id='organise'>";
		echo "<tr><td id='lefted'><b>Results ".($_POST['start']+1)." to ".($_POST['start']+$range).":</b>&nbsp;&nbsp;&nbsp;</td><td id='lefted'>";
		if ($_POST['start']>0) {
			echo "<a onclick='refreshList(0);'><< Prev</a>";
			echo "&nbsp;";
		}
		if (($_POST['start']+$range)<=$count) {
			echo "<a onclick='refreshList(1);'>Next >></a>";
		}
		echo "</td></tr>";

		for ($i=0;$i<count($resfiles);$i++) {
			echo "<tr><td id='lefted'>".$resfiles[$i]."&nbsp;&nbsp;&nbsp;</td><td id='lefted'><a href='resultsDisplay.php?databank=".$_REQUEST["databank"]."&pattern=".$_POST['hiddenpatternwait']."&id=".$resfiles[$i]."'>display</a>";

			if (isSet($displayadress[$_REQUEST['databank']]) && $displayadress[$_REQUEST['databank']]!="")	{
				echo "&nbsp;&nbsp;<a href='resultDisplay2.php?databank=".$_REQUEST["databank"]."&pattern=".$_POST['hiddenpatternwait']."&id=".$resfiles[$i]."'>".$displaytag[$_REQUEST['databank']]."</a>";

			}

			echo "</td></tr>";
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
	document.getElementById("hiddenpatternwait").value="<?php echo $_POST['hiddenpatternwait']; ?>";
	document.forms['resultForm'].submit();

 }

</script>

<form name="resultForm" action="dynamiclist.php?start=0&databank=<?php echo $_REQUEST['databank']; ?>" method="post">
<input type="hidden" name="hiddenlist" value="<?php

/*if ($count==0) {
	echo "No matching tree for this pattern";
} else{
	for ($i=0;$i<$count;$i++) {
		if ($i!=0)
			echo ";";
		echo $resfiles[$i];

	}
}*/


?>">
<input type="hidden" name="hiddenpatternwait" id="hiddenpatternwait" /><input type="hidden" name="start" value="0"><input type="hidden" name="id" value="<?php echo $_POST['id']; ?>"><input type="hidden" name="size" value="<?php echo $_POST['size']; ?>"></form>


</body>
</html>

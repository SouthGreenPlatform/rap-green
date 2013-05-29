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

<script type="text/javascript">

 function submitTree(treeString) {
	document.forms['research'].hiddenfield.value=treeString;
	document.forms['research'].submit();

 }

</script>

<form name="research" action="http://gohelle.cirad.fr/phylogeny/treedisplay/index.php" method="post">
<input type="hidden" name="hiddenfield" value="PLOUF">
</form>

<?php

// Get the tree newick from the daemon

$treeNewick="";

// open the socket
$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
$resultat = socket_connect($socket, $server, $port);
// send the signal to the daemon
$envoi="patternNewick\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_REQUEST['databank']."\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_REQUEST['id']."\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_REQUEST['pattern']."\n";
socket_write($socket, $envoi, strlen($envoi));
$count=0;
while ($reception = socket_read($socket, 2048)) {
	// split the buffer
	$families=split("\n", $reception);
	//echo $reception;
	for ($i=0;$i<count($families);$i++) {
		$treeNewick=$treeNewick.$families[$i];
	}
}
//echo $treeNewick;
//echo "Location: http://phylariane.univ-lyon1.fr/display/tree/reconciled/".$_REQUEST['id']."?idList=".$treeNewick;

?>

<script type="text/javascript">
	submitTree("<?php echo $treeNewick; ?>");

</script>

</body>
</html>


<?php
//header("Location: http://phylariane.univ-lyon1.fr/display/tree/reconciled/".$_REQUEST['id']."?idList=".$treeNewick."&analysis=".$_REQUEST['databank']);
//exit;
?>

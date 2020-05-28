<?php

 include('config.php');

?>

<script type="text/javascript">
	//alert("<?php echo $_POST['hiddenpatternwait']; ?>");
</script>
<?php
echo "<p>Tree pattern matching in progress...</p>";
// open the socket
$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
$resultat = socket_connect($socket, $server, $port);
// send the signal to the daemon
$envoi="patternSearch\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_REQUEST['database']."\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_POST['hiddenpatternwait']."\n";
socket_write($socket, $envoi, strlen($envoi));
$count=0;
//echo "start<br>";

while ($reception = socket_read($socket, 2048)) {
	// split the buffer
	$families=explode("\n", $reception);
	for ($i=0;$i<count($families);$i++) {
		// if the current word is significant
		if (strlen($families[$i])>0) {
			$res[$count]=$families[$i];
			$count++;
		}
	}

}


?>
<script type="text/javascript">

 function refreshList() {
	document.forms['resultForm'].submit();

 }
// alert("<?php echo $res[0]; ?>");
// alert("<?php echo $res[1]; ?>");

</script>

<form name="resultForm" action="dynamiclist.php?databank=<?php echo $_REQUEST['database']; ?>" method="post">
<input type="hidden" name="hiddenpatternwait" id="hiddenpatternwait" />
<input type="hidden" name="hiddenlist" value="<?php

/*if ($count==0) {
	echo "No matching tree for this pattern";
} else{
	for ($i=0;$i<$count;$i++) {
		if ($i!=0)
			echo ";";
		echo $res[$i];

	}
}*/

socket_close($socket);


?>">
<input type="hidden" name="start" value="0"><input type="hidden" name="id" value="<?php echo $res[0]; ?>"><input type="hidden" name="size" value="<?php echo $res[1]; ?>"></form>


<script type="text/javascript">
	document.getElementById("hiddenpatternwait").value="<?php echo $_POST['hiddenpatternwait']; ?>";
	refreshList();
</script>

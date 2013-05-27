
<?php
echo "<p>Tree pattern matching in progress...</p>";
//echo($_REQUEST['pattern']."<BR>".$_REQUEST['database']."<BR>");
// open the socket
$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
$resultat = socket_connect($socket, "localhost", 1666);
// send the signal to the daemon
$envoi="patternSearch\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_REQUEST['database']."\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_REQUEST['pattern']."\n";
socket_write($socket, $envoi, strlen($envoi));
$count=0;
//echo "start<br>";

while ($reception = socket_read($socket, 2048)) {
	// split the buffer
	$families=split("\n", $reception);
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

</script>

<form name="resultForm" action="dynamiclist.php?databank=<?php echo $_REQUEST['database']; ?>&pattern=<?php echo $_REQUEST['pattern']; ?>" method="post">
<input type="hidden" name="hiddenlist" value="<?php

if ($count==0) {
	echo "No matching tree for this pattern";
} else{
	for ($i=0;$i<$count;$i++) {
		if ($i!=0)
			echo ";";
		echo $res[$i];
		
	}		
}
socket_close($socket);


?>">
<input type="hidden" name="start" value="0"></form> 


<script type="text/javascript">
	refreshList();
</script>


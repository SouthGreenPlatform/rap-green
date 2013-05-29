<?php

 include('config.php');

?>
<?php
// open the socket
$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
$resultat = socket_connect($socket, $server, $port);

// send the signal to the daemon
$count=0;
$first="";
$envoi="speciesPlus\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_REQUEST['databank']."\n";
socket_write($socket, $envoi, strlen($envoi));
if (isSet($_REQUEST['tag'])) {
	$envoi=$_REQUEST['tag']."\n";
} else {
	$envoi="\n";
}
socket_write($socket, $envoi, strlen($envoi));
$rough="";
while ($reception = socket_read($socket, 2048)) {
	// split the buffer
	
	$rough=$rough.$reception;

}
$species=split("\n", $rough);

?>






<table id="menu">

<tr>
<td id="poptd" colspan=10>

<?php

for ($i=0;$i<count($species);$i++) {
	// if the current word is significant
	if (strlen($species[$i])>0) {
		$posi= strpos($species[$i],"|");
		
		echo '<p   style="opacity:0.8;" onmouseout="this.style.opacity = opac" onmouseover="';
		echo "this.style.opacity = '1.0';";
		echo '" id="linking" onclick="';
		echo "changeVisibiliteOnName('poplist',0);";
		echo "document.getElementById('speciesfield').value='";
		echo substr($species[$i], 0, $posi);
		echo "';";
		echo "dico['";
		echo substr($species[$i], 0, $posi);
		echo "']='";
		echo substr($species[$i], $posi+1, strlen($species[$i]));
		echo "';refreshTaxaList(event,document.getElementById('speciesfield').value);";
		echo '">&nbsp;&nbsp;&nbsp;&nbsp;';

		echo substr($species[$i], 0, $posi);
		echo "&nbsp;&nbsp;&nbsp;&nbsp;</p>";
		$count++;
	}
}

?>

</td>
</tr>
</table>

<?php
socket_close($socket);




?>

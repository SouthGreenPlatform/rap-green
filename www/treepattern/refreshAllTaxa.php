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



$envoi="\n";


socket_write($socket, $envoi, strlen($envoi));
$rough="";

while ($reception = socket_read($socket, 2048)) {

	// split the buffer

	

	$rough=$rough.$reception;



}
$species=split("\n", $rough);

?>





<script type="text/javascript">
<?php

for ($i=0;$i<count($species);$i++) {

	// if the current word is significant

	if (strlen($species[$i])>0) {
		$posi= strpos($species[$i],"|");
		

		echo 'reverseDico["';
		echo substr($species[$i], $posi+1, strlen($species[$i]));
		echo '"]="';
		echo substr($species[$i], 0, $posi);
		echo '";dico["';
		echo substr($species[$i], 0, $posi);
		echo '"]="';
		echo substr($species[$i], $posi+1, strlen($species[$i]));
		echo '";';

		$count++;

	}

}

?>
</script>

<?php

socket_close($socket);









?>

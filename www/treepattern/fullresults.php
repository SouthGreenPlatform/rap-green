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


$fp = fopen($resdir.$_REQUEST['id'].".csv", 'w');
$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
$resultat = socket_connect($socket, $server, $port);
// send the signal to the daemon

$envoi="saveResults\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_REQUEST['databank']."\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_REQUEST['id']."\n";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_REQUEST['pattern']."\n";
socket_write($socket, $envoi, strlen($envoi));
//$count=0;
$trace="";
while ($reception = socket_read($socket, 2048)) {
	// split the buffer
	$families=preg_split("/\n/", $reception);
	fwrite($fp, $trace.$families[0]."\n");
	for ($i=1;$i<count($families)-1;$i++) {
		// if the current word is significant
		if (strlen($families[$i])>0) {
			//echo $families[$i]."\n";

			fwrite($fp, $families[$i]."\n");
			//$count++;
		}
	}
	$trace=$families[count($families)-1];
}
fwrite($fp, $trace."\n");
fclose($fp);


socket_close($socket);


?><blockquote>
<p>Your results are available <a href="out/<?php echo $_REQUEST['id']; ?>.csv">here</a>, as a downloadable CSV file.<br>We cannot guarantee that your file will be available for long, so download it as soon as possible.</p>
</blockquote>
</body>
</html>

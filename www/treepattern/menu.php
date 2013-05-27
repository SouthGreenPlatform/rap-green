<?php
// open the socket
$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
$resultat = socket_connect($socket, "localhost", 1666);
// send the signal to the daemon
$envoi="databanks\n";
$count=0;
$first="";
socket_write($socket, $envoi, strlen($envoi));
while ($reception = socket_read($socket, 2048)) {
	// split the buffer
	$databanks=split("\n", $reception);
	for ($i=0;$i<count($databanks);$i++) {
		// if the current word is significant
		if (strlen($databanks[$i])>0) {
			if ($count==0) {
				$first=$databanks[$i];
			}
			// stock the default database
			if ($count==0) {
				$first=$databanks[$i];
			}


			$banks[$count]=$databanks[$i];

			$count++;
		}
	}

}
// select the default database if not already defined
if (!isSet($_REQUEST['databank'])) {
	$_REQUEST['databank']=$first;
}
socket_close($socket);

// open the socket
$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
$resultat = socket_connect($socket, "localhost", 1666);
// send the signal to the daemon
$envoi="specification\n";
$count=0;
$first="";
socket_write($socket, $envoi, strlen($envoi));
$envoi=$_REQUEST['databank']."\n";
socket_write($socket, $envoi, strlen($envoi));
$spec="";
while ($reception = socket_read($socket, 2048)) {
	// split the buffer
	$databanks=split("\n", $reception);
	for ($i=0;$i<count($databanks);$i++) {
		// if the current word is significant
		if (strlen($databanks[$i])>0) {
			$spec=$databanks[$i];
			$count++;
		}
	}

}
// select the default database if not already defined
if (!isSet($_REQUEST['databank'])) {
	$_REQUEST['databank']=$first;
}
socket_close($socket);

?>

<script type="text/javascript">

var opac='0.8';
function displayOnPress(e) {
    if (e.keyCode == 13) {
        displayOnClick();
    }
}

function displayOnClick() {
	// action . . .
}

function changeDatabase(newDatabase) {
	document.location.href="index.php?databank=" + newDatabase;
	//database=newDatabase;
	//document.getElementsByName("databaseTd")[0].innerHTML="DATABASE: " + database;
}

function refreshPattern(param) {
	if (param==4) {
		alert(param);
	}
	document.getElementsByName("searchTd")[0].innerHTML="Tree pattern matching in progress: " + param;
	return 1;
}
</script>

<table id="menu">
<tr id="toptr"><td colspan=10></td></tr>
<tr id="itemtr">

<td id="begining">
</td>

<td id="itemtd" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popfile',1);changeVisibiliteOnName('popdatabase',0);">
FILE
</td>

<td name="databaseTd" id="itemtd" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popfile',0);changeVisibiliteOnName('popdatabase',1);">
DATABASE: <?php if ($_REQUEST['databank']=="") {echo "No database available";} else {echo $_REQUEST['databank'];} ?>
</td>

<td name="searchTd" id="itemtd" onmouseout="this.style.opacity = opac;" onclick="displayResults();" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popfile',0);changeVisibiliteOnName('popdatabase',0);">
SEARCH PATTERN
</td>

<td id="closing">
</td>

</tr>
<tr height="0px">
<td id="poptd" colspan=1>
</td>
<td id="poptd" colspan=10>

<div id="popfile" id="itempop" name="popfile" style="display:none;" onmouseover="changeVisibiliteOnName('popfile',1)" onmouseout="changeVisibiliteOnName('popfile',0)">
<form name="changeTreeForm" method="post"  action="index.php">
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popfile',1);this.style.opacity = '1.0';" onclick="">Load pattern</p>
<hr id="large" onmouseover="changeVisibiliteOnName('popfile',1)">
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popfile',1);this.style.opacity = '1.0';" onclick="">Save pattern</p>
</form>
</div>
</td>
</tr>

<tr height="0px">
<td id="poptd" colspan=2>
</td>
<td id="poptd" colspan=10>

<div id="popdatabase" id="itempop" name="popdatabase" style="display:none;" onmouseover="changeVisibiliteOnName('popdatabase',1)" onmouseout="changeVisibiliteOnName('popdatabase',0)">
<p id="textual" onmouseover="changeVisibiliteOnName('popdatabase',1);">Available databases:</p>
<?php
for($i = 0; $i < count($banks); ++$i) {

?>

<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popdatabase',1);this.style.opacity = '1.0';" onclick="changeDatabase(<?php echo "'$banks[$i]'"; ?>)">

<?php

			echo $banks[$i];
?>
</p>
<?php


}

if ($_REQUEST['databank']=="") {
?>
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popdatabase',1);this.style.opacity = '1.0';" onclick="">
<?php
echo "No database available</p>";

} else {
?>
<script type="text/javascript">
//intialisation of database global variable
database="<?php echo $_REQUEST['databank']; ?>";

</script>

<?php
}
?>

</div>
</td>
</tr>
<tr height="0px">
<td id="poptd" colspan=3>
</td>
<td id="tdresults" colspan=12>

<div id="popresults" id="" name="popresults" style="display:none;">

</div>
</td>
</tr>

</table>
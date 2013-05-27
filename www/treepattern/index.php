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

 include('pattern.php');

?>
<script type="text/javascript">
<?php 

 if (isSet($_REQUEST['width'])) {
 	echo "width=".$_REQUEST['width'];	
 }
?>
// global variables of the interface

// initialised in the menu
var database="";
var tree= new Node(";");
var tool="speciation";
var dico= new Array();

</script>

<?php

 include('utilities.php');

?>
<?php

 include('menu.php');

?>

<div id="main">
<table id="organise">
<tr>
<td id="treePanel" name="treePanel">


</td>
<script type="text/javascript">
drawAll();
//tree.printTree(0);
</script>
<td id="toped">


<div id="poptaxa" style="display:none;">
Enter a taxa scientific name:<br>
<input id="speciesfield" type="text" size="40" value="" onkeyup="changeVisibiliteOnName('poplist',1);return refreshTaxaList(event,this.value)"><input type="button" value="Allow" onclick="addTaxon(document.getElementById('speciesfield').value,0)"><input type="button" value="Forbid" onclick="addTaxon(document.getElementById('speciesfield').value,1)">
<br>
<div name="poplist" id="poplist" style="position: absolute;">

</div><br>
<select width="300" style="width: 300px" id="speciesselector" multiple size="10">
</select>
<br>
<input type="button" value="Delete all" onclick="removeAllTaxon()">
</div>

</td>
</tr>
</table>
</div>

<?php

 include('toolbox.php');

?>
</body>
</html>
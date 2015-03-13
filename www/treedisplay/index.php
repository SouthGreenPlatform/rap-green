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
<div id="content"></div>

<?php

 include('utilities.php');

?>
<?php

 include('annotations.php');

?>
<?php 
//echo $_SERVER['HTTP_USER_AGENT'];
if ($_REQUEST['ie']==1 || strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) !== FALSE ) {
	//deprecated include("trees_ie.php"); 
	include("trees_standard.php");
} else {
	
	include("trees_standard.php");
}
?>
<?php

 include('menu.php');

?>



<script type="text/javascript">
function collapseType(val) {
	if(val.selectedIndex == 1)
	{
		collapseLabel('typeI');
	}
	else if(val.selectedIndex == 2)
	{
		collapseLabel('typeII');
	}
	else if(val.selectedIndex == 3)
	{
		collapseLabel('typeIII');
	}
	else if(val.selectedIndex == 4)
	{
		collapseLabel('typeIV');
	}
	else if(val.selectedIndex == 5)
	{
		collapseLabel('typeV');
	}
	else if(val.selectedIndex == 6)
	{
		collapseLabel('typeVI');
	}
	else if(val.selectedIndex == 7)
	{
		collapseLabel('typeVIII');
	} else if(val.selectedIndex == 8)
	{
		collapseLabel('typeI');
		collapseLabel('typeII');
		collapseLabel('typeIII');
		collapseLabel('typeIV');
		collapseLabel('typeV');
		collapseLabel('typeVI');
		collapseLabel('typeVIII');
	}
	val.selectedIndex=0;
}
</script>


<div id="legend" style="display:none;">

<table id="organise">
<tr>

<td name="legendPanel">

</td>
</tr>
</table>
</div>
<div height="200px" id="popannot" style="display:none;">


</div>
<?php	
	if (isSet($_POST['hiddenfield'])) {
		//echo $_POST['hiddenfield'];
		
	}
		
		
?>
<div id="treeDivId" style="overflow:scroll; width : 100% ; height : 800px ;">
<table id="organise"><tr><td id="treePanel" name="treePanel">





<script type="text/javascript">
var tree= <?php	
	if (isSet($_POST['hiddenfield'])) {
		echo 'new Node("';

		echo substr($_POST['hiddenfield'],0,strpos($_POST['hiddenfield'],";")+1);
	
		echo '");';
	} else {
		echo 'new Node("';

		echo $treenewick;
	
		echo '");';
	}


?>

</script>



<script type="text/javascript">
addAnnot("GO",1,"popup");
addAnnot("PO",1,"popup");
addAnnot("nameMSDMin",0,"plain");
addAnnot("primTypeN",0,"plain");
</script>
<script type="text/javascript">





drawAll();
<?php
if (isSet($_REQUEST['data']) && ($_REQUEST['data']=="EIL_banana" || $_REQUEST['data']=="EBF_banana")) {
?>
	tree.annoteValues(6,"red",labelExpress1,maxExpress1,express1,0);
	tree.annoteValues(18,"blue",labelExpress2,maxExpress2,express2,9.45);
<?php
} else if (isSet($_REQUEST['data']) && $_REQUEST['data']=="genfam") {
?>
	tree.annoteValues(6,"blue",titlesExpress,sumExpress/nbExpress*2.5,expvalues,0);
<?php
}
?>

</script>

<?php

 include('postannotation.php');

?>
</td></tr></table>



</div>



<!-- The footnote -->
</body>
</html>
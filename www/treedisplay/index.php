<!-- Standard definition page -->
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:html="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<meta http-equiv="Content-Language" content="en-gb">

<?php

 include('config.php');

?>

<head>
<?php if ($activategeco=="true") { ?>
 <!--Necessaire pour GCV-->
 <script src="d3.v5.js?<?php echo time();?>"></script>
    <script src="gcv_js_V1.js?<?php echo time();?>"></script>
  	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js?<?php echo time();?>"></script>


   <script src="https://d3js.org/d3-color.v1.min.js"></script>
<script src="https://d3js.org/d3-interpolate.v1.min.js"></script>
<script src="https://d3js.org/d3-scale-chromatic.v1.min.js"></script>
<!-- Standard headers (CSS...) -->
<?php } ?>
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
include("trees_standard.php");
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

<div name="treeDivId" id="treeDivId" style="overflow:scroll; width : 100% ; height : 800px ;">
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
	//alert("1");
	createIdeven(tree,ideven);
	tree.annoteValues(6,"blue",titlesExpress,sumExpress/nbExpress*2.5,sumNegExpress/nbNegExpress*2.5,expvalues,0);
	linkGeneFamEvent(tree);
	//alert("2");
<?php
}
?>
<?php
if (isSet($_REQUEST['data']) && $_REQUEST['data']=="express") {
?>
	//alert("1");
	tree.annoteValues(6,"blue",titlesExpress,sumExpress/nbExpress*2.5,sumNegExpress/nbNegExpress*2.5,expvalues,0);
	//alert("2");
<?php
}
?>
<?php if (isSet($_REQUEST['focus'])) {?>
var focusParam="<?php echo $_REQUEST['focus']; ?>";
var focusLeaf= indexOfleaves[focusParam];
var focusText= focusLeaf.text;
//focusText.appendChild(document.createTextNode("    <<<"));
focusText.setAttribute("font-weight","bold");
focusText.setAttribute("text-decoration","underline");
focusText.setAttribute("fill",tagColor);
scrollTree(focusText.getAttribute("y")-200);
focusLeaf.taxon="Focused sequence: "+focusLeaf.taxon;
colorize(focusLeaf.taxon,tagColor);
focusText.setAttribute("font-size",(2+parseFloat(focusText.getAttribute("font-size"))));
focusLeaf.taxon=focusLeaf.taxon.substring(18,focusLeaf.taxon.length());
<?php
}
?>

</script>


</td>


<?php if ($activategeco=="true") { ?>

<td id= "myidGCV" ></td>

<?php } ?>


</tr>
</table>

</div>


<?php if ($activategeco=="true") { ?>

<script>



 var listLeaves=Object.keys(indexOfleaves);//recupere l'ordre des gènes sur l'arbre

for (var i = 0;i<listLeaves.length;i++){
 index=listLeaves[i].lastIndexOf("_");//recureration de lindexe de "_" dans chaque nm de gène
 var espece = listLeaves[i].substr(0,index); //permet de retirer le _speciesCode dans le nom de gène

 //Verifie pour voir s'il ya le .p car dans le nom des gèn nous n'avons pas de .p dasn la base de données
 if(espece[(espece.length-1)]=="p"){
   listLeaves[i]=espece.substring(0,espece.length-2);
  }else{
   listLeaves[i]=espece;
  }

 }


  var neighborsRef;
  var gene;
  var stn;//ZOne de représentation du contexte
  var geneRef=listLeaves[0];//Nous prenons le premier gène comme la refference
  var nbNeighbors=15;

  function recuperation(geneRef,nbVoisin){
  $(document).ready(function(){
          $.ajax({
                    url: "gcvBDconfig.php",
                    type: "POST",
                    data : { geneName: geneRef, nbNeighbors:nbVoisin },
                    success: function(data){
                      var  datas = JSON.parse(data);

                      gene = datas.gene;neighborsRef=datas.neighborsRef;
                      stn = new gcv("#myidGCV",(nbNeighbors*5)*25,height,"blue",fill="none");
                      stn.drawAll(gene,neighborsRef,nbNeighbors,listLeaves);
                   }
          },'json');

      $("#zoom").click(function(){stn.zoom();});
      $("#dezoomer").click(function(){stn.dezoomer();});
     });}
  recuperation(geneRef,nbNeighbors);
</script>

<?php } ?>

<?php

 include('postannotation.php');

?>

<!-- The footnote -->
</body>
</html>

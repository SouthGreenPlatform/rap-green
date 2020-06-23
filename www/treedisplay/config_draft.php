<?php

$activategeco="false";

?>

<script type="text/javascript">


var alertDebug=0;

// Define primal dimensions and margins
var width=<?php if (isSet($_REQUEST['width'])) { echo $_REQUEST['width'].';'; } else {?>1800;<?php } ?>
var height=600;
var margin=30;
var annotMargin=600;
var annotX=0;
var legendHeight=80;
var legendX=0;
// Computed regarding data
var taxaMargin=0;

// Define the esthetic parameters of the tree displaying
var fontFamily="Candara";
var fontSize=14;
var legendFontSize="14";
var supportSize=11;
var fontColor="black";
var backColor="white";
var lineWidth= 2;
var lineColor= "#05357E";
var collapseColor="#EEEEEE";
var tagColor="#FF0000";
var splitColor="#000000"
var roundray=20;
var collapseWidth=3.0;
var tagWidth=15;
var opacitydegree=0.7;




var displayadress= new Array();
displayadress['ORYSJ']="https://grass-genome-hub.southgreen.fr/mRNA/";
displayadress['ZEAMA']="https://grass-genome-hub.southgreen.fr/protein/";
displayadress['SORBI']="https://grass-genome-hub.southgreen.fr/protein/";
displayadress['SETIT']="https://grass-genome-hub.southgreen.fr/protein/";
displayadress['MISSI']="https://grass-genome-hub.southgreen.fr/protein/";
displayadress['ARATH']="https://grass-genome-hub.southgreen.fr/mRNA/";
</script>

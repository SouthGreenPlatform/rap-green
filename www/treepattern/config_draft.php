
<script type="text/javascript">
// Define primal dimensions and margins
var width=900;
var height=400;
var margin=40;
</script>

<?php
/* *************************************************************** */
/* To install the service, you must rename this file in config.php */
/* *************************************************************** */

/* specify the server address as a string, between double quotes */
$server="localhost";
/* specify the opened port on the server as an integer */
$port=1666;
/* path to the result directory */
$resdir="";
/* link to the secondary visualisator, can contains the FAMID word to be replaced in the address by the family id contained in the database */
$displayadress=Array("My base" => "My address");
/* name of the secondary visualisator */
$displaytag=Array("My base" => "My tag");
/* public web address of the main treedisplay interface */
$treedisplayaddress="https://phylogeny.southgreen.fr/treedisplay/";
/* set to false if you don't want to allow user to choose between several databases */
$displaydatabases="true";
/* the size of the species field in the taxa cardinality tool, in number of character*/
$speciesfieldsize=40;
/* the size of the species selector in the taxa cardinality tool, in pixel */
$speciesselectsize=350; 


?>

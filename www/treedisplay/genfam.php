<script type="text/javascript">
var brut_expression="<?php 
echo str_replace("\r\n","&",$_POST['expression']); 
?>";
//alert(brut_expression);
var rows= brut_expression.split("&");
var titlesExpress= new Array();
var expvalues= new Array();
var splited= rows[0].split(",");
for (var i = 1; i < splited.length; i++) {
	titlesExpress[i-1]= splited[i];	
}
var sumExpress=0.0;
var nbExpress=0.0;
//alert("loc:" + titlesExpress.length);
for (var i = 1; i < rows.length; i++) {
	splited= rows[i].split(",");
	if (splited[1]!="UN") {
		expvalues[splited[0]]= new Array();
		for (var j = 1; j < splited.length; j++) {
			//if (i==1) alert(splited.length);
			var local=parseFloat(splited[j]);
			//alert(local);
			/*if (maxExpress<local) {
				maxExpress=local;
			}*/
			sumExpress+=local;
			nbExpress++;
			expvalues[splited[0]][j-1]= local;
		}
	}
}


</script>


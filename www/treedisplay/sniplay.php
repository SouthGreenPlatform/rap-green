<script type="text/javascript">
var brut_sniplay="<?php 
echo str_replace("\r\n","&",$_POST['colorsubtree']); 
?>";
//alert(brut_sniplay);
var rows= brut_sniplay.split("&");
var sniplay_colors= new Array();
for (var i = 0; i < rows.length; i++) {
	//alert(rows[i]);
	var splited= rows[i].split(",");
	var local_sniplay= new Array();
	local_sniplay['color']=splited[0];
	local_sniplay['size']=splited.length-1;
	for (var j = 1; j < splited.length; j++) {
		local_sniplay[splited[j]]=1;
		
	}
	
	sniplay_colors[i]=local_sniplay;
}
for (var i = 0; i < sniplay_colors.length; i++) {
	tree.colorizeStrictSubtreeWithLeaves(sniplay_colors[i]);
}

</script>


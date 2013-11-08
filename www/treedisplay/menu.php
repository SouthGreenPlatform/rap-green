<script type="text/javascript">
var opac='0.8';
function colorOnPress(e) {
    if (e.keyCode == 13) {
        colorize(document.getElementById('wordcolor').value,document.getElementById('colorcolor').value);
    }
}

function splitOnPress(e) {
    if (e.keyCode == 13) {
        treeSplit(document.getElementById('splittreshold').value);
    }
}

function collapseOnPress(e) {
    if (e.keyCode == 13) {
        collapseLabel(document.getElementById('wordcollapse').value);
    }
}

function displayOnPress(e) {
    if (e.keyCode == 13) {
        displayOnClick();
    }
}

function displayOnClick() {
	document.body.style.background =document.getElementById('backcolor').value;
	setAttributes("","",document.getElementById('linewidth').value,document.getElementById('roundray').value,"",document.getElementById('supportsize').value,document.getElementById('textfont').value,document.getElementById('collapsesize').value,document.getElementById('backcolor').value,document.getElementById('linecolor').value,document.getElementById('collapsecolor').value,document.getElementById('fontcolor').value,"",document.getElementById('opacitydegree').value);
}

function changeColorAnnote(e) {
    if (e.keyCode == 13) {
		colorbranchannote=document.getElementById('colorbranchannote').value;
		document.getElementById('colorbranchannote').style.backgroundColor=document.getElementById('colorbranchannote').value;
		//alert(document.getElementById('colorbranchannote').value);
	}
}

function initColors() {
	document.getElementById('colorbranchannote').style.backgroundColor=document.getElementById('colorbranchannote').value;
	if (document.getElementById('colorTypeI')!=null) {
	document.getElementById('colorTypeI').value=colorCustom["colorTypeI"];
	document.getElementById('colorTypeI').style.backgroundColor=colorCustom["colorTypeI"];
	document.getElementById('colorTypeII').value=colorCustom["colorTypeII"];
	document.getElementById('colorTypeII').style.backgroundColor=colorCustom["colorTypeII"];
	document.getElementById('colorTypeIII').value=colorCustom["colorTypeIII"];
	document.getElementById('colorTypeIII').style.backgroundColor=colorCustom["colorTypeIII"];
	document.getElementById('colorTypeIV').value=colorCustom["colorTypeIV"];
	document.getElementById('colorTypeIV').style.backgroundColor=colorCustom["colorTypeIV"];
	document.getElementById('colorTypeV').value=colorCustom["colorTypeV"];
	document.getElementById('colorTypeV').style.backgroundColor=colorCustom["colorTypeV"];
	document.getElementById('colorTypeVI').value=colorCustom["colorTypeVI"];
	document.getElementById('colorTypeVI').style.backgroundColor=colorCustom["colorTypeVI"];
	document.getElementById('colorTypeVIII').value=colorCustom["colorTypeVIII"];
	document.getElementById('colorTypeVIII').style.backgroundColor=colorCustom["colorTypeVIII"];
	}
}

var colorCustom=new Array();
colorCustom["colorTypeI"]="red";
colorCustom["colorTypeII"]="blue";
colorCustom["colorTypeIII"]="green";
colorCustom["colorTypeIV"]="lightgreen";
colorCustom["colorTypeV"]="purple";
colorCustom["colorTypeVI"]="orange";
colorCustom["colorTypeVIII"]="brown";

function changeColorCustom(e,id) {
    if (e.keyCode == 13) {
		colorCustom[id]=document.getElementById(id).value;
		document.getElementById(id).style.backgroundColor=document.getElementById(id).value;
		//alert(document.getElementById('colorbranchannote').value);
	}
}

function annoteAll() {
	if (annotebranchestool=="collapse" || annotebranchestool=="down") {
		check('colfet1');check('colfet2');check('colfet3');check('colfet4');check('colfet5');check('colfet6');check('colfet7');collapseLabel('TypeI_');collapseLabel('TypeII_');collapseLabel('TypeIII_');collapseLabel('TypeIV_');collapseLabel('TypeV_');collapseLabel('TypeVI_');collapseLabel('TypeVIII_');
	} else if (annotebranchestool=="color") {
		var oldcolor=colorbranchannote;
		check('colfet1');check('colfet2');check('colfet3');check('colfet4');check('colfet5');check('colfet6');check('colfet7');colorbranchannote=colorCustom["colorTypeI"];collapseLabel('TypeI_');colorbranchannote=colorCustom["colorTypeII"];collapseLabel('TypeII_');colorbranchannote=colorCustom["colorTypeIII"];collapseLabel('TypeIII_');colorbranchannote=colorCustom["colorTypeIV"];collapseLabel('TypeIV_');colorbranchannote=colorCustom["colorTypeV"];collapseLabel('TypeV_');colorbranchannote=colorCustom["colorTypeVI"];collapseLabel('TypeVI_');colorbranchannote=colorCustom["colorTypeVIII"];collapseLabel('TypeVIII_');	
		colorbranchannote=oldcolor;
	}
}

function annoteSpecific(id,tag) {
	if (annotebranchestool=="collapse") {
		collapseLabel(tag);
	} else {
		var oldcolor=colorbranchannote;
		colorbranchannote=colorCustom[id];
		collapseLabel(tag);
		colorbranchannote=oldcolor;
	}
}

</script>

<table id="menu">
<tr id="toptr"><td colspan=10></td></tr>
<tr id="itemtr">

<td id="begining">
</td>

<td id="itemtd" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popload',1);changeVisibiliteOnName('popadv',0);changeVisibiliteOnName('popcoloration',0);changeVisibiliteOnName('popzoom',0);changeVisibiliteOnName('popcollapse',0);changeVisibiliteOnName('popdata',0);changeVisibiliteOnName('popdisplay',0);">
LOAD
</td>

<td id="itemtd" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popload',0);changeVisibiliteOnName('popadv',0);changeVisibiliteOnName('popcoloration',1);changeVisibiliteOnName('popzoom',0);changeVisibiliteOnName('popcollapse',0);changeVisibiliteOnName('popdata',0);changeVisibiliteOnName('popdisplay',0);">
ANNOTE LEAVES
</td>

<td id="itemtd" onmouseout="this.style.opacity = opac;" onmouseover="initColors();this.style.opacity = '1.0';changeVisibiliteOnName('popload',0);changeVisibiliteOnName('popadv',0);changeVisibiliteOnName('popcollapse',1);changeVisibiliteOnName('popzoom',0);changeVisibiliteOnName('popcoloration',0);changeVisibiliteOnName('popdata',0);changeVisibiliteOnName('popdisplay',0);">
ANNOTE BRANCHES
</td>


<td id="itemtd" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popload',0);changeVisibiliteOnName('popadv',0);changeVisibiliteOnName('popcollapse',0);changeVisibiliteOnName('popzoom',0);changeVisibiliteOnName('popcoloration',0);changeVisibiliteOnName('popdata',1);changeVisibiliteOnName('popdisplay',0);">
DATA
</td>

<td id="itemtd" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popload',0);changeVisibiliteOnName('popadv',0);changeVisibiliteOnName('popdisplay',1);changeVisibiliteOnName('popzoom',0);changeVisibiliteOnName('popcollapse',0);changeVisibiliteOnName('popcoloration',0);changeVisibiliteOnName('popdata',0);">
DISPLAY OPTIONS
</td>

<td id="itemtd" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popload',0);changeVisibiliteOnName('popadv',0);changeVisibiliteOnName('popzoom',1);changeVisibiliteOnName('popdisplay',0);changeVisibiliteOnName('popcollapse',0);changeVisibiliteOnName('popcoloration',0);changeVisibiliteOnName('popdata',0);" onclick="wZoomOut();">
ZOOM
</td>

<td id="itemtd" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popload',0);changeVisibiliteOnName('popadv',1);this.style.opacity = '1.0';changeVisibiliteOnName('popzoom',0);changeVisibiliteOnName('popdisplay',0);changeVisibiliteOnName('popcollapse',0);changeVisibiliteOnName('popcoloration',0);changeVisibiliteOnName('popdata',0);" onclick="wZoomOut();">
GRAPHICAL RENDERING
</td>

<td id="closing">
</td>

</tr>
<tr height="0px">
<td id="poptd" colspan=1>
</td>
<td id="poptd" colspan=10>

<div id="popload" id="itempop" name="popload" style="display:none;" onmouseover="changeVisibiliteOnName('popload',1)" onmouseout="changeVisibiliteOnName('popload',0)">
<form name="changeTreeForm" method="post"  action="index.php">
<p id="textual" onmouseover="changeVisibiliteOnName('popload',1);"><textarea onkeypress="" onmouseover="changeVisibiliteOnName('popload',1)" cols="12" rows="5" name="hiddenfield" id="hiddenfield"><?php echo $_POST['hiddenfield']; ?></textarea></p>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popload',1)">
<?php } ?>
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popload',1);this.style.opacity = '1.0';" onclick="document.changeTreeForm.submit();">Load tree</p>
</form>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popload',1)">
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popload',1);this.style.opacity = '1.0';" onclick="saveSVG();">Save as image</p>
<?php } ?>
</div>
</td>
</tr>

<tr height="0px">
<td id="poptd" colspan=2>
</td>
<td id="poptd" colspan=10>

<div id="popcoloration" id="itempop" name="popcoloration" style="display:none;" onmouseover="changeVisibiliteOnName('popcoloration',1)" onmouseout="changeVisibiliteOnName('popcoloration',0)">
<p id="textual" onmouseover="changeVisibiliteOnName('popcoloration',1);"><input onkeypress="return colorOnPress(event)" onmouseover="changeVisibiliteOnName('popcoloration',1)" type="text" size="8" id="wordcolor" value="Arath" />&nbsp;Word</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popcoloration',1);"><input onkeypress="return colorOnPress(event)" onmouseover="changeVisibiliteOnName('popcoloration',1)" type="text" size="8" id="colorcolor" value="#FF0000" />&nbsp;Color</p>
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popcoloration',1);this.style.opacity = '1.0';" onclick="colorize(document.getElementById('wordcolor').value,document.getElementById('colorcolor').value);">Validate</p>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popcoloration',1)">
<?php } ?>
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popcoloration',1);this.style.opacity = '1.0';" onclick="resetColors();">Reset colors</p>

<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popcoloration',1)">

<p id="textual" onmouseover="changeVisibiliteOnName('popcoloration',1);"><input onkeypress="return splitOnPress(event)" onmouseover="changeVisibiliteOnName('popcoloration',1)" type="text" size="8" id="splittreshold" value="0.5" />&nbsp;Threshold</p>
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popcoloration',1);this.style.opacity = '1.0';" onclick="treeSplit(document.getElementById('splittreshold').value);">Split tree</p>

<?php } ?>




</div>
</td>
</tr>

<tr id="poptr">
<td id="poptd" colspan=3>
</td>
<td id="poptd" colspan=10>

<div id="popcollapse" id="itempop" name="popcollapse" style="display:none;" onmouseover="changeVisibiliteOnName('popcollapse',1)" onmouseout="changeVisibiliteOnName('popcollapse',0);">

<p id="textual" onmouseover="changeVisibiliteOnName('popcollapse',1);">Choose a tool:</p>

<p id="textual" onmouseover="changeVisibiliteOnName('popcollapse',1);">
<input type="radio" name="collapsetool" value="collapse" onclick="annotebranchestool='collapse';" checked>&nbsp;Collapse<br>
<input type="radio" name="collapsetool" value="color" onclick="annotebranchestool='color';">&nbsp;Color&nbsp;
<input onkeypress="changeColorAnnote(event);" onmouseover="changeVisibiliteOnName('popcollapse',1)" type="text" size="8" id="colorbranchannote" value="Red" /><br>
<input type="radio" name="collapsetool" value="down" onclick="annotebranchestool='down';">&nbsp;Tone down
</p>

<hr id="large" onmouseover="changeVisibiliteOnName('popcollapse',1)">

<p id="textual" onmouseover="changeVisibiliteOnName('popcollapse',1);"><input onkeypress="return collapseOnPress(event)" onmouseover="changeVisibiliteOnName('popcollapse',1)" type="text" size="8" id="wordcollapse" value="TypeI_" />&nbsp;Last common ancestor</p>
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popcollapse',1);this.style.opacity = '1.0';" onclick="collapseLabel(document.getElementById('wordcollapse').value);">Annote</p>



<?php 
if (isSet($_REQUEST['data'])) {
?>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popcollapse',1)">
<?php } ?>
<p id="textual" onmouseover="changeVisibiliteOnName('popcollapse',1);">Featured annotations:</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popcollapse',1);"><input name="colfet1" onmouseover="changeVisibiliteOnName('popcollapse',1);" type=checkbox onclick="annoteSpecific('colorTypeI','TypeI_');">TypeI_&nbsp;
<input onkeypress="changeColorCustom(event,'colorTypeI');" onmouseover="changeVisibiliteOnName('popcollapse',1)" type="text" size="8" id="colorTypeI" /><br></p>
<p id="textual" onmouseover="changeVisibiliteOnName('popcollapse',1);"><input name="colfet2" onmouseover="changeVisibiliteOnName('popcollapse',1);" type=checkbox onclick="annoteSpecific('colorTypeII','TypeII_');">TypeII_&nbsp;
<input onkeypress="changeColorCustom(event,'colorTypeII');" onmouseover="changeVisibiliteOnName('popcollapse',1)" type="text" size="8" id="colorTypeII" /><br></p>
<p id="textual" onmouseover="changeVisibiliteOnName('popcollapse',1);"><input name="colfet3" onmouseover="changeVisibiliteOnName('popcollapse',1);" type=checkbox onclick="annoteSpecific('colorTypeIII','TypeIII_');">TypeIII_&nbsp;
<input onkeypress="changeColorCustom(event,'colorTypeIII');" onmouseover="changeVisibiliteOnName('popcollapse',1)" type="text" size="8" id="colorTypeIII" /><br></p>
<p id="textual" onmouseover="changeVisibiliteOnName('popcollapse',1);"><input name="colfet4" onmouseover="changeVisibiliteOnName('popcollapse',1);" type=checkbox onclick="annoteSpecific('colorTypeIV','TypeIV_');">TypeIV_&nbsp;
<input onkeypress="changeColorCustom(event,'colorTypeIV');" onmouseover="changeVisibiliteOnName('popcollapse',1)" type="text" size="8" id="colorTypeIV" /><br></p>
<p id="textual" onmouseover="changeVisibiliteOnName('popcollapse',1);"><input name="colfet5" onmouseover="changeVisibiliteOnName('popcollapse',1);" type=checkbox onclick="annoteSpecific('colorTypeV','TypeV_');">TypeV_&nbsp;
<input onkeypress="changeColorCustom(event,'colorTypeV');" onmouseover="changeVisibiliteOnName('popcollapse',1)" type="text" size="8" id="colorTypeV" /><br></p>
<p id="textual" onmouseover="changeVisibiliteOnName('popcollapse',1);"><input name="colfet6" onmouseover="changeVisibiliteOnName('popcollapse',1);" type=checkbox onclick="annoteSpecific('colorTypeVI','TypeVI_');">TypeVI_&nbsp;
<input onkeypress="changeColorCustom(event,'colorTypeVI');" onmouseover="changeVisibiliteOnName('popcollapse',1)" type="text" size="8" id="colorTypeVI" /><br></p>
<p id="textual" onmouseover="changeVisibiliteOnName('popcollapse',1);"><input name="colfet7" onmouseover="changeVisibiliteOnName('popcollapse',1);" type=checkbox onclick="annoteSpecific('colorTypeVIII','TypeVIII_');">TypeVIII_&nbsp;
<input onkeypress="changeColorCustom(event,'colorTypeVIII');" onmouseover="changeVisibiliteOnName('popcollapse',1)" type="text" size="8" id="colorTypeVIII" /><br></p>
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popcollapse',1);this.style.opacity = '1.0';" onclick="annoteAll();">Annote all types</p> 
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popcollapse',1);this.style.opacity = '1.0';" onclick="tree.toneDownUndocumented();">Tone down undocumented proteins</p> 



<?php 
}
?>
</div>

</td>
</tr>

<tr id="poptr">
<td id="poptd" colspan=4>
</td>
<td id="poptd" colspan=10>
<div id="popdata" id="itempop" name="popdata" style="display:none;" onmouseover="changeVisibiliteOnName('popdata',1);" onmouseout="changeVisibiliteOnName('popdata',0);">
<p id="textual" onmouseover="changeVisibiliteOnName('popdata',1);"><input onmouseover="changeVisibiliteOnName('popdata',1);" type=checkbox name="GO" onclick="hideShow('GO');" checked>GO</input></p> 
<p id="textual" onmouseover="changeVisibiliteOnName('popdata',1);"><input onmouseover="changeVisibiliteOnName('popdata',1);" type=checkbox name="PO" onclick="hideShow('PO');" checked>PO</input></p> 
<p id="textual" onmouseover="changeVisibiliteOnName('popdata',1);"><input onmouseover="changeVisibiliteOnName('popdata',1);" type=checkbox name="Polypeptidename" onclick="hideShow('nameMSDMin');">Polypeptide name</input></p> 
<p id="textual" onmouseover="changeVisibiliteOnName('popdata',1);"><input onmouseover="changeVisibiliteOnName('popdata',1);" type=checkbox name="Typename" onclick="hideShow('primTypeN');">Typed full name</input></p> 
</div>

</td>
</tr>

<tr id="poptr">
<td id="poptd" colspan=<?php if (!isSet($_POST['hiddenfield'])) { echo "5"; } else { echo "4"; } ?>>
</td>
<td id="poptd" colspan=10>
<div id="popdisplay" id="itempop" name="popdisplay" style="display:none;" onmouseover="changeVisibiliteOnName('popdisplay',1);" onmouseout="changeVisibiliteOnName('popdisplay',0);">
<p id="textual" onmouseover="changeVisibiliteOnName('popdisplay',1);"><input onmouseover="changeVisibiliteOnName('popdisplay',1);" type=checkbox name="support" onclick="changeSupport();">Branch support</input></p>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popdisplay',1)">  
<?php } ?>
<p id="textual" onmouseover="changeVisibiliteOnName('popdisplay',1);"><input type="radio" onclick="changeTreeType('ultra');" onmouseover="changeVisibiliteOnName('popdisplay',1)" name="typetree" value="Ultrametric" checked>Ultrametric<br>
<input type="radio" onclick="changeTreeType('phylogram');" onmouseover="changeVisibiliteOnName('popdisplay',1)" name="typetree" value="Phylogram">Phylogram</p>
</div>

</td>
</tr>

<tr id="poptr">
<td id="poptd" colspan=<?php if (!isSet($_POST['hiddenfield'])) { echo "6"; } else { echo "5"; } ?>>
</td>
<td id="poptd" colspan=10>
<div id="popzoom" id="itempop" name="popzoom" style="display:none;" onmouseover="changeVisibiliteOnName('popzoom',1);" onmouseout="changeVisibiliteOnName('popzoom',0);">
<p id="lienimage" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popzoom',1);" onclick="wZoomOut();"><img width="40px" src="img/zoomin_h.png"></p>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popzoom',1)"> 
<?php } ?>
<p id="lienimage" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popzoom',1);" onclick="wZoomIn();"><img width="40px" src="img/zoomout_h.png"></p>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popzoom',1)"> 
<?php } ?>
<p id="lienimage" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popzoom',1);" onclick="hZoomOut();"><img width="40px" src="img/zoomin_v.png"></p>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popzoom',1)"> 
<?php } ?>
<p id="lienimage" onmouseout="this.style.opacity = opac;" onmouseover="this.style.opacity = '1.0';changeVisibiliteOnName('popzoom',1);" onclick="hZoomIn();"><img width="40px" src="img/zoomout_v.png"></p>
</div>

</td>
</tr>


<tr id="poptr">
<td id="poptd" colspan=<?php if (!isSet($_POST['hiddenfield'])) { echo "7"; } else { echo "6"; } ?>>
</td>
<td id="poptd" colspan=10>
<div id="popadv" id="itempop" name="popadv" style="display:none;" onmouseover="changeVisibiliteOnName('popadv',1);" onmouseout="changeVisibiliteOnName('popadv',0);">
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);">Tree lines:</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);"><input onkeypress="return displayOnPress(event)" onmouseover="changeVisibiliteOnName('popadv',1)" type="text" size="8" id="linewidth" value="2" />&nbsp;Line width</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);"><input onkeypress="return displayOnPress(event)" onmouseover="changeVisibiliteOnName('popadv',1)" type="text" size="8" id="linecolor" value="#05357E" />&nbsp;Line color</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);"><input onkeypress="return displayOnPress(event)" onmouseover="changeVisibiliteOnName('popadv',1)" type="text" size="8" id="roundray" value="20" />&nbsp;Round ray</p>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popadv',1)"> 
<?php } ?>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);">Branch annotations:</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);"><input onkeypress="return displayOnPress(event)" onmouseover="changeVisibiliteOnName('popadv',1)" type="text" size="8" id="collapsesize" value="3" />&nbsp;Lines per collapse</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);"><input onkeypress="return displayOnPress(event)" onmouseover="changeVisibiliteOnName('popadv',1)" type="text" size="8" id="collapsecolor" value="#EEEEEE" />&nbsp;Collapse color</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);"><input onkeypress="return displayOnPress(event)" onmouseover="changeVisibiliteOnName('popadv',1)" type="text" size="8" id="opacitydegree" value="0.3" />&nbsp;Opacity degree</p>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popadv',1)"> 
<?php } ?>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);">Texts:</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);"><input onkeypress="return displayOnPress(event)" onmouseover="changeVisibiliteOnName('popadv',1)" type="text" size="8" id="textfont" value="Candara" />&nbsp;Font family</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);"><input onkeypress="return displayOnPress(event)" onmouseover="changeVisibiliteOnName('popadv',1)" type="text" size="8" id="fontcolor" value="Black" />&nbsp;Font color</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);"><input onkeypress="return displayOnPress(event)" onmouseover="changeVisibiliteOnName('popadv',1)" type="text" size="8" id="supportsize" value="11" />&nbsp;Support size</p>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popadv',1)"> 
<?php } ?>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);">Background:</p>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);"><input onkeypress="return displayOnPress(event)" onmouseover="changeVisibiliteOnName('popadv',1)" type="text" size="8" id="backcolor" value="white" />&nbsp;Background color</p>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popadv',1)"> 
<?php } ?>
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popadv',1);this.style.opacity = '1.0';" onclick="displayOnClick();">Apply</p>
<?php if ($_REQUEST['ie']!=1 && strpos($_SERVER['HTTP_USER_AGENT'], 'MSIE' ) == FALSE ) {?>
<hr id="large" onmouseover="changeVisibiliteOnName('popadv',1)"> 
<?php } ?>
<p id="textual" onmouseover="changeVisibiliteOnName('popadv',1);">Featured configurations:</p> 
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popadv',1);this.style.opacity = '1.0';" onclick="document.getElementById('collapsecolor').value='#EEEEEE';document.getElementById('linecolor').value='#05357E';document.getElementById('fontcolor').value='black';document.getElementById('backcolor').value='white';displayOnClick();">Standard</p>
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popadv',1);this.style.opacity = '1.0';" onclick="document.getElementById('collapsecolor').value='#888888';document.getElementById('linecolor').value='white';document.getElementById('fontcolor').value='white';document.getElementById('backcolor').value='black';displayOnClick();">Blackboard</p>
</div>

</td>
</tr>

</table>
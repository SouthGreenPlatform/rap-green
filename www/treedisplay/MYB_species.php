<!-- Standard definition page -->
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:html="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<meta http-equiv="Content-Language" content="en-gb">
<head>
</head>
<body>
<form name="changeTreeForm" method="post"  action="index.php<?php if (isSet($_REQUEST['data'])) { echo '?data='.$_REQUEST['data']; }?>">
Tree:
<br><textarea  cols="100" rows="3" name="hiddenfield" id="hiddenfield">(((SETIT:1[&&NHX:C=240.170.0],(MAIZE:1[&&NHX:C=255.150.0],SORBI:1[&&NHX:C=255.0.0])147429:1[&&NHX:C=220.150.0])147369:1[&&NHX:C=200.150.0],(ORYSJ:1[&&NHX:C=255.255.0],BRADI:1[&&NHX:C=240.240.0])359160:1[&&NHX:C=220.220.0])4479:1[&&NHX:C=165.42.42],((MEDTR:1[&&NHX:C=0.0.255],POPTR:1[&&NHX:C=80.80.255])91835:1[&&NHX:C=80.80.220],(ARATH:1[&&NHX:C=0.255.0],EUCGR:1[&&NHX:C=80.255.80])91836:1[&&NHX:C=80.220.80])71275:1[&&NHX:C=150.100.150])3398:1[&&NHX:C=0.0.0];</textarea>
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popload',1);this.style.opacity = '1.0';" onclick="document.changeTreeForm.submit();"><a href="#">Load</a></p>
</form>

<!-- The footnote -->
</body>
</html>
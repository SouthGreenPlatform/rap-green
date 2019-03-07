<!-- Standard definition page -->
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:html="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<meta http-equiv="Content-Language" content="en-gb">
<head>
</head>
<body>
<form name="changeTreeForm" method="post"  action="index.php<?php if (isSet($_REQUEST['data'])) { echo '?data='.$_REQUEST['data']; }?>">
Tree:
<br><textarea  cols="100" rows="3" name="hiddenfield" id="hiddenfield">((((((((((((AA1:1[&&NHX:C=255.0.0],AA8:1[&&NHX:C=255.40.40])AAi1:1[&&NHX:C=255.0.0],(AA35:1[&&NHX:C=255.100.100],AAb:1[&&NHX:C=255.60.60])AAi2:1[&&NHX:C=255.100.100])AAi3:1[&&NHX:C=255.0.0],(AA4:1[&&NHX:C=200.0.0],AAm:1[&&NHX:C=220.0.0])AAi4:1[&&NHX:C=220.0.0])AAi5:1[&&NHX:C=255.0.0],(AA2:1[&&NHX:C=180.0.0],AAz:1[&&NHX:C=160.0.0])AAi6:1[&&NHX:C=180.0.0])AAi8:1[&&NHX:C=200.0.0],AA6:1[&&NHX:C=140.0.0])AAi7:1[&&NHX:C=180.0.0],(((AA13:1[&&NHX:C=255.255.0],AA7:1[&&NHX:C=240.240.0])AAi9:1[&&NHX:C=240.240.0],AA11:1[&&NHX:C=230.230.0])AAi10:1[&&NHX:C=230.230.0],M22:1[&&NHX:C=200.200.0])AAi11:1[&&NHX:C=200.200.0])AAi12:1[&&NHX:C=255.200.0],(M21:1[&&NHX:C=50.50.255],Ms:1[&&NHX:C=70.70.255])AAi12:1[&&NHX:C=50.50.255])AAi13:1[&&NHX:C=200.50.200],(Mv:1[&&NHX:C=80.80.255],M25:1[&&NHX:C=100.100.255])AAi14:1[&&NHX:C=100.100.255])AAi15:1[&&NHX:C=200.50.200],M24:1[&&NHX:C=0.0.220])AAi16:1[&&NHX:C=200.50.200],Mc:1[&&NHX:C=0.0.190])AAi17:1[&&NHX:C=0.255.0],(BBs:1[&&NHX:C=0.255.0],BB15:1[&&NHX:C=0.200.0])AAi18:1[&&NHX:C=0.255.0]):1[&&NHX:C=0.255.0],IGNAME:1[&&NHX:C=100.100.100],CLADE1:1[&&NHX:C=150.150.150])AAi19:1[&&NHX:C=0.0.0];</textarea>
<p id="linking" onmouseout="this.style.opacity = opac" onmouseover="changeVisibiliteOnName('popload',1);this.style.opacity = '1.0';" onclick="document.changeTreeForm.submit();"><a href="#">Load</a></p>
</form>

<!-- The footnote -->
</body>
</html>
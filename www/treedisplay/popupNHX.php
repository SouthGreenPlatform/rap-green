<!-- Standard definition page -->
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:html="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<meta http-equiv="Content-Language" content="en-gb">
<head>
<!-- Standard headers (CSS...) -->
<?php

 include('config.php');

?>
<?php

 include('header.php');

?>
</head>
<body>

<?php

$nhxtab= explode(":", $_REQUEST['nhx']);
echo "<table id=standardmenu><tr>";
for ($i = 0; $i<sizeof($nhxtab); $i++) {
	$arreq = explode("=", $nhxtab[$i]);
    echo "<td id=title>";
    if (isSet($displayNHXonbranch[$arreq[0]]) && $displayNHXonbranch[$arreq[0]]!=NULL) {
      echo $displayNHXonbranch[$arreq[0]];
    } else {
      echo $arreq[0];
    }
    echo "</td>";
}
	//echo "<table id=standardmenu><tr><td id=title>Sequence</td><td id=title>Code</td><td id=title>Function</td><td id=title>Reviewed</td><td id=title>Score</td></tr>";
echo "</tr><tr>";
for ($i = 0; $i<sizeof($nhxtab); $i++) {
	$arreq = explode("=", $nhxtab[$i]);
    echo "<td id=field>".$arreq[1]."</td>";
}

echo "</tr></table>";

?>
</body>
</html>

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
<p id="waiting">Tree pattern matching in progress...</p>



</body>
</html>


<script type="text/javascript">
var database="<?php echo $_REQUEST['database']; ?>";
var pattern="<?php echo $_REQUEST['pattern']; ?>";
window.location.href="resultsList.php?database=" + database + "&pattern=" + pattern + ";";

</script>
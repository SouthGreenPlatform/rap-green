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
<p id="waiting">Computing and generating whole result file...</p>



</body>
</html>


<script type="text/javascript">
var databank="<?php echo $_REQUEST['databank']; ?>";
var pattern="<?php echo $_REQUEST['pattern']; ?>";
var id="<?php echo $_REQUEST['id']; ?>";
window.location.href="fullresults.php?databank=" + databank + "&pattern=" + pattern + ";" + "&id=" + id;
</script>
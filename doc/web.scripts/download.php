<?php
/* PUT data comes in on the stdin stream */
function err($message) {
	echo "<html><body>$message</body></html>";
	exit;
}
$filename = $_GET["filename"];

if (!$filename) {
	err("No file selected");
}

$link = mysql_connect('mysql-s', 's210915rw', 'HeMySQ15');
if (!$link) {
	die('DB connection error: ' . mysql_error());
}
mysql_select_db("s210915_main");

$size_query = "SELECT SUM(OCTET_LENGTH(content)) " .
         "FROM files WHERE filename = '" . addslashes($filename) . "'";


$result = mysql_query($size_query) or die('Error, query 1 failed');
list($size) = mysql_fetch_array($result);
mysql_free_result($result);

header("Content-length: $size");
header("Content-type: unknown");
header("Content-Disposition: attachment; filename=$filename");

$query = "SELECT content " .
         "FROM files WHERE filename = '" . addslashes($filename) . "' ORDER BY chunk";
$result = mysql_query($query) or die('Error, query 2 failed');

while ($row = mysql_fetch_array($result)) {
	list($content) = $row;
	echo $content;
}

mysql_free_result($result);

mysql_close($link);

?>

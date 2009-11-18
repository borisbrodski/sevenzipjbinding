<?php

function getRealIpAddr()
{
	if (!empty($_SERVER['HTTP_CLIENT_IP']))   //check ip from share internet
	{
		$ip=$_SERVER['HTTP_CLIENT_IP'];
	}
	elseif (!empty($_SERVER['HTTP_X_FORWARDED_FOR']))   //to check ip is pass from proxy
	{
		$ip=$_SERVER['HTTP_X_FORWARDED_FOR'];
	}
	else
	{
		$ip=$_SERVER['REMOTE_ADDR'];
	}
	return $ip;
}

$size = $_SERVER["CONTENT_LENGTH"];
$filename = addslashes($_SERVER["HTTP_FILENAME"]);
$description = addslashes($_SERVER["HTTP_DESCR"]);
$ip = addslashes(getRealIpAddr());

$link = mysql_connect('mysql-s', 's210915rw', 'HeMySQ15');
if (!$link) {
	die('DB connection error: ' . mysql_error());
}
mysql_select_db("s210915_main");

$deletequery = "DELETE FROM files WHERE filename = '". addslashes($filename) . "'";
mysql_query($deletequery) or die("Error. Query 1 failed.\n");

$putdata = fopen("php://input", "r");
$chunk = 1;
while (!feof($putdata)) {
	$content = addslashes(fread($putdata, 500000));
	if (strlen($content) > 0) {
		$sql = "INSERT INTO files (content, ip, description, filename, chunk)
				VALUES ('$content', '$ip', '$description', '$filename', $chunk)";
		$r = mysql_query($sql) or die("Error. Query 2 failed, chunk $chunk" . mysql_error() . "\n");
		echo "Chunk $chunk: " . strlen($content) . "  ($r row added)\n";
		$chunk++;
	}
}

fclose($putdata);

mysql_close($link);

echo "OK. Size: $size bytes, " . ($chunk - 1) . " chunks\n";


?>
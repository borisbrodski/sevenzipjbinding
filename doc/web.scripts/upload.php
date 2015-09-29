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
$filename = addslashes($_SERVER["HTTP_FILENAME"]);
$md5 = addslashes($_SERVER["HTTP_MD5"]);
$chunknumber = addslashes($_SERVER["HTTP_CHUNK"]);
$description = addslashes($_SERVER["HTTP_DESCR"]);
$ip = addslashes(getRealIpAddr());

$link = mysql_connect('mysql-s', 's210915rw', 'HeMySQ15');
if (!$link) {
	die('DB connection error: ' . mysql_error());
}
mysql_select_db("s210915_main");


if ($md5) {
	echo "Filename: $filename\n";
	$sql = "SELECT content FROM files WHERE filename = '$filename' ORDER BY chunk";
	$result = mysql_query($sql) or die("Can't execute query 3");

	$digest = hash_init("md5");
	$len = 0;
	while ($row = mysql_fetch_row($result)) {
		$len = $len + strlen($row[0]);
		hash_update($digest, $row[0]);
		echo "-MD5: " . md5($row[0]) . "\n";
	}
	if (hash_final($digest) == $md5) {
		mysql_free_result($result);
		$setokquery = "UPDATE files SET md5ok = 1 WHERE filename = '$filename'";
		mysql_query($setokquery) or die("Can't execute query 4" . mysql_error());
		echo "File $filename completed successfully. md5: $md5\n";
	} else {
		die("ERROR: Wrong md5 for file $filename. Expected md5: $md5 (len: $len)\n");
	}
	mysql_close($link);
	exit;
}

if ($chunknumber == "1") {
	$deletequery = "DELETE FROM files WHERE filename = '". addslashes($filename) . "'";
	mysql_query($deletequery) or die("Error. Query 1 failed.\n");
}

$putdata = fopen("php://input", "r");
$content = addslashes(fread($putdata, 1000000));

$sql = "INSERT INTO files (content, ip, description, filename, chunk)
		VALUES ('$content', '$ip', '$description', '$filename', $chunknumber)";

$r = mysql_query($sql) or die("Error. Query 2 failed, chunk $chunk" . mysql_error() . "\n");
echo "Chunk $chunknumber (size=" . strlen($content) . ")\n";

fclose($putdata);

mysql_close($link);


?>
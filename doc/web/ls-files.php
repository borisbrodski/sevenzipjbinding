<?php
	$link = mysql_connect('mysql-s', 's210915rw', 'HeMySQ15');
	if (!$link) {
		die('DB connection error: ' . mysql_error());
	}
	mysql_select_db("s210915_main");

	$files = mysql_query("SELECT f1.id, f1.filename,
							(SELECT SUM(OCTET_LENGTH(content)) FROM files f2 WHERE f1.filename = f2.filename) as `size`,
						  	f1.createtimestamp, f1.ip, f1.description
						  FROM files f1
					      WHERE `chunk` = 1 AND md5ok = 1 ORDER BY `filename`") or die("Error. Query 3 failed.\n");

	while ($file = mysql_fetch_array($files)) {
		list($id, $name, $size, $timestamp, $ip, $description) = $file;
		if (!$description) {
			$description = "<i>no description</i>";
		}
		echo "$name\n";
	}
	mysql_free_result($votechoices);
	mysql_close($link);
?>
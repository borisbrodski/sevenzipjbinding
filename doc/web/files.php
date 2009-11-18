<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta name="description" content="Database List">
<meta http-equiv="Pragma" content="no-cache">
<link href="styles.css" rel="stylesheet" type="text/css">
<title>7-Zip-JBinding release assemble and test files</title>
</head>
<body>
<h1>7-Zip-JBinding release assemble and test files</h1>
<table border="1">
	<tr>
		<td>Id</td>
		<td>File name</td>
		<td>Size</td>
		<td>Timestamp</td>
		<td>IP</td>
		<td>Description</td>
	</tr>

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
					      WHERE `chunk` = 1 ORDER BY `filename`");

	while ($file = mysql_fetch_array($files)) {
		list($id, $name, $size, $timestamp, $ip, $description) = $file;
		if (!$description) {
			$description = "<i>no description</i>";
		}
		?>
	<tr>
		<td><?php echo $id ?></td>
		<td><a href="download.php?filename=<?php echo urlencode($name) ?>"><?php echo $name ?></a></td>
		<td><?php echo $size ?></td>
		<td><?php echo $timestamp ?></td>
		<td><?php echo $ip ?></td>
		<td><?php echo $description ?></td>
	</tr>


	<?php
	}
	mysql_free_result($votechoices);



	mysql_close($link);

	?>

</table>
</body>
</html>
